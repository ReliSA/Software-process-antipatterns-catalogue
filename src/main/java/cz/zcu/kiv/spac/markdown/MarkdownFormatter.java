package cz.zcu.kiv.spac.markdown;

import cz.zcu.kiv.spac.data.Constants;
import cz.zcu.kiv.spac.template.TemplateField;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MarkdownFormatter {

    public static String formatMarkdownTable(String markdownContent) {

        String patternString = "^(\\|--)+\\|$";

        Pattern pattern = Pattern.compile(patternString);

        StringBuilder newString = new StringBuilder();

        for (String contentLine : markdownContent.split("\n")) {
            Matcher matcher = pattern.matcher(contentLine);

            if (matcher.find()) {

                StringBuilder newParts = new StringBuilder();

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

    public static String createMarkdownFile(Map<String, String> headings, List<TemplateField> fieldList) {

        // TODO: Tasklist + table not implemented yet.
        StringBuilder sb = new StringBuilder();

        // Add path to antipattern name.
        sb.append("[Home](" + Constants.README_NAME + ") > [Catalogue](" + Constants.CATALOGUE_NAME + ") > ");

        boolean nameWrited = false;

        int i = 0;
        for (TemplateField field : fieldList) {

            if (nameWrited) {

                sb.append("## ");
                sb.append(field.getText());
                sb.append("\n");
                sb.append("\n");

                sb.append(headings.get(field.getName()));

            } else {

                String antipatternName = headings.get(field.getName());

                // Add antipatern name to path.
                sb.append(antipatternName);
                sb.append("\n");
                sb.append("\n");
                sb.append("\n");

                nameWrited = true;

                sb.append("# ");
                sb.append(antipatternName);
            }

            if (i < fieldList.size() - 1) {

                sb.append("\n");
                sb.append("\n");
            }

            i++;
        }

        return sb.toString();
    }
}
