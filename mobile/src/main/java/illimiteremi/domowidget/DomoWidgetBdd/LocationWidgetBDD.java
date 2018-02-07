package illimiteremi.domowidget.DomoWidgetBdd;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import java.util.ArrayList;

import illimiteremi.domowidget.DomoWidgetLocation.LocationWidget;

/**
 * Created by rcouturi on 02/07/2016.
 */
public class LocationWidgetBDD {

    private static final String TAG      = "[DOMO_LOCATION_BDD]";

    private final Context        context;

    private final DomoBaseSQLite domoBaseSQLite;
    private SQLiteDatabase       bdd;

    /**
     * LocationWidgetBDD
     * @param context
     */
    public LocationWidgetBDD(Context context){
        // On créer la BDD et sa table
        this.context   = context;
        domoBaseSQLite = new DomoBaseSQLite(context, UtilsDomoWidget.NOM_BDD, null, UtilsDomoWidget.VERSION_BDD);
    }

    /**
     * open
     */
    public void open(){
        // On ouvre la BDD en écriture
        bdd = domoBaseSQLite.getWritableDatabase();
    }

    /**
     * close
     */
    public void close(){
        // On ferme l'accès à la BDD
        bdd.close();
    }

    /**
     * Enregistrement d'un widget en BDD
     * @param widget
     * @return
     */
    public long insertWidget(LocationWidget widget){
        // Création d'un ContentValues
        ContentValues values = new ContentValues();
        values.put(UtilsDomoWidget.COL_ID_WIDGET, widget.getDomoId());
        values.put(UtilsDomoWidget.COL_NAME, widget.getDomoName());
        values.put(UtilsDomoWidget.COL_KEY, widget.getDomoKey());
        values.put(UtilsDomoWidget.COL_URL, widget.getDomoUrl());
        values.put(UtilsDomoWidget.COL_ID_BOX, widget.getDomoBox());
        values.put(UtilsDomoWidget.COL_ACTION, widget.getDomoAction());
        values.put(UtilsDomoWidget.COL_TIME_OUT, widget.getDomoTimeOut());
        values.put(UtilsDomoWidget.COL_DISTANCE, widget.getDomoDistance());
        values.put(UtilsDomoWidget.COL_LOCATION, widget.getDomoLocation());
        values.put(UtilsDomoWidget.COL_PROVIDER, widget.getDomoProvider());
        // On insère l'objet dans la BDD via le ContentValues
        try {
            return bdd.insert(UtilsDomoWidget.TABLE_LOCATION_WIDGET, null, values);
        } catch (SQLiteException e) {
            Log.e(TAG, "Erreur : " + e);
            return 0;
        }
    }

    /**
     * Mise à jour du widget dans la BDD
     * @param widget
     * @return
     */
    public int updateWidget(LocationWidget widget){
        ContentValues values = new ContentValues();
        values.put(UtilsDomoWidget.COL_ID_WIDGET, widget.getDomoId());
        values.put(UtilsDomoWidget.COL_NAME, widget.getDomoName());
        values.put(UtilsDomoWidget.COL_KEY, widget.getDomoKey());
        values.put(UtilsDomoWidget.COL_URL, widget.getDomoUrl());
        values.put(UtilsDomoWidget.COL_ID_BOX, widget.getDomoBox());
        values.put(UtilsDomoWidget.COL_ACTION, widget.getDomoAction());
        values.put(UtilsDomoWidget.COL_TIME_OUT, widget.getDomoTimeOut());
        values.put(UtilsDomoWidget.COL_DISTANCE, widget.getDomoDistance());
        values.put(UtilsDomoWidget.COL_LOCATION, widget.getDomoLocation());
        values.put(UtilsDomoWidget.COL_PROVIDER, widget.getDomoProvider());
        try {
            return bdd.update(UtilsDomoWidget.TABLE_LOCATION_WIDGET, values, UtilsDomoWidget.COL_ID_WIDGET + " = " + widget.getDomoId(), null);
        } catch (SQLiteException e) {
            Log.e(TAG, "Erreur : " + e);
            return -1;
        }
    }

    /**
     * Suppression du Widget en BDD
     * @param idWidget
     * @return
     */
    public int removeWidgetById(int idWidget){
        //Suppression d'un Widget de la BDD grâce à l'ID_WIDGET
        try {
            return bdd.delete(UtilsDomoWidget.TABLE_LOCATION_WIDGET, UtilsDomoWidget.COL_ID_WIDGET + " = " + idWidget, null);
        } catch (SQLiteException e) {
            Log.e(TAG, "Erreur : " + e);
            return 0;
        }
    }

    /**
     * Recherche d'un Widget en BDD
     * @param idWidget
     * @return
     */
    public LocationWidget getWidgetById(int idWidget){
        try {
            // Récupère dans un Cursor
            Cursor c = bdd.query(UtilsDomoWidget.TABLE_LOCATION_WIDGET, new String[] {
                    UtilsDomoWidget.COL_ID,
                    UtilsDomoWidget.COL_ID_WIDGET,
                    UtilsDomoWidget.COL_NAME,
                    UtilsDomoWidget.COL_KEY,
                    UtilsDomoWidget.COL_URL,
                    UtilsDomoWidget.COL_ID_BOX,
                    UtilsDomoWidget.COL_ACTION,
                    UtilsDomoWidget.COL_TIME_OUT,
                    UtilsDomoWidget.COL_DISTANCE,
                    UtilsDomoWidget.COL_LOCATION,
                    UtilsDomoWidget.COL_PROVIDER}, UtilsDomoWidget.COL_ID_WIDGET + " LIKE \"" + idWidget +"\"" , null, null, null, null);
            // Log.d(TAG, "Récuperation Widget <" + idWidget + "> dans la BDD");
            if (c.getCount() == 0) {
                Log.e(TAG, "Erreur : Widget non trouvé en BDD !");
                return null;
            }
            c.moveToFirst();
            LocationWidget widget = cursorToObjet(c);
            c.close();
            return widget;
        } catch (Exception e) {
            Log.e(TAG, "Erreur : " + e);
            return null;
        }
    }

    /**
     * Récuperation des widgets
     * @return
     */
    public ArrayList<LocationWidget> getAllWidgets(){
        try {
            // Récupère dans un Cursor
            ArrayList<LocationWidget> listWidget = new ArrayList<>();

            Cursor c = bdd.query(UtilsDomoWidget.TABLE_LOCATION_WIDGET, new String[] {
                    UtilsDomoWidget.COL_ID,
                    UtilsDomoWidget.COL_ID_WIDGET,
                    UtilsDomoWidget.COL_NAME,
                    UtilsDomoWidget.COL_KEY,
                    UtilsDomoWidget.COL_URL,
                    UtilsDomoWidget.COL_ID_BOX,
                    UtilsDomoWidget.COL_ACTION,
                    UtilsDomoWidget.COL_TIME_OUT,
                    UtilsDomoWidget.COL_DISTANCE,
                    UtilsDomoWidget.COL_LOCATION,
                    UtilsDomoWidget.COL_PROVIDER},null , null, null, null, null);

            // Log.d(TAG, "Récuperation de la totalité des Widgets");

            // Si aucun élément n'a été retourné dans la requête, on renvoie null
            if (c.getCount() == 0) {
                // Log.d(TAG, "Widget non trouvé !");
                return null;
            }

            while (c.moveToNext()) {
                listWidget.add(cursorToObjet(c));
            }
            // On ferme le cursor
            c.close();
            return listWidget;
        } catch (Exception e) {
            Log.e(TAG, "Erreur : " + e);
            return null;
        }
    }

    /**
     * Lecture du Cursor pour transformation Objet
     * @param c
     * @return
     */
    private LocationWidget cursorToObjet(Cursor c) {
        // On lui affecte toutes les infos grâce aux infos contenues dans le Cursor
        LocationWidget widget = new LocationWidget(context, c.getInt(c.getColumnIndexOrThrow(UtilsDomoWidget.COL_ID_WIDGET)));
        widget.setId(c.getInt(c.getColumnIndexOrThrow(UtilsDomoWidget.COL_ID)));
        widget.setDomoName(c.getString(c.getColumnIndexOrThrow(UtilsDomoWidget.COL_NAME)));
        widget.setDomoKey(c.getString(c.getColumnIndexOrThrow(UtilsDomoWidget.COL_KEY)));
        widget.setDomoUrl(c.getString(c.getColumnIndexOrThrow(UtilsDomoWidget.COL_URL)));
        widget.setDomoBox(c.getInt(c.getColumnIndexOrThrow(UtilsDomoWidget.COL_ID_BOX)));
        widget.setDomoAction(c.getString(c.getColumnIndexOrThrow(UtilsDomoWidget.COL_ACTION)));
        widget.setDomoTimeOut(c.getInt(c.getColumnIndexOrThrow(UtilsDomoWidget.COL_TIME_OUT)));
        widget.setDomoDistance(c.getInt(c.getColumnIndexOrThrow(UtilsDomoWidget.COL_DISTANCE)));
        widget.setDomoLocation(c.getString(c.getColumnIndexOrThrow(UtilsDomoWidget.COL_LOCATION)));
        widget.setDomoProvider(c.getString(c.getColumnIndexOrThrow(UtilsDomoWidget.COL_PROVIDER)));
        return widget;
    }
}

