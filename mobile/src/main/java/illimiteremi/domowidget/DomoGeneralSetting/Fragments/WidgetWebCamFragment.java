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
import android.widget.Spinner;
import android.widget.Toast;

import illimiteremi.domowidget.DomoAdapter.BoxAdapter;
import illimiteremi.domowidget.DomoAdapter.WidgetAdapter;
import illimiteremi.domowidget.DomoGeneralSetting.BoxSetting;
import illimiteremi.domowidget.DomoUtils.DomoUtils;
import illimiteremi.domowidget.DomoWidgetWebCam.WebCamWidget;
import illimiteremi.domowidget.DomoWidgetWebCam.WidgetWebCamProvider;
import illimiteremi.domowidget.R;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.APPWIDGET_UPDATE;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.BOX;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.NEW_WIDGET;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.NO_WIDGET;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.UPDATE_ALL_WIDGET_WEBCAM;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.WEBCAM;
import static illimiteremi.domowidget.DomoUtils.DomoUtils.hideKeyboard;

/**
 * Created by xzaq496 on 17/01/2018.
 */

public class WidgetWebCamFragment extends Fragment {

    private static final String   TAG                       = "[DOMO_WEBCAM_SEETING]";

    private Context               context;                  // Context

    private int                   newIdWidget;              // ID du widget à créer
    private boolean               isConfigured;             // Widget configuré

    private AutoCompleteTextView  name;                     // Nom du Widget
    private AutoCompleteTextView  url;                      // url de capture
    private AutoCompleteTextView  port;                     // Port de la webCam
    private Spinner               spinnerBox;               // Spinner liste des box
    private Spinner               spinnerWidgets;           // Spinner de la liste des widgets
    private LinearLayout          linearLayoutWidget;       // Layout de la configuration du widget

    private BoxSetting            selectedBox;              // Box domotique utilisé par le widget
    private WidgetAdapter         widgetAdapter;            // Adapter de la liste des widgets
    private WebCamWidget          widget;                   // Widget

    private MenuItem               deleteAction;             // MenuItem Delete

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
        inflater.inflate(R.menu.menu_save_settings, menu);
        MenuItem saveAction   = menu.findItem(R.id.save_action);
        deleteAction = menu.findItem(R.id.delete_action);

        if (newIdWidget != 0) {
            // Check si nouveau widget
            linearLayoutWidget.setVisibility(View.VISIBLE);
            WebCamWidget emptyWidget = new WebCamWidget(context, NEW_WIDGET);
            emptyWidget.setDomoName(context.getResources().getString(R.string.new_widget));
            widgetAdapter.insert(emptyWidget, 0);
            widgetAdapter.notifyDataSetChanged();
            saveAction.setVisible(true);
            deleteAction.setVisible(false);
            spinnerWidgets.performClick();
        } else {
            // Check si aucun widget
            if (widgetAdapter.getCount() == 0) {
                linearLayoutWidget.setVisibility(View.INVISIBLE);
                spinnerWidgets.setEnabled(false);
                saveAction.setVisible(false);
                deleteAction.setVisible(false);
                WebCamWidget noWidget = new WebCamWidget(context, NO_WIDGET);
                noWidget.setDomoName(context.getResources().getString(R.string.no_widget));
                widgetAdapter.add(noWidget);
            } else {
                // Affichage du layout
                widget = (WebCamWidget) spinnerWidgets.getSelectedItem();
                linearLayoutWidget.setVisibility(View.VISIBLE);
                saveAction.setVisible(true);
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
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.detach(this).attach(this).commit();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = getContext();
        getActivity().setResult(RESULT_CANCELED);
        // Maj du titre
        getActivity().setTitle(getResources().getString(R.string.fragment_webcam));
        // Récuperation de l'id Widget
        Bundle bundle = getArguments();
        if (bundle != null) {
            // Création d'un nouveau Widget WEBCAM
            newIdWidget = bundle.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID);
            isConfigured = false;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_webcam_setting, container, false);
        setHasOptionsMenu(true);

        name               = view.findViewById(R.id.editName);
        spinnerBox         = view.findViewById(R.id.spinnerBox);
        spinnerWidgets     = view.findViewById(R.id.spinnerWidgets);
        linearLayoutWidget = view.findViewById(R.id.linearWidget);
        port               = view.findViewById(R.id.editPort);
        url                = view.findViewById(R.id.editURL);

        // Chargement des spinners
        loadSpinner();

        // Listener de la liste des widgets
        spinnerWidgets.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                widget = (WebCamWidget) adapterView.getAdapter().getItem(i);
                // Chargement des informations du Widget
                if (widget != null) {
                    try {
                        selectedBox = widget.getSelectedBox();
                        name.setText(widget.getDomoName());
                        port.setText(widget.getDomoPort());
                        url.setText(widget.getDomoUrl());

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
        // Inflate the layout for this fragment
        return view;
    }

    /**
     * Affichage des informations Widget
     */
    private void loadSpinner()  {
        // Chargement du spinner de la liste des widgets
        widgetAdapter = (WidgetAdapter) DomoUtils.createAdapter(context, WEBCAM);
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
        widget.setDomoPort(port.getText().toString());
        widget.setDomoUrl(url.getText().toString());
        selectedBox = (BoxSetting) spinnerBox.getSelectedItem();
        widget.setDomoBox(selectedBox.getBoxId());

        // Si nouveau widget
        if (newIdWidget != 0) {
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
        Intent updateIntent = new Intent(context, WidgetWebCamProvider.class);
        updateIntent.setAction(UPDATE_ALL_WIDGET_WEBCAM);
        updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widget.getDomoId());
        updateIntent.setAction(APPWIDGET_UPDATE);
        context.sendBroadcast(updateIntent);
    }
}
