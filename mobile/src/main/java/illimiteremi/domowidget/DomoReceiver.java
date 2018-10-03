package illimiteremi.domowidget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Switch;

import java.util.ArrayList;

import illimiteremi.domowidget.DomoGeneralSetting.BoxSetting;
import illimiteremi.domowidget.DomoServices.DomoService;
import illimiteremi.domowidget.DomoUtils.DomoUtils;

import static illimiteremi.domowidget.DomoUtils.DomoConstants.BOX;

public class DomoReceiver extends BroadcastReceiver {

    private static final  String      TAG = "[DOMO_RECEIVER]";

    public DomoReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

       // <action android:name="android.intent.action.BOOT_COMPLETED" />//
       // <action android:name="android.intent.action.MY_PACKAGE_REPLACED" />
       Log.d(TAG, "Action = " + intent.getAction());

        switch(intent.getAction()) {
            case "android.intent.action.BOOT_COMPLETED" :
                // Démarrage du service GPS et update widget Boot et changement position GPS
                DomoUtils.startService(context, true);
                break;
            case "android.intent.action.MY_PACKAGE_REPLACED" :
                try {
                    ArrayList<Object> boxSettings = DomoUtils.getAllObjet(context, BOX);
                    if (boxSettings.size() != 0) {
                        BoxSetting boxSetting = (BoxSetting) boxSettings.get(0);
                        DomoUtils.getAllJeedomObjet(context, boxSetting);
                        DomoUtils.getAllJeedomCmd(context, boxSetting);
                        Log.d(TAG, "Mise à jour des données Jeedom : " + boxSetting.getBoxName());
                    }
                } catch (Exception e) {
                    Log.e(TAG, "onReceive: ", e);
                }
                DomoUtils.startService(context, false);
                break;
            default:
                // NOTHING
       }
    }
}
