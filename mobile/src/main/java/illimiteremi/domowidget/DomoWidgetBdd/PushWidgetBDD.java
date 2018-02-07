package illimiteremi.domowidget.DomoWidgetBdd;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.util.ArrayList;

import illimiteremi.domowidget.DomoWidgetPush.PushWidget;

/**
 * Created by rcouturi on 02/07/2016.
 */
public class PushWidgetBDD {

    private static final String TAG      = "[DOMO_PUSH_BDD]";

    private SQLiteDatabase       bdd;
    private final DomoBaseSQLite domoBaseSQLite;
    private final Context        context;

    public PushWidgetBDD(Context context){
        // On créer la BDD et sa table
        this.context = context;
        domoBaseSQLite = new DomoBaseSQLite(context, UtilsDomoWidget.NOM_BDD, null, UtilsDomoWidget.VERSION_BDD);
    }

    public void open(){
        // On ouvre la BDD en écriture
        //Log.d(TAG, "Ouverture de la BDD : " + UtilsDomoWidget.NOM_BDD);
        bdd = domoBaseSQLite.getWritableDatabase();
    }

    public void close(){
        // On ferme l'accès à la BDD
        //Log.d(TAG, "Fermeture de la BDD : " + UtilsDomoWidget.NOM_BDD);
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
    public long insertWidget(PushWidget widget){
        // Création d'un ContentValues
        ContentValues values = new ContentValues();
        values.put(UtilsDomoWidget.COL_NAME, widget.getDomoName());
        values.put(UtilsDomoWidget.COL_ID_WIDGET, widget.getDomoId());
        values.put(UtilsDomoWidget.COL_ID_BOX, widget.getDomoBox());
        values.put(UtilsDomoWidget.COL_ACTION, widget.getDomoAction());
        values.put(UtilsDomoWidget.COL_ID_IMAGE_ON, widget.getDomoIdImageOn());
        values.put(UtilsDomoWidget.COL_ID_IMAGE_OFF, widget.getDomoIdImageOff());
        values.put(UtilsDomoWidget.COL_LOCK, widget.getDomoLock());
        // On insère l'objet dans la BDD via le ContentValues
        try {
            return bdd.insert(UtilsDomoWidget.TABLE_PUSH_WIDGET, null, values);
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
    public int updateWidget(PushWidget widget){
        ContentValues values = new ContentValues();
        values.put(UtilsDomoWidget.COL_ID_WIDGET, widget.getDomoId());
        values.put(UtilsDomoWidget.COL_NAME, widget.getDomoName());
        values.put(UtilsDomoWidget.COL_ID_BOX, widget.getDomoBox());
        values.put(UtilsDomoWidget.COL_ACTION, widget.getDomoAction());
        values.put(UtilsDomoWidget.COL_ID_IMAGE_ON, widget.getDomoIdImageOn());
        values.put(UtilsDomoWidget.COL_ID_IMAGE_OFF, widget.getDomoIdImageOff());
        values.put(UtilsDomoWidget.COL_LOCK, widget.getDomoLock());
        try {
            return bdd.update(UtilsDomoWidget.TABLE_PUSH_WIDGET, values, UtilsDomoWidget.COL_ID_WIDGET + " = " + widget.getDomoId(), null);
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
            return bdd.delete(UtilsDomoWidget.TABLE_PUSH_WIDGET, UtilsDomoWidget.COL_ID_WIDGET + " = " + idWidget, null);
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
    public PushWidget getWidgetById(int idWidget){
        // Récupère dans un Cursor
        try {
            Cursor c = bdd.query(UtilsDomoWidget.TABLE_PUSH_WIDGET, new String[] {
                    UtilsDomoWidget.COL_ID,
                    UtilsDomoWidget.COL_ID_WIDGET,
                    UtilsDomoWidget.COL_NAME,
                    UtilsDomoWidget.COL_ID_BOX,
                    UtilsDomoWidget.COL_URL,
                    UtilsDomoWidget.COL_KEY,
                    UtilsDomoWidget.COL_ACTION,
                    UtilsDomoWidget.COL_ID_IMAGE_ON,
                    UtilsDomoWidget.COL_ID_IMAGE_OFF,
                    UtilsDomoWidget.COL_LOCK}, UtilsDomoWidget.COL_ID_WIDGET + " LIKE \"" + idWidget +"\"" , null, null, null, null);
            // Si aucun élément n'a été retourné dans la requête, on renvoie null
            if (c.getCount() == 0) {
                Log.e(TAG, "Erreur : Widget non trouvé en BDD !");
                return null;
            }
            // Sinon on se place sur le premier élément
            c.moveToFirst();
            PushWidget widget = cursorToObjet(c);
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
    public ArrayList<PushWidget> getAllWidgets(){
        try {
            // Récupère dans un Cursor
            ArrayList<PushWidget> listWidget = new ArrayList<>();

            Cursor c = bdd.query(UtilsDomoWidget.TABLE_PUSH_WIDGET, new String[] {
                    UtilsDomoWidget.COL_ID,
                    UtilsDomoWidget.COL_ID_WIDGET,
                    UtilsDomoWidget.COL_NAME,
                    UtilsDomoWidget.COL_ID_BOX,
                    UtilsDomoWidget.COL_URL,
                    UtilsDomoWidget.COL_KEY,
                    UtilsDomoWidget.COL_ACTION,
                    UtilsDomoWidget.COL_ID_IMAGE_ON,
                    UtilsDomoWidget.COL_ID_IMAGE_OFF,
                    UtilsDomoWidget.COL_LOCK},null , null, null, null, null);
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
     * Récuperation de la ressource image
     * @param pushWidget
     * @param isON
     * @return
     */
    public Bitmap getRessource(PushWidget pushWidget, boolean isON) {
        Cursor c;
        String ressource;

        if (isON) {
            ressource = "arcade_red_push";                               // Valeur par default Push
            c = bdd.query(UtilsDomoWidget.TABLE_RESS_WIDGET, new String[] {
                    UtilsDomoWidget.COL_ID,
                    UtilsDomoWidget.COL_RESS_NAME,
                    UtilsDomoWidget.COL_RESS_PATH}, UtilsDomoWidget.COL_ID + " = " + pushWidget.getDomoIdImageOn(), null, null, null, null);
        } else {
            ressource = "arcade_red_release";                          // Valeur par default Realse
            c = bdd.query(UtilsDomoWidget.TABLE_RESS_WIDGET, new String[] {
                    UtilsDomoWidget.COL_ID,
                    UtilsDomoWidget.COL_RESS_NAME,
                    UtilsDomoWidget.COL_RESS_PATH}, UtilsDomoWidget.COL_ID + " = " + pushWidget.getDomoIdImageOff(), null, null, null, null);
        }

        // Traitement si pas de ressource
        Bitmap bitmap;
        if (c.getCount() != 0) {
            c.moveToFirst();
            if (c.getString(c.getColumnIndexOrThrow(UtilsDomoWidget.COL_RESS_PATH)) == null) {
                // Image externe
                int ressourceId = context.getResources().getIdentifier(c.getString(c.getColumnIndexOrThrow(UtilsDomoWidget.COL_RESS_NAME)), "drawable",  context.getPackageName());
                bitmap = BitmapFactory.decodeResource(context.getResources(), ressourceId);
            } else {
                // Image interne
                bitmap = BitmapFactory.decodeFile(c.getString(c.getColumnIndexOrThrow(UtilsDomoWidget.COL_RESS_PATH)));
            }
            c.close();
            return Bitmap.createScaledBitmap(bitmap, 96, 96, true);
        } else {
            // Valeur par default
            int ressourceId = context.getResources().getIdentifier(ressource, "drawable",  context.getPackageName());
            bitmap = BitmapFactory.decodeResource(context.getResources(), ressourceId);
            return Bitmap.createScaledBitmap(bitmap, 96, 96, true);
        }
    }

    /**
     * Lecgture du Cursor pour transformation Objet
     * @param c
     * @return
     */
    private PushWidget cursorToObjet(Cursor c) {
        // On lui affecte toutes les infos grâce aux infos contenues dans le Cursor
        PushWidget widget = new PushWidget(context, c.getInt(c.getColumnIndexOrThrow(UtilsDomoWidget.COL_ID_WIDGET)));
        widget.setId(c.getInt(c.getColumnIndexOrThrow(UtilsDomoWidget.COL_ID)));
        widget.setDomoName(c.getString(c.getColumnIndexOrThrow(UtilsDomoWidget.COL_NAME)));
        widget.setDomoBox(c.getInt(c.getColumnIndexOrThrow(UtilsDomoWidget.COL_ID_BOX)));
        widget.setDomoUrl(c.getString(c.getColumnIndexOrThrow(UtilsDomoWidget.COL_URL)));
        widget.setDomoKey(c.getString(c.getColumnIndexOrThrow(UtilsDomoWidget.COL_KEY)));
        widget.setDomoAction(c.getString(c.getColumnIndexOrThrow(UtilsDomoWidget.COL_ACTION)));
        widget.setDomoIdImageOn(c.getInt(c.getColumnIndexOrThrow(UtilsDomoWidget.COL_ID_IMAGE_ON)));
        widget.setDomoIdImageOff(c.getInt(c.getColumnIndexOrThrow(UtilsDomoWidget.COL_ID_IMAGE_OFF)));
        widget.setDomoLock(c.getInt(c.getColumnIndexOrThrow(UtilsDomoWidget.COL_LOCK)));
        return widget;
    }

}

