package cz.zcu.kiv.spac.components;

import cz.zcu.kiv.spac.data.git.CommitType;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Custom ListviewItem for git window.
 */
public class ListViewItemWithStringAndCheckBox {

    private final StringProperty name = new SimpleStringProperty();
    private final BooleanProperty on = new SimpleBooleanProperty();
    private String filename;
    private CommitType type;

    /**
     * Constructor.
     * @param name - Displayed name.
     * @param on - True if selected, false if not.
     * @param type - Change type (ADDED, MODIFIED, ...)
     */
    public ListViewItemWithStringAndCheckBox(String name, boolean on, CommitType type) {

        this.filename = name;
        this.type = type;

        String typeChar = "";

        switch (type) {

            case ADD:

                typeChar = "+";
                break;

            case MODIFY:

                typeChar = "?";
                break;

            case REMOVE:

                typeChar = "x";
                break;

            case RENAMED:

                typeChar = "->";
                break;
        }

        setName("[" + typeChar + "] " + name);
        setOn(on);
    }

    public final StringProperty nameProperty() {

        return this.name;
    }

    public final String getFilename() {

        return filename;
    }

    public final String getName() {

        return this.nameProperty().get();
    }

    public final void setName(final String name) {

        this.nameProperty().set(name);
    }

    public final BooleanProperty onProperty() {

        return this.on;
    }

    public final boolean isOn() {

        return this.onProperty().get();
    }

    public final void setOn(final boolean on) {

        this.onProperty().set(on);
    }

    @Override
    public String toString() {

        return getName();
    }

    public CommitType getType() {

        return type;
    }
}
