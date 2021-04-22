package cz.zcu.kiv.spac.data.template;

/**
 * Class representing template field.
 */
public class TemplateField {

    private String name;
    private String text;
    private TemplateFieldType type;
    private boolean required;
    private String defaultValue;
    private String placeholder;

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

        this.name = name;
        this.text = text;
        this.type = type;
        this.required = required;
        this.defaultValue = defaultValue;
        this.placeholder = placeholder;
    }

    public boolean isRequired() {

        return required;
    }

    public TemplateFieldType getType() {

        return type;
    }

    public String getName() {

        return name;
    }

    public String getText() {

        return text;
    }

    public String getDefaultValue() {

        return defaultValue;
    }

    public String getPlaceholder() {

        return placeholder;
    }
}
