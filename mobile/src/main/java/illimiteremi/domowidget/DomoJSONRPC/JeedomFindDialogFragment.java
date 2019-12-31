package illimiteremi.domowidget.DomoJSONRPC;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

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
import illimiteremi.domowidget.DomoUtils.DomoConstants;
import illimiteremi.domowidget.DomoUtils.DomoUtils;
import illimiteremi.domowidget.DomoWidgetBdd.DomoJsonRpcBDD;
import illimiteremi.domowidget.R;

import static illimiteremi.domowidget.DomoUtils.DomoConstants.COMMANDE;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.EQUIPEMENT;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.SLIDER;

public class JeedomFindDialogFragment extends DialogFragment {

    private static final String  TAG      = "[DOMO_RPC]";

    private Context              context;

    private DomoConstants.CALLBACK_TYPE callbackType;                  // Type de callBack
    private Spinner                     spinnerEquipements;            // Spinner de la liste des equipements
    private Spinner                     spinnerCmd;                    // Spinner de la liste des commandes
    private AutoCompleteTextView        actionJeedom;                  // Action Jeedom
    private JeedomActionFindListener    mListener;
    private AutoCompleteTextView        autoCompleteTextViewRetour;
    private String                      commande;                       // Commande jeedom

    private DomoCmd                     domoCmd;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View mParentView    = inflater.inflate(R.layout.find_cmd_dialog, container, false);
        Button okButton     = mParentView.findViewById(R.id.buttonChoisir);
        Button cancelButton = mParentView.findViewById(R.id.buttonCancel);
        actionJeedom        = mParentView.findViewById(R.id.actionJeedom);
        TextView textAction = mParentView.findViewById(R.id.textAction);
        spinnerCmd          = mParentView.findViewById(R.id.spinnerCmd);
        spinnerEquipements  = mParentView.findViewById(R.id.spinnerEquipements);

        textAction.setText("Action - " + callbackType.getCmdType());

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
     * @param callbackType
     */
    public void setOnJeedomActionFindListener(JeedomActionFindListener listener, AutoCompleteTextView autoCompleteTextView, DomoConstants.CALLBACK_TYPE callbackType) {
        this.mListener                  = listener;
        this.autoCompleteTextViewRetour = autoCompleteTextView;
        this.callbackType               = callbackType;

        // Récuperation de l'objet Commande / id action jeedom
        try {
            commande = autoCompleteTextViewRetour.getText().toString();
            domoCmd = new DomoCmd();
            String[] allNumber = commande.split("&");
            domoCmd.setIdCmd(Integer.parseInt(allNumber[1].replaceAll("[^0-9]","")));
        } catch (Exception e) {
            Log.e(TAG, "setOnJeedomActionFindListener: ", e);
        }

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
                             DomoJsonRpcBDD domoJsonRPCcmd = new DomoJsonRpcBDD(context);
                             domoJsonRPCcmd.open();
                             jeedomCmd = domoJsonRPCcmd.getCmdByObjet(domoEquipement, callbackType.getCmdType());
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
                         //Log.d(TAG, "Nombre de commandes : " + jeedomCmd.size());
                         Collections.reverse(jeedomCmd);
                         CmdAdapter cmdAdapter = new CmdAdapter(context, jeedomCmd);
                         spinnerCmd.setAdapter(cmdAdapter);

                         // Position du spinner commande
                         if (domoCmd != null) {
                             Log.d(TAG, "loadSpinner: " + domoCmd.getCmdName() + " - " + domoCmd.getIdObjet());
                             try {
                                 for(int i = 0; i <= cmdAdapter.getCount()-1; i++) {
                                     DomoCmd domoCmdList = cmdAdapter.getItem(i);
                                     Log.d(TAG, "loadSpinner: " + domoCmdList.getCmdName() + " - " + domoCmdList.getIdCmd());
                                     if (domoCmdList.getIdCmd().equals(domoCmd.getIdCmd())) {
                                         spinnerCmd.setSelection(i);
                                     }
                                 }
                             } catch (Exception e) {
                                 Log.e(TAG, "loadSpinner: ",e);
                             }
                         }
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
                        switch (callbackType) {
                            case INFO:
                                actionJeedom.setText(COMMANDE + domoCmd.getIdCmd());
                                break;
                            case ACTION:
                                actionJeedom.setText(COMMANDE + domoCmd.getIdCmd());
                                break;
                            case SLIDER:
                                actionJeedom.setText(COMMANDE + domoCmd.getIdCmd() + SLIDER);
                                break;
                            default: actionJeedom.setText(COMMANDE + "?");
                        }
                    } else {
                        actionJeedom.setText(commande);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "spinnerCmd: " + e);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Position du spinner Equipement
        Log.d(TAG, "loadSpinner: " + domoCmd.getIdCmd());
        domoCmd = (DomoCmd) DomoUtils.getObjetById(context, domoCmd);
        if (domoCmd != null) {
            Log.d(TAG, "loadSpinner: " + domoCmd.getCmdName() + " - " + domoCmd.getIdObjet());
            try {
                for(int i = 0; i <= equipementAdapter.getCount()-1; i++) {
                    DomoEquipement domoEquipement = (DomoEquipement) equipementAdapter.getItem(i);
                    if (domoEquipement.getIdObjet().equals(domoCmd.getIdObjet())) {
                        spinnerEquipements.setSelection(i);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "loadSpinner: ",e);
            }
        }

    }
}
