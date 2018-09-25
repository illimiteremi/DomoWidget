package illimiteremi.domowidget.DomoServices;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import illimiteremi.domowidget.DomoGeneralSetting.BoxSetting;
import illimiteremi.domowidget.DomoJSONRPC.DomoCmd;
import illimiteremi.domowidget.DomoJSONRPC.DomoEquipement;
import illimiteremi.domowidget.DomoUtils.DomoBitmapUtils;
import illimiteremi.domowidget.DomoUtils.DomoConstants;
import illimiteremi.domowidget.DomoWidgetBdd.DomoJsonRPC;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static illimiteremi.domowidget.DomoUtils.DomoConstants.BOX_MESSAGE;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.BOX_PING;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.DONE;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.ERROR;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.JEEDOM_API_URL;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.JEEDOM_URL;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.MATCH;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.MOBILE_TIME_OUT;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.NO_MATCH;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.PING_ACTION;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.REQUEST;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.REQUEST_BOX;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.REQUEST_CMD;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.REQUEST_GEOLOC;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.REQUEST_OBJET;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.REQUEST_WEBCAM;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.WIDGET_VALUE;

public class DomoIntentService extends IntentService {

    private static final String TAG            = "[DOMO_INTENT_SERVICE]";

    private final DomoOkhttp domoOkhttp;
    private OkHttpClient     okHttpClient;

    // OkHttpCallback (callback utilisé pour les widgets)
    private class OkHttpCallback implements Callback {

        final DomoSerializableWidget widget;
        final Request                reTryRequest;

        public OkHttpCallback(DomoSerializableWidget widget, Request request) {
            this.widget       = widget;
            this.reTryRequest = request;
        }

        @Override
        public void onFailure(Call call, IOException e) {
            Log.e(TAG, "onFailure : " + e);
            if (reTryRequest != null) {
                Log.d(TAG, "Nouvelle tentative sur la 2em url...");
                okHttpClient = domoOkhttp.setBuilder(MOBILE_TIME_OUT);
                okHttpClient.newCall(reTryRequest).enqueue(new OkHttpCallback(widget, null));
            } else {
                sendErrorToProvider(widget);
            }
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String jeedomResponse = ERROR;
            try {
                // Traitement de la réponse
                ResponseBody responseBody = response.body();
                if (responseBody != null) {
                    jeedomResponse = responseBody.string();
                    if (response.code() == 200) {
                        if (!jeedomResponse.isEmpty()) {
                            // Si ExprReg
                            if (!widget.getDomoExpReg().isEmpty()) {
                                // Expression réguliere
                                if (jeedomResponse.matches(widget.getDomoExpReg())) {
                                    jeedomResponse = MATCH;
                                } else {
                                    jeedomResponse = NO_MATCH;
                                }
                            } else {
                                // Si la réponse contient Error
                                if (jeedomResponse.contains("error") ||
                                        jeedomResponse.contains("Aucune commande correspondant")) {
                                    jeedomResponse = ERROR;
                                }
                            }
                        }
                    }
                    Log.d(TAG, "réponse Jeedom : " + response.code() + " - " + (jeedomResponse.isEmpty() ? "none" : jeedomResponse )+ " - idWidget = " + widget.getDomoId());
                }
            } catch (NullPointerException e) {
                Log.e(TAG, "Erreur Jeedom : " + e + " - idWidget = " + widget.getDomoId());
            }
            if (!jeedomResponse.isEmpty()){
                // On ne pas traite pas la réponse suivant le widget
                switch (widget.getDomoType()) {
                    case PUSH :
                        break;
                    default:
                        sendToProvider(jeedomResponse, widget);
                }
            }
        }
    }

    // OkHttpBoxCallback (callback utilisé pour le ping de la box)
    private class OkHttpBoxCallback implements Callback {

        public OkHttpBoxCallback() {
        }

        @Override
        public void onFailure(Call call, IOException e) {
            Log.e(TAG, "onFailure : " + e);
            sendTestResponseToProvider(false, e.getMessage());
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {

            Boolean pingOK;
            try {
                String jsonData = response.body().string();
                Log.d(TAG, "réponse de Jeedom : " + jsonData);
                JSONObject jsonObject = new JSONObject(jsonData);
                pingOK = jsonObject.getString("result").contains("pong");
                sendTestResponseToProvider(pingOK,"OK");
            } catch (Exception e) {
                Log.e(TAG, "Erreur : " + e);
                sendTestResponseToProvider(false, e.getMessage());
            }
        }
    }

    // OkHttpBoxCallback (callback utilisé pour jsonrpc)
    private class OkHttpJsonRpcCallback implements Callback {

        String objetOrCmd;

        public OkHttpJsonRpcCallback(String objetOrCmd) {
            this.objetOrCmd = objetOrCmd;
        }

        @Override
        public void onFailure(Call call, IOException e) {
            Log.e(TAG, "onFailure : " + e);
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            try {
                String jsonData = response.body().string();
                JSONObject jsonObject = new JSONObject(jsonData);
                JSONArray result = jsonObject.getJSONArray("result");
                Log.d(TAG, objetOrCmd + " - Size : " + result.length());
                DomoJsonRPC domoJsonRPC = new DomoJsonRPC(getApplicationContext());

                switch (objetOrCmd) {
                    case REQUEST_OBJET :
                        domoJsonRPC.open();
                        domoJsonRPC.deleteData(REQUEST_OBJET);
                        for (int i = 0; i < result.length(); i++) {
                            JSONObject row = result.getJSONObject(i);
                            Log.d(TAG, "onResponse: " + row.toString());
                            int id = row.getInt("id");
                            String name = row.getString("name");
                            DomoEquipement objet = new DomoEquipement();
                            objet.setIdObjet(id);
                            objet.setObjetName(name);
                            domoJsonRPC.insertObjet(objet);
                        }
                        domoJsonRPC.close();
                        break;
                    case REQUEST_CMD:
                        domoJsonRPC.open();
                        domoJsonRPC.deleteData(REQUEST_CMD);
                        for (int i = 0; i < result.length(); i++) {
                            JSONObject row = result.getJSONObject(i);
                            int id = row.getInt("id");
                            String name = row.getString("name");
                            String type = row.getString("type");
                            String eqType = row.getString("eqType");
                            int objId = row.getInt("eqLogic_id");
                            DomoCmd cmd = new DomoCmd();
                            cmd.setIdObjet(objId);
                            cmd.setIdCmd(id);
                            cmd.setCmdName(eqType + " - " + name);
                            cmd.setType(type);
                            domoJsonRPC.insertCmd(cmd);
                        }
                        domoJsonRPC.close();
                        break;
                    }

            } catch (Exception e) {
                Log.e(TAG, "Erreur : " + e.getMessage());
                //sendTestResponseToProvider(false, e.getMessage());
            }
        }
    }

    // OkHttpWebCamCallback (callback utilisé pour le téléchargement de l'image webcam)
    private class OkHttpWebCamCallback implements Callback {

        final DomoSerializableWidget widget;
        final Request                reTryRequest;
        final int                    widgetWidth;

        public OkHttpWebCamCallback(DomoSerializableWidget widget, Request request) {
            this.widget       = widget;
            this.reTryRequest = request;
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
            Bundle widgetOption = appWidgetManager.getAppWidgetOptions(widget.getDomoId());
            widgetWidth = widgetOption.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH);
        }

        @Override
        public void onFailure(Call call, IOException e) {
            Log.e(TAG, "onFailure : " + e);
            if (reTryRequest != null) {
                Log.d(TAG, "Nouvelle tentative sur la 2em url...");
                okHttpClient = domoOkhttp.setBuilder(MOBILE_TIME_OUT);
                okHttpClient.newCall(reTryRequest).enqueue(new OkHttpWebCamCallback(widget, null));
            } else {
                sendErrorToProvider(widget);
            }
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            try {
                String fileName = getApplicationContext().getFilesDir().getAbsolutePath() + "/" + widget.getDomoId() + ".jpg" ;
                File file = new File (fileName);
                if (file.exists ()) {
                    file.delete ();
                }
                // Get picture from Webcam
                ResponseBody in = response.body();
                InputStream inputStream = in.byteStream();
                BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                Bitmap bitmap = BitmapFactory.decodeStream(bufferedInputStream);
                // Convert bitmap to file
                OutputStream os = new BufferedOutputStream(new FileOutputStream(file));
                DomoBitmapUtils domoBitmapUtils = new DomoBitmapUtils(getApplicationContext());
                bitmap = domoBitmapUtils.addBorderToBitmap(bitmap,5, Color.WHITE);
                Bitmap scaleBitmap = domoBitmapUtils.scaleDown(bitmap, widgetWidth, false);
                scaleBitmap.compress(Bitmap.CompressFormat.JPEG,100, os);
                bitmap.recycle();
                scaleBitmap.recycle();
                //Log.d(TAG, "Fichier : " + bitmap.getHeight() + " / " + bitmap.getWidth());
                os.close();
                Log.d(TAG, "Fichier enregistrée sous : " + fileName);
                sendToProvider(DONE, widget);
            } catch (Exception e) {
                Log.e(TAG, "Erreur : " + e);
                sendTestResponseToProvider(false, e.getMessage());
            }
        }
    }

    /**
     * Constructeur
     */
    public DomoIntentService() {
        super("DomoIntentService");
        domoOkhttp = DomoOkhttp.getInstance();
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Bundle extras = intent.getExtras();
        if (extras != null) {
            try {
                BoxSetting boxSetting          = (BoxSetting) intent.getSerializableExtra("BOX");
                DomoSerializableWidget widget  = (DomoSerializableWidget) intent.getSerializableExtra("WIDGET");
                String intentAction = intent.getAction();
                Log.d(TAG, "=> Action : " + intent.getAction());
                if (boxSetting != null) {
                    switch (intentAction) {
                        case REQUEST_BOX :
                            sendRequestForTest(boxSetting);
                            break;
                        case REQUEST :
                            sendRequestToJeedom(boxSetting, widget);
                            break;
                        case REQUEST_GEOLOC :
                            sendRequestGeoToJeedom(boxSetting, widget);
                            break;
                        case REQUEST_WEBCAM :
                            getWebCamPictureFromToJeedom(boxSetting, widget);
                            break;
                        case REQUEST_OBJET :
                            getAllJeedomObjet(boxSetting);
                            break;
                        case REQUEST_CMD :
                            getAllJeedomCmd(boxSetting);
                            break;
                        default:
                            // NOTHING
                    }
                } else {
                    sendErrorToProvider(widget);
                }
            } catch (Exception e) {
                Log.e(TAG, "Erreur -> IntentService : " + e);
            }
        }
    }

    /**
     * getAllCmd
     * @param boxSetting
     */
    public void getAllJeedomCmd(final BoxSetting boxSetting) {

        // TimeOut
        Integer wifiTimeOut   = boxSetting.getBoxTimeOut() == 0 ? DomoConstants.WIFI_TIME_OUT : boxSetting.getBoxTimeOut();
        Integer mobileTimeOut = boxSetting.getBoxTimeOut() == 0 ? MOBILE_TIME_OUT : boxSetting.getBoxTimeOut();
        Integer requestTimeOut;

        // Création de la requete http suivant le type de connexion
        Request request;
        JSONObject jsonObject = new JSONObject();
        try {
            JSONObject param = new JSONObject();
            param.put("apikey",boxSetting.getBoxKey());
            jsonObject.put("jsonrpc", "2.0");
            jsonObject.put("id"     , "1");
            jsonObject.put("method" , "cmd::all");
            jsonObject.put("params", param);
            MediaType JSON   = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(JSON, jsonObject.toString());

            Log.d(TAG,JSON.toString());
            if (checkWifi()) {
                // Url Interne (en Wifi)
                requestTimeOut = wifiTimeOut;
                request = new Request.Builder().url(boxSetting.getBoxUrlInterne() + JEEDOM_API_URL).post(body).build();
            } else {
                // Url Externe
                requestTimeOut = mobileTimeOut;
                request = new Request.Builder().url(boxSetting.getBoxUrlExterne() + JEEDOM_API_URL).post(body).build();
            }
            // Execution de la requete
            okHttpClient = domoOkhttp.setBuilder(requestTimeOut);
            okHttpClient.newCall(request).enqueue(new OkHttpJsonRpcCallback(REQUEST_CMD));
        } catch (OutOfMemoryError outOfMemoryError) {
            Log.e(TAG, "Erreur Mémoire : " + outOfMemoryError.getMessage());
            sendTestResponseToProvider(false, "OutOfMemoryError");
        } catch (Exception e) {
            Log.e(TAG, "Erreur : " + e.getMessage());
            sendTestResponseToProvider(false, e.getMessage());
        }
    }

    /**
     * getAllObjet
     * @param boxSetting
     */
    public void getAllJeedomObjet(final BoxSetting boxSetting) {

        // TimeOut
        Integer wifiTimeOut   = boxSetting.getBoxTimeOut() == 0 ? DomoConstants.WIFI_TIME_OUT : boxSetting.getBoxTimeOut();
        Integer mobileTimeOut = boxSetting.getBoxTimeOut() == 0 ? MOBILE_TIME_OUT : boxSetting.getBoxTimeOut();
        Integer requestTimeOut;

        // Création de la requete http suivant le type de connexion
        Request request;
        JSONObject jsonObject = new JSONObject();
        try {
            JSONObject param = new JSONObject();
            param.put("apikey",boxSetting.getBoxKey());
            jsonObject.put("jsonrpc", "2.0");
            jsonObject.put("id"     , "1");
            jsonObject.put("method" , "eqLogic::all");
            jsonObject.put("params", param);
            MediaType JSON   = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(JSON, jsonObject.toString());

            if (checkWifi()) {
                // Url Interne (en Wifi)
                requestTimeOut = wifiTimeOut;
                request = new Request.Builder().url(boxSetting.getBoxUrlInterne() + JEEDOM_API_URL).post(body).build();
            } else {
                // Url Externe
                requestTimeOut = mobileTimeOut;
                request = new Request.Builder().url(boxSetting.getBoxUrlExterne() + JEEDOM_API_URL).post(body).build();
            }
            // Execution de la requete
            okHttpClient = domoOkhttp.setBuilder(requestTimeOut);
            okHttpClient.newCall(request).enqueue(new OkHttpJsonRpcCallback(REQUEST_OBJET));
        } catch (OutOfMemoryError outOfMemoryError) {
            Log.e(TAG, "Erreur Mémoire : " + outOfMemoryError.getMessage());
            sendTestResponseToProvider(false, "OutOfMemoryError");
        } catch (Exception e) {
            Log.e(TAG, "Erreur : " + e.getMessage());
            sendTestResponseToProvider(false, e.getMessage());
        }
    }

    /**
     * sendRequestForTest
     * @param boxSetting
     */
    private void sendRequestForTest(final BoxSetting boxSetting) {

        // TimeOut
        Integer wifiTimeOut   = boxSetting.getBoxTimeOut() == 0 ? DomoConstants.WIFI_TIME_OUT : boxSetting.getBoxTimeOut();
        Integer mobileTimeOut = boxSetting.getBoxTimeOut() == 0 ? MOBILE_TIME_OUT : boxSetting.getBoxTimeOut();
        Integer requestTimeOut;

        // Création de la requete http suivant le type de connexion
        Request request;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("jsonrpc", "2.0");
            jsonObject.put("id"     , "1");
            jsonObject.put("method" , "ping");
            MediaType JSON   = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(JSON, jsonObject.toString());

            Log.d(TAG,JSON.toString());
            if (checkWifi()) {
                // Url Interne (en Wifi)
                requestTimeOut = wifiTimeOut;
                request = new Request.Builder().url(boxSetting.getBoxUrlInterne() + JEEDOM_API_URL).post(body).build();
            } else {
                // Url Externe
                requestTimeOut = mobileTimeOut;
                request = new Request.Builder().url(boxSetting.getBoxUrlExterne() + JEEDOM_API_URL).post(body).build();
            }
            // Execution de la requete
            okHttpClient = domoOkhttp.setBuilder(requestTimeOut);
            okHttpClient.newCall(request).enqueue(new OkHttpBoxCallback());
        } catch (OutOfMemoryError outOfMemoryError) {
            Log.e(TAG, "Erreur Mémoire : " + outOfMemoryError.getMessage());
            sendTestResponseToProvider(false, "OutOfMemoryError");
        } catch (Exception e) {
            Log.e(TAG, "Erreur : " + e.getMessage());
            sendTestResponseToProvider(false, e.getMessage());
        }
    }

    /**
     * sendRequestToJeedom
     * @param boxSetting
     * @param widget
     */
    private void sendRequestToJeedom(final BoxSetting boxSetting, final DomoSerializableWidget widget) {

        // TimeOut
        Integer wifiTimeOut   = boxSetting.getBoxTimeOut() == 0 ? DomoConstants.WIFI_TIME_OUT : boxSetting.getBoxTimeOut();
        Integer mobileTimeOut = boxSetting.getBoxTimeOut() == 0 ? MOBILE_TIME_OUT : boxSetting.getBoxTimeOut();
        Integer requestTimeOut;

        // Request Http
        Request request, reTryRequest;

        // Création de la requete http suivant le type de connexion
        try {
            if (checkWifi()) {
                // Url Interne (en Wifi)
                Log.d(TAG, "url Interne (Wifi) => " + widget.getDomoAction());
                requestTimeOut = wifiTimeOut;

                try {
                    // URL - 1er tentative en interne
                    request = new Request.Builder().url(boxSetting.getBoxUrlInterne() + JEEDOM_URL + boxSetting.getBoxKey() + "&" + widget.getDomoAction()).build();
                } catch (Exception e) {
                    // Utilisation url externe (si interne ko)
                    request = new Request.Builder().url(boxSetting.getBoxUrlExterne() + JEEDOM_URL + boxSetting.getBoxKey() + "&" + widget.getDomoAction()).build();
                }

                try {
                    // URL - 2em Tentative en externe
                    reTryRequest = new Request.Builder().url(boxSetting.getBoxUrlExterne() + JEEDOM_URL + boxSetting.getBoxKey() + "&" + widget.getDomoAction()).build();
                } catch (Exception e) {
                    reTryRequest = null;
                }
            } else {
                // Url Externe (mobile)
                Log.d(TAG, "url Externe (Mobile) => " + widget.getDomoAction());
                requestTimeOut = mobileTimeOut;
                // URL - 1er tentative en externe
                request = new Request.Builder().url(boxSetting.getBoxUrlExterne() + JEEDOM_URL + boxSetting.getBoxKey() + "&" + widget.getDomoAction()).build();
                try {
                    // 2em Tentative en interne
                    reTryRequest = new Request.Builder().url(boxSetting.getBoxUrlInterne() + JEEDOM_URL + boxSetting.getBoxKey() + "&" + widget.getDomoAction()).build();
                } catch (Exception e) {
                    reTryRequest = null;
                }
            }
            okHttpClient = domoOkhttp.setBuilder(requestTimeOut);
            okHttpClient.newCall(request).enqueue(new OkHttpCallback(widget, reTryRequest));
        } catch (OutOfMemoryError outOfMemoryError) {
            Log.e(TAG, "Erreur Mémoire : " + outOfMemoryError.getMessage());
            sendErrorToProvider(widget);
        } catch (Exception e) {
            Log.e(TAG, "Erreur : " + e.getMessage());
            sendErrorToProvider(widget);
        }
    }

    /**
     * getWebCamPictureFromToJeedom
     * @param boxSetting
     * @param widget
     */
    private void getWebCamPictureFromToJeedom(final BoxSetting boxSetting, final DomoSerializableWidget widget){
        // TimeOut
        Integer wifiTimeOut   = boxSetting.getBoxTimeOut() == 0 ? DomoConstants.WIFI_TIME_OUT : boxSetting.getBoxTimeOut();
        Integer mobileTimeOut = boxSetting.getBoxTimeOut() == 0 ? MOBILE_TIME_OUT : boxSetting.getBoxTimeOut();
        Integer requestTimeOut;

        // Request Http
        Request request, reTryRequest;
        String url = widget.getDomoAction();

        // Création de la requete http suivant le type de connexion
        try {
            if (checkWifi()) {
                // Url Interne (en Wifi)
                // Log.d(TAG, "url interne (Wifi) => " + widget.getDomoAction());
                requestTimeOut = wifiTimeOut;
                try {
                    // URL - 1er tentative en interne
                    request = new Request.Builder().url(boxSetting.getBoxUrlInterne() + url).build();
                } catch (Exception e) {
                    // Utilisation url externe (si interne ko)
                    request = new Request.Builder().url(boxSetting.getBoxUrlExterne() + url).build();
                }

                try {
                    // URL - 2em Tentative en externe
                    reTryRequest = new Request.Builder().url(boxSetting.getBoxUrlExterne() + url).build();
                } catch (Exception e) {
                    reTryRequest = null;
                }

            } else {
                // Url Externe (mobile)
                // Log.d(TAG, "url Externe (Mobile) => " + widget.getDomoAction());
                requestTimeOut = mobileTimeOut;
                // URL - 1er tentative en externe
                request = new Request.Builder().url(boxSetting.getBoxUrlExterne() + url).build();
                try {
                    // 2em Tentative en interne
                    reTryRequest = new Request.Builder().url(boxSetting.getBoxUrlInterne() + url).build();
                } catch (Exception e) {
                    reTryRequest = null;
                }
            }
            okHttpClient = domoOkhttp.setBuilder(requestTimeOut);
            okHttpClient.newCall(request).enqueue(new OkHttpWebCamCallback(widget, reTryRequest));
        } catch (OutOfMemoryError outOfMemoryError) {
            Log.e(TAG, "Erreur Mémoire : " + outOfMemoryError.getMessage());
            sendErrorToProvider(widget);
        } catch (Exception e) {
            Log.e(TAG, "Erreur : " + e.getMessage());
            sendErrorToProvider(widget);
        }
    }

    /**
     * sendRequestGeoloToJeedom
     * @param boxSetting
     * @param widget
     */
    private void sendRequestGeoToJeedom(final BoxSetting boxSetting, final DomoSerializableWidget widget) {

        // TimeOut
        Integer wifiTimeOut   = boxSetting.getBoxTimeOut() == 0 ? DomoConstants.WIFI_TIME_OUT : boxSetting.getBoxTimeOut();
        Integer mobileTimeOut = boxSetting.getBoxTimeOut() == 0 ? MOBILE_TIME_OUT : boxSetting.getBoxTimeOut();
        Integer requestTimeOut;

        // Request Http
        Request request, reTryRequest;

        String url =  widget.getDomoPluginKey().isEmpty() ?
                    JEEDOM_URL + boxSetting.getBoxKey() + "&" + widget.getDomoAction() :
                        widget.getDomoPluginURL() + "?apikey=" + widget.getDomoPluginKey() + "&" + widget.getDomoAction();
        // Création de la requete http suivant le type de connexion
        try {
            if (checkWifi()) {
                // Url Interne (en Wifi)
                Log.d(TAG, "url interne (Wifi) => " + widget.getDomoAction());
                requestTimeOut = wifiTimeOut;
                try {
                    // URL - 1er tentative en interne
                    request = new Request.Builder().url(boxSetting.getBoxUrlInterne() + url).build();
                } catch (Exception e) {
                    // Utilisation url externe (si interne ko)
                    request = new Request.Builder().url(boxSetting.getBoxUrlExterne() + url).build();
                }

                try {
                    // URL - 2em Tentative en externe
                    reTryRequest = new Request.Builder().url(boxSetting.getBoxUrlExterne() + url).build();
                } catch (Exception e) {
                    reTryRequest = null;
                }

            } else {
                // Url Externe (mobile)
                Log.d(TAG, "url Externe (Mobile) => " + widget.getDomoAction());
                requestTimeOut = mobileTimeOut;
                // URL - 1er tentative en externe
                request = new Request.Builder().url(boxSetting.getBoxUrlExterne() + url).build();
                try {
                    // 2em Tentative en interne
                    reTryRequest = new Request.Builder().url(boxSetting.getBoxUrlInterne() + url).build();
                } catch (Exception e) {
                    reTryRequest = null;
                }
            }
            okHttpClient = domoOkhttp.setBuilder(requestTimeOut);
            okHttpClient.newCall(request).enqueue(new OkHttpCallback(widget, reTryRequest));
        } catch (OutOfMemoryError outOfMemoryError) {
            Log.e(TAG, "Erreur Mémoire : " + outOfMemoryError.getMessage());
            sendErrorToProvider(widget);
        } catch (Exception e) {
            Log.e(TAG, "Erreur : " + e.getMessage());
            sendErrorToProvider(widget);
        }
    }

    /**
     * sendIntent
     * @param i
     */
    private void sendIntent(Intent i) {
        // Recherche des receivers
        PackageManager pm = getPackageManager();
        List<ResolveInfo> matches = pm.queryBroadcastReceivers(i, 0);
        //Log.d(TAG, "Nombre de broadcastReceivers trouvé : " + matches.size());
        if (matches.size() != 0) {
            for (ResolveInfo resolveInfo : matches) {
                Intent explicit = new Intent(i);
                ComponentName cn= new ComponentName(resolveInfo.activityInfo.applicationInfo.packageName, resolveInfo.activityInfo.name);
                //Log.d(TAG, "Envoie Intent => " + cn.getClassName());
                explicit.setComponent(cn);
                // Envoi Explicit
                sendBroadcast(explicit);
            }
        } else {
            // Envoi Implicit
            sendBroadcast(i);
        }
    }

    /**
     * sendToProvider
     * @param httpResponse
     * @param widget
     */
    private void sendToProvider(String httpResponse, DomoSerializableWidget widget) {
        Log.d(TAG, "sendToProvider : " + widget.getDomoType().getWidgetAction() + " - idWidget = " + widget.getDomoId());
        Intent broadcastIntent = new Intent();
        // Check reponse en erreur
        if (httpResponse.equals(ERROR)) {
            broadcastIntent.setAction(widget.getDomoType().getWidgetError());
        } else {
            broadcastIntent.setAction(widget.getDomoType().getWidgetAction());
        }
        broadcastIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widget.getDomoId());
        broadcastIntent.putExtra(WIDGET_VALUE, httpResponse);
        sendIntent(broadcastIntent);
    }

    /**
     * sendErrorToProvider
     * @param widget
     */
    private void sendErrorToProvider(DomoSerializableWidget widget) {
        Log.d(TAG, "sendErrorToProvider : " + widget.getDomoType().getWidgetError());
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(widget.getDomoType().getWidgetError());
        broadcastIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widget.getDomoId());
        sendIntent(broadcastIntent);
    }

    /**
     * sendTestResponseToPriver
     * @param result
     */
    private void sendTestResponseToProvider(boolean result, String message) {
        Intent callBackIntent = new Intent();
        callBackIntent.setAction(PING_ACTION);
        callBackIntent.putExtra(BOX_PING, result);
        callBackIntent.putExtra(BOX_MESSAGE, message);
        sendIntent(callBackIntent);
        Log.d(TAG, "réponse de Jeedom au ping : " + result + " => " + message);
    }

    /**
     * checkWifi
     * @return
     */
    private boolean checkWifi() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                return true;
            }

            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                return false;
            }
        }
        return false;
    }
}
