package illimiteremi.domowidget.DomoJSONRPC;

public interface JeedomActionFind {

    /**
     * Interface with callback methods for Action Find.
     */
    public interface OnJeedomActionFind {
        void onCancel(JeedomFindDialogFragment dialogFragment);

        void onOk(JeedomFindDialogFragment dialogFragment, String cmd);
    }
}
