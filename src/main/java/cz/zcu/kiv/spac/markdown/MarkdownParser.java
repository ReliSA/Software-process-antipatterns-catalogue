package cz.zcu.kiv.spac.markdown;

import com.vladsch.flexmark.ast.*;
import com.vladsch.flexmark.ext.autolink.AutolinkExtension;
import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension;
import com.vladsch.flexmark.ext.gfm.tasklist.TaskListExtension;
import com.vladsch.flexmark.ext.tables.TableBlock;
import com.vladsch.flexmark.ext.tables.TableBody;
import com.vladsch.flexmark.ext.tables.TableHead;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.BlankLine;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.collection.iteration.ReversiblePeekingIterable;
import com.vladsch.flexmark.util.data.MutableDataHolder;
import com.vladsch.flexmark.util.data.MutableDataSet;
import cz.zcu.kiv.spac.data.Constants;
import cz.zcu.kiv.spac.data.antipattern.Antipattern;
import cz.zcu.kiv.spac.data.antipattern.heading.AntipatternHeading;
import cz.zcu.kiv.spac.data.antipattern.heading.AntipatternTableHeading;
import cz.zcu.kiv.spac.data.antipattern.heading.AntipatternTextHeading;
import cz.zcu.kiv.spac.data.catalogue.Catalogue;
import cz.zcu.kiv.spac.data.catalogue.CatalogueRecord;
import cz.zcu.kiv.spac.data.template.TemplateField;
import cz.zcu.kiv.spac.enums.AntipatternHeadingType;
import cz.zcu.kiv.spac.data.template.Template;
import cz.zcu.kiv.spac.html.HTMLGenerator;
import cz.zcu.kiv.spac.richtext.BulletFactory;
import cz.zcu.kiv.spac.utils.Utils;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.reactfx.util.LL;

import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
     * @param antipattern - Antipattern.
     * @param markdownContent - Markdown content.
     * @return Map of antipattern headings.
     */
    public Map<String, AntipatternHeading> parseHeadings(Antipattern antipattern, String markdownContent) {

        String antipatternName = antipattern.getName();
        Map<String, AntipatternHeading> headings = new LinkedHashMap<>();

        MutableDataHolder options = getDataOptions();
        Parser parser = Parser.builder(options).build();
        Node document = parser.parse(markdownContent);

        boolean firstHeadingAdded = false;
        boolean parsingHeading = false;
        StringBuilder headingContent = new StringBuilder();
        AntipatternHeading heading = null;
        String headingName = "";
        String headingText = "";
        String value = "";

        List<TemplateField> fieldList = template.getFieldList();

        for (Node node : document.getChildren()) {

            if (node.getClass() == Heading.class) {

                parsingHeading = true;

                headingText = node.getFirstChild().getChars().toString();

                if (!firstHeadingAdded) {

                    TemplateField nameField = fieldList.get(0);
                    headingName = nameField.getName();
                    value = headingText;
                    headingText = nameField.getText();

                    firstHeadingAdded = true;
                    heading = new AntipatternTextHeading(value);
                    heading.setType(AntipatternHeadingType.TEXT);

                    // Get "The Antipattern Name" field text and set it as heading text for antipattern name.
                    heading.setHeadingText(headingText);
                    heading.setHeadingName(headingName);

                } else {

                    TemplateField field = template.getField(headingText);

                    if (field != null) {

                        headingName = field.getName();

                    } else {

                        headingName = headingText;
                    }

                    Node nextNode = node.getNext();

                    while(true) {

                        if (nextNode == null || nextNode.getClass() != BlankLine.class) {

                            break;
                        }

                        nextNode = nextNode.getNext();
                    }

                    // Create new heading type.
                    if (nextNode == null || nextNode.getClass() == Paragraph.class
                            || nextNode.getClass() == Heading.class || nextNode.getClass() == BulletList.class) {

                        heading = new AntipatternTextHeading(headingContent.toString());

                    } else if (nextNode.getClass() == TableBlock.class) {

                        heading = new AntipatternTableHeading();
                    }

                    // Set heading text.
                    heading.setHeadingText(headingText);
                    heading.setHeadingName(headingName);
                    heading.setType(AntipatternHeadingType.TEXT);

                    headingContent = new StringBuilder();
                }

                // Add new heading to map of headings.
                headings.put(headingName, heading);

            } else if ((node.getClass() == Paragraph.class || node.getClass() == BulletList.class || node.getClass() == BlankLine.class) && parsingHeading) {

                if (heading.getClass() != AntipatternTextHeading.class) {

                    continue;
                }

                // If node is paragraph, then it means we are working with antipattern text heading.
                AntipatternTextHeading textHeading = (AntipatternTextHeading) heading;
                textHeading.appendValue(node.getChars().toString());

                // Set antipatternHeadingType as TEXT.
                heading.setType(AntipatternHeadingType.TEXT);

            } else if (node.getClass() == TableBlock.class && parsingHeading) {

                // Set antipatternHeadingType as TABLE.
                heading.setType(AntipatternHeadingType.TABLE);

                TableBlock tableBlockNode = (TableBlock) node;

                antipattern.setRelationsHeadingName(heading.getHeadingName());

                try {

                    // Try to parse all relations.
                    AntipatternTableHeading tableHeading = (AntipatternTableHeading) heading;
                    TableHead tableHead = (TableHead) tableBlockNode.getFirstChild();

                    // Get all column names.
                    if (!tableHead.getChars().equals("")) {

                        String columnsString = tableHead.getChars().toString();
                        String[] columns = columnsString.split("\\|");

                        // First skipped, because it is blank string.
                        for (int i = 1; i < columns.length; i++) {

                            tableHeading.addColumn(columns[i].replace(" ", ""));
                        }
                    }

                    // Get all relations.
                    TableBody tableBody = (TableBody) tableBlockNode.getLastChild();

                    for (Node tableNode : tableBody.getChildren()) {

                        String relation = tableNode.getChars().toString();
                        String[] antipatternRelation = relation.split("\\|");

                        // TODO: MAYBE IN FUTURE: maybe rework, because it will work only if 2 columns are presented.
                        // |aa|bb| is splitted into 3 values - first blank, second 'aa', third 'bb'.

                        if (antipatternRelation.length == 3) {

                            tableHeading.addRelation(antipatternRelation[1], antipatternRelation[2]);
                        }
                    }

                } catch (Exception e) {

                    log.warn("Table in antipattern '" + antipatternName + "' is not in valid format!");
                }
            }
        }

        /*
        for (AntipatternHeading antipatternHeading : headings.values()) {

            if (antipatternHeading.getType() == AntipatternHeadingType.TEXT) {

                AntipatternTextHeading textHeading = (AntipatternTextHeading) antipatternHeading;

                String headingValue = textHeading.getValue();

                if (headingValue.startsWith(Constants.LINE_BREAKER_CRLF)) {

                    headingValue = headingValue.substring(Constants.LINE_BREAKER_CRLF.length());
                }

                if (headingValue.endsWith(Constants.LINE_BREAKER_CRLF + "\r")) {

                    headingValue = headingValue.substring(0, headingValue.length() - 1 - Constants.LINE_BREAKER_CRLF.length());
                }

                textHeading.setValue(headingValue);
            }
        }
         */

        return headings;
    }

    /**
     * Get references shortcuts from reference file.
     * @param referencesMarkdownText - References content in markdown.
     * @return List of references.
     */
    public static List<String> parseReferencesShortcuts(String referencesMarkdownText) {

        List<String> referencesShortcuts = new ArrayList<>();

        // Check if content is here.
        if (referencesMarkdownText == null) {

            return referencesShortcuts;
        }

        // Get lines from markdown content.
        List<String> lines = Utils.parseStringByLines(referencesMarkdownText);

        // Iterate through every line.
        for (String line : lines) {

            if (!line.isEmpty()) {

                // Shortcuts has format - [shortcut].
                if (line.contains("[") && line.contains("]")) {

                    int indexFirst = line.indexOf("[");
                    int indexLast = line.indexOf("]");

                    String shortcut = line.substring(indexFirst, indexLast + 1);

                    // Make sure that link to home is not included in shortcuts, because it has the same format.
                    if (!shortcut.toLowerCase().contains("home")) {

                        referencesShortcuts.add(shortcut);
                    }
                }
            }
        }

        return referencesShortcuts;
    }

    /**
     * Get name of antipattern from markdown table record format.
     * @param tableRecord - [AntipatternName](PathToAntipattern)
     * @return Parsed name of antipattern if it was link to antipattern, otherwise return same text.
     */
    public static String parseAntipatternFromTableRecord(String tableRecord) {

        if (tableRecord.matches("^\\[.*\\]\\(.*\\)$")) {

            return tableRecord.substring(tableRecord.indexOf("[") + 1, tableRecord.indexOf("]"));

        } else {

            return tableRecord;
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

        // You can re-use parser and renderer instances
        Node document = parser.parse(markdownContent);
        String htmlContent = HTMLGenerator.generateHTMLContentFromMarkdown(options, document);
        htmlContent = convertImagePathsToURI(htmlContent);

        return htmlContent;
    }

    /**
     * Convert image paths to URI to display image in html.
     * @param htmlContent - Html content.
     * @return Converted image paths in html content.
     */
    private String convertImagePathsToURI(String htmlContent) {

        List<String> imagesInHtml = new ArrayList<>();
        Map<String, String> srcAttributes = new HashMap<>();

        Pattern pattern = Pattern.compile("<img src=\\\"(.*)\\\" alt=\\\"(.*)\\\"( *)\\/>");
        Matcher matcher = pattern.matcher(htmlContent);

        // <img src="bro.png" alt="bro" />
        while (matcher.find()) {

            imagesInHtml.add(matcher.group());
        }

        pattern = Pattern.compile("src=\\\"(.*)\\.(png|jpg|jpeg|tiff|tif|bmp|gif|eps|raw|cr2|nef|orf|sr2)\\\"");
        matcher = pattern.matcher(htmlContent);

        // src="bro.png"
        while (matcher.find()) {

            // src="bro.png"
            String srcAttribute = matcher.group();

            // bro.png
            String imagePath = srcAttribute.substring(0, srcAttribute.length() - 1).replace("src=\"", "");

            // Get full path of image.
            imagePath = Utils.getRootDir() + "/" + Constants.CATALOGUE_FOLDER + "/" + imagePath;

            File imageFile = new File(imagePath);

            if (imageFile != null) {

                // Add html src attribute.
                imagePath = "src=\"" + imageFile.toURI()  + "\"";

            } else {

                // Add html src attribute.
                imagePath = "src=\"" + imagePath + "\"";
                log.warn("Image '" + imagePath + "' was not loaded, because it did not exists on this path.");
            }

            srcAttributes.put(srcAttribute, imagePath);
        }

        // Replace all old image html tags with new one (where image paths is represented as URI).
        for (String srcAttributeBefore : srcAttributes.keySet()) {

            String srcAttributeWithUrlPath = srcAttributes.get(srcAttributeBefore);

            htmlContent = htmlContent.replaceAll(srcAttributeBefore, srcAttributeWithUrlPath);
        }

        return htmlContent;
    }

    /**
     * Get data options for Markdown parser and HTML renderer
     * @return Data options.
     */
    public static MutableDataHolder getDataOptions() {

        MutableDataHolder options = new MutableDataSet();
        options = options.set(Parser.EXTENSIONS, Arrays.asList(TablesExtension.create(),
                StrikethroughExtension.create(),
                AutolinkExtension.create(),
                TaskListExtension.create()));

        //options = options.set(Parser.BLANK_LINES_IN_AST, true);
        //options = options.set(Parser.LISTS_LOOSE_WHEN_BLANK_LINE_FOLLOWS_ITEM_PARAGRAPH, true);
        options = options.set(Parser.INDENTED_CODE_NO_TRAILING_BLANK_LINES, false);

        return options;
    }
}
