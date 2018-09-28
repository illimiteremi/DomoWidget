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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

import illimiteremi.domowidget.DomoAdapter.BoxAdapter;
import illimiteremi.domowidget.DomoAdapter.WidgetAdapter;
import illimiteremi.domowidget.DomoGeneralSetting.BoxSetting;
import illimiteremi.domowidget.DomoJSONRPC.JeedomActionFindListener;
import illimiteremi.domowidget.DomoJSONRPC.JeedomFindDialogFragment;
import illimiteremi.domowidget.DomoUtils.DomoBitmapUtils;
import illimiteremi.domowidget.DomoUtils.DomoConstants;
import illimiteremi.domowidget.DomoUtils.DomoRessourceUtils;
import illimiteremi.domowidget.DomoUtils.DomoUtils;
import illimiteremi.domowidget.DomoWidgetSeekBar.SeekBarWidget;
import illimiteremi.domowidget.DomoWidgetSeekBar.SeekBarWidgetProvider;
import illimiteremi.domowidget.R;
import yuku.ambilwarna.colorpicker.AmbilWarnaDialogFragment;
import yuku.ambilwarna.colorpicker.OnAmbilWarnaListener;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.ACTION_CMD;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.APPWIDGET_UPDATE;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.BOX;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.INFO_CMD;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.NEW_WIDGET;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.NO_WIDGET;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.SEEKBAR;
import static illimiteremi.domowidget.DomoUtils.DomoUtils.hideKeyboard;


public class WidgetSeekBarFragment extends Fragment {

    private static final String   TAG                       = "[DOMO_SEEKBAR_SETTING]";

    private Context               context;                  // Context

    private int                   newIdWidget;              // ID du widget à créer
    private boolean               isConfigured;             // Widget configuré

    private static int            mColor = 0;               // Couleur default
    private AutoCompleteTextView  name;                     // Nom du Widget
    private Spinner               spinnerBox;               // Spinner liste des box
    private Spinner               spinnerWidgets;           // Spinner de la liste des widgets
    private AutoCompleteTextView  action;                   // Action domotique
    private AutoCompleteTextView  etat;                     // Retour d'etat
    private ImageButton           imageButtonOn;            // Image action ON
    private LinearLayout          linearLayoutWidget;       // Layout de la configuration du widget
    private TextView              editColor;                // Button de selection coleur widget
    private TextView              editMax;                  // Valeur Max de la barre
    private TextView              editMin;                  // Valeur Min de la barre

    private BoxSetting            selectedBox;              // Box domotique utilisé par le widget
    private WidgetAdapter         widgetAdapter;            // Adapter de la liste des widgets
    private SeekBarWidget         widget;                   // Widget

    private ImageButton           searchEtatButton;         // Button recherche etat
    private ImageButton           searchActionOnButton;     // Button recherche etat

    private DomoBitmapUtils       bitmapUtils;              // Boite à outils graphique
    private MenuItem              deleteAction;             // MenuItem Delete

    private final OnAmbilWarnaListener onAmbilWarnaListener = new OnAmbilWarnaListener() {
        @Override
        public void onCancel(AmbilWarnaDialogFragment dialogFragment) {
            Log.d(TAG, "onCancel()");
        }

        @Override
        public void onOk(AmbilWarnaDialogFragment dialogFragment, int color) {
            Log.d(TAG, "onOk(). Color: " + color);
            widget.setDomoColor(String.valueOf(color));
            DomoUtils.updateObjet(context, widget);
            mColor = color;
            editColor.setBackgroundColor(mColor);
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

    private final DomoRessourceUtils.OnRessourceFragmentListener ressourceFragmentListener = new DomoRessourceUtils.OnRessourceFragmentListener() {
        @Override
        public void onSelectRessource(Boolean isOn, int idRessource) {
            widget.setDomoIdImageOn(idRessource);
            imageButtonOn.setImageBitmap(bitmapUtils.getBitmapRessource(widget, true));
            Log.d("[DOMO", "Ressource Image - " + isOn + " = " + idRessource);
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
        MenuItem saveAction = menu.findItem(R.id.save_action);
        deleteAction = menu.findItem(R.id.delete_action);

        if (newIdWidget != 0) {
            // Check si nouveau widget
            linearLayoutWidget.setVisibility(View.VISIBLE);
            SeekBarWidget emptyWidget = new SeekBarWidget(context, NEW_WIDGET);
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
                SeekBarWidget noWidget = new SeekBarWidget(context, NO_WIDGET);
                noWidget.setDomoName(context.getResources().getString(R.string.no_widget));
                widgetAdapter.add(noWidget);
            } else {
                // Affichage du layout
                widget = (SeekBarWidget) spinnerWidgets.getSelectedItem();
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
        bitmapUtils = new DomoBitmapUtils(context);
        getActivity().setResult(RESULT_CANCELED);
        // Maj du titre
        getActivity().setTitle(getResources().getString(R.string.fragment_seekbar));
        // Récuperation de l'id Widget
        Bundle bundle = getArguments();
        if (bundle != null) {
            // Création d'un nouveau Widget GPS
            newIdWidget = bundle.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_seekbar_setting, container, false);
        setHasOptionsMenu(true);

        name               = view.findViewById(R.id.editName);
        spinnerBox         = view.findViewById(R.id.spinnerBox);
        spinnerWidgets     = view.findViewById(R.id.spinnerWidgets);
        linearLayoutWidget = view.findViewById(R.id.linearWidget);
        action             = view.findViewById(R.id.editAction);
        etat               = view.findViewById(R.id.editEtat);
        editMax            = view.findViewById(R.id.editMax);
        editMin            = view.findViewById(R.id.editMin);
        imageButtonOn      = view.findViewById(R.id.imageButtonOn);
        editColor          = view.findViewById(R.id.editColor);
        searchEtatButton   = view.findViewById(R.id.searchEtatButton);
        searchActionOnButton  = view.findViewById(R.id.searchActionOnButton);

        // Chargement des spinners
        loadSpinner();

        // init des fragment de selection des commandes
        initDialogFragment();

        // Listener de la liste des widgets
        spinnerWidgets.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                widget = (SeekBarWidget) adapterView.getAdapter().getItem(i);
                // Chargement des informations du Widget
                if (widget != null) {
                    try {
                        selectedBox = widget.getSelectedBox();
                        name.setText(widget.getDomoName());
                        action.setText(widget.getDomoAction());
                        etat.setText(widget.getDomoState());
                        editMax.setText(widget.getDomoMaxValue().toString());
                        editMin.setText(widget.getDomoMinValue().toString());
                        imageButtonOn.setImageBitmap(bitmapUtils.getBitmapRessource(widget, true));
                        mColor = Integer.parseInt(widget.getDomoColor());
                        if (mColor == 0) {
                            mColor = -16777216;
                        }
                        editColor.setBackgroundColor(mColor);
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

        // Inflate the layout for this fragment
        return view;
    }

    /**
     * Affichage des informations Widget
     */
    private void loadSpinner()  {
        // Chargement du spinner de la liste des widgets
        widgetAdapter = (WidgetAdapter) DomoUtils.createAdapter(context, SEEKBAR);
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
        widget.setDomoAction(action.getText().toString());
        widget.setDomoState(etat.getText().toString());

        int max = Objects.equals(editMax.getText().toString(), "") ? 255 : Integer.valueOf(editMax.getText().toString());
        int min = Objects.equals(editMin.getText().toString(), "") ? 0 : Integer.valueOf(editMin.getText().toString());
        widget.setDomoMaxValue(max);
        widget.setDomoMinValue(min);

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
        Intent updateIntent = new Intent(context, SeekBarWidgetProvider.class);
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
                fragment.setOnJeedomActionFindListener(jeedomActionFindListener, action, DomoConstants.CALLBACK_TYPE.SLIDER);
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                fragment.show(ft, "Find Info");
            }
        });
    }
}
