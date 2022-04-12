package cz.zcu.kiv.spac.data.template;

/**
 * Enum representing used type of fields in template.
 */
public enum TemplateFieldType {
    TEXTFIELD("Text field", "textfield"),
    TEXTAREA ("Text area", "textarea"),
    TABLE ("Table", "table"),
    SELECT ("Select", "select");

    final String name;
    final String def;

    TemplateFieldType(String name, String def) {
        this.name = name;
        this.def = def;
    }

    public String getName() {
        return name;
    }

    public String getDef() {
        return this.def;
    }
}
