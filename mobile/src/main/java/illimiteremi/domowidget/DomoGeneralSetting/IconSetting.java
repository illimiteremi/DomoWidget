package illimiteremi.domowidget.DomoGeneralSetting;

/**
 * Created by xzaq496 on 16/02/2017.
 */

public class IconSetting {

    static final String TAG      = "[DOMO_ICON_SETTING]";

    private int    id;                  // Identifiant de la resource en bdd
    private String ressourceName;       // Nom de la ressource
    private String ressourcePath;       // Chemin du fichier

    public String getRessourceName() {
        return ressourceName;
    }

    public void setRessourceName(String ressourceName) {
        this.ressourceName = ressourceName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRessourcePath() {
        return ressourcePath;
    }

    public void setRessourcePath(String ressourcePath) {
        this.ressourcePath = ressourcePath;
    }

}
