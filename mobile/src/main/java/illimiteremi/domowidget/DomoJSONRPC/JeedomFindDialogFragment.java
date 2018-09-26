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
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

import illimiteremi.domowidget.DomoAdapter.CmdAdapter;
import illimiteremi.domowidget.DomoAdapter.EquipementAdapter;
import illimiteremi.domowidget.DomoUtils.DomoUtils;
import illimiteremi.domowidget.DomoWidgetBdd.DomoJsonRPC;
import illimiteremi.domowidget.R;

import static illimiteremi.domowidget.DomoUtils.DomoConstants.EQUIPEMENT;

public class JeedomFindDialogFragment extends DialogFragment {

    private static final String  TAG      = "[JEEDOM_RPC]";

    private Context              context;

    private String               cmdType;                       // Type de commande ACTION / INFO
    private Button               cancelButton;                  // Button annuler
    private Button               okButton;                      // Button choisir
    private Spinner              spinnerEquipements;            // Spinner de la liste des equipements
    private Spinner              spinnerCmd;                    // Spinner de la liste des commandes
    private TextView             textAction;                    // Titre de l'action commandes
    private AutoCompleteTextView actionJeedom;                  // Action Jeedom

    private View                     mParentView;
    private JeedomActionFindListener mListener;
    private AutoCompleteTextView     autoCompleteTextViewRetour;

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
        textAction   = mParentView.findViewById(R.id.textAction);

        spinnerCmd         = mParentView.findViewById(R.id.spinnerCmd);
        spinnerEquipements = mParentView.findViewById(R.id.spinnerEquipements);

        textAction.setText("Action - " + cmdType);

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

    /**
     * setOnJeedomActionFindListener
     * @param listener
     * @param autoCompleteTextView
     * @param cmdType
     */
    public void setOnJeedomActionFindListener(JeedomActionFindListener listener, AutoCompleteTextView autoCompleteTextView, String cmdType) {
        this.mListener = listener;
        this.autoCompleteTextViewRetour = autoCompleteTextView;
        this.cmdType = cmdType;
    }

    /**
     * loadSpinner
     */
    private void loadSpinner() {
        /**
         * Chargement des Spinners
         */
        EquipementAdapter equipementAdapter = (EquipementAdapter) DomoUtils.createAdapter(context, EQUIPEMENT);
        spinnerEquipements.setAdapter(equipementAdapter);

        spinnerEquipements.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                 @Override
                 public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                     try {
                         // Recuperation de l'équipement
                         DomoEquipement domoEquipement = (DomoEquipement) parent.getAdapter().getItem(position);

                         // Création de la liste des commandes
                         ArrayList<DomoCmd> jeedomCmd = new ArrayList<>();
                         if (domoEquipement.getIdObjet() != -1) {
                             DomoJsonRPC domoJsonRPCcmd = new DomoJsonRPC(context);
                             domoJsonRPCcmd.open();
                             jeedomCmd = domoJsonRPCcmd.getCmdByObjet(domoEquipement, cmdType);
                             domoJsonRPCcmd.close();
                         }

                         // Aucune commande de trouvé pour l'équipement
                         if(jeedomCmd.size() == 0) {
                             DomoCmd domoCmd = new DomoCmd();
                             domoCmd.setIdCmd(-1);
                             domoCmd.setCmdName(context.getResources().getString(R.string.no_cmd));
                             jeedomCmd.add(domoCmd);
                         }

                         // Création de la liste des Commandes // Equipement
                         Log.d(TAG, "Nombre de commandes : " + jeedomCmd.size());
                         Collections.reverse(jeedomCmd);
                         CmdAdapter cmdAdapter = new CmdAdapter(context, jeedomCmd);
                         spinnerCmd.setAdapter(cmdAdapter);
                     } catch (Exception e) {
                         Log.e(TAG, "spinnerEquipements: " + e);
                     }
                 }

                 @Override
                 public void onNothingSelected(AdapterView<?> parent) {
                 }
             });

        spinnerCmd.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    DomoCmd domoCmd = (DomoCmd) parent.getAdapter().getItem(position);
                    if (domoCmd.getIdCmd() != -1) {
                        actionJeedom.setText("type=cmd&id=" + domoCmd.getIdCmd());
                    } else {
                        actionJeedom.setText("type=cmd&id=");
                    }
                } catch (Exception e) {
                    Log.e(TAG, "spinnerCmd: " + e);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}
