package cz.zcu.kiv.spac.controllers;

import cz.zcu.kiv.spac.data.Constants;
import cz.zcu.kiv.spac.file.FileLoader;
import cz.zcu.kiv.spac.file.FileWriter;
import cz.zcu.kiv.spac.utils.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.io.File;

/**
 * Controller for window, where adding reference is managed.
 */
public class NewReferenceWindowController {

    @FXML
    private Button btnAddReference;

    @FXML
    private TextArea txtareaNewReference;

    private boolean referenceAdded = false;

    // Logger.
    private static Logger log = Logger.getLogger(NewReferenceWindowController.class);

    /**
     * Constructor.
     */
    public NewReferenceWindowController() {

    }

    /**
     * Button action for adding new reference from textarea.
     * @param actionEvent - Action event.
     */
    @FXML
    private void btnAddReferenceAction(ActionEvent actionEvent) {

        // Get new reference.
        String bibtexReference = txtareaNewReference.getText();

        // Load bibtex file and content.
        File bibtexFile = new File(Utils.getRootDir() + "/" + Constants.BIBTEX_REFERENCES_NAME);
        String bibtexContent = FileLoader.loadFileContent(bibtexFile.getAbsolutePath());

        // Append new reference to bibtex file.
        bibtexContent += "\n";
        bibtexContent += bibtexReference;

        // Save updated bibtex content.
        referenceAdded = FileWriter.write(bibtexFile, bibtexContent);

        Stage stage = (Stage) btnAddReference.getScene().getWindow();
        stage.close();
    }

    public boolean isReferenceAdded() {

        return referenceAdded;
    }
}
