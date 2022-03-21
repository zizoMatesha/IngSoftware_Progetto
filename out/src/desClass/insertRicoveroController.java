package desClass;

import db.DataBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import srcClass.Paziente;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;

public class insertRicoveroController {
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
    @FXML
    TextField ricercaNome;
    @FXML
    TextField ricercaCognome;
    @FXML
    TextField ricercaCodSanitario;
    @FXML
    TableView listaPazientiCercati;
    @FXML
    Button ricoveroButton;
    @FXML
    Label codiceSanRicovero;
    @FXML
    Label nomeRicovero;
    @FXML
    Label cognomeRicovero;
    @FXML
    Label istruzioni;
    @FXML
    AnchorPane ricoveroBox;
    @FXML
    Label dataRicoveroLabel;


    public void addPatient() {
        //DataBase db = new DataBase();
        //db.insertPatient(Nome.getText(), Cognome.getText(), Date.valueOf(dataNascita.getValue().toString()), luogoNascita.getText(), codSanitario.getText(), errorLabel);

        Stage stgAddPatient = new Stage();
        stgAddPatient.setTitle("Aggiunta paziente al databse");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("addPatient.fxml"));

        try {
            stgAddPatient.setScene(new Scene((AnchorPane) loader.load()));
        }catch(IOException e){System.err.println(e);}
        stgAddPatient.show();

    }

    public void initData(){
        listaPazientiCercati.getItems().clear();
        listaPazientiCercati.getColumns().clear();

        ArrayList<Paziente> pazientiCercati = (new DataBase()).loadPatientList();

        TableColumn<Paziente, String> nameColumn =      new TableColumn("Nome");
        TableColumn<Paziente, String> surnameColumn =   new TableColumn("Cognome");
        TableColumn<Paziente, String> codSanColumn =    new TableColumn("codSanitario");
        TableColumn<Paziente, String> luogoColumn =     new TableColumn("LuogoNascita");
        TableColumn<Paziente, Date> dataColumn =        new TableColumn("DataNascita");

        nameColumn.setCellValueFactory(     new PropertyValueFactory<Paziente, String>("nome"));
        surnameColumn.setCellValueFactory(  new PropertyValueFactory<Paziente, String>("cognome"));
        codSanColumn.setCellValueFactory(   new PropertyValueFactory<Paziente, String>("codSanitario"));
        luogoColumn.setCellValueFactory(    new PropertyValueFactory<Paziente, String>("luogo_nascita"));
        dataColumn.setCellValueFactory(     new PropertyValueFactory<>("data_nascita"));

        listaPazientiCercati.getColumns().add(nameColumn);
        listaPazientiCercati.getColumns().add(surnameColumn);
        listaPazientiCercati.getColumns().add(codSanColumn);
        listaPazientiCercati.getColumns().add(luogoColumn);
        listaPazientiCercati.getColumns().add(dataColumn);

        //System.out.println("Lughezza listaaaaaaaaaaaaaaa" + sent);

        final ObservableList<Paziente> data = FXCollections.observableArrayList(
                pazientiCercati
        );

        listaPazientiCercati.setItems(data);
        listaPazientiCercati.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 2 && (listaPazientiCercati.getSelectionModel().getSelectedItem() != null)) {
                    istruzioni.setVisible(false);
                    ricoveroBox.setVisible(true);

                    System.out.println(listaPazientiCercati.getItems().get(listaPazientiCercati.getSelectionModel().getSelectedIndex()));
                    nomeRicovero.setText(((Paziente)listaPazientiCercati.getItems().get(listaPazientiCercati.getSelectionModel().getSelectedIndex())).getNome());
                    cognomeRicovero.setText(((Paziente)listaPazientiCercati.getItems().get(listaPazientiCercati.getSelectionModel().getSelectedIndex())).getCognome());
                    codiceSanRicovero.setText(((Paziente)listaPazientiCercati.getItems().get(listaPazientiCercati.getSelectionModel().getSelectedIndex())).getCodSanitario());
                    Calendar c =Calendar.getInstance();
                    dataRicoveroLabel.setText(c.get(Calendar.DAY_OF_MONTH) + "/" + (c.get(Calendar.MONTH)+1) + "/" + c.get(Calendar.YEAR));
                    ricoveroButton.setVisible(true);
                }
            }
        });

    }

    public void searchPatient(){
        istruzioni.setVisible(true);
        ricoveroBox.setVisible(false);
        listaPazientiCercati.getItems().clear();
        listaPazientiCercati.getColumns().clear();

        DataBase db = new DataBase();
        ArrayList<Paziente>  pazientiCercati = db.searchPatient(ricercaNome.getText(), ricercaCognome.getText(), ricercaCodSanitario.getText());
        listaPazientiCercati.setEditable(true);
        TableColumn<Paziente, String> nameColumn =      new TableColumn("Nome");
        TableColumn<Paziente, String> surnameColumn =   new TableColumn("Cognome");
        TableColumn<Paziente, String> codSanColumn =    new TableColumn("codSanitario");
        TableColumn<Paziente, String> luogoColumn =     new TableColumn("LuogoNascita");
        TableColumn<Paziente, Date> dataColumn =        new TableColumn("DataNascita");

        nameColumn.setCellValueFactory(     new PropertyValueFactory<Paziente, String>("nome"));
        surnameColumn.setCellValueFactory(  new PropertyValueFactory<Paziente, String>("cognome"));
        codSanColumn.setCellValueFactory(   new PropertyValueFactory<Paziente, String>("codSanitario"));
        luogoColumn.setCellValueFactory(    new PropertyValueFactory<Paziente, String>("luogo_nascita"));
        dataColumn.setCellValueFactory(     new PropertyValueFactory<>("data_nascita"));

        listaPazientiCercati.getColumns().add(nameColumn);
        listaPazientiCercati.getColumns().add(surnameColumn);
        listaPazientiCercati.getColumns().add(codSanColumn);
        listaPazientiCercati.getColumns().add(luogoColumn);
        listaPazientiCercati.getColumns().add(dataColumn);

        int sent = pazientiCercati.size();
        //System.out.println("Lughezza listaaaaaaaaaaaaaaa" + sent);

        final ObservableList<Paziente> data = FXCollections.observableArrayList(
                pazientiCercati
        );

        listaPazientiCercati.setItems(data);
        listaPazientiCercati.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 2 && (listaPazientiCercati.getSelectionModel().getSelectedItem() != null)) {
                    istruzioni.setVisible(false);
                    ricoveroBox.setVisible(true);

                    System.out.println(listaPazientiCercati.getItems().get(listaPazientiCercati.getSelectionModel().getSelectedIndex()));
                    nomeRicovero.setText(((Paziente)listaPazientiCercati.getItems().get(listaPazientiCercati.getSelectionModel().getSelectedIndex())).getNome());
                    cognomeRicovero.setText(((Paziente)listaPazientiCercati.getItems().get(listaPazientiCercati.getSelectionModel().getSelectedIndex())).getCognome());
                    codiceSanRicovero.setText(((Paziente)listaPazientiCercati.getItems().get(listaPazientiCercati.getSelectionModel().getSelectedIndex())).getCodSanitario());
                    ricoveroButton.setVisible(true);
                }
            }
        });
    }

    public void ricoveroPaziente(){
        System.out.println("RICOVERA: " + codiceSanRicovero.getText());
        DataBase db = new DataBase();
        if(db.generaRicovero(codiceSanRicovero.getText())){
            errorLabel.setTextFill(Color.rgb(0, 220, 30));
            errorLabel.setText("Paziente ricoverato correttamente");
        }
        else{
            errorLabel.setTextFill(Color.rgb(255, 55, 0));
            errorLabel.setText("Errore! Non è possibile ricoverare il paziente selezionato.");
        }

        //Faccio sparire la barra di generazione del ricovero
        nomeRicovero.setText("");
        cognomeRicovero.setText("");
        codiceSanRicovero.setText("");
        ricoveroButton.setVisible(false);
        istruzioni.setVisible(false);
        ricoveroBox.setVisible(false);
        //POST: reinizializzo la tabella (per togliere il paziente appena ricoverato)
        initData();

    }



}
