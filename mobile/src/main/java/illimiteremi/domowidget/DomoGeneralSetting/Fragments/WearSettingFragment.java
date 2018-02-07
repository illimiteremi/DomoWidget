package illimiteremi.domowidget.DomoGeneralSetting.Fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import illimiteremi.domowidget.DomoAdapter.BoxAdapter;
import illimiteremi.domowidget.DomoGeneralSetting.BoxSetting;
import illimiteremi.domowidget.DomoUtils.DomoUtils;
import illimiteremi.domowidget.DomoWear.WearSetting;
import illimiteremi.domowidget.R;

import static illimiteremi.domowidget.DomoUtils.DomoConstants.BOX;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.DEFAULT_WEAR_TIMEOUT;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.JSON_ASK_TYPE;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.JSON_MESSAGE;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.SETTING_PATH;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.WEAR;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.WEAR_SETTING;
import static illimiteremi.domowidget.DomoUtils.DomoUtils.hideKeyboard;

public class WearSettingFragment extends Fragment implements ConnectionCallbacks{

    private static final String   TAG      = "[DOMO_WEAR_SETTING]";

    private Context               context;
    private Spinner               spinnerBox;       // Spinner de la liste des box
    private AutoCompleteTextView  timeOut;          // TimeOut avant exécution action
    private AutoCompleteTextView  shakeTimeOut;     // TimeOut avant deux shake
    private SeekBar               shakeSeekBar;     // Niveau du shake
    private WearSetting           wearSetting;      // Confifguration de l'env Wear
    private TextView              textNode;         // Nom de la montre connectée
    private TextView              textShakeLevel;   // Message du shake


    private BoxAdapter            boxAdapter;       // Adapter de la liste des box
    private BoxSetting            boxSetting;       // Objet Box

    private GoogleApiClient       mGoogleApiClient; // Api Google
    private Node                  connectedNode;    // Noeud de connection

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Disconnect - GoogleApiClient");
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = getContext();
        // Maj du titre
        getActivity().setTitle(getResources().getString(R.string.fragment_wear));

        // Récuperation de la configuration android Wear en bdd
        ArrayList<Object> wearObjects = DomoUtils.getAllObjet(context, WEAR);
        if (wearObjects.size() != 0) {
            wearSetting = (WearSetting) wearObjects.get(0);
        } else {
            wearSetting = new WearSetting();
            wearSetting.setId((int) DomoUtils.insertObjet(context, wearSetting));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wear_setting, container, false);
        setHasOptionsMenu(true);

        spinnerBox     = (Spinner) view.findViewById(R.id.spinner);
        timeOut        = (AutoCompleteTextView) view.findViewById(R.id.editTimeOut);
        shakeTimeOut   = (AutoCompleteTextView) view.findViewById(R.id.editShakeTimeOut);
        shakeSeekBar   = (SeekBar) view.findViewById(R.id.shakeSeekBar);
        textShakeLevel = (TextView) view.findViewById(R.id.textShakeLevel);
        textNode       = (TextView) view.findViewById(R.id.textNode);
        textNode.setFocusable(false);

        // Connection à GoogleApiClient
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                            .addApi(Wearable.API)
                            .addConnectionCallbacks(this)
                            .build();
        mGoogleApiClient.connect();

        // Chargement des spinners
        loadSpinner();

        // Affichage des valeurs enregistrée
        timeOut.setText(String.format(Locale.getDefault(), "%d", wearSetting.getWearTimeOutTimeOut()));
        shakeTimeOut.setText(String.format(Locale.getDefault(), "%d", wearSetting.getShakeTimeOut()));

        BoxSetting selectedBox = new BoxSetting();
        selectedBox.setBoxId(wearSetting.getBoxId());
        int spinnerPostion = DomoUtils.getSpinnerPosition(context, selectedBox);
        spinnerBox.setSelection(spinnerPostion);

        spinnerBox.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                boxSetting = (BoxSetting) adapterView.getAdapter().getItem(i);
                if (boxSetting.getBoxId() != 0 && wearSetting != null) {
                    wearSetting.setBoxId(boxSetting.getBoxId());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        shakeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int shakeLevel, boolean b) {
                wearSetting.setShakeLevel(shakeLevel);
                String shakeTxt = context.getResources().getString(R.string.shake_level) ;
                shakeTxt = shakeLevel == 0 ? shakeTxt + " : " + context.getResources().getString(R.string.wear_disable) : shakeTxt + " : " + shakeLevel + " / 10";
                textShakeLevel.setText(shakeTxt);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        shakeSeekBar.setProgress(wearSetting.getShakeLevel());

        textNode.setBackgroundColor(Color.argb(100,255,0,0));

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_save_settings, menu);
        MenuItem itemDelete = menu.findItem(R.id.delete_action);
        itemDelete.setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        hideKeyboard(getActivity());
        switch (item.getItemId()) {
            case R.id.save_action:
                //Log.d(TAG, "Mise à jour de la configuration wear = " + backupBoxData());
                backupBoxData();
                break;
            case R.id.delete_action:
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected(): Successfully connected to Google API client");

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mGoogleApiClient.blockingConnect();
                    NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();
                    connectedNode = nodes.getNodes().get(0);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String watchName = connectedNode.getDisplayName();
                                textNode.setText(watchName);
                                textNode.setBackgroundColor(Color.argb(100,13,151,36));
                            } catch (Exception e) {
                                Log.e(TAG, "Erreur : " + e);
                            }
                        }
                    });
                    Log.d(TAG, "Connected Node = " + connectedNode.getDisplayName());
                } catch (Exception e) {
                    Log.e(TAG, "Erreur : " + e);
                }
            }
        }).start();
    }

    @Override
    public void onConnectionSuspended(int i) {

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
     * Mise à jour de la configuration wear
     */
    private boolean backupBoxData() {

        // Crtl timeOout
        String timeout = timeOut.getText().toString();
        wearSetting.setWearTimeOutTimeOut(timeout.isEmpty() ? DEFAULT_WEAR_TIMEOUT : Integer.parseInt(timeout));
        String shaketimeout = shakeTimeOut.getText().toString();
        wearSetting.setShakeTimeOut(shaketimeout.isEmpty() ? DEFAULT_WEAR_TIMEOUT : Integer.parseInt(shaketimeout));

        // Update BDD
        int updateResult = DomoUtils.updateObjet(context, wearSetting);

        // Update Wear
        sendSettingToWear(wearSetting);

        if (updateResult == -1) {
            return false;
        }
        Toast.makeText(getContext(), getContext().getResources().getString(R.string.save_box), Toast.LENGTH_SHORT).show();
        return true;
    }

    /**
     * Envoi de la configuration à la montre
     * @param wearSetting
     */
    private void sendSettingToWear(final WearSetting wearSetting) {

        if (connectedNode != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject jsonSendMessage = new JSONObject();
                        jsonSendMessage.put(JSON_ASK_TYPE, WEAR_SETTING);
                        jsonSendMessage.put(JSON_MESSAGE, wearSetting.toJson());
                        MessageApi.SendMessageResult sendResult = Wearable.MessageApi.sendMessage(mGoogleApiClient, connectedNode.getId(),SETTING_PATH ,jsonSendMessage.toString().getBytes()).await();
                       Log.d(TAG, "Send Result = " + sendResult.getStatus());
                    } catch (Exception e){
                        Log.e(TAG, "Erreur : " + e);
                    }
                }
            }).start();
        }
    }
}
