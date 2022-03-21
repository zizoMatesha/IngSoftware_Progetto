package desClass;

import db.DataBase;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

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
        DataBase db = new DataBase();
        if(db.insertPatient(Nome.getText(), Cognome.getText(), Date.valueOf(dataNascita.getValue().toString()), luogoNascita.getText(), codSanitario.getText(), errorLabel)){
            //Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
            //stage.close();
        }
    }

}
