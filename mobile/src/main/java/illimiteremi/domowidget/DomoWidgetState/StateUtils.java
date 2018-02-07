package illimiteremi.domowidget.DomoWidgetState;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.widget.RemoteViews;

import java.util.Objects;

import illimiteremi.domowidget.DomoGeneralSetting.BoxSetting;
import illimiteremi.domowidget.DomoUtils.DomoBitmapUtils;
import illimiteremi.domowidget.DomoUtils.DomoUtils;
import illimiteremi.domowidget.R;

import static illimiteremi.domowidget.DomoUtils.DomoConstants.APPWIDGET_UPDATE;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.DONE;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.ERROR;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.LOCK_TIME_SEC;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.NO_VALUE;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.WIDGET_ERROR;
import static illimiteremi.domowidget.DomoUtils.DomoUtils.getWidgetNameSize;

/**
 * Created by rcouturi on 28/06/2016.
 */
public class StateUtils {

    private static final String     TAG      = "[DOMO_STATE_UTILS]";
    private final Context           context;

    private final StateWidget       widget;                     // Objet Widget
    private final RemoteViews       views;                      // Views du Widget
    private       BoxSetting        selectedBox;                // Box selectionnée
    private final AppWidgetManager  appWidgetManager;           // Widget Manager
    private       Bitmap            widgetBitmap;               // Retour d'etat du widget
    private       int               lockTime = LOCK_TIME_SEC;   // Temps affichage No_data

    public StateUtils(Context context, AppWidgetManager appWidgetManager, RemoteViews views, int appWidgetId) {

        this.context          = context;
        this.appWidgetManager = appWidgetManager;
        this.views            = views;

        // Lecture du Widget en base
        widget = (StateWidget) DomoUtils.getObjetById(context, new StateWidget(context, appWidgetId));

        if (widget == null || widget.getSelectedBox() == null) {
            erreur(appWidgetId);
            throw new RuntimeException(WIDGET_ERROR);
        } else {
            selectedBox    = widget.getSelectedBox();
            Intent intent = new Intent(context, WidgetStateProvider.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widget.getDomoId());
            intent.setAction(APPWIDGET_UPDATE);
            PendingIntent statePendingIntent = PendingIntent.getBroadcast(context, widget.getDomoId(), intent, 0);
            views.setOnClickPendingIntent(R.id.widgetButton, statePendingIntent);

            // Nom du widget
            views.setTextViewText(R.id.widgetName, widget.getDomoName());
            views.setTextColor(R.id.widgetName, selectedBox.getWidgetNameColor());
            views.setTextViewTextSize(R.id.widgetName, TypedValue.COMPLEX_UNIT_PX,  getWidgetNameSize(context, selectedBox));
            appWidgetManager.updateAppWidget(widget.getDomoId(), views);

            // Affiche la dernière valeur connue
            updateWidgetValue(widget.getDomoLastValue());
        }
    }

    /**
     * updateWidgetValue
     * @param value         - Valeur à afficher
     */
    public void updateWidgetValue(String value) {

        // Enregistrement de la valeur en BDD
        widget.setDomoLastValue(value);
        DomoUtils.updateLastValue(context, widget);
        RemoteViews restoredView = views.clone();

        // Récuperation de l'information à la box
        DomoBitmapUtils bitmapUtils = new DomoBitmapUtils(context);
        // Récuperation de la taille du widget
        Bundle widgetOption = appWidgetManager.getAppWidgetOptions(widget.getDomoId());
        int widgetWidth = widgetOption.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH);
        bitmapUtils.setImageWidth(widgetWidth);
        if (selectedBox != null) {
            String widgetState = !Objects.equals(value, ERROR) || Objects.equals(value, DONE) ? value + widget.getDomoUnit() : NO_VALUE;
            widgetBitmap = bitmapUtils.setColorText(widgetState, widget.getDomoColor());
            views.setImageViewBitmap(R.id.widgetButton, widgetBitmap);
            //Log.d(TAG, "Etat du widget : " + widget.getDomoName() + " = " + widgetState);
        } else {
            widgetBitmap = bitmapUtils.setColorText(NO_VALUE, widget.getDomoColor());
            views.setImageViewBitmap(R.id.widgetButton, widgetBitmap);
        }
        // Mise à jour du widget
        // The total Bitmap memory used by the RemoteViews object cannot exceed that required to fill the screen 1.5 times, ie.
        // (screen width x screen height x 4 x 1.5) bytes.
        try {
            appWidgetManager.updateAppWidget(widget.getDomoId(), views);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Erreur : " + e);
            widgetBitmap = bitmapUtils.setColorText(context.getString(R.string.too_long), widget.getDomoColor());
            restoredView.setImageViewBitmap(R.id.widgetButton, widgetBitmap);
            appWidgetManager.updateAppWidget(widget.getDomoId(), restoredView);
        }
    }

    /**
     * checkWidgetValue
     */
    public void checkWidgetValue() {
            DomoUtils.requestToJeedom(context, selectedBox, widget, widget.getDomoState());
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
