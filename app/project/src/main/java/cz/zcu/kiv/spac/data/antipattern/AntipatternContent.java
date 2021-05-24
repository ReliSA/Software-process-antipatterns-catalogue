package cz.zcu.kiv.spac.data.antipattern;

/**
 * Antipattern content.
 */
public class AntipatternContent {

    private String content;

    /**
     * Constructor.
     * @param content - Antipattern content in string.
     */
    public AntipatternContent(String content) {

        this.content = content;
    }

    public void setContent(String content) {

        this.content = content;
    }

    @Override
    public String toString() {

        return content;
    }
}
