package illimiteremi.domowidget.DomoJSONRPC;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;

import java.util.ArrayList;

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
    private BoxSetting boxSetting;

    private Button               cancelButton;                  // Button annuler
    private Button               okButton;                      // Button choisir
    private ImageButton          download;                      // Button download
    private Spinner              spinnerEquipements;            // Spinner de la liste des equipements
    private Spinner              spinnerCmd;                    // Spinner de la liste des commandes
    private AutoCompleteTextView actionJeedom;                  // Action Jeedom

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
        download      = mParentView.findViewById(R.id.imageButtonDownload);
        actionJeedom = mParentView.findViewById(R.id.actionJeedom);

        spinnerCmd         = mParentView.findViewById(R.id.spinnerCmd);
        spinnerEquipements = mParentView.findViewById(R.id.spinnerEquipements);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Recuperation des data Jeedom
                if (boxSetting != null) {
                    DomoUtils.getAllJeedomObjet(context, boxSetting);
                    DomoUtils.getAllJeedomCmd(context, boxSetting);
                }
            }
        });

        loadSpinner();

        return mParentView;
    }

    public void setOnJeedomActionFindListener(JeedomActionFindListener listener, BoxSetting boxSetting) {
        this.boxSetting = boxSetting;
        mListener = listener;
    }

    private void loadSpinner() {
        /**
         * Chargement des Spinners
         */
        EquipementAdapter equipementAdapter = (EquipementAdapter) DomoUtils.createAdapter(context, EQUIPEMENT);
        spinnerEquipements.setAdapter(equipementAdapter);
    }
}
