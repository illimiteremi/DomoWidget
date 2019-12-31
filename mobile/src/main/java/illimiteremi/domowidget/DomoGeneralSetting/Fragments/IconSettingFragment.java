package illimiteremi.domowidget.DomoGeneralSetting.Fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.List;

import illimiteremi.domowidget.DomoAdapter.IconAdapter;
import illimiteremi.domowidget.DomoGeneralSetting.IconSetting;
import illimiteremi.domowidget.DomoUtils.DomoRessourceUtils;
import illimiteremi.domowidget.DomoUtils.FileExplorerActivity;
import illimiteremi.domowidget.R;


public class IconSettingFragment extends Fragment {

    private static final String   TAG      = "[DOMO_UPLOAD_PIC]";
    private ListView              listView;

    @Override
    public void onResume() {
        super.onResume();
        List<IconSetting> listRessource = DomoRessourceUtils.getAllImageRessource(getActivity().getApplicationContext());
        if (listRessource != null) {
            IconAdapter iconAdapter = new IconAdapter(getActivity(), listRessource, true);
            listView.setAdapter(iconAdapter);
            DomoRessourceUtils.setListViewHeightBasedOnItems(listView);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_manage, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_file:
                Intent intent = new Intent(getActivity(), FileExplorerActivity.class);
                intent.setAction("IMPORT_ICON");
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Maj du titre
        getActivity().setTitle(getResources().getString(R.string.fragment_upload_pic));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_icon_setting, container, false);
        setHasOptionsMenu(true);

        listView = (ListView) view.findViewById(R.id.listRessource);

        return view;
    }

}
