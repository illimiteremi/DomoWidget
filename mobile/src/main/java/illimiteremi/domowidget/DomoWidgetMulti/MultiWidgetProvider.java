package illimiteremi.domowidget.DomoWidgetMulti;

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

import static illimiteremi.domowidget.DomoUtils.DomoConstants.ACTION_WIDGET_MULTI_RESS_PUSH;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.APPWIDGET_UPDATE;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.NO_VALUE;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.UPDATE_ALL_MULTI_WIDGET;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.UPDATE_WIDGET_MULTI_ERROR;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.UPDATE_WIDGET_MULTI_VALUE;

/**
 * Implementation of App Widget functionality.
 */
public class MultiWidgetProvider extends AppWidgetProvider {

    private static final String TAG         = "[DOMO_MULTI_PROVIDER]";


    @Override
    public void onReceive(final Context context, Intent intent) {

        // Récuperation de l'identifiant du Widget
        Bundle extras = intent.getExtras();
        final int appWidgetId = extras == null ? 0 : extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID) ;
        final int idRow       = extras == null ? 0 : extras.getInt(DomoConstants.POSITION_VIEW);

        final RemoteViews remoteViews           = new RemoteViews(context.getPackageName(), R.layout.multi_widget);
        final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

        Log.d(TAG, "action = " + intent.getAction() + " - idWidget = " + appWidgetId + " - idRow = " + idRow);

        try {
            switch (intent.getAction()) {
                case UPDATE_ALL_MULTI_WIDGET :
                    // Action mise à jour des widgets
                    ComponentName thisAppWidget = new ComponentName(context.getPackageName(), MultiWidgetProvider.class.getName());
                    int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);
                    onUpdate(context, appWidgetManager, appWidgetIds);
                    break;
                case APPWIDGET_UPDATE :
                    // Mise à jour du widget
                    if (appWidgetId != 0) {
                        updateAppWidget(context, appWidgetId, remoteViews, appWidgetManager);
                    }
                    break;
                case UPDATE_WIDGET_MULTI_VALUE :
                    // Mise à jour du widget
                    if (appWidgetId != 0) {
                        String widgetValue = extras.getString("WIDGET_VALUE") == null ? NO_VALUE : extras.getString("WIDGET_VALUE");
                        new MultiUtils(context, appWidgetManager, remoteViews, appWidgetId).updateWidgetValue(widgetValue);
                    }
                    break;
                case ACTION_WIDGET_MULTI_RESS_PUSH :
                    // Action sur une action du widget
                    if (appWidgetId != 0) {
                        new MultiUtils(context, appWidgetManager, remoteViews, appWidgetId).pushAction(idRow);
                    }
                    break;
                case UPDATE_WIDGET_MULTI_ERROR :
                    // Widget en erreur
                    if (appWidgetId != 0) {
                        new MultiUtils(context, appWidgetManager, remoteViews, appWidgetId).noData();
                    }
                    break;
                default :
                    // NOTHING
            }
        } catch (Exception e) {
            Log.e(TAG, "Erreur - onReceive : " + e);
        }
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        for (int appWidgetId : appWidgetIds) {
            // Vérification de la configuration du widget
            MultiWidget widget = (MultiWidget) DomoUtils.getObjetById(context, new MultiWidget(context, appWidgetId));
            if (widget != null) {
                RemoteViews views  = new RemoteViews(context.getPackageName(), R.layout.multi_widget);
                updateAppWidget(context, appWidgetId, views, appWidgetManager);
            }
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
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
            new MultiUtils(context, appWidgetManager, remoteViews, appWidgetId).checkWidgetValue();
        } catch (Exception e) {
            Log.e(TAG, "Erreur - updateAppWidget : " + e);
        }
    }
}

