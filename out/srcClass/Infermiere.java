package srcClass;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

public class Infermiere extends LoggedUser{
    private String usrName;
    private String nome;
    private String cognome;

    public Infermiere(String usrName, String nome, String cognome, CodFiscale cf) {
        this.usrName = usrName;
        this.nome = nome;
        this.cognome = cognome;
        super.setCodFiscale(cf);
    }

    public Infermiere(String usrName, String nome, String cognome) {
        this.usrName = usrName;
        this.nome = nome;
        this.cognome = cognome;
    }

    //Set Method

    public void setUsrName(String usrName) {
        this.usrName = usrName;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public void setCf(CodFiscale cf) {
        super.setCodFiscale(cf);
    }

    //Get Method
    public String getUsrName() {
        return usrName;
    }

    public String getNome() {
        return nome;
    }

    public String getCognome() {
        return cognome;
    }
}
