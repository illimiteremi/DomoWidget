package illimiteremi.domowidget.DomoWidgetToogle;


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

import static illimiteremi.domowidget.DomoUtils.DomoConstants.COMMANDE;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.DEFAULT_TIMEOUT;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.LOCK_TIME_SEC;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.UPDATE_WIDGET_TOOGLE_CHANGE;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.UPDATE_WIDGET_TOOGLE_UNLOCK;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.WIDGET_ERROR;
import static illimiteremi.domowidget.DomoUtils.DomoUtils.getWidgetNameSize;

/**
 * Created by rcouturi on 28/06/2016.
 */
public class ToogleUtils {

    private static final String     TAG       = "[DOMO_TOOGLE_UTILS]";
    private final Context           context;

    private final ToogleWidget      widget;                     // Objet Widget
    private RemoteViews             views;                      // Views du Widget
    private BoxSetting              selectedBox;                // Box selectionnée
    private Bitmap                  ressourceIdOn;              // Identifiant de la ressource image On
    private Bitmap                  ressourceIdOff;             // Identifiant de la ressource image Off
    private final AppWidgetManager  appWidgetManager;           // Widget Manager
    private int                     lockTime = LOCK_TIME_SEC;   // Temps affichage No_data
    private int                     timeOut;                    // Temps avant retour d'état

    public ToogleUtils(Context context, AppWidgetManager appWidgetManager, RemoteViews views, int appWidgetId) {

        this.context          = context;
        this.appWidgetManager = appWidgetManager;
        this.views            = views;

        // Lecture du Widget en base
        widget = (ToogleWidget) DomoUtils.getObjetById(context, new ToogleWidget(context, appWidgetId));

        if (widget == null || widget.getSelectedBox() == null) {
            erreur(appWidgetId);
            throw new RuntimeException(WIDGET_ERROR);
        } else {
            selectedBox = widget.getSelectedBox();
            DomoBitmapUtils bitmapUtils = new DomoBitmapUtils(context);
            ressourceIdOn  = bitmapUtils.getBitmapRessource(widget, true);
            ressourceIdOff = bitmapUtils.getBitmapRessource(widget, false);

            // Intent pour le changement d'état du Widget
            Intent toogleIntent = new Intent(context, WidgetToogleProvider.class);
            toogleIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widget.getDomoId());
            toogleIntent.setAction(UPDATE_WIDGET_TOOGLE_CHANGE);
            PendingIntent tooglePendingIntent = PendingIntent.getBroadcast(context, widget.getDomoId(), toogleIntent, 0);
            views.setOnClickPendingIntent(R.id.widgetButton, tooglePendingIntent);

            // Intent de déverrouillage
            toogleIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widget.getDomoId());
            toogleIntent.setAction(UPDATE_WIDGET_TOOGLE_UNLOCK);
            PendingIntent unlookPendingIntent = PendingIntent.getBroadcast(context, widget.getDomoId(), toogleIntent, 0);
            views.setOnClickPendingIntent(R.id.unlockButton, unlookPendingIntent);

            // Nom du widget
            views.setTextViewText(R.id.widgetName, widget.getDomoName());
            views.setTextColor(R.id.widgetName, selectedBox.getWidgetNameColor());
            views.setTextViewTextSize(R.id.widgetName, TypedValue.COMPLEX_UNIT_PX, getWidgetNameSize(context, selectedBox));
            appWidgetManager.updateAppWidget(widget.getDomoId(), views);

            // Récuperation de la dernière valeur connue
            updateWidgetValue(Boolean.valueOf(widget.getDomoLastValue()));
        }
    }

    /**
     * updateWidgetValue
     * @param isOn      - Etat du toogle On/Off
     */
    private void updateWidgetValue(Boolean isOn) {
        // Check retour d'etat
        if (isOn) {
            views.setImageViewBitmap(R.id.widgetButton, ressourceIdOn);
        } else {
            views.setImageViewBitmap(R.id.widgetButton, ressourceIdOff);
        }

        // Si verrouillage du widget
        if (widget.getDomoLock() == 0) {
            views.setViewVisibility(R.id.unlockButton, View.INVISIBLE);
        } else {
            views.setViewVisibility(R.id.unlockButton, View.VISIBLE);
        }

        // Mise à jour du widget
        appWidgetManager.updateAppWidget(widget.getDomoId(), views);
    }

    /**
     * checkWidgetValue
     */
    public void checkWidgetValue() {
        DomoUtils.requestToJeedom(context, selectedBox, widget, widget.getDomoState());
    }

    /**
     * Changement d'état du widget
     */
    public void changeToogleState() {

        if (widget.getDomoOn().equals(COMMANDE) || widget.getDomoOff().equals(COMMANDE)) {
            Log.d(TAG, "Pas d'action ! Widget en mode info...");
            checkWidgetValue();
        } else {
            Boolean state = Boolean.valueOf(widget.getDomoLastValue());
            Log.d(TAG, "Valeur connue du widget avant changement d'état = " + state);
            Log.d(TAG, "Action : " + state + " => " + !state);
            String widgetAction;
            if (state) {
                views.setImageViewBitmap(R.id.widgetButton, ressourceIdOff);
                widgetAction = widget.getDomoOff();
            } else {
                views.setImageViewBitmap(R.id.widgetButton, ressourceIdOn);
                widgetAction = widget.getDomoOn();
            }

            // Mise à jour du widget
            appWidgetManager.updateAppWidget(widget.getDomoId(), views);

            // Envoi ordre à la box
            DomoUtils.requestToJeedom(context, selectedBox, widget, widgetAction);

            // Attente avant retour d'etat
            timeOut = (widget.getDomoTimeOut() == null ? DEFAULT_TIMEOUT : widget.getDomoTimeOut());
            final Handler handler = new Handler();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (timeOut > 0) {
                        timeOut--;
                        handler.postDelayed(this, 1000);
                    } else {
                        // Fin TIMER
                        // Mise à jour du widget
                        Log.d(TAG, "Fin d'attente avant lecture état !");
                        checkWidgetValue();
                    }
                }
            });
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
                        views  = new RemoteViews(context.getPackageName(), R.layout.toogle_widget);
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
            views.setTextViewTextSize(R.id.widgetName, TypedValue.COMPLEX_UNIT_PX, getWidgetNameSize(context, selectedBox));
            views.setTextViewText(R.id.widgetName, context.getString(R.string.widget_error));
            views.setImageViewResource(R.id.widgetButton, R.drawable.no_data);
            // Mise à jour du widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        } catch (Exception e) {
            Log.e(TAG, "Erreur : " + e) ;
        }
    }
}
