package illimiteremi.domowidget.DomoWidgetBdd;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import java.util.ArrayList;

import illimiteremi.domowidget.DomoGeneralSetting.BoxSetting;

import static illimiteremi.domowidget.DomoWidgetBdd.UtilsDomoWidget.COL_ID_BOX;
import static illimiteremi.domowidget.DomoWidgetBdd.UtilsDomoWidget.TABLE_LOCATION_WIDGET;
import static illimiteremi.domowidget.DomoWidgetBdd.UtilsDomoWidget.TABLE_MUTLI_WIDGET;
import static illimiteremi.domowidget.DomoWidgetBdd.UtilsDomoWidget.TABLE_PUSH_WIDGET;
import static illimiteremi.domowidget.DomoWidgetBdd.UtilsDomoWidget.TABLE_STATE_WIDGET;
import static illimiteremi.domowidget.DomoWidgetBdd.UtilsDomoWidget.TABLE_TOOGLE_WIDGET;
import static illimiteremi.domowidget.DomoWidgetBdd.UtilsDomoWidget.TABLE_VOCAL_WIDGET;
import static illimiteremi.domowidget.DomoWidgetBdd.UtilsDomoWidget.TABLE_WEBCAM_WIDGET;

/**
 * Created by rcouturi on 02/07/2016.
 */
public class DomoBoxBDD {

    private static final String TAG      = "[DOMO_GLOBAL_BOX_BDD]";

    private SQLiteDatabase       bdd;
    private final DomoBaseSQLite domoBaseSQLite;

    public DomoBoxBDD(Context context){
        // On créer la BDD et sa table
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
     * Enregistrement d'une box en BDD
     * @param box
     * @return
     */
    public long insertBox(BoxSetting box){
        // Création d'un ContentValues
        ContentValues values = new ContentValues();
        values.put(UtilsDomoWidget.COL_NAME, box.getBoxName());
        values.put(UtilsDomoWidget.COL_BOX_KEY, box.getBoxKey());
        values.put(UtilsDomoWidget.COL_URL_EXTERNE, box.getBoxUrlExterne());
        values.put(UtilsDomoWidget.COL_URL_INTERNE, box.getBoxUrlInterne());
        values.put(UtilsDomoWidget.COL_TIME_OUT, box.getBoxTimeOut());
        values.put(UtilsDomoWidget.COL_COLOR, box.getWidgetNameColor());
        values.put(UtilsDomoWidget.COL_TEXT_SIZE, box.getWidgetTextSize());
        values.put(UtilsDomoWidget.COL_REFRESH_TIME, box.getWidgetRefreshTime());

        // On insère l'objet dans la BDD via le ContentValues
        try {
            return bdd.insert(UtilsDomoWidget.TABLE_GLOBAL_SETTING, null, values);
        } catch (SQLiteException e) {
            Log.e(TAG, "Erreur : " + e);
            return 0;
        }
    }

    /**
     * Mise à jour de la box dans la BDD
     * @param box
     * @return
     */
    public int updateBox(BoxSetting box){
        ContentValues values = new ContentValues();
        values.put(UtilsDomoWidget.COL_NAME, box.getBoxName());
        values.put(UtilsDomoWidget.COL_BOX_KEY, box.getBoxKey());
        values.put(UtilsDomoWidget.COL_URL_EXTERNE, box.getBoxUrlExterne());
        values.put(UtilsDomoWidget.COL_URL_INTERNE, box.getBoxUrlInterne());
        values.put(UtilsDomoWidget.COL_TIME_OUT, box.getBoxTimeOut());
        values.put(UtilsDomoWidget.COL_COLOR, box.getWidgetNameColor());
        values.put(UtilsDomoWidget.COL_TEXT_SIZE, box.getWidgetTextSize());
        values.put(UtilsDomoWidget.COL_REFRESH_TIME, box.getWidgetRefreshTime());

        try {
            return bdd.update(UtilsDomoWidget.TABLE_GLOBAL_SETTING, values, COL_ID_BOX + " = " + box.getBoxId(), null);
        } catch (SQLiteException e) {
            Log.e(TAG, "Erreur : " + e);
            return -1;
        }
    }

    /**
     * Suppression d'une box en BDD
     * @param idBox
     * @return
     */
    public int removeBox(int idBox){
        //Suppression d'un Widget de la BDD grâce à l'ID_WIDGET
        try {
            return bdd.delete(UtilsDomoWidget.TABLE_GLOBAL_SETTING, COL_ID_BOX + " = " + idBox, null);
        } catch (SQLiteException e) {
            Log.e(TAG, "Erreur : " + e);
            return 0;
        }
    }

    /**
     * Recherche d'une box en BDD via son id
     * @param idBox
     * @return GlobalSetting
     */
    public BoxSetting getBoxById(int idBox){
        try {
            // Récupère dans un Cursor
            Cursor c = bdd.query(UtilsDomoWidget.TABLE_GLOBAL_SETTING, new String[] {
                    COL_ID_BOX,
                    UtilsDomoWidget.COL_NAME,
                    UtilsDomoWidget.COL_BOX_KEY,
                    UtilsDomoWidget.COL_URL_EXTERNE,
                    UtilsDomoWidget.COL_URL_INTERNE,
                    UtilsDomoWidget.COL_TIME_OUT,
                    UtilsDomoWidget.COL_COLOR,
                    UtilsDomoWidget.COL_TEXT_SIZE,
                    UtilsDomoWidget.COL_REFRESH_TIME}, COL_ID_BOX + " LIKE \"" + idBox +"\"" , null, null, null, null);
            // Log.d(TAG, "Récuperation Box <" + idBox + "> dans la BDD");
            // Si aucun élément n'a été retourné dans la requête, on renvoie null
            if (c.getCount() == 0) {
                Log.e(TAG, "Erreur : Box non trouvé en BDD !");
                return null;
            }
            c.moveToFirst();
            BoxSetting box = cursorToObjet(c);
            c.close();
            return box;
        } catch (Exception e) {
            Log.e(TAG, "Erreur : " + e);
            return null;
        }
    }

    /**
     * Recherche d'une box en BDD via son Nom
     * @param boxName
     * @return GlobalSetting
     */
    public BoxSetting getBoxByName(String boxName){
        try {
            // Récupère dans un Cursor
            Cursor c = bdd.query(UtilsDomoWidget.TABLE_GLOBAL_SETTING, new String[] {
                    COL_ID_BOX,
                    UtilsDomoWidget.COL_NAME,
                    UtilsDomoWidget.COL_BOX_KEY,
                    UtilsDomoWidget.COL_URL_EXTERNE,
                    UtilsDomoWidget.COL_URL_INTERNE,
                    UtilsDomoWidget.COL_TIME_OUT,
                    UtilsDomoWidget.COL_COLOR,
                    UtilsDomoWidget.COL_TEXT_SIZE,
                    UtilsDomoWidget.COL_REFRESH_TIME}, UtilsDomoWidget.COL_NAME + " LIKE \"" + boxName +"\"" , null, null, null, null);
            // Log.d(TAG, "Récuperation Box <" + idBox + "> dans la BDD");
            // Si aucun élément n'a été retourné dans la requête, on renvoie null
            if (c.getCount() == 0) {
                // Log.d(TAG, "Box non trouvé !");
                return null;
            }
            c.moveToFirst();
            BoxSetting box = cursorToObjet(c);
            c.close();
            return box;
        } catch (Exception e) {
            Log.e(TAG, "Erreur : " + e);
            return null;
        }
    }

    /**
     * Recherche d'une box en BDD via son Nom
     * @param boxKey
     * @return GlobalSetting
     */
    public BoxSetting getBoxByKey(String boxKey){
        try {
            // Récupère dans un Cursor
            Cursor c = bdd.query(UtilsDomoWidget.TABLE_GLOBAL_SETTING, new String[] {
                    COL_ID_BOX,
                    UtilsDomoWidget.COL_NAME,
                    UtilsDomoWidget.COL_BOX_KEY,
                    UtilsDomoWidget.COL_URL_EXTERNE,
                    UtilsDomoWidget.COL_URL_INTERNE,
                    UtilsDomoWidget.COL_TIME_OUT,
                    UtilsDomoWidget.COL_COLOR,
                    UtilsDomoWidget.COL_TEXT_SIZE,
                    UtilsDomoWidget.COL_REFRESH_TIME}, UtilsDomoWidget.COL_BOX_KEY + " LIKE \"" + boxKey +"\"" , null, null, null, null);
            // Log.d(TAG, "Récuperation Box <" + idBox + "> dans la BDD");
            // Si aucun élément n'a été retourné dans la requête, on renvoie null
            if (c.getCount() == 0) {
                // Log.d(TAG, "Box non trouvé !");
                return null;
            }
            c.moveToFirst();
            BoxSetting box = cursorToObjet(c);
            c.close();
            return box;
        } catch (Exception e) {
            Log.e(TAG, "Erreur : " + e);
            return null;
        }
    }

    /**
     * Récuperation des box
     * @return ArrayList<GlobalSetting>
     */
    public ArrayList<BoxSetting> getAllBox(){
        try {
            // Récupère dans un Cursor
            ArrayList<BoxSetting> listBox = new ArrayList<>();

            Cursor c = bdd.query(UtilsDomoWidget.TABLE_GLOBAL_SETTING, new String[] {
                    COL_ID_BOX,
                    UtilsDomoWidget.COL_NAME,
                    UtilsDomoWidget.COL_BOX_KEY,
                    UtilsDomoWidget.COL_URL_EXTERNE,
                    UtilsDomoWidget.COL_URL_INTERNE,
                    UtilsDomoWidget.COL_TIME_OUT,
                    UtilsDomoWidget.COL_COLOR,
                    UtilsDomoWidget.COL_TEXT_SIZE,
                    UtilsDomoWidget.COL_REFRESH_TIME},null , null, null, null, null);
            // Log.d(TAG, "Récuperation de la totalité des Box");

            // Si aucun élément n'a été retourné dans la requête, on renvoie null
            if (c.getCount() == 0) {
                // Log.d(TAG, "Box non trouvé !");
                return null;
            }

            while (c.moveToNext()) {
                listBox.add(cursorToObjet(c));
            }
            // On ferme le cursor
            c.close();
            return listBox;
        } catch (Exception e) {
            Log.e(TAG, "Erreur : " + e);
            return null;
        }
    }

    /**
     * Recherche si une Box est utilsé par un widget
     * @param idBox
     * @return GlobalSetting
     */
    public boolean isUse(int idBox){
        // Récupère dans un Cursor
        Cursor c = bdd.rawQuery("SELECT " + COL_ID_BOX + " FROM "  + TABLE_TOOGLE_WIDGET + " WHERE " +  COL_ID_BOX + " = " + idBox
                + " UNION "
                + "SELECT " + COL_ID_BOX + " FROM " + TABLE_LOCATION_WIDGET + " WHERE " +  COL_ID_BOX + " = " + idBox
                + " UNION "
                + "SELECT " + COL_ID_BOX + " FROM " + TABLE_PUSH_WIDGET + " WHERE " +  COL_ID_BOX + " = " + idBox
                + " UNION "
                + "SELECT " + COL_ID_BOX + " FROM " + TABLE_STATE_WIDGET + " WHERE " +  COL_ID_BOX + " = " + idBox
                + " UNION "
                + "SELECT " + COL_ID_BOX + " FROM " + TABLE_MUTLI_WIDGET + " WHERE " +  COL_ID_BOX + " = " + idBox
                + " UNION "
                + "SELECT " + COL_ID_BOX + " FROM " + TABLE_VOCAL_WIDGET + " WHERE " +  COL_ID_BOX + " = " + idBox
                + " UNION "
                + "SELECT " + COL_ID_BOX + " FROM " + TABLE_WEBCAM_WIDGET + " WHERE " +  COL_ID_BOX + " = " + idBox, null);
        // Si aucun élément n'a été retourné dans la requête, on false
        if (c.getCount() == 0) {
            return false;
        }
        return true;
    }

    /**
     * Lecgture du Cursor pour transformation Objet
     * @param c
     * @return
     */
    private BoxSetting cursorToObjet(Cursor c) {
        // On lui affecte toutes les infos grâce aux infos contenues dans le Cursor
        BoxSetting box = new BoxSetting();
        box.setBoxId(c.getInt(c.getColumnIndexOrThrow(COL_ID_BOX)));
        box.setBoxName(c.getString(c.getColumnIndexOrThrow(UtilsDomoWidget.COL_NAME)));
        box.setBoxKey(c.getString(c.getColumnIndexOrThrow(UtilsDomoWidget.COL_BOX_KEY)));
        box.setBoxUrlExterne(c.getString(c.getColumnIndexOrThrow(UtilsDomoWidget.COL_URL_EXTERNE)));
        box.setBoxUrlInterne(c.getString(c.getColumnIndexOrThrow(UtilsDomoWidget.COL_URL_INTERNE)));
        box.setBoxTimeOut(c.getInt(c.getColumnIndexOrThrow(UtilsDomoWidget.COL_TIME_OUT)));
        box.setWidgetNameColor(c.getString(c.getColumnIndexOrThrow(UtilsDomoWidget.COL_COLOR)));
        box.setWidgetTextSize(c.getInt(c.getColumnIndexOrThrow(UtilsDomoWidget.COL_TEXT_SIZE)));
        box.setWidgetRefreshTime(c.getInt(c.getColumnIndexOrThrow(UtilsDomoWidget.COL_REFRESH_TIME)));
        return box;
    }
}

