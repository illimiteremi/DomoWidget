package illimiteremi.domowidget.DomoGeneralSetting.Fragments;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

import illimiteremi.domowidget.DomoAdapter.BoxAdapter;
import illimiteremi.domowidget.DomoAdapter.MultiRessAdapter;
import illimiteremi.domowidget.DomoAdapter.WidgetAdapter;
import illimiteremi.domowidget.DomoGeneralSetting.BoxSetting;
import illimiteremi.domowidget.DomoJSONRPC.JeedomActionFindListener;
import illimiteremi.domowidget.DomoJSONRPC.JeedomFindDialogFragment;
import illimiteremi.domowidget.DomoUtils.DomoRessourceUtils;
import illimiteremi.domowidget.DomoUtils.DomoUtils;
import illimiteremi.domowidget.DomoWidgetMulti.MultiWidget;
import illimiteremi.domowidget.DomoWidgetMulti.MultiWidgetProvider;
import illimiteremi.domowidget.DomoWidgetMulti.MultiWidgetRess;
import illimiteremi.domowidget.R;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.APPWIDGET_UPDATE;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.BOX;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.DEFAULT_TIMEOUT;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.INFO_CMD;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.MULTI;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.NEW_WIDGET;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.NO_WIDGET;
import static illimiteremi.domowidget.DomoUtils.DomoUtils.hideKeyboard;


public class WidgetMultiFragment extends Fragment {


    private static final String     TAG                       = "[DOMO_MULTI_SEETING]";

    private Context                 context;                  // Context

    private int                     newIdWidget;              // ID du widget à créer
    private boolean                 isConfigured;             // Widget configuré

    private AutoCompleteTextView    name;                     // Nom du Widget
    private AutoCompleteTextView    etat;                     // Action domotique
    private Spinner                 spinnerBox;               // Spinner liste des box
    private AutoCompleteTextView    timeOut;                  // TimeOut avant retour
    private TextView                textViewAction;           // Text liste des actions

    private static ListView         listView;                 // Liste des actions
    private static MultiRessAdapter multiRessAdapter;         // Adapter de la liste des actions

    private Spinner                 spinnerWidgets;           // Spinner de la liste des widgets
    private LinearLayout            linearLayoutWidget;       // Layout de la configuration du widget
    private BoxSetting              selectedBox;              // Box domotique utilisé par le widget
    private MultiWidget             widget;                   // Widget
    private WidgetAdapter           widgetAdapter;            // Adapter de la liste des widgets
    private MenuItem                deleteAction;             // MenuItem Delete

    // Listener de modification d'une ressource
    public static final DomoRessourceUtils.OnMultiWidgetRessourceFragmentListener ressourceFragmentListener = new DomoRessourceUtils.OnMultiWidgetRessourceFragmentListener() {
        @Override
        public void onAddRessource(Context context, ArrayList<MultiWidgetRess> multiWidgetRess) {
            Log.d(TAG, "onAddRessource");
                if (multiRessAdapter.isEmpty()) {
                    multiRessAdapter = new MultiRessAdapter(context, multiWidgetRess);
                    listView.setAdapter(multiRessAdapter);
                } else {
                    multiRessAdapter.clear();
                    multiRessAdapter.addAll(multiWidgetRess);
                }
                multiRessAdapter.notifyDataSetChanged();
                DomoRessourceUtils.setListViewHeightBasedOnItems(listView);
        }
    };

    /**
     * Listener de selection des commandes
     */
    private final JeedomActionFindListener jeedomActionFindListener = new JeedomActionFindListener() {
        @Override
        public void onCancel() {
            Log.d(TAG, "onCancel: ");
        }

        @Override
        public void onOk(AutoCompleteTextView cmdTextView, String cmd) {
            cmdTextView.setText(cmd);
            Log.d(TAG, "onOk: ");
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        // Re Chargement des spinners
        if (newIdWidget == 0) {
            loadSpinner();
            // Rechargement des menus
            getActivity().invalidateOptionsMenu();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Annulation du widget et suppression de la bdd
        if (!isConfigured && newIdWidget != 0) {
            Log.d(TAG, "Widget non enregistré");
            DomoUtils.removeObjet(context, widget);
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, newIdWidget);
            getActivity().setResult(RESULT_CANCELED);
            getActivity().finish();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.menu_multi_settings, menu);
        MenuItem saveAction = menu.findItem(R.id.save_action);
        MenuItem addAction  = menu.findItem(R.id.add_action);
        deleteAction        = menu.findItem(R.id.delete_action);

        if (newIdWidget != 0) {
            // Check si nouveau widget
            linearLayoutWidget.setVisibility(View.VISIBLE);
            MultiWidget emptyWidget = new MultiWidget(context, NEW_WIDGET);
            emptyWidget.setDomoName(context.getResources().getString(R.string.new_widget));
            widgetAdapter.insert(emptyWidget, 0);
            widgetAdapter.notifyDataSetChanged();
            saveAction.setVisible(true);
            deleteAction.setVisible(false);
            addAction.setVisible(false);
            spinnerWidgets.performClick();
        } else {
            // Check si aucun widget
            if (widgetAdapter.getCount() == 0) {
                linearLayoutWidget.setVisibility(View.INVISIBLE);
                spinnerWidgets.setEnabled(false);
                saveAction.setVisible(false);
                deleteAction.setVisible(false);
                addAction.setVisible(false);
                MultiWidget noWidget = new MultiWidget(context, NO_WIDGET);
                noWidget.setDomoName(context.getResources().getString(R.string.no_widget));
                widgetAdapter.add(noWidget);
            } else {
                // Affichage du layout
                widget = (MultiWidget) spinnerWidgets.getSelectedItem();
                linearLayoutWidget.setVisibility(View.VISIBLE);
                saveAction.setVisible(true);
                addAction.setVisible(true);
            }
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        hideKeyboard(getActivity());
        switch (item.getItemId()) {
            case R.id.save_action:
                Log.d(TAG, "Mise à jour du widget " + widget.getDomoName());
                backupWidgetData();
                break;
            case R.id.delete_action:
                Log.d(TAG, "Suppression du widget en bdd : " + widget.getDomoName());
                DomoUtils.removeObjet(context, widget);
                widget = null;
                // Refresh current fragment
                FragmentTransaction ftDel = getFragmentManager().beginTransaction();
                ftDel.detach(this).attach(this).commit();
                break;
            case R.id.add_action:
                // Ouverture du fragment
                int idwidget = newIdWidget != 0 ? newIdWidget : widget.getDomoId();
                DomoRessourceUtils.MultiWidgetRessourceFragment fragment = DomoRessourceUtils.MultiWidgetRessourceFragment.newInstance(idwidget, 0);
                fragment.setOnRessourceListener(ressourceFragmentListener);
                FragmentTransaction ftAdd = getFragmentManager().beginTransaction();
                fragment.show(ftAdd, "NEW RESSOURCE");
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = getContext();

        // Maj du titre
        getActivity().setTitle(getResources().getString(R.string.fragment_multi));

        // Récuperation de l'id Widget
        Bundle bundle = getArguments();
        if (bundle != null) {
            // Création d'un nouveau Widget GPS
            newIdWidget = bundle.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID);
            isConfigured = false;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mutli_setting, container, false);
        setHasOptionsMenu(true);

        name               = (AutoCompleteTextView) view.findViewById(R.id.editName);
        spinnerBox         = (Spinner) view.findViewById(R.id.spinnerBox);
        spinnerWidgets     = (Spinner) view.findViewById(R.id.spinnerWidgets);
        linearLayoutWidget = (LinearLayout) view.findViewById(R.id.linearWidget);
        etat               = (AutoCompleteTextView) view.findViewById(R.id.editEtat);
        listView           = (ListView) view.findViewById(R.id.ressourceList);
        textViewAction     = (TextView) view.findViewById(R.id.textViewAction);
        timeOut            = (AutoCompleteTextView) view.findViewById(R.id.editTimeOut);

        // Chargement des spinners
        loadSpinner();

        // init des fragment de selection des commandes
        initDialogFragment();

        spinnerWidgets.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                widget = (MultiWidget) adapterView.getAdapter().getItem(i);
                // Chargement des informations du Widget
                if (widget != null) {
                    try {
                        selectedBox = widget.getSelectedBox();
                        name.setText(widget.getDomoName());
                        etat.setText(widget.getDomoState());
                        timeOut.setText(String.format(Locale.getDefault(), "%d", widget.getDomoTimeOut()));

                        multiRessAdapter = new MultiRessAdapter(context, widget.getMutliWidgetRess());
                        listView.setAdapter(multiRessAdapter);
                        DomoRessourceUtils.setListViewHeightBasedOnItems(listView);

                        // Selection du spinner box associé au widget
                        if (selectedBox != null) {
                            int spinnerPostion = DomoUtils.getSpinnerPosition(context, selectedBox);
                            spinnerBox.setSelection(spinnerPostion);
                        } else {
                            spinnerBox.setSelection(spinnerBox.getAdapter().getCount()-1);
                        }

                        // Check si widget present sur le bureau
                        if (widget.getPresent()) {
                            deleteAction.setVisible(false);
                        } else {
                            deleteAction.setVisible(true);
                        }
                    } catch (Exception e) {
                        Log.d(TAG, "Erreur " + e);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // Listener de la liste des box
        spinnerBox.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    selectedBox = (BoxSetting) adapterView.getAdapter().getItem(i);
                    if (selectedBox.getBoxId() != 0 && widget != null) {
                        widget.setDomoBox(selectedBox.getBoxId() == null ? 0 : selectedBox.getBoxId());
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Erreur " + e);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // Chargement des spinners
        loadSpinner();
        // Inflate the layout for this fragment
        return view;
    }

    /**
     * Affichage des informations Widget
     */
    private void loadSpinner()  {
        // Chargement du spinner de la liste des widgets
        widgetAdapter = (WidgetAdapter) DomoUtils.createAdapter(context, MULTI);
        spinnerWidgets.setAdapter(widgetAdapter);

        // Chargement du spinner Box
        BoxAdapter boxAdapter = (BoxAdapter) DomoUtils.createAdapter(context, BOX);
        spinnerBox.setAdapter(boxAdapter);
    }

    /**
     * Mise à jour du widget dans la bdd
     */
    private void backupWidgetData() {
        widget.setDomoName(name.getText().toString());
        widget.setDomoState(etat.getText().toString());
        widget.setDomoState(widget.getDomoState());
        String timeout = timeOut.getText().toString();
        widget.setDomoTimeOut(timeout.isEmpty() ? DEFAULT_TIMEOUT : Integer.parseInt(timeout));

        selectedBox = (BoxSetting) spinnerBox.getSelectedItem();
        widget.setDomoBox(selectedBox.getBoxId());

        // Si nouveau widget
        if (newIdWidget != 0) {
            // Copie des ressources
            ArrayList<MultiWidgetRess> newMutliRess = widget.getMutliWidgetRess();
            if (newMutliRess != null) {
                // Copie des actions existantes
                for (MultiWidgetRess resultValue : newMutliRess) {
                    resultValue.setDomoId(newIdWidget);
                    DomoUtils.insertObjet(context, resultValue);
                }
            }
            widget.setMutliWidgetRess(null);
            widget.setDomoId(newIdWidget);
            if (DomoUtils.insertObjet(context, widget) != -1) {
                isConfigured = true;
                Log.d(TAG, "Création du widget en BDD");
            }

            // RESULT OK
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, newIdWidget);
            getActivity().setResult(RESULT_OK, resultValue);
            loadSpinner();
            getActivity().finish();
        } else {
            // Update d'un widget
            DomoUtils.updateObjet(context, widget);
            // Message de sauvegarde
            Toast.makeText(getContext(), getContext().getResources().getString(R.string.save_box), Toast.LENGTH_SHORT).show();
        }

        // Mise à jour des widgets
        Intent updateIntent = new Intent(context, MultiWidgetProvider.class);
        updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widget.getDomoId());
        updateIntent.setAction(APPWIDGET_UPDATE);
        context.sendBroadcast(updateIntent);
    }

    /**
     * initDialogFragment
     */
    private void initDialogFragment() {
        etat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JeedomFindDialogFragment fragment = new JeedomFindDialogFragment();
                fragment.setOnJeedomActionFindListener(jeedomActionFindListener, etat, INFO_CMD);
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                fragment.show(ft, "Find cmd");
            }
        });
    }

}
