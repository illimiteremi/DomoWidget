package illimiteremi.domowidget.DomoWidgetVocal;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizerSetup;
import illimiteremi.domowidget.DomoUtils.DomoUtils;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.VOCAL;

public class VocalService extends Service {

    private static final String TAG           = "[DOMO_VOCAL_SERVICE]";

    private static final String KWS_SEARCH    = "wakeup";
    private static final String KWS_GOOGLE    = "ok google";

    private String              keyPhrase;                          // Mot clef
    private int                 appWidgetId;                        // Id du widget Vocal
    private int                 level;                              // Seuil de détection

    private edu.cmu.pocketsphinx.SpeechRecognizer recognizer;

    private final RecognitionListener recognitionListener = new RecognitionListener() {
        @Override
        public void onBeginningOfSpeech() {
            // Log.d(TAG, "onBeginningOfSpeech");
        }

        @Override
        public void onEndOfSpeech() {
            // Log.d(TAG, "onEndOfSpeech");
        }

        @Override
        public void onPartialResult(Hypothesis hypothesis) {
            if (hypothesis != null) {
                String text = hypothesis.getHypstr();
                if (text.equals(keyPhrase)) {
                    recognizer.stop();
                }
            }
        }

        @Override
        public void onResult(Hypothesis hypothesis) {
            if (hypothesis != null) {
                String text = hypothesis.getHypstr();
                Log.d(TAG, "onResult : " + text);

                if (text.contains("ok google")) {
                    Intent googleNowIntent = new Intent("android.intent.action.VOICE_ASSIST");
                    googleNowIntent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                    startActivity(googleNowIntent);
                } else {
                    // start Activity Interaction
                    Intent voiceIntent = new Intent(getApplicationContext(), VocalActivity.class);
                    voiceIntent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                    voiceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
                    startActivity(voiceIntent);
                }

                // Stop recognizer
                //recognizer.cancel();
                //recognizer.shutdown();
                //recognizer.removeListener(this);

            }
        }

        @Override
        public void onError(Exception e) {
            Log.d(TAG, "Erreur : " + e.getMessage());
            //Toast.makeText(getApplicationContext(), "Problème " + e, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onTimeout() {
            Log.d(TAG, "onTimeout");
        }
    };

    /**
     * Configuration du Listener pocketsphinx
     * @param assetsDir
     * @param keyPhrase
     * @throws IOException
     */
    private void setupRecognizer(File assetsDir, String keyPhrase) throws IOException {
        // Log.d(TAG, "setupRecognizer");
        recognizer = SpeechRecognizerSetup.defaultSetup()
                //Set Dictionary and Acoustic Model files
                .setAcousticModel(new File(assetsDir, "fr-eu-ptm"))
                .setDictionary(new File(assetsDir, "fr.dict"))
                .setRawLogDir(assetsDir)
                .setKeywordThreshold((float) (1 * Math.pow(10,-(level))))
                .getRecognizer();
        recognizer.addListener(recognitionListener);

        // Create keyword-activation search.
        recognizer.addKeyphraseSearch(KWS_SEARCH, keyPhrase);
        recognizer.addKeyphraseSearch(KWS_GOOGLE, KWS_GOOGLE);
        Log.d(TAG, "Ajout des mots : " + keyPhrase + " / " + "ok google");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        try {
            ArrayList<Object> Objects = DomoUtils.getAllObjet(getApplicationContext(), VOCAL);
            for (Object vocalObject : Objects) {
                VocalWidget vocalWidget = ((VocalWidget) vocalObject);
                appWidgetId = vocalWidget.getDomoId();
                keyPhrase   = vocalWidget.getKeyPhrase().toLowerCase();
                level       = ((VocalWidget) vocalObject).getThresholdLevel();
            }
        } catch (Exception e) {
            Log.e(TAG, "Erreur : " + e);
            level = 0;      // Service is disable
        }

        // Check si KeyPhrase
        if (level != 0) {
            Log.d(TAG, "Vocal Service - Mot Clef = " + keyPhrase + " - Seuil = " + (1 * Math.pow(10,-(level))));
            HandlerThread handlerThread = new HandlerThread(TAG);
            handlerThread.start();
            Handler handler = new Handler(handlerThread.getLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        Assets assets = new Assets(getApplicationContext());
                        File assetDir = assets.syncAssets();
                        setupRecognizer(assetDir, keyPhrase);
                        recognizer.stop();
                        recognizer.startListening(KWS_SEARCH);
                    } catch (IOException e) {
                        Log.e(TAG, "Erreur : " + e);
                    }
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Log.d(TAG,"onDestroy");
        if (recognizer != null) {
            try {
                recognizer.cancel();
                recognizer.shutdown();
                recognizer.removeListener(recognitionListener);
            } catch (Exception e) {
                Log.e(TAG, "Erreur : " + e.getMessage());
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
