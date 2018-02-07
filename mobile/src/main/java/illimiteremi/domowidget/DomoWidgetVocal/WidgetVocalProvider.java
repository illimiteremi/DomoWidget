package illimiteremi.domowidget.DomoWidgetVocal;

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
import static illimiteremi.domowidget.DomoUtils.DomoConstants.UPDATE_ALL_VOCAL_WIDGET;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.UPDATE_WIDGET_VOCAL_ERROR;

/**
 * Implementation of App Widget functionality.
 */
public class WidgetVocalProvider extends AppWidgetProvider {

    private static final String TAG = "[DOMO_VOCAL_PROVIDER]";

    @Override
    public void onReceive(final Context context, final Intent intent) {

        // Récuperation de l'identifiant du Widget
        Bundle extras = intent.getExtras();
        int appWidgetId = extras == null ? 0 : extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID) ;

        RemoteViews remoteViews           = new RemoteViews(context.getPackageName(), R.layout.state_widget);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

        Log.d(TAG, "action = " + intent.getAction() + " - idWidget = " + appWidgetId);
        try {
            switch (intent.getAction()) {
                case UPDATE_ALL_VOCAL_WIDGET :
                    // Action mise à jour des widgets
                    ComponentName thisAppWidget = new ComponentName(context.getPackageName(), WidgetVocalProvider.class.getName());
                    int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);
                    onUpdate(context, appWidgetManager, appWidgetIds);
                    break;
                case APPWIDGET_UPDATE :
                    // Mise à jour du widget
                    if (appWidgetId != 0) {
                        updateAppWidget(context, appWidgetId, remoteViews, appWidgetManager);
                    }
                    break;
                case UPDATE_WIDGET_VOCAL_ERROR :
                    // Widget en erreur
                    if (appWidgetId != 0) {
                        new VocalUtils(context, appWidgetManager, remoteViews, appWidgetId).noData();
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
            VocalWidget widget = (VocalWidget) DomoUtils.getObjetById(context, new VocalWidget(context, appWidgetId));
            if (widget != null) {
                RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.vocal_widget);
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
            new VocalUtils(context, appWidgetManager, remoteViews, appWidgetId).updateWidgetView();
        } catch (Exception e) {
            Log.e(TAG, "Erreur : " + e);
        }
    }

}

