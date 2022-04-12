package cz.zcu.kiv.spac.data.template;

import javafx.beans.property.*;

/**
 * Class representing template field.
 */
public class TemplateField {

    private StringProperty name;
    private StringProperty text;
    private ObjectProperty<TemplateFieldType> type;
    private BooleanProperty required;
    private StringProperty defaultValue;
    private StringProperty placeholder;

    /**
     * Constructor.
     * @param name - Field name.
     * @param text - Field text.
     * @param type - Field type.
     * @param required - True if field is required, false if not.
     * @param defaultValue - Field default value.
     * @param placeholder - Placeholder for field.
     */
    public TemplateField(String name, String text, TemplateFieldType type, boolean required, String defaultValue, String placeholder) {

        this.name = new SimpleStringProperty(name);
        this.text = new SimpleStringProperty(text);
        this.type = new SimpleObjectProperty<>(type);
        this.required = new SimpleBooleanProperty(required);
        this.defaultValue = new SimpleStringProperty(defaultValue);
        this.placeholder = new SimpleStringProperty(placeholder);
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public String getText() {
        return text.get();
    }

    public void setText(String text) {
        this.text.set(text);
    }

    public StringProperty textProperty() {
        return text;
    }


    public boolean isRequired() {
        return required.get();
    }

    public void setRequired(boolean required) {
        this.required.set(required);
    }

    public BooleanProperty requiredProperty() {
        return required;
    }

    public String getDefaultValue() {
        return defaultValue.get();
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue.set(defaultValue);
    }

    public TemplateFieldType getType() {
        return type.get();
    }

    public void setType(TemplateFieldType type) {
        this.type.set(type);
    }

    public ObjectProperty<TemplateFieldType> typeProperty() {
        return type;
    }

    public StringProperty defaultValueProperty() {
        return defaultValue;
    }

    public String getPlaceholder() {
        return placeholder.get();
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder.set(placeholder);
    }

    public StringProperty placeholderProperty() {
        return placeholder;
    }
}
