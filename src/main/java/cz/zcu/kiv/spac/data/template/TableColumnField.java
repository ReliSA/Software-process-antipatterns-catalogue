package cz.zcu.kiv.spac.template;

/**
 * Class representing table column for table in configuration.
 */
public class TableColumnField {

    private String text;
    private String defaultValue;

    /**
     * Constructor.
     * @param text - Table column displayed text.
     * @param defaultValue - Default column value.
     */
    public TableColumnField(String text, String defaultValue) {

        this.text = text;
        this.defaultValue = defaultValue;
    }

    public String getText() {

        return text;
    }

    public String getDefaultValue() {

        return defaultValue;
    }
}
