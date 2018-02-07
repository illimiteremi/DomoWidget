package illimiteremi.domowidget.DomoServices;


import java.io.Serializable;

import illimiteremi.domowidget.DomoUtils.DomoConstants.WIDGET_TYPE;


/**
 * Created by XZAQ496 on 10/07/2017.
 */

public class DomoSerializableWidget implements Serializable {

    static final String TAG      = "[DOMO_SERIAL_WIDGET]";

    private Integer domoId        = -1;                            // Identifiant du Widget
    private WIDGET_TYPE domoType  = WIDGET_TYPE.UNKNOWN;           // Type du Widget
    private String  domoAction    = "";                            // Action Jeedom
    private String  domoExpReg    = "";                            // Expression réguliere - retour Etat
    private String  domoPluginKey = "";                            // Clef API du plugin (si nécessaire)
    private String  domoPluginURL = "";                            // URL du plugin (si nécessaire)

    public DomoSerializableWidget(Integer domoId, WIDGET_TYPE domoType) {
        this.domoId       = domoId;
        this.domoType     = domoType;
    }

    public Integer getDomoId() {
        return domoId;
    }

    public void setDomoId(Integer domoId) {
        this.domoId = domoId;
    }

    public WIDGET_TYPE getDomoType() {
        return domoType;
    }

    public void setDomoName(WIDGET_TYPE domoType) {
        this.domoType = domoType;
    }

    public String getDomoAction() {
        return domoAction;
    }

    public void setDomoAction(String domoAction) {
        this.domoAction = domoAction;
    }

    public String getDomoExpReg() {
        return domoExpReg;
    }

    public void setDomoExpReg(String domoExpReg) {
        this.domoExpReg = domoExpReg;
    }

    public String getDomoPluginKey() {
        return domoPluginKey;
    }

    public void setDomoPluginKey(String domoPluginKey) {
        this.domoPluginKey = domoPluginKey;
    }

    public String getDomoPluginURL() {
        return domoPluginURL;
    }

    public void setDomoPluginURL(String domoPluginURL) {
        this.domoPluginURL = domoPluginURL;
    }
}
