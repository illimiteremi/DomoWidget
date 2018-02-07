package illimiteremi.domowidget.DomoWidgetToogle;

import android.content.Context;
import android.util.Log;

import illimiteremi.domowidget.DomoGeneralSetting.BoxSetting;
import illimiteremi.domowidget.DomoUtils.DomoUtils;
import illimiteremi.domowidget.DomoWidgetBdd.DomoBoxBDD;

import static illimiteremi.domowidget.DomoUtils.DomoConstants.COMMANDE;

/**
 * Created by rcouturi on 02/07/2016.
 */
public class ToogleWidget {

    static final String TAG      = "[DOMO_OBJET_WIDGET]";

    private final Context context;              // Context de l'app

    private Integer id;                         // Identifiant SQL
    private Integer domoId;                     // Identifiant du Widget
    private String  domoName  = "";             // Nom du Widget
    private Integer domoBox   = 0;              // Identifiant de la box
    private String domoUrl    = "";             // Url de la box
    private String domoKey    = "";             // Clée de la box
    private String domoOn     = "";             // Action On
    private String domoOff    = "";             // Action Off
    private String domoState  = "";             // Action de l'état

    private Integer domoIdImageOn  = 0;         // Identifant Image ON
    private Integer domoIdImageOff = 0;         // Identifant Image OFF

    private Integer domoLock    = 0;            // Verouillage du widget
    private String  domoExpReg  = "";           // Expression Reguliere Retour Etat
    private Integer domoTimeOut = 0;            // TimeOut anant retour
    private BoxSetting selectedBox;             // Box selectionnée
    private Boolean    isPresent;               // Widget présent sur le home
    private String     lastValue;               // Derniere valeur connu du Widget

    public ToogleWidget(Context context, Integer domoId) {
        // Log.d(TAG, "Instance de l'objet Widget : " + domoId);
        this.context      = context;
        this.domoId       = domoId;
        this.domoOn       = COMMANDE;
        this.domoOff      = COMMANDE;
        this.domoState    = COMMANDE;
        this.selectedBox  = getSelectedBox();
        this.isPresent    = DomoUtils.widgetIsPresent(context, domoId);
    }

    public String getDomoLastValue() {
        return lastValue;
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDomoId() {
        return domoId;
    }

    public void setDomoId(Integer domoId) {
        this.domoId = domoId;
    }

    public String getDomoKey() {
        return domoKey;
    }

    public void setDomoKey(String domoKey) {
        this.domoKey = domoKey;
    }

    public String getDomoName() {
        return domoName;
    }

    public void setDomoName(String domoName) {
        this.domoName = domoName;
    }

    public String getDomoOff() {
        if (domoOff.isEmpty()) {
            domoOff = "type=cmd&id=";
        }
        return domoOff;
    }

    public void setDomoOff(String domoOff) {
        this.domoOff = domoOff;
    }

    public String getDomoOn() {
        if (domoOn.isEmpty()) {
            domoOn = "type=cmd&id=";
        }
        return domoOn;
    }

    public void setDomoOn(String domoOn) {
        this.domoOn = domoOn;
    }

    public String getDomoState() {
        if (domoState.isEmpty()) {
            domoState = "type=cmd&id=";
        }
        return domoState;
    }

    public void setDomoState(String domoState) {
        this.domoState = domoState;
    }

    public String getDomoUrl() {
        return domoUrl;
    }

    public void setDomoUrl(String domoUrl) {
        this.domoUrl = domoUrl;
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

    public void setDomoLock(Integer domoLock) {
        this.domoLock = domoLock;
    }

    public Integer getDomoLock() {
        return this.domoLock;
    }

    public String getDomoExpReg() {
        return domoExpReg;
    }

    public void setDomoExpReg(String domoExpReg) {
        this.domoExpReg = domoExpReg;
    }

    public Integer getDomoTimeOut() {
        return this.domoTimeOut;
    }

    public void setDomoTimeOut(Integer domoTimeOut) {
        this.domoTimeOut = domoTimeOut;
    }

    public void toogleWidgetLog() {
        try {
            Log.d(TAG, "ID             = " + this.id);
            Log.d(TAG, "DOMO_ID        = " + this.domoId);
            Log.d(TAG, "DOMO_NAME      = " + this.domoName);
            Log.d(TAG, "DOMO_URL       = " + this.domoUrl);
            Log.d(TAG, "DOMO_KEY       = " + this.domoKey);
            Log.d(TAG, "DOMO_ON        = " + this.domoOn);
            Log.d(TAG, "DOMO_OFF       = " + this.domoOff);
            Log.d(TAG, "DOMO_STATE     = " + this.domoState);
            Log.d(TAG, "DOMO_IMAGE_ON  = " + this.domoIdImageOn);
            Log.d(TAG, "DOMO_IMAGE_OFF = " + this.domoIdImageOff);
            Log.d(TAG, "DOMO_LOCK      = " + this.domoLock);
            Log.d(TAG, "DOMO_EXP_REG   = " + this.domoExpReg);
            Log.d(TAG, "DOMO_TIME_OUT = " + this.domoTimeOut);
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
