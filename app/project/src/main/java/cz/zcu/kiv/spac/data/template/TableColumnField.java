package cz.zcu.kiv.spac.data.template;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Class representing table column for table in configuration.
 */
public class TableColumnField {

    private StringProperty text;
    private StringProperty defaultValue;

    /**
     * Constructor.
     * @param text - Table column displayed text.
     * @param defaultValue - Default column value.
     */
    public TableColumnField(String text, String defaultValue) {

        this.text = new SimpleStringProperty(text);
        this.defaultValue = new SimpleStringProperty(defaultValue);
    }

    public String getText() {
        return text.get();
    }

    public StringProperty textProperty() {
        return text;
    }

    public void setText(String text) {
        this.text.set(text);
    }

    public String getDefaultValue() {
        return defaultValue.get();
    }

    public StringProperty defaultValueProperty() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue.set(defaultValue);
    }
}
