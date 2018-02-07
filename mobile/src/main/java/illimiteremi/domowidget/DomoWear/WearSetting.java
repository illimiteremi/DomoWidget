package illimiteremi.domowidget.DomoWear;

import org.json.JSONException;
import org.json.JSONObject;

import static illimiteremi.domowidget.DomoUtils.DomoConstants.DEFAULT_SHAKE_LEVEL;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.DEFAULT_SHAKE_TIMEOUT;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.DEFAULT_WEAR_TIMEOUT;
import static illimiteremi.domowidget.DomoWidgetBdd.UtilsDomoWidget.COL_ID_BOX;
import static illimiteremi.domowidget.DomoWidgetBdd.UtilsDomoWidget.COL_SHAKE_LEVEL;
import static illimiteremi.domowidget.DomoWidgetBdd.UtilsDomoWidget.COL_SHAKE_TIME_OUT;
import static illimiteremi.domowidget.DomoWidgetBdd.UtilsDomoWidget.COL_TIME_OUT;

/**
 * Created by rcouturi on  24/11/2016.
 */
public class WearSetting {

    static final String TAG       = "[DOMO_WEAR_SETTING]";

    private Integer id;                   // Identifiant SQL
    private Integer boxId;                // Identifiant de la box
    private Integer wearTimeOut;          // TimeOut avant envoi
    private Integer shakeTimeOut;         // TimeOut avant envoi
    private Integer shakeLevel;           // TimeOut avant envoi

    public WearSetting() {
        this.boxId = 0;
        this.wearTimeOut  = DEFAULT_WEAR_TIMEOUT;
        this.shakeTimeOut = DEFAULT_SHAKE_TIMEOUT;
        this.shakeLevel   = DEFAULT_SHAKE_LEVEL;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getBoxId() {
        return boxId == null ? 0 : boxId;
    }

    public void setBoxId(Integer boxId) {
        this.boxId = boxId;
    }

    public Integer getWearTimeOutTimeOut() {
        return wearTimeOut = (wearTimeOut == null) ? DEFAULT_WEAR_TIMEOUT : wearTimeOut;
    }

    public void setWearTimeOutTimeOut(Integer domoTimeOut) {
        if (domoTimeOut == (int) domoTimeOut) {
            this.wearTimeOut = domoTimeOut;
        } else {
            this.wearTimeOut = DEFAULT_WEAR_TIMEOUT;
        }
    }

    public Integer getShakeTimeOut() {
        return shakeTimeOut;
    }

    public void setShakeTimeOut(Integer shakeTimeOut) {
        if (shakeTimeOut == (int) shakeTimeOut) {
            this.shakeTimeOut = shakeTimeOut;
        } else {
            this.shakeTimeOut = DEFAULT_SHAKE_TIMEOUT;
        }
    }

    public Integer getShakeLevel() {
        return shakeLevel;
    }

    public void setShakeLevel(Integer shakeLevel) {
        this.shakeLevel = shakeLevel;
    }

    public JSONObject toJson(){
        JSONObject jsonWearSetting = new JSONObject();
        try {
            jsonWearSetting.put(COL_ID_BOX, this.boxId);
            jsonWearSetting.put(COL_TIME_OUT,this.wearTimeOut);
            jsonWearSetting.put(COL_SHAKE_TIME_OUT, this.shakeTimeOut);
            jsonWearSetting.put(COL_SHAKE_LEVEL, this.shakeLevel);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonWearSetting;
    }
}
