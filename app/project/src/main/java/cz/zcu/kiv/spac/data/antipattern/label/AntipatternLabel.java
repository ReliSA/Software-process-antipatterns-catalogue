package cz.zcu.kiv.spac.data.antipattern.label;

import javafx.beans.property.*;
import javafx.scene.paint.Color;

public class AntipatternLabel {

    private StringProperty name;

    private ObjectProperty<Color> color;

    private BooleanProperty selected;

    public AntipatternLabel(String name, Color color) {
        this.name = new SimpleStringProperty(name);
        this.color = new SimpleObjectProperty<>(color);
        this.selected = new SimpleBooleanProperty(false);
    }

    public AntipatternLabel(String name, Color color, boolean selected) {
        this.name = new SimpleStringProperty(name);
        this.color = new SimpleObjectProperty<>(color);
        this.selected = new SimpleBooleanProperty(selected);
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public Color getColor() {
        return color.get();
    }

    public ObjectProperty<Color> colorProperty() {
        return color;
    }

    public void setColor(Color color) {
        this.color.set(color);
    }

    public boolean isSelected() {
        return selected.get();
    }

    public BooleanProperty selectedProperty() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected.set(selected);
    }
}
