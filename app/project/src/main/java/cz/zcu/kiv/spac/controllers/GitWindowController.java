package cz.zcu.kiv.spac.controllers;

import cz.zcu.kiv.spac.components.ListViewItemWithStringAndCheckBox;
import cz.zcu.kiv.spac.data.Constants;
import cz.zcu.kiv.spac.data.git.CommitType;
import cz.zcu.kiv.spac.data.git.CustomGitObject;
import cz.zcu.kiv.spac.data.git.PreviewFileContentLine;
import cz.zcu.kiv.spac.data.git.PreviewFileContentLineType;
import cz.zcu.kiv.spac.file.FileLoader;
import cz.zcu.kiv.spac.html.HTMLGenerator;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.diff.*;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

import java.util.*;
import java.util.stream.Collectors;

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

    private HashMap<String, List<PreviewFileContentLine>> changedFiles;

    private boolean successfullyPulled = false;


    // Logger.
    private static Logger log = LogManager.getLogger(GitWindowController.class);

    /**
     * Controller.
     */
    public GitWindowController() {

        changedFiles = new HashMap<>();
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

        wviewChanges.getEngine().setUserStyleSheetLocation(getClass().getResource(Constants.RESOURCE_GIT_PREVIEW_CSS).toString());
    }

    /**
     * Commit button action.
     * @param actionEvent - Action event.
     */
    @FXML
    private void btnCommitAction(ActionEvent actionEvent) {

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

            Utils.showAlertWindow(Alert.AlertType.WARNING, Constants.APP_NAME,
                    "Creating commit",
                    "No files were selected for commit.");

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

    /**
     * Preview selected file.
     * @param mouseEvent - Event.
     */
    @FXML
    private void previewFile(MouseEvent mouseEvent) {

        Object item = listViewFileChanged.getSelectionModel().getSelectedItem();

        if (item == null) {

            mouseEvent.consume();
            return;
        }

        ListViewItemWithStringAndCheckBox selectedItem = (ListViewItemWithStringAndCheckBox) item;

        wviewChanges.getEngine().loadContent(HTMLGenerator.createHTMLFileContent(changedFiles.get(selectedItem.getFilename())));
    }

    /**
     * Do PUSH command.
     */
    @FXML
    private void doPush() {

        if (commitsAhead == 0) {

            // Create an alert.

            Utils.showAlertWindow(Alert.AlertType.WARNING, Constants.APP_NAME,
                    "Push to git",
                    "There are no commits to push.");

            return;
        }

        try {

            // push to remote:
            PushCommand pushCommand = customGitObject.getGit().push();

            pushCommand.setCredentialsProvider(new UsernamePasswordCredentialsProvider(customGitObject.getPersonalAccessToken(), ""));
            // you can add more settings here if needed
            pushCommand.call();

            log.info("Push command completed successfully.");

            // Create an alert.

            Utils.showAlertWindow(Alert.AlertType.INFORMATION, Constants.APP_NAME,
                    "Push to git",
                    "Pushing commits to git was successful.");

            getBranchTrackingStatus();

        } catch (Exception e) {

            log.warn("Invalid personal access token!");

            Utils.showAlertWindow(Alert.AlertType.ERROR, Constants.APP_NAME,
                    "Push to git",
                    "Invalid personal access token!");

            openGitLoginWindow();
        }
    }

    /**
     * Do FETCH command.
     * @param actionEvent - Event.
     */
    @FXML
    private void doFetch(ActionEvent actionEvent) {

        doFetch();
    }

    /**
     * Do PULL command.
     * @param actionEvent - Event.
     */
    @FXML
    private void doPull(ActionEvent actionEvent) {

        if (commitsBehind == 0) {

            // Create an alert.

            Utils.showAlertWindow(Alert.AlertType.WARNING, Constants.APP_NAME,
                    "Pull from git",
                    "There are no commits to pull.");
            return;
        }

        try {

            Git git = customGitObject.getGit();
            Repository repository = git.getRepository();

            ObjectId oldHead = repository.resolve("HEAD^{tree}");

            PullCommand pullCommand = customGitObject.getGit().pull();
            pullCommand.setCredentialsProvider(new UsernamePasswordCredentialsProvider(customGitObject.getPersonalAccessToken(), ""));
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

            Utils.showAlertWindow(Alert.AlertType.INFORMATION, Constants.APP_NAME,
                    "Pull from git",
                    content);

            getBranchTrackingStatus();

            successfullyPulled = true;

        } catch (CheckoutConflictException ee) {

            log.warn("Checkout conflict exception!");
            log.warn(ee.getMessage());

            Utils.showAlertWindow(Alert.AlertType.ERROR, Constants.APP_NAME,
                    "Pull from git",
                    ee.getMessage());

        } catch (Exception e) {

            log.warn("Invalid credentials!");

            Utils.showAlertWindow(Alert.AlertType.ERROR, Constants.APP_NAME,
                    "Pull from git",
                    "Invalid credentials!");

            openGitLoginWindow();
        }
    }

    /**
     * Select all changed files in listview.
     * @param actionEvent - Event.
     */
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

    /**
     * Do FETCH command.
     */
    private void doFetch() {

        try {

            customGitObject.getGit().fetch().call();
            lblFetched.setText(defaultTextLblFetch + Utils.getCurrentDateInString());
            getBranchTrackingStatus();

        } catch (Exception e) {

            Utils.showAlertWindow(Alert.AlertType.ERROR, Constants.APP_NAME,
                    "Git fetch",
                    "Undefined error while fetching.");

            log.warn("Error while fetching.");
        }
    }

    /**
     * Get differences from STATUS command and display it in list of changes.
     */
    private void getDifferences() {

        try {

            listViewFileChanged.getItems().clear();
            Git git = customGitObject.getGit();

            Status status = git.status().call();

            Set<String> added = status.getAdded();
            Set<String> untracked = status.getUntracked();
            Set<String> modified = status.getModified();
            Set<String> removed = status.getRemoved();

            Set<String> setIntersection = getIntersection(added, modified, removed);

            for(String add : added) {

                if (!setIntersection.contains(Utils.getFilenameFromStringPath(add))) {

                    listViewFileChanged.getItems().add(new ListViewItemWithStringAndCheckBox(add, false, CommitType.ADD));

                } else {

                    listViewFileChanged.getItems().add(new ListViewItemWithStringAndCheckBox(add, false, CommitType.RENAMED));
                }
            }

            for(String untrack : untracked) {

                listViewFileChanged.getItems().add(new ListViewItemWithStringAndCheckBox(untrack, false, CommitType.ADD));
            }

            for(String modify : modified) {

                if (!setIntersection.contains(Utils.getFilenameFromStringPath(modify))) {

                    listViewFileChanged.getItems().add(new ListViewItemWithStringAndCheckBox(modify, false, CommitType.MODIFY));
                }
            }

            for(String remove : removed) {

                if (!setIntersection.contains(Utils.getFilenameFromStringPath(remove))) {

                    listViewFileChanged.getItems().add(new ListViewItemWithStringAndCheckBox(remove, false, CommitType.REMOVE));
                }
            }

            lblChangedFiles.setText(listViewFileChanged.getItems().size() + " changed files");

        } catch (Exception e) {
            e.printStackTrace();
            log.warn("Error while getting differences " + e.getMessage() + ".");
        }
    }

    /**
     * Get intersection of added / modified / deleted set of changes.
     * @param added - Set of added files.
     * @param modified - Set of modified files.
     * @param removed - Set of deleted files.
     * @return Intersection set of all sets.
     */
    private Set<String> getIntersection(Set<String> added, Set<String> modified, Set<String> removed) {

        // Get intersection (renamed values are in added + modified + deleted sets).
        Set<String> setIntersection = added.stream()
                .distinct()
                .filter(modified::contains)
                .collect(Collectors.toSet());

        Set<String> setIntersectionOnlyFilename = new HashSet<>();

        for (String intersectionFilename : setIntersection) {

            setIntersectionOnlyFilename.add(Utils.getFilenameFromStringPath(intersectionFilename));
        }

        setIntersection.clear();
        for (String remove : removed) {

            String removeFilename = Utils.getFilenameFromStringPath(remove);
            if (setIntersectionOnlyFilename.contains(removeFilename)) {

                setIntersection.add(removeFilename);
            }
        }

        return setIntersection;
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
            gitLoginController.setPersonalAccessToken(customGitObject.getPersonalAccessToken());

            // Set stage.
            stage.setTitle(stageTitle);
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            String loginPersonalAccessToken = gitLoginController.getPersonalAccessToken();

            if (!loginPersonalAccessToken.equals("")) {

                customGitObject.setPersonalAccessToken(loginPersonalAccessToken);
            }


        } catch (Exception e) {

            log.error("Invalid GitLogin scene.");
        }
    }

    /**
     * Get number of commits ahead / behind of selected branch and display it.
     */
    private void getBranchTrackingStatus() {

        try {

            Git git = customGitObject.getGit();

            List<Ref> call = git.branchList().call();
            for (Ref ref : call) {

                if (ref.getName().contains(customGitObject.getBranchName())) {

                    List<Integer> counts = customGitObject.getBranchTrackingCount();

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

    /**
     * Compare updated files with files from latest commit and assign color to lines (GREEN - Added line, RED - deleted line).
     */
    private void compareUpdatedFiles() {

        // Get map of files from latest commit.
        Map<String, String> filesFromPreviousCommit = customGitObject.loadFilesFromPreviousCommit();

        // Get list of updated files.
        ObservableList updatedFiles = listViewFileChanged.getItems();

        if (updatedFiles == null) {

            return;
        }

        for (Object file : updatedFiles) {

            ListViewItemWithStringAndCheckBox updatedFile = (ListViewItemWithStringAndCheckBox) file;
            String fullPathFilename = updatedFile.getFilename();
            String filename = Utils.getFilenameFromStringPath(fullPathFilename);

            if (updatedFile.getType() == CommitType.ADD) {

                // File was added.
                // Get new lines from disk.
                try {

                    String contentPath = Utils.getAntipatternFolderPath()  +  fullPathFilename;
                    String content = FileLoader.loadFileContent(contentPath);
                    List<String> lines = Utils.parseStringByLines(Utils.getFilesDifference("", content));

                    changedFiles.put(fullPathFilename, createPreviewLinesWithOneType(lines, PreviewFileContentLineType.ADDED));

                } catch (Exception e) {

                    // Do nothing.
                }

            } else if (updatedFile.getType() == CommitType.REMOVE) {

                // File was deleted.
                // Get lines from latest commit.
                try {

                    String content = filesFromPreviousCommit.get(filename);

                    if (content == null) {

                        log.error("Error while comparing updated files (deleting).");
                    }

                    List<String> lines = Utils.parseStringByLines(Utils.getFilesDifference(content, ""));

                    changedFiles.put(fullPathFilename, createPreviewLinesWithOneType(lines, PreviewFileContentLineType.DELETED));

                } catch (Exception e) {

                    // Do nothing.
                }

            } else {

                String newContentPath = Utils.getAntipatternFolderPath() + fullPathFilename;
                // Get contents.
                String oldContent = filesFromPreviousCommit.get(filename);
                String newContent = FileLoader.loadFileContent(newContentPath);

                // Get different lines.
                List<String> lines = Utils.parseStringByLines(Utils.getFilesDifference(oldContent, newContent));
                List<PreviewFileContentLine> contentLines = new ArrayList<>();

                int infoOldIndex = 0;
                int infoNewIndex = 0;

                // Iterate through every diff line and assign his line index and type.
                for (String line : lines) {

                    PreviewFileContentLineType type;

                    int lineIndex = 0;

                    switch (line.charAt(0)) {

                        case '-':

                            type = PreviewFileContentLineType.DELETED;
                            lineIndex = infoOldIndex;
                            infoOldIndex++;
                            break;

                        case '+':

                            type = PreviewFileContentLineType.ADDED;
                            lineIndex = infoNewIndex;
                            infoNewIndex++;
                            break;

                        case ' ':

                            type = PreviewFileContentLineType.NOT_MODIFIED;
                            lineIndex = infoNewIndex;
                            infoOldIndex++;
                            infoNewIndex++;
                            break;

                        default:

                            type = PreviewFileContentLineType.DIFF_INFO;

                            // If current line is diff info.
                            if (line.contains("@")) {

                                List<String> indexes = getIndexesFromDiff(line);

                                // Do Math.abs, because old file index contains '-'.
                                infoOldIndex = Math.abs(Integer.parseInt(indexes.get(0)));
                                infoNewIndex = Integer.parseInt(indexes.get(1));
                            }
                            break;
                    }
                    contentLines.add(new PreviewFileContentLine(line, type, lineIndex));
                }

                changedFiles.put(fullPathFilename, contentLines);
            }
        }
    }

    /**
     * Create content line (line + type of line (ADDED, DELETED)).
     * @param lines - Content in lines.
     * @param type - Type of lines.
     * @return Content lines.
     */
    private List<PreviewFileContentLine> createPreviewLinesWithOneType(List<String> lines, PreviewFileContentLineType type) {

        long i = 1;
        List<PreviewFileContentLine> contentLines = new ArrayList<>();

        for (String line : lines) {

            if (line.matches("^(@@)[^@]*(@@)$")) {

                contentLines.add(new PreviewFileContentLine(line, PreviewFileContentLineType.DIFF_INFO, 0));
                continue;
            }

            contentLines.add(new PreviewFileContentLine(line, type, i));
            i++;
        }

        return contentLines;
    }

    /**
     * Set custom git object + additional informations into labels (branch name, repository name, ...).
     * @param customGitObject
     */
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

        compareUpdatedFiles();

        System.out.println();
    }

    /**
     * Get indexes from diff string.
     * @param diffString - Diff string.
     * @return List of indexes.
     */
    private static List<String> getIndexesFromDiff(String diffString) {

        // Format: @@ -61,11 +66,33 @@

        String[] diffStringArray = diffString.split(" ");

        String infoOld = diffStringArray[1];
        String infoNew = diffStringArray[2];

        String[] infoOldIndex = infoOld.split(",");
        String[] infoNewIndex = infoNew.split(",");

        List<String> indexes = new ArrayList<>();

        indexes.add(infoOldIndex[0]);
        indexes.add(infoNewIndex[0]);

        return indexes;
    }

    public boolean isSuccessfullyPulled() {

        return successfullyPulled;
    }
}
