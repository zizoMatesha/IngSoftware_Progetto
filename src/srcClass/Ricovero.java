package srcClass;

import java.io.Serializable;
import java.sql.Date;
import java.util.Objects;

public class Ricovero implements Serializable {

    private int idRicovero;
    private boolean chiusa;
    private Paziente pazienteRicoverato;
    private Date dataInizioRicovero;
    private Date dataFineRicovero;
    private int durataOssTerapia; //In minuti
    private int durataVentilazAssistita; //In minuti

    public Ricovero(){}
    public Ricovero(Ricovero ricovero){
        this.chiusa =                   ricovero.chiusa;
        this.pazienteRicoverato =       ricovero.pazienteRicoverato;
        this.dataInizioRicovero =       ricovero.dataInizioRicovero;
        this.dataFineRicovero =         ricovero.dataFineRicovero;
        this.durataOssTerapia =         ricovero.durataOssTerapia; //In minuti
        this.durataVentilazAssistita =  ricovero.durataVentilazAssistita; //In minuti
    }
    public Ricovero(int idRicovero, boolean chiusa, Paziente pazienteRicoverato, Date dataInizioRicovero, Date dataFineRicovero, int durataOssTerapia, int durataVentilazAssistita){
        this.idRicovero = idRicovero;
        this.chiusa = chiusa;
        this.pazienteRicoverato = pazienteRicoverato;
        this.dataInizioRicovero = dataInizioRicovero;
        this.dataFineRicovero = dataFineRicovero;
        this.durataOssTerapia = durataOssTerapia; //In minuti
        this.durataVentilazAssistita = durataVentilazAssistita; //In minuti
    }


    /*_______________________________________________________________________________________________________________
            GET AND SET METHOD
    _________________________________________________________________________________________________________________*/
    public boolean isChiusa() {
        return chiusa;
    }

    public void setChiusa(boolean chiusa) {
        this.chiusa = chiusa;
    }

    public Paziente getPazienteRicoverato() {
        return pazienteRicoverato;
    }

    public void setPazienteRicoverato(Paziente pazienteRicoverato) {
        this.pazienteRicoverato = pazienteRicoverato;
    }

    public Date getDataInizioRicovero() {
        return dataInizioRicovero;
    }

    public void setDataInizioRicovero(Date dataInizioRicovero) {
        this.dataInizioRicovero = dataInizioRicovero;
    }

    public int getDurataOssTerapia() {
        return durataOssTerapia;
    }

    public void setDurataOssTerapia(int durataOssTerapia) {
        this.durataOssTerapia = durataOssTerapia;
    }

    public int getDurataVentilazAssistita() {
        return durataVentilazAssistita;
    }

    public void setDurataVentilazAssistita(int durataVentilazAssistita) {
        this.durataVentilazAssistita = durataVentilazAssistita;
    }

    public String toString(){
        return "Ricovero di " + this.pazienteRicoverato.getNome() + " " + this.pazienteRicoverato.getCognome() + " " +
                "\nDati ricovero: " +
                "(data->'"+ this.dataInizioRicovero + "'), " +
                "(chiusa->'" + this.chiusa + "'), " +
                "(oss terapia->'" + this.durataOssTerapia + "')," +
                "(vent assistita->'" + this.durataVentilazAssistita + "')" ;
    }

    public int getIdRicovero() {
        return idRicovero;
    }

    public void setIdRicovero(int idRicovero) {
        this.idRicovero = idRicovero;
    }

    public Date getDataFineRicovero() {
        return dataFineRicovero;
    }

    public void setDataFineRicovero(Date dataFineRicovero) {
        this.dataFineRicovero = dataFineRicovero;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ricovero)) return false;
        Ricovero ricovero = (Ricovero) o;
        return idRicovero == ricovero.idRicovero && chiusa == ricovero.chiusa && durataOssTerapia == ricovero.durataOssTerapia && durataVentilazAssistita == ricovero.durataVentilazAssistita && pazienteRicoverato.equals(ricovero.pazienteRicoverato) && dataInizioRicovero.equals(ricovero.dataInizioRicovero) && Objects.equals(dataFineRicovero, ricovero.dataFineRicovero);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idRicovero, pazienteRicoverato, dataInizioRicovero);
    }
}
