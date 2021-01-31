package cz.zcu.kiv.spac.markdown;

import cz.zcu.kiv.spac.data.Constants;
import cz.zcu.kiv.spac.data.antipattern.Antipattern;
import cz.zcu.kiv.spac.data.antipattern.AntipatternRelation;
import cz.zcu.kiv.spac.data.antipattern.heading.AntipatternHeading;
import cz.zcu.kiv.spac.data.antipattern.heading.AntipatternTableHeading;
import cz.zcu.kiv.spac.data.antipattern.heading.AntipatternTextHeading;
import cz.zcu.kiv.spac.data.catalogue.Catalogue;
import cz.zcu.kiv.spac.data.catalogue.CatalogueRecord;
import cz.zcu.kiv.spac.enums.AntipatternHeadingType;
import cz.zcu.kiv.spac.data.template.TableColumnField;
import cz.zcu.kiv.spac.data.template.TableField;
import cz.zcu.kiv.spac.data.template.TemplateField;
import cz.zcu.kiv.spac.utils.Utils;
import org.apache.commons.io.FilenameUtils;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Markdown formatter.
 */
public class MarkdownFormatter {

    /**
     * Format markdown table.
     * Some antipatterns contains table with column specification.
     * But in some files, column specification is provided only with 2x '-', like "|--|--|".
     * But markdown formatter we used in app needs at least 3x '-' to create HTML table from markdown table.
     * @param markdownContent - Markdown content.
     * @return Formatted markdown table.
     */
    public static String formatMarkdownTable(String markdownContent) {

        // Regex string.
        String patternString = "^(\\|--)+\\|$";

        Pattern pattern = Pattern.compile(patternString);

        StringBuilder newString = new StringBuilder();

        // Iterate through every line in markdown.
        for (String contentLine : markdownContent.split(Constants.LINE_BREAKER_CRLF)) {
            Matcher matcher = pattern.matcher(contentLine);

            // If lines contains markdown table column specification, then format it.
            if (matcher.find()) {

                StringBuilder newParts = new StringBuilder();

                // Go through every block (|--|--| -> |, --|, --|
                for (int i = 0; i < matcher.groupCount(); i++) {

                    // Get matched line.
                    String tableColumnDefine = matcher.group(i);

                    // Split matched line with pipeline, to get each block.
                    String[] parts = tableColumnDefine.split("\\|");

                    // Foreach every block.
                    for (String part : parts) {

                        // If block equals "--", then add one more "-".
                        // It is neccessary to have at least 3x "-" in table definition,
                        // Because flexmark do not recognize 2x "-" as a table definition.
                        if (part.equals("--")) {

                            part += "-";
                        }

                        newParts.append(part).append("|");
                    }
                }

                newString.append(newParts);
                newString.append(Constants.LINE_BREAKER_CRLF);
                continue;
            }

            newString.append(contentLine);
            newString.append(Constants.LINE_BREAKER_CRLF);
        }

        return newString.toString();
    }

    /**
     * Create antipattern markdown content from Form in antipattern window.
     * @param headings - Headings contains field definition and text.
     * @param fieldList - Template field list.
     * @return Markdown content for antipattern.
     */
    public static String createAntipatternMarkdownContent(Map<String, AntipatternHeading> headings, List<TemplateField> fieldList) {

        // TODO: ADDITIONAL: Tasklist not implemented yet.
        StringBuilder sb = new StringBuilder();

        // Add path to antipattern name.
        sb.append("[Home](" + Constants.README_NAME + ") > [Catalogue](" + Constants.CATALOGUE_NAME + ") > ");

        boolean nameWrited = false;

        int i = 0;

        // Iterate through every template field to extract value for every field.
        for (TemplateField field : fieldList) {

            AntipatternHeading antipatternHeading = headings.get(field.getName());

            // First, we need to write name, which is every time on first position.
            if (!nameWrited) {

                AntipatternTextHeading textHeading = (AntipatternTextHeading) antipatternHeading;

                // Add antipatern name to path.
                sb.append(textHeading.getValue());
                sb.append(Constants.LINE_BREAKER_CRLF);
                sb.append(Constants.LINE_BREAKER_CRLF);
                sb.append(Constants.LINE_BREAKER_CRLF);

                nameWrited = true;

                // Antipattern name.
                sb.append("# ");
                sb.append(textHeading.getValue());

            } else {

                sb.append("## ");
                sb.append(field.getText());

                if (!field.isRequired()) {
                    sb.append(Constants.TEMPLATE_FIELD_OPTIONAL_STRING);
                }

                sb.append(Constants.LINE_BREAKER_CRLF);
                sb.append(Constants.LINE_BREAKER_CRLF);

                // Check textarea and textfield.
                if (antipatternHeading.getType() == AntipatternHeadingType.TEXT) {

                    AntipatternTextHeading textHeading = (AntipatternTextHeading) antipatternHeading;
                    sb.append(textHeading.getValue());

                } else if (antipatternHeading.getType() == AntipatternHeadingType.TABLE) {

                    AntipatternTableHeading tableHeading = (AntipatternTableHeading) antipatternHeading;
                    TableField tableField = (TableField) field;

                    sb.append(createTableHeaderMarkdownContent(tableField));

                    // TODO: ADDITIONAL: Create link for source if exists.
                    // TODO: ADDITIONAL: Link two antipatterns / sources if exists in bibtex.
                    for(AntipatternRelation relation : tableHeading.getRelations()) {

                        sb.append("|").append(relation.getAntipattern()).append("|").append(relation.getRelation());
                        sb.append(Constants.LINE_BREAKER_CRLF);
                    }
                }
            }

            if (i < fieldList.size() - 1) {

                sb.append(Constants.LINE_BREAKER_CRLF);
                sb.append(Constants.LINE_BREAKER_CRLF);
            }

            i++;
        }

        return sb.toString();
    }

    /**
     * Create markdown table header.
     * @param tableField - Table field.
     * @return Table header in markdown.
     */
    private static String createTableHeaderMarkdownContent(TableField tableField) {

        StringBuilder sb = new StringBuilder();

        sb.append("|");

        // Append column names.
        for (TableColumnField tableColumnField : tableField.getColumns()) {

            sb.append(tableColumnField.getText()).append("|");
        }

        sb.append(Constants.LINE_BREAKER_CRLF);
        sb.append("|");

        // Append column separators.
        for (int j = 0; j < tableField.getColumns().size(); j++) {

            sb.append("---|");
        }

        sb.append(Constants.LINE_BREAKER_CRLF);

        return sb.toString();
    }

    /**
     * Create markdown catalogue content.
     * @param catalogue - Catalogue.
     * @param antipatterns - Antipatterns
     * @return Markdown content for catalogue.
     */
    public static String createCatalogueMarkdownContent(Catalogue catalogue, Map<String, Antipattern> antipatterns) {

        StringBuilder sb = new StringBuilder();

        Map<String, List<CatalogueRecord>> catalogueRecordMap = catalogue.getCatalogueRecords();

        sb.append("[Home](").append(Utils.getFilenameFromStringPath(Constants.README_NAME)).append(") > Catalogue");
        sb.append(Constants.LINE_BREAKER_CRLF);

        sb.append("# " + Constants.APP_NAME);
        sb.append(Constants.LINE_BREAKER_CRLF);
        sb.append(Constants.LINE_BREAKER_CRLF);

        sb.append("[Template](" + Constants.CATALOGUE_FOLDER + "/" + Constants.TEMPLATE_FILE + ") for new anti-pattern contents.");
        sb.append(Constants.LINE_BREAKER_CRLF);
        sb.append(Constants.LINE_BREAKER_CRLF);
        sb.append(Constants.LINE_BREAKER_CRLF);

        for (String key : catalogueRecordMap.keySet()) {

            List<CatalogueRecord> recordList = catalogueRecordMap.get(key);

            sb.append("## ").append(key);
            sb.append(Constants.LINE_BREAKER_CRLF);

            for (CatalogueRecord record : recordList) {

                if (record.getPath().equals("")) {

                    sb.append(record.getAntipatternName());

                } else {

                    String formattedName = Utils.formatAntipatternName(record.getAntipatternName());
                    Antipattern antipattern = antipatterns.get(formattedName);

                    if (antipattern != null && antipattern.isLinking()) {

                        String linkedAntipatternName = Utils.getFilenameFromStringPath(antipattern.getPath());
                        linkedAntipatternName = FilenameUtils.removeExtension(linkedAntipatternName);
                        linkedAntipatternName = Utils.formatAntipatternName(linkedAntipatternName);

                        Antipattern linkedAntipattern = antipatterns.get(linkedAntipatternName);
                        sb.append(record.getAntipatternName()).append(" - _see [").append(linkedAntipattern.getName()).append("](").append(antipattern.getPath()).append(")_");

                    } else {

                        sb.append("[").append(record.getAntipatternName()).append("](").append(record.getPath()).append(")");
                    }
                }

                sb.append(Constants.LINE_BREAKER_CRLF);
                sb.append(Constants.LINE_BREAKER_CRLF);
            }

        }

        return sb.toString();
    }

    /**
     * Get markdown content for nonexisting antipattern.
     * @param antipatternName - Name of nonexisting antipattern.
     * @return Markdown content.
     */
    public static String getNonExistingAntipatternContent(String antipatternName) {

        StringBuilder sb = new StringBuilder();

        sb.append("# " + antipatternName);

        sb.append(Constants.LINE_BREAKER_CRLF);
        sb.append(Constants.LINE_BREAKER_CRLF);

        sb.append("This antipattern was not yet created, only mentioned in antipattern catalogue.");

        return sb.toString();
    }
}
