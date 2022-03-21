package desClass;

import db.DataBase;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import srcClass.Alarm;
import srcClass.LoggedUser;
import javafx.fxml.*;
import javafx.scene.layout.AnchorPane;
import srcClass.Ricovero;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.sql.Date;
import java.util.Calendar;
import java.util.List;

public class staticPageController {

    @FXML
    private AnchorPane contentWindow;
    @FXML
    private MenuButton menuPazienti;
    @FXML
    private Label nomeUtente;


    private int typeOfAccount;
    private LoggedUser userAccount;
    DataBase db = new DataBase();
    private FXMLLoader loader = null; //Loader utilizzato per istanziare le pagine dinamiche all'interno dell'home controller
    private Socket s;

    //Grazie a initData passo le informazioni dalla vecchia scena a quella nuova, il cui controller è l'istanza della classe staticPageController che gestisce la nuova scena.
    public void initData(LoggedUser userAccount){
        this.userAccount = userAccount;
        nomeUtente.setText(userAccount.getNomeUtente());
        backToHome(db, userAccount);
        loadData();
        //Dopo aver fatto il login l'applicazione mi potrà inviare allarmi relativi ai parametri vitali del paziente!
        if(userAccount.getAccountType() == 1/*Medico*/ || userAccount.getAccountType() == 3/*Primario*/){
            setAlarmDetector();
        }
    }

    public void loadData(){
        System.out.println("LoadData()");
        EventHandler<ActionEvent> action;

        //Cancello i dati del menù
        List<MenuItem> items = menuPazienti.getItems();
        items.removeAll(items);

        List<Ricovero> listaRicoveri = db.getPatientsHospitalized();

        for(Ricovero ric : listaRicoveri){
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
                if(loader != null) {
                    loader.<desClass.patientController>getController().closeAll();
                    loader.<patientController>getController().closeTask();

                }
                //loadPatientInfo(nome, cognome, ricovero);
                closePrecPatientInfo();
                //Inizializzo il loader e passo i dati del paziente al controller della scheda paziente
                loader = new FXMLLoader(getClass().getResource("patientInfo.fxml"));
                try{
                    AnchorPane newAnchorPane = loader.load();
                    loader.<patientController>getController().initData(nome, cognome, ricovero, typeOfAccount, userAccount);
                    AnchorPane.setTopAnchor(newAnchorPane, 0.0);
                    AnchorPane.setBottomAnchor(newAnchorPane, 0.0);
                    AnchorPane.setLeftAnchor(newAnchorPane, 0.0);
                    AnchorPane.setRightAnchor(newAnchorPane, 0.0);
                    contentWindow.getChildren().removeAll(contentWindow.getChildren());
                    contentWindow.getChildren().add(newAnchorPane);
                }catch(IOException e){e.printStackTrace();}
            }
        };
    }

    public void closePrecPatientInfo(){
        if(loader != null && (loader.getController().getClass()) == (new patientController()).getClass()){
            loader.<patientController>getController().closeTask();
            loader.<patientController>getController().closeAll();
        }
    }

    public void addPatient() throws IOException {
        closePrecPatientInfo();
        FXMLLoader loader2 = new FXMLLoader(getClass().getResource("insertRicovero.fxml"));
        AnchorPane newAnchorPane = loader2.load();
        loader2.<insertRicoveroController>getController().initData();
        /*FXMLLoader.load(getClass().getResource("insertRicovero.fxml"))*/
        AnchorPane.setTopAnchor(newAnchorPane, 0.0);
        AnchorPane.setBottomAnchor(newAnchorPane, 0.0);
        AnchorPane.setLeftAnchor(newAnchorPane, 0.0);
        AnchorPane.setRightAnchor(newAnchorPane, 0.0);
        contentWindow.getChildren().removeAll(contentWindow.getChildren());
        contentWindow.getChildren().add(newAnchorPane);
    }

    public class RicoveroForTable extends Ricovero{
        private String nome;
        private String cognome;
        private String codSanitario;
        private Date dataInizioRicovero;
        public RicoveroForTable(Ricovero ricovero){
            super(ricovero);
            nome = ricovero.getPazienteRicoverato().getNome();
            cognome = ricovero.getPazienteRicoverato().getCognome();
            codSanitario = ricovero.getPazienteRicoverato().getCodSanitario();
            dataInizioRicovero = ricovero.getDataInizioRicovero();
        }

        public String getNome() {
            return nome;
        }

        public void setNome(String nome) {
            this.nome = nome;
        }

        public String getCognome() {
            return cognome;
        }

        public void setCognome(String cognome) {
            this.cognome = cognome;
        }

        public String getCodSanitario() {
            return codSanitario;
        }

        public void setCodSanitario(String codSanitario) {
            this.codSanitario = codSanitario;
        }

        @Override
        public Date getDataInizioRicovero() {
            return dataInizioRicovero;
        }

        @Override
        public void setDataInizioRicovero(Date dataInizioRicovero) {
            this.dataInizioRicovero = dataInizioRicovero;
        }
    }

    public void clickForBack(){
        backToHome(db, userAccount);
    }

    public void backToHome(DataBase db, LoggedUser user){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("home.fxml"));
        AnchorPane newAnchorPane = null;
        try {
            newAnchorPane = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        closePrecPatientInfo();
        loader.<homeController>getController().initData(db, user);
        /*FXMLLoader.load(getClass().getResource("insertRicovero.fxml"))*/
        AnchorPane.setTopAnchor(newAnchorPane, 0.0);
        AnchorPane.setBottomAnchor(newAnchorPane, 0.0);
        AnchorPane.setLeftAnchor(newAnchorPane, 0.0);
        AnchorPane.setRightAnchor(newAnchorPane, 0.0);
        contentWindow.getChildren().removeAll(contentWindow.getChildren());
        contentWindow.getChildren().add(newAnchorPane);
    }

    public void setAlarmDetector(){
        (new alarmDetector()).start();
    }

    class alarmDetector extends Thread {
        @Override
        public void run() {
            MulticastSocket socket = null;
            byte[] buf = new byte[256];
            try {
                socket = new MulticastSocket(50153);
                InetAddress group =
                        InetAddress.getByName("225.0.0.1");
                socket.joinGroup(group);

                String idRicoveroTemp = "0000000000";
                Alarm alarm = new Alarm(0, Alarm.TipoAllarme.Ipertermia, 0, idRicoveroTemp);


                //dataByte rappresenta il numero STATICO di byte e il relativo contenuto di ogni pacchetto dati riguardante gli allarmi
                //idRicovero di lunghezza 10 è standard, impostato anche su database di lunghezza massima 10.
                //Creato esclusivamente per avere un numero di byte statico da trasferire quando si vuole inviare/ricevere un allarme.
                byte[] dataByte = alarm.toString().getBytes();

                DatagramPacket packet = new DatagramPacket(dataByte, dataByte.length);
                DatagramPacket precPacket = new DatagramPacket(dataByte, dataByte.length);
                Alarm.TipoAllarme[] tipoallarme = Alarm.TipoAllarme.values();

                while(true){
                    socket.receive(packet);
                    String received = new String(packet.getData());
                    String[] receivedSplitted = received.split(" ");

                    received = "[Allarme " + Calendar.getInstance().getTime().getTime() + "]-> " + tipoallarme[Integer.valueOf(receivedSplitted[1])].name() + " tempo: " + receivedSplitted[2] + " livello: " + receivedSplitted[0] + " idricovero: " + Integer.valueOf(receivedSplitted[3]);
                    System.out.println("Received " + received);


                    Platform.runLater(() -> {
                        Stage stg = new Stage();
                        stg.initOwner((Stage) contentWindow.getScene().getWindow());
                        stg.initModality(Modality.WINDOW_MODAL);

                        FXMLLoader alarmLoader = new FXMLLoader(getClass().getResource("allarmeFXML.fxml"));
                        try {
                            stg.setScene(new Scene((Pane) alarmLoader.load()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        Ricovero ric = db.getRicovero(Integer.valueOf(receivedSplitted[3]));
                        if(ric == null) System.err.println("Ricovero inesistente!!!!!");

                        String message = tipoallarme[Integer.valueOf(receivedSplitted[1])].name() + " in atto al paziente " + ric.getPazienteRicoverato().getCognome() + " " + ric.getPazienteRicoverato().getNome();

                        alarmLoader.<allarmeController>getController().initData(ric, message);

                        stg.show();
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }

}

