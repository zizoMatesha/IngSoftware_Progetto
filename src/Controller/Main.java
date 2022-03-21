package Controller;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        //Parent root = FXMLLoader.load(getClass().getResource("loginPage.fxml"));
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/src/View/unloggedFXML.fxml"));
        Parent root = (AnchorPane) loader.load();
        loader.<FirstController>getController().loadData();

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


    public static void main(String[] args) {
        launch(args);
    }
}
