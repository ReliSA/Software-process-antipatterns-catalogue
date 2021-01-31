package cz.zcu.kiv.spac.data.template;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing template field, specifically for tables.
 */
public class TableField extends TemplateField {

    private List<TableColumnField> columns;

    /**
     * Constructor.
     * @param name - Field name.
     * @param text - Field text.
     * @param type - Field type.
     * @param required - True if field is required, false if not.
     */
    public TableField(String name, String text, TemplateFieldType type, boolean required) {

        super(name, text, type, required, "", "");
        columns = new ArrayList<>();
    }

    public void addColumn(TableColumnField column) {

        this.columns.add(column);
    }

    public List<TableColumnField> getColumns() {

        return this.columns;
    }

}
