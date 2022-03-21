package srcClass;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

public class Somministrazione {
    private String cod_infermiere;
    private Timestamp dataSomministrazione;
    private Float doseSomministrata;
    private String note_stato;
    private int numPrescrizione;

    public Somministrazione(String cod_infermiere, Timestamp dataSomministrazione, Float doseSomministrata, String note_stato, int numPrescrizione){
        this.cod_infermiere = cod_infermiere;
        this.dataSomministrazione = dataSomministrazione;
        this.doseSomministrata = doseSomministrata;
        this.note_stato = note_stato;
        this.numPrescrizione = numPrescrizione;
    }

    public String getCod_infermiere() {
        return cod_infermiere;
    }

    public void setCod_infermiere(String cod_infermiere) {
        this.cod_infermiere = cod_infermiere;
    }

    public Timestamp getDataSomministrazione() {
        return dataSomministrazione;
    }

    public void setDataSomministrazione(Timestamp dataSomministrazione) {
        this.dataSomministrazione = dataSomministrazione;
    }

    public Float getDoseSomministrata() {
        return doseSomministrata;
    }

    public void setDoseSomministrata(Float doseSomministrata) {
        this.doseSomministrata = doseSomministrata;
    }

    public String getNote_stato() {
        return note_stato;
    }

    public void setNote_stato(String note_stato) {
        this.note_stato = note_stato;
    }

    public int getNumPrescrizione() {
        return numPrescrizione;
    }

    public void setNumPrescrizione(int numPrescrizone) {
        this.numPrescrizione = numPrescrizone;
    }
}
