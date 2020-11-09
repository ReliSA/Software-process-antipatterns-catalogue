package cz.zcu.kiv.spac.controllers;

import cz.zcu.kiv.spac.data.Constants;
import cz.zcu.kiv.spac.enums.GitLoginChoice;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;

/**
 * Controller for git login window.
 */
public class GitLoginController {

    @FXML
    private RadioButton rbUP;

    @FXML
    private RadioButton rbOA;

    @FXML
    private RadioButton rbJWT;

    @FXML
    private TextField txtfieldUsername;

    @FXML
    private TextField txtfieldOAuth;

    @FXML
    private TextField txtfieldJWT;

    @FXML
    private Button btnLogin;

    @FXML
    private PasswordField pfieldPassword;

    private GitLoginChoice loginChoice;

    private GitHub gitHub;

    private String gitConfigurationUsername;


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

        ToggleGroup rbGroup = new ToggleGroup();
        rbUP.setToggleGroup(rbGroup);
        rbOA.setToggleGroup(rbGroup);
        rbJWT.setToggleGroup(rbGroup);

        gitHub = null;

        disableAllControls();
    }

    /**
     * Login action.
     * @param actionEvent Action event.
     */
    @FXML
    private void btnGitLoginAction(ActionEvent actionEvent) {

        GitHub gitHubb = null;

        try {

            if (loginChoice != null) {

                switch (loginChoice) {

                    case JWT:

                        String jwt = txtfieldJWT.getText();

                        if (!jwt.equals("")) {

                            gitHubb = new GitHubBuilder().withJwtToken(jwt).build();
                        }

                        break;

                    case OAUTH:

                        String oauth = txtfieldOAuth.getText();

                        if (!oauth.equals("")) {

                            gitHubb = new GitHubBuilder().withOAuthToken(oauth).build();
                        }

                        break;

                    case USERNAME_PASSWORD:

                        String username = txtfieldUsername.getText();
                        String password = pfieldPassword.getText();

                        if (!username.equals("") && !password.equals("")) {

                            gitHubb = new GitHubBuilder().withPassword(username, password).build();
                        }

                        break;
                }
            }

            // If github login was successful, then close window.
            if (gitHubb != null && gitHubb.getMyself() != null) {

                Stage stage = (Stage) btnLogin.getScene().getWindow();
                gitHub = gitHubb;

                stage.close();

            }

        } catch (Exception e) {

            // Otherwise print error
            Alert alert = new Alert(Alert.AlertType.NONE);
            alert.setTitle(Constants.APP_NAME);
            alert.setAlertType(Alert.AlertType.ERROR);
            alert.setHeaderText("Login into git.");
            alert.setContentText("Login parameters are invalid!");
            alert.showAndWait();
        }
    }

    /**
     * Enable all controls prior to username + password login.
     * @param actionEvent - Action event.
     */
    @FXML
    private void rbUPAction(ActionEvent actionEvent) {

        disableAllControls();
        txtfieldUsername.setDisable(false);

        // If git username was set in configuration.
        if (!gitConfigurationUsername.equals(Constants.LBL_GIT_LOGGED_USER_DEFAULT)) {

            txtfieldUsername.setText(gitConfigurationUsername);
        }

        pfieldPassword.setDisable(false);

        loginChoice = GitLoginChoice.USERNAME_PASSWORD;
    }

    /**
     * Enable all controls prior to OAuth login.
     * @param actionEvent - Action event.
     */
    @FXML
    private void rbOAAction(ActionEvent actionEvent) {

        disableAllControls();
        txtfieldOAuth.setDisable(false);

        loginChoice = GitLoginChoice.OAUTH;
    }

    /**
     * Enable all controls prior to JWT login.
     * @param actionEvent - Action event.
     */
    @FXML
    private void rbJWTAction(ActionEvent actionEvent) {

        disableAllControls();
        txtfieldJWT.setDisable(false);

        loginChoice = GitLoginChoice.JWT;
    }

    /**
     * Disable all controls.
     */
    private void disableAllControls() {

        txtfieldUsername.setDisable(true);
        txtfieldUsername.setText("");

        txtfieldOAuth.setDisable(true);
        txtfieldOAuth.setText("");

        txtfieldJWT.setDisable(true);
        txtfieldJWT.setText("");

        pfieldPassword.setDisable(true);
        pfieldPassword.setText("");
    }

    public void setGitConfigurationUsername(String username) {

        this.gitConfigurationUsername = username;
    }

    public GitHub getGitHub() {

        return gitHub;
    }
}
