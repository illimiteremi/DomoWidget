package illimiteremi.domowidget.DomoWidgetBdd;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.util.ArrayList;

import illimiteremi.domowidget.DomoWidgetToogle.ToogleWidget;

/**
 * Created by rcouturi on 02/07/2016.
 */
public class ToogleWidgetBDD {

    private static final String TAG      = "[DOMO_TOOGLE_BDD]";

    private SQLiteDatabase       bdd;
    private final DomoBaseSQLite domoBaseSQLite;
    private final Context        context;

    public ToogleWidgetBDD(Context context){
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
    public long insertWidget(ToogleWidget widget){
        // Création d'un ContentValues
        ContentValues values = new ContentValues();
        values.put(UtilsDomoWidget.COL_NAME, widget.getDomoName());
        values.put(UtilsDomoWidget.COL_ID_WIDGET, widget.getDomoId());
        values.put(UtilsDomoWidget.COL_ID_BOX, widget.getDomoBox());
        values.put(UtilsDomoWidget.COL_ON, widget.getDomoOn());
        values.put(UtilsDomoWidget.COL_OFF, widget.getDomoOff());
        values.put(UtilsDomoWidget.COL_ETAT, widget.getDomoState());
        values.put(UtilsDomoWidget.COL_ID_IMAGE_ON, widget.getDomoIdImageOn());
        values.put(UtilsDomoWidget.COL_ID_IMAGE_OFF, widget.getDomoIdImageOff());
        values.put(UtilsDomoWidget.COL_LOCK, widget.getDomoLock());
        values.put(UtilsDomoWidget.COL_EXP_REG, widget.getDomoExpReg());
        values.put(UtilsDomoWidget.COL_TIME_OUT, widget.getDomoTimeOut());
        // On insère l'objet dans la BDD via le ContentValues
        // Log.d(TAG, "Insertion Widget <" + widget.getDomoName() + "> dans la BDD");
        return bdd.insert(UtilsDomoWidget.TABLE_TOOGLE_WIDGET, null, values);
    }

    /**
     * Mise à jour du widget dans la BDD
     * @param widget
     * @return
     */
    public int updateWidget(ToogleWidget widget){
        ContentValues values = new ContentValues();
        values.put(UtilsDomoWidget.COL_ID_WIDGET, widget.getDomoId());
        values.put(UtilsDomoWidget.COL_NAME, widget.getDomoName());
        values.put(UtilsDomoWidget.COL_ID_BOX, widget.getDomoBox());
        values.put(UtilsDomoWidget.COL_ON, widget.getDomoOn());
        values.put(UtilsDomoWidget.COL_OFF, widget.getDomoOff());
        values.put(UtilsDomoWidget.COL_ETAT, widget.getDomoState());
        values.put(UtilsDomoWidget.COL_ID_IMAGE_ON, widget.getDomoIdImageOn());
        values.put(UtilsDomoWidget.COL_ID_IMAGE_OFF, widget.getDomoIdImageOff());
        values.put(UtilsDomoWidget.COL_LOCK, widget.getDomoLock());
        values.put(UtilsDomoWidget.COL_EXP_REG, widget.getDomoExpReg());
        values.put(UtilsDomoWidget.COL_TIME_OUT, widget.getDomoTimeOut());
        // Log.d(TAG, "Result Update = " + nbUpdate);
        return bdd.update(UtilsDomoWidget.TABLE_TOOGLE_WIDGET, values, UtilsDomoWidget.COL_ID_WIDGET + " = " + widget.getDomoId(), null);
    }

    /**
     * Mise à jour de la derniere valeur connu du widget dans la BDD
     * @param widget
     * @return
     */
    public int updateLastValue(ToogleWidget widget){
        ContentValues values = new ContentValues();
        values.put(UtilsDomoWidget.COL_ID_WIDGET, widget.getDomoId());
        values.put(UtilsDomoWidget.COL_LAST_VALUE, widget.getDomoLastValue());
        // Log.d(TAG, "Result Update = " + nbUpdate);
        return bdd.update(UtilsDomoWidget.TABLE_TOOGLE_WIDGET, values, UtilsDomoWidget.COL_ID_WIDGET + " = " + widget.getDomoId(), null);
    }

    /**
     * Suppression du Widget en BDD
     * @param idWidget
     * @return
     */
    public int removeWidgetById(int idWidget){
        //Suppression d'un Widget de la BDD grâce à l'ID_WIDGET
        // Log.d(TAG, "Suppression Widget " + idWidget + " dans la BDD");
        return bdd.delete(UtilsDomoWidget.TABLE_TOOGLE_WIDGET, UtilsDomoWidget.COL_ID_WIDGET + " = " + idWidget, null);
    }

    /**
     * Recherche d'un Widget en BDD
     * @param idWidget
     * @return
     */
    public ToogleWidget getWidgetById(int idWidget){
        try {
            // Récupère dans un Cursor
            Cursor c = bdd.query(UtilsDomoWidget.TABLE_TOOGLE_WIDGET, new String[] {
                    UtilsDomoWidget.COL_ID,
                    UtilsDomoWidget.COL_ID_WIDGET,
                    UtilsDomoWidget.COL_NAME,
                    UtilsDomoWidget.COL_ID_BOX,
                    UtilsDomoWidget.COL_URL,
                    UtilsDomoWidget.COL_KEY,
                    UtilsDomoWidget.COL_ON,
                    UtilsDomoWidget.COL_OFF,
                    UtilsDomoWidget.COL_ETAT,
                    UtilsDomoWidget.COL_ID_IMAGE_ON,
                    UtilsDomoWidget.COL_ID_IMAGE_OFF,
                    UtilsDomoWidget.COL_LOCK,
                    UtilsDomoWidget.COL_EXP_REG,
                    UtilsDomoWidget.COL_TIME_OUT,
                    UtilsDomoWidget.COL_LAST_VALUE}, UtilsDomoWidget.COL_ID_WIDGET + " LIKE \"" + idWidget +"\"" , null, null, null, null);
            // Log.d(TAG, "Récuperation Widget <" + idWidget + "> dans la BDD");
            if (c.getCount() == 0) {
                Log.e(TAG, "Erreur : Widget non trouvé en BDD !");
                return null;
            }
            c.moveToFirst();
            ToogleWidget widget = cursorToObjet(c);
            // On ferme le cursor
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
    public ArrayList<ToogleWidget> getAllWidgets(){
        try {
            // Récupère dans un Cursor
            ArrayList<ToogleWidget> listWidget = new ArrayList<>();
            Cursor c = bdd.query(UtilsDomoWidget.TABLE_TOOGLE_WIDGET, new String[] {
                    UtilsDomoWidget.COL_ID,
                    UtilsDomoWidget.COL_ID_WIDGET,
                    UtilsDomoWidget.COL_NAME,
                    UtilsDomoWidget.COL_ID_BOX,
                    UtilsDomoWidget.COL_URL,
                    UtilsDomoWidget.COL_KEY,
                    UtilsDomoWidget.COL_ON,
                    UtilsDomoWidget.COL_OFF,
                    UtilsDomoWidget.COL_ETAT,
                    UtilsDomoWidget.COL_ID_IMAGE_ON,
                    UtilsDomoWidget.COL_ID_IMAGE_OFF,
                    UtilsDomoWidget.COL_LOCK,
                    UtilsDomoWidget.COL_EXP_REG,
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
     * @param toogleWidget
     * @param isON
     * @return
     */
    public Bitmap getRessource(ToogleWidget toogleWidget, boolean isON) {
        Cursor c;
        String ressource;
        if (isON) {
            ressource = "toggle_metal_on";                               // Valeur par default du toogle
            c = bdd.query(UtilsDomoWidget.TABLE_RESS_WIDGET, new String[] {
                    UtilsDomoWidget.COL_ID,
                    UtilsDomoWidget.COL_RESS_NAME,
                    UtilsDomoWidget.COL_RESS_PATH}, UtilsDomoWidget.COL_ID + " = " + toogleWidget.getDomoIdImageOn(), null, null, null, null);
        } else {
            ressource = "toggle_metal_off";                            // Valeur par default du toogle
            c = bdd.query(UtilsDomoWidget.TABLE_RESS_WIDGET, new String[] {
                    UtilsDomoWidget.COL_ID,
                    UtilsDomoWidget.COL_RESS_NAME,
                    UtilsDomoWidget.COL_RESS_PATH}, UtilsDomoWidget.COL_ID + " = " + toogleWidget.getDomoIdImageOff(), null, null, null, null);
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
    private ToogleWidget cursorToObjet(Cursor c) {
        // On lui affecte toutes les infos grâce aux infos contenues dans le Cursor
        ToogleWidget widget = new ToogleWidget(context, c.getInt(c.getColumnIndexOrThrow(UtilsDomoWidget.COL_ID_WIDGET)));
        widget.setId(c.getInt(c.getColumnIndexOrThrow(UtilsDomoWidget.COL_ID)));
        widget.setDomoName(c.getString(c.getColumnIndexOrThrow(UtilsDomoWidget.COL_NAME)));
        widget.setDomoBox(c.getInt(c.getColumnIndexOrThrow(UtilsDomoWidget.COL_ID_BOX)));
        widget.setDomoUrl(c.getString(c.getColumnIndexOrThrow(UtilsDomoWidget.COL_URL)));
        widget.setDomoKey(c.getString(c.getColumnIndexOrThrow(UtilsDomoWidget.COL_KEY)));
        widget.setDomoOn(c.getString(c.getColumnIndexOrThrow(UtilsDomoWidget.COL_ON)));
        widget.setDomoOff(c.getString(c.getColumnIndexOrThrow(UtilsDomoWidget.COL_OFF)));
        widget.setDomoState(c.getString(c.getColumnIndexOrThrow(UtilsDomoWidget.COL_ETAT)));
        widget.setDomoIdImageOn(c.getInt(c.getColumnIndexOrThrow(UtilsDomoWidget.COL_ID_IMAGE_ON)));
        widget.setDomoIdImageOff(c.getInt(c.getColumnIndexOrThrow(UtilsDomoWidget.COL_ID_IMAGE_OFF)));
        widget.setDomoLock(c.getInt(c.getColumnIndexOrThrow(UtilsDomoWidget.COL_LOCK)));
        widget.setDomoExpReg(c.getString(c.getColumnIndexOrThrow(UtilsDomoWidget.COL_EXP_REG)));
        widget.setDomoTimeOut(c.getInt(c.getColumnIndexOrThrow(UtilsDomoWidget.COL_TIME_OUT)));
        widget.setDomoLastValue(c.getString(c.getColumnIndexOrThrow(UtilsDomoWidget.COL_LAST_VALUE)));
        return widget;
    }
}

