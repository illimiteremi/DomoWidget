package illimiteremi.domowidget.wear;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class WearReceiver extends BroadcastReceiver {

    private static final String   TAG      = "[DOMO_WEAR_RECEIVER]";

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        // start service
        Log.d(TAG, "ONBOOT -> Démarrage du service SensorService...");
        Intent msgIntent = new Intent(context, SensorService.class);
        context.startService(msgIntent);
    }
}
