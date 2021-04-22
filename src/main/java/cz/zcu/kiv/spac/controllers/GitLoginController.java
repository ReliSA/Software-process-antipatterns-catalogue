package cz.zcu.kiv.spac.controllers;

import cz.zcu.kiv.spac.data.Constants;
import cz.zcu.kiv.spac.utils.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

/**
 * Controller for git login window.
 */
public class GitLoginController {

    @FXML
    private TextField txtfieldPAT;

    @FXML
    private Button btnLogin;

    private String personalAccessToken;


    // Logger.
    private static Logger log = Logger.getLogger(GitLoginController.class);

    /**
     * Controller.
     */
    public GitLoginController() {

    }

    /**
     * Initialize.
     */
    @FXML
    public void initialize() {

    }

    /**
     * Login action.
     * @param actionEvent Action event.
     */
    @FXML
    private void btnGitLoginAction(ActionEvent actionEvent) {

        personalAccessToken = txtfieldPAT.getText();

        if (!personalAccessToken.equals("")) {

            Stage stage = (Stage) btnLogin.getScene().getWindow();
            stage.close();

        } else {

            Utils.showAlertWindow(Alert.AlertType.ERROR, Constants.APP_NAME, "Git authentication window.",
                    "Personal access token is not set!");
        }
    }


    public String getPersonalAccessToken() {

        return personalAccessToken;
    }

    public void setPersonalAccessToken(String personalAccessToken) {

        this.personalAccessToken = personalAccessToken;

        txtfieldPAT.setText(personalAccessToken);
    }
}
