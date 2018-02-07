package illimiteremi.domowidget.wear;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import illimiteremi.domowidget.R;

import static illimiteremi.domowidget.wear.WearConstants.COL_SHAKE_LEVEL;
import static illimiteremi.domowidget.wear.WearConstants.JSON_ASK_TYPE;
import static illimiteremi.domowidget.wear.WearConstants.JSON_MESSAGE;
import static illimiteremi.domowidget.wear.WearConstants.SETTING_PATH;
import static illimiteremi.domowidget.wear.WearConstants.WEAR_INTERACTION;
import static illimiteremi.domowidget.wear.WearConstants.WEAR_SETTING;

public class WearSettingService extends WearableListenerService  {

    private static final String TAG = "[DOMO_WEAR_SETTING]";

    private Context              context;
    private GoogleApiClient      mGoogleApiClient; // Api Google

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        Log.d(TAG, "Constucteur : WearSettingService ");

        // Connection à GoogleApiClient
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d(TAG, "onMessageReceived ");
        // Ouvre une connexion vers le telephone
        ConnectionResult connectionResult = mGoogleApiClient.blockingConnect(5, TimeUnit.SECONDS);
        if (!connectionResult.isSuccess()) {
            Log.e(TAG, "Failed to connect to GoogleApiClient.");
            return;
        }

        // Traitement du message reçu
        final String askMsg   = new String(messageEvent.getData());
        final String wearPath = messageEvent.getPath();
        Log.d(TAG, "Question wear : " + wearPath + " / " + askMsg);

        if (wearPath.contentEquals(SETTING_PATH)) {
            try {
                JSONObject jsnObject = new JSONObject(askMsg);
                String type          = jsnObject.getString(JSON_ASK_TYPE);
                String message       = jsnObject.getString(JSON_MESSAGE);

                // Selon le type de question
                switch (type) {
                    case WEAR_SETTING:
                        Log.d(TAG, "Message Received  : " + askMsg);
                        Toast.makeText(context, getString(R.string.setting_change), Toast.LENGTH_SHORT).show();
                        WearSharePreferences.sauveToSharePreferences(context, message);
                        // Stop service
                        int shakeLevel = WearSharePreferences.readFromSharePreferences(context, COL_SHAKE_LEVEL);
                        Intent intent = new Intent(context, SensorService.class);
                        if (shakeLevel != 0) {
                            context.startService(intent);
                        } else {
                            context.stopService(intent);
                        }
                        break;
                    case WEAR_INTERACTION:
                        break;
                    default:
                        break;
                }
            } catch (JSONException e) {
                Log.e(TAG, "Erreur : " + e);
            }

        }
        mGoogleApiClient.disconnect();
    }

}
