package cz.zcu.kiv.spac.controllers;

import cz.zcu.kiv.spac.components.ListViewItemWithStringAndCheckBox;
import cz.zcu.kiv.spac.data.Constants;
import cz.zcu.kiv.spac.data.git.CommitType;
import cz.zcu.kiv.spac.data.git.CustomGitObject;
import cz.zcu.kiv.spac.data.git.PreviewFileContentLine;
import cz.zcu.kiv.spac.data.git.PreviewFileContentLineType;
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
import org.eclipse.jgit.dircache.DirCacheIterator;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.FileTreeIterator;
import org.eclipse.jgit.treewalk.TreeWalk;

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
    private static Logger log = Logger.getLogger(GitWindowController.class);

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

        String content = FileLoader.loadFileContent(Utils.getRootDir() + "/" + selectedItem.getFilename());

        // TODO: test preview file
        if (selectedItem.getType() == CommitType.MODIFY) {

            wviewChanges.getEngine().loadContent(Utils.replaceLineBreakersForHTMLNewLine(content));

        } else {

            wviewChanges.getEngine().loadContent(createHTMLFileContent(changedFiles.get(selectedItem.getFilename())));
        }

        /*
        String content = FileLoader.loadFileContent(Utils.getRootDir() + "/" + selectedItem.getFilename());

        if (content != null) {

            wviewChanges.getEngine().loadContent(Utils.replaceLineBreakersForHTMLNewLine(content));

        } else {

            log.warn("File '" + selectedItem.getFilename() + "' did not exists in source control!");
        }
         */
    }

    /**
     * Do PUSH command.
     */
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

            Alert alert = new Alert(Alert.AlertType.NONE);
            alert.setTitle(Constants.APP_NAME);
            alert.setAlertType(Alert.AlertType.ERROR);
            alert.setHeaderText("Git fetch");
            alert.setContentText("Undefined error while fetching.");
            alert.showAndWait();

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

    /**
     * Get number of commits ahead / behind of selected branch and display it.
     */
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

    /**
     * Get number of commits ahead / behind of selected branch.
     * @param repository - Currently logged repository.
     * @param branchName - Name of branch.
     * @return List of number counts for commits ahead / behind.
     */
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

    /**
     * Compare updated files with files from latest commit and assign color to lines (GREEN - Added line, RED - deleted line).
     */
    private void compareUpdatedFiles() {

        // Get map of files from latest commit.
        Map<String, List<String>> filesFromPreviousCommit = loadFilesFromPreviousCommit();

        if (filesFromPreviousCommit == null) {

            return;
        }

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

                    String content = FileLoader.loadFileContent(Utils.getRootDir() + "/" + fullPathFilename);
                    List<String> lines = Utils.parseStringByLines(content);

                    changedFiles.put(fullPathFilename, createPreviewLinesWithOneType(lines, PreviewFileContentLineType.ADDED));

                } catch (Exception e) {

                    // Do nothing.
                    continue;
                }

            } else if (updatedFile.getType() == CommitType.REMOVE) {

                // File was deleted.
                // Get lines from latest commit.
                try {

                    List<String> lines = filesFromPreviousCommit.get(filename);

                    if (lines == null) {

                        log.error("Error while comparing updated files (deleting).");
                    }

                    changedFiles.put(fullPathFilename, createPreviewLinesWithOneType(lines, PreviewFileContentLineType.DELETED));

                } catch (Exception e) {

                    // Do nothing.
                    continue;
                }

            } else {

                // TODO: compare modified files.
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

            contentLines.add(new PreviewFileContentLine(line, type, i));
            i++;
        }

        return contentLines;
    }

    /**
     * Load files from previous commit.
     * @return Map of files with its content from latest commit.
     */
    private Map<String, List<String>> loadFilesFromPreviousCommit() {

        Map<String, List<String>> filesFromPreviousCommit = new HashMap<>();

        // TODO: ADDITIONAL: highlight changes.
        // Momentálně to načte last commit, ze kterýho si vyberu specifický soubor (který je smazaný) a vypíšu jeho kontent
        // -> lze použít při porovnávání řádků souboru z commitu a z aktuálního stavu a možnost highlightu
        Git git = customGitObject.getGit();
        Repository repository = git.getRepository();
        RevWalk rw = new RevWalk(repository);

        try (TreeWalk tw = new TreeWalk(repository)) {


            RevCommit latestCommit = git.log().setMaxCount(1).call().iterator().next();

            String latestCommitHash = latestCommit.getName();

            RevCommit commitToCheck = rw.parseCommit(repository.resolve(latestCommitHash));
            tw.addTree(commitToCheck.getTree());
            tw.addTree(new DirCacheIterator(repository.readDirCache()));
            tw.addTree(new FileTreeIterator(repository));
            tw.setRecursive(true);

            while (tw.next()) {

                String filename = tw.getPathString();

                // Get object ID of file from latest commit.
                ObjectId objectId;

                if (tw.getFileMode(0).getBits() != 0) {

                    objectId = tw.getObjectId(0);

                } else {

                    objectId = tw.getObjectId(1);
                }

                // Get content of file from latest commit.

                /*
                System.out.printf(
                        "path: %s, Commit(mode/oid): %s/%s, Index(mode/oid): %s/%s, Workingtree(mode/oid): %s/%s\n",
                        tw.getPathString(), tw.getFileMode(0), tw.getObjectId(0), tw.getFileMode(1), tw.getObjectId(1),
                        tw.getFileMode(2), tw.getObjectId(2));
                 */

                // Commit(mode/oid): 0/AnyObjectId[0000000000000000000000000000000000000000],

                byte[] bytes = repository.open(objectId).getBytes();
                String content = new String(bytes);

                filesFromPreviousCommit.put(Utils.getFilenameFromStringPath(filename), Utils.parseStringByLines(content));
            }

        } catch (Exception e) {

            // TODO: what if there are no commits (newly created branch) ?
            e.printStackTrace();
        }

        return filesFromPreviousCommit;
    }

    /**
     * Create HTML content from file content lines.
     * @param contentLines - File content lines.
     * @return HTML file content.
     */
    public static String createHTMLFileContent(List<PreviewFileContentLine> contentLines) {

        StringBuilder htmlContent = new StringBuilder();
        htmlContent.append("<table>");

        if (contentLines != null) {

            for (PreviewFileContentLine contentLine : contentLines) {

                String line = "&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp";
                for (char c : contentLine.getLine().toCharArray()) {

                    if (c != ' ') {

                        break;
                    }

                    line += "&nbsp";
                }

                line += contentLine.getLine();

                htmlContent.append("<tr>");

                switch (contentLine.getType()) {

                    case ADDED:

                        htmlContent.append("<td class=\"line-index line line-" + contentLine.getType().name().toLowerCase() + "\">" + contentLine.getLineNumber() + "</td>");
                        htmlContent.append("<td class=\"line-index line line-" + contentLine.getType().name().toLowerCase() + "\"></td>");
                        break;
                    case DELETED:

                        htmlContent.append("<td class=\"line-index line line-" + contentLine.getType().name().toLowerCase() + "\"></td>");
                        htmlContent.append("<td class=\"line-index line line-" + contentLine.getType().name().toLowerCase() + "\">" + contentLine.getLineNumber() + "</td>");
                        break;
                }

                htmlContent.append("<td class=\"line line-" + contentLine.getType().name().toLowerCase() + "\">" + line + "</td>");
                htmlContent.append("</tr>");
            }
            htmlContent.append("</table>");

        } else {

            htmlContent.append("<h1>No content for preview</h1>");
        }

        return htmlContent.toString();
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

    public boolean isSuccessfullyPulled() {

        return successfullyPulled;
    }
}
