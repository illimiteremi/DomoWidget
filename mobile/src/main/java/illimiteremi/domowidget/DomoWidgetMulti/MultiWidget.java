package illimiteremi.domowidget.DomoWidgetMulti;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

import illimiteremi.domowidget.DomoGeneralSetting.BoxSetting;
import illimiteremi.domowidget.DomoUtils.DomoUtils;
import illimiteremi.domowidget.DomoWidgetBdd.DomoBoxBDD;

import static illimiteremi.domowidget.DomoUtils.DomoConstants.NO_VALUE;

/**
 * Created by rcouturi on 06/12/2016.
 */

public class MultiWidget {

    static final String TAG      = "[DOMO_OBJET_WIDGET]";

    private final Context context;                  // Context de l'app

    private Integer    id;                          // Identifiant SQL
    private Integer    domoId;                      // Identifiant du Widget
    private String     domoName        = "";        // Nom du Widget
    private Integer    domoBox         = 0;         // Identifiant de la box
    private String     domoUrl         = "";        // Url de la box
    private String     domoKey         = "";        // Clée de la box
    private String     domoState       = "";        // Action de l'état

    private Integer    domoIdImageOn   = 0;               // Identifant Image ON
    private Integer    domoIdImageOff  = 0;               // Identifant Image OFF
    private Integer    domoTimeOut     = 2;               // TimeOut avant retour
    private ArrayList<MultiWidgetRess> mutliWidgetRess;   // Ressource Mutli Widget
    private BoxSetting selectedBox;                       // Box selectionnée
    private Boolean    isPresent;                         // Widget présent sur le home
    private String     lastValue;                         // Derniere valeur connu du Widget


    public MultiWidget(Context context, Integer domoId) {
        // Log.d(TAG, "Instance de l'objet Widget : " + domoId);
        this.context     = context;
        this.domoId      = domoId;
        this.selectedBox = getSelectedBox();
        this.isPresent   = DomoUtils.widgetIsPresent(context, domoId);
    }

    public String getDomoLastValue() {
        return lastValue == null ? NO_VALUE : lastValue;
    }

    public void setDomoLastValue(String lastValue) {
        this.lastValue = lastValue;
    }

    public Boolean getPresent() {
        return isPresent;
    }

    public Integer getDomoBox() {
        return domoBox;
    }

    public void setDomoBox(Integer domoBox) {
        this.domoBox = domoBox;
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

    public String getDomoKey() {
        return domoKey;
    }

    public void setDomoKey(String domoKey) {
        this.domoKey = domoKey;
    }

    public String getDomoState() {
        return domoState;
    }

    public void setDomoState(String domoState) {
        this.domoState = domoState;
    }

    public String getDomoName() {
        return domoName;
    }

    public void setDomoName(String domoName) {
        this.domoName = domoName;
    }

    public String getDomoUrl() {
        return domoUrl;
    }

    public void setDomoUrl(String domoUrl) {
        this.domoUrl = domoUrl;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ArrayList<MultiWidgetRess> getMutliWidgetRess() {
        return mutliWidgetRess;
    }

    public void setMutliWidgetRess(ArrayList<MultiWidgetRess> mutliWidgetRess) {
        this.mutliWidgetRess = mutliWidgetRess;
    }

    public Integer getDomoTimeOut() {
        return domoTimeOut;
    }

    public void setDomoTimeOut(Integer domoTimeOut) {
        this.domoTimeOut = domoTimeOut;
    }

    public void MutliWidgetLog() {
        try {
            Log.d(TAG, "ID             = " + this.id);
            Log.d(TAG, "DOMO_ID        = " + this.domoId);
            Log.d(TAG, "DOMO_NAME      = " + this.domoName);
            Log.d(TAG, "DOMO_URL       = " + this.domoUrl);
            Log.d(TAG, "DOMO_KEY       = " + this.domoKey);
            Log.d(TAG, "DOMO_ETAT      = " + this.domoState);
            Log.d(TAG, "DOMO_IMAGE_ON  = " + this.domoIdImageOn);
            Log.d(TAG, "DOMO_IMAGE_OFF = " + this.domoIdImageOff);
            Log.d(TAG, "DOMO_TIME_OUT  = " + this.domoTimeOut);
            Log.d(TAG, "-----------------");
        } catch (Exception e) {
            Log.d(TAG, "Erreur : " + e);
        }
    }

    public BoxSetting getSelectedBox() {
        if (domoBox != 0) {
            DomoBoxBDD domoBoxBDD = new DomoBoxBDD(context);
            domoBoxBDD.open();
            selectedBox = domoBoxBDD.getBoxById(domoBox);
            domoBoxBDD.close();
            return selectedBox;
        }
        return null;
    }
}
