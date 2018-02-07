package illimiteremi.domowidget.DomoWidgetMulti;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import illimiteremi.domowidget.DomoUtils.DomoBitmapUtils;
import illimiteremi.domowidget.DomoUtils.DomoConstants;
import illimiteremi.domowidget.DomoUtils.DomoUtils;
import illimiteremi.domowidget.R;

/**
 * Created by rcouturi on 17/12/2016.
 */

public class MultiWidgetService extends RemoteViewsService {

    private static final String TAG = "[DOMO_MULTI_RS]";

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        // Log.d(TAG, "onGetViewFactory = " + appWidgetId);
        return new MutliWidgetFactory(this.getApplicationContext(), intent);
    }

    class MutliWidgetFactory implements RemoteViewsService.RemoteViewsFactory {

        private final Context         context;
        private MultiWidget           widget;
        private final int             idWidget;
        private Bitmap                buttom;
        private final DomoBitmapUtils bitmapUtils;

        public MutliWidgetFactory(Context context, Intent intent) {
            this.context = context;
            idWidget = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
            bitmapUtils = new DomoBitmapUtils(context);
            // Log.d(TAG, "MutliWidgetFactory " + idWidget);
        }

        @Override
        public void onCreate() {
            try {
                // Récuperation du widget
                Log.d(TAG, "onCreate id = " + idWidget);
                widget = (MultiWidget) DomoUtils.getObjetById(context, new MultiWidget(context, idWidget));
            } catch (Exception e) {
                Log.d(TAG, "Erreur : " + e);
            }
        }

        @Override
        public void onDataSetChanged() {
            widget = (MultiWidget) DomoUtils.getObjetById(context, new MultiWidget(context, idWidget));
            // Log.d(TAG, "onDataSetChanged");
        }

        @Override
        public void onDestroy() {
        }

        @Override
        public int getCount() {
            int count = 0;
            try {
                count = widget.getMutliWidgetRess() != null ? widget.getMutliWidgetRess().size() : 0;
            } catch (Exception e) {
                Log.e(TAG, "Erreur : " + e);
            }
            return count;
        }

        @Override
        public RemoteViews getViewAt(int i) {
            MultiWidgetRess domoRessource = widget.getMutliWidgetRess().get(i);
            RemoteViews row = new RemoteViews(context.getPackageName(), R.layout.multi_widget_row);
            buttom = bitmapUtils.getBitmapRessource(domoRessource, true);
            row.setImageViewBitmap(R.id.imageView, buttom);
            row.setTextViewText(R.id.actionName, domoRessource.getDomoName());

            // Call setOnClickFillInIntent
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putInt(DomoConstants.POSITION_VIEW, i);
            intent.putExtras(bundle);
            row.setOnClickFillInIntent(R.id.idRowParent, intent);
            return row;
        }

        @Override
        public RemoteViews getLoadingView() {
            // Log.d(TAG, "getLoadingView");
            return null;
        }

        @Override
        public int getViewTypeCount() {
            // Log.d(TAG, "getViewTypeCount");
            return 1;
        }

        @Override
        public long getItemId(int i) {
            // Log.d(TAG, "getItemId " + i);
            return i;
        }

        @Override
        public boolean hasStableIds() {
            // Log.d(TAG, "hasStableIds");
            return true;
        }
    }
}
