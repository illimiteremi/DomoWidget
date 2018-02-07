package illimiteremi.domowidget.DomoWidgetBdd;

import static illimiteremi.domowidget.DomoUtils.DomoConstants.TIME_OUT;

/**
 * Created by rcouturi on 02/07/2016.
 */
public class UtilsDomoWidget {

    public static final int    VERSION_BDD                 = 34;
    public static final String NOM_BDD                     = "domo_widget.db";
    public static final String TABLE_TOOGLE_WIDGET         = "table_toogle_widget";
    public static final String TABLE_STATE_WIDGET          = "table_state_widget";
    public static final String TABLE_RESS_WIDGET           = "table_ressource_widget";
    public static final String TABLE_PUSH_WIDGET           = "table_push_widget";
    public static final String TABLE_LOCATION_WIDGET       = "table_location_widget";
    public static final String TABLE_MUTLI_WIDGET          = "table_multi_widget";
    public static final String TABLE_MUTLI_RESSOURCE       = "table_multi_widget_ress";
    public static final String TABLE_SEEKBAR_WIDGET        = "table_domo_seekbar";
    public static final String TABLE_GLOBAL_SETTING        = "table_global_setting";
    public static final String TABLE_VOCAL_WIDGET          = "table_vocal_widget";
    public static final String TABLE_DOMO_WEAR             = "table_domo_wear";
    public static final String TABLE_WEBCAM_WIDGET         = "table_webcam_widget";

    public static final String COL_ID                       = "ID";
    public static final String COL_ID_WIDGET                = "ID_WIDGET";
    public static final String COL_NAME                     = "NOM_ACTION";
    public static final String COL_URL                      = "URL";
    public static final String COL_KEY                      = "DOMO_KEY";
    public static final String COL_ON                       = "ACTION_ON";
    public static final String COL_OFF                      = "ACTION_OFF";
    public static final String COL_ETAT                     = "ETAT";
    public static final String COL_MANUEL_UPDATE            = "MANUEL_UPDATE";
    public static final String COL_PORT                     = "PORT";

    public static final String COL_LAST_VALUE               = "LAST_VALUE";

    public static final String COL_ID_IMAGE_ON              = "ID_IMAGE_ON";
    public static final String COL_ID_IMAGE_OFF             = "ID_IMAGE_OFF";
    public static final String COL_LOCK                     = "IS_LOCK";
    public static final String COL_EXP_REG                  = "EXP_REG";
    public static final String COL_COLOR                    = "ARGB";
    public static final String COL_UNIT                     = "UNIT";

    public static final String COL_RESS_NAME                = "NAME";
    public static final String COL_RESS_PATH                = "PATH";
    public static final String COL_ACTION                   = "ACTION";

    public static final String COL_TIME_OUT                 = "TIME_OUT";
    public static final String COL_DISTANCE                 = "DISTANCE";
    public static final String COL_LOCATION                 = "LOCATION";
    public static final String COL_PROVIDER                 = "PROVIDER";

    public static final String COL_DEFAULT_MULTI_RESS       = "COL_ID_DEFAULT";

    public static final String COL_ID_BOX                   = "ID_BOX";
    public static final String COL_BOX_KEY                  = "BOX_KEY";
    public static final String COL_URL_INTERNE              = "URL_INTERNE";
    public static final String COL_URL_EXTERNE              = "URL_EXTERNE";
    public static final String COL_REFRESH_TIME             = "REFRESH_TIME";
    public static final String COL_TEXT_SIZE                = "TEXT_SIZE";

    public static final String COL_SYNTHESE_VOCAL           = "SYNTHESE_VOCAL";
    public static final String COL_KEYPHRASE                = "KEYPHRASE";
    public static final String COL_THRESHOLD_LEVEL          = "THRESHOLD_LEVEL";

    public static final String COL_SHAKE_TIME_OUT           = "SHAKE_TIME_OUT";
    public static final String COL_SHAKE_LEVEL              = "SHAKE_LEVEL";

    public static final String COL_MIN                      = "IN_VALUE";
    public static final String COL_MAX                      = "MAX_VALUE";


    public static final String CREATE_LOCATION_BDD = "CREATE TABLE IF NOT EXISTS " + TABLE_LOCATION_WIDGET + " ("
            + COL_ID             + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_ID_WIDGET      + " INTEGER NOT NULL, "
            + COL_NAME           + " TEXT, "
            + COL_ID_BOX         + " INTEGER, "
            + COL_URL            + " TEXT, "
            + COL_KEY            + " TEXT, "
            + COL_ACTION         + " TEXT, "
            + COL_TIME_OUT       + " INTEGER, "
            + COL_DISTANCE       + " INTEGER, "
            + COL_LOCATION       + " TEXT, "
            + COL_PROVIDER       + " TEXT DEFAULT 'network');";

    public static final String CREATE_TOOGLE_BDD = "CREATE TABLE IF NOT EXISTS " + TABLE_TOOGLE_WIDGET + " ("
            + COL_ID             + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_ID_WIDGET      + " INTEGER NOT NULL, "
            + COL_NAME           + " TEXT, "
            + COL_ID_BOX         + " INTEGER, "
            + COL_URL            + " TEXT, "
            + COL_KEY            + " TEXT, "
            + COL_ON             + " TEXT, "
            + COL_OFF            + " TEXT, "
            + COL_ETAT           + " TEXT, "
            + COL_ID_IMAGE_ON    + " INTEGER, "
            + COL_ID_IMAGE_OFF   + " INTEGER, "
            + COL_LOCK           + " INTEGER DEFAULT 0, "
            + COL_EXP_REG        + " TEXT, "
            + COL_TIME_OUT       + " INTEGER DEFAULT 2,"
            + COL_LAST_VALUE     + " TEXT);";

    public static final String CREATE_PUSH_BDD = "CREATE TABLE IF NOT EXISTS " + TABLE_PUSH_WIDGET + " ("
            + COL_ID           + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_ID_WIDGET    + " INTEGER NOT NULL, "
            + COL_NAME         + " TEXT, "
            + COL_ID_BOX       + " INTEGER, "
            + COL_URL          + " TEXT, "
            + COL_KEY          + " TEXT, "
            + COL_ACTION       + " TEXT, "
            + COL_ID_IMAGE_ON  + " INTEGER, "
            + COL_ID_IMAGE_OFF + " INTEGER,"
            + COL_LOCK         + " INTEGER);";

    public static final String CREATE_STATE_BDD = "CREATE TABLE IF NOT EXISTS " + TABLE_STATE_WIDGET + " ("
            + COL_ID            + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_ID_WIDGET     + " INTEGER NOT NULL, "
            + COL_NAME          + " TEXT, "
            + COL_ID_BOX        + " INTEGER, "
            + COL_URL           + " TEXT, "
            + COL_KEY           + " TEXT, "
            + COL_ETAT          + " TEXT, "
            + COL_UNIT          + " TEXT, "
            + COL_COLOR         + " TEXT DEFAULT 'FF000000', "
            + COL_MANUEL_UPDATE + " INTEGER DEFAULT 0,"
            + COL_LAST_VALUE    + " TEXT);";

    public static final String CREATE_RES_BDD = "CREATE TABLE IF NOT EXISTS " + TABLE_RESS_WIDGET + " ("
            + COL_ID        + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_RESS_NAME + " TEXT UNIQUE,"
            + COL_RESS_PATH + " TEXT UNIQUE)";

    public static final String CREATE_MUTLI_BDD = "CREATE TABLE IF NOT EXISTS " + TABLE_MUTLI_WIDGET + " ("
            + COL_ID           + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_ID_WIDGET    + " INTEGER NOT NULL, "
            + COL_NAME         + " TEXT, "
            + COL_ID_BOX       + " INTEGER, "
            + COL_URL          + " TEXT, "
            + COL_KEY          + " TEXT, "
            + COL_ETAT         + " TEXT, "
            + COL_ID_IMAGE_ON  + " INTEGER, "
            + COL_ID_IMAGE_OFF + " INTEGER, "
            + COL_TIME_OUT     + " INTEGER DEFAULT 2,"
            + COL_LAST_VALUE   + " TEXT);";

    public static final String CREATE_MUTLI_RESS_BDD = "CREATE TABLE IF NOT EXISTS " + TABLE_MUTLI_RESSOURCE + " ("
            + COL_ID           + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_ID_WIDGET    + " INTEGER NOT NULL CONSTRAINT fk_widget_mutli REFERENCES "+ TABLE_MUTLI_WIDGET + ", "
            + COL_NAME         + " TEXT, "
            + COL_ACTION       + " TEXT, "
            + COL_ID_IMAGE_ON  + " INTEGER, "
            + COL_ID_IMAGE_OFF + " INTEGER, "
            + COL_DEFAULT_MULTI_RESS + " INTEGER);";

    public static final String CREATE_GLOBAL_SETTING_BDD = "CREATE TABLE IF NOT EXISTS " + TABLE_GLOBAL_SETTING + " ("
            + COL_ID_BOX       + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_NAME         + " TEXT, "
            + COL_BOX_KEY      + " TEXT, "
            + COL_URL_INTERNE  + " TEXT, "
            + COL_URL_EXTERNE  + " TEXT, "
            + COL_TIME_OUT     + " INTEGER DEFAULT " + TIME_OUT + ", "
            + COL_COLOR        + " TEXT DEFAULT 'FFFFFFFF', "
            + COL_REFRESH_TIME + " INTEGER DEFAULT 0, "
            + COL_TEXT_SIZE    + " INTEGER DEFAULT 0);";

    public static final String CREATE_VOCAL_BDD = "CREATE TABLE IF NOT EXISTS " + TABLE_VOCAL_WIDGET + " ("
            + COL_ID              + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_ID_WIDGET       + " INTEGER NOT NULL, "
            + COL_NAME            + " TEXT,    "
            + COL_ID_BOX          + " INTEGER, "
            + COL_SYNTHESE_VOCAL  + " TEXT,    "
            + COL_KEYPHRASE       + " TEXT, "
            + COL_THRESHOLD_LEVEL + " INTEGER DEFAULT 0,"
            + COL_ID_IMAGE_ON     + " INTEGER);";

    public static final String CREATE_WEAR_BDD = "CREATE TABLE IF NOT EXISTS " + TABLE_DOMO_WEAR + " ("
            + COL_ID             + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_ID_BOX         + " INTEGER, "
            + COL_TIME_OUT       + " INTEGER DEFAULT 5, "
            + COL_SHAKE_TIME_OUT + " INTEGER DEFAULT 5, "
            + COL_SHAKE_LEVEL    + " INTEGER DEFAULT 5);";

    public static final String CREATE_SEEKBAR_BDD = "CREATE TABLE IF NOT EXISTS " + TABLE_SEEKBAR_WIDGET + " ("
            + COL_ID             + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_ID_WIDGET      + " INTEGER NOT NULL, "
            + COL_NAME           + " TEXT, "
            + COL_ID_BOX         + " INTEGER, "
            + COL_URL            + " TEXT, "
            + COL_KEY            + " TEXT, "
            + COL_ACTION         + " TEXT, "
            + COL_ETAT           + " TEXT, "
            + COL_ID_IMAGE_ON    + " INTEGER, "
            + COL_MIN            + " TEXT, "
            + COL_MAX            + " TEXT, "
            + COL_TIME_OUT       + " INTEGER DEFAULT 2,"
            + COL_COLOR          + " TEXT DEFAULT 'FF000000', "
            + COL_LAST_VALUE     + " TEXT);";

    public static final String CREATE_WEBCAM_BDD = "CREATE TABLE IF NOT EXISTS " + TABLE_WEBCAM_WIDGET + " ("
            + COL_ID             + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_ID_WIDGET      + " INTEGER NOT NULL, "
            + COL_ID_BOX         + " INTEGER, "
            + COL_NAME           + " TEXT, "
            + COL_URL            + " TEXT, "
            + COL_PORT           + " TEXT);";
}
