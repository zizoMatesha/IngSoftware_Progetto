package srcClass;

import java.sql.Date;

public class Prescrizione {
    private Farmaco farmacoPrescritto;
    private Date dataPrescrizione;
    private int durataTerapia; //la durata della terapia viene misurata in giorni
    private int numeroDosiGiornaliere;
    private float qtaFarmacoDose;
    private Medico medicoResp;
    private Ricovero ricovero;
    private int codPrescrizione;

    public Prescrizione(Farmaco farmacoPrescritto, Date dataPrescrizione, int durataTerapia, int numeroDosiGiornaliere, float qtaFarmacoDose, Medico medicoResp, Ricovero ricovero, int codPrescrizione) {
        this.farmacoPrescritto = farmacoPrescritto;
        this.dataPrescrizione = dataPrescrizione;
        this.durataTerapia = durataTerapia;
        this.numeroDosiGiornaliere = numeroDosiGiornaliere;
        this.qtaFarmacoDose = qtaFarmacoDose;
        this.medicoResp = medicoResp;
        this.ricovero = ricovero;
        this.codPrescrizione = codPrescrizione;
    }

    public void setMedicoResp(Medico medicoResp) {
        this.medicoResp = medicoResp;
    }

    public Medico getMedicoResp() {
        return medicoResp;
    }

    public void setFarmacoPrescritto(Farmaco farmacoPrescritto) {
        this.farmacoPrescritto = farmacoPrescritto;
    }

    public void setDataPrescrizione(Date dataPrescrizione) {
        this.dataPrescrizione = dataPrescrizione;
    }

    public void setDurataTerapia(int durataTerapia) {
        this.durataTerapia = durataTerapia;
    }

    public void setNumeroDosiGiornaliere(int numeroDosiGiornaliere) {
        this.numeroDosiGiornaliere = numeroDosiGiornaliere;
    }

    public void setQtaFarmacoDose(float qtaFarmacoDose) {
        this.qtaFarmacoDose = qtaFarmacoDose;
    }

    public Farmaco getFarmacoPrescritto() {
        return farmacoPrescritto;
    }

    public Date getDataPrescrizione() {
        return dataPrescrizione;
    }

    public int getDurataTerapia() {
        return durataTerapia;
    }

    public int getNumeroDosiGiornaliere() {
        return numeroDosiGiornaliere;
    }

    public float getQtaFarmacoDose() {
        return qtaFarmacoDose;
    }

    public Ricovero getRicovero() {
        return ricovero;
    }

    public void setRicovero(Ricovero ricovero) {
        this.ricovero = ricovero;
    }

    public int getCodPrescrizione() {
        return codPrescrizione;
    }

    public void setCodPrescrizione(int codPrescrizione) {
        this.codPrescrizione = codPrescrizione;
    }
}
