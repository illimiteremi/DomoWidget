package illimiteremi.domowidget.DomoWidgetSeekBar;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

import illimiteremi.domowidget.DomoUtils.DomoConstants;
import illimiteremi.domowidget.DomoUtils.DomoUtils;
import illimiteremi.domowidget.R;

import static illimiteremi.domowidget.DomoUtils.DomoConstants.ACTION_WIDGET_SEEKBAE_PUSH;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.APPWIDGET_UPDATE;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.UPDATE_ALL_WIDGET_SEEKBAR;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.UPDATE_WIDGET_SEEKBAR_ERROR;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.UPDATE_WIDGET_SEEKBAR_VALUE;

/**
 * Implementation of App Widget functionality.
 */
public class SeekBarWidgetProvider extends AppWidgetProvider {

    private static final String TAG = "[DOMO_SEEKBAR_PROVIDER]";

    @Override
    public void onReceive(final Context context, Intent intent) {

        // Récuperation de l'identifiant du Widget
        Bundle extras = intent.getExtras();
        final int appWidgetId = extras == null ? 0 : extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID) ;
        final int idRow       = extras == null ? 0 : extras.getInt(DomoConstants.POSITION_VIEW);

        final RemoteViews remoteViews           = new RemoteViews(context.getPackageName(), R.layout.seekbar_widget);
        final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

        Log.d(TAG, "action = " + intent.getAction() + " - idWidget = " + appWidgetId + " - idRow = " + idRow);

        try {
            switch (intent.getAction()) {
                case UPDATE_ALL_WIDGET_SEEKBAR :
                    // Action mise à jour des widgets
                    ComponentName thisAppWidget = new ComponentName(context.getPackageName(), SeekBarWidgetProvider.class.getName());
                    int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);
                    onUpdate(context, appWidgetManager, appWidgetIds);
                    break;
                case APPWIDGET_UPDATE :
                    // Mise à jour du widget
                    if (appWidgetId != 0) {
                        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.gridView);
                        updateAppWidget(context, appWidgetId, remoteViews, appWidgetManager);
                    }
                    break;
                case ACTION_WIDGET_SEEKBAE_PUSH :
                    // Valeur selection barre
                    if (appWidgetId != 0) {
                        new SeekBarUtils(context, appWidgetManager, remoteViews, appWidgetId).seekBarActionSetValue(idRow);
                    }
                    break;
                case UPDATE_WIDGET_SEEKBAR_VALUE :
                    if (appWidgetId != 0) {
                        String widgetValue = extras.getString("WIDGET_VALUE") == null ? "0" : extras.getString("WIDGET_VALUE");

                        // Mise à jour en BDD avec la valeur réel
                        SeekBarWidget widget = (SeekBarWidget) DomoUtils.getObjetById(context, new SeekBarWidget(context, appWidgetId));
                        widget.setDomoLastValue(String.valueOf(widgetValue));
                        DomoUtils.updateLastValue(context, widget);

                        // Rechargement des données de la barre
                        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.gridView);
                    }
                    break;
                case UPDATE_WIDGET_SEEKBAR_ERROR :
                    // Widget en erreur
                    if (appWidgetId != 0) {
                       new SeekBarUtils(context, appWidgetManager, remoteViews, appWidgetId).noData();
                    }
                    break;
                default:
                    // NOTHING
            }
        } catch (Exception e) {
            Log.e(TAG, "Erreur : " + e);
        }
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        for (int appWidgetId : appWidgetIds) {
            // Vérification de la configuration du widget
            SeekBarWidget widget = (SeekBarWidget) DomoUtils.getObjetById(context, new SeekBarWidget(context, appWidgetId));
            if (widget != null) {
                RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.seekbar_widget);
                updateAppWidget(context, appWidgetId, remoteViews, appWidgetManager);
                // Si retour d'etat
                if (!widget.getDomoState().isEmpty()) {
                    DomoUtils.requestToJeedom(context, widget.getSelectedBox(), widget, widget.getDomoState());
                }
            }
        }
    }

    /**
     * updateAppWidget
     * @param context
     * @param appWidgetId
     * @param remoteViews
     * @param appWidgetManager
     */
    private void updateAppWidget(Context context, int appWidgetId, RemoteViews remoteViews, AppWidgetManager appWidgetManager) {
        // Construction du Widget
        try {
            new SeekBarUtils(context, appWidgetManager, remoteViews, appWidgetId).updateWidgetView();
        } catch (Exception e) {
            Log.e(TAG, "Erreur : " + e);
        }
    }
}

