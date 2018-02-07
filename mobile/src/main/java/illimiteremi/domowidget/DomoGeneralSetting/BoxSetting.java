package illimiteremi.domowidget.DomoGeneralSetting;

import android.content.Context;
import android.util.Log;

import java.io.Serializable;

import static illimiteremi.domowidget.DomoUtils.DomoConstants.TIME_OUT;

/**
 * Created by rcouturi on  24/11/2016.
 */
public class BoxSetting implements Serializable {

    static final String TAG       = "[DOMO_GLOBAL_SETTING]";

    private Integer boxId               = 0;           // Identifiant de la box
    private String  boxName             = "";          // Nom de la box
    private String  boxKey              = "";          // Clée de la box
    private String  boxUrlInterne       = "";          // URL Interne
    private String  boxUrlExterne       = "";          // URL Externe
    private Integer boxTimeOut          = TIME_OUT;    // TimeOut
    private String  widgetNameColor     = "";          // Couleur du nom des widgets
    private Integer widgetTextSize      = 0;           // Taille du nom des widgets
    private Integer widgetRefreshTime   = TIME_OUT;    // Refresh

    public BoxSetting() {
        Log.d(TAG, widgetNameColor);
    }

    public Integer getBoxId() {
        return boxId == null ? 0 : boxId;
    }

    public void setBoxId(Integer boxId) {
        this.boxId = boxId;
    }

    public String getBoxKey() {
        return boxKey;
    }

    public void setBoxKey(String boxKey) {
        this.boxKey = boxKey;
    }

    public String getBoxName() {
        return boxName;
    }

    public void setBoxName(String boxName) {
        this.boxName = boxName;
    }

    public String getBoxUrlExterne() {
        return boxUrlExterne;
    }

    public void setBoxUrlExterne(String boxUrlExterne) {
        this.boxUrlExterne = boxUrlExterne;
    }

    public String getBoxUrlInterne() {
        return boxUrlInterne;
    }

    public void setBoxUrlInterne(String boxUrlInterne) {
        this.boxUrlInterne = boxUrlInterne;
    }

    public Integer getBoxTimeOut() {
        return boxTimeOut == null ? TIME_OUT : boxTimeOut;
    }

    public void setBoxTimeOut(Integer boxTimeOut) {
        this.boxTimeOut = boxTimeOut;
    }

    public int getWidgetNameColor() {
        int intColor;
        try {
            intColor = Integer.parseInt(widgetNameColor);
        } catch (NumberFormatException e){
            intColor = -1;
            Log.d(TAG, "Erreur color : " + intColor + " : " + e);
        }
        return intColor;
    }

    public void setWidgetNameColor(String widgetTextColor) {
        this.widgetNameColor = widgetTextColor;
    }

    public Integer getWidgetTextSize() {
        return widgetTextSize;
    }

    public void setWidgetTextSize(Integer widgetTextSize) {
        this.widgetTextSize = widgetTextSize;
    }

    public Integer getWidgetRefreshTime() {
        return widgetRefreshTime;
    }

    public void setWidgetRefreshTime(Integer widgetRefreshTime) {
        this.widgetRefreshTime = widgetRefreshTime;
    }
}
