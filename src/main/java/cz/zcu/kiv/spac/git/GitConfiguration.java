package cz.zcu.kiv.spac.git;

import cz.zcu.kiv.spac.data.Constants;

/**
 * CLass representing git configuration for git connection.
 */
public class GitConfiguration {

    private String branchName;
    private String repositoryUrl;
    private String username;

    /**
     * Constructor.
     * @param branchName - Name of git branch.
     * @param repositoryUrl - Git Repository url.
     */
    public GitConfiguration(String branchName, String repositoryUrl) {

        this.branchName = branchName;
        this.repositoryUrl = repositoryUrl;
        this.username = Constants.LBL_GIT_LOGGED_USER_DEFAULT;
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
}
