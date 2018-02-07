package illimiteremi.domowidget.DomoWidgetBdd;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

/**
 * Created by xzaq496 on 16/02/2017.
 */

public class IconWidgetBDD {

    static final String TAG      = "[DOMO_ICON_WIDGET_BDD]";

    private SQLiteDatabase bdd;
    private final DomoBaseSQLite domoBaseSQLite;

    public IconWidgetBDD(Context context){
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
     * Suppression d'une box en BDD
     * @param id
     * @return
     */
    public int removeIcon(int id){
        //Suppression d'un Widget de la BDD grâce à l'ID_WIDGET
        try {
            return bdd.delete(UtilsDomoWidget.TABLE_RESS_WIDGET, UtilsDomoWidget.COL_ID + " = " + id, null);
        } catch (SQLiteException e) {
            Log.e(TAG, "Erreur : " + e);
            return 0;
        }
    }
}
