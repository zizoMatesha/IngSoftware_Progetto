package sistemaMonitoraggio;

import java.sql.*;
import java.util.ArrayList;
import srcClass.*;

public class dataBaseForMonitor{
    private Connection connection = null;
    private Statement statement = null;
    private ResultSet resultSet = null;
    private int accountType = 0;
    private String nomeUtente;
    private String codFiscale;
    private LoggedUser userAccount;

    public dataBaseForMonitor(){}

    public void connectToDb(){
        String pathDB = new String("jdbc:mariadb://localhost:3306/tidb?user=root&pwd=");    //root user: ha tutti i privilegi dimodifica e/o aggiornamento del db
        try {
            this.connection = DriverManager.getConnection(pathDB);
        } catch (Exception e) {
            System.out.println("Login Failed! [...]\n");
            e.printStackTrace();
        }
    }

    // You need to close the resultSet
    private void close() {
        try {
            if (resultSet != null) { resultSet.close();}

            if (statement != null) { statement.close(); }

            if (connection != null) { connection.close(); }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getPazientiRicoverati(){
        connectToDb();
        String query = "SELECT * " +
                "FROM cartelle_cliniche " +
                "WHERE chiusa = 0;";

        try {
            statement = connection.createStatement();
            System.out.println(query);
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
            int numPazinetiRicoverati = 0;
            while(resultSet.next()){
                ++numPazinetiRicoverati;
            }
            close();
            return numPazinetiRicoverati;
        }catch (SQLException e){
            System.err.println("Errore nella ricerca paziente!");
            close();
        }
        finally {
            close();
        }
        return -1;
    }

    public ArrayList<Ricovero> getRicoveri(){
        String query = "SELECT * FROM tidb.cartelle_cliniche WHERE `chiusa` = 0";
        ArrayList<Ricovero> listaPazientiRicoverati = new ArrayList<>();

        try{
            connectToDb();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);

            while(resultSet.next()){
                Ricovero ricoveroAttuale = new Ricovero();

                Paziente temp = new Paziente();

                //Paziente
                ResultSet pazienteResult = statement.executeQuery("SELECT * FROM tidb.pazienti WHERE pazienti.codice_sanitario = '" + resultSet.getString("codiceSan_paziente") + "';");
                if(pazienteResult.next()) {
                    temp.setCodSanitario(pazienteResult.getString("codice_sanitario"));
                    temp.setCognome(pazienteResult.getString("cognome"));
                    temp.setNome(pazienteResult.getString("nome"));
                    temp.setLuogo_nascita(pazienteResult.getString("luogo_nascita"));
                    temp.setData_nascita(Date.valueOf(pazienteResult.getString("data_nascita")));
                }else{new SQLException("Errore, paziente ricoverato, ma non presente nel database");}

                if(Integer.valueOf(resultSet.getString("chiusa")) == 0) ricoveroAttuale.setChiusa(true);
                else{ricoveroAttuale.setChiusa(false);}

                ricoveroAttuale.setDataInizioRicovero(Date.valueOf(resultSet.getString("dataInizioRicovero")));
                ricoveroAttuale.setDurataOssTerapia(Integer.valueOf(resultSet.getString("durata_ossTerapia")));
                ricoveroAttuale.setDurataVentilazAssistita(Integer.valueOf(resultSet.getString("durata_ventAssistita")));
                ricoveroAttuale.setPazienteRicoverato(temp);
                ricoveroAttuale.setIdRicovero(Integer.valueOf(resultSet.getString("id_cartClinica")));
                listaPazientiRicoverati.add(ricoveroAttuale);
            }

        }catch(SQLException e){
            e.printStackTrace();
            System.out.println("Errore durante il caricamento dei ricoveri");
            Thread.currentThread().interrupt();
        }finally {
            close();
        }
        return listaPazientiRicoverati;
    }

}
