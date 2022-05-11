package cz.zcu.kiv.spac.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NewLabelWindowController {

    private static Logger log = LogManager.getLogger(NewLabelWindowController.class);

    @FXML
    public TextField txtName;
    @FXML
    public Button btnAdd;

    private void close() {
        Stage stage = (Stage) btnAdd.getScene().getWindow();
        stage.close();
    }

    public void btnAddAction(ActionEvent actionEvent) {
        close();
    }

    public String getLabelName() {
        return txtName.getText();
    }
}
