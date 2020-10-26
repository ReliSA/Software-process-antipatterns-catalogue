package cz.zcu.kiv.spac.template;

import cz.zcu.kiv.spac.enums.TemplateFieldType;

/**
 * Class representing template field.
 */
public class TemplateField {

    private String name;
    private String text;
    private TemplateFieldType type;
    private boolean required;

    /**
     * Constructor.
     * @param name - Field name.
     * @param text - Field text.
     * @param type - Field type.
     * @param required - True if field is required, false if not.
     */
    public TemplateField(String name, String text, TemplateFieldType type, boolean required) {

        this.name = name;
        this.text = text;
        this.type = type;
        this.required = required;
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
}
