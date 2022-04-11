package cz.zcu.kiv.spac.controllers;

import cz.zcu.kiv.spac.bibtex.BibtexParser;
import cz.zcu.kiv.spac.data.Constants;
import cz.zcu.kiv.spac.data.reference.Reference;
import cz.zcu.kiv.spac.data.reference.References;
import cz.zcu.kiv.spac.file.FileWriter;
import cz.zcu.kiv.spac.markdown.MarkdownGenerator;
import cz.zcu.kiv.spac.utils.Utils;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jbibtex.BibTeXDatabase;

import java.io.File;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class ReferencesManagerWindowController implements Initializable {

    private static Logger log = LogManager.getLogger(ReferencesManagerWindowController.class);

    @FXML
    ListView<Reference> lvReferences;

    @FXML
    Button btnDone;

    private References references;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        lvReferences.setCellFactory(CheckBoxListCell.forListView(Reference::selectedProperty, new StringConverter<>() {
            @Override
            public String toString(Reference reference) {
                return reference.getShortcut() + " - " + reference.getTitle() + " (" + reference.getAuthor() + ")";
            }

            @Override
            public Reference fromString(String s) {
                return null;
            }
        }));
    }

    private void close() {
        Stage stage = (Stage) btnDone.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void btnDoneAction(ActionEvent actionEvent) {
        close();
    }

    @FXML
    public void btnNewReferenceAction(ActionEvent actionEvent) {
        try {
            String stageTitle = Constants.APP_NAME;

            // Load antipattern window template.
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource(Constants.RESOURCE_NEW_REFERENCE_WINDOW)));
            Parent root = loader.load();

            Stage stage = new Stage();

            // Create new antipattern window controller and set values.
            NewReferenceWindowController newReferenceWindowController;
            newReferenceWindowController = loader.getController();

            // Set stage.
            stage.setTitle(stageTitle);
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            // If new reference was added to bibtex file, generate new References.md and update references object.
            if (newReferenceWindowController.isReferenceAdded()) {

                BibTeXDatabase database = BibtexParser.parseBibTeX(new File(Utils.getRootDir() + "/" + Constants.BIBTEX_REFERENCES_NAME));
                this.references = MarkdownGenerator.generateReferencesFromBibtex(database.getObjects());

                generateReferencesFile();
            }

        } catch (Exception e) {
            log.error("Invalid NewReferenceWindow scene.");
        }
    }

    private void generateReferencesFile() {
        File referencesFile = new File(Utils.getRootDir() + "/" + Constants.REFERENCES_NAME);

        if (!referencesFile.exists()) {
            try {
                boolean fileCreated = referencesFile.createNewFile();

                if (!fileCreated) {
                    log.warn("Cannot create new file with name: " + referencesFile.getAbsolutePath());
                    return;
                }
            } catch (Exception e) {
                log.warn("Cannot create new file with name: " + referencesFile.getAbsolutePath());
                return;
            }
        }

        FileWriter.write(referencesFile, references.getMarkdownFormat());
        log.info("Generating References.md was successfuly completed.");
        Utils.showAlertWindow(Alert.AlertType.INFORMATION, Constants.APP_NAME, "Generating References.md",
                "Generating References.md was successfuly completed.");
    }

    public void initReferences(References references) {
        this.references = references;
        lvReferences.setItems(FXCollections.observableArrayList(this.references.getReferenceMap().values()));
    }

    public References getReferences() {
        return this.references;
    }
}
