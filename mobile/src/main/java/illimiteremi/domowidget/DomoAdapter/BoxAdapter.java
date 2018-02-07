package illimiteremi.domowidget.DomoAdapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import illimiteremi.domowidget.DomoGeneralSetting.BoxSetting;
import illimiteremi.domowidget.R;


public class BoxAdapter extends ArrayAdapter<Object> {

    private final String  TAG           = "[DOMO_BOX_ADAPTER]";

    private class ViewHolder {
        public TextView                 boxName;
    }

    public BoxAdapter(Context context, List<Object> boxSettingList) {
        super(context, 0, boxSettingList);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listview_box, null);
        }

        ViewHolder viewHolder = (ViewHolder)convertView.getTag();

        if(viewHolder == null) {
            viewHolder = new ViewHolder();
        }

        BoxSetting boxSetting = (BoxSetting) getItem(position);
        viewHolder.boxName = (TextView) convertView.findViewById(R.id.itemName);
        String name = boxSetting.getBoxId() == 0 ? boxSetting.getBoxName() : (position + 1) + " - " + boxSetting.getBoxName();
        viewHolder.boxName.setText(name);

        // Changement de couleur 1 ligne / 2
        if ((position % 2) == 0) {
            viewHolder.boxName.setBackgroundColor(Color.argb(100,13,151,36));
        } else {
            viewHolder.boxName.setBackgroundColor(Color.argb(10,13,151,36));
        }

        // Couleur rouge si pas d'id box
        if (boxSetting.getBoxId().equals(0)) {
            viewHolder.boxName.setBackgroundColor(Color.argb(100,255,0,0));
        }

        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView,  parent);
    }

}
