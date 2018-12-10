package illimiteremi.domowidget.DomoAdapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import illimiteremi.domowidget.DomoJSONRPC.DomoCmd;
import illimiteremi.domowidget.R;

public class CmdAdapter extends ArrayAdapter<DomoCmd> {

    private class ViewHolder {
        public TextView cmdName;
    }

    public CmdAdapter(Context context, ArrayList<DomoCmd> domoCmds) {
        super(context, 0, domoCmds);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listview_box, null);
        }

        CmdAdapter.ViewHolder viewHolder = (CmdAdapter.ViewHolder)convertView.getTag();

        if(viewHolder == null) {
            viewHolder = new CmdAdapter.ViewHolder();
        }

        DomoCmd domoCmd = getItem(position);
        viewHolder.cmdName = convertView.findViewById(R.id.itemName);
        viewHolder.cmdName.setText(domoCmd.getCmdName());

        // Changement de couleur 1 ligne / 2
        if ((position % 2) == 0) {
            viewHolder.cmdName.setBackgroundColor(Color.argb(100,13,151,36));
        } else {
            viewHolder.cmdName.setBackgroundColor(Color.argb(20,13,151,36));
        }

        // Couleur rouge si id = -1
        if (domoCmd.getIdCmd() == -1) {
            viewHolder.cmdName.setBackgroundColor(Color.argb(100,255,0,0));
        }

        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView,  parent);
    }

}
