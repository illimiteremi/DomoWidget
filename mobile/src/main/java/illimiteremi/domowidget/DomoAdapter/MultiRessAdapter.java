package illimiteremi.domowidget.DomoAdapter;

import android.content.Context;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import illimiteremi.domowidget.DomoGeneralSetting.Fragments.WidgetMultiFragment;
import illimiteremi.domowidget.DomoUtils.DomoBitmapUtils;
import illimiteremi.domowidget.DomoUtils.DomoRessourceUtils;
import illimiteremi.domowidget.DomoUtils.DomoUtils;
import illimiteremi.domowidget.DomoWidgetMulti.MultiWidgetRess;
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
public class MultiRessAdapter extends ArrayAdapter<MultiWidgetRess> {

    private final String  TAG           = "[DOMO_RESS_ADAPTER]";
    private final Context context;

    private final List<MultiWidgetRess> domoRessources;

    private class RessourceViewHolder {
        public int                      ressourceDomoId;
        public TextView                 ressourceName;
        public ImageView                ressourceButtonOn;
        public ImageButton              ressourceButtonSetting;
        public ImageButton              ressourceDel;
    }

    private final DomoBitmapUtils bitmapUtils;            // Boite à outils graphique

    public MultiRessAdapter(Context context, List<MultiWidgetRess> domoRessources) {
        super(context, 0, domoRessources);
        this.context        = context;
        this.domoRessources = domoRessources;
        this.bitmapUtils    = new DomoBitmapUtils(context);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.multi_ress_listview, null);
        }

        RessourceViewHolder ressourceViewHolder = (RessourceViewHolder)convertView.getTag();

        if(ressourceViewHolder == null) {
            ressourceViewHolder = new RessourceViewHolder();
        }

        ressourceViewHolder.ressourceButtonOn      = (ImageView) convertView.findViewById(R.id.buttonOn);
        ressourceViewHolder.ressourceButtonSetting = (ImageButton) convertView.findViewById(R.id.buttonSetting);
        ressourceViewHolder.ressourceDel           = (ImageButton) convertView.findViewById(R.id.button);
        ressourceViewHolder.ressourceName          = (TextView) convertView.findViewById(R.id.textActionName);

        final MultiWidgetRess domoRessource = getItem(position);
        ressourceViewHolder.ressourceDomoId = domoRessource.getDomoId();
        ressourceViewHolder.ressourceName.setText(domoRessource.getDomoName());

        // Récuperation image On
        ressourceViewHolder.ressourceButtonOn.setImageBitmap(bitmapUtils.getBitmapRessource(domoRessource, true));

        // Modification
        ressourceViewHolder.ressourceButtonSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ouverture du fragment
                DomoRessourceUtils.MultiWidgetRessourceFragment fragment = DomoRessourceUtils.MultiWidgetRessourceFragment.newInstance(domoRessource.getDomoId(), domoRessource.getId());
                fragment.setOnRessourceListener(WidgetMultiFragment.ressourceFragmentListener);
                FragmentTransaction ft = ((FragmentActivity)context).getSupportFragmentManager().beginTransaction();
                fragment.show(ft, "MODIFIER RESSOURCE");
            }
        });

        ressourceViewHolder.ressourceDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remove(domoRessource);
            }
        });

        return convertView;
    }

    @Override
    public void remove(MultiWidgetRess object) {
        DomoUtils.removeObjet(context, object);
        super.remove(object);
    }

    @Override
    public void add(MultiWidgetRess object) {
        super.add(object);
    }

    @Override
    public long getItemId(int position) {
        return (domoRessources != null) ? domoRessources.indexOf(domoRessources.get(position)) : 0;
    }

    @Override
    public int getCount() {
        return (domoRessources != null) ? domoRessources.size() : 0;
    }

}
