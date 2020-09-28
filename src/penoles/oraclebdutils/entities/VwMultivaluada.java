package penoles.oraclebdutils.entities;

import java.io.Serializable;

public class VwMultivaluada implements Serializable {

    private String tab_cve;
    private String eta_desc;
    private String lta_nom_campo;
    private String lta_nom_tabla;
    private String eta_cve;

    public VwMultivaluada() {
    }

    public VwMultivaluada(String tab_cve, String eta_desc, String lta_nom_campo, String lta_nom_tabla, String eta_cve) {
        this.tab_cve = tab_cve;
        this.eta_desc = eta_desc;
        this.lta_nom_campo = lta_nom_campo;
        this.lta_nom_tabla = lta_nom_tabla;
        this.eta_cve = eta_cve;
    }

    public String getTab_cve() {
        return tab_cve;
    }

    public void setTab_cve(String tab_cve) {
        this.tab_cve = tab_cve;
    }

    public String getEta_desc() {
        return eta_desc;
    }

    public void setEta_desc(String eta_desc) {
        this.eta_desc = eta_desc;
    }

    public String getLta_nom_campo() {
        return lta_nom_campo;
    }

    public void setLta_nom_campo(String lta_nom_campo) {
        this.lta_nom_campo = lta_nom_campo;
    }

    public String getLta_nom_tabla() {
        return lta_nom_tabla;
    }

    public void setLta_nom_tabla(String lta_nom_tabla) {
        this.lta_nom_tabla = lta_nom_tabla;
    }

    public String getEta_cve() {
        return eta_cve;
    }

    public void setEta_cve(String eta_cve) {
        this.eta_cve = eta_cve;
    }
}
