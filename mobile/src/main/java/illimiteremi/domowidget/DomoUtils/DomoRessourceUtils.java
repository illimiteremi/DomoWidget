package illimiteremi.domowidget.DomoUtils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import illimiteremi.domowidget.DomoAdapter.IconAdapter;
import illimiteremi.domowidget.DomoGeneralSetting.IconSetting;
import illimiteremi.domowidget.DomoWidgetBdd.DomoBaseSQLite;
import illimiteremi.domowidget.DomoWidgetBdd.UtilsDomoWidget;
import illimiteremi.domowidget.DomoWidgetMulti.MultiWidget;
import illimiteremi.domowidget.DomoWidgetMulti.MultiWidgetRess;
import illimiteremi.domowidget.R;

/**
 * $Description
 *
 * @author xzaq496 (non modifiable)
 *         Date de création : 19/07/2016 (non modifiable)
 *         <p/>
 *         <b><u>Dernière modification : $Description_modification</u></b>
 *         <li>$Date     : 19/07/2016$</li>
 *         <li>$Author   : xzaq496$</li>
 *         <li>$Revision :        $</li>
 *         <li>$HeadURL  :        $</li>
 */
public class DomoRessourceUtils {

    private static final String TAG      = "[DOMO_RESSOURCE_UTILS]";

    /**
     * interface de RessourceFragment
     */
    public interface OnRessourceFragmentListener {
        void onSelectRessource(Boolean isOn, int idRessource);
    }

    /**
     * Class RessourceFragment (Selection des images ressource)
     */
    public static class RessourceFragment extends DialogFragment {

        private boolean                     isOn;
        private OnRessourceFragmentListener mListener;

        public RessourceFragment() {
        }

        public static RessourceFragment newInstance(Boolean isOn) {
            RessourceFragment ressourceFragment = new RessourceFragment();
            // Supply num input as an argument.
            Bundle args = new Bundle();
            args.putBoolean("isOn", isOn);
            ressourceFragment.setArguments(args);
            return ressourceFragment;
        }

        public void setOnRessourceListener(OnRessourceFragmentListener listener) {
            mListener = listener;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            // Récuperation des images ressources
            Bundle args = getArguments();
            List<IconSetting> listRessource = DomoRessourceUtils.getAllImageRessource(getActivity().getApplicationContext());

            View view = getActivity().getLayoutInflater().inflate(R.layout.list_ressource, null);
            ListView listView     = (ListView ) view.findViewById(R.id.listRessource);
            TextView iconTextView = (TextView)  view.findViewById(R.id.iconTextView);

            // Titre selon le type d'action
            isOn = args.getBoolean("isOn");
            if (isOn) {
                iconTextView.setText(getResources().getString(R.string.widget_action_image_on));
            } else {
                iconTextView.setText(getResources().getString(R.string.widget_action_image_off));
            }

            // Si aucune Icon de disponible
            if (listRessource == null) {
                return view;
            }

            IconAdapter ressourceAdapter = new IconAdapter(getActivity(), listRessource, false);
            listView.setAdapter(ressourceAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    IconSetting domoRessource = (IconSetting) parent.getItemAtPosition(position);
                    mListener.onSelectRessource(isOn, domoRessource.getId());
                    dismiss();
                }
            });

            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            return view;
        }
    }

    /**
     * interface de OnMultiWidgetRessourceFragmentListener
     */
    public interface OnMultiWidgetRessourceFragmentListener {
        void onAddRessource(Context context, ArrayList<MultiWidgetRess> multiWidgetRess);
    }

    /**
     * Class MultiWidgetRessourceFragment (Ajout d'une ressource mutliWidget)
     */
    public static class MultiWidgetRessourceFragment extends DialogFragment {

        private OnMultiWidgetRessourceFragmentListener mListener;

        private int             idRess;
        private int             idWidget;
        private MultiWidgetRess widgetRess;
        private DomoBitmapUtils bitmapUtils;

        public MultiWidgetRessourceFragment() {
        }

        public static MultiWidgetRessourceFragment newInstance(int idWidget, int idRess) {
            MultiWidgetRessourceFragment fragment = new MultiWidgetRessourceFragment();
            // Supply num input as an argument.
            Bundle args = new Bundle();
            args.putInt("idWidget", idWidget);
            args.putInt("idRess", idRess);
            fragment.setArguments(args);
            return fragment;
        }

        public void setOnRessourceListener(OnMultiWidgetRessourceFragmentListener listener) {
            mListener = listener;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            // Récuperation des images ressources
            Bundle args = getArguments();
            idRess      = args.getInt("idRess");
            idWidget    = args.getInt("idWidget");

            View view = getActivity().getLayoutInflater().inflate(R.layout.multi_ress_setting, null);

            final AutoCompleteTextView ressName   = (AutoCompleteTextView) view.findViewById(R.id.editName);
            final AutoCompleteTextView ressAction = (AutoCompleteTextView) view.findViewById(R.id.editEtat);
            final ImageButton    imageButtonOn    = (ImageButton) view.findViewById(R.id.imageButtonOn);
            final ImageButton    imageButtonOff   = (ImageButton) view.findViewById(R.id.imageButtonOff);
            Button               addButton        = (Button) view.findViewById(R.id.buttonSave);

            if (idRess != 0) {
                MultiWidgetRess newMultiWidgetRess = new MultiWidgetRess(idWidget);
                newMultiWidgetRess.setId(idRess);
                widgetRess = (MultiWidgetRess) DomoUtils.getObjetById(getContext(), newMultiWidgetRess);
            } else {
                widgetRess = new MultiWidgetRess(idWidget);
            }

            ressName.setText(widgetRess.getDomoName());
            ressAction.setText(widgetRess.getDomoAction());

            bitmapUtils = new DomoBitmapUtils(getContext());
            imageButtonOn.setImageBitmap(bitmapUtils.getBitmapRessource(widgetRess, true));
            imageButtonOff.setImageBitmap(bitmapUtils.getBitmapRessource(widgetRess, false));

            final DomoRessourceUtils.OnRessourceFragmentListener ressourceFragmentListener = new DomoRessourceUtils.OnRessourceFragmentListener() {
                @Override
                public void onSelectRessource(Boolean isOn, int idRessource) {
                    try {
                        if (isOn) {
                            widgetRess.setDomoIdImageOn(idRessource);
                            imageButtonOn.setImageBitmap(bitmapUtils.getBitmapRessource(widgetRess, true));
                        } else {
                            widgetRess.setDomoIdImageOff(idRessource);
                            imageButtonOff.setImageBitmap(bitmapUtils.getBitmapRessource(widgetRess, false));
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Erreur : " + e);
                    }
                    Log.d("[DOMO", "Ressource " + isOn + " - " + idRessource);
                }
            };

            imageButtonOn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DomoRessourceUtils.RessourceFragment fragment = DomoRessourceUtils.RessourceFragment.newInstance(true);
                    fragment.setOnRessourceListener(ressourceFragmentListener);
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    fragment.show(ft, "Ressource ON");
                }
            });

            imageButtonOff.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DomoRessourceUtils.RessourceFragment fragment = DomoRessourceUtils.RessourceFragment.newInstance(false);
                    fragment.setOnRessourceListener(ressourceFragmentListener);
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    fragment.show(ft, "Ressource OFF");
                }
            });

            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    widgetRess.setDomoName(ressName.getText().toString());
                    widgetRess.setDomoAction(ressAction.getText().toString());
                    widgetRess.multiWidgetRessLog();
                    if (widgetRess.getId() == null){
                        widgetRess.setId((int) DomoUtils.insertObjet(getContext(), widgetRess));
                    } else {
                        DomoUtils.updateObjet(getContext(),widgetRess);
                    }
                    MultiWidget multiWidget = (MultiWidget) DomoUtils.getObjetById(getContext(), new MultiWidget(getContext(), widgetRess.getDomoId()));
                    mListener.onAddRessource(getContext(), multiWidget.getMutliWidgetRess());
                    dismiss();
                }
            });

            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

            return view;
        }
    }

    /**
     * Mise à jour de la taille du tableau suivant le nombre d'item
     * @param listView
     */
    public static void setListViewHeightBasedOnItems(ListView listView) {

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter != null) {
            int numberOfItems = listAdapter.getCount();

            // Get total height of all items.
            int totalItemsHeight = 0;
            for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
                View item = listAdapter.getView(itemPos, null, listView);
                item.measure(0, 0);
                totalItemsHeight += item.getMeasuredHeight();
            }
            // Get total height of all item dividers + 255 avec ce style ???
            int totalDividersHeight = listView.getDividerHeight() * (numberOfItems - 1);

            // Set list height.
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalItemsHeight + totalDividersHeight;
            listView.setLayoutParams(params);
            listView.requestLayout();
        }
    }

    /**
     * Récuperation des ressources dans le BDD
     * @return
     */
    public static ArrayList<IconSetting> getAllImageRessource(Context context){
        // Récupère dans un Cursor
        ArrayList<IconSetting> listRessource = new ArrayList<>();

        DomoBaseSQLite domoBaseSQLite = new DomoBaseSQLite(context, UtilsDomoWidget.NOM_BDD, null, UtilsDomoWidget.VERSION_BDD);
        SQLiteDatabase bdd = domoBaseSQLite.getWritableDatabase();

        Cursor c = bdd.query(UtilsDomoWidget.TABLE_RESS_WIDGET, new String[] {
                UtilsDomoWidget.COL_ID,
                UtilsDomoWidget.COL_RESS_NAME,
                UtilsDomoWidget.COL_RESS_PATH},null , null, null, null, null);

        // Log.d(TAG, "Récuperation de la totalité des ressources");
        // Si aucun élément n'a été retourné dans la requête, on renvoie null
        if (c.getCount() == 0) {
            // Log.d(TAG, "Pas de ressource !");
            return null;
        }

        while (c.moveToNext()) {
            IconSetting domoRessource = new IconSetting();
            // On lui affecte toutes les infos grâce aux infos contenues dans le Cursor
            domoRessource.setId(c.getInt(c.getColumnIndexOrThrow(UtilsDomoWidget.COL_ID)));
            domoRessource.setRessourceName(c.getString(c.getColumnIndexOrThrow(UtilsDomoWidget.COL_RESS_NAME)));
            domoRessource.setRessourcePath(c.getString(c.getColumnIndexOrThrow(UtilsDomoWidget.COL_RESS_PATH)));

            // Verification de la présence du fichier
            if (domoRessource.getRessourcePath() != null) {
                File file = new File(domoRessource.getRessourcePath());
                if(file.exists()) {
                    listRessource.add(domoRessource);
                } else  {
                    // On efface de la base
                    bdd.delete(UtilsDomoWidget.TABLE_RESS_WIDGET, UtilsDomoWidget.COL_ID + " = " + domoRessource.getId(), null);
                }
            } else {
                listRessource.add(domoRessource);
            }
        }
        // On ferme le cursor
        c.close();
        return listRessource;
    }

}
