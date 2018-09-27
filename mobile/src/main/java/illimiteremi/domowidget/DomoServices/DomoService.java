package illimiteremi.domowidget.DomoServices;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import illimiteremi.domowidget.DomoGeneralSetting.BoxSetting;
import illimiteremi.domowidget.DomoGeneralSetting.ManageActivity;
import illimiteremi.domowidget.DomoUtils.DomoConstants;
import illimiteremi.domowidget.DomoUtils.DomoUtils;
import illimiteremi.domowidget.DomoWidgetLocation.LocationWidget;
import illimiteremi.domowidget.DomoWidgetLocation.WidgetLocationProvider;
import illimiteremi.domowidget.R;

import static illimiteremi.domowidget.DomoUtils.DomoConstants.LOCATION;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.UPDATE_WIDGET_LOCATION_CHANGED;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.VOCAL;

public class DomoService extends Service {

    private static final String             TAG = "[DOMO_SERVICE]";

    private static final int NOTIFICATION_ID = 101;

    private Context                         context;
    private BroadcastReceiver               mReceiver;
    private LocationManager                 mLocationManager;
    private ArrayList<ListenerGPS>          listenerGPSs;

    private class ListenerGPS implements LocationListener {

        private final LocationWidget widget;

        ListenerGPS(LocationWidget widget) {
            this.widget = widget;
        }

        @Override
        public void onLocationChanged(Location location) {
            // Log.d(TAG, "onLocationChanged : Widget = " + widget.getDomoName());
            try {
                if (location != null) {
                    widget.setDomoLocation(location.getLatitude() + "," + location.getLongitude());
                    // Mise à jour du widgets GPS en BDD
                    DomoUtils.updateObjet(context, widget);

                    // Envoi de la requete à la box Domotique
                    BoxSetting boxSetting = widget.getSelectedBox();
                    widget.setDomoLocation(location.getLatitude() + "," + location.getLongitude());
                    DomoUtils.updateObjet(context, widget);
                    DomoUtils.requestToJeedom(context, boxSetting, widget, widget.getDomoAction() + "&value=" + widget.getDomoLocation());

                    // Mise à jour du widgets GPS si affiché sur le bureau
                    if (widget.getPresent()) {
                        Intent updateIntent = new Intent(getApplicationContext(), WidgetLocationProvider.class);
                        updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widget.getDomoId());
                        updateIntent.setAction(UPDATE_WIDGET_LOCATION_CHANGED);
                        sendBroadcast(updateIntent);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Erreur : " + e);
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // Log.d(TAG, "onStatusChanged : Widget = " + widget.getDomoName());
        }

        @Override
        public void onProviderEnabled(String provider) {
            // Log.d(TAG, "onProviderEnabled : Widget = " + widget.getDomoName());
        }

        @Override
        public void onProviderDisabled(String provider) {
            // Log.d(TAG, "onProviderDisabled : Widget = " + widget.getDomoName());
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (startId == 1) {
            Log.d(TAG, "Démarrage du service DOMO-WIDGET...");
        } else {
            Log.d(TAG, "Redémarrage du service DOMO-WIDGET...");
        }
        // Creation du receiver de maj des widgets
        createBroadcastReceiver();

        // Activation du service GPS
        boolean gpsService  = !DomoUtils.getAllObjet(context, LOCATION).isEmpty();
        Log.d(TAG, "Activation du service GPS : " + gpsService);
        if (gpsService) {
            createLocation();
        }
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.context = getApplicationContext();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Android 8.0 Background Execution Limits
            try {
                String id = "domoWidget_channel_01";
                CharSequence name = "Service DomoWidget";
                NotificationChannel mChannel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_NONE);
                mChannel.setShowBadge(false);
                mChannel.setLightColor(Color.GREEN);
                mChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

                NotificationManager service  = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                service.createNotificationChannel(mChannel);

                Intent notificationIntent = new Intent(context, ManageActivity.class);
                notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                PendingIntent pIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

                Notification notification =
                        new Notification.Builder(this, id)
                                .setSmallIcon(R.drawable.ic_domo_notification)
                                .setContentTitle(getString(R.string.domo_service))
                                .setCategory(Notification.CATEGORY_SERVICE)
                                .setContentIntent(pIntent)
                                .build();
                startForeground(NOTIFICATION_ID, notification);
            } catch (Exception e) {
                Log.e(TAG, "Erreur : " + e);
            }
        }
    }

    /**
     * Creation du service de location
     */
    private void createLocation() {
        // Gestion du service GPS
        try {
            ArrayList<Object> objects = DomoUtils.getAllObjet(context, LOCATION);
            ArrayList<LocationWidget> locationWidgets = new ArrayList<>();
            listenerGPSs = new ArrayList<>();
            for (Object locationObject : objects) {
                locationWidgets.add((LocationWidget) locationObject);
            }

            if (locationWidgets.size() != 0) {
                // Pour chaque Widget Location
                mLocationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
                for (final LocationWidget widget : locationWidgets) {
                    try {
                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            Log.d(TAG, "Problème de permission ACCESS_FINE_LOCATION !");
                            return;
                        } else {
                            if (!widget.getDomoProvider().contentEquals(DomoConstants.PROVIDER_TYPE.DISABLE.getProvider())) {
                                Log.d(TAG, "Construction du listener GPS : " + widget.getDomoName()
                                                                             + " / "    + widget.getDomoTimeOut()
                                                                             + "min / " + widget.getDomoDistance()
                                                                             + "m / "   + widget.getDomoProvider());
                                // ListenerGPS - TEMPS
                                if (!widget.getDomoTimeOut().equals(0)) {
                                    ListenerGPS timeListener = new ListenerGPS(widget);
                                    listenerGPSs.add(timeListener);
                                    mLocationManager.requestLocationUpdates(widget.getDomoProvider(), TimeUnit.MINUTES.toMillis(widget.getDomoTimeOut()), 0, timeListener);
                                }

                                // ListenerGPS - DISTANCE
                                if (!widget.getDomoDistance().equals(0)) {
                                    ListenerGPS distanceListener = new ListenerGPS(widget);
                                    listenerGPSs.add(distanceListener);
                                    mLocationManager.requestLocationUpdates(widget.getDomoProvider(), TimeUnit.SECONDS.toMillis(10), widget.getDomoDistance(), distanceListener);
                                }
                            } else {
                                Log.d(TAG, "Widget GPS : " + widget.getDomoName() + " désactivé..");
                            }
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Erreur : " + e);
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Erreur : " + e);
        }
    }


    /**
     * Création du BroadcastReceiver de Mise à jour du Widget
     */
    private void createBroadcastReceiver() {

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String action = intent.getAction();
                Log.d(TAG, "Action = " + action);

                Boolean voiceService = !DomoUtils.getAllObjet(context, VOCAL).isEmpty();
                Log.d(TAG, "Activation du service vocal : " + voiceService);

                if (!isInitialStickyBroadcast()) {
                    switch (action) {
                        case Intent.ACTION_SCREEN_ON :
                            if (voiceService) {
                               DomoUtils.startVoiceService(context, false);
                            }
                            DomoUtils.updateAllWidget(context);
                        case Intent.ACTION_SCREEN_OFF:
                            if (voiceService) {
                                DomoUtils.stopVoiceService(context);
                            }
                            break;
                        default:
                            // NOTHING
                    }
                }
            }
        };

        // Construction du receiver
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mReceiver, intentFilter);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Log.d(TAG, "onBind...");
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        // Log.d(TAG, "Service OnDestroy");
        // Log.d(TAG, "Nombre de Listener GPS à détruire : " + listenerGPSs.size());
        if (listenerGPSs != null) {
            if (listenerGPSs.size() != 0) {
                for (ListenerGPS listenerGPS : listenerGPSs) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "Problème de permission ACCESS_FINE_LOCATION !");
                        return;
                    } else {
                        mLocationManager.removeUpdates(listenerGPS);
                    }
                }
            }
        }

        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
        stopForeground(true);
        super.onDestroy();
    }

}
