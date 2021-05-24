package cz.zcu.kiv.spac.data.reference;

/**
 * CLass representing single reference.
 */
public class Reference {

    private String shortcut;
    private String title;
    private String author;

    /**
     * Constructor.
     */
    public Reference() {

        this.shortcut = "";
        this.title = "";
        this.author = "";
    }

    /**
     * Constructor.
     * @param shortcut - Shortcut of reference.
     * @param title - Title of reference.
     * @param author - Author of reference.
     */
    public Reference(String shortcut, String title, String author) {

        this.shortcut = shortcut;
        this.title = title;
        this.author = author;
    }

    public String getShortcut() {

        return shortcut;
    }

    public String getTitle() {

        return title;
    }

    public String getAuthor() {

        return author;
    }

    public void setShortcut(String shortcut) {

        this.shortcut = shortcut;
    }

    public void setTitle(String title) {

        this.title = title;
    }

    public void setAuthor(String author) {

        this.author = author;
    }
}
