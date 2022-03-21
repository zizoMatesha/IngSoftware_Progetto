package desClass;

import db.DataBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import srcClass.LoggedUser;
import srcClass.Ricovero;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class homeController {
    @FXML
    Label numPazienti;
    @FXML
    Label accountUtenteInfo;
    @FXML
    Label numPazientiRegistrati;
    @FXML
    Label numCartelleCliniche;
    @FXML
    AnchorPane tipsField;
    @FXML
    TableView pazientiRicoveratiTable;
    @FXML
    Button archivioCartelleCliniche;
    @FXML
    AnchorPane contentWindow;

    DataBase db;
    LoggedUser userAccount;


    public void initData(DataBase db, LoggedUser userAccount){
        this.db = db;
        this.userAccount = userAccount;
        ArrayList<Ricovero> ricoveriArchiviati = db.getRicoveriArchiviati();
        if(ricoveriArchiviati == null){numCartelleCliniche.setText("In uno dei passati ricoveri non è stata settata la data di fine ricovero");}
        else{numCartelleCliniche.setText(ricoveriArchiviati.size()+"");}

        accountUtenteInfo.setText("ACCOUNT UTENTE: " + userAccount.getNomeUtente());
        if(userAccount.getAccountType() == 1)   accountUtenteInfo.setText(accountUtenteInfo.getText() + " - Medico");
        else if(userAccount.getAccountType() == 2)   accountUtenteInfo.setText(accountUtenteInfo.getText() + " - Infermiere");
        else if(userAccount.getAccountType() == 3)   accountUtenteInfo.setText(accountUtenteInfo.getText() + " - Primario");

        List<Ricovero> listaRicoveri = db.getPatientsHospitalized();
        numPazienti.setText(listaRicoveri.size() + "");

        //Set tabella dei ricoveri
        TableColumn<staticPageController.RicoveroForTable, String> nameColumn =          new TableColumn("Nome");
        TableColumn<staticPageController.RicoveroForTable, String> surnameColumn =       new TableColumn("Cognome");
        TableColumn<staticPageController.RicoveroForTable, Integer> idRicovero =         new TableColumn("Codice sanitario");
        TableColumn<staticPageController.RicoveroForTable, Date> dataInizioRicovero =    new TableColumn("Data inizio ricovero");


        nameColumn.setCellValueFactory(             new PropertyValueFactory<staticPageController.RicoveroForTable, String>("nome"));
        surnameColumn.setCellValueFactory(          new PropertyValueFactory<staticPageController.RicoveroForTable, String>("cognome"));
        idRicovero.setCellValueFactory(             new PropertyValueFactory<staticPageController.RicoveroForTable, Integer>("codSanitario"));
        dataInizioRicovero.setCellValueFactory(     new PropertyValueFactory<staticPageController.RicoveroForTable, Date>("dataInizioRicovero"));

        pazientiRicoveratiTable.getColumns().add(nameColumn);
        pazientiRicoveratiTable.getColumns().add(surnameColumn);
        pazientiRicoveratiTable.getColumns().add(idRicovero);
        pazientiRicoveratiTable.getColumns().add(dataInizioRicovero);

        ArrayList<RicoveroForTable> listaRicoveriForTable = new ArrayList<>();
        for(Ricovero ric : listaRicoveri)
            listaRicoveriForTable.add(new RicoveroForTable(ric));

        final ObservableList<RicoveroForTable> data = FXCollections.observableArrayList(
                listaRicoveriForTable
        );

        pazientiRicoveratiTable.setItems(data);


    }

    public void closeTips(){
        tipsField.setVisible(false);
    }

    public class RicoveroForTable extends Ricovero{
        private String nome;
        private String cognome;
        private String codSanitario;
        private Date dataInizioRicovero;
        public RicoveroForTable(Ricovero ricovero){
            super(ricovero);
            nome = ricovero.getPazienteRicoverato().getNome();
            cognome = ricovero.getPazienteRicoverato().getCognome();
            codSanitario = ricovero.getPazienteRicoverato().getCodSanitario();
            dataInizioRicovero = ricovero.getDataInizioRicovero();
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
    }

    public void loadArchivio(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("archivioFXML.fxml"));
        AnchorPane newAnchorPane = null;
        try {
            newAnchorPane = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        loader.<archivioController>getController().initData(userAccount);

        AnchorPane.setTopAnchor(newAnchorPane, 0.0);
        AnchorPane.setBottomAnchor(newAnchorPane, 0.0);
        AnchorPane.setLeftAnchor(newAnchorPane, 0.0);
        AnchorPane.setRightAnchor(newAnchorPane, 0.0);
        contentWindow.getChildren().removeAll(contentWindow.getChildren());
        contentWindow.getChildren().add(newAnchorPane);
    }

}
