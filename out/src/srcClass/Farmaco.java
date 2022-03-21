package srcClass;

public class Farmaco {
    private String nomeCommerciale;
    private int codAIC;
    private String principioAttivo;
    private String titolareAIC;
    private String descrizione;

    public Farmaco(String nomeCommerciale, int codAIC, String principioAttivo) {
        this.nomeCommerciale = nomeCommerciale;
        this.codAIC = codAIC;
        this.principioAttivo = principioAttivo;
    }

    public Farmaco(){}

    public String getNomeCommerciale() {
        return nomeCommerciale;
    }

    public void setNomeCommerciale(String nomeCommerciale) {
        this.nomeCommerciale = nomeCommerciale;
    }

    public String getPrincipioAttivo() {
        return principioAttivo;
    }

    public void setPrincipioAttivo(String principioAttivo) {
        this.principioAttivo = principioAttivo;
    }

    public int getCodAIC() {
        return codAIC;
    }

    public void setCodAIC(int codAIC) {
        this.codAIC = codAIC;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getTitolareAIC() {
        return titolareAIC;
    }

    public void setTitolareAIC(String titolareAIC) {
        this.titolareAIC = titolareAIC;
    }

    public String toSting(){
        return ""+codAIC;
    }

}
