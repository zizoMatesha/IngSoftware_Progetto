package db;

import srcClass.*;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public interface DataBaseDAO {
    public LoggedUser login(String username, String password)  throws SQLException;
    public boolean registraPaziente(String nome, String cognome, Date dataNascita, String luogoNascita, String codSanitario);
    public boolean generaRicovero(String codSanitarioRicovero);
    public boolean registraPrescrizione(Prescrizione prescrizione);
    public boolean registraSomministrazione(Somministrazione somministrazione);
    public boolean closeRicovero(Ricovero ricovero);
    public ArrayList<Paziente> getPatientList();
    public ArrayList<Farmaco> getSearchedFarmaco(String denominazione, String codAIC, String principioAttivo);
    public ArrayList<Ricovero> getRicoveriArchiviatiFiltrati(String nomePaziente, String cognomePaziente, String codiceSanitarioPaziente, LocalDate dataInizio, LocalDate dataFine);
    public ArrayList<Paziente> getSearchedPaziente(String nome, String cognome, String codSanitario);
    public List<Ricovero> getPatientsHospitalized();
    public ArrayList<Ricovero> getRicoveriArchiviati();
    public Ricovero getRicovero(Integer codRicovero);
    public Ricovero getRicoveroArchiviato(Integer codRicovero);
    public ArrayList<Prescrizione> getPrescrizioni(Ricovero ricovero);
    public ArrayList<Somministrazione> getSomministrazioni(Ricovero ricovero);
    public LoggedUser getUserAccount();
    public boolean setAlarm(Ricovero ricovero, LoggedUser userAccount, Allarme alarm, Time time, String intervento);
}
