package illimiteremi.domowidget.DomoJSONRPC;

public class DomoCmd {

    private Integer idObjet;                                     // Identifiant de l'objet jeedom
    private String  cmdName;                                     // Nom de la commande
    private String  type;                                        // Type de la commande ACTION / INFO
    private Integer idCmd;                                       // Identifiant de la commande

    public DomoCmd() {
        cmdName = "";
        idObjet = -1;
    }

    public Integer getIdObjet() {
        return idObjet;
    }

    public void setIdObjet(Integer idObjet) {
        this.idObjet = idObjet;
    }

    public String getCmdName() {
        return cmdName;
    }

    public void setCmdName(String cmdName) {
        this.cmdName = cmdName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getIdCmd() {
        return idCmd;
    }

    public void setIdCmd(Integer idCmd) {
        this.idCmd = idCmd;
    }
}
