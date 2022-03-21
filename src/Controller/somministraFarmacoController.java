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
import srcClass.*;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;

public class somministraFarmacoController {

    @FXML
    TableView listaPrescrizioni;
    @FXML
    Label infermiereRespLabel;
    @FXML
    Label pazienteLabel;
    @FXML
    DatePicker dataSommField;
    @FXML
    TextField doseSomm;
    @FXML
    TextField prescCod;
    @FXML
    TextField farmacoField;
    @FXML
    TextArea notePaziente;
    @FXML
    AnchorPane confermaPane;
    @FXML
    TextField codFarmacoField;
    @FXML
    TextField momentoSomm;
    @FXML
    Label errorLabel;


    public LoggedUser userAccount;
    public Ricovero ricovero;

    public void initData(LoggedUser userAccount, Ricovero ricovero){
        this.ricovero = ricovero;
        this.userAccount = userAccount;
    }

    public void loadPrescrizioni(){
        DataBase db = DataBase.getDataBaseInstance();
        ArrayList<Prescrizione> listaPrescr = db.getPrescrizioni(ricovero);
        ArrayList<prescrizioneForTable> prescrPerTabella = new ArrayList<>();

        for (Prescrizione pres : listaPrescr) {
            //System.out.println("Medico:" + pres.getMedicoResp().getCf() + " Paziente: " + pres.getRicovero().getPazienteRicoverato().getCodSanitario() + " Farmaco: " + pres.getFarmacoPrescritto().getNomeCommerciale());
            prescrPerTabella.add(new prescrizioneForTable(
                    pres.getCodPrescrizione(),
                    pres.getFarmacoPrescritto().getCodAIC(),
                    pres.getFarmacoPrescritto().getNomeCommerciale(),
                    pres.getDataPrescrizione(),
                    pres.getDurataTerapia(),
                    pres.getNumeroDosiGiornaliere(),
                    pres.getQtaFarmacoDose(),
                    pres.getMedicoResp().getCf(),
                    pres.getRicovero().getPazienteRicoverato().getCodSanitario(),
                    pres.getMedicoResp(),
                    pres.getRicovero()));
            }


        TableColumn<prescrizioneForTable, String> denominazioneFarmaco =       new TableColumn("denominazioneFarmaco");
        TableColumn<prescrizioneForTable, Integer> codFarmaco =          new TableColumn("codFarmaco");
        TableColumn<prescrizioneForTable, Date> dataPrescrizione =          new TableColumn("dataPrescrizione");
        TableColumn<prescrizioneForTable, Integer> durataTerapia =  new TableColumn("durataTerapia");
        TableColumn<prescrizioneForTable, Integer> numeroDosiGiornaliere =           new TableColumn("qtaFarmacoDose");
        TableColumn<prescrizioneForTable, Float> qtaFarmacoDose =           new TableColumn("qtaFarmacoDose");
        TableColumn<prescrizioneForTable, String> csPaziente =          new TableColumn("csPaziente");
        TableColumn<prescrizioneForTable, String> cfMedico =       new TableColumn("cfMedico");
        TableColumn<prescrizioneForTable, String> codPrescrizione = new TableColumn<>("codPrescrizione");

        denominazioneFarmaco.setCellValueFactory(       new PropertyValueFactory<>("denominazioneFarmaco"));
        codFarmaco.setCellValueFactory(                 new PropertyValueFactory<>("codFarmaco"));
        dataPrescrizione.setCellValueFactory(           new PropertyValueFactory<>("dataPrescrizione"));
        durataTerapia.setCellValueFactory(              new PropertyValueFactory<>("durataTerapia"));
        numeroDosiGiornaliere.setCellValueFactory(      new PropertyValueFactory<>("numeroDosiGiornaliere"));
        qtaFarmacoDose.setCellValueFactory(             new PropertyValueFactory<>("qtaFarmacoDose"));
        csPaziente.setCellValueFactory(                 new PropertyValueFactory<>("csPaziente"));
        cfMedico.setCellValueFactory(                   new PropertyValueFactory<>("cfMedico"));
        codPrescrizione.setCellValueFactory(            new PropertyValueFactory<>("codPrescrizione"));

        listaPrescrizioni.getColumns().add(denominazioneFarmaco);
        listaPrescrizioni.getColumns().add(codFarmaco);
        listaPrescrizioni.getColumns().add(dataPrescrizione);
        listaPrescrizioni.getColumns().add(durataTerapia);
        listaPrescrizioni.getColumns().add(numeroDosiGiornaliere);
        listaPrescrizioni.getColumns().add(qtaFarmacoDose);
        listaPrescrizioni.getColumns().add(csPaziente);
        listaPrescrizioni.getColumns().add(cfMedico);
        listaPrescrizioni.getColumns().add(codPrescrizione);

        final ObservableList<prescrizioneForTable> data = FXCollections.observableArrayList(
                prescrPerTabella
        );

        for (prescrizioneForTable temp : data){
            listaPrescrizioni.getItems().add(temp);
        }

        setField();

        listaPrescrizioni.setOnMouseClicked(new EventHandler<MouseEvent>(){

            @Override
            public void handle(MouseEvent mouseEvent) {
                if( mouseEvent.getClickCount() == 1 && (listaPrescrizioni.getSelectionModel().getSelectedItem()) != null){
                    System.out.println(Integer.toString(((prescrizioneForTable) listaPrescrizioni.getSelectionModel().getSelectedItem()).getCodPrescrizione()));

                    prescrizioneForTable prescrizioneSelez = (prescrizioneForTable) listaPrescrizioni.getSelectionModel().getSelectedItem();

                    infermiereRespLabel.setText(userAccount.getCodFiscale().toString());
                    dataSommField.setValue(LocalDate.now());
                    pazienteLabel.setText(ricovero.getPazienteRicoverato().getCognome() + " " + ricovero.getPazienteRicoverato().getNome());
                    doseSomm.setText(prescrizioneSelez.getQtaFarmacoDose()+"");
                    prescCod.setText(prescrizioneSelez.getCodPrescrizione()+"");
                    farmacoField.setText(prescrizioneSelez.getDenominazioneFarmaco());
                    //notePaziente.setText();
                    codFarmacoField.setText(prescrizioneSelez.getCodFarmaco()+"");
                    Calendar rightNow = Calendar.getInstance();

                    SimpleDateFormat dft = new SimpleDateFormat();
                    dft.applyPattern("HH:mm");

                    momentoSomm.setText(dft.format(rightNow.getTime()));

                    confermaPane.setVisible(true);
                }
            }
        });
    }

    public void confermaSomministrazione(){
        if(dataSommField.getValue() == null || dataSommField.getValue().toString() == "" || farmacoField.getText() == null || farmacoField.getText() == "" || codFarmacoField.getText() == null || codFarmacoField.getText() == "" || momentoSomm.getText() == null || momentoSomm.getText() == "" || doseSomm.getText() == null || doseSomm.getText() == "" || prescCod.getText() == null || prescCod.getText() == ""){
            errorLabel.setText("Errore: compila corretamente tutti i campi!");
            return;
        }else{
            DataBase db = DataBase.getDataBaseInstance();
            //errorLabel.setText(dataPrescField.getValue().toString());
            //System.out.println("ERRORE? " + Integer.valueOf(durataTerapiaField.getText()));
            //Prescrizione prescrizione = new Prescrizione(farmacoSelezionato, Date.valueOf(dataPrescField.getValue()), Integer.valueOf(durataTerapiaField.getText()), Integer.valueOf(numDosiField.getText()), (new FloatStringConverter()).fromString(qtaDoseField.getText()), new Medico(userAccount), ricovero, 0);
            Calendar cal = Calendar.getInstance();
            int hour = Integer.valueOf((momentoSomm.getText()).substring(0,2));
            int min = Integer.valueOf((momentoSomm.getText()).substring(3,5));
            System.out.println("Hour " + hour + " minute: " + min);
            cal.set(Calendar.HOUR_OF_DAY, hour);
            cal.set(Calendar.MINUTE, min);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);

            Timestamp timeSomm = new Timestamp(cal.getTimeInMillis());

            Somministrazione somministrazione = new Somministrazione(infermiereRespLabel.getText(), timeSomm, Float.valueOf(doseSomm.getText()), notePaziente.getText(), Integer.valueOf(prescCod.getText()));
            if(db.registraSomministrazione(somministrazione)){
                errorLabel.setTextFill(Color.rgb(110,195, 111));
                errorLabel.setText("Somministrazione registrata con successo");
            }else{
                resetErrorLabel();
                errorLabel.setText("Errore durante la convalida della prescrizione");
            }
        }
    }

    public void setField() {
        momentoSomm.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                try {
                    if (!newValue.matches("\\d{0,2}([\\:]\\d{0,2})?")) {
                        momentoSomm.setText("");
                        errorLabel.setTextFill(Color.rgb(255, 107, 0));
                        errorLabel.setText("Inserire correttamente la qta per dose (al massimo 2 cifre decimali!)");
                    } else {
                        //resetErrorLabel();
                        errorLabel.setText("");
                    }
                } catch (IllegalArgumentException e) {
                    System.err.println(e);
                }
            }
        });
    }

    public void resetErrorLabel(){
        errorLabel.setTextFill(Color.rgb(255, 107, 0));
    }

}


