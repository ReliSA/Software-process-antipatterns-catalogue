package cz.zcu.kiv.spac.file;

import cz.zcu.kiv.spac.data.Constants;
import cz.zcu.kiv.spac.data.antipattern.Antipattern;
import cz.zcu.kiv.spac.enums.FieldType;
import cz.zcu.kiv.spac.markdown.MarkdownFormatter;
import cz.zcu.kiv.spac.markdown.MarkdownParser;
import cz.zcu.kiv.spac.template.TableField;
import cz.zcu.kiv.spac.template.Template;
import cz.zcu.kiv.spac.template.TemplateField;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

/**
 * Class for loading files.
 */
public class FileLoader {

    // Logger.
    private static Logger log = Logger.getLogger(FileLoader.class);

    /**
     * Load configuration.
     * @param configurationPath - Path to configuration.
     * @return Template.
     */
    public static Template loadConfiguration(String configurationPath) {

        Template template;

        List<TemplateField> fieldList = new ArrayList<>();

        log.info("Loading configuration file: " + configurationPath);

        try {

            File configFile = new File(configurationPath);

            // Parse configuration as XML.
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document doc = documentBuilder.parse(configFile);

            doc.getDocumentElement().normalize();

            NodeList fields = doc.getElementsByTagName("field");

            // Iterate through every field in configuration.
            for (int i = 0; i < fields.getLength(); i++) {

                Node fieldNode = fields.item(i);
                NamedNodeMap attributes = fieldNode.getAttributes();

                String name = attributes.getNamedItem("name").getTextContent();
                String text = attributes.getNamedItem("text").getTextContent();
                FieldType field = FieldType.valueOf(attributes.getNamedItem("field").getTextContent().toUpperCase());
                boolean required = attributes.getNamedItem("required").getTextContent().equals("yes");

                TemplateField templateField;

                // If current field is table, parse its columns and add it to list.
                if (field == FieldType.TABLE) {

                    templateField = new TableField(name, text, field, required);

                    NodeList columns = ((Element) fieldNode).getElementsByTagName("column");

                    for (int j = 0; j < columns.getLength(); j++) {
                        String columnName = columns.item(j).getAttributes().getNamedItem("text").getTextContent();

                        ((TableField) templateField).addColumn(columnName);
                    }

                } else {

                    // Otherwise create normal template field.
                    templateField = new TemplateField(name, text, field, required);
                }

                // Add field to list.
                fieldList.add(templateField);
            }

            // Create new template object.
            template = new Template(fieldList);

        } catch (Exception e) {

            return null;
        }

        log.info("Configuration was loaded successfully.");

        return template;
    }

    /**
     * Load all antipatterns from folder.
     * @param markdownParser - Markdown parser.
     * @param antipatternFolder - Folder with antipattern files.
     * @return Map of antipatterns.
     */
    public static Map<String, Antipattern> loadAntipatterns(MarkdownParser markdownParser, String antipatternFolder) {

        log.info("Initializing antipattern list.");

        Map<String, Antipattern> antipatterns = new LinkedHashMap<>();

        // Get all files from folder.
        File folder = new File(antipatternFolder);
        File[] listOfFiles = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(Constants.FILES_EXTENSION));

        if (listOfFiles != null) {

            // Iterate through every file.
            for (File file : listOfFiles) {

                // If file exists.
                if (file.isFile()) {

                    // Get antipattern name.
                    String aPatternName = FilenameUtils.removeExtension(file.getName());

                    // If antipattern name is not in disabled name list.
                    if (!Constants.TEMPLATE_FILES.contains(aPatternName)) {

                        // Read markdown content and format it (if contains table).
                        String markdownContent = loadFileContent(file.getPath());

                        if (markdownContent == null) {

                            continue;
                        }

                        markdownContent = MarkdownFormatter.formatMarkdownTable(markdownContent);

                        // TODO: do parse.
                        //markdownParser.parse(markdownContent);

                        // Add new antipattern to map.
                        antipatterns.put(aPatternName, new Antipattern(aPatternName, markdownContent, file.getPath()));

                    }
                }
            }
        }

        log.info("Antipattern list initialized, loaded " + antipatterns.size() + " antipatterns");

        return antipatterns;
    }

    /**
     * Load file content from path.
     * @param path - Path to file.
     * @return File content.
     */
    public static String loadFileContent(String path) {

        try {

            File file = new File(path);
            return Files.readString(file.toPath());

        } catch (IOException e) {

            log.warn("File '" + path + "' cannot be parsed.");
            return null;
        }
    }
}
