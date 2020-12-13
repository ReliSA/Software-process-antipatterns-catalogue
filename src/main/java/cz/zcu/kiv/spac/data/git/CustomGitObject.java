package cz.zcu.kiv.spac.git;

/**
 * CLass representing git attributes for git connection.
 */
public class CustomGitObject {


    // Git attributes.
    private String branchName;
    private String repositoryUrl;
    private String repositoryName;
    private String username;
    private String password;

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
}
