package illimiteremi.domowidget.DomoWidgetVocal;

import android.content.Context;

import illimiteremi.domowidget.DomoGeneralSetting.BoxSetting;
import illimiteremi.domowidget.DomoUtils.DomoUtils;

/**
 * Created by rcouturi on 02/07/2016.
 */
public class VocalWidget {

    static final String TAG      = "[DOMO_OBJET_WIDGET]";

    private final Context context;                                  // Context de l'app

    private Integer    id;                                          // Identifiant SQL
    private Integer    domoId;                                      // Identifiant du Widget
    private String     domoName  = "";                              // Nom du Widget
    private Integer    domoBox   = 0;                               // Identifiant de la box
    private Integer    domoSynthese;                                // Retour synthese vocal
    private BoxSetting selectedBox;                                 // Box selectionnée
    private Boolean    isPresent;                                   // Widget présent sur le home
    private String     keyPhrase;                                   // Mot clef
    private Integer    thresholdLevel;                              // Seuil de détection
    private Integer    domoIdImageOn  = 0;                          // Identifant Image ON

    public VocalWidget(Context context, Integer domoId) {
        // Log.d(TAG, "Instance de l'objet Widget : " + domoId);
        this.context        = context;
        this.domoId         = domoId;
        this.domoSynthese   = 1;
        this.thresholdLevel = 0;
        this.isPresent      = DomoUtils.widgetIsPresent(context, domoId);
    }

    public Integer getDomoSynthese() {
        return domoSynthese;
    }

    public void setDomoSynthese(int domoSynthese) {
        this.domoSynthese = domoSynthese;
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

    public String getDomoName() {
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

    public String getKeyPhrase() {
        return keyPhrase;
    }

    public void setKeyPhrase(String keyPhrase) {
        this.keyPhrase = keyPhrase;
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

    public Integer getThresholdLevel() {
        return thresholdLevel;
    }

    public void setThresholdLevel(Integer thresholdLevel) {
        this.thresholdLevel = thresholdLevel;
    }

    public Integer getDomoIdImageOn() {
        return domoIdImageOn;
    }

    public void setDomoIdImageOn(Integer domoIdImageOn) {
        this.domoIdImageOn = domoIdImageOn;
    }
}
