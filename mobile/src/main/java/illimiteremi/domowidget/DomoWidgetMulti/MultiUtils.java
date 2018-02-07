package illimiteremi.domowidget.DomoWidgetMulti;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.widget.RemoteViews;

import java.text.Normalizer;

import illimiteremi.domowidget.DomoGeneralSetting.BoxSetting;
import illimiteremi.domowidget.DomoUtils.DomoBitmapUtils;
import illimiteremi.domowidget.DomoUtils.DomoConstants;
import illimiteremi.domowidget.DomoUtils.DomoUtils;
import illimiteremi.domowidget.R;

import static android.view.View.VISIBLE;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.ACTION_WIDGET_MULTI_RESS_PUSH;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.APPWIDGET_UPDATE;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.DEFAULT_TIMEOUT;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.LOCK_TIME_SEC;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.WIDGET_ERROR;

/**
 * Created by rcouturi on 28/06/2016.
 */
public class MultiUtils {

    private static final String             TAG      = "[DOMO_MULTI_UTILS]";

    private final        Context           context;

    private final        MultiWidget       widget;                      // Objet Widget
    private final        RemoteViews       views;                       // Views du Widget
    private              BoxSetting        selectedBox;                 // Box selectionnée
    private final        AppWidgetManager  appWidgetManager;            // Widget Manager
    private final        Boolean           retourEtat;                  // Bool de retour d'état

    private              Bitmap             widgetBitmap;               // Retour d'etat du widget
    private final        DomoBitmapUtils    bitmapUtils;                // Boite à outils graphique
    private              int                lockTime = LOCK_TIME_SEC;   // Temps affichage No_data
    private              int               timeOut;                     // Temps avant retour d'état

    public MultiUtils(Context context, AppWidgetManager  appWidgetManager,  RemoteViews views, Integer appWidgetId) {

        this.context          = context;
        this.appWidgetManager = appWidgetManager;
        this.views            = views;

        bitmapUtils           = new DomoBitmapUtils(context);

        widget = (MultiWidget) DomoUtils.getObjetById(context, new MultiWidget(context, appWidgetId));

        if (widget == null || widget.getSelectedBox() == null) {
            erreur(appWidgetId);
            throw new RuntimeException(WIDGET_ERROR);
        } else {
            selectedBox = widget.getSelectedBox();
            // Check si retour d'état
            retourEtat = !widget.getDomoState().isEmpty();

            // Intent Clique Action
            Intent templateIntent = new Intent(context, MultiWidgetProvider.class);
            templateIntent.setAction(ACTION_WIDGET_MULTI_RESS_PUSH);
            templateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widget.getDomoId());
            templateIntent.setData(Uri.parse(templateIntent.toUri(Intent.URI_INTENT_SCHEME)));
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, templateIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setPendingIntentTemplate(R.id.ressListView, pendingIntent);

            // Intent Refresh
            Intent intent = new Intent(context, MultiWidgetProvider.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,  widget.getDomoId());
            intent.setAction(APPWIDGET_UPDATE);
            PendingIntent statePendingIntent = PendingIntent.getBroadcast(context,  widget.getDomoId(), intent, 0);
            views.setOnClickPendingIntent(R.id.widgetButton, statePendingIntent);

            // Intent Remote Service
            Intent svcIntent = new Intent(context, MultiWidgetService.class);
            svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,  widget.getDomoId());
            svcIntent.setData(Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME)));
            views.setRemoteAdapter(R.id.ressListView, svcIntent);
            appWidgetManager.notifyAppWidgetViewDataChanged(widget.getDomoId(), R.id.ressListView);

            // Retour d'etat ou nom du widget
            String value = retourEtat ? widget.getDomoLastValue() : widget.getDomoName();
            widgetBitmap = bitmapUtils.setColorText(value.toUpperCase(), DomoConstants.DEFAULT_COLOR);
            views.setImageViewBitmap(R.id.widgetButton, widgetBitmap);

            // Check si le widget est vide
            if (widget.getMutliWidgetRess() == null) {
                views.setViewVisibility(R.id.retourEtat, VISIBLE);
                widgetBitmap = bitmapUtils.setColorText(context.getString(R.string.widget_empty), DomoConstants.DEFAULT_COLOR);
                views.setImageViewBitmap(R.id.widgetButton, widgetBitmap);
            }
            // Mise à jour de la view
            appWidgetManager.updateAppWidget(widget.getDomoId(), views);
        }
    }

    /**
     * checkWidgetValue
     */
    public void checkWidgetValue() {
        if (retourEtat) {
            // Envoi de requete RETOUR à la box Domotique
            String action = widget.getDomoState();
            DomoUtils.requestToJeedom(context, selectedBox, widget, action);
        }
    }

    /**
     * Action sur le widget
     * @param idRow
     */
    public void pushAction(int idRow) {

        // Récuperation de l'action
        MultiWidgetRess multiWidgetRess = widget.getMutliWidgetRess().get(idRow);
        String action = multiWidgetRess.getDomoAction();

        // Envoi de requete ACTION à la box Domotique
        DomoUtils.requestToJeedom(context, selectedBox, widget, action);

        // Attente si retour d'etat
        if (retourEtat) {
            Bitmap widgetBitmap = bitmapUtils.setColorText(context.getString(R.string.widget_busy), DomoConstants.DEFAULT_COLOR);
            views.setImageViewBitmap(R.id.widgetButton, widgetBitmap);
            appWidgetManager.updateAppWidget(widget.getDomoId(), views);

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
                        // Envoi de requete RETOUR à la box Domotique
                        String action = widget.getDomoState();
                        DomoUtils.requestToJeedom(context, selectedBox, widget, action);
                    }
                }
            });
        }
    }

    /**
     * Mise à jour de la valeur de retour
     * @param value
     */
    public void updateWidgetValue(String value) {
        // Enregistrement de la valeur en BDD
        value = Normalizer.normalize(value, Normalizer.Form.NFD);
        value = value.replaceAll("[^\\p{ASCII}]", "");
        widget.setDomoLastValue(value);
        DomoUtils.updateLastValue(context, widget);
        widgetBitmap = bitmapUtils.setColorText(value, DomoConstants.DEFAULT_COLOR);
        views.setImageViewBitmap(R.id.widgetButton, widgetBitmap);

        // Mise à jour du widget
        appWidgetManager.partiallyUpdateAppWidget(widget.getDomoId(), views);
    }

    /**
     * noData
     */
    public void noData() {
        try {
            widgetBitmap = bitmapUtils.setColorText(context.getString(R.string.error), DomoConstants.DEFAULT_COLOR);
            views.setImageViewBitmap(R.id.widgetButton, widgetBitmap);
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
                        updateWidgetValue(widget.getDomoLastValue());
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
            views.setViewVisibility(R.id.retourEtat, VISIBLE);
            widgetBitmap = bitmapUtils.setColorText(context.getString(R.string.widget_error), DomoConstants.DEFAULT_COLOR);
            views.setImageViewBitmap(R.id.widgetButton, widgetBitmap);
            // Mise à jour du widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        } catch (Exception e) {
            Log.e(TAG, "Erreur : " + e) ;
        }
    }

}
