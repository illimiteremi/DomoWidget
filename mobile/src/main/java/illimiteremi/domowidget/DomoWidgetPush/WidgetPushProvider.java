package illimiteremi.domowidget.DomoWidgetPush;

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
import static illimiteremi.domowidget.DomoUtils.DomoConstants.UPDATE_ALL_WIDGET_PUSH;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.UPDATE_WIDGET_PUSH_ERROR;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.UPDATE_WIDGET_PUSH_UNLOCK;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.UPDATE_WIDGET_PUSH_VALUE;

/**
 * Implementation of App Widget functionality.
 */
public class WidgetPushProvider extends AppWidgetProvider {

    private static final String TAG = "[DOMO_PUSH_PROVIDER]";

    @Override
    public void onReceive(final Context context, Intent intent) {

        // Récuperation de l'identifiant du Widget
        Bundle extras = intent.getExtras();
        final int appWidgetId = extras == null ? 0 : extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID) ;

        final RemoteViews remoteViews           = new RemoteViews(context.getPackageName(), R.layout.push_widget);
        final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

        Log.d(TAG, "action = " + intent.getAction() + " - idWidget = " + appWidgetId);

        try {
            switch (intent.getAction()) {
                case UPDATE_ALL_WIDGET_PUSH :
                    // Action mise à jour des widgets
                    ComponentName thisAppWidget = new ComponentName(context.getPackageName(), WidgetPushProvider.class.getName());
                    int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);
                    onUpdate(context, appWidgetManager, appWidgetIds);
                    break;
                case APPWIDGET_UPDATE :
                    // Action button
                    if (appWidgetId != 0) {
                        updateAppWidget(context, appWidgetId, remoteViews, appWidgetManager);
                    }
                    break;
                case UPDATE_WIDGET_PUSH_VALUE :
                    if (appWidgetId != 0) {
                        new PushUtils(context, appWidgetManager, remoteViews, appWidgetId).pushAction();
                    }
                    break;
                case UPDATE_WIDGET_PUSH_UNLOCK :
                    // Action déverrouillage
                    if (appWidgetId != 0) {
                        new PushUtils(context, appWidgetManager, remoteViews, appWidgetId).unLock();
                    }
                    break;
                case UPDATE_WIDGET_PUSH_ERROR :
                    // Widget en erreur
                    if (appWidgetId != 0) {
                        new PushUtils(context, appWidgetManager, remoteViews, appWidgetId).noData();
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
            PushWidget widget = (PushWidget) DomoUtils.getObjetById(context, new PushWidget(context, appWidgetId));
            if (widget != null) {
                RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.push_widget);
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
            new PushUtils(context, appWidgetManager, remoteViews, appWidgetId).updateWidgetView();
        } catch (Exception e) {
            Log.e(TAG, "Erreur : " + e);
        }
    }
}

