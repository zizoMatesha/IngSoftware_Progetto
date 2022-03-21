package desClass;

import db.DataBase;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import srcClass.*;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.sql.Timestamp;
import java.util.*;

public class patientController implements Serializable{

    @FXML
    Label nomeCognomePaziente;
    @FXML
    Button SomministraFarmaco;
    @FXML
    Button PrescriviFarmaco;
    @FXML
    Button buttonTemp;
    @FXML
    Button buttonBP;
    @FXML
    Button chiudiRicoveroButton;
    @FXML
    Label sys;
    @FXML
    Label dias;
    @FXML
    Label SpO2;
    @FXML
    Label temp;
    @FXML
    Label ecgField;
    @FXML
    TableView somministrazioniTable;
    @FXML
    LineChart<String, Float> tempChart;
    @FXML
    LineChart<String, Float> bloodPressureChart;
    @FXML
    NumberAxis yaxis;
    @FXML
    NumberAxis yaxisBP;
    @FXML
    AnchorPane anchorTemp;
    @FXML
    AnchorPane anchorBP;
    @FXML
    AnchorPane contentAnchorPane;

    private Ricovero ricovero;
    private LoggedUser userAccount;
    private int typeOfAccount;

    private Task task;
    private Task aggiornaVisualeParametri;

    private Thread taskThread;
    private Task taskUpdatePlot;
    private Thread taskUpdatePlotThread;
    private Thread aggiornaVisualeThread;
    private XYChart.Series<String, Float> temperatureSeries;
    private XYChart.Series<String, Float> bloodPresSisSeries;
    private XYChart.Series<String, Float> bloodPresDiasSeries;

    //For communication with server
    private Socket s = null;
    private ObjectInputStream dis = null;
    private ObjectOutputStream dos = null;
    private boolean flag = false;

    public patientController() {}

    public void initData(String nome, String cognome, Ricovero ricovero, int typeOfAccount, LoggedUser userAccount){
       /* nomePaziente.setText(nome);
        cognomePaziente.setText(cognome);*/
        nomeCognomePaziente.setText(nome + " " + cognome);
        this.ricovero = ricovero;
        this.typeOfAccount = typeOfAccount;
        this.userAccount = userAccount;

        if(userAccount.getAccountType() == 1)       //medico
            PrescriviFarmaco.setVisible(true);
        else if(userAccount.getAccountType() == 2)  //infermiere
            SomministraFarmaco.setVisible(true);
        else if(userAccount.getAccountType() == 3) {
            PrescriviFarmaco.setVisible(true);
            chiudiRicoveroButton.setVisible(true);
        }


        loadSomministrazioniTable();

        temperatureSeries = new XYChart.Series();
        temperatureSeries.setName("Temperatura");
        bloodPresSisSeries = new XYChart.Series();
        bloodPresSisSeries.setName("Sistolica");
        bloodPresDiasSeries = new XYChart.Series();
        bloodPresDiasSeries.setName("Diastolica");

        tempChart.getYAxis().setAutoRanging(false);
        yaxis.setLowerBound(35);
        yaxis.setUpperBound(43.0);
        yaxis.setTickUnit(1);

        bloodPressureChart.getYAxis().setAutoRanging(false);
        yaxisBP.setLowerBound(75);
        yaxisBP.setUpperBound(100.0);
        yaxisBP.setTickUnit(5);


        tempChart.setAnimated(false);
        tempChart.setAxisSortingPolicy(LineChart.SortingPolicy.X_AXIS);

        bloodPressureChart.setAnimated(false);
        bloodPressureChart.setAxisSortingPolicy(LineChart.SortingPolicy.X_AXIS);

        tempChart.getData().add(temperatureSeries);
        bloodPressureChart.getData().add(bloodPresDiasSeries);
        bloodPressureChart.getData().add(bloodPresSisSeries);

        tempChart.setAxisSortingPolicy(LineChart.SortingPolicy.X_AXIS);
        bloodPressureChart.setAxisSortingPolicy(LineChart.SortingPolicy.X_AXIS);

        task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                return null;
            }

            @Override public void run() {

                //Effettuo il collegamento con il monitor della stanza del ricovero indicato
                try {
                    if(connectServer() == false){
                        System.err.println("Errore nel collegamento al server!\nChiudo immediatamente la task");
                        return;
                    }

                    System.out.println("Connected");

                    ObjectOutputStream dos = new ObjectOutputStream(s.getOutputStream());
                    ObjectInputStream dis = new ObjectInputStream(s.getInputStream());
                    dos.writeObject(ricovero);
                    dos.flush();

                    if (dis.readBoolean()) {
                        String risposta = "<Client response> Invia parametri";
                        //System.out.println(risposta);
                        dos.writeObject(risposta);
                        dos.flush();
                        parametriVitali parametri = new parametriVitali(-1, -1, -1, -1, -1);
                        while(!task.isCancelled()){
                            try {
                                if (!s.isClosed()) {
                                    boolean flag = true;
                                    do {
                                        String lineFromSocket = (String) dis.readObject();
                                        System.out.println(lineFromSocket);

                                        if (lineFromSocket.contains("end")){
                                            flag = false;
                                            //System.out.println("PARAMETRI FINALI" +parametri.getEcg() + " " + parametri.getSistolica() + " " + parametri.getDiastolica() + " " + parametri.getSatOss() + " " + parametri.getTemperatura());

                                            Platform.runLater(()->{
                                                if(parametri.getSistolica() != -1) sys.setText(parametri.getSistolica() + "");
                                                if(parametri.getDiastolica() != -1) dias.setText(parametri.getDiastolica() + "");
                                                if(parametri.getSatOss() != -1) SpO2.setText(parametri.getSatOss() + "");
                                                if(parametri.getTemperatura() != -1) temp.setText(parametri.getTemperatura() + "");
                                                if(parametri.getEcg() != -1) ecgField.setText(parametri.getEcg() + "");
                                                parametri.clear(); //setta i parametri a -1, così al prossimo invio di dati dal monitor verranno aggiornati anche nella visulizzazione i parametri nuovi

                                            });

                                            dos.writeBoolean(true);
                                            dos.flush();
                                        }
                                        else {
                                            String[] buffer = lineFromSocket.split(" ");

                                            if(parametri.getEcg()           == -1 && (!buffer[2].contains("-")))                  parametri.setEcg(Integer.valueOf(buffer[2]));
                                            if(parametri.getSistolica()     == -1 && (!buffer[3].contains("-")))                  parametri.setSistolica(Integer.valueOf(buffer[3]));
                                            if(parametri.getDiastolica()    == -1 && (!buffer[4].contains("-")))                  parametri.setDiastolica(Integer.valueOf(buffer[4]));
                                            if(parametri.getSatOss()        == -1 && (!buffer[5].contains("-")))                  parametri.setSatOss(Integer.valueOf(buffer[5]));
                                            if(parametri.getTemperatura()   == -1 && (!buffer[buffer.length-1].contains("-")))    parametri.setTemperatura(Float.valueOf(buffer[6]));

                                            //System.out.println("ADDED: " + buffer[1] + " " + Float.valueOf(buffer[(buffer.length) - 1]));
                                            if(!buffer[buffer.length-1].contains("-")) {
                                                Platform.runLater(() -> {
                                                    try {
                                                        temperatureSeries.getData().add(new XYChart.Data<String, Float>(buffer[1].substring(0,5), Float.valueOf(buffer[(buffer.length) - 1])));
                                                        temperatureSeries.getData().sort((o1, o2) -> o1.getXValue().compareTo(o2.getXValue()));
                                                    } catch (NumberFormatException e) { /*Nel caso in cui la temperatura sia pari a null ovvero sul file txt pari a "-" */}
                                                });
                                            }
                                            if(!buffer[3].contains("-")){
                                                Platform.runLater(() -> {
                                                    try {
                                                        bloodPresSisSeries.getData().add(new XYChart.Data<String, Float>(buffer[1].substring(0,5), Float.valueOf(buffer[3])));
                                                        bloodPresSisSeries.getData().sort((o1, o2) -> o1.getXValue().compareTo(o2.getXValue()));
                                                    } catch (NumberFormatException e) { /*Nel caso in cui la temperatura sia pari a null ovvero sul file txt pari a "-" */}
                                                });
                                            }

                                            if(!buffer[4].contains("-")){
                                                Platform.runLater(() -> {
                                                    try {
                                                        bloodPresDiasSeries.getData().add(new XYChart.Data<String, Float>(buffer[1].substring(0,5), Float.valueOf(buffer[4])));
                                                        bloodPresDiasSeries.getData().sort((o1, o2) -> o1.getXValue().compareTo(o2.getXValue()));
                                                    } catch (NumberFormatException e) { /*Nel caso in cui la temperatura sia pari a null ovvero sul file txt pari a "-" */}
                                                });
                                            }

                                            dos.writeBoolean(true);
                                            dos.flush();
                                        }
                                    }while (flag);
                                }
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (SocketException e) {
                                System.out.println("Chiusura delle TASK!");
                                return;
                            } catch (IOException e) {
                                e.printStackTrace();
                                return;
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }


                        }
                    }

                        dos.writeBoolean(false);
                        dos.flush();
                        return;
                    //}

                } catch (IOException error) {
                    System.out.println(error.toString());
                    System.out.println("----------------Chiusura TASK-------------------");
                    return;
                }
            }
        };

        //Start Thread per la comunicazione con il server che genera/memorizza i parametri vitali del paziente
        //monitorPatient monitorPaziente = new monitorPatient(ricovero, this);
        taskThread = new Thread(task);
        taskThread.start();



    }

    public void prescriviFarmaco(){
        Stage stgPrescrizione = new Stage();
        stgPrescrizione.setTitle("Prescrizione farmaco - " + userAccount.getCodFiscale());
        FXMLLoader loader = new FXMLLoader(getClass().getResource("prescrFarmaco.fxml"));

        try {
            stgPrescrizione.setScene(new Scene((AnchorPane) loader.load()));
            loader.<prescrizioneFarmacoController>getController().filtraFarmaci();
            loader.<prescrizioneFarmacoController>getController().userAccount = userAccount;
            loader.<prescrizioneFarmacoController>getController().ricovero = ricovero;
        }catch (IOException e){
            System.err.println("Errore caricamento pagina prescrizione farmaco!\n" + e.toString());
        }
        stgPrescrizione.show();
    }

    public void somministraFarmaco(){
        Stage stgSomministrazione = new Stage();
        stgSomministrazione.setTitle("Somministrazione farmaci - " + userAccount.getCodFiscale());
        FXMLLoader loader = new FXMLLoader(getClass().getResource("somministrazione.fxml"));

        try {
            stgSomministrazione.setScene(new Scene((AnchorPane) loader.load()));
            loader.<somministraFarmacoController>getController().initData(userAccount, ricovero);
            loader.<somministraFarmacoController>getController().loadPrescrizioni();

        }catch (IOException e){
            System.err.println("Errore caricamento pagina prescrizione farmaco!\n" + e.toString());
        }
        //Una volta chiusa la finestra delle somministrazioni in automatico ricarico la tabella delle somministrazioni
        stgSomministrazione.setOnHiding(new EventHandler<WindowEvent>() {

            @Override
            public void handle(WindowEvent event) {
                Platform.runLater(new Runnable() {

                    @Override
                    public void run() {
                        loadSomministrazioniTable();
                    }
                });
            }
        });
        stgSomministrazione.show();
    }

    public void closeTask() {
        if(taskThread != null)
            taskThread.interrupt();
        if(task != null) {
            task.cancel();
            if(s != null) {
                try {
                    s.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if(taskUpdatePlotThread != null)
            taskUpdatePlotThread.interrupt();
        if(taskUpdatePlot != null) {
            taskUpdatePlot.cancel();
            if(s != null) {
                try {
                    s.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


        if(aggiornaVisualeParametri != null) {
            aggiornaVisualeParametri.cancel();
        }

    }

    public boolean connectServer(){
        try {
            InetAddress ip = InetAddress.getByName("localhost");
            s = new Socket(ip, 50152);

        } catch (IOException e) {
            e.printStackTrace();
            return false;}
        return true;
    }

    public void closeAll(){
        // closing resources
        try {
            if(dis != null) dis.close();
            if(dos != null) dos.close();
            if(s != null) s.close();
            task.cancel();
        } catch (IOException | IllegalArgumentException | NullPointerException e) {
            System.out.println("Errore chiusura Task!-->"); e.printStackTrace();
        }

    }

    public void loadSomministrazioniTable(){
        somministrazioniTable.getItems().clear();
        somministrazioniTable.getColumns().clear();

        DataBase db = new DataBase();
        ArrayList<Somministrazione> somministrazioni = db.getSomministrazioni(ricovero);

        TableColumn<Somministrazione, String> cod_infermiere =              new TableColumn("Infermiere resp");
        TableColumn<Somministrazione, Timestamp> dataSomministrazione =     new TableColumn("Data");
        TableColumn<Somministrazione, Float> doseSomministrata =            new TableColumn("Dose");
        TableColumn<Somministrazione, Integer> numPrescrizione =            new TableColumn("Prescrizione num");
        TableColumn<Somministrazione, String> note_stato =                  new TableColumn("Note");

        cod_infermiere.setCellValueFactory(                                 new PropertyValueFactory<>("cod_infermiere"));
        dataSomministrazione.setCellValueFactory(                           new PropertyValueFactory<>("dataSomministrazione"));
        doseSomministrata.setCellValueFactory(                              new PropertyValueFactory<>("doseSomministrata"));
        numPrescrizione.setCellValueFactory(                                new PropertyValueFactory<>("numPrescrizione"));
        note_stato.setCellValueFactory(                                     new PropertyValueFactory<>("note_stato"));


        somministrazioniTable.getColumns().add(cod_infermiere);
        somministrazioniTable.getColumns().add(dataSomministrazione);
        somministrazioniTable.getColumns().add(doseSomministrata);
        somministrazioniTable.getColumns().add(numPrescrizione);
        somministrazioniTable.getColumns().add(note_stato);


        final ObservableList<Somministrazione> data = FXCollections.observableArrayList(
                somministrazioni
        );

        for (Somministrazione temp : data){
            somministrazioniTable.getItems().add(temp);
        }
    }

    public synchronized void updatePlot(){
        //Platform.runLater(() -> {series.getData().clear();});

        File fn = new File("src/sistemaMonitoraggio/" + ricovero.getIdRicovero() + "_" +ricovero.getPazienteRicoverato().getCodSanitario()+".txt");
        try {
            if(fn.exists()) {
                Scanner sc = new Scanner(fn);
                //Calendar c = Calendar.getInstance();
                //seriesTemp.getData().get(seriesTemp.getData().size());
                //c.add(Calendar.HOUR_OF_DAY, -2);
                boolean flag = true; //se sto stampando un dato entro le due ore

                String lastSeriesValue;

                while (sc.hasNext() && flag) {
                    if (temperatureSeries.getData().size() > 0)
                        lastSeriesValue = temperatureSeries.getData().get((temperatureSeries.getData().size() - 1)).getXValue();
                    else {
                        lastSeriesValue = "0";
                        new Error().printStackTrace();
                    }
                    String s = sc.nextLine();
                    String[] buffer = s.split(" ");

                    int year    = Integer.valueOf(buffer[0].split("/")[2]);
                    int month   = Integer.valueOf(buffer[0].split("/")[1]);
                    int day     = Integer.valueOf(buffer[0].split("/")[0]);
                    int hour    = Integer.valueOf((buffer[1].split("\\."))[0]);
                    int min     = Integer.valueOf((buffer[1].split("\\."))[1]);

                    Calendar cd = Calendar.getInstance();
                    cd.set(year, month-1, day, hour, min);

                    Calendar lastVal = (Calendar.getInstance());
                    lastVal.set(Calendar.MINUTE, Integer.valueOf(lastSeriesValue.split("\\.")[1]));
                    lastVal.set(Calendar.HOUR_OF_DAY, Integer.valueOf(lastSeriesValue.split("\\.")[0]));


                    if(lastVal.compareTo(cd) >= 0){
                        flag = false;
                        //System.out.println("Prima " + cd.toString() + " < " + lastVal.toString());
                    }
                    else {
                        if (!buffer[buffer.length-1].contains("-")) {
                            while(temperatureSeries.getData().size() > 40)
                                temperatureSeries.getData().remove(0);
                            //System.out.println("Trovata temp con minutaggio maggiore [" + Float.valueOf(buffer[(buffer.length) - 1]) + "] : value " + Double.valueOf(buffer[1]) + " last " + Double.valueOf(lastSeriesValue));
                            System.out.println("Trovata temp con minutaggio maggiore [" + Float.valueOf(buffer[(buffer.length) - 1]) + "] : value " + cd.getTime() + " last " + lastVal.getTime());
                            Platform.runLater(() -> {
                                try {
                                    temperatureSeries.getData().add(new XYChart.Data<String, Float>(buffer[1], Float.valueOf(buffer[(buffer.length) - 1])));
                                } catch (NumberFormatException e) { /*Nel caso in cui la temperatura sia pari a null ovvero sul file txt pari a "-" */}
                            });
                        }//else System.out.println("Nuovi record letto, ma non c'è la temperatura");
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void loadTempChart(){
        anchorBP.setVisible(false);
        anchorTemp.setVisible(true);
    }

    public void loadBloodPressureChart(){
        anchorTemp.setVisible(false);
        anchorBP.setVisible(true);
    }

    public void compileDischargeLetter(){

        FXMLLoader loader = new FXMLLoader(getClass().getResource("dischargeLetter.fxml"));
        try {
            AnchorPane dischargeLetterPane = loader.load();
            loader.<dischargeLetterController>getController().initData(ricovero, userAccount);
            AnchorPane.setTopAnchor(dischargeLetterPane, 0.0);
            AnchorPane.setBottomAnchor(dischargeLetterPane, 0.0);
            AnchorPane.setLeftAnchor(dischargeLetterPane, 0.0);
            AnchorPane.setRightAnchor(dischargeLetterPane, 0.0);
            contentAnchorPane.getChildren().removeAll(contentAnchorPane.getChildren());
            contentAnchorPane.getChildren().add(dischargeLetterPane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}