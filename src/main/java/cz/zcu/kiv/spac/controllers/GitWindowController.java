package cz.zcu.kiv.spac.controllers;

import cz.zcu.kiv.spac.components.ListViewItemWithStringAndCheckBox;
import cz.zcu.kiv.spac.data.Constants;
import cz.zcu.kiv.spac.data.git.CommitType;
import cz.zcu.kiv.spac.data.git.CustomGitObject;
import cz.zcu.kiv.spac.file.FileLoader;
import cz.zcu.kiv.spac.utils.Utils;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

import java.util.*;

/**
 * Controller for git window.
 */
public class GitWindowController {

    @FXML
    private CheckBox chckboxSelectAll;

    @FXML
    private Label lblFetched;

    @FXML
    private Label lblCommitsAhead;

    @FXML
    private Label lblCommitsBehind;

    @FXML
    private WebView wviewChanges;

    @FXML
    private Button btnCommit;

    @FXML
    private TextArea txtareaDescription;

    @FXML
    private TextField txtfieldSummary;

    @FXML
    private ListView listViewFileChanged;

    @FXML
    private Label lblRepositoryName;

    @FXML
    private Label lblChangedFiles;

    @FXML
    private Label lblBranchName;

    @FXML
    private AnchorPane contentPane;


    private CustomGitObject customGitObject;
    private String defaultTextLblCommitAhead;
    private String defaultTextLblCommitBehind;
    private String defaultTextLblFetch;
    private int commitsAhead = 0;
    private int commitsBehind = 0;

    private boolean successfullyPulled = false;


    // Logger.
    private static Logger log = Logger.getLogger(GitWindowController.class);

    /**
     * Controller.
     */
    public GitWindowController() {

    }

    /**
     * Initialize.
     */
    @FXML
    public void initialize() {

        // Apply css styles to content pane.
        contentPane.getStylesheets().add(Constants.RESOURCE_GIT_WINDOW_STYLES_CSS);

        // Set line break for description.
        txtareaDescription.setWrapText(true);

        listViewFileChanged.setCellFactory(CheckBoxListCell.forListView(ListViewItemWithStringAndCheckBox::onProperty));
    }

    /**
     * Commit button action.
     * @param actionEvent - Action event.
     */
    @FXML
    private void btnCommitAction(ActionEvent actionEvent) {

        // TODO: create button for checking / unchecking all files for commit.

        String summary = txtfieldSummary.getText();
        String description = txtareaDescription.getText();

        ObservableList allFiles = listViewFileChanged.getItems();
        List<String> selectedFiles = new ArrayList<>();

        Git git = customGitObject.getGit();
        AddCommand addCommand = git.add();

        for (Object object : allFiles) {

            ListViewItemWithStringAndCheckBox item = (ListViewItemWithStringAndCheckBox) object;

            if (item.isOn()) {

                selectedFiles.add(item.getFilename());
                addCommand.addFilepattern(item.getFilename());
            }
        }

        if (selectedFiles.size() == 0) {

            // Create an alert.
            Alert alert = new Alert(Alert.AlertType.NONE);
            alert.setTitle(Constants.APP_NAME);
            alert.setAlertType(Alert.AlertType.WARNING);
            alert.setHeaderText("Creating commit");
            alert.setContentText("No files were selected for commit.");
            alert.showAndWait();
            return;
        }

        try {

            addCommand.call();
            git.commit().setMessage(summary + "\n" + description).call();

            txtareaDescription.setText("");
            txtfieldSummary.setText("");
            getDifferences();
            getBranchTrackingStatus();

        } catch (Exception e) {

            log.warn("Failed to create commit!");
        }
    }

    @FXML
    private void previewFile(MouseEvent mouseEvent) {

        Object item = listViewFileChanged.getSelectionModel().getSelectedItem();

        if (item == null) {

            mouseEvent.consume();
            return;
        }

        ListViewItemWithStringAndCheckBox selectedItem = (ListViewItemWithStringAndCheckBox) item;

        String content = FileLoader.loadFileContent(Utils.getRootDir() + "/" + selectedItem.getFilename());

        if (content != null) {

            wviewChanges.getEngine().loadContent(Utils.replaceLineBreakersForHTMLNewLine(content));

        } else {

            log.warn("File '" + selectedItem.getFilename() + "' did not exists in source control!");
        }
    }

    @FXML
    private void doPush() {

        if (commitsAhead == 0) {

            // Create an alert.
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(Constants.APP_NAME);
            alert.setHeaderText("Push to git");
            alert.setContentText("There are no commits to push.");
            alert.showAndWait();
            return;
        }

        try {

            // push to remote:
            PushCommand pushCommand = customGitObject.getGit().push();
            pushCommand.setCredentialsProvider(new UsernamePasswordCredentialsProvider(customGitObject.getUsername(), customGitObject.getPassword()));
            // you can add more settings here if needed
            pushCommand.call();

            log.info("Push command completed successfully.");

            // Create an alert.
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(Constants.APP_NAME);
            alert.setHeaderText("Push to git");
            alert.setContentText("Pushing commits to git was successful.");
            alert.showAndWait();

            getBranchTrackingStatus();

        } catch (Exception e) {

            log.warn("Invalid credentials!");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(Constants.APP_NAME);
            alert.setHeaderText("Push to git");
            alert.setContentText("Invalid credentials!");
            alert.showAndWait();

            openGitLoginWindow();
        }
    }

    @FXML
    private void doFetch(ActionEvent actionEvent) {

        doFetch();
    }

    @FXML
    private void doPull(ActionEvent actionEvent) {

        if (commitsBehind == 0) {
            // Create an alert.
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(Constants.APP_NAME);
            alert.setHeaderText("Pull from git");
            alert.setContentText("There are no commits to pull.");
            alert.showAndWait();
            return;
        }

        try {

            Git git = customGitObject.getGit();
            Repository repository = git.getRepository();

            ObjectId oldHead = repository.resolve("HEAD^{tree}");

            PullCommand pullCommand = customGitObject.getGit().pull();
            pullCommand.setCredentialsProvider(new UsernamePasswordCredentialsProvider(customGitObject.getUsername(), customGitObject.getPassword()));
            pullCommand.call();

            ObjectId head = repository.resolve("HEAD^{tree}");
            ObjectReader reader = repository.newObjectReader();
            CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
            oldTreeIter.reset(reader, oldHead);
            CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
            newTreeIter.reset(reader, head);

            List<DiffEntry> diffs = git.diff()
                    .setNewTree(newTreeIter)
                    .setOldTree(oldTreeIter)
                    .call();

            String content = "Pulling commits from git was successful. Pulled files (" + diffs.size() + "):";

            for (DiffEntry entry : diffs) {

                content += "\n" + entry.getNewPath();
            }

            log.info("Pull command completed successfully.");

            // Create an alert.
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(Constants.APP_NAME);
            alert.setHeaderText("Pull from git");
            alert.setContentText(content);
            alert.showAndWait();

            getBranchTrackingStatus();

            successfullyPulled = true;

        } catch (CheckoutConflictException ee) {

            log.warn("Checkout conflict exception!");
            log.warn(ee.getMessage());

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(Constants.APP_NAME);
            alert.setHeaderText("Pull from git");
            alert.setContentText(ee.getMessage());
            alert.showAndWait();

        } catch (Exception e) {

            log.warn("Invalid credentials!");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(Constants.APP_NAME);
            alert.setHeaderText("Pull from git");
            alert.setContentText("Invalid credentials!");
            alert.showAndWait();

            openGitLoginWindow();
        }
    }

    @FXML
    private void selectAllFiles(ActionEvent actionEvent) {

        for (Object object : listViewFileChanged.getItems()) {

            ListViewItemWithStringAndCheckBox item = (ListViewItemWithStringAndCheckBox) object;

            if (chckboxSelectAll.isSelected()) {

                item.setOn(true);

            } else {

                item.setOn(false);
            }
        }
    }

    private void doFetch() {

        try {

            customGitObject.getGit().fetch().call();
            lblFetched.setText(defaultTextLblFetch + Utils.getCurrentDateInString());
            getBranchTrackingStatus();

        } catch (Exception e) {

            Alert alert = new Alert(Alert.AlertType.NONE);
            alert.setTitle(Constants.APP_NAME);
            alert.setAlertType(Alert.AlertType.ERROR);
            alert.setHeaderText("Git fetch");
            alert.setContentText("Undefined error while fetching.");
            alert.showAndWait();

            log.warn("Error while fetching.");
        }
    }

    private void getDifferences() {

        try {

            listViewFileChanged.getItems().clear();
            Git git = customGitObject.getGit();

            Status status = git.status().call();

            Set<String> added = status.getAdded();
            for(String add : added) {

                listViewFileChanged.getItems().add(new ListViewItemWithStringAndCheckBox(add, false, CommitType.ADD));
            }

            Set<String> untracked = status.getUntracked();
            for(String untrack : untracked) {

                listViewFileChanged.getItems().add(new ListViewItemWithStringAndCheckBox(untrack, false, CommitType.ADD));
            }

            Set<String> modified = status.getModified();
            for(String modify : modified) {

                listViewFileChanged.getItems().add(new ListViewItemWithStringAndCheckBox(modify, false, CommitType.MODIFY));
            }

            Set<String> removed = status.getRemoved();
            for(String remove : removed) {

                listViewFileChanged.getItems().add(new ListViewItemWithStringAndCheckBox(remove, false, CommitType.REMOVE));
            }

            lblChangedFiles.setText(listViewFileChanged.getItems().size() + " changed files");

        } catch (Exception e) {

            log.warn("Error while getting differences.");
        }
    }

    /**
     * Try to connect to git via GitLogin window.
     */
    private void openGitLoginWindow() {

        try {

            String stageTitle = Constants.APP_NAME;

            // Load antipattern window template.
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource(Constants.RESOURCE_GIT_LOGIN_WINDOW)));
            Parent root = loader.load();

            Stage stage = new Stage();

            // Create new antipattern window controller and set values.
            GitLoginController gitLoginController;
            gitLoginController = loader.getController();
            gitLoginController.setUsername(customGitObject.getUsername());
            gitLoginController.setPassword(customGitObject.getPassword());

            // Set stage.
            stage.setTitle(stageTitle);
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            String loginUsername = gitLoginController.getUsername();
            String loginPassword = gitLoginController.getPassword();

            if (!loginUsername.equals("") && !loginPassword.equals("")) {

                customGitObject.setUsername(loginUsername);
                customGitObject.setPassword(loginPassword);
            }


        } catch (Exception e) {

            log.error("Invalid GitLogin scene.");
        }
    }

    private void getBranchTrackingStatus() {

        try {

            Git git = customGitObject.getGit();

            List<Ref> call = git.branchList().call();
            for (Ref ref : call) {

                if (ref.getName().contains(customGitObject.getBranchName())) {

                    List<Integer> counts = getBranchTrackingCount(git.getRepository(), ref.getName());

                    if (counts != null) {

                        commitsAhead = counts.get(0);
                        commitsBehind = counts.get(1);
                        lblCommitsAhead.setText(commitsAhead + " " + defaultTextLblCommitAhead);
                        lblCommitsBehind.setText(commitsBehind + " " + defaultTextLblCommitBehind);
                    }

                    return;
                }
            }

        } catch (Exception e) {

            log.warn("Error while getting branch tracking status.");
        }
    }

    private List<Integer> getBranchTrackingCount(Repository repository, String branchName) {

        try {

            BranchTrackingStatus trackingStatus = BranchTrackingStatus.of(repository, branchName);
            List<Integer> counts = new ArrayList<>();
            if (trackingStatus != null) {
                counts.add(trackingStatus.getAheadCount());
                counts.add(trackingStatus.getBehindCount());
            } else {
                System.out.println("Returned null, likely no remote tracking of branch " + branchName);
                counts.add(0);
                counts.add(0);
            }
            return counts;

        } catch (Exception e) {

            log.warn("Error while getting count in branch tracking.");
            return null;
        }
    }

    public void setCustomGitObject(CustomGitObject customGitObject) {

        this.customGitObject = customGitObject;

        lblBranchName.setText(customGitObject.getBranchName());

        lblRepositoryName.setText(customGitObject.getRepositoryName());

        btnCommit.setText(btnCommit.getText() + " " + customGitObject.getBranchName());

        getDifferences();

        defaultTextLblCommitAhead = lblCommitsAhead.getText();
        defaultTextLblCommitBehind = lblCommitsBehind.getText();
        defaultTextLblFetch = lblFetched.getText();

        doFetch();
    }

    public boolean isSuccessfullyPulled() {

        return successfullyPulled;
    }
}
