package cz.zcu.kiv.spac.markdown;

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
}
