package db;

import java.sql.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import srcClass.*;

public class DataBase implements DataBaseDAO{
    private Connection connection = null;
    private Statement statement = null;
    private ResultSet resultSet = null;
    private LoggedUser userAccount;

    private DataBase(){}
    private static DataBase db = new DataBase();
    public static DataBase getDataBaseInstance(){
        return db;
    }
    private void connectToDb() {
        try {
            String pathDB = new String("jdbc:mariadb://localhost:3306/tidb?user=userGen&pwd=");
            this.connection = DriverManager.getConnection(pathDB);
        } catch (Exception e) {
            System.out.println("Login Failed! [...]\n" + e.toString());
        }
    }
    public LoggedUser login(String username, String password) throws SQLException {
        String pathDB = new String("jdbc:mariadb://localhost:3306/tidb?user=userGen&pwd=");
        //System.out.println(pathDB);
        this.connectToDb();

        try {
            statement = connection.createStatement();
        } catch (Exception e) {
            System.out.println("Error! [...]\n" + e.toString());
            return null;
        }

        try {
            //System.out.println("SELECT userName, password, tipoAccount, CodFiscale FROM " + dbName + "." + tableName + " WHERE userName='" + username + "' AND password='" + password + "';");
            resultSet = statement.executeQuery("SELECT userName, password, tipoAccount, CodFiscale FROM tidb.logintable WHERE userName='" + username + "' AND password='" + password + "';");
            resultSet.next();
            if(resultSet.getString("userName") != "" && resultSet.getString("password") != ""){
                userAccount = new LoggedUser();
                userAccount.setCodFiscale(new CodFiscale(resultSet.getString("CodFiscale")));
                userAccount.setAccountType(Integer.parseInt(resultSet.getString("tipoAccount")));
                userAccount.setNomeUtente(resultSet.getString("userName"));

                String tableName = null;
                //Controllo che il proprietario dell'account registrato sia effettivamente registrato anche come infermiere e/o paziente
                if(userAccount.getAccountType() == 1)    tableName = "medici";
                if(userAccount.getAccountType() == 2)    tableName = "infermieri";
                if(userAccount.getAccountType() == 3)    tableName = "medici";

                //System.out.println("Select cod_fiscale FROM " + dbName + "." + tableName + " WHERE " + tableName + ".cod_fiscale = '" + this.codFiscale + "'");
                resultSet = statement.executeQuery("Select cod_fiscale FROM tidb." + tableName + " WHERE " + tableName + ".cod_fiscale = '" + userAccount.getCodFiscale() + "'");
                resultSet.next();

                if(resultSet.getString("cod_fiscale") != "")
                    return userAccount;
            }

        } catch (Exception e) {
            System.err.println("Non esiste nessun account corrispondente alle credenziali inserite!");
            System.err.println(e);
        }
        return null;

    }
    // You need to close the resultSet
    private void close() {
        try {
            if (resultSet != null) { resultSet.close();}

            if (statement != null) { statement.close(); }

            if (connection != null) { connection.close(); }
        } catch (Exception e) { }
    }
    public boolean registraPaziente(String nome, String cognome, Date dataNascita, String luogoNascita, String codSanitario){
        this.connectToDb();

        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT codice_sanitario FROM tidb.pazienti WHERE codice_sanitario = '"+codSanitario.toUpperCase()+"';");
            if(resultSet.next()){
                return false;
            }
        }catch (SQLException e){
            e.printStackTrace();
            close();
            return false;
        }

        if(codSanitario.length() != 20){
            close();
            return false;
        }
        nome = nome.toUpperCase(); cognome = cognome.toUpperCase(); luogoNascita = luogoNascita.toUpperCase(); codSanitario = codSanitario.toUpperCase();
        //INSERT INTO tidb.`pazienti` (
        // `codice_sanitario`,
        // `nome`,
        // `cognome`,
        // `luogo_nascita`,
        // `data_nascita`)
        // VALUES ('AFSRDTE2875IOULE8920', 'DAVIDE', 'TISO', 'LEGNAGO', '2000-07-03')

        String query = "INSERT INTO tidb.pazienti (" +
                "codice_sanitario, " +
                "nome , " +
                "cognome , " +
                "luogo_nascita , " +
                "data_nascita) " +
                "VALUES ('"+ codSanitario +"', '"+ nome +"', '"+ cognome +"', '"+ luogoNascita +"', '"+ dataNascita +"')";
        System.out.println(query);

        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);

        }catch (SQLException e){
            e.printStackTrace();
            close();
            return false;
        }
        return true;
    }
    public boolean generaRicovero(String codSanitarioRicovero){
        /*INSERT INTO `cartelle_cliniche` VALUES (
        NULL,
        '0',
        '2021-12-10',
        '0',
        '0',
        '12345678912345678911')*/



        String query = "INSERT INTO cartelle_cliniche VALUES( NULL, '0', '" + Date.valueOf(LocalDate.now()) +"', NULL, '0', '0', '" + codSanitarioRicovero + "');";

        try{
            this.connectToDb();
            statement = connection.createStatement();

            resultSet = statement.executeQuery("SELECT (COUNT(*)) totCartelleCliniche  FROM tidb.cartelle_cliniche WHERE cartelle_cliniche.chiusa = '0'");
            //Controllo che le cartelle cliniche aperte non siano più di 10
            if(resultSet.next()){
                if(Integer.valueOf(resultSet.getString("totCartelleCliniche")) >= 10) {
                    System.err.println("Num pazienti non valido!");
                    return false;
                }else{
                    if(statement.executeQuery(query) != null){
                        System.out.println("Query andata a buon fine, apziente ricoverato");
                        return true;
                    }
                }
            }
        }catch (SQLException e){
            System.err.println("Errore query 'Genera ricovero'!! ( " + query+" )");
            return false;
        }finally {
            close();
        }
        return false;
    }
    private void setUserAccount(LoggedUser userAccount) {
        this.userAccount = userAccount;
    }
    public boolean registraPrescrizione(Prescrizione prescrizione){
        /*INSERT INTO `prescrizioni` VALUES (NULL, '1', '12', '123', '2021-12-10', 'medico', '31842026')*/

        try{
            connectToDb();
            statement = connection.createStatement();

            String query = "INSERT INTO tidb.prescrizioni VALUES ( NULL, '" + prescrizione.getNumeroDosiGiornaliere() + "', '" + prescrizione.getQtaFarmacoDose() + "'," +
                    " '" + prescrizione.getDurataTerapia() + "'," +
                    " '" + prescrizione.getDataPrescrizione() + "'," +
                    " '" + prescrizione.getMedicoResp().getCodFiscale() + "'," +
                    " '" + prescrizione.getFarmacoPrescritto().getCodAIC() + "'," +
                    " '" + prescrizione.getRicovero().getIdRicovero() + "');";
            System.out.println(query);

            resultSet = statement.executeQuery(query);
            return true;
        }catch (SQLException e){
            System.err.println(e.toString());
        }finally {
            this.close();
        }

        return false;
    }
    public boolean registraSomministrazione(Somministrazione somministrazione){
        try{

            //INSERT INTO `somministrazioni` (`cod_infermiere`, `dataSomministrazione`, `doseSomministrata`, `note_stato`, `numPrescrizone`) VALUES ('', '', '', '', '')
            connectToDb();
            statement = connection.createStatement();

            String query = "INSERT INTO tidb.somministrazioni VALUES ( '" + somministrazione.getCod_infermiere() + "', '" + somministrazione.getDataSomministrazione().toString() + "', '" + somministrazione.getDoseSomministrata() + "'," +
                    " '" + somministrazione.getNote_stato() + "'," +
                    " '" + somministrazione.getNumPrescrizione() + "');";

            System.out.println(query);

            resultSet = statement.executeQuery(query);
            return true;
        }catch (SQLException e){
            System.err.println(e.toString());
        }finally {
            this.close();
        }

        return false;
    }
    public boolean closeRicovero(Ricovero ricovero){
        connectToDb();
        try {
            statement = connection.createStatement();
            String query = "UPDATE cartelle_cliniche SET chiusa = '1', dataFineRicovero = '" + java.sql.Date.valueOf(ricovero.getDataFineRicovero().toLocalDate()) + "' WHERE cartelle_cliniche.id_cartClinica = " + ricovero.getIdRicovero() +" AND cartelle_cliniche.chiusa = '0';";

            int res = statement.executeUpdate(query);
            if(res == 1)
                return true;
            else
                return false;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                statement.close();
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }
    public ArrayList<Paziente> getPatientList() {
        ArrayList<Paziente> listaPazienti = new ArrayList<>();
        connectToDb();

        try {
            //Seleziona tutti i pazienti non ricoverati attualmente (Pe rnon generare duplicati) -- POST
            String query = "SELECT pazienti.* " +
                    "FROM pazienti " +
                    "EXCEPT " +
                    "SELECT DISTINCT pazienti.* FROM pazienti, cartelle_cliniche WHERE cartelle_cliniche.codiceSan_paziente = pazienti.codice_sanitario AND cartelle_cliniche.chiusa = '0'";
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
            while(resultSet.next()){
                Paziente temp = new Paziente();
                //System.out.println(resultSet.getString("codice_sanitario"));
                temp.setCodSanitario(resultSet.getString("codice_sanitario"));
                temp.setCognome(resultSet.getString("cognome"));
                temp.setNome(resultSet.getString("nome"));
                temp.setLuogo_nascita(resultSet.getString("luogo_nascita"));
                temp.setData_nascita(Date.valueOf(resultSet.getString("data_nascita")));
                //System.out.println(temp.toString());
                listaPazienti.add(temp);
            }
        }catch (SQLException e){
            System.err.println("Errore nella ricerca paziente!");
            close();
        }
        finally {
            close();
        }

        return listaPazienti;
    }
    public ArrayList<Farmaco> getSearchedFarmaco(String denominazione, String codAIC, String principioAttivo){
        int codAICInt;
        ArrayList<Farmaco> farmaci = new ArrayList<>();

        if(codAIC != null && codAIC != "")
            codAICInt = Integer.valueOf(codAIC);
        String query = "SELECT * FROM `farmaci` WHERE " +
                "INSTR(`Principio_Attivo`, '" + principioAttivo + "') > 0 AND " +
                "INSTR(`Codice_AIC`, '" + codAIC + "') > 0 AND " +
                "INSTR(`Denominazione_e_Confezione`,'" + denominazione + "') > 0;";
        System.out.println("QUERY: "+query);

        this.connectToDb();

        try {
            statement = this.connection.createStatement();
            resultSet = statement.executeQuery(query);

            String codiceAic;
            while(resultSet.next()){
                Farmaco temp = new Farmaco();
                codiceAic = resultSet.getString("Codice_AIC");
                temp.setPrincipioAttivo(resultSet.getString("Principio_Attivo"));
                temp.setCodAIC(Integer.parseInt(codiceAic));
                temp.setNomeCommerciale(resultSet.getString("Denominazione_e_Confezione"));
                temp.setDescrizione(resultSet.getString("Descrizione_Gruppo_Equivalenza"));
                temp.setTitolareAIC(resultSet.getString("Titolare_AIC"));
                farmaci.add(temp);
            }

        }catch(SQLException e){
            System.out.println("Errore esecuzione query ricerca farmaci: \n" + e);
        }finally {
            close();
        }
        return farmaci;
    }
    public ArrayList<Ricovero> getRicoveriArchiviatiFiltrati(String nomePaziente, String cognomePaziente, String codiceSanitarioPaziente, LocalDate dataInizio, LocalDate dataFine){
        String query = "SELECT * FROM tidb.cartelle_cliniche, tidb.pazienti " +
                "WHERE cartelle_cliniche.chiusa = '1' " +
                "AND cartelle_cliniche.codiceSan_paziente = pazienti.codice_sanitario " +
                "AND INSTR(nome, '" + nomePaziente + "') > 0 " +
                "AND INSTR(cognome, '" + cognomePaziente + "') > 0 " +
                "AND INSTR(codice_sanitario, '" + codiceSanitarioPaziente + "') > 0";


        if(dataInizio != null){
            query = query + " AND dataInizioRicovero >= '" + Date.valueOf(dataInizio).toString() +"' ";
        }
        if(dataFine != null){
            query = query + " AND dataFineRicovero <= '" + Date.valueOf(dataFine).toString() + "' ";
        }
            System.out.println("QUERY: "+query);

            this.connectToDb();

            try {
                statement = connection.createStatement();
                resultSet = statement.executeQuery(query);
                ArrayList<Ricovero> ricoveriPassatiFiltrati = new ArrayList<>();
                while(resultSet.next()){
                    try {
                        Ricovero temp = new Ricovero(
                                Integer.valueOf(resultSet.getString("id_cartClinica")),
                                true,
                                new Paziente(resultSet.getString("nome"), resultSet.getString("cognome"), resultSet.getString("codiceSan_paziente"), java.sql.Date.valueOf(resultSet.getString("data_nascita")), resultSet.getString("luogo_nascita")),
                                Date.valueOf(resultSet.getString("dataInizioRicovero")),
                                Date.valueOf(resultSet.getString("dataFineRicovero")),
                                Integer.valueOf(resultSet.getString("durata_ossTerapia")),
                                Integer.valueOf(resultSet.getString("durata_ventAssistita"))
                        );
                        ricoveriPassatiFiltrati.add(temp);
                    }catch (IllegalArgumentException e){
                        return null;
                    }
                }
                return ricoveriPassatiFiltrati;
            }catch(SQLException e){
                System.out.println("Errore esecuzione query ricerca pazienti: \n" + e);
                return null;
            }finally {
                close();
            }

    }
    public ArrayList<Paziente> getSearchedPaziente(String nome, String cognome, String codSanitario){

        ArrayList<Paziente> listaPazienti = new ArrayList<>();
        System.out.println("->"+nome.length()+" "+ cognome.length());
        int i = 0; //Mi serve per contare quanti parametri ho e per sapere se devo aggiungere "," nella mia query, se ho almeno un elemento di ricerca il secondo sarà preceduto da una ",".
        //ES: [...] WHERE nome='Fabio', cognome='Rossi'; || [...] WHERE cognome='Rossi';
        //Il carattere "," viene aggiunto solo se il parametro di ricerca che sto aggiungendo è il sucessivo di un altro elemento.
        String query = "SELECT * " +
                "FROM pazienti " +
                "WHERE ";
        if(nome.length() > 0){
            query = query.concat(" nome= '"+ nome +"'");
            ++i;
        }
        if(i>0) query.concat(", ");
        if(cognome.length() > 0){
            query = query.concat(" cognome= '"+ cognome +"' ");
            ++i;
        }
        if(i>0) query.concat(", ");
        if(codSanitario.length() > 0){
            query = query.concat(" codice_sanitario='"+ codSanitario +"'");
        }
        connectToDb();

        try {
            System.out.println(query);
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
            while(resultSet.next()){
                Paziente temp = new Paziente();
                System.out.println(resultSet.getString("codice_sanitario"));
                temp.setCodSanitario(resultSet.getString("codice_sanitario"));
                temp.setCognome(resultSet.getString("cognome"));
                temp.setNome(resultSet.getString("nome"));
                temp.setLuogo_nascita(resultSet.getString("luogo_nascita"));
                temp.setData_nascita(Date.valueOf(resultSet.getString("data_nascita")));
                //System.out.println(temp.toString());
                listaPazienti.add(temp);
            }
        }catch (SQLException e){
            System.err.println("Errore nella ricerca paziente!");
            close();
        }
        finally {
            close();
        }

        return listaPazienti;
    }
    public List<Ricovero> getPatientsHospitalized(){
        List<Ricovero> ricoveriList = new LinkedList<>();
        this.connectToDb();
        try {
            statement = connection.createStatement();
            //Query che mi restituisce la lista dei pazienti attualmente ricoverati in teerapia intensiva
            resultSet = statement.executeQuery(
                    "SELECT cartelle_cliniche.id_cartClinica, cartelle_cliniche.dataInizioRicovero, cartelle_cliniche.dataFineRicovero, cartelle_cliniche.durata_ossTerapia, cartelle_cliniche.durata_ventAssistita, " +
                            "pazienti.nome, pazienti.cognome, pazienti.codice_sanitario, pazienti.luogo_nascita, pazienti.data_nascita " +
                            "FROM pazienti, cartelle_cliniche " +
                            "WHERE cartelle_cliniche.chiusa = 0 AND " +
                            "cartelle_cliniche.codiceSan_paziente = pazienti.codice_sanitario;");

            while(resultSet.next()){
                Paziente temp = new Paziente();
                temp.setCodSanitario(resultSet.getString("codice_sanitario"));
                temp.setCognome(resultSet.getString("cognome"));
                temp.setNome(resultSet.getString("nome"));
                temp.setLuogo_nascita(resultSet.getString("luogo_nascita"));
                temp.setData_nascita(Date.valueOf(resultSet.getString("data_nascita")));

                Ricovero temp2 = new Ricovero();
                temp2.setIdRicovero(Integer.valueOf(resultSet.getString("id_cartClinica")));
                temp2.setPazienteRicoverato(temp);
                temp2.setChiusa(false);
                temp2.setDataInizioRicovero(Date.valueOf(resultSet.getString("dataInizioRicovero")));
                try {
                    temp2.setDataFineRicovero(Date.valueOf(resultSet.getString("dataFineRicovero")));
                }catch(IllegalArgumentException e) {
                    temp2.setDataFineRicovero(null);
                }
                temp2.setDurataVentilazAssistita(Integer.valueOf(resultSet.getString("durata_ventAssistita")));
                temp2.setDurataOssTerapia(Integer.valueOf(resultSet.getString("durata_ossTerapia")));
                ricoveriList.add(temp2);
            }
        }catch (SQLException e){
            System.err.println(e.toString());
        }
        this.close();
        return ricoveriList;
    }
    public ArrayList<Ricovero> getRicoveriArchiviati(){
        String query = "SELECT * FROM tidb.cartelle_cliniche, tidb.pazienti WHERE cartelle_cliniche.chiusa = '1' AND cartelle_cliniche.codiceSan_paziente = pazienti.codice_sanitario;";

        this.connectToDb();

        try{
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
            ArrayList<Ricovero> ricoveriPassati = new ArrayList<>();
            while(resultSet.next()){
                try {
                    Ricovero temp = new Ricovero(
                            Integer.valueOf(resultSet.getString("id_cartClinica")),
                            true,
                            new Paziente(resultSet.getString("nome"), resultSet.getString("cognome"), resultSet.getString("codiceSan_paziente"), java.sql.Date.valueOf(resultSet.getString("data_nascita")), resultSet.getString("luogo_nascita")),
                            Date.valueOf(resultSet.getString("dataInizioRicovero")),
                            Date.valueOf(resultSet.getString("dataFineRicovero")),
                            Integer.valueOf(resultSet.getString("durata_ossTerapia")),
                            Integer.valueOf(resultSet.getString("durata_ventAssistita"))
                    );
                    ricoveriPassati.add(temp);
                }catch (IllegalArgumentException e){
                    return null;
                }
            }
            this.close();
            return ricoveriPassati;
        }catch (SQLException e){
            e.printStackTrace();
        }
        this.close();
        return null;
    }
    public Ricovero getRicovero(Integer codRicovero){
        this.connectToDb();
        try {
            statement = connection.createStatement();
            //Query che mi restituisce la lista dei pazienti attualmente ricoverati in teerapia intensiva
            resultSet = statement.executeQuery(
                    "SELECT cartelle_cliniche.id_cartClinica, cartelle_cliniche.dataInizioRicovero, cartelle_cliniche.dataFineRicovero, cartelle_cliniche.durata_ossTerapia, cartelle_cliniche.durata_ventAssistita, " +
                            "pazienti.nome, pazienti.cognome, pazienti.codice_sanitario, pazienti.luogo_nascita, pazienti.data_nascita " +
                            "FROM pazienti, cartelle_cliniche " +
                            "WHERE cartelle_cliniche.chiusa = 0 " +
                            "AND cartelle_cliniche.id_cartClinica = " + codRicovero + " "+
                            "AND cartelle_cliniche.codiceSan_paziente = pazienti.codice_sanitario;");

            /*System.out.println("SELECT cartelle_cliniche.id_cartClinica, cartelle_cliniche.dataInizioRicovero, cartelle_cliniche.dataFineRicovero, cartelle_cliniche.durata_ossTerapia, cartelle_cliniche.durata_ventAssistita, " +
                    "pazienti.nome, pazienti.cognome, pazienti.codice_sanitario, pazienti.luogo_nascita, pazienti.data_nascita " +
                    "FROM pazienti, cartelle_cliniche " +
                    "WHERE cartelle_cliniche.chiusa = 0 " +
                    "AND cartelle_cliniche.id_cartClinica = " + codRicovero + " "+
                    "AND cartelle_cliniche.codiceSan_paziente = pazienti.codice_sanitario;");*/

            Paziente paziente = new Paziente();
            Ricovero ricovero = new Ricovero();

            if(resultSet.next()){
                paziente.setCodSanitario(resultSet.getString("codice_sanitario"));
                paziente.setCognome(resultSet.getString("cognome"));
                paziente.setNome(resultSet.getString("nome"));
                paziente.setLuogo_nascita(resultSet.getString("luogo_nascita"));
                paziente.setData_nascita(Date.valueOf(resultSet.getString("data_nascita")));
                ricovero.setIdRicovero(Integer.valueOf(resultSet.getString("id_cartClinica")));
                ricovero.setPazienteRicoverato(paziente);
                ricovero.setChiusa(false);
                ricovero.setDataInizioRicovero(Date.valueOf(resultSet.getString("dataInizioRicovero")));
                try {
                    ricovero.setDataFineRicovero(Date.valueOf(resultSet.getString("dataFineRicovero")));
                }catch(IllegalArgumentException e) {
                    ricovero.setDataFineRicovero(null);
                }
                ricovero.setDurataVentilazAssistita(Integer.valueOf(resultSet.getString("durata_ventAssistita")));
                ricovero.setDurataOssTerapia(Integer.valueOf(resultSet.getString("durata_ossTerapia")));
            }else{return null;}
            return ricovero;

        }catch (SQLException e){
            this.close();
            System.err.println(e.toString());
            return null;
        }

    }
    public Ricovero getRicoveroArchiviato(Integer codRicovero){
        this.connectToDb();
        try {
            statement = connection.createStatement();
            //Query che mi restituisce la lista dei pazienti attualmente ricoverati in teerapia intensiva
            resultSet = statement.executeQuery(
                    "SELECT cartelle_cliniche.id_cartClinica, cartelle_cliniche.dataInizioRicovero, cartelle_cliniche.dataFineRicovero, cartelle_cliniche.durata_ossTerapia, cartelle_cliniche.durata_ventAssistita, " +
                            "pazienti.nome, pazienti.cognome, pazienti.codice_sanitario, pazienti.luogo_nascita, pazienti.data_nascita " +
                            "FROM pazienti, cartelle_cliniche " +
                            "WHERE cartelle_cliniche.chiusa = 1 " +
                            "AND cartelle_cliniche.id_cartClinica = " + codRicovero + " "+
                            "AND cartelle_cliniche.codiceSan_paziente = pazienti.codice_sanitario;");

            System.out.println("SELECT cartelle_cliniche.id_cartClinica, cartelle_cliniche.dataInizioRicovero, cartelle_cliniche.dataFineRicovero, cartelle_cliniche.durata_ossTerapia, cartelle_cliniche.durata_ventAssistita, " +
                    "pazienti.nome, pazienti.cognome, pazienti.codice_sanitario, pazienti.luogo_nascita, pazienti.data_nascita " +
                    "FROM pazienti, cartelle_cliniche " +
                    "WHERE cartelle_cliniche.chiusa = 0 " +
                    "AND cartelle_cliniche.id_cartClinica = " + codRicovero + " "+
                    "AND cartelle_cliniche.codiceSan_paziente = pazienti.codice_sanitario;");

            Paziente paziente = new Paziente();
            Ricovero ricovero = new Ricovero();

            if(resultSet.next()){
                paziente.setCodSanitario(resultSet.getString("codice_sanitario"));
                paziente.setCognome(resultSet.getString("cognome"));
                paziente.setNome(resultSet.getString("nome"));
                paziente.setLuogo_nascita(resultSet.getString("luogo_nascita"));
                paziente.setData_nascita(Date.valueOf(resultSet.getString("data_nascita")));
                ricovero.setIdRicovero(Integer.valueOf(resultSet.getString("id_cartClinica")));
                ricovero.setPazienteRicoverato(paziente);
                ricovero.setChiusa(false);
                ricovero.setDataInizioRicovero(Date.valueOf(resultSet.getString("dataInizioRicovero")));
                try {
                    ricovero.setDataFineRicovero(Date.valueOf(resultSet.getString("dataFineRicovero")));
                }catch(IllegalArgumentException e) {
                    ricovero.setDataFineRicovero(null);
                }
                ricovero.setDurataVentilazAssistita(Integer.valueOf(resultSet.getString("durata_ventAssistita")));
                ricovero.setDurataOssTerapia(Integer.valueOf(resultSet.getString("durata_ossTerapia")));
            }else{return null;}
            return ricovero;

        }catch (SQLException e){
            this.close();
            System.err.println(e.toString());
            return null;
        }

    }
    public ArrayList<Prescrizione> getPrescrizioni(Ricovero ricovero) {
        String query = "SELECT prescrizioni.*, medici.nome, medici.cognome, medici.cod_fiscale, farmaci.Principio_Attivo, farmaci.Denominazione_e_Confezione, farmaci.Codice_AIC, prescrizioni.data_prescrizione, prescrizioni.durata_terapia, prescrizioni.num_dosi_giornaliere, prescrizioni.qta_dose " +
                "FROM prescrizioni, cartelle_cliniche, medici, farmaci " +
                "WHERE cartelle_cliniche.id_cartClinica = '" + ricovero.getIdRicovero() +"' " +
                "AND prescrizioni.codCartellaClinica = cartelle_cliniche.id_cartClinica AND prescrizioni.medico_resp = medici.cod_fiscale AND prescrizioni.codiceFarmaco = farmaci.Codice_AIC;";
        System.out.println(query);
        ArrayList<Prescrizione> lista = new ArrayList<>();
        connectToDb();
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
            //DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
            Date now = new Date(System.currentTimeMillis());
            while(resultSet.next()){
                Farmaco tempFarmaco = new Farmaco(
                        resultSet.getString("Denominazione_e_Confezione"),
                        Integer.valueOf(resultSet.getString("codiceFarmaco")),
                        resultSet.getString("Principio_Attivo")
                );
                Medico tempMedico = new Medico(
                        resultSet.getString("nome"),
                        resultSet.getString("cognome"),
                        new CodFiscale(resultSet.getString("cod_fiscale"))
                );
                Prescrizione temp = new Prescrizione(
                        tempFarmaco,
                        Date.valueOf(resultSet.getString("data_prescrizione")),
                        Integer.valueOf(resultSet.getString("durata_terapia")),
                        Integer.valueOf(resultSet.getString("num_dosi_giornaliere")),
                        Float.valueOf(resultSet.getString("qta_dose")),
                        tempMedico,
                        ricovero,
                        Integer.valueOf(resultSet.getString("cod_prescrizione"))
                );

                Calendar c = Calendar.getInstance();
                c.setTime(temp.getDataPrescrizione());
                //Tutte le prescrizioni "scadute" da almeno 1 giorno dalla data di fine terapia, non verranno visualizzate per la somministrazione
                c.add(Calendar.DATE, temp.getDurataTerapia()+1);

                if(now.before(c.getTime())){
                    lista.add(temp);
                }

            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return lista;
    }
    public ArrayList<Somministrazione> getSomministrazioni(Ricovero ricovero) {
        //SELECT * FROM tidb.somministrazioni WHERE somministrazioni.dataSomministrazione > "2022/01/07"

        Calendar dataPartenza = (Calendar.getInstance());
        LocalDateTime lc = LocalDateTime.now();

        dataPartenza.set(lc.getYear(), (lc.getMonthValue()-1), lc.getDayOfMonth(), lc.getHour(), lc.getMinute());
        //dataPartenza.setTime(new Date(System.currentTimeMillis()));
        dataPartenza.add(Calendar.DATE, (-2));

        SimpleDateFormat dft = new SimpleDateFormat();
        dft.applyPattern("yyyy-MM-dd HH:mm:ss");


        //OLD GENERAL QUERY String query = "SELECT * FROM tidb.somministrazioni WHERE somministrazioni.dataSomministrazione > '" + dft.format(dataPartenza.getTime())  + "'";
        String query = "SELECT somministrazioni.* FROM tidb.somministrazioni, tidb.cartelle_cliniche, prescrizioni " +
                "WHERE somministrazioni.dataSomministrazione > '" + dft.format(dataPartenza.getTime()) + "' " +
                "AND somministrazioni.numPrescrizione = prescrizioni.cod_prescrizione " +
                "AND prescrizioni.codCartellaClinica = cartelle_cliniche.id_cartClinica " +
                "AND cartelle_cliniche.id_cartClinica = " + ricovero.getIdRicovero() + ";";

        System.out.println(query);

        ArrayList<Somministrazione> lista = new ArrayList<>();
        connectToDb();
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);

            while(resultSet.next()){
                Somministrazione somm = new Somministrazione(resultSet.getString("cod_infermiere"), Timestamp.valueOf(resultSet.getString("dataSomministrazione")), Float.valueOf(resultSet.getString("doseSomministrata")), resultSet.getString("note_stato"), Integer.valueOf(resultSet.getString("numPrescrizione")));
                System.out.println("Somm--> " + somm.getNumPrescrizione());
                lista.add(somm);
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return lista;
    }
    public Medico getMedicoFromLoggedUser(LoggedUser user){
        connectToDb();
        try{
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM tidb.medici WHERE cod_fiscale = '" + user.getCodFiscale() + "'");
            if(resultSet.next())
                return new Medico(resultSet.getString("nome"), resultSet.getString("cognome"), resultSet.getString("cod_fiscale"));
        }catch (SQLException e){
            System.err.println(e.toString());
        }
        return null;
    }
    public LoggedUser getUserAccount() {return userAccount;}
    public boolean setAlarm(Ricovero ricovero, LoggedUser userAccount, Allarme alarm, Time time, String intervento) {
        this.connectToDb();
        try {
            String query = "INSERT INTO allarme VALUES ('"+ricovero.getIdRicovero()+"', '"+alarm.getTipoAllarme().toString()+"', '"+alarm.getLvlAlarm()+"', '"+ alarm.getMomentoGenerazioneAllarme()+"', '"+time.toString()+"', '"+userAccount.getCodFiscale()+"', '"+intervento+"')";
            System.out.println(query);
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
            return true;
        }catch (SQLException e){
            e.printStackTrace();
            close();
            return false;
        }
    }

}