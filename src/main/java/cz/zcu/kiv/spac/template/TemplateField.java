package cz.zcu.kiv.spac.template;

import cz.zcu.kiv.spac.enums.FieldType;

public class TemplateField {

    private String name;
    private String text;
    private FieldType type;
    private boolean required;

    public TemplateField(String name, String text, FieldType type, boolean required) {

        this.name = name;
        this.text = text;
        this.type = type;
        this.required = required;
    }

    public boolean isRequired() {

        return required;
    }

    public FieldType getType() {

        return type;
    }

    public String getName() {

        return name;
    }

    public String getText() {

        return text;
    }
}
