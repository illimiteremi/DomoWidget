package illimiteremi.domowidget.DomoWear;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import illimiteremi.domowidget.DomoGeneralSetting.BoxSetting;
import illimiteremi.domowidget.DomoUtils.DomoUtils;

import static illimiteremi.domowidget.DomoUtils.DomoConstants.INTERACTION_PATH;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.INTERCATION;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.JSON_ASK_TYPE;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.JSON_MESSAGE;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.SETTING;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.UPDATE_WIDGET_WEAR_ERROR;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.UPDATE_WIDGET_WEAR_VALUE;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.WEAR;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.WEAR_INTERACTION;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.WEAR_SETTING;


public class DomoServiceWear extends WearableListenerService {

    private static final String     TAG      = "[DOMO_WEAR_SERVICE]";
    private Context                 context;
    protected GoogleApiClient       mApiClient;       // API Google
    private WearSetting             wearSetting;      // Confifguration Wear
    private BoxSetting              boxSetting;       // Confifguration Box
    private BroadcastReceiver       mReceiver;        // Receiver

    public DomoServiceWear() {
        Log.d(TAG, "DomoServiceWear");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.context = getApplicationContext();

        createBroadcastReceiver();

        try {
            // Connection à l'API GOOGLE
            mApiClient = new GoogleApiClient.Builder(this)
                            .addApi(Wearable.API)
                            .build();
            mApiClient.connect();

            // Réuperation de la configuration WEAR
            ArrayList<Object> wearObjects = DomoUtils.getAllObjet(context, WEAR);
            if (wearObjects.size() != 0) {
                wearSetting = (WearSetting) wearObjects.get(0);
                boxSetting = new BoxSetting();
                boxSetting.setBoxId(wearSetting.getBoxId());
                boxSetting = (BoxSetting) DomoUtils.getObjetById(context, boxSetting);
                Log.d(TAG, "Box associée à Wear " + boxSetting.getBoxName());
            } else {
                Log.d(TAG, "Pas de configuration Android wear !");
            }
        } catch (Exception e ) {
            Log.e(TAG, "Erreur : " + e);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mApiClient.disconnect();
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        super.onMessageReceived(messageEvent);

        // Ouvre une connexion vers la montre
        ConnectionResult connectionResult = mApiClient.blockingConnect(5, TimeUnit.SECONDS);
        if (!connectionResult.isSuccess()) {
            Log.e(TAG, "Failed to connect to GoogleApiClient.");
            return;
        }
        // Traitement du message reçu
        final String askMsg   = new String(messageEvent.getData());
        final String wearPath = messageEvent.getPath();
        Log.d(TAG, "Question wear : " + wearPath + " / " + askMsg);

        // Demande d'interaction Jeedom
        if (wearPath.contentEquals(INTERACTION_PATH)) {
            try {
                JSONObject jsnObject = new JSONObject(askMsg);
                String asktype       = jsnObject.getString(JSON_ASK_TYPE);
                String message       = jsnObject.getString(JSON_MESSAGE);

                // Selon le type de question
                switch (asktype) {
                    case SETTING:
                        // Cas configuration android wear
                        if (Objects.equals(message, WEAR_SETTING)) {
                            ArrayList<Object> wearObjects = DomoUtils.getAllObjet(context, WEAR);
                            if (wearObjects.size() != 0) {
                                wearSetting = (WearSetting) wearObjects.get(0);
                            } else {
                                wearSetting = null;
                            }
                        }
                        // Création de la réponse en json
                        jsnObject = new JSONObject();
                        jsnObject.put(JSON_ASK_TYPE, asktype);
                        jsnObject.put(JSON_MESSAGE, wearSetting.toJson().toString());
                        Log.d(TAG, "réponse wear : " + jsnObject.toString());
                        if (boxSetting != null) {
                            sendMessage(jsnObject.toString());
                        }
                        break;
                    case WEAR_INTERACTION:
                        // Cas interaction Jeedom via android wear
                        if (boxSetting != null) {
                            DomoUtils.requestToJeedom(context, boxSetting, wearSetting, INTERCATION + message);
                        }
                        break;
                    default:
                        break;
                }
            } catch (JSONException e) {
                Log.e(TAG, "Erreur : " + e);
            }
        }
    }

    /**
     * Envoie un message à la montre
     * @param message message à transmettre
     */
    protected void sendMessage(final String message) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                // Envoie le message à tous les noeuds/montres connectées
                final NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(mApiClient).await();
                for (Node node : nodes.getNodes()) {
                    Wearable.MessageApi.sendMessage(mApiClient, node.getId(), INTERACTION_PATH, message.getBytes()).await();
                    mApiClient.disconnect();
                }
            }
        }).start();
    }

    /**
     * Receiver de la réponse Jeedom
     */
    private void createBroadcastReceiver() {

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String action = intent.getAction();

                Bundle extras = intent.getExtras();
                Log.d(TAG, "action = " + intent.getAction());

                if (!isInitialStickyBroadcast()) {
                    switch (action) {
                        case UPDATE_WIDGET_WEAR_VALUE :
                            String answerMsg = extras.getString("WIDGET_VALUE");
                            Log.d(TAG, "réponse wear : " + answerMsg);
                            // Création de la réponse en json
                            JSONObject jsnObject = new JSONObject();
                            try {
                                jsnObject.put(JSON_ASK_TYPE, WEAR_INTERACTION);
                                jsnObject.put(JSON_MESSAGE, answerMsg);
                                if (boxSetting != null) {
                                    sendMessage(jsnObject.toString());
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            break;
                        case UPDATE_WIDGET_WEAR_ERROR :
                            break;
                        default:
                            // NOTHING
                    }
                }
            }
        };

        // Construction du receiver
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UPDATE_WIDGET_WEAR_VALUE);
        intentFilter.addAction(UPDATE_WIDGET_WEAR_ERROR);
        registerReceiver(mReceiver, intentFilter);
    }

}
