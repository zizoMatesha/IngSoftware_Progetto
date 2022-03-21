package srcClass;

public class LoggedUser {
    private CodFiscale cf;
    private int accountType;
    private String nomeUtente;

    public LoggedUser(){}

    public CodFiscale getCodFiscale() {
        return cf;
    }

    public void setCodFiscale(CodFiscale codFiscale) {
        this.cf = codFiscale;
    }

    public int getAccountType() {
        return accountType;
    }

    public void setAccountType(int accountType) {
        this.accountType = accountType;
    }

    public String getNomeUtente() {
        return nomeUtente;
    }

    public void setNomeUtente(String nomeUtente) {
        this.nomeUtente = nomeUtente;
    }
}
