package Controller;

import db.DataBase;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.sql.Date;

public class addPatientController {

    @FXML
    TextField Nome;
    @FXML
    TextField Cognome;
    @FXML
    TextField luogoNascita;
    @FXML
    DatePicker dataNascita;
    @FXML
    TextField codSanitario;
    @FXML
    Label errorLabel;

    public void addPatient(ActionEvent event) {
        DataBase db = DataBase.getDataBaseInstance();
        if(db.registraPaziente(Nome.getText(), Cognome.getText(), Date.valueOf(dataNascita.getValue().toString()), luogoNascita.getText(), codSanitario.getText())){
            errorLabel.setText("Paziente aggiunto con successo!");
        }
        else{
            errorLabel.setText("Paziente già presente nel sistema o dati inseriti non correttamente! (Controlla il codice sanitario)");
        }
    }


}
