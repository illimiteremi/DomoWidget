package illimiteremi.domowidget.DomoJSONRPC;

public class DomoEquipement {

    private Integer idObjet;                                     // Identifiant de l'objet jeedom
    private String  objetName  = "";                             // Nom de l'objet

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
}

