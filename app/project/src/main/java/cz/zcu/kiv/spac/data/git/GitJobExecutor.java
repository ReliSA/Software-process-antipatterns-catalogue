package cz.zcu.kiv.spac.data.git;

import cz.zcu.kiv.spac.components.ListViewItemWithStringAndCheckBox;
import cz.zcu.kiv.spac.controllers.GitLoginController;
import cz.zcu.kiv.spac.data.Constants;
import cz.zcu.kiv.spac.utils.Utils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

import java.util.*;

public class GitJobExecutor {

    private static Logger log = LogManager.getLogger(GitJobExecutor.class);

    private final CustomGitObject customGitObject;

    private String defaultTextLblCommitAhead;
    private String defaultTextLblCommitBehind;
    private String defaultTextLblFetch;
    private int commitsAhead = 0;

    private int commitsBehind = 0;
    private HashMap<String, List<PreviewFileContentLine>> changedFiles;
    private boolean successfullyPulled = false;

    public GitJobExecutor(CustomGitObject git) {
        this.customGitObject = git;
    }

    public void fetch() throws GitAPIException {
        customGitObject.getGit().fetch().call();
    }

    public String pull(boolean verbose) {
        String status = "";
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

            StringBuilder content = new StringBuilder("Pulling commits from git was successful. Pulled files (" + diffs.size() + "):");

            for (DiffEntry entry : diffs) {

                content.append("\n").append(entry.getNewPath());
            }

            log.info("Pull command completed successfully.");

            if (verbose) {
                Utils.showAlertWindow(Alert.AlertType.INFORMATION, Constants.APP_NAME,
                        "Pull from git",
                        content.toString());
            }

            status = content.toString();
            updateBranchStatus();

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

        return status;
    }

    public void commit(List<String> allFiles, String summary, String description) {
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

        if (summary == null || summary.length() == 0) {

            Utils.showAlertWindow(Alert.AlertType.WARNING, Constants.APP_NAME,
                    "Creating commit",
                    "Summary is required !");

            return;
        }

        try {

            addCommand.call();
            git.commit().setMessage(summary + "\n" + description).call();
        } catch (Exception e) {

            log.warn("Failed to create commit!");
        }

        push();
    }

    public void push() {
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


        } catch (Exception e) {

            log.warn("Invalid personal access token!");

            Utils.showAlertWindow(Alert.AlertType.ERROR, Constants.APP_NAME,
                    "Push to git",
                    "Invalid personal access token!");

            openGitLoginWindow();
        }
    }

    /**
     * Get number of commits ahead / behind of selected branch and display it.
     */
    private void updateBranchStatus() {
        try {
            Git git = customGitObject.getGit();

            List<Ref> call = git.branchList().call();
            for (Ref ref : call) {

                if (ref.getName().contains(customGitObject.getBranchName())) {

                    List<Integer> counts = customGitObject.getBranchTrackingCount();

                    if (counts != null) {

                        commitsAhead = counts.get(0);
                        commitsBehind = counts.get(1);
                    }

                    return;
                }
            }

        } catch (Exception e) {

            log.warn("Error while getting branch tracking status.");
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
    public Map<String, String> loadFilesFromPreviousCommit() {
        return customGitObject.loadFilesFromPreviousCommit();
    }

    public StatusCommand getStatus() {
        Git git = customGitObject.getGit();

        return git.status();
    }

    public int getCommitsAhead() {
        return commitsAhead;
    }

    public int getCommitsBehind() {
        return commitsBehind;
    }
}
