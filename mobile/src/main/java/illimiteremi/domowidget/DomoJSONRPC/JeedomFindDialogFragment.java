package illimiteremi.domowidget.DomoJSONRPC;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;

import java.util.ArrayList;

import illimiteremi.domowidget.DomoAdapter.CmdAdapter;
import illimiteremi.domowidget.DomoAdapter.EquipementAdapter;
import illimiteremi.domowidget.DomoGeneralSetting.BoxSetting;
import illimiteremi.domowidget.DomoUtils.DomoUtils;
import illimiteremi.domowidget.DomoWidgetBdd.DomoJsonRPC;
import illimiteremi.domowidget.R;

import static illimiteremi.domowidget.DomoUtils.DomoConstants.EQUIPEMENT;

public class JeedomFindDialogFragment extends DialogFragment {

    private static final String   TAG      = "[RPC]";

    private JeedomActionFindListener mListener;
    private View mParentView;
    private Context context;

    private Button               cancelButton;                  // Button annuler
    private Button               okButton;                      // Button choisir
    private Spinner              spinnerEquipements;            // Spinner de la liste des equipements
    private Spinner              spinnerCmd;                    // Spinner de la liste des commandes
    private AutoCompleteTextView actionJeedom;                  // Action Jeedom
    private AutoCompleteTextView autoCompleteTextViewRetour;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mParentView = inflater.inflate(R.layout.find_cmd_dialog, container, false);

        okButton     = mParentView.findViewById(R.id.buttonChoisir);
        cancelButton = mParentView.findViewById(R.id.buttonCancel);
        actionJeedom = mParentView.findViewById(R.id.actionJeedom);

        spinnerCmd         = mParentView.findViewById(R.id.spinnerCmd);
        spinnerEquipements = mParentView.findViewById(R.id.spinnerEquipements);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoCompleteTextViewRetour.setText(actionJeedom.getText());
                mListener.onOk(autoCompleteTextViewRetour, actionJeedom.getText().toString());
                dismiss();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onCancel();
                dismiss();
            }
        });

        loadSpinner();
        return mParentView;
    }

    public void setOnJeedomActionFindListener(JeedomActionFindListener listener, AutoCompleteTextView autoCompleteTextView) {
        this.mListener = listener;
        this.autoCompleteTextViewRetour = autoCompleteTextView;
    }

    private void loadSpinner() {
        /**
         * Chargement des Spinners
         */
        EquipementAdapter equipementAdapter = (EquipementAdapter) DomoUtils.createAdapter(context, EQUIPEMENT);
        spinnerEquipements.setAdapter(equipementAdapter);
        spinnerEquipements.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                 @Override
                 public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                     DomoEquipement domoEquipement = (DomoEquipement) parent.getAdapter().getItem(position);

                     DomoJsonRPC domoJsonRPCcmd = new DomoJsonRPC(context);
                     domoJsonRPCcmd.open();
                     ArrayList<DomoCmd> jeedomCmd = domoJsonRPCcmd.getCmdByObjet(domoEquipement, "info");
                     domoJsonRPCcmd.close();

                     CmdAdapter cmdAdapter = new CmdAdapter(context, jeedomCmd);
                     spinnerCmd.setAdapter(cmdAdapter);
                     Log.d(TAG, "onItemSelected: " + jeedomCmd.size());
                 }

                 @Override
                 public void onNothingSelected(AdapterView<?> parent) {
                 }
             });

        spinnerCmd.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                DomoCmd domoCmd = (DomoCmd) parent.getAdapter().getItem(position);
                actionJeedom.setText("type=cmd&id=" + domoCmd.getIdCmd());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

}
