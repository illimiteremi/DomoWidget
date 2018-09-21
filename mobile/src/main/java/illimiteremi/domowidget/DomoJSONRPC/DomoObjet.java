package illimiteremi.domowidget.DomoJSONRPC;

public class DomoObjet {

    private Integer id;                                          // Identifiant SQL
    private Integer idObjet;                                     // Identifiant de l'objet jeedom
    private String  objetName  = "";                             // Nom de l'objet
    private String  type;                                        // Type de la commande ACTION / INFO
    private Integer idCmd;                                       // Identifiant de la commande

    public Integer getIdObjet() {
        return idObjet;
    }

    public void setIdObjet(Integer idObjet) {
        this.idObjet = idObjet;
    }

    public String getObjetName() {
        return objetName;
    }

    public void setObjetName(String objetName) {
        this.objetName = objetName;
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

