package illimiteremi.domowidget.DomoWidgetLocation;

import android.content.Context;

import illimiteremi.domowidget.DomoGeneralSetting.BoxSetting;
import illimiteremi.domowidget.DomoUtils.DomoConstants;
import illimiteremi.domowidget.DomoUtils.DomoUtils;

import static illimiteremi.domowidget.DomoUtils.DomoConstants.GEOLOC;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.GEOLOC_URL;

/**
 * Created by rcouturi on  24/11/2016.
 */
public class LocationWidget {

    private static final String TAG             = "[DOMO_OBJET_WIDGET]";

    private final Context context;              // Context de l'app

    private Integer id;                         // Identifiant SQL
    private Integer domoId        = 0;          // Identifiant du Widget
    private String  domoName      = "";         // Nom du Widget
    private Integer domoBox       = 0;          // Identifiant de la box
    private String  domoUrl       = "";         // Url de la box
    private String  domoKey       = "";         // Clef API du pluging Geoloc
    private String  domoAction    = "";         // Action de mise à jour GPS

    private Integer    domoTimeOut;             // Unité de la valeur d'état
    private Integer    domoDistance;            // Unité de la valeur d'état
    private String     domoLocation = "";       // Latitude,Longitude
    private String     domoProvider;            // Type de provider GPS : gpd, network, passive
    private BoxSetting selectedBox;             // Box selectionnée
    private Boolean    isPresent;               // Widget présent sur le home

    /**
     * Constructeur
     * @param context
     * @param domoId
     */
    public LocationWidget(Context context, Integer domoId) {
        // Log.d(TAG, "Instance de l'objet Widget : " + domoId);
        this.context      = context;
        this.domoId       = domoId;
        this.domoUrl      = GEOLOC_URL;
        this.domoAction   = GEOLOC;
        this.domoProvider = DomoConstants.PROVIDER_TYPE.NETWORK.getProvider();
        this.domoTimeOut  = DomoConstants.TIMEOUT_LOCATION;
        this.domoDistance = DomoConstants.DISTANCE_LOCATION;
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

    public String getDomoLocation() {
        return domoLocation;
    }

    public void setDomoLocation(String domoLocation) {
        this.domoLocation = domoLocation;
    }

    public Integer getDomoDistance() {
        return domoDistance;
    }

    public void setDomoDistance(Integer domoDistance) {
        this.domoDistance = domoDistance;
    }

    public Integer getDomoTimeOut() {
        return domoTimeOut;
    }

    public void setDomoTimeOut(Integer domoTimeOut) {
        this.domoTimeOut = domoTimeOut;
    }

    public String getDomoProvider() {
        return domoProvider;
    }

    public void setDomoProvider(String domoProvider) {
        this.domoProvider = domoProvider;
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
