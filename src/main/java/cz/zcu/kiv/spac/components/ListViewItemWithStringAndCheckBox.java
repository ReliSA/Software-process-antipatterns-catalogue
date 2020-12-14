package cz.zcu.kiv.spac.components;

import cz.zcu.kiv.spac.data.git.CommitType;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.eclipse.jgit.diff.DiffEntry;

public class ListViewItemWithStringAndCheckBox {

    private final StringProperty name = new SimpleStringProperty();
    private final BooleanProperty on = new SimpleBooleanProperty();
    private String filename;

    public ListViewItemWithStringAndCheckBox(String name, boolean on, CommitType type) {

        this.filename = name;

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
}
