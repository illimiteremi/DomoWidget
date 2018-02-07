package illimiteremi.domowidget.DomoAdapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import illimiteremi.domowidget.DomoGeneralSetting.IconSetting;
import illimiteremi.domowidget.DomoUtils.DomoUtils;
import illimiteremi.domowidget.R;

/**
 * $Description
 *
 * @author xzaq496 (non modifiable)
 *         Date de création : 19/07/2016 (non modifiable)
 *         <p/>
 *         <b><u>Dernière modification : $Description_modification</u></b>
 *         <li>$Date     : 19/07/2016$</li>
 *         <li>$Author   : xzaq496$</li>
 *         <li>$Revision :        $</li>
 *         <li>$HeadURL  :        $</li>
 */
public class IconAdapter extends ArrayAdapter<IconSetting> {

    private final String TAG = "[DOMO_ADAPTER]";

    private final boolean delIcon;

    private class RessourceViewHolder {
        public ImageView      ressourceView;
        public int            ressourceId;
        public TextView       ressourceName;
        public String         ressourcePath;
        public RelativeLayout relativeLayout;
        public ImageButton    buttonDel;
    }

    public IconAdapter(Context context, List<IconSetting> iconSettings, boolean delIcon) {
        super(context, 0, iconSettings);
        this.delIcon = delIcon;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listview_ressource, null);
        }

        RessourceViewHolder viewHolder = (RessourceViewHolder)convertView.getTag();

        if(viewHolder == null) {
            viewHolder = new RessourceViewHolder();
        }

        viewHolder.ressourceView  = (ImageView) convertView.findViewById(R.id.imageButtonOn);
        viewHolder.ressourceName  = (TextView) convertView.findViewById(R.id.ressourceName);
        viewHolder.relativeLayout = (RelativeLayout) convertView.findViewById(R.id.ressourceLayout);
        viewHolder.buttonDel      = (ImageButton) convertView.findViewById(R.id.buttonDel);

        IconSetting iconSetting = getItem(position);
        viewHolder.ressourceId      = iconSetting.getId();
        viewHolder.ressourcePath    = iconSetting.getRessourcePath();
        viewHolder.ressourceName.setText(iconSetting.getRessourceName());

        if (delIcon) {
            viewHolder.buttonDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                remove(getItem(position));
                }
            });
        } else {
            viewHolder.buttonDel.setEnabled(false);
            viewHolder.buttonDel.setVisibility(View.INVISIBLE);
        }

        if (iconSetting.getRessourcePath() == null) {
            int ressourceId = getContext().getResources().getIdentifier(iconSetting.getRessourceName(), "drawable", getContext().getPackageName());
            viewHolder.ressourceView.setImageResource(ressourceId);
        } else {
            Drawable drawable = Drawable.createFromPath(iconSetting.getRessourcePath());
            viewHolder.ressourceView.setImageDrawable(drawable);
        }

        // Changement de couleur 1 ligne / 2
        if ((position % 2) == 0) {
            viewHolder.relativeLayout.setBackgroundColor(Color.argb(20,13,151,36));
        } else {
            viewHolder.relativeLayout.setBackgroundColor(Color.argb(10,13,151,36));
        }

        return convertView;
    }

    @Override
    public void remove(IconSetting object) {
        DomoUtils.removeObjet(getContext(), object);
        super.remove(object);
    }
}
