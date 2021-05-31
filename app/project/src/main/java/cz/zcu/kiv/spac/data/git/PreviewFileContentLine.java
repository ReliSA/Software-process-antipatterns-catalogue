package cz.zcu.kiv.spac.data.git;

/**
 * Class representing one line of file content in git window.
 */
public class PreviewFileContentLine {

    private String line;
    private PreviewFileContentLineType type;
    private long lineNumber;

    /**
     * Constructor.
     * @param line - Content line.
     * @param type - Type of line.
     */
    public PreviewFileContentLine(String line, PreviewFileContentLineType type, long lineNumber) {

        this.line = line;
        this.type = type;
        this.lineNumber = lineNumber;
    }


    public String getLine() {

        return line;
    }

    public PreviewFileContentLineType getType() {

        return type;
    }

    public long getLineNumber() {

        return lineNumber;
    }
}
