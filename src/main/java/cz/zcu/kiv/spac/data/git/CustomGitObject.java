package cz.zcu.kiv.spac.data.git;

import cz.zcu.kiv.spac.controllers.GitWindowController;
import cz.zcu.kiv.spac.utils.Utils;
import org.apache.log4j.Logger;
import org.eclipse.jgit.api.Git;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * CLass representing git attributes for git connection.
 */
public class CustomGitObject {

    private Git git;

    // Git attributes.
    private String branchName;
    private String repositoryUrl;
    private String repositoryName;
    private String username;
    private String password;

    // Logger.
    private static Logger log = Logger.getLogger(GitWindowController.class);

    /**
     * Constructor.
     * @param branchName - Name of git branch.
     * @param repositoryUrl - Git Repository url.
     * @param username  - Username for git.
     * @param password - Password for git.
     */
    public CustomGitObject(String branchName, String repositoryUrl, String username, String password) {

        this.branchName = branchName;
        this.repositoryUrl = repositoryUrl;
        this.username = username;
        this.password = password;

        // Get repository name.
        String[] splittedUrl = repositoryUrl.split("/");

        // First 3 strings are just parsed github url.
        // Name of organization not included.
        if (splittedUrl.length > 3) {

            repositoryName = splittedUrl[splittedUrl.length - 1];
        }

        createGitObject();
    }

    /**
     * Create git object.
     * @return True if git object was created successfully, false if not.
     */
    private void createGitObject() {

        try {

            // TODO: maybe check url repository from customGitObject and compare it with repository set in .git folder.
            git = Git.open(new File(Utils.getRootDir() + "/.git"));
            //git.checkout().setName(branchName).call();

        } catch (Exception e) {

            log.error(e.getMessage());
            git = null;
        }
    }

    public String getBranchName() {

        return branchName;
    }

    public String getRepositoryUrl() {

        return repositoryUrl;
    }

    public String getUsername() {

        return username;
    }

    public void setUsername(String username) {

        this.username = username;
    }

    public String getPassword() {

        return password;
    }

    public void setPassword(String password) {

        this.password = password;
    }

    public String getRepositoryName() {

        return repositoryName;
    }

    public Git getGit() {

        return git;
    }
}
