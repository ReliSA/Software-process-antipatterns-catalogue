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
import cz.zcu.kiv.spac.data.antipattern.Antipattern;
import cz.zcu.kiv.spac.data.antipattern.heading.AntipatternHeading;
import cz.zcu.kiv.spac.data.antipattern.heading.AntipatternTextHeading;
import cz.zcu.kiv.spac.data.catalogue.Catalogue;
import cz.zcu.kiv.spac.data.catalogue.CatalogueRecord;
import cz.zcu.kiv.spac.enums.AntipatternHeadingType;
import cz.zcu.kiv.spac.template.Template;
import org.apache.log4j.Logger;

import java.util.*;

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
     * Parse antipattern headings from markdown content.
     * @param markdownContent - Markdown content.
     * @return Map of antipattern headings.
     */
    public Map<String, AntipatternHeading> parseHeadings(String markdownContent) {

        Map<String, AntipatternHeading> headings = new LinkedHashMap<>();

        MutableDataHolder options = getDataOptions();
        Parser parser = Parser.builder(options).build();
        Node document = parser.parse(markdownContent);

        boolean firstHeadingAdded = false;
        boolean parsingHeading = false;
        StringBuilder headingContent = new StringBuilder();
        AntipatternHeading heading = null;

        for (Node node : document.getChildren()) {

            if (node.getClass() == Heading.class) {

                parsingHeading = true;

                String headingName = node.getFirstChild().getChars().toString();

                if (!firstHeadingAdded) {

                    firstHeadingAdded = true;
                    heading = new AntipatternTextHeading(headingName);
                    heading.setType(AntipatternHeadingType.TEXT);

                    // Get "The Antipattern Name" field text and set it as heading text for antipattern name.
                    String antipatternFieldText = template.getFieldTextList().get(0);
                    heading.setHeadingText(antipatternFieldText);
                    headingName = antipatternFieldText;

                } else {

                    heading = new AntipatternTextHeading(headingContent.toString());
                    heading.setHeadingText(headingName);
                    headingContent = new StringBuilder();

                    // TODO: somehow try to check table and set type.
                    heading.setType(AntipatternHeadingType.TEXT);
                }

                headings.put(headingName, heading);

            } else if (node.getClass() == Paragraph.class && parsingHeading) {

                AntipatternTextHeading textHeading = (AntipatternTextHeading) heading;
                textHeading.appendValue(node.getChars().toString());
            }
        }

        return headings;
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

                        // Part, where is parsed record like "Antipattern name - see [Other antipattern](path/to/other antipattern).
                        if (nodeCatalogueRecordLast != null && nodeCatalogueRecordLast.getClass() == Emphasis.class) {

                            for (Node emphasisChildNode : nodeCatalogueRecordLast.getChildren()) {

                                if (emphasisChildNode.getClass() == Link.class) {

                                    Link emphasisLink = (Link) emphasisChildNode;
                                    path = emphasisLink.getUrl().toString();
                                }
                            }

                            antipatternName = antipatternName.replace(" - ", "");
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
