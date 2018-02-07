package illimiteremi.domowidget.wear;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONObject;

import static android.content.Context.MODE_PRIVATE;
import static illimiteremi.domowidget.wear.WearConstants.COL_SHAKE_LEVEL;
import static illimiteremi.domowidget.wear.WearConstants.COL_SHAKE_TIME_OUT;
import static illimiteremi.domowidget.wear.WearConstants.COL_TIME_OUT;

/**
 * Created by xzaq496 on 07/04/2017.
 */

public class WearSharePreferences {

    private static final String TAG = "[DOMO_WEAR_SHAREPREF]";

    /**
     * Sauvegarde de la configuration en sharePref
     * @param context
     * @param jsonMessage
     */
    public static void sauveToSharePreferences(Context context, String jsonMessage) {

        Log.d(TAG, "sauveToSharePreferences : " + jsonMessage);
        try {
            JSONObject jsonWearSetting = new JSONObject(jsonMessage);
            int wearTimeOut = jsonWearSetting.getInt(COL_TIME_OUT);
            int shakeTimeOut = jsonWearSetting.getInt(COL_SHAKE_TIME_OUT);
            int shakeLevel = jsonWearSetting.getInt(COL_SHAKE_LEVEL);

            // Enregistrement dans les SharePref
            SharedPreferences prefs = context.getSharedPreferences("domowidget", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(COL_TIME_OUT, wearTimeOut);
            editor.putInt(COL_SHAKE_TIME_OUT, shakeTimeOut);
            editor.putInt(COL_SHAKE_LEVEL, shakeLevel);
            editor.commit();

        } catch (Exception e) {
            Log.e(TAG, "Erreur : " + e);
        }
    }

     /**
     * Lecture d'une Share Prefrerence
     * @param context
     * @param index
     * @return
     */
    public static int readFromSharePreferences(Context context, String index) {

        int iResult = 0;
        try {
            // Lecture dans les SharePref
            SharedPreferences prefs = context.getSharedPreferences("domowidget", MODE_PRIVATE);
            iResult = prefs.getInt(index, 5);
        } catch (Exception e) {
            Log.e(TAG, "Erreur : " + e);
        }
        Log.d(TAG, "readFromSharePreferences : " + index + " = " + iResult);
        return iResult;
    }


}
