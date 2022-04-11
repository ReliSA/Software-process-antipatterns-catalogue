package cz.zcu.kiv.spac.data.reference;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * CLass representing single reference.
 */
public class Reference {

    private final StringProperty shortcut = new SimpleStringProperty();
    private final StringProperty title = new SimpleStringProperty();
    private final StringProperty author = new SimpleStringProperty();
    private final BooleanProperty selected = new SimpleBooleanProperty();
    private final StringProperty url = new SimpleStringProperty();

    /**
     * Constructor.
     */
    public Reference() {
        this.shortcut.set("");
        this.title.set("");
        this.author.set("");
        this.selected.set(false);
        this.author.set("");
    }

    /**
     * Constructor.
     * @param shortcut - Shortcut of reference.
     * @param title - Title of reference.
     * @param author - Author of reference.
     */
    public Reference(String shortcut, String title, String author, boolean selected, String url) {
        this.shortcut.set(shortcut);
        this.title.set(title);
        this.author.set(author);
        this.selected.set(selected);
        this.url.set(url);
    }

    public String getShortcut() {

        return shortcut.getValue();
    }

    public String getTitle() {

        return title.getValue();
    }

    public String getAuthor() {

        return author.getValue();
    }

    public void setShortcut(String shortcut) {

        this.shortcut.set(shortcut);
    }

    public void setTitle(String title) {

        this.title.set(title);
    }

    public void setAuthor(String author) {

        this.author.set(author);
    }

    public final BooleanProperty selectedProperty() {
        return this.selected;
    }

    public boolean isSelected() {
        return this.selected.get();
    }

    public void setSelected(boolean selected) {
        this.selected.set(selected);
    }

    public String getUrl() {
        return url.get();
    }

    public StringProperty urlProperty() {
        return url;
    }

    public void setUrl(String url) {
        this.url.set(url);
    }
}
