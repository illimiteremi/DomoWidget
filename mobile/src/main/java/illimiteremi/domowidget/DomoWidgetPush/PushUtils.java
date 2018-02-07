package illimiteremi.domowidget.DomoWidgetPush;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.RemoteViews;

import illimiteremi.domowidget.DomoGeneralSetting.BoxSetting;
import illimiteremi.domowidget.DomoUtils.DomoBitmapUtils;
import illimiteremi.domowidget.DomoUtils.DomoUtils;
import illimiteremi.domowidget.R;

import static illimiteremi.domowidget.DomoUtils.DomoConstants.LOCK_TIME_SEC;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.PUSH_TIME;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.UPDATE_WIDGET_PUSH_UNLOCK;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.UPDATE_WIDGET_PUSH_VALUE;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.WIDGET_ERROR;
import static illimiteremi.domowidget.DomoUtils.DomoUtils.getWidgetNameSize;

/**
 * Created by rcouturi on 28/06/2016.
 */
public class PushUtils {

    private static final String     TAG      = "[DOMO_PUSH_UTILS]";
    private final Context           context;

    private final PushWidget        widget;                     // Objet Widget
    private final RemoteViews       views;                      // Views du Widget
    private BoxSetting              selectedBox;                // Box selectionnée
    private Bitmap                  ressourceIdOn;              // Identifiant de la ressource image On
    private Bitmap                  ressourceIdOff;             // Identifiant de la ressource image Off
    private final AppWidgetManager  appWidgetManager;           // Widget Manager
    private int                     lockTime = LOCK_TIME_SEC;   // Temps affichage No_data
    private int                     pushTime = PUSH_TIME;       // Temps avant retour

    public PushUtils(Context context, AppWidgetManager appWidgetManager, RemoteViews views, int appWidgetId) {

        this.context          = context;
        this.appWidgetManager = appWidgetManager;
        this.views            = views;

        // Lecture du Widget en base
        widget = (PushWidget) DomoUtils.getObjetById(context, new PushWidget(context, appWidgetId));

        if (widget == null || widget.getSelectedBox() == null) {
            erreur(appWidgetId);
            throw new RuntimeException(WIDGET_ERROR);
        } else {
            selectedBox    = widget.getSelectedBox();
            DomoBitmapUtils bitmapUtils = new DomoBitmapUtils(context);
            ressourceIdOn  = bitmapUtils.getBitmapRessource(widget, true);
            ressourceIdOff = bitmapUtils.getBitmapRessource(widget, false);
        }
    }

    /**
     * updateWidgetView
     */
    public void updateWidgetView() {
        // Intent pour le changement d'état du Widget
        Intent pushIntent = new Intent(context, WidgetPushProvider.class);
        pushIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widget.getDomoId());
        pushIntent.setAction(UPDATE_WIDGET_PUSH_VALUE);
        PendingIntent pushPendingIntent = PendingIntent.getBroadcast(context, widget.getDomoId(), pushIntent, 0);
        views.setOnClickPendingIntent(R.id.widgetButton, pushPendingIntent);

        // Intent de déverrouillage
        pushIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widget.getDomoId());
        pushIntent.setAction(UPDATE_WIDGET_PUSH_UNLOCK);
        PendingIntent unlockPendingIntent = PendingIntent.getBroadcast(context, widget.getDomoId(), pushIntent, 0);
        views.setOnClickPendingIntent(R.id.unlockButton, unlockPendingIntent);

        // Nom du widget
        views.setTextViewText(R.id.widgetName, widget.getDomoName());
        views.setTextColor(R.id.widgetName, selectedBox.getWidgetNameColor());
        views.setTextViewTextSize(R.id.widgetName, TypedValue.COMPLEX_UNIT_PX,  getWidgetNameSize(context, selectedBox));
        views.setImageViewBitmap(R.id.widgetButton, ressourceIdOff);

        // Verrouillage
        if (widget.getDomoLock() == 0) {
            views.setViewVisibility(R.id.unlockButton, View.INVISIBLE);
        } else {
            views.setViewVisibility(R.id.unlockButton, View.VISIBLE);
        }

        appWidgetManager.updateAppWidget(widget.getDomoId(), views);
    }

    /**
     * Changement d'état du widget
     */
    public void pushAction() {
        // Envoi de requete à la box Domotique
        DomoUtils.requestToJeedom(context, selectedBox, widget, widget.getDomoAction());

        // Mise à jour du widget
        views.setImageViewBitmap(R.id.widgetButton, ressourceIdOn);
        appWidgetManager.updateAppWidget(widget.getDomoId(), views);

        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (pushTime > 0) {
                    pushTime--;
                    handler.postDelayed(this, 1000);
                } else {
                    // Mise à jour du widget
                    updateWidgetView();
                }
            }
        });
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
     * unLock
     */
    public void unLock() {
        try {
            views.setViewVisibility(R.id.unlockButton, View.INVISIBLE);
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
                        // Affichage du lock sur la nouvelle vue
                        views.setViewVisibility(R.id.unlockButton, View.VISIBLE);
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
