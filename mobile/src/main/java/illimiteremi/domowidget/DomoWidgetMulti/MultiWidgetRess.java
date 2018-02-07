package illimiteremi.domowidget.DomoWidgetMulti;

import android.annotation.SuppressLint;
import android.util.Log;

import static illimiteremi.domowidget.DomoUtils.DomoConstants.COMMANDE;

/**
 * Created by rcouturi on 05/12/2016.
 */

public class MultiWidgetRess {

    static final String TAG      = "[DOMO_OBJET_WIDGET_RESS]";

    private Integer id;                         // Identifiant SQL
    private Integer domoId;                     // Identifiant du Widget
    private String  domoName       = "";        // Nom du Widget
    private String  domoAction     = "";        // Action de mise à jour
    private Integer domoIdImageOn  = 0;         // Identifant Image ON
    private Integer domoIdImageOff = 0;         // Identifant Image OFF
    private Integer domoDefault    = 0;         // Ressource par default

    public MultiWidgetRess(Integer domoId) {
        // Log.d(TAG, "Instance de l'objet Widget : " + domoId);
        this.domoId = domoId;
        this.domoName = "Nouvelle Action";
        this.domoAction = COMMANDE;
    }

    public String getDomoAction() {
        return domoAction;
    }

    public void setDomoAction(String domoAction) {
        this.domoAction = domoAction;
    }

    public Integer getDomoId() {
        return domoId;
    }

    public void setDomoId(Integer domoId) {
        this.domoId = domoId;
    }

    public Integer getDomoIdImageOff() {
        return domoIdImageOff;
    }

    public void setDomoIdImageOff(Integer domoIdImageOff) {
        this.domoIdImageOff = domoIdImageOff;
    }

    public Integer getDomoIdImageOn() {
        return domoIdImageOn;
    }

    public void setDomoIdImageOn(Integer domoIdImageOn) {
        this.domoIdImageOn = domoIdImageOn;
    }

    public String getDomoName() {
        domoName = ((domoName.isEmpty()) ? domoName = "Nouvelle Action" : domoName);
        return domoName;
    }

    public void setDomoName(String domoName) {
        this.domoName = domoName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDomoDefault() {
        return domoDefault;
    }

    public void setDomoDefault(Integer domoDefault) {
        this.domoDefault = domoDefault;
    }

    @SuppressLint("LongLogTag")
    public void multiWidgetRessLog() {
        try {
            Log.d(TAG, "ID             = " + this.id);
            Log.d(TAG, "DOMO_ID        = " + this.domoId);
            Log.d(TAG, "DOMO_NAME      = " + this.domoName);
            Log.d(TAG, "DOMO_ACTION    = " + this.domoAction);
            Log.d(TAG, "DOMO_IMAGE_ON  = " + this.domoIdImageOn);
            Log.d(TAG, "DOMO_IMAGE_OFF = " + this.domoIdImageOff);
            Log.d(TAG, "DOMO_DEFAULT   = " + this.domoDefault);
            Log.d(TAG, "-----------------");
        } catch (Exception e) {
            Log.d(TAG, "Erreur : " + e);
        }
    }

}
