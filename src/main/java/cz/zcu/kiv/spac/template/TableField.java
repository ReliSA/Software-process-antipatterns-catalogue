package cz.zcu.kiv.spac.template;

import cz.zcu.kiv.spac.enums.FieldType;

import java.util.ArrayList;
import java.util.List;

public class TableField extends TemplateField {

    private List<String> columns;

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
