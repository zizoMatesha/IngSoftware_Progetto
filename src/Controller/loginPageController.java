package Controller;

import db.DataBase;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import srcClass.LoggedUser;

import java.io.IOException;
import java.sql.SQLException;

public class loginPageController {
    @FXML
    PasswordField pwdField;
    @FXML
    TextField usrField;
    @FXML
    Label message;

    public void login(ActionEvent actionEvent) throws SQLException {
        DataBase db = DataBase.getDataBaseInstance();
        String userName = usrField.getText();
        String password = pwdField.getText();
        try {
            Stage secondStage = new Stage();

            //Login effettuato nella classe db, che si vollega al database ed effettua un controllo sulla tipologia dell'account
            // e sulla corretta registrazione dell'account
            LoggedUser user = db.login(userName, password);
            if (user != null) {

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/src/View/staticPage.fxml"));

                message.setText("You are logged!");
                userName = userName.toUpperCase();
                secondStage.setTitle("Terapia intensiva - " + userName);

                secondStage.setScene(new Scene((Pane) loader.load()));
                secondStage.setMaximized(true);
                //loader.<staticPageController>getController().initData(db.getAccountType(), db.getNomeUtente(), db.getUserAccount());
                loader.<staticPageController>getController().initData(user);
                secondStage.show();

                //Nel caso in cui l'applicazione venisse chiusa bruscamente, anche tutte le thread verranno chiuse!
                secondStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                    @Override
                    public void handle(WindowEvent e) {
                        Platform.exit();
                        System.exit(0);
                    }
                });

                //Hide login stage
                ((Node) actionEvent.getSource()).getScene().getWindow().hide();
            }else{
                System.out.println("USER " + userName +" PWD: " + password);
                message.setText("Credenziali non valide!");
            }
        }catch(SQLException | IOException e){
           System.err.println("Error in login function [DataBase.login()]");
           e.printStackTrace();
        }
    }
}
