package Controller;

import db.DataBase;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import srcClass.Allarme;
import srcClass.LoggedUser;
import srcClass.Ricovero;
import util.KTimer;

import java.sql.Time;
import java.time.LocalTime;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class allarmeController {

    @FXML
    Label timeLabel;

    @FXML
    Label alarmMessage;

    @FXML
    ImageView sendButton;
    @FXML
    TextArea interventoEffetuato;
    @FXML
    Label labelInterventoEffettuato;
    @FXML
    ImageView okImage;
    @FXML
    ImageView alertImage;
    @FXML
    AnchorPane bloccoIntestazione;
    @FXML
    Button spegniButton;


    KTimer timer = new KTimer();
    private Ricovero ricovero;
    private Allarme alarm;
    private LoggedUser userAccount;

    public void initData(Ricovero ric, String message, Allarme alarm, LoggedUser userAccount){
        this.ricovero = ric;
        this.alarm = alarm;
        this.userAccount = userAccount;

        timer.startTimer(0);

        alarmMessage.setText(message);

        Timer t = new Timer();
        TimerTask tt = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(()->timeLabel.setText(timer.getTimeFormatted()));
            }
        };

        t.schedule(tt, 10, 10);

    }

    public void spegniAllarme(){
        spegniButton.setVisible(false);
        alertImage.setVisible(false);
        okImage.setVisible(true);
        interventoEffetuato.setVisible(true);
        labelInterventoEffettuato.setVisible(true);
        sendButton.setVisible(true);
        timer.stopTimer();
        Platform.runLater(()->timeLabel.setText(timer.getTimeFormatted()));
        bloccoIntestazione.setStyle("-fx-background-color:   #108201");
    }

    public void salvaIntervento(){
        labelInterventoEffettuato.setStyle("-fx-background-color:   #000000");
        if(interventoEffetuato.getText().equals("") || interventoEffetuato.getText().equals(" ") || interventoEffetuato.getText() == null){
            labelInterventoEffettuato.setText("Inserisci una breve descrizione dell'intervento prima di inviarlo!");
        }else{
            //Salva Intervento
            String intervento = interventoEffetuato.getText().replaceAll("\n", System.getProperty("line.separator"));

            DataBase db = DataBase.getDataBaseInstance();
            if(db.setAlarm(ricovero, userAccount, alarm, Time.valueOf(timer.getTimeFormatted()), intervento)){
                System.out.println("[Intervento]-> " + interventoEffetuato.getText());
                ((Stage)bloccoIntestazione.getScene().getWindow()).close();

                String tempoRezione = timer.getTimeFormatted();
                String tempoReazioneSplitted[] = tempoRezione.split(":");

                if(Integer.valueOf(tempoReazioneSplitted[0]) >= alarm.getTempoRisposta()){
                    if(Integer.valueOf(tempoReazioneSplitted[1])>0){
                        showAlert(Alert.AlertType.ERROR, spegniButton.getScene().getWindow(), "Report", "Allarme caricato correttamente!\nIl tempo di reazione ("+timer.getTimeFormatted()+") all'allarme è maggiore del tempo limite concesso ("+alarm.getTempoRisposta()+" minuti), report inviato!");
                    }
                }
                else {
                    showAlert(Alert.AlertType.CONFIRMATION, spegniButton.getScene().getWindow(), "Conferma", "Allarme caricato correttamente!\n");
                }
            }
            labelInterventoEffettuato.setStyle("-fx-background-color:   #e30808");
            labelInterventoEffettuato.setText("Impossibile inviare l'intervento, chiudi la schermata");
        }
    }
    private void showAlert(Alert.AlertType alertType, Window owner, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(owner);
        alert.show();
    }


}
