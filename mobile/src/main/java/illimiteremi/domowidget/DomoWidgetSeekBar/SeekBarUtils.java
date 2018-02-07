package illimiteremi.domowidget.DomoWidgetSeekBar;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.widget.RemoteViews;

import illimiteremi.domowidget.DomoGeneralSetting.BoxSetting;
import illimiteremi.domowidget.DomoUtils.DomoBitmapUtils;
import illimiteremi.domowidget.DomoUtils.DomoUtils;
import illimiteremi.domowidget.R;

import static illimiteremi.domowidget.DomoUtils.DomoConstants.ACTION_WIDGET_SEEKBAE_PUSH;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.LOCK_TIME_SEC;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.WIDGET_ERROR;
import static illimiteremi.domowidget.DomoUtils.DomoUtils.getWidgetNameSize;

/**
 * Created by rcouturi on 28/06/2016.
 */
public class SeekBarUtils {

    private static final String     TAG      = "[DOMO_SEEKBAR_UTILS]";
    private final Context           context;

    private final int               appWidgetId;                // Identifiant du widget

    private final SeekBarWidget     widget;                     // Objet Widget
    private final RemoteViews       views;                      // Views du Widget
    private BoxSetting              selectedBox;                // Box selectionnée
    private Bitmap                  ressourceIdOn;              // Identifiant de la ressource image On
    private final AppWidgetManager  appWidgetManager;           // Widget Manager
    private       int               lockTime = LOCK_TIME_SEC;   // Temps affichage No_data

    public SeekBarUtils(Context context, AppWidgetManager appWidgetManager, RemoteViews views, int appWidgetId) {

        this.appWidgetId      = appWidgetId;
        this.context          = context;
        this.appWidgetManager = appWidgetManager;
        this.views            = views;

        // Lecture du Widget en base
        widget = (SeekBarWidget) DomoUtils.getObjetById(context, new SeekBarWidget(context, appWidgetId));

        if (widget == null || widget.getSelectedBox() == null) {
            erreur(appWidgetId);
            throw new RuntimeException(WIDGET_ERROR);
        } else {
            selectedBox    = widget.getSelectedBox();
            DomoBitmapUtils bitmapUtils = new DomoBitmapUtils(context);
            ressourceIdOn  = bitmapUtils.getBitmapRessource(widget, true);
        }
    }

    /**
     * updateWidgetView
     */
    public void updateWidgetView() {
        //Log.d(TAG, "updateWidgetView");

        // Intent Clique Action
        Intent templateIntent = new Intent(context, SeekBarWidgetProvider.class);
        templateIntent.setAction(ACTION_WIDGET_SEEKBAE_PUSH);
        templateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widget.getDomoId());
        templateIntent.setData(Uri.parse(templateIntent.toUri(Intent.URI_INTENT_SCHEME)));
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, templateIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setPendingIntentTemplate(R.id.gridView, pendingIntent);

        // Intent Remote Service
        Intent svcIntent = new Intent(context, SeekBarWidgetService.class);
        svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,  widget.getDomoId());
        svcIntent.setData(Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME)));
        views.setRemoteAdapter(R.id.gridView, svcIntent);
        appWidgetManager.notifyAppWidgetViewDataChanged(widget.getDomoId(), R.id.gridView);

        // Nom du widget
        views.setTextViewText(R.id.widgetName, widget.getDomoName());
        views.setTextColor(R.id.widgetName, selectedBox.getWidgetNameColor());
        views.setTextViewTextSize(R.id.widgetName, TypedValue.COMPLEX_UNIT_PX,  getWidgetNameSize(context, selectedBox));
        views.setImageViewBitmap(R.id.widgetImage, ressourceIdOn);

        // Mise à jour de la view
        appWidgetManager.updateAppWidget(widget.getDomoId(), views);
    }

    /**
     * seekBarActionGetValue
     * @param value
     * @return
     */
    public void seekBarActionSetValue(int value) {
        // Calcul de la valeur réelle suivant la position de la  barre
        double  seekBarValue;

        switch (value) {
            case 0 :
                seekBarValue = widget.getDomoMinValue();
                break;
            case 39 :
                seekBarValue = widget.getDomoMaxValue();
                break;
            default:
                seekBarValue = (value) * 2.5;
                seekBarValue = Math.round((seekBarValue * (widget.getDomoMaxValue() - widget.getDomoMinValue())) / 100) + widget.getDomoMinValue();
        }
        int valueToSend = (int) seekBarValue;
        Log.d(TAG, "Valeur : " + value + "/40 => " + valueToSend);

        // Mise à jour en BDD avec la valeur réel
        widget.setDomoLastValue(String.valueOf(valueToSend));
        DomoUtils.updateLastValue(context, widget);

        // Envoi de requete à la box Domotique
        DomoUtils.requestToJeedom(context, selectedBox, widget, widget.getDomoAction() + valueToSend);

        // Rechargement de la barre
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.gridView);
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
