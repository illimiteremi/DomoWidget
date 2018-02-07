package illimiteremi.domowidget.DomoWidgetBdd;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import java.util.ArrayList;

import illimiteremi.domowidget.DomoWear.WearSetting;

import static illimiteremi.domowidget.DomoWidgetBdd.UtilsDomoWidget.COL_ID;
import static illimiteremi.domowidget.DomoWidgetBdd.UtilsDomoWidget.COL_ID_BOX;
import static illimiteremi.domowidget.DomoWidgetBdd.UtilsDomoWidget.COL_SHAKE_LEVEL;
import static illimiteremi.domowidget.DomoWidgetBdd.UtilsDomoWidget.COL_SHAKE_TIME_OUT;
import static illimiteremi.domowidget.DomoWidgetBdd.UtilsDomoWidget.COL_TIME_OUT;

/**
 * Created by rcouturi on 02/07/2016.
 */
public class DomoWearBDD {

    private static final String TAG      = "[DOMO_WEAR_BDD]";

    private SQLiteDatabase       bdd;
    private final DomoBaseSQLite domoBaseSQLite;

    public DomoWearBDD(Context context){
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
     * Enregistrement de la configuration wear en BDD
     * @param wear
     * @return
     */
    public long insertWear(WearSetting wear){
        // Création d'un ContentValues
        ContentValues values = new ContentValues();
        values.put(UtilsDomoWidget.COL_ID_BOX, wear.getBoxId());
        values.put(COL_TIME_OUT, wear.getWearTimeOutTimeOut());
        values.put(UtilsDomoWidget.COL_SHAKE_TIME_OUT, wear.getShakeTimeOut());
        values.put(UtilsDomoWidget.COL_SHAKE_LEVEL, wear.getShakeLevel());
        // On insère l'objet dans la BDD via le ContentValues
        try {
            return bdd.insert(UtilsDomoWidget.TABLE_DOMO_WEAR, null, values);
        } catch (SQLiteException e) {
            Log.e(TAG, "Erreur : " + e);
            return 0;
        }
    }

    /**
     * Mise à jour de la configuration wear dans la BDD
     * @param wear
     * @return
     */
    public int updateWear(WearSetting wear){
        ContentValues values = new ContentValues();
        values.put(UtilsDomoWidget.COL_ID_BOX, wear.getBoxId());
        values.put(COL_TIME_OUT, wear.getWearTimeOutTimeOut());
        values.put(UtilsDomoWidget.COL_SHAKE_TIME_OUT, wear.getShakeTimeOut());
        values.put(UtilsDomoWidget.COL_SHAKE_LEVEL, wear.getShakeLevel());
        try {
            return bdd.update(UtilsDomoWidget.TABLE_DOMO_WEAR, values, COL_ID + " = " + wear.getId(), null);
        } catch (SQLiteException e) {
            Log.e(TAG, "Erreur : " + e);
            return -1;
        }
    }

    /**
     * Suppression de la configuration wear en BDD
     * @param idWear
     * @return
     */
    public int removeWear(int idWear){
        //Suppression d'un Widget de la BDD grâce à l'ID_WIDGET
        try {
            return bdd.delete(UtilsDomoWidget.TABLE_DOMO_WEAR, COL_ID + " = " + idWear, null);
        } catch (SQLiteException e) {
            Log.e(TAG, "Erreur : " + e);
            return 0;
        }
    }

    /**
     * Récuperation des configurations wear
     * @return ArrayList<DomoWear>
     */
    public ArrayList<WearSetting> getWearSeeting(){
        try {
            // Récupère dans un Cursor
            ArrayList<WearSetting> listWear = new ArrayList<>();

            Cursor c = bdd.query(UtilsDomoWidget.TABLE_DOMO_WEAR, new String[] {
                    COL_ID,
                    COL_ID_BOX,
                    COL_TIME_OUT,
                    COL_SHAKE_TIME_OUT,
                    COL_SHAKE_LEVEL,},null , null, null, null, null);
            // Log.d(TAG, "Récuperation de la totalité des Box");

            // Si aucun élément n'a été retourné dans la requête, on renvoie null
            if (c.getCount() == 0) {
                // Log.d(TAG, "Box non trouvé !");
                return null;
            }

            while (c.moveToNext()) {
                listWear.add(cursorToObjet(c));
            }
            // On ferme le cursor
            c.close();
            return listWear;
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
    private WearSetting cursorToObjet(Cursor c) {
        // On lui affecte toutes les infos grâce aux infos contenues dans le Cursor
        WearSetting wear = new WearSetting();
        wear.setId(c.getInt(c.getColumnIndexOrThrow(UtilsDomoWidget.COL_ID)));
        wear.setBoxId(c.getInt(c.getColumnIndexOrThrow(COL_ID_BOX)));
        wear.setWearTimeOutTimeOut(c.getInt(c.getColumnIndexOrThrow(COL_TIME_OUT)));
        wear.setShakeTimeOut(c.getInt(c.getColumnIndexOrThrow(COL_SHAKE_TIME_OUT)));
        wear.setShakeLevel(c.getInt(c.getColumnIndexOrThrow(COL_SHAKE_LEVEL)));
        return wear;
    }
}

