package illimiteremi.domowidget.DomoWidgetBdd;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import java.util.ArrayList;

import illimiteremi.domowidget.DomoJSONRPC.DomoCmd;
import illimiteremi.domowidget.DomoJSONRPC.DomoEquipement;

import static illimiteremi.domowidget.DomoUtils.DomoConstants.REQUEST_CMD;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.REQUEST_OBJET;
import static illimiteremi.domowidget.DomoWidgetBdd.UtilsDomoWidget.TABLE_JEEDOM_CMD;
import static illimiteremi.domowidget.DomoWidgetBdd.UtilsDomoWidget.TABLE_JEEDOM_OBJET;

public class DomoJsonRPC {

    private static final String TAG      = "[DOMO_JSONRPC]";

    private SQLiteDatabase bdd;
    private final DomoBaseSQLite domoBaseSQLite;

    public DomoJsonRPC(Context context){
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
     * Enregistrement d'un objet en BDD
     * @param objet
     * @return
     */
    public long insertObjet(DomoEquipement objet){
        // Création d'un ContentValues
        ContentValues values = new ContentValues();
        values.put(UtilsDomoWidget.COL_NAME, objet.getObjetName());
        values.put(UtilsDomoWidget.COL_ID_OBJET, objet.getIdObjet());

        // On insère l'objet dans la BDD via le ContentValues
        try {
            return bdd.insert(TABLE_JEEDOM_OBJET, null, values);
        } catch (SQLiteException e) {
            Log.e(TAG, "Erreur : " + e);
            return 0;
        }
    }

    /**
     * insertCmd
     * @param cmd
     * @return
     */
    public long insertCmd(DomoCmd cmd){
        // Création d'un ContentValues
        ContentValues values = new ContentValues();
        values.put(UtilsDomoWidget.COL_ID_OBJET, cmd.getIdObjet());
        values.put(UtilsDomoWidget.COL_TYPE, cmd.getType());
        values.put(UtilsDomoWidget.COL_NAME, cmd.getCmdName());
        values.put(UtilsDomoWidget.COL_ID_CMD, cmd.getIdCmd());
        // On insère la commande dans la BDD via le ContentValues
        try {
            return bdd.insert(UtilsDomoWidget.TABLE_JEEDOM_CMD, null, values);
        } catch (SQLiteException e) {
            Log.e(TAG, "Erreur : " + e);
            return 0;
        }
    }

    /**
     * getAllCmd
     * @return
     */
    public ArrayList<DomoEquipement> getAllObjet(){
        try {
            ArrayList<DomoEquipement> listDomoEquipement = new ArrayList<>();

            // Récupère dans un Cursor
            Cursor c = bdd.query(TABLE_JEEDOM_OBJET, new String[] {
                    UtilsDomoWidget.COL_ID,
                    UtilsDomoWidget.COL_ID_OBJET,
                    UtilsDomoWidget.COL_NAME}, null , null, null, null, null);
            if (c.getCount() == 0) {
                Log.e(TAG, "Erreur : Commande non trouvé en BDD !");
                return null;
            }

            while (c.moveToNext()) {
                listDomoEquipement.add(cursorToDomoObjet(c));
            }
            // On ferme le cursor
            c.close();
            return listDomoEquipement;
        } catch (Exception e) {
            Log.e(TAG, "Erreur : " + e);
            return null;
        }
    }

    /**
     * getAllCmd
     * @return
     */
    public ArrayList<DomoCmd> getAllCmd(){
        try {
            ArrayList<DomoCmd> listDomoCmd= new ArrayList<>();

            // Récupère dans un Cursor
            Cursor c = bdd.query(UtilsDomoWidget.TABLE_JEEDOM_CMD, new String[] {
                    UtilsDomoWidget.COL_ID,
                    UtilsDomoWidget.COL_ID_OBJET,
                    UtilsDomoWidget.COL_NAME,
                    UtilsDomoWidget.COL_TYPE,
                    UtilsDomoWidget.COL_ID_CMD}, null , null, null, null, null);
            if (c.getCount() == 0) {
                Log.e(TAG, "Erreur : Commande non trouvé en BDD !");
                return null;
            }

            while (c.moveToNext()) {
                listDomoCmd.add(cursorToDomoCmd(c));
            }
            // On ferme le cursor
            c.close();
            return listDomoCmd;
        } catch (Exception e) {
            Log.e(TAG, "Erreur : " + e);
            return null;
        }
    }

    /**
     * getCmdByObjet
     * @param domoEquipement
     * @return
     */
    public ArrayList<DomoCmd> getCmdByObjet(DomoEquipement domoEquipement, String cmdType) {

        ArrayList<DomoCmd> listDomoCmd= new ArrayList<>();
        // Récupère dans un Cursor
        try {
            Cursor c = bdd.query(UtilsDomoWidget.TABLE_JEEDOM_CMD, new String[] {
                    UtilsDomoWidget.COL_ID,
                    UtilsDomoWidget.COL_ID_OBJET,
                    UtilsDomoWidget.COL_NAME,
                    UtilsDomoWidget.COL_TYPE,
                    UtilsDomoWidget.COL_ID_CMD}, UtilsDomoWidget.COL_TYPE + " LIKE \"" + cmdType + "\" AND " + UtilsDomoWidget.COL_ID_OBJET + " LIKE \"" + domoEquipement.getIdObjet() +"\"" , null, null, null, null);
            // Si aucun élément n'a été retourné dans la requête, on renvoie null
            if (c.getCount() == 0) {
                Log.e(TAG, "Erreur : Widget non trouvé en BDD !");
                return null;
            }

            while (c.moveToNext()) {
                listDomoCmd.add(cursorToDomoCmd(c));
            }
            // On ferme le cursor
            c.close();
            return listDomoCmd;
        } catch (Exception e) {
            Log.e(TAG, "Erreur : " + e);
            return null;
        }
    }

    /**
     * deleteData
     * @param table
     */
    public void deleteData(String table) {
        switch (table) {
            case REQUEST_OBJET:
                bdd.delete(TABLE_JEEDOM_OBJET, null, null);
                break;
            case REQUEST_CMD:
                bdd.delete(TABLE_JEEDOM_CMD, null, null);
                break;
        }
    }

    /**
     * Lecture du Cursor pour transformation Objet
     * @param c
     * @return
     */
    private DomoCmd cursorToDomoCmd(Cursor c) {
        // On lui affecte toutes les infos grâce aux infos contenues dans le Cursor
        DomoCmd cmd = new DomoCmd();
        cmd.setIdObjet(c.getInt(c.getColumnIndexOrThrow(UtilsDomoWidget.COL_ID_OBJET)));
        cmd.setCmdName(c.getString(c.getColumnIndexOrThrow(UtilsDomoWidget.COL_NAME)));
        cmd.setType(c.getString(c.getColumnIndexOrThrow(UtilsDomoWidget.COL_TYPE)));
        cmd.setIdCmd(c.getInt(c.getColumnIndexOrThrow(UtilsDomoWidget.COL_ID_CMD)));
        return cmd;
    }

    /**
     * Lecture du Cursor pour transformation Objet
     * @param c
     * @return
     */
    private DomoEquipement cursorToDomoObjet(Cursor c) {
        // On lui affecte toutes les infos grâce aux infos contenues dans le Cursor
        DomoEquipement objet = new DomoEquipement();
        objet.setIdObjet(c.getInt(c.getColumnIndexOrThrow(UtilsDomoWidget.COL_ID_OBJET)));
        objet.setObjetName(c.getString(c.getColumnIndexOrThrow(UtilsDomoWidget.COL_NAME)));
        return objet;
    }
}
