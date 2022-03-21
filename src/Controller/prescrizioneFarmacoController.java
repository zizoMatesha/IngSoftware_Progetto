package Controller;

import db.DataBase;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.util.converter.FloatStringConverter;
import srcClass.*;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;

public class prescrizioneFarmacoController {

    public LoggedUser userAccount;
    public Ricovero ricovero;
    private Farmaco farmacoSelezionato;

    @FXML
    TextField ricercaDenominazione;
    @FXML
    TextField ricercaCodAIC;
    @FXML
    TextField ricercaPrincipioAttivo;
    @FXML
    TextField farmacoField;
    @FXML
    TextField numDosiField;
    @FXML
    TextField qtaDoseField;
    @FXML
    TextField durataTerapiaField;
    @FXML
    DatePicker dataPrescField;
    @FXML
    Label medicoRespLabel;
    @FXML
    Label pazienteLabel;
    @FXML
    Label errorLabel;
    @FXML
    Button searchButton;
    @FXML
    TableView listaFarmaciFiltered;
    @FXML
    AnchorPane confermaPane;


    public void filtraFarmaci(){

        setNumericField();
        DataBase db = DataBase.getDataBaseInstance();
        ArrayList<Farmaco> farmaciList = db.getSearchedFarmaco(ricercaDenominazione.getText(), ricercaCodAIC.getText(), ricercaPrincipioAttivo.getText());

        TableColumn<Farmaco, String> nomeCommerciale =      new TableColumn("nomeCommerciale");
        TableColumn<Farmaco, Integer> codAIC =     new TableColumn("codAIC");
        TableColumn<Farmaco, String> principioAttivo =        new TableColumn("principioAttivo");
        TableColumn<Farmaco, String> titolareAIC =     new TableColumn("titolareAIC");
        TableColumn<Farmaco, String> descrizione =        new TableColumn("descrizione");


        nomeCommerciale.setCellValueFactory(    new PropertyValueFactory<Farmaco, String>("nomeCommerciale"));
        codAIC.setCellValueFactory(             new PropertyValueFactory<Farmaco, Integer>("codAIC"));
        principioAttivo.setCellValueFactory(    new PropertyValueFactory<>("principioAttivo"));
        titolareAIC.setCellValueFactory(        new PropertyValueFactory<Farmaco, String>("titolareAIC"));
        descrizione.setCellValueFactory(        new PropertyValueFactory<>("descrizione"));

        listaFarmaciFiltered.getColumns().add(nomeCommerciale);
        listaFarmaciFiltered.getColumns().add(codAIC);
        listaFarmaciFiltered.getColumns().add(principioAttivo);
        listaFarmaciFiltered.getColumns().add(titolareAIC);
        listaFarmaciFiltered.getColumns().add(descrizione);

        int sent = farmaciList.size();

        final ObservableList<Farmaco> data = FXCollections.observableArrayList(
                farmaciList
        );

        listaFarmaciFiltered.setItems(data);
        listaFarmaciFiltered.setOnMouseClicked(new EventHandler<MouseEvent>(){

            @Override
            public void handle(MouseEvent mouseEvent) {
                if( mouseEvent.getClickCount() == 1 && (listaFarmaciFiltered.getSelectionModel().getSelectedItem()) != null){
                    System.out.println(Integer.toString(((Farmaco) listaFarmaciFiltered.getSelectionModel().getSelectedItem()).getCodAIC()));
                    pazienteLabel.setText(ricovero.getPazienteRicoverato().getCognome() + " " + ricovero.getPazienteRicoverato().getNome());
                    confermaPane.setVisible(true);
                    farmacoSelezionato = (Farmaco) listaFarmaciFiltered.getSelectionModel().getSelectedItem();
                    farmacoField.setText(Integer.toString(farmacoSelezionato.getCodAIC()));
                    medicoRespLabel.setText(userAccount.getCodFiscale().toString());
                    dataPrescField.setValue(LocalDate.now());
                }
            }
        });

    }

    public void confermaPrescrizione(){
       if(dataPrescField.getValue() == null || dataPrescField.getValue().toString() == "" || farmacoField.getText() == null || farmacoField.getText() == "" || numDosiField.getText() == null || numDosiField.getText() == "" || durataTerapiaField.getText() == null || durataTerapiaField.getText() == "" || qtaDoseField.getText() == null || qtaDoseField.getText() == ""){
           errorLabel.setText("Errore: compila corretamente tutti i campi!");
           return;
       }else{
           DataBase db = DataBase.getDataBaseInstance();
           errorLabel.setText(dataPrescField.getValue().toString());
           //System.out.println("ERRORE? " + Integer.valueOf(durataTerapiaField.getText()));
           Prescrizione prescrizione = new Prescrizione(farmacoSelezionato, Date.valueOf(dataPrescField.getValue()), Integer.valueOf(durataTerapiaField.getText()), Integer.valueOf(numDosiField.getText()), (new FloatStringConverter()).fromString(qtaDoseField.getText()), new Medico(userAccount), ricovero, 0);
           if(db.registraPrescrizione(prescrizione)){
               errorLabel.setTextFill(Color.rgb(110,195, 111));
               errorLabel.setText("Prescrizione aggiunta con successo");
           }else{
               resetErrorLabel();
               errorLabel.setText("Errore durante la convalida della prescrizione");
           }
       }
    }

    public void setNumericField(){
        //Durata della terapia deve essere un numero minore di 7 cifre, non vengono accetati caratteri
        durataTerapiaField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                try {
                    if (!newValue.matches("\\d{0,7}?")) {
                        durataTerapiaField.setText("");
                        errorLabel.setText("Inserire correttamente il numero di giorni");
                    }else{
                        resetErrorLabel();
                        errorLabel.setText("");
                    }
                } catch (IllegalArgumentException e) {
                    errorLabel.setText("Inserisci corretamente numeri, senza lettere e non superare le 7 cifre!");
                }
            }
        });

        //Qtà per dose può essere un numero intero o con la virgola, per indicare una parte di una dose
        qtaDoseField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                try {
                    if (!newValue.matches("\\d{0,7}([\\.]\\d{0,2})?")) {
                        qtaDoseField.setText("");
                        errorLabel.setText("Inserire correttamente la qta per dose (al massimo 2 cifre decimali!)");
                    }else{
                        resetErrorLabel();
                        errorLabel.setText("");
                    }
                } catch (IllegalArgumentException e) {
                    System.err.println(e);
                }
            }
        });

        //Il numero di dosi giornaliere deve essere semplicemente un numero intero
        numDosiField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                try {
                    if (!newValue.matches("\\d{0,5}?")) {
                        numDosiField.setText("");
                        errorLabel.setText("Inserire correttamente il numero di dosi giornaliere");
                    }else{
                        resetErrorLabel();
                        errorLabel.setText("");
                    }
                } catch (IllegalArgumentException e) {
                    System.err.println(e);
                }
            }
        });
        resetErrorLabel();
    }

    private void resetErrorLabel() {
        errorLabel.setTextFill(Color.rgb(255, 107, 0));
        errorLabel.setText("");
    }

}
