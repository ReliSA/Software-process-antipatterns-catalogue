package cz.zcu.kiv.spac.markdown;

import com.vladsch.flexmark.ast.Text;
import com.vladsch.flexmark.ext.autolink.AutolinkExtension;
import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension;
import com.vladsch.flexmark.ext.gfm.tasklist.TaskListExtension;
import com.vladsch.flexmark.ext.tables.TableBody;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataHolder;
import com.vladsch.flexmark.util.data.MutableDataSet;
import cz.zcu.kiv.spac.template.Template;
import cz.zcu.kiv.spac.template.TemplateField;

import java.util.Arrays;
import java.util.List;

public class MarkdownParser {

    private Template template;

    public MarkdownParser(Template template) {

        this.template = template;
    }

    public void parse(String markdownContent) {

        List<String> templateFieldTextList = template.getFieldTextList();

        // TODO: Parse markdown content and extract all fields.
        MutableDataHolder options = getDataOptions();
        Parser parser = Parser.builder(options).build();

        if (markdownContent.contains("Viewgraph Engineering") && !markdownContent.contains("AnalysisParalysis") && !markdownContent.contains("Architects Don't Code")) {
            Node document = parser.parse(markdownContent);

            test(document, 0);

            for (Node node : document.getChildren()) {

                //System.out.println(node.getNodeName());

                for (Node node2 : node.getChildren()) {

                    //System.out.println("\t" + node2.getNodeName() + " " + node2.hasChildren());
                    //System.out.println("\t\t" + node2.getChars().toString());

                    if (templateFieldTextList.contains(node2.getChars().toString())) {
                        //System.out.println(node2.getChars().toString());
                    }
                }
            }
        }
    }

    private void test(Node node, int i) {

        if (node.hasChildren()) {

            for (Node node1 : node.getChildren()) {

                for (int j = 0; j < i; j++) {
                    System.out.print("\t");
                }
                System.out.println(node1.getNodeName() + " - " + node1.getChars().toString());

                test(node1, i + 1);
            }
        }
    }

    public String generateHTMLContent(String markdownContent) {

        MutableDataHolder options = getDataOptions();
        Parser parser = Parser.builder(options).build();
        HtmlRenderer renderer = HtmlRenderer.builder(options).build();

        // You can re-use parser and renderer instances
        Node document = parser.parse(markdownContent);
        return renderer.render(document);
    }

    private static MutableDataHolder getDataOptions() {

        MutableDataHolder options = new MutableDataSet();
        options.set(Parser.EXTENSIONS, Arrays.asList(TablesExtension.create(),
                StrikethroughExtension.create(),
                AutolinkExtension.create(),
                TaskListExtension.create()));

        return options;
    }
}
