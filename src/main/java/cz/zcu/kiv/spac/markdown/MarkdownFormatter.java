package cz.zcu.kiv.spac.markdown;

import cz.zcu.kiv.spac.data.Constants;
import cz.zcu.kiv.spac.data.catalogue.Catalogue;
import cz.zcu.kiv.spac.data.catalogue.CatalogueRecord;
import cz.zcu.kiv.spac.template.TemplateField;
import cz.zcu.kiv.spac.utils.Utils;

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
        for (String contentLine : markdownContent.split("\n")) {
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
                newString.append("\n");
                continue;
            }

            newString.append(contentLine);
        }

        return newString.toString();
    }

    /**
     * Create antipattern markdown content from Form in antipattern window.
     * @param headings - Headings contains field definition and text.
     * @param fieldList - Template field list.
     * @return Markdown content for antipattern.
     */
    public static String createMarkdownTemplateFile(Map<String, String> headings, List<TemplateField> fieldList) {

        // TODO: Tasklist + table not implemented yet.
        StringBuilder sb = new StringBuilder();

        // Add path to antipattern name.
        sb.append("[Home](" + Constants.README_NAME + ") > [Catalogue](" + Constants.CATALOGUE_NAME + ") > ");

        boolean nameWrited = false;

        int i = 0;

        // Iterate through every template field to extract value for every field.
        for (TemplateField field : fieldList) {

            // First, we need to write name, which is everytime on first position.
            if (nameWrited) {

                sb.append("## ");
                sb.append(field.getText());
                sb.append(Constants.LINE_BREAKER);
                sb.append(Constants.LINE_BREAKER);

                sb.append(headings.get(field.getName()));

            } else {

                String antipatternName = headings.get(field.getName());

                // Add antipatern name to path.
                sb.append(antipatternName);
                sb.append(Constants.LINE_BREAKER);
                sb.append(Constants.LINE_BREAKER);
                sb.append(Constants.LINE_BREAKER);

                nameWrited = true;

                sb.append("# ");
                sb.append(antipatternName);
            }

            if (i < fieldList.size() - 1) {

                sb.append(Constants.LINE_BREAKER);
                sb.append(Constants.LINE_BREAKER);
            }

            i++;
        }

        return sb.toString();
    }

    /**
     * Create markdown catalogue content.
     * @param catalogue - Catalogue.
     * @return Markdown content for catalogue.
     */
    public static String createMarkdownCatalogueFile(Catalogue catalogue) {

        StringBuilder sb = new StringBuilder();

        Map<String, List<CatalogueRecord>> catalogueRecordMap = catalogue.getCatalogueRecords();

        sb.append("[Home](" + Utils.getFilenameFromStringPath(Constants.README_NAME) + ") > Catalogue");
        sb.append(Constants.LINE_BREAKER);

        sb.append("# " + Constants.APP_NAME);
        sb.append(Constants.LINE_BREAKER);
        sb.append(Constants.LINE_BREAKER);

        // TODO: generate template from configuration ?
        sb.append("[Template](" + Constants.CATALOGUE_FOLDER + "/" + Constants.TEMPLATE_FILE + ") for new anti-pattern contents.");
        sb.append(Constants.LINE_BREAKER);
        sb.append(Constants.LINE_BREAKER);
        sb.append(Constants.LINE_BREAKER);

        for (String key : catalogueRecordMap.keySet()) {

            List<CatalogueRecord> recordList = catalogueRecordMap.get(key);

            sb.append("## ").append(key);
            sb.append(Constants.LINE_BREAKER);

            for (CatalogueRecord record : recordList) {

                if (record.getPath().equals("")) {

                    sb.append(record.getAntipatternName());

                } else {

                    sb.append("[").append(record.getAntipatternName()).append("](").append(record.getPath()).append(")");
                }

                sb.append(Constants.LINE_BREAKER);
                sb.append(Constants.LINE_BREAKER);
            }

        }

        return sb.toString();
    }
}
