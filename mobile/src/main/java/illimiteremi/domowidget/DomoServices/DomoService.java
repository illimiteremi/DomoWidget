package illimiteremi.domowidget.DomoServices;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import illimiteremi.domowidget.DomoGeneralSetting.BoxSetting;
import illimiteremi.domowidget.DomoUtils.DomoConstants;
import illimiteremi.domowidget.DomoUtils.DomoUtils;
import illimiteremi.domowidget.DomoWidgetLocation.LocationWidget;
import illimiteremi.domowidget.DomoWidgetLocation.WidgetLocationProvider;
import illimiteremi.domowidget.R;

import static illimiteremi.domowidget.DomoUtils.DomoConstants.LOCATION;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.UPDATE_WIDGET_LOCATION_CHANGED;

public class DomoService extends Service {

    private static final String             TAG = "[DOMO_SERVICE]";

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

    /**
     * Constructeur
     */
    public DomoService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (startId == 1) {
            Log.d(TAG, "Démarrage du service DOMO-WIDGET...");
            if (Build.VERSION.SDK_INT >= 26) {
                // Android 8.0 Background Execution Limits
                NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                String id = "domoWidget_channel_01";
                CharSequence name = "Service DomoWidget";
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel mChannel = new NotificationChannel(id, name, importance);
                mChannel.setShowBadge(true);
                mNotificationManager.createNotificationChannel(mChannel);
                Notification notification = new Notification.Builder(this, id)
                        .setSmallIcon(R.drawable.ic_domo_notification)
                        .setContentTitle(getString(R.string.domo_service))
                        .build();
                startForeground(1, notification);
            }
        } else {
            Log.d(TAG, "Redémarrage du service DOMO-WIDGET...");
        }
        // Creation du receiver de maj des widgets
        createBroadcastReceiver();
        createLocation();
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        this.context = getApplicationContext();
        super.onCreate();
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

                if (!isInitialStickyBroadcast()) {
                    switch (action) {
                        case Intent.ACTION_SCREEN_ON :
                            DomoUtils.startVoiceService(context, false);
                        case "android.net.conn.CONNECTIVITY_CHANGE":
                            DomoUtils.updateAllWidget(context);
                            break;
                        case Intent.ACTION_SCREEN_OFF:
                            DomoUtils.stopVoiceService(context);
                            break;
                        default:
                            // NOTHING
                    }
                }
            }
        };

        // Construction du receiver
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
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
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }

        stopForeground(true);

        super.onDestroy();
    }
}
