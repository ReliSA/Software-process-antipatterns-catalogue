package cz.zcu.kiv.spac.controllers;

import cz.zcu.kiv.spac.data.Constants;
import cz.zcu.kiv.spac.data.antipattern.Antipattern;
import cz.zcu.kiv.spac.data.antipattern.AntipatternContent;
import cz.zcu.kiv.spac.data.antipattern.heading.AntipatternHeading;
import cz.zcu.kiv.spac.markdown.MarkdownParser;
import cz.zcu.kiv.spac.template.Template;
import cz.zcu.kiv.spac.template.TemplateField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Controller for antipattern raw window.
 */
public class AntipatternRawWindowController {

    @FXML
    private TextArea txtAreaRawAntipatternContent;

    @FXML
    private ListView listDifferences;

    @FXML
    private Button btnBack;

    @FXML
    private Button btnSave;


    private Antipattern antipattern;
    private Antipattern tempAntipattern;
    private List<String> differences;
    private Template template;
    private MarkdownParser parser;

    private boolean antipatternUpdated = false;

    // Logger.
    private static Logger log = Logger.getLogger(AntipatternRawWindowController.class);

    /**
     * Constructor.
     */
    public AntipatternRawWindowController() {

    }

    /**
     * Initialize method for antipattern raw window.
     */
    @FXML
    public void initialize() {

        // Set line break.
        txtAreaRawAntipatternContent.setWrapText(true);
    }

    /**
     * Close antipattern raw window.
     * @param actionEvent Button back click action event.
     */
    @FXML
    private void closeAntipatternRawWindow(ActionEvent actionEvent) {

        Stage stage = (Stage) btnBack.getScene().getWindow();
        stage.close();
    }

    /**
     * Save raw content of antipattern to file.
     * @param actionEvent - Action event.
     */
    @FXML
    private void saveRawAntipattern(ActionEvent actionEvent) {

        tempAntipattern.setContent(txtAreaRawAntipatternContent.getText());
        tempAntipattern.setAntipatternHeadings(parser.parseHeadings(txtAreaRawAntipatternContent.getText()));

        List<String> differences = template.getHeadingDifferences(tempAntipattern);

        if (differences.size() == 0) {
            antipatternUpdated = true;

            Stage stage = (Stage) btnBack.getScene().getWindow();
            stage.close();

        } else {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(Constants.APP_NAME);
            alert.setHeaderText("Antipattern content does not match template !");
            alert.setContentText("Antipattern content does not match template fields !");
            alert.show();

            listDifferences.getItems().clear();
            setDifferences(differences);

        }

    }

    public Antipattern getAntipattern() {

        return antipattern;
    }

    public void setAntipattern(Antipattern antipattern) {

        this.antipattern = antipattern;

        // Create temporary antipattern.
        AntipatternContent tempContent = new AntipatternContent(antipattern.getContent().toString());
        tempAntipattern = new Antipattern(antipattern.getName(), tempContent, antipattern.getPath());
        tempAntipattern.setAntipatternHeadings(antipattern.getAntipatternHeadings());

        // Set textarea text.
        txtAreaRawAntipatternContent.setText(tempAntipattern.getContent().toString());
    }

    public void setTemplate(Template template) {

        this.template = template;
    }

    public Antipattern getTempAntipattern() {

        return this.tempAntipattern;
    }

    public void setDifferences(List<String> differences) {

        this.differences = differences;

        listDifferences.getItems().addAll(differences);
    }

    public void setParser(MarkdownParser parser) {

        this.parser = parser;
    }

    public boolean isUpdated() {

        return this.antipatternUpdated;
    }
}
