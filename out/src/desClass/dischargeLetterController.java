package desClass;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import db.DataBase;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import srcClass.LoggedUser;
import srcClass.Medico;
import srcClass.Ricovero;

import java.io.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Map;

public class dischargeLetterController {

    private Ricovero ricovero;
    private LoggedUser userAccount;
    private Medico primario;

    @FXML
    TextField pazienteFieldFXML;
    @FXML
    TextField uoFieldFXML;
    @FXML
    TextField dataInizioRicFXML;
    @FXML
    TextField dataFineRicFXML;
    @FXML
    TextArea accertamentiFXML;
    @FXML
    TextArea terapiaFXML;
    @FXML
    TextArea diagnosiFXML;
    @FXML
    TextArea terapiaDomicilioFXML;
    @FXML
    TextField numTelefonicoFXML;
    @FXML
    TextField dataOdiernaFXML;
    @FXML
    ImageView confermaLettera;
    @FXML
    AnchorPane contPane;

    public void initData(Ricovero ric, LoggedUser userAccount){
        ricovero = ric;
        this.userAccount = userAccount;

        primario = (new DataBase()).getMedicoFromLoggedUser(userAccount);
        if(primario != null) {
            contPane.setVisible(true);
            pazienteFieldFXML.setText(ricovero.getPazienteRicoverato().getNome() + " " + ricovero.getPazienteRicoverato().getCognome());
            pazienteFieldFXML.setEditable(false);

            dataInizioRicFXML.setText(ricovero.getDataInizioRicovero().toLocalDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
            dataInizioRicFXML.setEditable(false);

            dataOdiernaFXML.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
            dataOdiernaFXML.setEditable(false);

            confermaLettera.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    if (pazienteFieldFXML.getText().isEmpty()) {
                        showAlert(Alert.AlertType.ERROR, contPane.getScene().getWindow(), "Errore compilazione lettera!", "Inserisci i dati del paziente!");
                        return;
                    }
                    if (uoFieldFXML.getText().isEmpty()) {
                        showAlert(Alert.AlertType.ERROR, contPane.getScene().getWindow(), "Errore compilazione lettera!", "Inserisci il nome dell'unità ospedaliera!");
                        return;
                    }
                    if (numTelefonicoFXML.getText().isEmpty()) {
                        showAlert(Alert.AlertType.ERROR, contPane.getScene().getWindow(), "Errore compilazione lettera!", "Inserisci il recapito telefonico!");
                        return;
                    }
                    if (dataFineRicFXML.getText().isEmpty()) {
                        showAlert(Alert.AlertType.ERROR, contPane.getScene().getWindow(), "Errore compilazione lettera!", "Manca la data di fine ricovero!");
                        return;
                    }
                    ricovero.setDataFineRicovero(Date.valueOf(LocalDate.now()));
                    confirm();
                }
            });
        }else{
            contPane.setVisible(false);
        }

    }

    private void showAlert(Alert.AlertType alertType, Window owner, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(owner);
        alert.show();
    }

    public void confirm(){
        DataBase db = new DataBase();
        if(db.closeRicovero(ricovero)) {
            try {
                compilePDF();
                showAlert(Alert.AlertType.CONFIRMATION, contPane.getScene().getWindow(), "Conferma", "Lettera di dimissioni caricata corretamente!\nPaziente " + ricovero.getPazienteRicoverato().getNome() + " " + ricovero.getPazienteRicoverato().getCognome() + " dimesso.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            showAlert(Alert.AlertType.ERROR, contPane.getScene().getWindow(), "Errore", "Errore generico nella chiusura del ricovero, ritenta.");
            //Files.deleteIfExists(Paths.get("src/documenti/"+ ricovero.getIdRicovero() + "_" + ricovero.getPazienteRicoverato().getCodSanitario() + "_" + date.get(Calendar.DAY_OF_MONTH) + "" + date.get(Calendar.MONTH)+1 + "" + date.get(Calendar.YEAR) + ".pdf"));
        }
    }

    public void compilePDF() throws IOException {
        Calendar date = Calendar.getInstance();
        date.setTime(ricovero.getDataFineRicovero());

        PdfDocument pdfDoc = new PdfDocument(new PdfReader(new File("src/documenti/LetteraDimissioni.pdf")), new PdfWriter("src/documenti/lettereDimissioni/"+ ricovero.getPazienteRicoverato().getCodSanitario() + "_" + ricovero.getIdRicovero() +
                "_" + date.get(Calendar.DAY_OF_MONTH) + "" + date.get(Calendar.MONTH)+1 + "" + date.get(Calendar.YEAR) + ".pdf"));
        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);

        Map fields = form.getFormFields();
        ((PdfFormField) fields.get("pazienteNomeCognome")).setValue(pazienteFieldFXML.getText());
        ((PdfFormField) fields.get("nomeUO")).setValue(             uoFieldFXML.getText());

        ((PdfFormField) fields.get("ggInizioRicovero")).setValue(   dataInizioRicFXML.getText().substring(0,2));
        ((PdfFormField) fields.get("meseInizioRicovero")).setValue( dataInizioRicFXML.getText().substring(3,5));
        ((PdfFormField) fields.get("annoInizioRicovero")).setValue( dataInizioRicFXML.getText().substring(6,10));

        ((PdfFormField) fields.get("ggFineRicovero")).setValue(     dataFineRicFXML.getText().substring(0,2));
        ((PdfFormField) fields.get("meseFineRicovero")).setValue(   dataFineRicFXML.getText().substring(3,5));
        ((PdfFormField) fields.get("annoFineRicovero")).setValue(   dataFineRicFXML.getText().substring(6,10));

        ((PdfFormField) fields.get("accertamentiField")).setValue(  accertamentiFXML.getText());
        ((PdfFormField) fields.get("accertamentiField")).setReadOnly(true);

        ((PdfFormField) fields.get("terapiaField")).setValue(       terapiaFXML.getText());
        ((PdfFormField) fields.get("terapiaField")).setReadOnly(true);

        ((PdfFormField) fields.get("diagnosiDimissione")).setValue( diagnosiFXML.getText());
        ((PdfFormField) fields.get("diagnosiDimissione")).setReadOnly(true);

        ((PdfFormField) fields.get("terapiaDomicilio")).setValue(   terapiaFXML.getText());
        ((PdfFormField) fields.get("terapiaDomicilio")).setReadOnly(true);

        ((PdfFormField) fields.get("numTelReparto")).setValue(      numTelefonicoFXML.getText());
        ((PdfFormField) fields.get("numTelReparto")).setReadOnly(true);

        ((PdfFormField) fields.get("ggOggi")).setValue(             dataOdiernaFXML.getText().substring(0,2));
        ((PdfFormField) fields.get("meseOggi")).setValue(           dataOdiernaFXML.getText().substring(3,5));
        ((PdfFormField) fields.get("annoOggi")).setValue(           dataOdiernaFXML.getText().substring(6,10));
        pdfDoc.close();


        Stage stage = (Stage) contPane.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("staticPage.fxml"));
        stage.setScene(new Scene((AnchorPane) loader.load()));
        stage.setMaximized(true);
        loader.<staticPageController>getController().initData(userAccount);

    }

}
