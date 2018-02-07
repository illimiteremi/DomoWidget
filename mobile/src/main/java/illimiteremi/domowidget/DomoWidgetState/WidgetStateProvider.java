package illimiteremi.domowidget.DomoWidgetState;

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
import static illimiteremi.domowidget.DomoUtils.DomoConstants.NO_VALUE;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.UPDATE_ALL_WIDGET_STATE;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.UPDATE_WIDGET_STATE_ERROR;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.UPDATE_WIDGET_STATE_VALUE;

/**
 * Implementation of App Widget functionality.
 */
public class WidgetStateProvider extends AppWidgetProvider {

    private static final String TAG = "[DOMO_STATE_PROVIDER]";

    @Override
    public void onReceive(Context context, Intent intent) {

        // Récuperation de l'identifiant du Widget
        Bundle extras = intent.getExtras();
        int appWidgetId = extras == null ? 0 : extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID) ;

        RemoteViews remoteViews           = new RemoteViews(context.getPackageName(), R.layout.state_widget);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

        Log.d(TAG, "action = " + intent.getAction() + " - idWidget = " + appWidgetId);

        try {
            switch (intent.getAction()) {
                case UPDATE_ALL_WIDGET_STATE :
                    // Création de la liste des widgets à mettre à jour.
                    ComponentName thisAppWidget = new ComponentName(context.getPackageName(), WidgetStateProvider.class.getName());
                    int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);

                    for (int widgetId : appWidgetIds) {
                        StateWidget stateWidget = new StateWidget(context, widgetId);
                        stateWidget = (StateWidget) DomoUtils.getObjetById(context, stateWidget);
                        if (stateWidget != null) {
                            if (stateWidget.getManuelUpdate().equals(0)) {
                                updateAppWidget(context, widgetId, remoteViews, appWidgetManager);
                            }
                        } else {
                            Log.e(TAG, "Identifiant widget non présent en BDD !");
                        }
                    }
                    break;
                case APPWIDGET_UPDATE :
                    // Mise à jour du widget
                    if (appWidgetId != 0) {
                        updateAppWidget(context, appWidgetId, remoteViews, appWidgetManager);
                    }
                    break;
                case UPDATE_WIDGET_STATE_VALUE :
                    // Affichage de la valeur
                    if (appWidgetId != 0) {
                        String widgetValue = extras.getString("WIDGET_VALUE") == null ? NO_VALUE : extras.getString("WIDGET_VALUE");
                        new StateUtils(context, appWidgetManager, remoteViews, appWidgetId).updateWidgetValue(widgetValue);
                    }
                    break;
                case UPDATE_WIDGET_STATE_ERROR :
                    // Widget en erreur
                    if (appWidgetId != 0) {
                        new StateUtils(context, appWidgetManager, remoteViews, appWidgetId).noData();
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
    public  void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        for (int appWidgetId : appWidgetIds) {
            // Vérification de la configuration du widget
            StateWidget widget = (StateWidget) DomoUtils.getObjetById(context, new StateWidget(context, appWidgetId));
            if (widget != null) {
                RemoteViews remoteViews  = new RemoteViews(context.getPackageName(), R.layout.state_widget);
                updateAppWidget(context, appWidgetId, remoteViews, appWidgetManager);
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
            new StateUtils(context, appWidgetManager, remoteViews, appWidgetId).checkWidgetValue();
        } catch (Exception e) {
            Log.e(TAG, "Erreur : " + e);
        }
    }
}

