package Controller;

import db.DataBase;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import srcClass.Ricovero;
import srcClass.parametriVitali;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;

/**
 * FirstUnloggedController: è la prima schermata che compare all'utente, presenta un menù a tendina contenente il nome dei pazienti attualmente ricoverati,
 * selezionandone uno la schermata mostra i parametri vitali registrati negli ultimi 15 minuti a prescindere dall'autenticazione effetuata o meno.
 * E' inoltre possibile accedere a tutte le funzionalità del programma autenticandosi con l'apposito bottone "Login"
 **/

public class FirstController {
    @FXML
    MenuButton menuPazienti;
    @FXML
    Button loginButton;
    @FXML
    AnchorPane contentWindow;
    @FXML
    AnchorPane anchorTemp;
    @FXML
    AnchorPane anchorBP;
    @FXML
    Label nomeCognomePaziente;
    @FXML
    Button buttonTemp;
    @FXML
    Button buttonBP;
    @FXML
    Label errorLabel;
    @FXML
    LineChart<String, Float> tempChart;
    @FXML
    LineChart<String, Float> bloodPressureChart;
    @FXML
    NumberAxis yaxis;
    @FXML
    NumberAxis yaxisBP;

    FXMLLoader loader = null;
    Ricovero ricovero;
    private Task task;
    private Thread taskThread;
    private XYChart.Series<String, Float> temperatureSeries;
    private XYChart.Series<String, Float> bloodPresSisSeries;
    private XYChart.Series<String, Float> bloodPresDiasSeries;
    private int typeOfAccount = -1;

    //For communication with server
    private Socket s = null;
    private final ObjectInputStream dis = null;
    private final ObjectOutputStream dos = null;
    private final boolean flag = false;

    public void loadData() {
        DataBase db = DataBase.getDataBaseInstance();
        EventHandler<ActionEvent> action;

        //Cancello i dati del menù
        List<MenuItem> items = menuPazienti.getItems();
        items.removeAll(items);

        List<Ricovero> listaRicoveri = db.getPatientsHospitalized();

        for (Ricovero ric : listaRicoveri) {
            MenuItem temp;
            //System.out.println(ric.getPazienteRicoverato().toString());
            temp = new MenuItem();
            temp.setText(ric.getPazienteRicoverato().getCognome() + " " + ric.getPazienteRicoverato().getNome());

            //Set dell'evento in modo che, se l'utente seleziona uno tra i pazienti listati nel menù, si visualizzino le generalià e i parametri vitali
            action = onClickLoad(ric.getPazienteRicoverato().getNome(), ric.getPazienteRicoverato().getCognome(), ric);
            temp.setOnAction(action);
            menuPazienti.getItems().add(temp);
        }

    }

    private EventHandler<ActionEvent> onClickLoad(String nome, String cognome, Ricovero ricovero) {
        return new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                closeAll();
                closeTask();
                loadPatientInfo(nome, cognome, ricovero);
            }
        };
    }

    public void loadTempChart() {
        anchorBP.setVisible(false);
        anchorTemp.setVisible(true);
    }

    public void loadBloodPressureChart() {
        anchorTemp.setVisible(false);
        anchorBP.setVisible(true);
    }

    public void loadPatientInfo(String nome, String cognome, Ricovero ricovero) {


        if (tempChart != null) tempChart.getData().clear();
        if (bloodPressureChart != null) bloodPressureChart.getData().clear();

        nomeCognomePaziente.setText(nome + " " + cognome);
        this.ricovero = ricovero;
        this.typeOfAccount = typeOfAccount;

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

            @Override
            public void run() {

                //Effettuo il collegamento con il monitor della stanza del ricovero indicato
                try {
                    if (connectServer() == false) {
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

                        //Dico al server se sono un utente loggato o meno (in questo caso non sono loggato -> -1)
                        dos.writeInt(typeOfAccount);
                        dos.flush();

                        while (!task.isCancelled()) {
                            try {
                                if (!s.isClosed()) {
                                    boolean flag = true;
                                    do {
                                        String lineFromSocket = (String) dis.readObject();
                                        //System.out.println(lineFromSocket);

                                        if (lineFromSocket.contains("end")) {
                                            flag = false;
                                            dos.writeBoolean(true);
                                            dos.flush();
                                        } else {
                                            String[] buffer = lineFromSocket.split(" ");

                                            if (parametri.getEcg() == -1 && (!buffer[2].contains("-")))
                                                parametri.setEcg(Integer.valueOf(buffer[2]));
                                            if (parametri.getSistolica() == -1 && (!buffer[3].contains("-")))
                                                parametri.setSistolica(Integer.valueOf(buffer[3]));
                                            if (parametri.getDiastolica() == -1 && (!buffer[4].contains("-")))
                                                parametri.setDiastolica(Integer.valueOf(buffer[4]));
                                            if (parametri.getSatOss() == -1 && (!buffer[5].contains("-")))
                                                parametri.setSatOss(Integer.valueOf(buffer[5]));
                                            if (parametri.getTemperatura() == -1 && (!buffer[buffer.length - 1].contains("-")))
                                                parametri.setTemperatura(Float.valueOf(buffer[6]));

                                            //System.out.println("ADDED: " + buffer[1] + " " + Float.valueOf(buffer[(buffer.length) - 1]));
                                            if (!buffer[buffer.length - 1].contains("-")) {
                                                Platform.runLater(() -> {
                                                    try {
                                                        temperatureSeries.getData().add(new XYChart.Data<String, Float>(buffer[1].substring(0, 5), Float.valueOf(buffer[(buffer.length) - 1])));
                                                        temperatureSeries.getData().sort((o1, o2) -> o1.getXValue().compareTo(o2.getXValue()));
                                                    } catch (NumberFormatException e) { /*Nel caso in cui la temperatura sia pari a null ovvero sul file txt pari a "-" */}
                                                });
                                            }
                                            if (!buffer[3].contains("-")) {
                                                Platform.runLater(() -> {
                                                    try {
                                                        bloodPresSisSeries.getData().add(new XYChart.Data<String, Float>(buffer[1].substring(0, 5), Float.valueOf(buffer[3])));
                                                        bloodPresSisSeries.getData().sort((o1, o2) -> o1.getXValue().compareTo(o2.getXValue()));
                                                    } catch (NumberFormatException e) { /*Nel caso in cui la temperatura sia pari a null ovvero sul file txt pari a "-" */}
                                                });
                                            }

                                            if (!buffer[4].contains("-")) {
                                                Platform.runLater(() -> {
                                                    try {
                                                        bloodPresDiasSeries.getData().add(new XYChart.Data<String, Float>(buffer[1].substring(0, 5), Float.valueOf(buffer[4])));
                                                        bloodPresDiasSeries.getData().sort((o1, o2) -> o1.getXValue().compareTo(o2.getXValue()));
                                                    } catch (NumberFormatException e) { /*Nel caso in cui la temperatura sia pari a null ovvero sul file txt pari a "-" */}
                                                });
                                            }

                                            dos.writeBoolean(true);
                                            dos.flush();
                                        }
                                    } while (flag);
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
                    System.out.println(error);
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

    public void closeAll() {
        // closing resources
        try {
            if (dis != null) dis.close();
            if (dos != null) dos.close();
            if (s != null) s.close();
            if (task != null) task.cancel();
        } catch (IOException | IllegalArgumentException | NullPointerException e) {
            System.out.println("Errore chiusura Task!-->");
            e.printStackTrace();
        }

    }

    public void closeTask() {
        if (taskThread != null)
            taskThread.interrupt();
        if (task != null) {
            task.cancel();
            if (s != null) {
                try {
                    s.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean connectServer() {
        try {
            InetAddress ip = InetAddress.getByName("localhost");
            s = new Socket(ip, 50152);

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void Login() {
        closeAll();
        closeTask();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/src/View/loginPage.fxml"));
        Parent root = null;
        try {
            root = loader.load();
            loader.<loginPageController>getController().message.setText("");
        } catch (IOException e) {
            errorLabel.setText("Impossibile effettuare il login, la pagina di login o il server non rispondono!");
        }
        Stage primaryStage = (Stage) loginButton.getScene().getWindow();

        primaryStage.setTitle("Terapia intensiva");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        //Nel caso in cui l'applicazione venisse chiusa bruscamente, anche tutte le thread verranno chiuse!
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent e) {
                Platform.exit();
                System.exit(0);
            }
        });
    }


}
