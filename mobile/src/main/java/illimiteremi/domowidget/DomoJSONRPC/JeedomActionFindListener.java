package illimiteremi.domowidget.DomoJSONRPC;

import android.widget.AutoCompleteTextView;

public interface JeedomActionFindListener {

    /**
     * Interface with callback methods for Action Find.
     */
    void onCancel();

    void onOk(AutoCompleteTextView cmdTextView, String cmd);

}
