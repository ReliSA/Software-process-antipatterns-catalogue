package cz.zcu.kiv.spac.data.template;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;

import java.util.List;

/**
 * Class representing template field, specifically for tables.
 */
public class TableField extends TemplateField {

    private ListProperty<TableColumnField> columns;

    /**
     * Constructor.
     * @param name - Field name.
     * @param text - Field text.
     * @param type - Field type.
     * @param required - True if field is required, false if not.
     */
    public TableField(String name, String text, TemplateFieldType type, boolean required) {

        super(name, text, type, required, "", "");
        columns = new SimpleListProperty<>(FXCollections.observableArrayList());
    }

    public List<TableColumnField> getColumns() {
        return columns.getValue();
    }

    public void setColumns(List<TableColumnField> columns) {
        this.columns.setAll(columns);
    }

    public ListProperty<TableColumnField> columnsProperty() {
        return columns;
    }

    public void addColumn(TableColumnField column) {
        this.columns.add(column);
    }
}
