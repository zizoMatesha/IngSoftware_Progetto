package srcClass;

import java.sql.Date;

public class prescrizioneForTable {
        private int codFarmaco;
        private String denominazioneFarmaco;
        private Date dataPrescrizione;
        private int durataTerapia;
        private int numeroDosiGiornaliere;
        private float qtaFarmacoDose;
        private String cfMedico;
        private String csPaziente;
        private Medico medicoResp;
        private Ricovero ricovero;
        private int codPrescrizione;

        public prescrizioneForTable(int codPrescrizione, int codFarmaco, String denominazioneFarmaco, Date dataPrescrizione, int durataTerapia, int numeroDosiGiornaliere, float qtaFarmacoDose, String cfMedico, String csPaziente, Medico medicoResp, Ricovero ricovero) {
            this.codFarmaco = codFarmaco;
            this.denominazioneFarmaco = denominazioneFarmaco;
            this.dataPrescrizione = dataPrescrizione;
            this.durataTerapia = durataTerapia;
            this.numeroDosiGiornaliere = numeroDosiGiornaliere;
            this.qtaFarmacoDose = qtaFarmacoDose;
            this.cfMedico = cfMedico;
            this.csPaziente = csPaziente;
            this.medicoResp = medicoResp;
            this.ricovero = ricovero;
            this.codPrescrizione = codPrescrizione;
        }


    public int getCodFarmaco() {
        return codFarmaco;
    }

    public void setCodFarmaco(int codFarmaco) {
        this.codFarmaco = codFarmaco;
    }

    public String getDenominazioneFarmaco() {
        return denominazioneFarmaco;
    }

    public void setDenominazioneFarmaco(String denominazioneFarmaco) {
        this.denominazioneFarmaco = denominazioneFarmaco;
    }

    public Date getDataPrescrizione() {
        return dataPrescrizione;
    }

    public void setDataPrescrizione(Date dataPrescrizione) {
        this.dataPrescrizione = dataPrescrizione;
    }

    public int getDurataTerapia() {
        return durataTerapia;
    }

    public void setDurataTerapia(int durataTerapia) {
        this.durataTerapia = durataTerapia;
    }

    public int getNumeroDosiGiornaliere() {
        return numeroDosiGiornaliere;
    }

    public void setNumeroDosiGiornaliere(int numeroDosiGiornaliere) {
        this.numeroDosiGiornaliere = numeroDosiGiornaliere;
    }

    public float getQtaFarmacoDose() {
        return qtaFarmacoDose;
    }

    public void setQtaFarmacoDose(float qtaFarmacoDose) {
        this.qtaFarmacoDose = qtaFarmacoDose;
    }

    public String getCfMedico() {
        return cfMedico;
    }

    public void setCfMedico(String cfMedico) {
        this.cfMedico = cfMedico;
    }

    public Medico getMedicoResp() {
        return medicoResp;
    }

    public void setMedicoResp(Medico medicoResp) {
        this.medicoResp = medicoResp;
    }

    public Ricovero getRicovero() {
        return ricovero;
    }

    public void setRicovero(Ricovero ricovero) {
        this.ricovero = ricovero;
    }

    public String getCsPaziente() {
        return csPaziente;
    }

    public void setCsPaziente(String csPaziente) {
        this.csPaziente = csPaziente;
    }

    public int getCodPrescrizione() {
        return codPrescrizione;
    }

    public void setCodPrescrizione(int codPrescrizione) {
        this.codPrescrizione = codPrescrizione;
    }
}
