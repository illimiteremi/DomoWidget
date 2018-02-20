package illimiteremi.domowidget.DomoUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XZAQ496 on 27/12/2016.
 */

public class DomoConstants {

    public static final String POSITION_VIEW         = "POSITION_VIEW";
    public static final String CONFIGURATION         = "CONFIGURATION";
    public static final int    LOCK_TIME             = 5 * 1000;
    public static final int    LOCK_TIME_SEC         = 5;
    public static final String DEFAULT_COLOR         = "0";
    public static final int    DEFAULT_TIMEOUT       = 2;
    public static final int    WHITE                 = -1;
    public static final String JEEDOM_URL            = "/core/api/jeeApi.php?apikey=";
    public static final String JEEDOM_API_URL        = "/core/api/jeeApi.php";
    public static final String GEOLOC_URL            = "/plugins/geoloc/core/api/jeeGeoloc.php";

    public static final String COMMANDE              = "type=cmd&id=";
    public static final String GEOLOC                = "id=";
    public static final String SCENARIO              = "type=scenario&id=";
    public static final String ACTION                = "&action=";
    public static final String INTERCATION           = "&type=interact&query=%";
    public static final String CAM_SNAPSHOT          = "&snapshot.cgi";

    public static final String WIDGET_ERROR          = "Informations Widget !";

    public static final String UNKNOWN     = "UNKNOWN";
    public static final String TOOGLE      = "ToogleWidget";
    public static final String STATE       = "StateWidget";
    public static final String PUSH        = "PushWidget";
    public static final String LOCATION    = "LocationWidget";
    public static final String MULTI       = "MultiWidget";
    public static final String VOCAL       = "VocalWidget";
    public static final String SEEKBAR     = "SeekBarWidget";
    public static final String WEAR        = "WearSetting";
    public static final String BOX         = "BoxSetting";
    public static final String MULTI_RESS  = "MultiWidgetRess";
    public static final String WEBCAM      = "WebCamWidget";
    public static final String ICON        = "IconSetting";

    public static final String ERROR             = "ERROR";
    public static final String MATCH             = "1";
    public static final String NO_MATCH          = "0";
    public static final String DONE              = "DONE";
    public static final int    PERMISSION_OK     = 1;
    public static final int    NEW_WIDGET        = -1;
    public static final int    NO_WIDGET         = -2;

    public static final int    MOBILE_TIME_OUT   = 5;
    public static final int    WIFI_TIME_OUT     = 2;
    public static final int    READ_TIME_OUT     = 10;
    public static final int    TIME_OUT          = 0;

    public static final String IMPORT_DOMO_WIDGET = "IMPORT_DOMO_WIDGET";
    public static final String IMPORT_ICON        = "IMPORT_ICON";

    public enum PROVIDER_TYPE {
        GPS          (0, "gps"),
        NETWORK      (1, "network"),
        PASSIVE      (2, "passive"),
        DISABLE      (3, "désactivé");

        private int     code       = 0;
        private String  provider   = "";

        /**
         * MEDIA_TYPE constructor
         * @param _code
         * @param _provider
         */
        PROVIDER_TYPE(int _code, String _provider) {
            code       = _code;
            provider   = _provider;
        }

        /**
         * getCode
         * @return
         */
        public int getCode() { return code; }

        /**
         * getLibelle
         * @return
         */
        public String getProvider() {
            return provider;
        }


        /**
         * Retourne la liste des medias (en string)
         * @return
         */
        public static List<String> toList() {
            List<String> myList = new ArrayList<>();
            for (PROVIDER_TYPE media: PROVIDER_TYPE.values()) {
                myList.add(media.getProvider());
            }
            return myList;
        }
    }

    public enum WIDGET_TYPE {
        UNKNOWN      (-1, ""                             , ""),
        STATE        (0 , UPDATE_WIDGET_STATE_VALUE      , UPDATE_WIDGET_STATE_ERROR),
        TOOGLE       (1 , UPDATE_WIDGET_TOOGLE_VALUE     , UPDATE_WIDGET_TOOGLE_ERROR),
        PUSH         (2 , UPDATE_WIDGET_PUSH_VALUE       , UPDATE_WIDGET_PUSH_ERROR),
        MULTI        (3 , UPDATE_WIDGET_MULTI_VALUE      , UPDATE_WIDGET_MULTI_ERROR),
        LOCATION     (4 , UPDATE_WIDGET_LOCATION_CHANGED , UPDATE_WIDGET_LOCATION_ERROR),
        VOCAL        (5 , UPDATE_WIDGET_VOCAL_VALUE      , UPDATE_WIDGET_VOCAL_ERROR),
        WEAR         (6 , UPDATE_WIDGET_WEAR_VALUE       , UPDATE_WIDGET_WEAR_ERROR),
        SEEKBAR      (7 , UPDATE_WIDGET_SEEKBAR_VALUE    , UPDATE_WIDGET_SEEKBAR_ERROR),
        WEBCAM       (8 , UPDATE_WIDGET_WEBCAM_VALUE     , UPDATE_WIDGET_WEBCAM_ERROR);

        private int     code           = -1;
        private String  widgetAction;
        private String  widgetError;

        WIDGET_TYPE(int _code, String _widgetAction, String _widgetError) {
            this.code         = _code;
            this.widgetAction = _widgetAction;
            this.widgetError  = _widgetError;
        }

        public int getCode() {
            return code;
        }

        public String getWidgetAction() {
            return widgetAction;
        }

        public String getWidgetError() {
            return widgetError;
        }
    }

    // INTENT SERVICE
    public static final String REQUEST        = "REQUEST";
    public static final String REQUEST_BOX    = "REQUEST_BOX";
    public static final String REQUEST_GEOLOC = "REQUEST_GEOLOC";
    public static final String BOX_PING       = "BOX_PING";
    public static final String BOX_MESSAGE    = "BOX_MESSAGE";
    public static final String REQUEST_WEBCAM = "WEBCAM";
    public static final String WIDGET_VALUE   = "WIDGET_VALUE";

    // STATE
    public static final String  UPDATE_ALL_WIDGET_STATE    = "android.appwidget.action.STATE_WIDGET_UPDATE_ALL";
    public static final String  UPDATE_WIDGET_STATE_VALUE  = "android.appwidget.action.STATE_WIDGET_VALUE_UPDATE";
    public static final String  UPDATE_WIDGET_STATE_ERROR  = "android.appwidget.action.STATE_WIDGET_ERROR";
    public static final String  NO_VALUE                   = "--.-";
    public static final String  STATE_LABEL                = "Widget Info";

    // TOOGLE
    public static final String  UPDATE_ALL_WIDGET_TOOGLE    = "android.appwidget.action.TOOGLE_WIDGET_UPDATE_ALL";
    public static final String  UPDATE_WIDGET_TOOGLE_VALUE  = "android.appwidget.action.TOOGLE_WIDGET_VALUE_UPDATE";
    public static final String  UPDATE_WIDGET_TOOGLE_CHANGE = "android.appwidget.action.TOOGLE_WIDGET_CHANGE";
    public static final String  UPDATE_WIDGET_TOOGLE_UNLOCK = "android.appwidget.action.APPWIDGET_UNLOCK";
    public static final String  UPDATE_WIDGET_TOOGLE_ERROR  = "android.appwidget.action.TOOGLE_WIDGET_ERROR";
    public static final String  TOOGLE_LABEL                = "Widget Action";

    // PUSH
    public static final String  UPDATE_ALL_WIDGET_PUSH    = "android.appwidget.action.PUSH_WIDGET_UPDATE_ALL";
    public static final String  UPDATE_WIDGET_PUSH_VALUE  = "android.appwidget.action.PUSH_WIDGET_VALUE_UPDATE";
    public static final String  UPDATE_WIDGET_PUSH_UNLOCK = "android.appwidget.action.APPWIDGET_UNLOCK";
    public static final String  UPDATE_WIDGET_PUSH_ERROR  = "android.appwidget.action.PUSH_WIDGET_ERROR";
    public static final String  PUSH_LABEL                = "Widget Push";
    public static final int     PUSH_TIME                 = 1;

    // MULTI
    public static final String  UPDATE_ALL_MULTI_WIDGET       = "android.appwidget.action.MULTI_WIDGET_UPDATE_ALL";
    public static final String  UPDATE_WIDGET_MULTI_VALUE     = "android.appwidget.action.MULTI_WIDGET_VALUE_UPDATE";
    public static final String  UPDATE_WIDGET_MULTI_ERROR     = "android.appwidget.action.MULTI_WIDGET_ERROR";
    public static final String  ACTION_WIDGET_MULTI_RESS_PUSH = "android.appwidget.action.RESS_BUTTON_PUSH";
    public static final String  MULTI_LABEL                   = "Widget Mutli";

    // VOCAL
    public static final String  UPDATE_ALL_VOCAL_WIDGET   = "android.appwidget.action.VOCAL_WIDGET_UPDATE_ALL";
    public static final String  UPDATE_WIDGET_VOCAL_VALUE = "android.appwidget.action.VOCAL_WIDGET_VALUE_UPDATE";
    public static final String  UPDATE_WIDGET_VOCAL_ERROR = "android.appwidget.action.VOCAL_WIDGET_ERROR";
    public static final String  VOCAL_LABEL               = "Widget Vocal";

    // GPS
    public static final String  UPDATE_ALL_LOCATION_WIDGET      = "android.appwidget.action.LOCATION_WIDGET_UPDATE_ALL";
    public static final String  UPDATE_WIDGET_LOCATION_CHANGED  = "android.appwidget.action.LOCATION_WIDGET_CHANGED";
    public static final String  UPDATE_WIDGET_LOCATION_ERROR    = "android.appwidget.action.LOCATION_WIDGET_ERROR";
    public static final int     TIMEOUT_LOCATION                = 15;
    public static final int     DISTANCE_LOCATION               = 100;
    public static final int     GPS_TIME_OUT                    = 2;
    public static final String  LOCATION_LABEL                  = "Widget GPS";

    // SEEKBAR
    public static final String  UPDATE_ALL_WIDGET_SEEKBAR    = "android.appwidget.action.SEEKBAR_WIDGET_UPDATE_ALL";
    public static final String  UPDATE_WIDGET_SEEKBAR_VALUE  = "android.appwidget.action.SEEKBAR_WIDGET_VALUE_UPDATE";
    public static final String  UPDATE_WIDGET_SEEKBAR_ERROR  = "android.appwidget.action.SEEKBAR_WIDGET_ERROR";
    public static final String  ACTION_WIDGET_SEEKBAE_PUSH   = "android.appwidget.action.SEEKBAR_PUSH";
    public static final String  SEEKBAR_LABEL                = "Widget SeekBar";
    public static final int     SEEKBAR_TIME                 = 1000;

    // WEBCAM
    public static final String  UPDATE_ALL_WIDGET_WEBCAM    = "android.appwidget.action.WEBCAM_WIDGET_UPDATE_ALL";
    public static final String  UPDATE_WIDGET_WEBCAM_VALUE  = "android.appwidget.action.WEBCAM_WIDGET_VALUE_UPDATE";
    public static final String  UPDATE_WIDGET_WEBCAM_ERROR  = "android.appwidget.action.WEBCAM_WIDGET_ERROR";
    public static final String  WEBCAM_LABEL                = "Widget WebCam";

    // PAYPAL & FORUM
    public static final String  URL_FORUM               = "https://www.jeedom.com/forum/viewtopic.php?f=25&t=19261";
    public static final String  URL_PAYPAL              = "https://www.paypal.me/illimiteremi";
    public static final String  URL_WORDPRESS           = "https://domowidget.wordpress.com";

    // ALL
    public static final String  INTENT_NO_DATA          = "android.appwidget.action.APPWIDGET_NODATA";
    public static final String  APPWIDGET_UPDATE        = "android.appwidget.action.APPWIDGET_UPDATE";

    // BOX
    public static final String  PING_ACTION             = "android.appbox.action.APPBOX_PING";

    // WEAR
    public static final String   UPDATE_WIDGET_WEAR_VALUE = "android.appwidget.action.WEAR_WIDGET_VALUE_UPDATE";
    public static final String   UPDATE_WIDGET_WEAR_ERROR = "android.appwidget.action.WEAR_WIDGET_ERROR";
    public static final String  SETTING                   = "SETTING";
    public static final String  INTERACTION_PATH          = "/interaction";
    public static final String  SETTING_PATH              = "/setting";
    public static final String  WEAR_SETTING              = "WEAR_SETTING";
    public static final String  WEAR_INTERACTION          = "INTERACTION";
    public static final String  JSON_ASK_TYPE             = "ASK_TYPE";
    public static final String  JSON_MESSAGE              = "MESSAGE";
    public static final int     DEFAULT_WEAR_TIMEOUT      = 3;
    public static final int     DEFAULT_SHAKE_TIMEOUT     = 5;
    public static final int     DEFAULT_SHAKE_LEVEL       = 0;

}