package srcClass;

public class Medico extends LoggedUser{
    private String nome;
    private String cognome;
    private String usrName;

    public Medico(String nome, String cognome, CodFiscale cf, String usrName) {
        this.nome = nome;
        this.cognome = cognome;
        super.setCodFiscale(cf);
        this.usrName = usrName;
    }

    public Medico(String nome, String cognome, CodFiscale cf) {
        this.nome = nome;
        this.cognome = cognome;
        super.setCodFiscale(cf);
    }

    public Medico(String nome, String cognome, String usrName) {
        this.nome = nome;
        this.cognome = cognome;
        this.usrName = usrName;
    }
    public Medico(LoggedUser userMedico) {
        super.setCodFiscale(userMedico.getCodFiscale());
        super.setAccountType(1);
    }

    public String getNome() {
        return nome;
    }

    public String getCognome() {
        return cognome;
    }

    public String getCf() { return super.getCodFiscale().getCf(); }

    public String getUsrName() {
        return usrName;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public void setCf(CodFiscale cf) { super.setCodFiscale(cf); }

    public void setUsrName(String usrName) {
        this.usrName = usrName;
    }
}
