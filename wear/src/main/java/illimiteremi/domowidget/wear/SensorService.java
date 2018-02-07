package illimiteremi.domowidget.wear;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;

import static illimiteremi.domowidget.wear.WearConstants.COL_SHAKE_LEVEL;
import static illimiteremi.domowidget.wear.WearConstants.COL_SHAKE_TIME_OUT;
import static illimiteremi.domowidget.wear.WearConstants.DEFAULT_SHAKE_LEVEL;
import static illimiteremi.domowidget.wear.WearConstants.DEFAULT_SHAKE_TIMEOUT;

/**
 * Created by xzaq496 on 04/04/2017.
 */

public class SensorService extends Service implements SensorEventListener {

    private static final String   TAG      = "[DOMO_WEAR_SHAKE]";

    private Context       context;

    private long          lastUpdate;
    private Integer       shakeLevel;
    private Integer       shakeTimeOut;

    private SensorManager mSensorManager;
    private Sensor        mAccelerometer;

    private Vibrator      vibrator;

    @Override
    public void onCreate() {
        Log.d(TAG, "Démarrage du service SensorService...");
        context = getApplicationContext();

        shakeLevel   = DEFAULT_SHAKE_LEVEL;
        shakeTimeOut = DEFAULT_SHAKE_TIMEOUT;

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        // Lecteur des sharePrefs SHAKE LEVEL et TIME_OUT
        try {
            shakeLevel   = WearSharePreferences.readFromSharePreferences(context, COL_SHAKE_LEVEL);
            shakeTimeOut = WearSharePreferences.readFromSharePreferences(context, COL_SHAKE_TIME_OUT);
        } catch (Exception e) {
            shakeLevel   = DEFAULT_SHAKE_LEVEL;
            shakeTimeOut = DEFAULT_SHAKE_TIMEOUT;
        }

        // Shake level
        if (shakeLevel == 0) {
            Log.d(TAG, "Service STOP");
            stopForeground(true);
            stopSelf();
        }
        Log.d(TAG, "SHAKE LEVEL : " + shakeLevel + " - SHAKE TIME OUT : " + shakeTimeOut);
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Arret du service SensorService...");
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            getAccelerometer(sensorEvent);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * getAccelerometer
     * @param event
     */
    private void getAccelerometer(SensorEvent event) {
        // Mouvement
        float[] values = event.values;
        float x = values[0];
        float y = values[1];
        float z = values[2];

        float accelationSquareRoot = (x * x + y * y + z * z) / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
        long actualTime = event.timestamp;
        // Si niveau shake
        if (accelationSquareRoot >= (shakeLevel + 7)) {
            if (actualTime - lastUpdate < java.util.concurrent.TimeUnit.SECONDS.toNanos(shakeTimeOut)) {
                return;
            }
            Log.d(TAG, "shake shake shake !");

            // Vibration
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        vibrator =  (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                        vibrator.vibrate(100);
                    } catch (Exception e){
                        Log.e(TAG, "Erreur : " + e);
                    }
                }
            }).start();

            // start activity
            lastUpdate = actualTime;
            Intent intent = new Intent(context, WearActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }
}
