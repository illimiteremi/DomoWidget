package illimiteremi.domowidget.DomoWidgetBdd;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.util.ArrayList;

import illimiteremi.domowidget.DomoWidgetVocal.VocalWidget;

/**
 * Created by rcouturi on 02/07/2016.
 */
public class VocalWidgetBDD {

    private static final String TAG      = "[DOMO_VOCAL_BDD]";

    private SQLiteDatabase       bdd;
    private final DomoBaseSQLite domoBaseSQLite;
    private final Context        context;

    public VocalWidgetBDD(Context context){
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
    public long insertWidget(VocalWidget widget){
        // Création d'un ContentValues
        ContentValues values = new ContentValues();
        values.put(UtilsDomoWidget.COL_NAME, widget.getDomoName());
        values.put(UtilsDomoWidget.COL_ID_WIDGET, widget.getDomoId());
        values.put(UtilsDomoWidget.COL_ID_BOX, widget.getDomoBox());
        values.put(UtilsDomoWidget.COL_SYNTHESE_VOCAL, widget.getDomoSynthese());
        values.put(UtilsDomoWidget.COL_KEYPHRASE, widget.getKeyPhrase());
        values.put(UtilsDomoWidget.COL_THRESHOLD_LEVEL, widget.getThresholdLevel());
        values.put(UtilsDomoWidget.COL_ID_IMAGE_ON, widget.getDomoIdImageOn());
        // On insère l'objet dans la BDD via le ContentValues
        // Log.d(TAG, "Insertion Widget <" + widget.getDomoName() + "> dans la BDD");
        return bdd.insert(UtilsDomoWidget.TABLE_VOCAL_WIDGET, null, values);
    }

    /**
     * Mise à jour du widget dans la BDD
     * @param widget
     * @return
     */
    public int updateWidget(VocalWidget widget){
        ContentValues values = new ContentValues();
        values.put(UtilsDomoWidget.COL_ID_WIDGET, widget.getDomoId());
        values.put(UtilsDomoWidget.COL_NAME, widget.getDomoName());
        values.put(UtilsDomoWidget.COL_ID_BOX, widget.getDomoBox());
        values.put(UtilsDomoWidget.COL_SYNTHESE_VOCAL, widget.getDomoSynthese());
        values.put(UtilsDomoWidget.COL_KEYPHRASE, widget.getKeyPhrase());
        values.put(UtilsDomoWidget.COL_THRESHOLD_LEVEL, widget.getThresholdLevel());
        values.put(UtilsDomoWidget.COL_ID_IMAGE_ON, widget.getDomoIdImageOn());
        // Log.d(TAG, "Maj Widget <" + widget.getDomoName() + "> dans la BDD");
        return bdd.update(UtilsDomoWidget.TABLE_VOCAL_WIDGET, values, UtilsDomoWidget.COL_ID_WIDGET + " = " + widget.getDomoId(), null);
    }

    /**
     * Suppression du Widget en BDD
     * @param idWidget
     * @return
     */
    public int removeWidgetById(int idWidget){
        //Suppression d'un Widget de la BDD grâce à l'ID_WIDGET
        // Log.d(TAG, "Suppression Widget " + idWidget + " dans la BDD");
        return bdd.delete(UtilsDomoWidget.TABLE_VOCAL_WIDGET, UtilsDomoWidget.COL_ID_WIDGET + " = " + idWidget, null);
    }

    /**
     * Recherche d'un Widget en BDD
     * @param idWidget
     * @return
     */
    public VocalWidget getWidgetById(int idWidget){
        // Récupère dans un Cursor
        Cursor c = bdd.query(UtilsDomoWidget.TABLE_VOCAL_WIDGET, new String[] {
                                UtilsDomoWidget.COL_ID,
                                UtilsDomoWidget.COL_ID_WIDGET,
                                UtilsDomoWidget.COL_NAME,
                                UtilsDomoWidget.COL_ID_BOX,
                                UtilsDomoWidget.COL_SYNTHESE_VOCAL,
                                UtilsDomoWidget.COL_KEYPHRASE,
                                UtilsDomoWidget.COL_THRESHOLD_LEVEL,
                                UtilsDomoWidget.COL_ID_IMAGE_ON}, UtilsDomoWidget.COL_ID_WIDGET + " LIKE \"" + idWidget +"\"" , null, null, null, null);
        // Log.d(TAG, "Récuperation Widget <" + idWidget + "> dans la BDD");
        if (c.getCount() == 0) {
            Log.e(TAG, "Erreur : Widget non trouvé en BDD !");
            return null;
        }
        // Sinon on se place sur le premier élément
        c.moveToFirst();
        VocalWidget widget = cursorToObjet(c);
        c.close();
        return widget;
    }

    /**
     * Récuperation des widgets
     * @return
     */
    public ArrayList<VocalWidget> getAllWidgets(){
        // Récupère dans un Cursor
        ArrayList<VocalWidget> listWidget = new ArrayList<>();

        Cursor c = bdd.query(UtilsDomoWidget.TABLE_VOCAL_WIDGET, new String[] {
                UtilsDomoWidget.COL_ID,
                UtilsDomoWidget.COL_ID_WIDGET,
                UtilsDomoWidget.COL_NAME,
                UtilsDomoWidget.COL_ID_BOX,
                UtilsDomoWidget.COL_SYNTHESE_VOCAL,
                UtilsDomoWidget.COL_KEYPHRASE,
                UtilsDomoWidget.COL_THRESHOLD_LEVEL,
                UtilsDomoWidget.COL_ID_IMAGE_ON},null , null, null, null, null);
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
    }

    /**
     * Lecture du Cursor pour transformation Objet
     * @param c
     * @return
     */
    private VocalWidget cursorToObjet(Cursor c) {
        // On lui affecte toutes les infos grâce aux infos contenues dans le Cursor
        VocalWidget widget = new VocalWidget(context, c.getInt(c.getColumnIndexOrThrow(UtilsDomoWidget.COL_ID_WIDGET)));
        widget.setId(c.getInt(c.getColumnIndexOrThrow(UtilsDomoWidget.COL_ID)));
        widget.setDomoName(c.getString(c.getColumnIndexOrThrow(UtilsDomoWidget.COL_NAME)));
        widget.setDomoBox(c.getInt(c.getColumnIndexOrThrow(UtilsDomoWidget.COL_ID_BOX)));
        widget.setDomoSynthese(c.getInt(c.getColumnIndexOrThrow(UtilsDomoWidget.COL_SYNTHESE_VOCAL)));
        widget.setKeyPhrase(c.getString(c.getColumnIndexOrThrow(UtilsDomoWidget.COL_KEYPHRASE)));
        widget.setThresholdLevel(c.getInt(c.getColumnIndexOrThrow(UtilsDomoWidget.COL_THRESHOLD_LEVEL)));
        widget.setDomoIdImageOn(c.getInt(c.getColumnIndexOrThrow(UtilsDomoWidget.COL_ID_IMAGE_ON)));
        return widget;
    }

    /**
     * Récuperation de la ressource image
     * @param vocalWidget
     * @return
     */
    public Bitmap getRessource(VocalWidget vocalWidget) {
        Cursor c;
        String ressource;

        ressource = "widget_vocal_preview";                               // Valeur par default
        c = bdd.query(UtilsDomoWidget.TABLE_RESS_WIDGET, new String[] {
                UtilsDomoWidget.COL_ID,
                UtilsDomoWidget.COL_RESS_NAME,
                UtilsDomoWidget.COL_RESS_PATH}, UtilsDomoWidget.COL_ID + " = " + vocalWidget.getDomoIdImageOn(), null, null, null, null);

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
}