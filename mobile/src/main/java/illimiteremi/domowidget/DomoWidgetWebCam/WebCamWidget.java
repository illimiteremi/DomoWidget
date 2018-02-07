package illimiteremi.domowidget.DomoWidgetWebCam;

import android.content.Context;

import illimiteremi.domowidget.DomoGeneralSetting.BoxSetting;
import illimiteremi.domowidget.DomoUtils.DomoUtils;

/**
 * Created by rcouturi on 02/07/2016.
 */
public class WebCamWidget {

    static final String TAG      = "[DOMO_OBJET_WIDGET]";

    private final Context context;                               // Context de l'app

    private Integer id;                                          // Identifiant SQL
    private Integer domoBox   = 0;                               // Identifiant de la box
    private Integer domoId;                                      // Identifiant du Widget
    private String  domoName  = "";                              // Nom du Widget
    private String  domoUrl   = "";                              // Url de la box
    private String  domoPort  = "";                              // Port de la WebCam
    private Boolean isPresent;                                   // Widget présent sur le home

    private BoxSetting selectedBox;                              // Box selectionnée

    public WebCamWidget(Context context, Integer domoId) {
        // Log.d(TAG, "Instance de l'objet Widget : " + domoId);
        this.context      = context;
        this.domoId       = domoId;
        this.domoUrl      = "";
        this.isPresent    = DomoUtils.widgetIsPresent(context, domoId);
    }

    public Boolean getPresent() {
        return isPresent;
    }

    public Integer getDomoId() {
        return domoId;
    }

    public Integer getDomoBox() {
        return domoBox;
    }

    public void setDomoBox(Integer domoBox) {
        this.domoBox = domoBox;
    }

    public void setDomoId(Integer domoId) {
        this.domoId = domoId;
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

    public String getDomoPort() {
        return domoPort;
    }

    public void setDomoPort(String domoPort) {
        this.domoPort = domoPort;
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
