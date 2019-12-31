package illimiteremi.domowidget.DomoWidgetLocation;

import android.Manifest;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import androidx.core.app.ActivityCompat;
import android.util.Log;
import android.util.TypedValue;
import android.widget.RemoteViews;

import illimiteremi.domowidget.DomoGeneralSetting.BoxSetting;
import illimiteremi.domowidget.DomoUtils.DomoUtils;
import illimiteremi.domowidget.R;

import static illimiteremi.domowidget.DomoUtils.DomoConstants.GPS_TIME_OUT;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.LOCK_TIME_SEC;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.WIDGET_ERROR;
import static illimiteremi.domowidget.DomoUtils.DomoUtils.getWidgetNameSize;

/**
 * Created by rcouturi on 28/06/2016.
 */
public class LocationUtils {

    private static final String TAG = "[DOMO_GPS_UTILS]";

    private final Context           context;

    private final LocationWidget    widget;                     // Objet Widget
    private final RemoteViews       views;                      // Views du Widget
    private       BoxSetting        selectedBox;                // Box selectionnée
    private final AppWidgetManager  appWidgetManager;           // Widget Manager
    private       Bitmap            widgetBitmap;               // Retour d'etat du widget
    private       int               lockTime = LOCK_TIME_SEC;   // Temps affichage No_data
    private       int               timeOut = GPS_TIME_OUT;     // Temps avant retour d'état

    public LocationUtils(Context context, AppWidgetManager  appWidgetManager, RemoteViews views, Integer appWidgetId) {

        this.context          = context;
        this.appWidgetManager = appWidgetManager;
        this.views            = views;

        widget = (LocationWidget) DomoUtils.getObjetById(context, new LocationWidget(context, appWidgetId));

        if (widget == null || widget.getSelectedBox() == null) {
            erreur(appWidgetId);
            throw new RuntimeException(WIDGET_ERROR);
        } else {
            selectedBox = widget.getSelectedBox();
        }
    }

    /**
     * Mise à jour de la wiew du widget
     */
    public void updateWidgetView() {
        try {
            // Intent pour la mise à jour de la possition
            Intent intent = new Intent(context, WidgetLocationProvider.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widget.getDomoId());
            intent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
            PendingIntent locationPendingIntent = PendingIntent.getBroadcast(context, widget.getDomoId(), intent, 0);
            views.setOnClickPendingIntent(R.id.widgetButton, locationPendingIntent);

            // Image du GPS
            int ressourceId = context.getResources().getIdentifier("ic_gps_green", "drawable", context.getPackageName());
            widgetBitmap = BitmapFactory.decodeResource(context.getResources(), ressourceId);
            views.setImageViewBitmap(R.id.widgetButton, widgetBitmap);

            // Nom du widget
            views.setTextViewText(R.id.widgetName, widget.getDomoName());
            views.setTextColor(R.id.widgetName, selectedBox.getWidgetNameColor());
            views.setTextViewTextSize(R.id.widgetName, TypedValue.COMPLEX_UNIT_PX,  getWidgetNameSize(context, selectedBox));

            // Mise à jour du widget
            appWidgetManager.updateAppWidget(widget.getDomoId(), views);

            // Envoi de l'information à la box
            if (selectedBox != null) {
                LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                // get the last know location from your location manager.
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                // Enregistrement de la postion en bdd (si disponible)
                Location location = locationManager.getLastKnownLocation(widget.getDomoProvider());
                if (location != null) {
                    Log.d(TAG, "Widget : " + widget.getDomoName() + " - getLastKnownLocation "  + location.getLongitude() + " / " + location.getAltitude());
                    widget.setDomoLocation(location.getLatitude() + "," + location.getLongitude());
                    DomoUtils.updateObjet(context, widget);
                    // Envoi de requete à la box Domotique
                    DomoUtils.requestToJeedom(context, selectedBox, widget, widget.getDomoAction() + "&value=" + widget.getDomoLocation());
                }
            }

            final Handler handler = new Handler();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (timeOut > 0) {
                        timeOut--;
                        handler.postDelayed(this, 1000);
                    } else {
                        // Fin TIMER
                        // Image GPS Blanc
                        int ressourceId = context.getResources().getIdentifier("ic_gps_white", "drawable",  context.getPackageName());
                        widgetBitmap = BitmapFactory.decodeResource(context.getResources(), ressourceId);
                        views.setImageViewBitmap(R.id.widgetButton, widgetBitmap);
                        // Mise à jour du widget
                        appWidgetManager.updateAppWidget(widget.getDomoId(), views);
                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Erreur : " + e) ;
        }
    }

    /**
     * noData
     */
    public void noData() {
        try {
            views.setTextColor(R.id.widgetName, Color.RED);
            appWidgetManager.updateAppWidget(widget.getDomoId(), views);

            final Handler handler = new Handler();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (lockTime > 0) {
                        lockTime--;
                        handler.postDelayed(this, 1000);
                    } else {
                        // Fin TIMER
                        views.setTextColor(R.id.widgetName, selectedBox.getWidgetNameColor());
                        appWidgetManager.partiallyUpdateAppWidget(widget.getDomoId(), views);
                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Erreur : " + e) ;
        }
    }

    /**
     * Widget en erreur
     */
    private void erreur(int appWidgetId) {
        try {
            views.setTextColor(R.id.widgetName, Color.RED);
            views.setTextViewTextSize(R.id.widgetName, TypedValue.COMPLEX_UNIT_PX,  getWidgetNameSize(context, selectedBox));
            views.setTextViewText(R.id.widgetName, context.getString(R.string.widget_error));
            views.setImageViewResource(R.id.widgetButton, R.drawable.no_data);
            // Mise à jour du widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        } catch (Exception e) {
            Log.e(TAG, "Erreur : " + e) ;
        }
    }
}
