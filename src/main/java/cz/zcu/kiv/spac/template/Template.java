package cz.zcu.kiv.spac.template;

import cz.zcu.kiv.spac.enums.FieldType;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing template.
 */
public class Template {

    private List<TemplateField> fieldList;

    /**
     * Constructor.
     * @param fieldList - List of template fields.
     */
    public Template(List<TemplateField> fieldList) {

        this.fieldList = fieldList;
    }

    public List<TemplateField> getFieldList() {

        return fieldList;
    }

    /**
     * Get list of field names (related, sources, ...).
     * @return List of field names.
     */
    public List<String> getFieldNameList() {

        List<String> fieldNameList = new ArrayList<>();

        for (TemplateField field : fieldList) {

            fieldNameList.add(field.getName());
        }

        return fieldNameList;
    }

    /**
     * Get list of field texts (Related Anti-patterns, Sources, ...).
     * @return list of field texts.
     */
    public List<String> getFieldTextList() {

        List<String> fieldNameList = new ArrayList<>();

        for (TemplateField field : fieldList) {

            fieldNameList.add(field.getText());
        }

        return fieldNameList;
    }

    /**
     * Get field type of field.
     * @param fieldName - Name of field.
     * @return Field type.
     */
    public FieldType getFieldType(String fieldName) {

        for (TemplateField field : fieldList) {

            if (field.getName().equals(fieldName)) {

                return field.getType();
            }
        }

        return null;
    }

    /**
     * Get field by field name.
     * @param fieldName - Field name.
     * @return Field.
     */
    public TemplateField getField(String fieldName) {

        for (TemplateField field : fieldList) {

            if (field.getName().equals(fieldName)) {

                return field;
            }
        }
        return null;
    }
}
