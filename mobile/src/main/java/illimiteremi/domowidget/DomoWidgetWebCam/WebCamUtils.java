package illimiteremi.domowidget.DomoWidgetWebCam;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.widget.RemoteViews;

import illimiteremi.domowidget.DomoGeneralSetting.BoxSetting;
import illimiteremi.domowidget.DomoUtils.DomoBitmapUtils;
import illimiteremi.domowidget.DomoUtils.DomoUtils;
import illimiteremi.domowidget.R;

import static illimiteremi.domowidget.DomoUtils.DomoConstants.APPWIDGET_UPDATE;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.LOCK_TIME_SEC;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.WIDGET_ERROR;
import static illimiteremi.domowidget.DomoUtils.DomoUtils.getWidgetNameSize;

/**
 * Created by rcouturi on 28/06/2016.
 */
public class WebCamUtils {

    private static final String     TAG      = "[DOMO_WEBCAM_UTILS]";
    private final Context           context;

    private final WebCamWidget      widget;                     // Objet Widget
    private final RemoteViews       views;                      // Views du Widget
    private BoxSetting              selectedBox;                // Box selectionnée
    private final AppWidgetManager  appWidgetManager;           // Widget Manager
    private       int               lockTime = LOCK_TIME_SEC;   // Temps affichage No_data

    public WebCamUtils(Context context, AppWidgetManager appWidgetManager, RemoteViews views, int appWidgetId) {

        this.context          = context;
        this.appWidgetManager = appWidgetManager;
        this.views            = views;

        // Lecture du Widget en base
        widget = (WebCamWidget) DomoUtils.getObjetById(context, new WebCamWidget(context, appWidgetId));

        if (widget == null) {
            erreur(appWidgetId);
            throw new RuntimeException(WIDGET_ERROR);
        } else {
            selectedBox   = widget.getSelectedBox();
            if (selectedBox == null) {
                erreur(appWidgetId) ;
            } else {
                Intent intent = new Intent(context, WidgetWebCamProvider.class);
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widget.getDomoId());
                intent.setAction(APPWIDGET_UPDATE);
                PendingIntent statePendingIntent = PendingIntent.getBroadcast(context, widget.getDomoId(), intent, 0);
                views.setOnClickPendingIntent(R.id.widgetButton, statePendingIntent);

                // Nom du widget
                views.setTextViewText(R.id.widgetName, widget.getDomoName());
                views.setTextColor(R.id.widgetName, selectedBox.getWidgetNameColor());
                views.setTextViewTextSize(R.id.widgetName, TypedValue.COMPLEX_UNIT_PX,  getWidgetNameSize(context, selectedBox));
                appWidgetManager.updateAppWidget(widget.getDomoId(), views);
            }
        }
    }

    /**
     * updateWidgetValue
     */
    public void updateWidgetPicture() {
        DomoBitmapUtils bitmapUtils = new DomoBitmapUtils(context);
        Bitmap capture = bitmapUtils.getBitmapRessource(widget, true);
        views.setImageViewBitmap(R.id.widgetButton, capture);
        // Mise à jour du widget
        appWidgetManager.updateAppWidget(widget.getDomoId(), views);
    }

    /**
     * checkWidgetValue
     */
    public void downloadWebCamPicture() {

        Log.d(TAG, "Demande de téléchargement Webcam : " + widget.getDomoName());
        // Envoi ordre à la box
        DomoUtils.requestToJeedom(context, selectedBox, widget, widget.getDomoUrl());
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
