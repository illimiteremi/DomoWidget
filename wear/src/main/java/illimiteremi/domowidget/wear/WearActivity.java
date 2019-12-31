package illimiteremi.domowidget.wear;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import androidx.annotation.Nullable;
import android.support.wearable.view.DelayedConfirmationView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.TimeUnit;

import illimiteremi.domowidget.R;

import static illimiteremi.domowidget.wear.WearConstants.COL_TIME_OUT;
import static illimiteremi.domowidget.wear.WearConstants.INTERACTION_PATH;
import static illimiteremi.domowidget.wear.WearConstants.JSON_ASK_TYPE;
import static illimiteremi.domowidget.wear.WearConstants.JSON_MESSAGE;
import static illimiteremi.domowidget.wear.WearConstants.WEAR_INTERACTION;

public class WearActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, MessageApi.MessageListener {

    private static final String     TAG                            = "[DOMO_WEAR_ACTIVITY]";
    private static final int        SPEECH_RECOGNIZER_REQUEST_CODE = 0;

    private Context                 context;
    private GoogleApiClient         mGoogleApiClient;           // ApiGoogle
    private Node                    connectedNode;              // Noeud de connection

    private DelayedConfirmationView mDelayedConfirmationView;   // DelayedConfirmation
    private String                  recognizedText;             // Message
    private TextView                sendtxt;                    // Envoi en cours

    private ImageButton             interactionButton;

    private int                     wearTimeOut;                // Temps avant envois du message
    private boolean                 isCancel = false;           // Annulation

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Disconnect GoogleApiClient");
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mGoogleApiClient.disconnect();
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SPEECH_RECOGNIZER_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                recognizedText = results.get(0);
                Log.d(TAG, "Message : " + recognizedText);
                sendtxt.setText(getString(R.string.interaction_send));
                mDelayedConfirmationView.setVisibility(View.VISIBLE);
                mDelayedConfirmationView.start();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = getApplicationContext();
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_main);

        // Message envoi en cours...
        sendtxt = (TextView) findViewById(R.id.sendtxt);
        sendtxt.setText(getString(R.string.interaction_title));

        // Start service
        Intent msgIntent = new Intent(context, SensorService.class);
        context.startService(msgIntent);

        // Connection à GoogleApiClient
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .build();
        Wearable.MessageApi.addListener(mGoogleApiClient, this);
        mGoogleApiClient.connect();

        // DelayedConfirmationView
        wearTimeOut = WearSharePreferences.readFromSharePreferences(context, COL_TIME_OUT);
        Log.d(TAG, "TIME OUT : " + wearTimeOut);
        mDelayedConfirmationView = (DelayedConfirmationView) findViewById(R.id.delayed_confirmation);
        mDelayedConfirmationView.setVisibility(View.INVISIBLE);
        mDelayedConfirmationView.setTotalTimeMs(TimeUnit.SECONDS.toMillis(wearTimeOut));
        mDelayedConfirmationView.setListener(
                new DelayedConfirmationView.DelayedConfirmationListener() {
                    @Override
                    public void onTimerFinished(View view) {
                        Log.d(TAG, "onTimerFinished : " + isCancel);
                        if (!isCancel) {
                            sendMessage(recognizedText);
                        } else {
                            isCancel = false;
                        }
                    }

                    @Override
                    public void onTimerSelected(View view) {
                        Log.d(TAG, "onTimerSelected");
                        isCancel = true;
                        finish();
                    }
                });

        interactionButton = (ImageButton) findViewById(R.id.domoButton);
        interactionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    onClickMe(null);
            }
        });

       onClickMe(null);
    }

    /**
     * onClickMe
     */
    private void onClickMe(View view){
        Log.d(TAG, "onClickMe");
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        startActivityForResult(intent, SPEECH_RECOGNIZER_REQUEST_CODE);
    }

/*
    private void test() {
        recognizedText = "blblablabla";
        mDelayedConfirmationView.setVisibility(View.VISIBLE);
        mDelayedConfirmationView.start();
        sendtxt.setText(getString(R.string.interaction_send));
    }
*/
    /**
     * Envoi de la configuration à la montre
     * @param wearMessage
     */
    private void sendMessage(final String wearMessage) {

        if (connectedNode != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject jsonSendMessage = new JSONObject();
                        jsonSendMessage.put(JSON_ASK_TYPE, WEAR_INTERACTION);
                        jsonSendMessage.put(JSON_MESSAGE, wearMessage);
                        Wearable.MessageApi.sendMessage(mGoogleApiClient, connectedNode.getId(),INTERACTION_PATH, jsonSendMessage.toString().getBytes());
                        Log.d(TAG, "sendMessage => " + jsonSendMessage.toString());
                    } catch (Exception e){
                        Log.e(TAG, "Erreur : " + e);
                    }
                }
            }).start();
        } else {
            finish();
            Toast.makeText(context, getString(R.string.error), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected(): Successfully connected to Google API client");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mGoogleApiClient.blockingConnect();
                    final List<Node> connectedNodes = Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await().getNodes();
                    connectedNode = connectedNodes.get(0);
                } catch (Exception e){
                    Log.e(TAG, "Erreur : " + e);
                }
            }
        }).start();

    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

        final String wearMessage  = new String(messageEvent.getData());
        final String wearPath     = messageEvent.getPath();
        Log.d(TAG, "réponse wear : " + wearPath + " / " + wearMessage);

        if (wearPath.contentEquals(INTERACTION_PATH)) {
            try {
                String jsonMsg       = wearMessage;
                JSONObject jsnObject = new JSONObject(jsonMsg);
                String type          = jsnObject.getString(JSON_ASK_TYPE);
                String message       = jsnObject.getString(JSON_MESSAGE);

                if (type.contains(WEAR_INTERACTION)) {
                    Log.d(TAG, "onMessageReceived => " + messageEvent.getPath());
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    finish();
                }
            } catch (Exception e) {
                Log.d(TAG, "Erreur :" + e);
            }
            mGoogleApiClient.disconnect();
        }
    }
}
