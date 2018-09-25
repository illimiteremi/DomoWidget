package illimiteremi.domowidget.DomoJSONRPC;

public interface JeedomActionFindListener {

    /**
     * Interface with callback methods for Action Find.
     */
    void onCancel(JeedomFindDialogFragment dialogFragment);

    void onOk(JeedomFindDialogFragment dialogFragment, String cmd);

}
