package illimiteremi.domowidget.DomoWidgetVocal;

import android.appwidget.AppWidgetManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

import illimiteremi.domowidget.DomoGeneralSetting.BoxSetting;
import illimiteremi.domowidget.DomoUtils.DomoUtils;
import illimiteremi.domowidget.R;

import static illimiteremi.domowidget.DomoUtils.DomoConstants.INTERCATION;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.UPDATE_WIDGET_VOCAL_VALUE;

public class VocalActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private static final String TAG                = "[DOMO_VOCAL_ACTIVITY]";
    private static final int    VOICE_REQUEST_CODE = 1;

    private Context             context;

    private int                 idWidget;               // Id du widget
    private VocalWidget         widget;                 // Widget vocal

    private TextToSpeech        tts;                    // TextToSpeech
    private Boolean             ttsIsInit = false;      // Etat init tts
    private String              askMsg;                 // Question

    private TextView            askTextView;            // Question du Toast
    private TextView            answerTextView;         // Reponse du Toast
    private View                layout;                 // Layout du Toast
    private int                 toastTime;              // Temps affichage toas

    /**
     * Ce broadcast permet de traiter la reponse de l'intercation Jeedom
     */
    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                final String response = extras.getString("WIDGET_VALUE");
                Log.d(TAG, "Réponse à l'interaction Jeedom => " + response);
                // Lecture reponse
                if (widget.getDomoSynthese().equals(1)) {
                    // Log.d(TAG, "TextToSpeech is init : " + ttsIsInit);
                    if (ttsIsInit) {
                        new Thread(new Runnable() {
                            public void run() {
                                int result;
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    tts.speak(response, TextToSpeech.QUEUE_FLUSH, null, null);
                                } else {
                                    tts.speak(response, TextToSpeech.QUEUE_FLUSH, null);
                                }
                            }
                        }).start();
                    }
                }
                creatCustomToast(askMsg + "...", response);
                DomoUtils.updateAllWidget(context);
            }
            finish();
        }
    };

    @Override
    public void finish() {
        // Log.d(TAG, "finish");
        DomoUtils.startVoiceService(context, false);
        if(android.os.Build.VERSION.SDK_INT >= 21) {
            super.finishAndRemoveTask();
        } else {
            super.finish();
        }
    }


    @Override
    protected void onDestroy() {
        // Log.d(TAG, "onDestroy");
        unregisterReceiver(mBroadcastReceiver);
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Log.d(TAG,"onCreate");
        context = getApplicationContext();

        // Creation du receiver (réponse à l'intercation Jeedom)
        IntentFilter filter = new IntentFilter();
        filter.addAction(UPDATE_WIDGET_VOCAL_VALUE);
        registerReceiver(mBroadcastReceiver, filter);

        // Stop voice service
        DomoUtils.stopVoiceService(context);

        // Récuperation de l'ID widget
        Bundle extras = getIntent().getExtras();
        idWidget      = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

        LayoutInflater inflater = getLayoutInflater();
        layout                  = inflater.inflate(R.layout.vocal_toast, (ViewGroup) findViewById(R.id.toast_layout_root));
        layout.setMinimumWidth(WindowManager.LayoutParams.MATCH_PARENT);

        askTextView    = (TextView) layout.findViewById(R.id.ask);
        answerTextView = (TextView) layout.findViewById(R.id.answer);

        // Init TextToSpeech
        tts = new TextToSpeech(context, this);

        Intent mRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mRecognizerIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, context.getResources().getString(R.string.widget_vocal_speak));

        try {
            startActivityForResult(mRecognizerIntent, VOICE_REQUEST_CODE);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getApplicationContext(), context.getString(R.string.widget_vocal_error), Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Erreur : " + e);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Log.d(TAG, "on onActivityResult : " + requestCode + " - " + resultCode);
        if (requestCode == VOICE_REQUEST_CODE && resultCode == RESULT_OK) {
            ArrayList matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            // Récuperation de la saisie vocal
            askMsg = matches.get(0).toString();
            Log.d(TAG, "Question à l'interaction Jeedom : " + askMsg);

            // Récuperation des informations Widget Vocal
            try {
                widget = new VocalWidget(context, idWidget);
                widget = (VocalWidget) DomoUtils.getObjetById(context, widget);
                if (widget != null) {
                    BoxSetting boxSetting = widget.getSelectedBox();
                    // Execution de l'interaction
                    DomoUtils.requestToJeedom(context, boxSetting, widget, INTERCATION + askMsg);
                }
            } catch (Exception e) {
                Toast.makeText(this,"Erreur Widget...", Toast.LENGTH_LONG).show();
                Log.e(TAG, "Erreur : " + e);
            }
        } else {
            finish();
        }
    }

    @Override
    public void onInit(int i) {
        // Log.d(TAG, "onInit");
        if (i == TextToSpeech.SUCCESS) {
            int language = tts.setLanguage(Locale.FRENCH);
            if (language == TextToSpeech.LANG_MISSING_DATA) {
                Toast.makeText(this,"Données vocales non présentes...", Toast.LENGTH_LONG).show();
            } else {
                ttsIsInit = true;
            }
        } else {
            Toast.makeText(this,"Erreur synthese vocale !", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * creatCustomToast
     * @param ask Question
     * @param answer Réponse
     */
    private void creatCustomToast(String ask, String answer) {
        askTextView.setText(ask);
        answerTextView.setText(answer);
        final Toast toast = new Toast(context);
        toast.setGravity(Gravity.FILL_HORIZONTAL | Gravity.BOTTOM , 0, 250);
        toast.setView(layout);
        //Log.d(TAG, ask + "..." + answer);

        // Calcul durée affichage suivant la réponse
        toastTime = answer.length() / 40;
        if (toastTime <= 1) {
            toastTime = 3;
        }

        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (toastTime > 0) {
                    toastTime--;
                    handler.postDelayed(this, 1000);
                } else {
                    // Fin TIMER
                    toast.show();
                }
            }
        });
    }

}