package illimiteremi.domowidget.DomoAdapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.List;

import illimiteremi.domowidget.R;

import static illimiteremi.domowidget.DomoUtils.DomoConstants.NEW_WIDGET;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.NO_WIDGET;


public class WidgetAdapter extends ArrayAdapter<Object> {

    private final String  TAG           = "[DOMO_WIDGET_ADAPTER]";

    public class ViewHolder {
        public  TextView  itemName;
    }

    public WidgetAdapter(Context context, List<Object> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        try {
            if(convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.listview_box, parent, false);
            }

            ViewHolder viewHolder = (ViewHolder) convertView.getTag();

            if(viewHolder == null) {
                viewHolder = new ViewHolder();
            }

            viewHolder.itemName = (TextView) convertView.findViewById(R.id.itemName);

            // Identifiant du widget
            Object object = getItem(position);
            Field field = object.getClass().getDeclaredField("domoId");
            field.setAccessible(true);
            Integer idWidget = (Integer) field.get(object);

            // Nom du widget
            field = object.getClass().getDeclaredField("domoName");
            field.setAccessible(true);
            String name = (String) field.get(object);

            // Information présence
            field = object.getClass().getDeclaredField("isPresent");
            field.setAccessible(true);
            Boolean isPesent = (Boolean) field.get(object);

            String spinnerName = idWidget.equals(0) ? name : (position + 1)  + " - " + name;

            // Changement de couleur 1 ligne sur 2
            if ((position % 2) == 0) {
                viewHolder.itemName.setBackgroundColor(Color.argb(100,13,151,36));
            } else {
                viewHolder.itemName.setBackgroundColor(Color.argb(10,13,151,36));
            }

            // Verification de la présence du widget sur le bureau
            switch (idWidget) {
                case NEW_WIDGET:
                    viewHolder.itemName.setTextColor(Color.argb(255,255,255,255));
                    viewHolder.itemName.setBackgroundColor(Color.argb(255,13,151,36));
                    viewHolder.itemName.setTypeface(null, Typeface.NORMAL);
                    break;
                case NO_WIDGET:
                    viewHolder.itemName.setTextColor(Color.argb(255,0,0,0));
                    viewHolder.itemName.setBackgroundColor(Color.argb(100,255,0,0));
                    viewHolder.itemName.setTypeface(null, Typeface.NORMAL);
                    break;
                default:
                    if (!isPesent) {
                        viewHolder.itemName.setTypeface(null, Typeface.ITALIC);
                        spinnerName += " " + getContext().getResources().getString((R.string.not_on_screen));
                    } else {
                        viewHolder.itemName.setTypeface(null, Typeface.NORMAL);
                    }
            }

            // Affiche dans le spinner
            viewHolder.itemName.setText(spinnerName);

        } catch (Exception e) {
            Log.e(TAG, "Erreur : " + e);
        }
        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView,  parent);
    }

}
