package illimiteremi.domowidget.DomoWidgetSeekBar;

import android.content.Context;

import java.util.Objects;

import illimiteremi.domowidget.DomoGeneralSetting.BoxSetting;
import illimiteremi.domowidget.DomoUtils.DomoUtils;

import static illimiteremi.domowidget.DomoUtils.DomoConstants.COMMANDE;

/**
 * Created by rcouturi on 02/07/2016.
 */
public class SeekBarWidget {

    private static final String TAG      = "[DOMO_OBJET_WIDGET]";

    private final Context context;              // Context de l'app

    private Integer id;                         // Identifiant SQL
    private Integer domoId;                     // Identifiant du Widget
    private String  domoName      = "";         // Nom du Widget
    private Integer domoBox       = 0;          // Identifiant de la box
    private String  domoUrl       = "";         // Url de la box
    private String  domoKey       = "";         // Clée de la box
    private String  domoAction    = "";         // Action du widget
    private String  domoState     = "";         // Etat di widget
    private Integer domoIdImageOn = 0;          // Identifant Image ON
    private String  domoColor     = "";         // Couleur de fond
    private Integer domoMinValue  = 0;          // Valeur mini du seekbar
    private Integer domoMaxValue  = 254;        // Valeur max du seekbar
    private String  lastValue;                  // Derniere valeur connu du Widget

    private BoxSetting selectedBox;              // Box selectionnée
    private Boolean    isPresent;                // Widget présent sur le home

   public SeekBarWidget(Context context, Integer domoId) {
       this.context      = context;
       this.domoId       = domoId;
       this.domoState    = COMMANDE;
       this.domoAction   = COMMANDE + "xx&slider=";
       this.isPresent    = DomoUtils.widgetIsPresent(context, domoId);
    }

    public String getDomoLastValue() {
        return lastValue == null ? "0" : lastValue;
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

    public String getDomoAction() {
        return domoAction;
    }

    public void setDomoAction(String domoAction) {
        this.domoAction = domoAction;
    }

    public String getDomoState() {
        return domoState;
    }

    public void setDomoState(String domoState) {
        this.domoState = domoState;
    }

    public Integer getDomoIdImageOn() {
        return domoIdImageOn;
    }

    public void setDomoIdImageOn(Integer domoIdImageOn) {
        this.domoIdImageOn = domoIdImageOn;
    }

    public Integer getDomoMinValue() {
        return domoMinValue;
    }

    public void setDomoMinValue(Integer domoMinValue) {
        this.domoMinValue = domoMinValue;
    }

    public Integer getDomoMaxValue() {
        return domoMaxValue;
    }

    public void setDomoMaxValue(Integer domoMaxValue) {
        this.domoMaxValue = domoMaxValue;
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

    public String getDomoColor() {
        return Objects.equals(domoColor, "") ? "-1" : domoColor;
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
