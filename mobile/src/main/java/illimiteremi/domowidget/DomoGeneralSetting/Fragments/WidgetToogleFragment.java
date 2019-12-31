package illimiteremi.domowidget.DomoGeneralSetting.Fragments;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Locale;

import illimiteremi.domowidget.DomoAdapter.BoxAdapter;
import illimiteremi.domowidget.DomoAdapter.WidgetAdapter;
import illimiteremi.domowidget.DomoGeneralSetting.BoxSetting;
import illimiteremi.domowidget.DomoJSONRPC.JeedomActionFindListener;
import illimiteremi.domowidget.DomoJSONRPC.JeedomFindDialogFragment;
import illimiteremi.domowidget.DomoUtils.DomoBitmapUtils;
import illimiteremi.domowidget.DomoUtils.DomoConstants;
import illimiteremi.domowidget.DomoUtils.DomoRessourceUtils;
import illimiteremi.domowidget.DomoUtils.DomoUtils;
import illimiteremi.domowidget.DomoWidgetToogle.ToogleWidget;
import illimiteremi.domowidget.DomoWidgetToogle.WidgetToogleProvider;
import illimiteremi.domowidget.R;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.APPWIDGET_UPDATE;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.BOX;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.DEFAULT_TIMEOUT;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.NEW_WIDGET;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.NO_WIDGET;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.TOOGLE;
import static illimiteremi.domowidget.DomoUtils.DomoUtils.hideKeyboard;


public class WidgetToogleFragment extends Fragment {

    private static final String   TAG                       = "[DOMO_TOOGLE_SEETING]";

    private Context               context;                  // Context

    private int                   newIdWidget;              // ID du widget à créer
    private boolean               isConfigured;             // Widget configuré

    private AutoCompleteTextView  name;                     // Nom du Widget
    private Spinner               spinnerBox;               // Spinner liste des box
    private Spinner               spinnerWidgets;           // Spinner de la liste des widgets
    private ImageButton           imageButtonOn;            // Image action ON
    private ImageButton           imageButtonOff;           // Image action OFF
    private CheckBox              isLock;                   // Verouillage widget
    private LinearLayout          linearLayoutWidget;       // Layout de la configuration du widget

    private AutoCompleteTextView  etat;                     // Etat du widget
    private AutoCompleteTextView  on;                       // Action On
    private AutoCompleteTextView  off;                      // Action Off
    private AutoCompleteTextView  expReg;                   // Exp régulière On
    private AutoCompleteTextView  timeOut;                  // TimeOut avant lecture état
    private ImageButton           searchEtatButton;         // Button recherche etat
    private ImageButton           searchActionOnButton;     // Button recherche etat
    private ImageButton           searchActionOffButton;    // Button recherche etat

    private BoxSetting            selectedBox;              // Box domotique utilisé par le widget
    private ToogleWidget          widget;                   // Widget
    private WidgetAdapter         widgetAdapter;            // Adapter de la liste des widgets
    private MenuItem              deleteAction;             // MenuItem Delete

    private DomoBitmapUtils       bitmapUtils;              // Boite à outils graphique


    private final DomoRessourceUtils.OnRessourceFragmentListener ressourceFragmentListener = new DomoRessourceUtils.OnRessourceFragmentListener() {
        @Override
        public void onSelectRessource(Boolean isOn, int idRessource) {
            if (isOn) {
                widget.setDomoIdImageOn(idRessource);
                imageButtonOn.setImageBitmap(bitmapUtils.getBitmapRessource(widget, true));
            } else {
                widget.setDomoIdImageOff(idRessource);
                imageButtonOff.setImageBitmap(bitmapUtils.getBitmapRessource(widget, false));
            }
            Log.d(TAG, "Ressource " + isOn + " - " + idRessource);
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
        inflater.inflate(R.menu.menu_save_settings, menu);
        MenuItem saveAction   = menu.findItem(R.id.save_action);
        deleteAction = menu.findItem(R.id.delete_action);

        if (newIdWidget != 0) {
            // Check si nouveau widget
            linearLayoutWidget.setVisibility(View.VISIBLE);
            ToogleWidget emptyWidget = new ToogleWidget(context, NEW_WIDGET);
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
                ToogleWidget noWidget = new ToogleWidget(context, NO_WIDGET);
                noWidget.setDomoName(context.getResources().getString(R.string.no_widget));
                widgetAdapter.add(noWidget);
            } else {
                // Affichage du layout
                widget = (ToogleWidget) spinnerWidgets.getSelectedItem();
                linearLayoutWidget.setVisibility(View.VISIBLE);
                saveAction.setVisible(true);
            }
        }
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
        // Maj du titre
        getActivity().setTitle(getResources().getString(R.string.fragment_action));

        bitmapUtils = new DomoBitmapUtils(context);

        // Récuperation de l'id Widget
        Bundle bundle = getArguments();
        if (bundle != null) {
            // Création d'un nouveau Widget GPS
            newIdWidget = bundle.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID);
            isConfigured = false;
        }
        Log.d(TAG, "newIdWidget = " + newIdWidget);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_toogle_setting, container, false);
        setHasOptionsMenu(true);

        name               = view.findViewById(R.id.editName);
        spinnerBox         = view.findViewById(R.id.spinnerBox);
        spinnerWidgets     = view.findViewById(R.id.spinnerWidgets);
        linearLayoutWidget = view.findViewById(R.id.linearWidget);
        imageButtonOn      = view.findViewById(R.id.imageButtonOn);
        imageButtonOff     = view.findViewById(R.id.imageButtonOff);
        isLock             = view.findViewById(R.id.checkBoxLock);
        etat               = view.findViewById(R.id.editEtat);
        on                 = view.findViewById(R.id.editOn);
        off                = view.findViewById(R.id.editOff);
        expReg             = view.findViewById(R.id.editExpReg);
        timeOut            = view.findViewById(R.id.editTimeOut);
        searchEtatButton   = view.findViewById(R.id.searchEtatButton);
        searchActionOnButton  = view.findViewById(R.id.searchActionOnButton);
        searchActionOffButton = view.findViewById(R.id.searchActionOffButton);

        // Chargement des spinners
        loadSpinner();

        // Init des fragment de selection des commandes
        initDialogFragment();

        // Listener de la liste des widgets
        spinnerWidgets.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                widget = (ToogleWidget) adapterView.getAdapter().getItem(i);
                // Chargement des informations du Widget
                if (widget != null) {
                    try {
                        selectedBox = widget.getSelectedBox();
                        name.setText(widget.getDomoName());
                        //action.setText(widget.getDomoAction());
                        imageButtonOn.setImageBitmap(bitmapUtils.getBitmapRessource(widget, true));
                        imageButtonOff.setImageBitmap(bitmapUtils.getBitmapRessource(widget, false));
                        isLock.setChecked((widget.getDomoLock() == 1));
                        etat.setText(widget.getDomoState());
                        on.setText(widget.getDomoOn());
                        off.setText(widget.getDomoOff());
                        expReg.setText(widget.getDomoExpReg());
                        timeOut.setText(String.format(Locale.getDefault(), "%d", widget.getDomoTimeOut()));
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

        // Listener ation image ON
        imageButtonOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DomoRessourceUtils.RessourceFragment fragment = DomoRessourceUtils.RessourceFragment.newInstance(true);
                fragment.setOnRessourceListener(ressourceFragmentListener);
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                fragment.show(ft, "Ressource ON");
            }
        });

        // Listener ation image OFF
        imageButtonOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DomoRessourceUtils.RessourceFragment fragment = DomoRessourceUtils.RessourceFragment.newInstance(false);
                fragment.setOnRessourceListener(ressourceFragmentListener);
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                fragment.show(ft, "Ressource OFF");
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
        widgetAdapter = (WidgetAdapter) DomoUtils.createAdapter(context, TOOGLE);
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
        //widget.setDomoAction(action.getText().toString());
        widget.setDomoLock((isLock.isChecked())? 1 : 0);
        selectedBox = (BoxSetting) spinnerBox.getSelectedItem();
        widget.setDomoBox(selectedBox.getBoxId());
        widget.setDomoState(etat.getText().toString());
        widget.setDomoOn(on.getText().toString());
        widget.setDomoOff(off.getText().toString());
        widget.setDomoExpReg(expReg.getText().toString());
        String timeout = timeOut.getText().toString();
        widget.setDomoTimeOut(timeout.isEmpty() ? DEFAULT_TIMEOUT : Integer.parseInt(timeout));

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
        Intent updateIntent = new Intent(context, WidgetToogleProvider.class);
        updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widget.getDomoId());
        updateIntent.setAction(APPWIDGET_UPDATE);
        context.sendBroadcast(updateIntent);
    }

    /**
     * initDialogFragment
     */
    private void initDialogFragment() {

        // Creation d'une animation sur la loupe
        Animation myAnim = AnimationUtils.loadAnimation(context, R.anim.bounce);
        DomoBitmapUtils.MyBounceInterpolator interpolator = new DomoBitmapUtils.MyBounceInterpolator(0.2, 20);
        myAnim.setInterpolator(interpolator);

        final JeedomFindDialogFragment fragment = new JeedomFindDialogFragment();

        searchEtatButton.startAnimation(myAnim);
        searchEtatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment.setOnJeedomActionFindListener(jeedomActionFindListener, etat, DomoConstants.CALLBACK_TYPE.INFO);
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                fragment.show(ft, "Find Info");
            }
        });

        searchActionOnButton.startAnimation(myAnim);
        searchActionOnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment.setOnJeedomActionFindListener(jeedomActionFindListener, on, DomoConstants.CALLBACK_TYPE.ACTION);
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                fragment.show(ft, "Find Action");
            }
        });

        searchActionOffButton.startAnimation(myAnim);
        searchActionOffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment.setOnJeedomActionFindListener(jeedomActionFindListener, off, DomoConstants.CALLBACK_TYPE.ACTION);
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                fragment.show(ft, "Find Action");
            }
        });
    }

}
