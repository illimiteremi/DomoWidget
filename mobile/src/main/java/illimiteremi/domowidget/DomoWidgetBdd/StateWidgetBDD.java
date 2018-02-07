package illimiteremi.domowidget.DomoWidgetBdd;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import java.util.ArrayList;

import illimiteremi.domowidget.DomoWidgetState.StateWidget;

/**
 * Created by rcouturi on 02/07/2016.
 */
public class StateWidgetBDD {

    private static final String TAG      = "[DOMO_STATE_BDD]";

    private SQLiteDatabase       bdd;
    private final DomoBaseSQLite domoBaseSQLite;
    private final Context        context;

    public StateWidgetBDD(Context context){
        // On créer la BDD et sa table
        this.context = context;
        domoBaseSQLite = new DomoBaseSQLite(context, UtilsDomoWidget.NOM_BDD, null, UtilsDomoWidget.VERSION_BDD);
    }

    public void open(){
        // On ouvre la BDD en écriture
        bdd = domoBaseSQLite.getWritableDatabase();
    }

    public void close(){
        // On ferme l'accès à la BDD
        bdd.close();
    }

    public SQLiteDatabase getBDD(){
        return bdd;
    }

    /**
     * Enregistrement d'un widget en BDD
     * @param widget
     * @return
     */
    public long insertWidget(StateWidget widget){
        // Création d'un ContentValues
        ContentValues values = new ContentValues();
        values.put(UtilsDomoWidget.COL_NAME, widget.getDomoName());
        values.put(UtilsDomoWidget.COL_ID_WIDGET, widget.getDomoId());
        values.put(UtilsDomoWidget.COL_ID_BOX, widget.getDomoBox());
        values.put(UtilsDomoWidget.COL_ETAT, widget.getDomoState());
        values.put(UtilsDomoWidget.COL_UNIT, widget.getDomoUnit());
        values.put(UtilsDomoWidget.COL_COLOR, widget.getDomoColor());
        values.put(UtilsDomoWidget.COL_MANUEL_UPDATE, widget.getManuelUpdate());
        // On insère l'objet dans la BDD via le ContentValues
        try {
            return bdd.insert(UtilsDomoWidget.TABLE_STATE_WIDGET, null, values);
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
    public int updateWidget(StateWidget widget){
        ContentValues values = new ContentValues();
        values.put(UtilsDomoWidget.COL_ID_WIDGET, widget.getDomoId());
        values.put(UtilsDomoWidget.COL_NAME, widget.getDomoName());
        values.put(UtilsDomoWidget.COL_ID_BOX, widget.getDomoBox());
        values.put(UtilsDomoWidget.COL_ETAT, widget.getDomoState());
        values.put(UtilsDomoWidget.COL_UNIT, widget.getDomoUnit());
        values.put(UtilsDomoWidget.COL_COLOR, widget.getDomoColor());
        values.put(UtilsDomoWidget.COL_MANUEL_UPDATE, widget.getManuelUpdate());
        try {
            return bdd.update(UtilsDomoWidget.TABLE_STATE_WIDGET, values, UtilsDomoWidget.COL_ID_WIDGET + " = " + widget.getDomoId(), null);
        } catch (SQLiteException e) {
            Log.e(TAG, "Erreur : " + e);
            return -1;
        }

    }

    /**
     * Mise à jour de la derniere valeur connu du widget dans la BDD
     * @param widget
     * @return
     */
    public int updateLastValue(StateWidget widget){
        ContentValues values = new ContentValues();
        values.put(UtilsDomoWidget.COL_ID_WIDGET, widget.getDomoId());
        values.put(UtilsDomoWidget.COL_LAST_VALUE, widget.getDomoLastValue());
        try {
            return bdd.update(UtilsDomoWidget.TABLE_STATE_WIDGET, values, UtilsDomoWidget.COL_ID_WIDGET + " = " + widget.getDomoId(), null);
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
            return bdd.delete(UtilsDomoWidget.TABLE_STATE_WIDGET, UtilsDomoWidget.COL_ID_WIDGET + " = " + idWidget, null);
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
    public StateWidget getWidgetById(int idWidget){
        try {
            // Récupère dans un Cursor
            Cursor c = bdd.query(UtilsDomoWidget.TABLE_STATE_WIDGET, new String[] {
                    UtilsDomoWidget.COL_ID,
                    UtilsDomoWidget.COL_ID_WIDGET,
                    UtilsDomoWidget.COL_NAME,
                    UtilsDomoWidget.COL_ID_BOX,
                    UtilsDomoWidget.COL_URL,
                    UtilsDomoWidget.COL_KEY,
                    UtilsDomoWidget.COL_ETAT,
                    UtilsDomoWidget.COL_UNIT,
                    UtilsDomoWidget.COL_COLOR,
                    UtilsDomoWidget.COL_MANUEL_UPDATE,
                    UtilsDomoWidget.COL_LAST_VALUE}, UtilsDomoWidget.COL_ID_WIDGET + " LIKE \"" + idWidget +"\"" , null, null, null, null);
            // Log.d(TAG, "Récuperation Widget <" + idWidget + "> dans la BDD");
            if (c.getCount() == 0) {
                Log.e(TAG, "Erreur : Widget non trouvé en BDD !");
                return null;
            }
            // Sinon on se place sur le premier élément
            c.moveToFirst();
            StateWidget widget = cursorToObjet(c);
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
    public ArrayList<StateWidget> getAllWidgets(){
        try {
            // Récupère dans un Cursor
            ArrayList<StateWidget> listWidget = new ArrayList<>();

            Cursor c = bdd.query(UtilsDomoWidget.TABLE_STATE_WIDGET, new String[] {
                    UtilsDomoWidget.COL_ID,
                    UtilsDomoWidget.COL_ID_WIDGET,
                    UtilsDomoWidget.COL_NAME,
                    UtilsDomoWidget.COL_ID_BOX,
                    UtilsDomoWidget.COL_URL,
                    UtilsDomoWidget.COL_KEY,
                    UtilsDomoWidget.COL_ETAT,
                    UtilsDomoWidget.COL_UNIT,
                    UtilsDomoWidget.COL_COLOR,
                    UtilsDomoWidget.COL_MANUEL_UPDATE,
                    UtilsDomoWidget.COL_LAST_VALUE},null , null, null, null, null);
            // Log.d(TAG, "Récuperation des Widgets");

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
    private StateWidget cursorToObjet(Cursor c) {
        // On lui affecte toutes les infos grâce aux infos contenues dans le Cursor
        StateWidget widget = new StateWidget(context, c.getInt(c.getColumnIndexOrThrow(UtilsDomoWidget.COL_ID_WIDGET)));
        widget.setId(c.getInt(c.getColumnIndexOrThrow(UtilsDomoWidget.COL_ID)));
        widget.setDomoName(c.getString(c.getColumnIndexOrThrow(UtilsDomoWidget.COL_NAME)));
        widget.setDomoBox(c.getInt(c.getColumnIndexOrThrow(UtilsDomoWidget.COL_ID_BOX)));
        widget.setDomoUrl(c.getString(c.getColumnIndexOrThrow(UtilsDomoWidget.COL_URL)));
        widget.setDomoKey(c.getString(c.getColumnIndexOrThrow(UtilsDomoWidget.COL_KEY)));
        widget.setDomoState(c.getString(c.getColumnIndexOrThrow(UtilsDomoWidget.COL_ETAT)));
        widget.setDomoUnit(c.getString(c.getColumnIndexOrThrow(UtilsDomoWidget.COL_UNIT)));
        widget.setDomoColor(c.getString(c.getColumnIndexOrThrow(UtilsDomoWidget.COL_COLOR)));
        widget.setManuelUpdate(c.getInt(c.getColumnIndexOrThrow(UtilsDomoWidget.COL_MANUEL_UPDATE)));
        widget.setDomoLastValue(c.getString(c.getColumnIndexOrThrow(UtilsDomoWidget.COL_LAST_VALUE)));
        return widget;
    }

}

