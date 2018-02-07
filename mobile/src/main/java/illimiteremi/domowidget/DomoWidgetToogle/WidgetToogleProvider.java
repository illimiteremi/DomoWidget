package illimiteremi.domowidget.DomoWidgetToogle;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

import illimiteremi.domowidget.DomoUtils.DomoUtils;
import illimiteremi.domowidget.R;

import static illimiteremi.domowidget.DomoUtils.DomoConstants.APPWIDGET_UPDATE;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.NO_MATCH;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.UPDATE_ALL_WIDGET_TOOGLE;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.UPDATE_WIDGET_TOOGLE_CHANGE;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.UPDATE_WIDGET_TOOGLE_ERROR;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.UPDATE_WIDGET_TOOGLE_UNLOCK;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.UPDATE_WIDGET_TOOGLE_VALUE;

/**
 * Implementation of App Widget functionality.
 */
public class WidgetToogleProvider extends AppWidgetProvider {

    private static final String TAG = "[DOMO_TOOGLE_PROVIDER]";

    @Override
    public void onReceive(final Context context, Intent intent) {

        // Récuperation de l'identifiant du Widget
        Bundle extras = intent.getExtras();
        final int appWidgetId = extras == null ? 0 : extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID) ;

        final RemoteViews views                 = new RemoteViews(context.getPackageName(), R.layout.toogle_widget);
        final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

        Log.d(TAG, "action = " + intent.getAction() + " - idWidget = " + appWidgetId);
        try {
            switch (intent.getAction()) {
                case UPDATE_ALL_WIDGET_TOOGLE :
                    // Action mise à jour des widgets
                    ComponentName thisAppWidget = new ComponentName(context.getPackageName(), WidgetToogleProvider.class.getName());
                    int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);
                    onUpdate(context, appWidgetManager, appWidgetIds);
                    break;
                case APPWIDGET_UPDATE :
                    // Mise à jour du widget
                    if (appWidgetId != 0) {
                        updateAppWidget(context, appWidgetId, views, appWidgetManager);
                    }
                    break;
                case UPDATE_WIDGET_TOOGLE_VALUE :
                    // Affichage de la valeur
                    if (appWidgetId != 0) {
                        String toogleValue = extras.getString("WIDGET_VALUE") == null ? NO_MATCH : extras.getString("WIDGET_VALUE");
                        boolean widgetIsOn = !toogleValue.matches(NO_MATCH);

                        // Enregistrement de la nouvelle valeur en BDD
                        ToogleWidget widget = (ToogleWidget) DomoUtils.getObjetById(context, new ToogleWidget(context, appWidgetId));
                        widget.setDomoLastValue(String.valueOf(widgetIsOn));
                        DomoUtils.updateLastValue(context, widget);

                        new ToogleUtils(context, appWidgetManager, views, appWidgetId);
                    }
                    break;
                case UPDATE_WIDGET_TOOGLE_CHANGE :
                    // Action button
                    if (appWidgetId != 0) {
                        new ToogleUtils(context, appWidgetManager, views, appWidgetId).changeToogleState();
                    }
                    break;
                case UPDATE_WIDGET_TOOGLE_UNLOCK :
                    // Action déverrouillage
                    if (appWidgetId != 0) {
                        new ToogleUtils(context, appWidgetManager, views, appWidgetId).unLock();
                    }
                    break;
                case UPDATE_WIDGET_TOOGLE_ERROR :
                    // Widget en erreur
                    if (appWidgetId != 0) {
                        new ToogleUtils(context, appWidgetManager, views, appWidgetId).noData();
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
            ToogleWidget toogleWidget = (ToogleWidget) DomoUtils.getObjetById(context, new ToogleWidget(context, appWidgetId));
            if (toogleWidget != null) {
                RemoteViews views  = new RemoteViews(context.getPackageName(), R.layout.toogle_widget);
                updateAppWidget(context, appWidgetId, views, appWidgetManager);
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
            new ToogleUtils(context, appWidgetManager, remoteViews, appWidgetId).checkWidgetValue();
        } catch (Exception e) {
            Log.e(TAG, "Erreur : " + e);
        }
    }
}

