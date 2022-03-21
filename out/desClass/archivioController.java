package desClass;

import db.DataBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import srcClass.LoggedUser;
import srcClass.Ricovero;


import java.sql.Date;
import java.util.ArrayList;

public class archivioController {
    @FXML
    TableView cartelleClinicheTable;

    private LoggedUser userAccount;

    public void initData(LoggedUser userAccount){
        this.userAccount = userAccount;
        DataBase db = new DataBase();

        ArrayList<Ricovero> listaRicoveri = db.getRicoveriArchiviati();

        //Set tabella dei ricoveri pregeressi
        TableColumn<staticPageController.RicoveroForTable, String> nameColumn =          new TableColumn("Nome");
        TableColumn<staticPageController.RicoveroForTable, String> surnameColumn =       new TableColumn("Cognome");
        TableColumn<staticPageController.RicoveroForTable, Integer> idRicovero =         new TableColumn("Codice sanitario");
        TableColumn<staticPageController.RicoveroForTable, Date> dataInizioRicovero =    new TableColumn("Data inizio ricovero");
        TableColumn<staticPageController.RicoveroForTable, Date> dataFineRicovero =    new TableColumn("Data fine ricovero");

        PDFDisplayer displayer = new PDFDisplayer();
        nameColumn.setCellValueFactory(             new PropertyValueFactory<staticPageController.RicoveroForTable, String>("nome"));
        surnameColumn.setCellValueFactory(          new PropertyValueFactory<staticPageController.RicoveroForTable, String>("cognome"));
        idRicovero.setCellValueFactory(             new PropertyValueFactory<staticPageController.RicoveroForTable, Integer>("codSanitario"));
        dataInizioRicovero.setCellValueFactory(     new PropertyValueFactory<staticPageController.RicoveroForTable, Date>("dataInizioRicovero"));
        dataFineRicovero.setCellValueFactory(     new PropertyValueFactory<staticPageController.RicoveroForTable, Date>("dataInizioRicovero"));

        cartelleClinicheTable.getColumns().add(nameColumn);
        cartelleClinicheTable.getColumns().add(surnameColumn);
        cartelleClinicheTable.getColumns().add(idRicovero);
        cartelleClinicheTable.getColumns().add(dataInizioRicovero);
        cartelleClinicheTable.getColumns().add(dataFineRicovero);
        cartelleClinicheTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        ArrayList<RicoveroForTable> listaRicoveriForTable = new ArrayList<>();
        for(Ricovero ric : listaRicoveri)
            listaRicoveriForTable.add(new RicoveroForTable(ric));

        final ObservableList<RicoveroForTable> data = FXCollections.observableArrayList(
                listaRicoveriForTable
        );

        cartelleClinicheTable.setItems(data);
    }

    public class RicoveroForTable extends Ricovero {
        private String nome;
        private String cognome;
        private String codSanitario;
        private Date dataInizioRicovero;
        private Date dataFineRicovero;

        public RicoveroForTable(Ricovero ricovero){
            super(ricovero);
            nome = ricovero.getPazienteRicoverato().getNome();
            cognome = ricovero.getPazienteRicoverato().getCognome();
            codSanitario = ricovero.getPazienteRicoverato().getCodSanitario();
            dataInizioRicovero = ricovero.getDataInizioRicovero();
            dataFineRicovero = ricovero.getDataFineRicovero();
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
    }
}
