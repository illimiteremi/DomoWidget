package illimiteremi.domowidget.DomoWidgetPush;

import android.content.Context;

import illimiteremi.domowidget.DomoGeneralSetting.BoxSetting;
import illimiteremi.domowidget.DomoUtils.DomoUtils;

import static illimiteremi.domowidget.DomoUtils.DomoConstants.COMMANDE;

/**
 * Created by rcouturi on 02/07/2016.
 */
public class PushWidget {

    private static final String TAG      = "[DOMO_OBJET_WIDGET]";

    private final Context context;               // Context de l'app

    private Integer id;                          // Identifiant SQL
    private Integer domoId;                      // Identifiant du Widget
    private String  domoName   = "";             // Nom du Widget
    private Integer domoBox    = 0;              // Identifiant de la box
    private String  domoUrl    = "";             // Url de la box
    private String  domoKey    = "";             // Clée de la box
    private String  domoAction = "";             // Action du widget

    private Integer domoIdImageOn  = 0;          // Identifant Image ON
    private Integer domoIdImageOff = 0;          // Identifant Image OFF

    private Integer    domoLock    = 0;          // Verouillage du widget
    private BoxSetting selectedBox;              // Box selectionnée
    private Boolean    isPresent;               // Widget présent sur le home

   public PushWidget(Context context, Integer domoId) {
       this.context      = context;
       this.domoId       = domoId;
       this.domoAction   = COMMANDE;
       this.isPresent    = DomoUtils.widgetIsPresent(context, domoId);
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

   public String getDomoAction() {
        return domoAction;
    }

   public void setDomoAction(String domoAction) {
        this.domoAction = domoAction;
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
