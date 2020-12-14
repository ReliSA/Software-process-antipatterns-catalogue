package cz.zcu.kiv.spac.data.git;

public class UpdatedFile {

    private String filePath;
    private String contentWithDifferences;

    public UpdatedFile(String filePath, String contentWithDifferences) {

        this.filePath = filePath;
        this.contentWithDifferences = contentWithDifferences;
    }

    public String getContentWithDifferences() {

        return contentWithDifferences;
    }

    public String getFilePath() {

        return filePath;
    }
}
