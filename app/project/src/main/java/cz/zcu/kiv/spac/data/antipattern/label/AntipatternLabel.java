package cz.zcu.kiv.spac.data.antipattern.label;

import javafx.beans.property.*;
import javafx.scene.paint.Color;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AntipatternLabel that = (AntipatternLabel) o;
        return name.getValue().equals(that.name.getValue()) && color.getValue().equals(that.color.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, color);
    }
}
