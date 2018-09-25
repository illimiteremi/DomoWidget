package illimiteremi.domowidget.DomoAdapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;
import illimiteremi.domowidget.DomoJSONRPC.DomoEquipement;
import illimiteremi.domowidget.R;

public class EquipementAdapter extends ArrayAdapter<Object> {

    private class ViewHolder {
        public TextView equipementName;
    }

    public EquipementAdapter(Context context, List<Object> domoEquipements) {
        super(context, 0, domoEquipements);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listview_box, null);
        }

        EquipementAdapter.ViewHolder viewHolder = (EquipementAdapter.ViewHolder)convertView.getTag();

        if(viewHolder == null) {
            viewHolder = new EquipementAdapter.ViewHolder();
        }

        DomoEquipement domoEquipement = (DomoEquipement) getItem(position);
        viewHolder.equipementName = convertView.findViewById(R.id.itemName);
        String name = domoEquipement != null ? domoEquipement.getObjetName() : "Aucun Equipement(s)";
        viewHolder.equipementName.setText(name);

        // Changement de couleur 1 ligne / 2
        if ((position % 2) == 0) {
            viewHolder.equipementName.setBackgroundColor(Color.argb(100,13,151,36));
        } else {
            viewHolder.equipementName.setBackgroundColor(Color.argb(10,13,151,36));
        }
        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView,  parent);
    }

}
