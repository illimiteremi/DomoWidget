package illimiteremi.domowidget.DomoGeneralSetting.Fragments;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;

import java.util.ArrayList;

import illimiteremi.domowidget.DomoAdapter.BoxAdapter;
import illimiteremi.domowidget.DomoGeneralSetting.BoxSetting;
import illimiteremi.domowidget.DomoJSONRPC.DomoCmd;
import illimiteremi.domowidget.DomoJSONRPC.DomoObjet;
import illimiteremi.domowidget.DomoUtils.DomoUtils;
import illimiteremi.domowidget.DomoWidgetBdd.DomoJsonRPC;
import illimiteremi.domowidget.FireBaseJobDispatcher.FireBaseJobService;
import illimiteremi.domowidget.R;
import yuku.ambilwarna.colorpicker.AmbilWarnaDialogFragment;
import yuku.ambilwarna.colorpicker.OnAmbilWarnaListener;

import static illimiteremi.domowidget.DomoUtils.DomoConstants.BOX;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.BOX_MESSAGE;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.BOX_PING;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.PING_ACTION;
import static illimiteremi.domowidget.DomoUtils.DomoUtils.hideKeyboard;

public class BoxSettingFragment extends Fragment {

    private static final String   TAG      = "[DOMO_BOX_FRAGMENT]";

    private Context               context;
    private boolean               isNew;            // Première Création du widget

    private Spinner               spinnerBox;        // Spinner de la liste des box
    private AutoCompleteTextView  boxName;           // Nom de la Box
    private AutoCompleteTextView  boxUrlExt;         // Accès externe
    private AutoCompleteTextView  boxUrlInt;         // Accès interne
    private AutoCompleteTextView  boxApiKey;         // Api Key
    private TextView              boxTimeOut;        // Message temps de reponse
    private SeekBar               timeOutLevel;      // Level Bar TimeOut

    private TextView              textTextSize;      // Message taille du text
    private SeekBar               widgetTextSize;    // Taille du nom des widgets

    private TextView              textRefreshTime;   // Message textRefreshTimet
    private SeekBar               widgetRefreshTime; // rafraishissement du widget

    private LinearLayout          linearBox;         // Layout de la configuration de la box
    private TextView              editColor;         // TextViex valeur de couleur

    private static int            mColor = 255;      // Couleur default

    private BoxAdapter            boxAdapter;        // Adapter de la liste des box
    private BoxSetting            boxSetting;        // Objet Box
    private MenuItem              itemAdd;           // Item du menu Ajouter
    private MenuItem              itemSave;          // Item du menu Sauvergarder
    private MenuItem              itemDelete;        // Item du menu Supprimer

    private FirebaseJobDispatcher dispatcher;        // Job Dispatcher FireBase

    /**
     * Listener de selection de couleur
     */
    private final OnAmbilWarnaListener onAmbilWarnaListener = new OnAmbilWarnaListener() {
        @Override
        public void onCancel(AmbilWarnaDialogFragment dialogFragment) {
            Log.d(TAG, "onCancel()");
        }

        @Override
        public void onOk(AmbilWarnaDialogFragment dialogFragment, int color) {
            Log.d(TAG, "Selected color : " + color);
            boxSetting.setWidgetNameColor(String.valueOf(color));
            mColor = color;
            editColor.setBackgroundColor(mColor);
        }
    };

    /**
     * Boite de dialogue erreur configuration BOX
     */
    public static class SettingFragment extends DialogFragment {

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            String message = getArguments().getString(BOX_MESSAGE);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
            alertDialogBuilder.setIcon(R.drawable.no_data);
            alertDialogBuilder.setTitle(getResources().getString(R.string.widget_problem_popup));
            alertDialogBuilder.setMessage(getResources().getString(R.string.box_message_popup) + "\n" + message);
            alertDialogBuilder.setPositiveButton("OK", null);
            return alertDialogBuilder.create();
        }
    }

    /**
     * Ce broadcast permet de recevoir la réponse du ping
     */
    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {

            Bundle extras = intent.getExtras();
            if (extras != null) {
                Log.d(TAG, "Réponse API JSON ping : " + extras.getBoolean(BOX_PING));
                if (extras.getBoolean(BOX_PING)) {
                    Toast.makeText(getContext(), getContext().getResources().getString(R.string.save_box), Toast.LENGTH_SHORT).show();
                } else {
                    SettingFragment dialogFragment = new SettingFragment();
                    dialogFragment.setArguments(extras);
                    getFragmentManager().beginTransaction().add(dialogFragment, "settingError").commitAllowingStateLoss();
                }
            }
            // Mise à jour des widgets
            DomoUtils.updateAllWidget(context);

            // Création du JobService
            Integer timeRepeat = boxSetting.getWidgetRefreshTime();
            dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
            dispatcher.cancelAll();
            if (!timeRepeat.equals(0)) {
                // Set Extra
                final Bundle bundle = new Bundle();
                bundle.putInt("boxId", boxSetting.getBoxId());
                bundle.putInt("trigger", timeRepeat);

                // Create JOB
                Job widgetJob = dispatcher.newJobBuilder()
                        .setService(FireBaseJobService.class)
                        .setTag("DOMO_WIDGET")
                        .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                        .setExtras(bundle)
                        .setTag("[UPDATE_ALL_WIDGET]")
                        .setRecurring(true)
                        .setLifetime(Lifetime.FOREVER)
                        .setTrigger(Trigger.executionWindow((int) timeRepeat, (int) timeRepeat))
                        .build();
                // Dispatch Job
                Log.d(TAG, "Création du Job de rafraichissement des Widgets : " + timeRepeat + " sec");
                dispatcher.mustSchedule(widgetJob);
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        getContext().unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = getContext();
        // Maj du titre
        getActivity().setTitle(getResources().getString(R.string.fragment_configuration));

        // TEST
        DomoJsonRPC domoJsonRPC = new DomoJsonRPC(context);
        domoJsonRPC.open();
        ArrayList<DomoObjet> jeedomObjets = domoJsonRPC.getAllObjet();
        if (jeedomObjets != null) {
            for (DomoObjet domoObjet: jeedomObjets) {
                Log.d(TAG, domoObjet.getObjetName());
                ArrayList<DomoCmd> jeedomCmds = domoJsonRPC.getCmdByObjet(domoObjet, "info");
                if (jeedomCmds != null) {
                    for (DomoCmd domoCmd: jeedomCmds) {
                        Log.d(TAG,  "- " + domoCmd.getCmdName() + " - " + domoCmd.getIdCmd());
                    }
                }
            }
        }
        domoJsonRPC.close();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_box_setting, container, false);
        setHasOptionsMenu(true);

        boxName       = (AutoCompleteTextView) view.findViewById(R.id.editBoxName);
        boxUrlExt     = (AutoCompleteTextView) view.findViewById(R.id.editUrlExt);
        boxUrlInt     = (AutoCompleteTextView) view.findViewById(R.id.editUrlInt);
        boxApiKey     = (AutoCompleteTextView) view.findViewById(R.id.editKey);

        boxTimeOut    = (TextView) view.findViewById(R.id.timeOutLevel);
        timeOutLevel  = (SeekBar) view.findViewById(R.id.timeOutSeekBar);

        textTextSize  = (TextView) view.findViewById(R.id.textTextSize);
        widgetTextSize  = (SeekBar) view.findViewById(R.id.sizeSeekBar);

        textRefreshTime = (TextView) view.findViewById(R.id.textRefreshTime);
        widgetRefreshTime  = (SeekBar) view.findViewById(R.id.refreshTimeSeekBar);

        spinnerBox    = (Spinner) view.findViewById(R.id.spinner);
        linearBox     = (LinearLayout) view.findViewById(R.id.linearBox);
        editColor     = (TextView) view.findViewById(R.id.editColor);

        spinnerBox.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                boxSetting = (BoxSetting) adapterView.getAdapter().getItem(i);
                if (boxSetting.getBoxId() != 0) {
                    boxName.setText(boxSetting.getBoxName());
                    boxUrlExt.setText(boxSetting.getBoxUrlExterne());
                    boxUrlInt.setText(boxSetting.getBoxUrlInterne());
                    boxApiKey.setText(boxSetting.getBoxKey());
                    timeOutLevel.setProgress(boxSetting.getBoxTimeOut());
                    widgetTextSize.setProgress(boxSetting.getWidgetTextSize());
                    widgetRefreshTime.setProgress(boxSetting.getWidgetRefreshTime());
                    linearBox.setVisibility(View.VISIBLE);
                    editColor.setBackgroundColor(boxSetting.getWidgetNameColor());
                } else {
                    linearBox.setVisibility(View.INVISIBLE);
                }
                // Rechargement des menus
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // Listener de selection des couleur
        editColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AmbilWarnaDialogFragment fragment = AmbilWarnaDialogFragment.newInstance(mColor);
                fragment.setOnAmbilWarnaListener(onAmbilWarnaListener);
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                fragment.show(ft, "color_picker_dialog");
            }
        });

        // Listener du timeOut
        timeOutLevel.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int threshold, boolean b) {
                try {
                    boxSetting.setBoxTimeOut(threshold);
                    String thresholdTxt = context.getString(R.string.box_time_out) ;
                    thresholdTxt = threshold == 0 ? thresholdTxt + " : " + context.getString(R.string.default_time_out) : thresholdTxt + " : " + threshold + " sec";
                    boxTimeOut.setText(thresholdTxt);
                } catch (Exception e) {
                    Log.e(TAG, "Erreur : " + e);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // Listener de la taille du widget
        widgetTextSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int threshold, boolean b) {
                try {
                    boxSetting.setWidgetTextSize(threshold);
                    String thresholdTxt = context.getString(R.string.widget_text_size) ;
                    thresholdTxt = threshold == 0 ? thresholdTxt + " : " + context.getString(R.string.default_time_out) : thresholdTxt + " : +" + threshold;
                    textTextSize.setText(thresholdTxt);
                } catch (Exception e) {
                    Log.e(TAG, "Erreur : " + e);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // Listener du refresh time
        widgetRefreshTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int threshold, boolean b) {
                try {
                    threshold = threshold < 40 ? 0 : threshold;
                    boxSetting.setWidgetRefreshTime(threshold);
                    String thresholdTxt = context.getString(R.string.widget_refresh_time) ;
                    thresholdTxt = threshold == 0 ? thresholdTxt + " : " + context.getString(R.string.default_time_out) : thresholdTxt + " : " + threshold + " sec";
                    textRefreshTime.setText(thresholdTxt);
                } catch (Exception e) {
                    Log.e(TAG, "Erreur : " + e);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // Chargement des spinners
        loadSpinner();

        // Creation du receiver (réponse au ping de l'intentService)
        IntentFilter filter = new IntentFilter();
        filter.addAction(PING_ACTION);
        getContext().registerReceiver(mBroadcastReceiver, filter);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_box_activity, menu);
        itemAdd    = menu.findItem(R.id.add_action);
        itemSave   = menu.findItem(R.id.save_action);
        itemDelete = menu.findItem(R.id.delete_action);

        // Si aucune box est sélectionnée
        if (boxSetting != null) {
            if (boxSetting.getBoxId() == 0){
                itemSave.setVisible(false);
                itemAdd.setVisible(true);
                itemDelete.setVisible(false);
             } else {
                // si la box n'est pas utilisée
                if (DomoUtils.boxIsUsed(context, boxSetting)) {
                    itemDelete.setVisible(false);
                } else {
                    itemDelete.setVisible(true);
                }
            }
        }

        if (isNew) {
            itemAdd.setVisible(false);
        }

        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        hideKeyboard(getActivity());
        switch (item.getItemId()) {
            case R.id.save_action:
                backupBoxData();
                break;
            case R.id.add_action:
                createNewBox();
                break;
            case R.id.delete_action:
                DomoUtils.removeObjet(context, boxSetting);
                // Refresh current fragment
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.detach(this).attach(this).commit();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    /**
     * Chargement des Spinners
     */
    private void loadSpinner() {
        boxAdapter = (BoxAdapter) DomoUtils.createAdapter(context, BOX);
        spinnerBox.setAdapter(boxAdapter);
        // Si pas de box sélectionnée
        if (boxSetting == null) {
            if (spinnerBox.getCount() >= 2) {
                spinnerBox.setSelection(0);
            } else {
                spinnerBox.setSelection(spinnerBox.getAdapter().getCount()-1);
            }

        }
    }

    /**
     * Mise à jour de la box dans la bdd
     */
    private boolean backupBoxData() {

        // Verification du formulaire
        Boolean isCheck = true;
        if (boxName.getText().toString().length() == 0) {
            boxName.setError(getResources().getString(R.string.error_name));
            isCheck = false;
        }

        if (boxUrlExt.getText().toString().length() == 0 && boxUrlInt.getText().toString().length() == 0 ) {
            boxUrlExt.setError(getResources().getString(R.string.error_url_ext));
            boxUrlInt.setError(getResources().getString(R.string.error_url_ext));
            isCheck = false;
        }

        if (!boxUrlExt.getText().toString().contains("http") && boxUrlExt.getText().toString().length() != 0 ) {
            boxUrlExt.setError(getResources().getString(R.string.error_http));
            isCheck = false;
        }

        if (!boxUrlInt.getText().toString().contains("http") && boxUrlInt.getText().toString().length() != 0 ) {
            boxUrlInt.setError(getResources().getString(R.string.error_http));
            isCheck = false;
        }

        if (boxApiKey.getText().toString().length() == 0) {
            boxApiKey.setError(getResources().getString(R.string.error_key));
        }

        if (isCheck) {
            // Maj de la box en Bdd
            boxSetting.setBoxName(boxName.getText().toString());
            boxSetting.setBoxUrlExterne(boxUrlExt.getText().toString());
            boxSetting.setBoxUrlInterne(boxUrlInt.getText().toString());
            boxSetting.setBoxKey(boxApiKey.getText().toString());

            if (boxSetting.getBoxId() == 0) {
                long id = DomoUtils.insertObjet(context, boxSetting);
                boxSetting.setBoxId((int) id);
                Log.d(TAG, "Ajout de la box " + id + " - " + boxSetting.getBoxName());
            } else {
                DomoUtils.updateObjet(context, boxSetting);
                Log.d(TAG, "Mise à jour de la box " + boxSetting.getBoxName());
            }

            // Refresh current fragment
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.detach(this).attach(this).commit();

            // Envoi de requete à la box Domotique
            DomoUtils.pingRequestToJeedom(context, boxSetting);

            // Recuperation des data Jeedom
            DomoUtils.getAllJeedomObjet(context, boxSetting);
            DomoUtils.getAllJeedomCmd(context, boxSetting);

            isNew = false;
            return true;
        }
        return false;
    }

    /**
     * Création d'une nouvelle Box
     */
    private void createNewBox() {
        // Maj des menus
        itemSave.setVisible(true);
        itemAdd.setVisible(false);
        linearBox.setVisibility(View.VISIBLE);

        // Création d'une nouvelle Box Domotique
        boxSetting = new BoxSetting();
        boxSetting.setBoxName(getResources().getString(R.string.new_box));

        // Ajout d'une box en BDD
        int idBox = (int) DomoUtils.insertObjet(context, boxSetting);
        boxSetting.setBoxId(idBox);
        isNew = true;

        // Position du spinner
        spinnerBox.setEnabled(false);
        boxAdapter = (BoxAdapter) DomoUtils.createAdapter(context, BOX);
        spinnerBox.setAdapter(boxAdapter);
        int position = DomoUtils.getSpinnerPosition(context, boxSetting);
        spinnerBox.setSelection(position);
    }

}
