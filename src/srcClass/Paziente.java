package srcClass;

import java.io.Serializable;
import java.sql.Date;
import java.util.Objects;

public class Paziente implements Serializable {
    private String nome;
    private String cognome;
    private String codSanitario;
    private String luogo_nascita;
    private Date data_nascita;

    public Paziente(String nome, String cognome, String codSanitario, Date data_nascita, String luogo_nascita) {
        this.nome = nome;
        this.cognome = cognome;
        this.codSanitario = codSanitario;
        this.data_nascita = data_nascita;
        this.luogo_nascita = luogo_nascita;
    }
    public Paziente() {}

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public void setCodSanitario(String codSanitario) {this.codSanitario = codSanitario;}

    public void setData_nascita(Date data_nascita) {
        this.data_nascita = data_nascita;
    }

    public void setLuogo_nascita(String luogo_nascita) {this.luogo_nascita = luogo_nascita;}

    public String getNome() { return nome; }

    public String getCognome() { return cognome;}

    public String getCodSanitario() {
        return codSanitario;
    }

    public Date getData_nascita() {
        return data_nascita;
    }

    public String getLuogo_nascita() {
        return luogo_nascita;
    }

    @Override
    public String toString() {
        return "Paziente{" +
                "nome='" + nome + '\'' +
                ", cognome='" + cognome + '\'' +
                ", codSanitario='" + codSanitario + '\'' +
                ", luogo_nascita='" + luogo_nascita + '\'' +
                ", data_nascita=" + data_nascita +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Paziente)) return false;
        Paziente paziente = (Paziente) o;
        return nome.equals(paziente.nome) && cognome.equals(paziente.cognome) && codSanitario.equals(paziente.codSanitario) && luogo_nascita.equals(paziente.luogo_nascita) && data_nascita.equals(paziente.data_nascita);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nome, cognome, codSanitario, luogo_nascita, data_nascita);
    }
}
