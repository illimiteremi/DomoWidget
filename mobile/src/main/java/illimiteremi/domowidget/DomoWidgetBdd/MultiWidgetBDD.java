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

import illimiteremi.domowidget.DomoWidgetMulti.MultiWidget;
import illimiteremi.domowidget.DomoWidgetMulti.MultiWidgetRess;

/**
 * Created by rcouturi on 02/07/2016.
 */
public class MultiWidgetBDD {

    private static final String TAG      = "[DOMO_MULTI_BDD]";

    private SQLiteDatabase       bdd;
    private final DomoBaseSQLite domoBaseSQLite;
    private final Context        context;

    public MultiWidgetBDD(Context context){
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
    public long insertWidget(MultiWidget widget){
        // Création d'un ContentValues
        ContentValues values = new ContentValues();
        values.put(UtilsDomoWidget.COL_NAME, widget.getDomoName());
        values.put(UtilsDomoWidget.COL_ID_WIDGET, widget.getDomoId());
        values.put(UtilsDomoWidget.COL_ID_BOX, widget.getDomoBox());
        values.put(UtilsDomoWidget.COL_URL, widget.getDomoUrl());
        values.put(UtilsDomoWidget.COL_KEY, widget.getDomoKey());
        values.put(UtilsDomoWidget.COL_ETAT, widget.getDomoState());
        values.put(UtilsDomoWidget.COL_ID_IMAGE_ON, widget.getDomoIdImageOn());
        values.put(UtilsDomoWidget.COL_ID_IMAGE_OFF, widget.getDomoIdImageOff());
        values.put(UtilsDomoWidget.COL_TIME_OUT, widget.getDomoTimeOut());
        // On insère l'objet dans la BDD via le ContentValues
        try {
            return bdd.insert(UtilsDomoWidget.TABLE_MUTLI_WIDGET, null, values);
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
    public int updateWidget(MultiWidget widget){
        ContentValues values = new ContentValues();
        values.put(UtilsDomoWidget.COL_ID_WIDGET, widget.getDomoId());
        values.put(UtilsDomoWidget.COL_ID_BOX, widget.getDomoBox());
        values.put(UtilsDomoWidget.COL_NAME, widget.getDomoName());
        values.put(UtilsDomoWidget.COL_ETAT, widget.getDomoState());
        values.put(UtilsDomoWidget.COL_ID_IMAGE_ON, widget.getDomoIdImageOn());
        values.put(UtilsDomoWidget.COL_ID_IMAGE_OFF, widget.getDomoIdImageOff());
        values.put(UtilsDomoWidget.COL_TIME_OUT, widget.getDomoTimeOut());
        try {
            return bdd.update(UtilsDomoWidget.TABLE_MUTLI_WIDGET, values, UtilsDomoWidget.COL_ID_WIDGET + " = " + widget.getDomoId(), null);
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
    public int updateLastValue(MultiWidget widget){
        ContentValues values = new ContentValues();
        values.put(UtilsDomoWidget.COL_ID_WIDGET, widget.getDomoId());
        values.put(UtilsDomoWidget.COL_LAST_VALUE, widget.getDomoLastValue());
        try {
            return bdd.update(UtilsDomoWidget.TABLE_MUTLI_WIDGET, values, UtilsDomoWidget.COL_ID_WIDGET + " = " + widget.getDomoId(), null);
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
        bdd.delete(UtilsDomoWidget.TABLE_MUTLI_RESSOURCE, UtilsDomoWidget.COL_ID_WIDGET + " = " + idWidget, null);
        try {
            return bdd.delete(UtilsDomoWidget.TABLE_MUTLI_WIDGET, UtilsDomoWidget.COL_ID_WIDGET + " = " + idWidget, null);
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
    public MultiWidget getWidgetById(int idWidget){
        try {
            // Récupère dans un Cursor
            Cursor c = bdd.query(UtilsDomoWidget.TABLE_MUTLI_WIDGET, new String[] {
                    UtilsDomoWidget.COL_ID,
                    UtilsDomoWidget.COL_ID_WIDGET,
                    UtilsDomoWidget.COL_NAME,
                    UtilsDomoWidget.COL_ID_BOX,
                    UtilsDomoWidget.COL_URL,
                    UtilsDomoWidget.COL_KEY,
                    UtilsDomoWidget.COL_ETAT,
                    UtilsDomoWidget.COL_ID_IMAGE_ON,
                    UtilsDomoWidget.COL_ID_IMAGE_OFF,
                    UtilsDomoWidget.COL_TIME_OUT,
                    UtilsDomoWidget.COL_LAST_VALUE}, UtilsDomoWidget.COL_ID_WIDGET + " LIKE \"" + idWidget +"\"" , null, null, null, null);
            // Log.d(TAG, "Récuperation Widget <" + idWidget + "> dans la BDD");
            if (c.getCount() == 0) {
                Log.e(TAG, "Erreur : Widget non trouvé en BDD !");
                return null;
            }
            c.moveToFirst();
            MultiWidget widget = cursorToObjet(c);
            c.close();
            // Récuperaton des actions
            widget.setMutliWidgetRess(getAllRessource(widget));
            return  widget;
        } catch (Exception e) {
            Log.e(TAG, "Erreur : " + e);
            return null;
        }
    }

    /**
     * Récuperation des widgets
     * @return
     */
    public ArrayList<MultiWidget> getAllWidgets(){
        try {
            // Récupère dans un Cursor
            ArrayList<MultiWidget> listWidget = new ArrayList<>();

            Cursor c = bdd.query(UtilsDomoWidget.TABLE_MUTLI_WIDGET, new String[] {
                    UtilsDomoWidget.COL_ID,
                    UtilsDomoWidget.COL_ID_WIDGET,
                    UtilsDomoWidget.COL_NAME,
                    UtilsDomoWidget.COL_ID_BOX,
                    UtilsDomoWidget.COL_URL,
                    UtilsDomoWidget.COL_KEY,
                    UtilsDomoWidget.COL_ETAT,
                    UtilsDomoWidget.COL_ID_IMAGE_ON,
                    UtilsDomoWidget.COL_ID_IMAGE_OFF,
                    UtilsDomoWidget.COL_TIME_OUT,
                    UtilsDomoWidget.COL_LAST_VALUE},null , null, null, null, null);
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
     * Récuperation de la ressource image
     * @param multiWidget
     * @param isON
     * @return
     */
    public Bitmap getRessource(MultiWidget multiWidget, boolean isON) {
        Cursor c;
        String ressource;

        if (isON) {
             ressource = "arcade_red_push";                               // Valeur par default Push
             c = bdd.query(UtilsDomoWidget.TABLE_RESS_WIDGET, new String[] {
                        UtilsDomoWidget.COL_ID,
                        UtilsDomoWidget.COL_RESS_NAME,
                        UtilsDomoWidget.COL_RESS_PATH}, UtilsDomoWidget.COL_ID + " = " + multiWidget.getDomoIdImageOn(), null, null, null, null);
        } else {
             ressource = "arcade_red_release";                          // Valeur par default Realse
             c = bdd.query(UtilsDomoWidget.TABLE_RESS_WIDGET, new String[] {
                        UtilsDomoWidget.COL_ID,
                        UtilsDomoWidget.COL_RESS_NAME,
                        UtilsDomoWidget.COL_RESS_PATH}, UtilsDomoWidget.COL_ID + " = " + multiWidget.getDomoIdImageOff(), null, null, null, null);
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
     * Ajout d'une ressource au widget en BDD
     * @param ressource
     * @return
     */
    public long insertRessource(MultiWidgetRess ressource){
        // Création d'un ContentValues
        ContentValues values = new ContentValues();
        values.put(UtilsDomoWidget.COL_NAME, ressource.getDomoName());
        values.put(UtilsDomoWidget.COL_ID_WIDGET, ressource.getDomoId());
        values.put(UtilsDomoWidget.COL_ACTION, ressource.getDomoAction());
        values.put(UtilsDomoWidget.COL_ID_IMAGE_ON, ressource.getDomoIdImageOn());
        values.put(UtilsDomoWidget.COL_ID_IMAGE_OFF, ressource.getDomoIdImageOff());
        // On insère l'objet dans la BDD via le ContentValues
        try {
            return bdd.insert(UtilsDomoWidget.TABLE_MUTLI_RESSOURCE, null, values);
        } catch (SQLiteException e) {
            Log.e(TAG, "Erreur : " + e);
            return 0;
        }
    }

    /**
     * Mise à jour d'une ressource pour le widget dans la BDD
     * @param ressource
     * @return
     */
    public int updateRessource(MultiWidgetRess ressource){
        ContentValues values = new ContentValues();
        values.put(UtilsDomoWidget.COL_ID_WIDGET, ressource.getDomoId());
        values.put(UtilsDomoWidget.COL_NAME, ressource.getDomoName());
        values.put(UtilsDomoWidget.COL_ACTION, ressource.getDomoAction());
        values.put(UtilsDomoWidget.COL_ID_IMAGE_ON, ressource.getDomoIdImageOn());
        values.put(UtilsDomoWidget.COL_ID_IMAGE_OFF, ressource.getDomoIdImageOff());
        try {
            return bdd.update(UtilsDomoWidget.TABLE_MUTLI_RESSOURCE, values, UtilsDomoWidget.COL_ID + " = " + ressource.getId() , null);
        } catch (SQLiteException e) {
            Log.e(TAG, "Erreur : " + e);
            return -1;
        }
    }

    /**
     * Suppression du Widget en BDD
     * @param idRessource
     * @return
     */
    public int removeRessource(int idRessource){
        //Suppression d'un Widget de la BDD grâce à l'ID_WIDGET
        try {
            return bdd.delete(UtilsDomoWidget.TABLE_MUTLI_RESSOURCE, UtilsDomoWidget.COL_ID + " = " + idRessource, null);
        } catch (SQLiteException e) {
            Log.e(TAG, "Erreur : " + e);
            return 0;
        }
    }

    /**
     * Récuperation des ressources d'un widget
     * @param widget
     * @return
     */
    public ArrayList<MultiWidgetRess> getAllRessource(MultiWidget widget){
        try {
            // Récupère dans un Cursor
            Cursor c = bdd.query(UtilsDomoWidget.TABLE_MUTLI_RESSOURCE, new String[] {
                    UtilsDomoWidget.COL_ID,
                    UtilsDomoWidget.COL_ID_WIDGET,
                    UtilsDomoWidget.COL_NAME,
                    UtilsDomoWidget.COL_ACTION,
                    UtilsDomoWidget.COL_ID_IMAGE_ON,
                    UtilsDomoWidget.COL_ID_IMAGE_OFF,
                    UtilsDomoWidget.COL_DEFAULT_MULTI_RESS}, UtilsDomoWidget.COL_ID_WIDGET + " LIKE \"" + widget.getDomoId() +"\"" , null, null, null, null);
            //Log.d(TAG, "Récuperation des ressources <" + widget.getDomoName() + "> dans la BDD");
            if (c.getCount() == 0) {
                Log.d(TAG, "Ressources non trouvé !");
                return null;
            }

            ArrayList<MultiWidgetRess> multiWidgetRess = new ArrayList<>();
            while (c.moveToNext()) {
                multiWidgetRess.add(cursorToRessource(c));
            }
            // On ferme le cursor
            c.close();
            return multiWidgetRess;
        } catch (Exception e) {
            Log.e(TAG, "Erreur : " + e);
            return null;
        }
    }

    /**
     * Récuperation d'une ressource par un id
     * @param id
     * @return
     */
    public MultiWidgetRess getAllRessourceById(int id){
        try {
            // Récupère dans un Cursor
            Cursor c = bdd.query(UtilsDomoWidget.TABLE_MUTLI_RESSOURCE, new String[] {
                    UtilsDomoWidget.COL_ID,
                    UtilsDomoWidget.COL_ID_WIDGET,
                    UtilsDomoWidget.COL_NAME,
                    UtilsDomoWidget.COL_ACTION,
                    UtilsDomoWidget.COL_ID_IMAGE_ON,
                    UtilsDomoWidget.COL_ID_IMAGE_OFF,
                    UtilsDomoWidget.COL_DEFAULT_MULTI_RESS}, UtilsDomoWidget.COL_ID + " LIKE \"" + id +"\"" , null, null, null, null);
            if (c.getCount() == 0) {
                Log.e(TAG, "Erreur : Ressources non trouvé en BDD !");
                return null;
            }

            c.moveToFirst();
            MultiWidgetRess ressource = cursorToRessource(c);
            // On ferme le cursor
            c.close();
            return ressource;
        } catch (Exception e) {
            Log.e(TAG, "Erreur : " + e);
            return null;
        }
    }

    /**
     * Récuperation de la ressource image
     * @param multiWidgetRess
     * @param isON
     * @return
     */
    public Bitmap getMutliWidgetRess(MultiWidgetRess multiWidgetRess, boolean isON) {
        Cursor c;
        String ressource;

        if (isON) {
            ressource = "arcade_red_push";                               // Valeur par default Push
            c = bdd.query(UtilsDomoWidget.TABLE_RESS_WIDGET, new String[] {
                    UtilsDomoWidget.COL_ID,
                    UtilsDomoWidget.COL_RESS_NAME,
                    UtilsDomoWidget.COL_RESS_PATH}, UtilsDomoWidget.COL_ID + " = " + multiWidgetRess.getDomoIdImageOn(), null, null, null, null);
        } else {
            ressource = "arcade_red_release";                          // Valeur par default Realse
            c = bdd.query(UtilsDomoWidget.TABLE_RESS_WIDGET, new String[] {
                    UtilsDomoWidget.COL_ID,
                    UtilsDomoWidget.COL_RESS_NAME,
                    UtilsDomoWidget.COL_RESS_PATH}, UtilsDomoWidget.COL_ID + " = " + multiWidgetRess.getDomoIdImageOff(), null, null, null, null);
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
     * Lecture du Cursor pour transformation Objet
     * @param c
     * @return
     */
    private MultiWidget cursorToObjet(Cursor c) {
        // On lui affecte toutes les infos grâce aux infos contenues dans le Cursor
        MultiWidget widget = new MultiWidget(context, c.getInt(c.getColumnIndexOrThrow(UtilsDomoWidget.COL_ID_WIDGET)));
        widget.setId(c.getInt(c.getColumnIndexOrThrow(UtilsDomoWidget.COL_ID)));
        widget.setDomoName(c.getString(c.getColumnIndexOrThrow(UtilsDomoWidget.COL_NAME)));
        widget.setDomoBox(c.getInt(c.getColumnIndexOrThrow(UtilsDomoWidget.COL_ID_BOX)));
        widget.setDomoUrl(c.getString(c.getColumnIndexOrThrow(UtilsDomoWidget.COL_URL)));
        widget.setDomoKey(c.getString(c.getColumnIndexOrThrow(UtilsDomoWidget.COL_KEY)));
        widget.setDomoState(c.getString(c.getColumnIndexOrThrow(UtilsDomoWidget.COL_ETAT)));
        widget.setDomoIdImageOn(c.getInt(c.getColumnIndexOrThrow(UtilsDomoWidget.COL_ID_IMAGE_ON)));
        widget.setDomoIdImageOff(c.getInt(c.getColumnIndexOrThrow(UtilsDomoWidget.COL_ID_IMAGE_OFF)));
        widget.setDomoTimeOut(c.getInt(c.getColumnIndexOrThrow(UtilsDomoWidget.COL_TIME_OUT)));
        widget.setDomoLastValue(c.getString(c.getColumnIndexOrThrow(UtilsDomoWidget.COL_LAST_VALUE)));
        return widget;
    }

    /**
     * Lecture du Cursor pour transformation Objet
     * @param c
     * @return
     */
    private MultiWidgetRess cursorToRessource(Cursor c) {
        // On lui affecte toutes les infos grâce aux infos contenues dans le Cursor
        MultiWidgetRess ressource = new MultiWidgetRess(c.getInt(c.getColumnIndexOrThrow(UtilsDomoWidget.COL_ID_WIDGET)));
        ressource.setId(c.getInt(c.getColumnIndexOrThrow(UtilsDomoWidget.COL_ID)));
        ressource.setDomoName(c.getString(c.getColumnIndexOrThrow(UtilsDomoWidget.COL_NAME)));
        ressource.setDomoAction(c.getString(c.getColumnIndexOrThrow(UtilsDomoWidget.COL_ACTION)));
        ressource.setDomoIdImageOn(c.getInt(c.getColumnIndexOrThrow(UtilsDomoWidget.COL_ID_IMAGE_ON)));
        ressource.setDomoIdImageOff(c.getInt(c.getColumnIndexOrThrow(UtilsDomoWidget.COL_ID_IMAGE_OFF)));
        ressource.setDomoDefault(c.getInt(c.getColumnIndexOrThrow(UtilsDomoWidget.COL_DEFAULT_MULTI_RESS)));
        return ressource;
    }
}

