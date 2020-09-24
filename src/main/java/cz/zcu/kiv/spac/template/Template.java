package cz.zcu.kiv.spac.template;

import cz.zcu.kiv.spac.enums.FieldType;

import java.util.ArrayList;
import java.util.List;

public class Template {

    private List<TemplateField> fieldList;

    public Template(List<TemplateField> fieldList) {

        this.fieldList = fieldList;
    }

    public List<TemplateField> getFieldList() {

        return fieldList;
    }

    public List<String> getFieldNameList() {

        List<String> fieldNameList = new ArrayList<>();

        for (TemplateField field : fieldList) {

            fieldNameList.add(field.getName());
        }

        return fieldNameList;
    }

    public List<String> getFieldTextList() {

        List<String> fieldNameList = new ArrayList<>();

        for (TemplateField field : fieldList) {

            fieldNameList.add(field.getText());
        }

        return fieldNameList;
    }

    public FieldType getFieldType(String fieldName) {

        for (TemplateField field : fieldList) {

            if (field.getName().equals(fieldName)) {

                return field.getType();
            }
        }

        return null;
    }

    public TemplateField getField(String fieldName) {

        for (TemplateField field : fieldList) {

            if (field.getName().equals(fieldName)) {

                return field;
            }
        }
        return null;
    }
}
