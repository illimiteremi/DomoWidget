package illimiteremi.domowidget.DomoWidgetState;

import android.content.Context;

import java.io.Serializable;

import illimiteremi.domowidget.DomoGeneralSetting.BoxSetting;
import illimiteremi.domowidget.DomoUtils.DomoConstants;
import illimiteremi.domowidget.DomoUtils.DomoUtils;

import static illimiteremi.domowidget.DomoUtils.DomoConstants.COMMANDE;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.ERROR;

/**
 * Created by rcouturi on 02/07/2016.
 */
public class StateWidget implements Serializable {

    static final String TAG      = "[DOMO_OBJET_WIDGET]";

    private final Context context;                               // Context de l'app

    private Integer id;                                          // Identifiant SQL
    private Integer domoId;                                      // Identifiant du Widget
    private String  domoName  = "";                              // Nom du Widget
    private Integer domoBox   = 0;                               // Identifiant de la box
    private String  domoUrl   = "";                              // Url de la box
    private String  domoKey   = "";                              // Clée de la box
    private String  domoState = "";                              // Action de l'état
    private String  domoUnit  = "";                              // Unité de la valeur d'état
    private String  domoColor = "";                              // Couleur de fond
    private BoxSetting selectedBox;                              // Box selectionnée
    private Integer    manuelUpdate;                             // Mise à jour manuel du widget
    private Boolean    isPresent;                                // Widget présent sur le home
    private String     lastValue;                                // Derniere valeur connu du Widget

    public StateWidget(Context context, Integer domoId) {
        // Log.d(TAG, "Instance de l'objet Widget : " + domoId);
        this.context      = context;
        this.domoId       = domoId;
        this.domoUrl      = "";
        this.domoState    = COMMANDE;
        this.domoColor    = DomoConstants.DEFAULT_COLOR;
        this.manuelUpdate = 0;
        this.isPresent    = DomoUtils.widgetIsPresent(context, domoId);
    }

    public String getDomoLastValue() {
        return lastValue == null ? ERROR : lastValue;
    }

    public void setDomoLastValue(String lastValue) {
        this.lastValue = lastValue;
    }

    public Integer getManuelUpdate() {
        return manuelUpdate;
    }

    public void setManuelUpdate(Integer manuelUpdate) {
        this.manuelUpdate = manuelUpdate;
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

    public String getDomoState() {
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDomoUnit() {
        return domoUnit;
    }

    public void setDomoUnit(String domoUnit) {
        this.domoUnit = domoUnit;
    }

    public String getDomoColor() {
        return domoColor;
    }

    public void setDomoColor(String domoColor) {
        this.domoColor = domoColor;
    }

    public BoxSetting getSelectedBox() {
        if (domoBox != 0) {
            selectedBox = new BoxSetting();
            selectedBox.setBoxId(this.domoBox);
            selectedBox = (BoxSetting) DomoUtils.getObjetById(context, selectedBox);
            return selectedBox;
        }
        return null;
    }
}
