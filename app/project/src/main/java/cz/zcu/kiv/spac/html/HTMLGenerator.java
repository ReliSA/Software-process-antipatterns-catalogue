package cz.zcu.kiv.spac.html;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataHolder;
import cz.zcu.kiv.spac.data.git.PreviewFileContentLine;

import java.util.List;

public class HTMLGenerator {

    /**
     * Generate html content from markdown content.
     * @param options - Options for renderer.
     * @param markdownDocument - Document with markdown content.
     * @return Html content.
     */
    public static String generateHTMLContentFromMarkdown(MutableDataHolder options, Node markdownDocument) {

        HtmlRenderer renderer = HtmlRenderer.builder(options).build();
        return renderer.render(markdownDocument);
    }

    /**
     * Create HTML content from file content lines.
     * @param contentLines - File content lines.
     * @return HTML file content.
     */
    public static String createHTMLFileContent(List<PreviewFileContentLine> contentLines) {

        StringBuilder htmlContent = new StringBuilder();
        htmlContent.append("<table>");

        if (contentLines != null) {

            for (PreviewFileContentLine contentLine : contentLines) {

                String line = contentLine.getLine();

                String lineNumberDeleted = "";
                String lineNumberAdded = "";
                String lineColorClass = "";
                String lineColorIndexClass = "";
                String symbol = "&nbsp;";

                htmlContent.append("<tr>");

                switch (contentLine.getType()) {

                    case ADDED:

                        lineNumberDeleted = "";
                        lineNumberAdded = "" + contentLine.getLineNumber();
                        lineColorClass = "line-added";
                        lineColorIndexClass = "line-index-added";
                        symbol = "" + line.charAt(0);
                        break;

                    case DELETED:

                        lineNumberDeleted = "" + contentLine.getLineNumber();
                        lineNumberAdded = "";
                        lineColorClass = "line-deleted";
                        lineColorIndexClass = "line-index-deleted";
                        symbol = "" + line.charAt(0);
                        break;

                    case DIFF_INFO:

                        lineColorClass = "line-diff-info";
                        lineColorIndexClass = "line-index-diff-info";
                        break;

                    case NOT_MODIFIED:

                        lineNumberDeleted = "" + contentLine.getLineNumber();
                        lineNumberAdded = "" + contentLine.getLineNumber();;
                        break;
                }

                symbol = "&nbsp;" + symbol;

                // If line is more than blank space, cut the symbol.
                if (line.length() > 1) {

                    line = line.substring(1);

                } else {

                    line = "";
                }

                htmlContent.append("<td class=\"line-index ").append(lineColorIndexClass).append("\">").
                        append(lineNumberDeleted).append("</td>");
                htmlContent.append("<td class=\"line-index ").append(lineColorIndexClass).append("\">").
                        append(lineNumberAdded).append("</td>");

                htmlContent.append("<td class=\"line ").append(lineColorClass).append("\">")
                        .append("<span class=\"line-symbol\">").append(symbol).append("</span>")
                        .append(line).append("</td>");
                htmlContent.append("</tr>");
            }
            htmlContent.append("</table>");

        } else {

            htmlContent.append("<h1>No content for preview</h1>");
        }

        return htmlContent.toString();
    }
}
