package desClass;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import srcClass.Ricovero;
import util.KTimer;

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

    public void initData(Ricovero ric, String message){
        this.ricovero = ric;

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
        if(interventoEffetuato.getText().equals("") || interventoEffetuato.getText().equals(" ") || interventoEffetuato.getText() == null){
            labelInterventoEffettuato.setText("Inserisci una breve descrizione dell'intervento prima di inviarlo!");
        }else{
            //Salva Intervento
            //TODO: String intervento = interventoEffetuato.getText().replaceAll("\n", System.getProperty("line.separator"));

            System.out.println("[Intervento]-> " + interventoEffetuato.getText());
            ((Stage)bloccoIntestazione.getScene().getWindow()).close();
        }
    }


}
