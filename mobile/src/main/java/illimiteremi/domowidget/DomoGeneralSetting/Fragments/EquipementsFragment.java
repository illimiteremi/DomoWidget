package illimiteremi.domowidget.DomoGeneralSetting.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import illimiteremi.domowidget.DomoJSONRPC.JeedomActionFindListener;
import illimiteremi.domowidget.DomoJSONRPC.JeedomFindDialogFragment;
import illimiteremi.domowidget.DomoUtils.DomoConstants;
import illimiteremi.domowidget.R;

public class EquipementsFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.find_cmd_dialog, container, false);
        return view;
    }

}
