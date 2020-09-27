package cz.zcu.kiv.spac.template;

import cz.zcu.kiv.spac.enums.FieldType;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing template field, specifically for tables.
 */
public class TableField extends TemplateField {

    private List<String> columns;

    /**
     * Constructor.
     * @param name - Field name.
     * @param text - Field text.
     * @param type - Field type.
     * @param required - True if field is required, false if not.
     */
    public TableField(String name, String text, FieldType type, boolean required) {

        super(name, text, type, required);
        columns = new ArrayList<>();
    }

    public void addColumn(String column) {

        this.columns.add(column);
    }

    public List<String> getColumns() {

        return this.columns;
    }

}
