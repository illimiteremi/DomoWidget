package illimiteremi.domowidget.DomoWidgetSeekBar;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import illimiteremi.domowidget.DomoUtils.DomoConstants;
import illimiteremi.domowidget.DomoUtils.DomoUtils;
import illimiteremi.domowidget.R;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

/**
 * Created by rcouturi on 17/12/2016.
 */

public class SeekBarWidgetService extends RemoteViewsService {

    private static final String TAG = "[DOMO_SEEKBAR_RS]";

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        //Log.d(TAG, "onGetViewFactory");
        return new SeekBarWidgetFactory(this.getApplicationContext(), intent);
    }

    class SeekBarWidgetFactory implements RemoteViewsFactory {

        private final Context         context;
        private final int             appWidgetId;
        private SeekBarWidget         widget;
        private int                   lastValue;

        public SeekBarWidgetFactory(Context context, Intent intent) {
            this.context = context;
            appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        @Override
        public void onCreate() {
        }

        @Override
        public void onDataSetChanged() {
            widget = (SeekBarWidget) DomoUtils.getObjetById(context, new SeekBarWidget(context, appWidgetId));
            try {
                lastValue = widget.getDomoLastValue() == null ? 0 : Integer.parseInt(widget.getDomoLastValue());
                lastValue = seekBarActionSetValue(lastValue);
            } catch (Exception e) {
                lastValue = 0;
            }
            // Log.d(TAG, "onDataSetChanged : " + lastValue);
        }

        @Override
        public void onDestroy() {
        }

        @Override
        public int getCount() {
            return 40;
        }

        @Override
        public RemoteViews getViewAt(int i) {
            //Log.d(TAG, "getViewAt " + i + " - " + this.lastValue);
            RemoteViews row = new RemoteViews(context.getPackageName(), R.layout.seekbar_widget_row);

            if (i <= this.lastValue) {
                try {
                    row.setViewVisibility(R.id.imageView, VISIBLE);
                    row.setInt(R.id.imageView, "setBackgroundColor", Integer.parseInt(widget.getDomoColor()));
                } catch (Exception e) {
                    row.setInt(R.id.imageView, "setBackgroundColor", Color.BLUE);
                    Log.e(TAG, "Erreur Couleur : " + e);
                }
                if (i == this.lastValue) {
                    row.setInt(R.id.imageView, "setBackgroundColor", Color.WHITE);
                }
            }  else {
                row.setViewVisibility(R.id.imageView, INVISIBLE);
            }

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
            //Log.d(TAG, "getLoadingView");
            return null;
        }

        @Override
        public int getViewTypeCount() {
            //Log.d(TAG, "getViewTypeCount");
            return 1;
        }

        @Override
        public long getItemId(int i) {
            //Log.d(TAG, "getItemId " + i);
            return i;
        }

        @Override
        public boolean hasStableIds() {
            //Log.d(TAG, "hasStableIds");
            return true;
        }

        /**
         * seekBarActionSetValue
         * @param reelValue
         * @return
         */
        private int seekBarActionSetValue(int reelValue) {
            // Calcul de la valeur de la barre (entre 0 et 40)
            int valueToSlide;
            if (reelValue == widget.getDomoMinValue()) {
                valueToSlide = 0;
            } else if (reelValue == widget.getDomoMaxValue()) {
                valueToSlide = 39;
            } else {
                valueToSlide = (int) Math.round(((reelValue * 100) / (widget.getDomoMaxValue() - widget.getDomoMinValue())) / 2.5);
            }
            Log.d(TAG, "idWidget = " + appWidgetId + " - Valeur à positionner  : " + reelValue + " => " + valueToSlide + "/40");
            return valueToSlide;
        }
    }
}