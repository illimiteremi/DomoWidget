package illimiteremi.domowidget.DomoWidgetWebCam;

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
import static illimiteremi.domowidget.DomoUtils.DomoConstants.UPDATE_ALL_WIDGET_WEBCAM;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.UPDATE_WIDGET_WEBCAM_ERROR;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.UPDATE_WIDGET_WEBCAM_VALUE;


/**
 * Implementation of App Widget functionality.
 */
public class WidgetWebCamProvider extends AppWidgetProvider {

    private static final String TAG = "[DOMO_WEBCAM_PROVIDER]";

    @Override
    public void onReceive(final Context context, Intent intent) {

        // Récuperation de l'identifiant du Widget
        Bundle extras = intent.getExtras();
        final int appWidgetId = extras == null ? 0 : extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID) ;

        final RemoteViews remoteViews           = new RemoteViews(context.getPackageName(), R.layout.webcam_widget);
        final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

        Log.d(TAG, "action = " + intent.getAction() + " - idWidget = " + appWidgetId);

        try {
            switch (intent.getAction()) {
                case UPDATE_ALL_WIDGET_WEBCAM :
                    // Création de la liste des widgets à mettre à jour.
                    ComponentName thisAppWidget = new ComponentName(context.getPackageName(), WidgetWebCamProvider.class.getName());
                    int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);

                    for (int widgetId : appWidgetIds) {
                        WebCamWidget webCamWidget = new WebCamWidget(context, widgetId);
                        webCamWidget = (WebCamWidget) DomoUtils.getObjetById(context, webCamWidget);
                        if (webCamWidget != null) {
                            updateAppWidget(context, widgetId, remoteViews, appWidgetManager);
                        } else {
                            Log.e(TAG, "Identifiant widget non présent en BDD !");
                        }
                    }
                    break;
                case APPWIDGET_UPDATE :
                    // Mise à jour du widget
                    if (appWidgetId != 0) {
                        new WebCamUtils(context, appWidgetManager, remoteViews, appWidgetId).downloadWebCamPicture();
                    }
                    break;
                case UPDATE_WIDGET_WEBCAM_VALUE :
                    // Affichage de la valeur
                    if (appWidgetId != 0) {
                        new WebCamUtils(context, appWidgetManager, remoteViews, appWidgetId).updateWidgetPicture();
                    }
                    break;
                case UPDATE_WIDGET_WEBCAM_ERROR :
                    // Widget en erreur
                    if (appWidgetId != 0) {
                        new WebCamUtils(context, appWidgetManager, remoteViews, appWidgetId).noData();
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
            WebCamWidget widget = (WebCamWidget) DomoUtils.getObjetById(context, new WebCamWidget(context, appWidgetId));
            if (widget != null) {
                RemoteViews remoteViews  = new RemoteViews(context.getPackageName(), R.layout.webcam_widget);
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
           new WebCamUtils(context, appWidgetManager, remoteViews, appWidgetId).downloadWebCamPicture();
        } catch (Exception e) {
            Log.e(TAG, "Erreur : " + e);
        }
    }
}

