package cz.zcu.kiv.spac.markdown;

import com.vladsch.flexmark.ast.*;
import com.vladsch.flexmark.ext.autolink.AutolinkExtension;
import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension;
import com.vladsch.flexmark.ext.gfm.tasklist.TaskListExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataHolder;
import com.vladsch.flexmark.util.data.MutableDataSet;
import cz.zcu.kiv.spac.data.catalogue.Catalogue;
import cz.zcu.kiv.spac.data.catalogue.CatalogueRecord;
import cz.zcu.kiv.spac.template.Template;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class for parsing markdowns.
 */
public class MarkdownParser {

    private Template template;

    // Logger.
    private static Logger log = Logger.getLogger(MarkdownParser.class);

    /**
     * Constructor.
     * @param template - Template.
     */
    public MarkdownParser(Template template) {

        this.template = template;
    }

    /**
     * Parse antipattern content.
     * @param markdownContent - Antipattern markdown content.
     */
    public void parseAntipattern(String markdownContent) {

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

    /**
     * Parse catalogue markdown content and create Catalogue object.
     * @param markdownContent - Catalogue markdown content.
     * @return Catalogue.
     */
    public Catalogue parseCatalogue(String markdownContent) {

        Catalogue catalogue = new Catalogue();

        try {

            // Set parser and parse content.
            MutableDataHolder options = getDataOptions();
            Parser parser = Parser.builder(options).build();
            Node document = parser.parse(markdownContent);

            // If catalogue does not have any field, then return null.
            if (!document.hasChildren()) {

                return null;
            }

            // First heading is name of file - "Antipatterns Catalogue".
            boolean firstHeadingSkipped = false;
            boolean parsingCatalogueInstance = false;
            
            String catalogueInstanceName = "";
            List<CatalogueRecord> records = null;

            // Iterate through every child.
            for (Node node : document.getChildren()) {

                // If node is Heading.
                if (node.getClass() == Heading.class) {

                    // In catalogue, first heading is "Antipatterns Catalogue", so we need to skip it.
                    if (!firstHeadingSkipped) {

                        firstHeadingSkipped = true;
                        continue;
                    }

                    // If actual node is heading and parsing catalogue records was
                    if (parsingCatalogueInstance) {
                        
                        catalogue.addCatalogueInstance(catalogueInstanceName, records);
                    }

                    // Init records list.
                    records = new ArrayList<>();

                    parsingCatalogueInstance = true;

                    // Get heading node and its name.
                    Heading catalogueInstance = (Heading) node;

                    if (catalogueInstance.getFirstChild() != null) {

                        catalogueInstanceName = catalogueInstance.getFirstChild().getChars().toString();
                    }

                } else if (node.getClass() == Paragraph.class && parsingCatalogueInstance) {

                    // If node is paragraph + we are now iterating through antipattern list.

                    Node nodeCatalogueRecord = node.getFirstChild();
                    Node nodeCatalogueRecordLast = node.getLastChild();

                    if (nodeCatalogueRecord == null) {

                        continue;
                    }

                    String antipatternName = "";
                    String path = "";

                    if (nodeCatalogueRecord.getClass() == Link.class) {

                        // If this node is Link, then extract name of antipattern + relative path to it.
                        Link linkNode = (Link) nodeCatalogueRecord;
                        antipatternName = linkNode.getText().toString();
                        path = linkNode.getUrl().toString();

                    } else if (nodeCatalogueRecord.getClass() == Text.class) {

                        // If this node is Text, then extract only name of antipattern.
                        // It means that antipattern is still not created, only mentioned.
                        Text textNode = (Text) nodeCatalogueRecord;
                        antipatternName = textNode.getChars().toString();
                        path = "";

                        if (nodeCatalogueRecordLast != null && nodeCatalogueRecordLast.getClass() == Emphasis.class) {

                            antipatternName += nodeCatalogueRecordLast.getChars().toString();
                        }

                    } else {

                        log.warn("Unexpected node '" + node.getClass() + "' in Antipattern catalogue.");
                        continue;
                    }

                    // Add new catalogue record.
                    records.add(new CatalogueRecord(antipatternName, path));
                }

                // Add last instance to map - because last Node in catalogue file is paragraph, we need to do it right here
                // not in heading part.
                if (parsingCatalogueInstance) {

                    catalogue.addCatalogueInstance(catalogueInstanceName, records);
                }
            }

        } catch (Exception e) {

            return null;
        }

        return catalogue;
    }

    /**
     * Generate html content from markdown content.
     * @param markdownContent - Markdown content.
     * @return Html content.
     */
    public String generateHTMLContent(String markdownContent) {

        MutableDataHolder options = getDataOptions();
        Parser parser = Parser.builder(options).build();
        HtmlRenderer renderer = HtmlRenderer.builder(options).build();

        // You can re-use parser and renderer instances
        Node document = parser.parse(markdownContent);
        return renderer.render(document);
    }

    /**
     * Get data options for Markdown parser and HTML renderer
     * @return Data options.
     */
    private static MutableDataHolder getDataOptions() {

        MutableDataHolder options = new MutableDataSet();
        options.set(Parser.EXTENSIONS, Arrays.asList(TablesExtension.create(),
                StrikethroughExtension.create(),
                AutolinkExtension.create(),
                TaskListExtension.create()));

        return options;
    }
}
