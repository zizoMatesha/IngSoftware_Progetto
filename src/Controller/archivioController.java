package Controller;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import db.DataBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import srcClass.LoggedUser;
import srcClass.Ricovero;
import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

public class archivioController {
    @FXML
    TableView cartelleClinicheTable;
    @FXML
    AnchorPane letteraDimissioni;
    @FXML
    Label pazienteField;
    @FXML
    Label dataInizioField;
    @FXML
    Label dataFineField;
    @FXML
    TextArea diagnosiDimissioneField;
    @FXML
    TextArea terapiaRicoveroField;
    @FXML
    TextArea terapiaDomicilioField;
    @FXML
    Label errorLabel;
    //Search Nav
    @FXML
    TextField codSanitarioSearch;
    @FXML
    TextField nomePazienteSearch;
    @FXML
    TextField cognomePazienteSearch;
    @FXML
    DatePicker startDateSearch;
    @FXML
    DatePicker endDateSearch;

    private LoggedUser userAccount;

    public void initData(LoggedUser userAccount){
        this.userAccount = userAccount;
        loadTable();
    }

    public void getPDFinfo(int idRicovero) throws IOException {
        errorLabel.setText("");
        DataBase db = DataBase.getDataBaseInstance();
        Ricovero ricoveroSelezionato = db.getRicoveroArchiviato(Integer.valueOf(idRicovero));


        Calendar date = Calendar.getInstance();
        date.setTime(ricoveroSelezionato.getDataFineRicovero());

        PdfDocument pdfDoc = new PdfDocument(new PdfReader(new File("src/documenti/lettereDimissioni/"+ ricoveroSelezionato.getPazienteRicoverato().getCodSanitario() + "_" + ricoveroSelezionato.getIdRicovero() +
                "_" + date.get(Calendar.DAY_OF_MONTH) + "" + date.get(Calendar.MONTH)+1 + "" + date.get(Calendar.YEAR) + ".pdf")));
        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);

        Map fields = form.getFormFields();

        pazienteField.setText(              ((PdfFormField) fields.get("pazienteNomeCognome")).getValueAsString());
        dataInizioField.setText(            (((PdfFormField) fields.get("ggInizioRicovero"))).getValueAsString() + "/" + ((PdfFormField) fields.get("meseInizioRicovero")).getValueAsString() + "/" + ((PdfFormField) fields.get("annoInizioRicovero")).getValueAsString());
        dataFineField.setText(              (((PdfFormField) fields.get("ggFineRicovero"))).getValueAsString() + "/" + ((PdfFormField) fields.get("meseFineRicovero")).getValueAsString() + "/" + ((PdfFormField) fields.get("annoFineRicovero")).getValueAsString());
        diagnosiDimissioneField.setText(    ((PdfFormField) fields.get("diagnosiDimissione")).getValueAsString());
        terapiaRicoveroField.setText(       ((PdfFormField) fields.get("terapiaField")).getValueAsString());
        terapiaDomicilioField.setText(      ((PdfFormField) fields.get("terapiaDomicilio")).getValueAsString());
        pdfDoc.close();
    }

    public void loadTable(){
        DataBase db = DataBase.getDataBaseInstance();

        ArrayList<Ricovero> listaRicoveri = db.getRicoveriArchiviati();

        //Set tabella dei ricoveri pregeressi
        TableColumn<staticPageController.RicoveroForTable, String> nameColumn =          new TableColumn("Nome");
        TableColumn<staticPageController.RicoveroForTable, String> surnameColumn =       new TableColumn("Cognome");
        TableColumn<staticPageController.RicoveroForTable, Integer> codSanitarioPaziente =         new TableColumn("Codice sanitario");
        TableColumn<staticPageController.RicoveroForTable, Date> dataInizioRicovero =    new TableColumn("Data inizio ricovero");
        TableColumn<staticPageController.RicoveroForTable, Date> dataFineRicovero =    new TableColumn("Data fine ricovero");
        TableColumn<staticPageController.RicoveroForTable, Integer> idRicovero =    new TableColumn("Id ricovero");


        nameColumn.setCellValueFactory(             new PropertyValueFactory<staticPageController.RicoveroForTable, String>("nome"));
        surnameColumn.setCellValueFactory(          new PropertyValueFactory<staticPageController.RicoveroForTable, String>("cognome"));
        codSanitarioPaziente.setCellValueFactory(             new PropertyValueFactory<staticPageController.RicoveroForTable, Integer>("codSanitario"));
        dataInizioRicovero.setCellValueFactory(     new PropertyValueFactory<staticPageController.RicoveroForTable, Date>("dataInizioRicovero"));
        dataFineRicovero.setCellValueFactory(     new PropertyValueFactory<staticPageController.RicoveroForTable, Date>("dataFineRicovero"));
        idRicovero.setCellValueFactory(             new PropertyValueFactory<staticPageController.RicoveroForTable, Integer>("idRicovero"));


        cartelleClinicheTable.getColumns().add(nameColumn);
        cartelleClinicheTable.getColumns().add(surnameColumn);
        cartelleClinicheTable.getColumns().add(codSanitarioPaziente);
        cartelleClinicheTable.getColumns().add(dataInizioRicovero);
        cartelleClinicheTable.getColumns().add(dataFineRicovero);
        cartelleClinicheTable.getColumns().add(idRicovero);
        cartelleClinicheTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        ArrayList<RicoveroForTable> listaRicoveriForTable = new ArrayList<>();
        for(Ricovero ric : listaRicoveri)
            listaRicoveriForTable.add(new RicoveroForTable(ric));

        final ObservableList<RicoveroForTable> data = FXCollections.observableArrayList(
                listaRicoveriForTable
        );

        cartelleClinicheTable.setItems(data);

        cartelleClinicheTable.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 1 && (cartelleClinicheTable.getSelectionModel().getSelectedItem() != null)) {
                    try {
                        getPDFinfo(((RicoveroForTable) cartelleClinicheTable.getItems().get(cartelleClinicheTable.getSelectionModel().getSelectedIndex())).getIdRicovero());
                    }catch (IOException e){
                        errorLabel.setText("Impossibile trovare in archivio la cartella del paziente ");
                    }
                }
            }
        });
    }

    public void loadTableFiltered(){
        DataBase db = DataBase.getDataBaseInstance();
        ArrayList<Ricovero> ricoveriFiltrati = db.getRicoveriArchiviatiFiltrati(nomePazienteSearch.getText(), cognomePazienteSearch.getText(), codSanitarioSearch.getText(), startDateSearch.getValue(), endDateSearch.getValue());

        cartelleClinicheTable.getItems().clear();

        ArrayList<RicoveroForTable> listaRicoveriForTable = new ArrayList<>();
        for(Ricovero ric : ricoveriFiltrati)
            listaRicoveriForTable.add(new RicoveroForTable(ric));

        final ObservableList<RicoveroForTable> data = FXCollections.observableArrayList(
                listaRicoveriForTable
        );

        cartelleClinicheTable.setItems(data);

        cartelleClinicheTable.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 1 && (cartelleClinicheTable.getSelectionModel().getSelectedItem() != null)) {
                    try {
                        getPDFinfo(((RicoveroForTable) cartelleClinicheTable.getItems().get(cartelleClinicheTable.getSelectionModel().getSelectedIndex())).getIdRicovero());
                    }catch (IOException e){
                        errorLabel.setText("Impossibile trovare in archivio la cartella del paziente ");
                    }
                }
            }
        });
    }

    public class RicoveroForTable extends Ricovero {
        private String nome;
        private String cognome;
        private String codSanitario;
        private Date dataInizioRicovero;
        private Date dataFineRicovero;
        private int idRicovero;

        public RicoveroForTable(Ricovero ricovero){
            super(ricovero);
            nome = ricovero.getPazienteRicoverato().getNome();
            cognome = ricovero.getPazienteRicoverato().getCognome();
            codSanitario = ricovero.getPazienteRicoverato().getCodSanitario();
            dataInizioRicovero = ricovero.getDataInizioRicovero();
            dataFineRicovero = ricovero.getDataFineRicovero();
            idRicovero = ricovero.getIdRicovero();
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

        @Override
        public Date getDataFineRicovero() {
            return dataFineRicovero;
        }

        @Override
        public void setDataFineRicovero(Date dataFineRicovero) {
            this.dataFineRicovero = dataFineRicovero;
        }

        @Override
        public int getIdRicovero() {
            return idRicovero;
        }

        @Override
        public void setIdRicovero(int idRicovero) {
            this.idRicovero = idRicovero;
        }
    }

    public void StampaReport(){
        ObservableList<RicoveroForTable> lista = cartelleClinicheTable.getItems();
        //TODO:

    }
}
