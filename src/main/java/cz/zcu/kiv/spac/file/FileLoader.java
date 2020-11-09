package cz.zcu.kiv.spac.file;

import cz.zcu.kiv.spac.data.Constants;
import cz.zcu.kiv.spac.data.antipattern.Antipattern;
import cz.zcu.kiv.spac.data.antipattern.AntipatternContent;
import cz.zcu.kiv.spac.data.catalogue.Catalogue;
import cz.zcu.kiv.spac.data.catalogue.CatalogueRecord;
import cz.zcu.kiv.spac.enums.TemplateFieldType;
import cz.zcu.kiv.spac.git.GitConfiguration;
import cz.zcu.kiv.spac.markdown.MarkdownFormatter;
import cz.zcu.kiv.spac.markdown.MarkdownParser;
import cz.zcu.kiv.spac.template.TableColumnField;
import cz.zcu.kiv.spac.template.TableField;
import cz.zcu.kiv.spac.template.Template;
import cz.zcu.kiv.spac.template.TemplateField;
import cz.zcu.kiv.spac.utils.Utils;
import org.apache.log4j.Logger;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
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
     * Load configuration (Template + git configuration).
     * @param configurationPath - Path to configuration.
     * @return Template and git configuration.
     */
    public static Object[] loadConfiguration(String configurationPath) {

        Object[] configurationFields = new Object[2];

        Template template;
        GitConfiguration gitConfiguration;

        log.info("Loading configuration file: " + configurationPath);

        try {

            File configFile = new File(configurationPath);

            // Parse configuration as XML.
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document doc = documentBuilder.parse(configFile);

            doc.getDocumentElement().normalize();

            // Create new template object.
            template = loadTemplate(doc);
            gitConfiguration = loadGitConfiguration(doc);

        } catch (Exception e) {

            return null;
        }

        if (template == null || gitConfiguration == null) {

            return null;
        }

        log.info("Configuration was loaded successfully.");

        configurationFields[0] = template;
        configurationFields[1] = gitConfiguration;

        return configurationFields;
    }

    /**
     * Load template fields from xml document.
     * @param doc - XML Document.
     * @return New Template.
     */
    private static Template loadTemplate(Document doc) {

        try {
            List<TemplateField> fieldList = new ArrayList<>();

            NodeList fields = doc.getElementsByTagName("field");

            // Iterate through every template field in configuration.
            for (int i = 0; i < fields.getLength(); i++) {

                Node fieldNode = fields.item(i);
                NamedNodeMap attributes = fieldNode.getAttributes();

                String name = attributes.getNamedItem("name").getTextContent();
                String text = attributes.getNamedItem("text").getTextContent();
                TemplateFieldType field = TemplateFieldType.valueOf(attributes.getNamedItem("field").getTextContent().toUpperCase());
                boolean required = attributes.getNamedItem("required").getTextContent().equals("yes");

                TemplateField templateField;

                // If current field is table, parse its columns and add it to list.
                if (field == TemplateFieldType.TABLE) {

                    templateField = new TableField(name, text, field, required);

                    NodeList columns = ((Element) fieldNode).getElementsByTagName("column");

                    for (int j = 0; j < columns.getLength(); j++) {
                        String columnName = columns.item(j).getAttributes().getNamedItem("text").getTextContent();
                        String columnDefaultValue = columns.item(j).getAttributes().getNamedItem("default_value").getTextContent();

                        ((TableField) templateField).addColumn(new TableColumnField(columnName, columnDefaultValue));
                    }

                } else {

                    // Otherwise create normal template field.
                    templateField = new TemplateField(name, text, field, required);
                }

                // Add field to list.
                fieldList.add(templateField);
            }

            return new Template(fieldList);

        } catch (Exception e) {

            log.error("Error while parsing template! It was probably caused by bad element names or bad attributes names");
            return null;
        }

    }

    /**
     * Load configuration for git (branch name, ...).
     * @param doc - XML Document.
     * @return Git configuration.
     */
    private static GitConfiguration loadGitConfiguration(Document doc) {

        try {

            String branchName = "";
            String repositoryUrl = "";

            // Load branch name from configuration.
            NodeList branchNode = doc.getElementsByTagName("branch");
            if (branchNode.getLength() == 1) {

                branchName = branchNode.item(0).getAttributes().getNamedItem("name").getTextContent();

            } else {

                log.error("There are more branches specified in configuration!");
                return null;
            }

            // Load repository url from configuration.
            NodeList repositoryNode = doc.getElementsByTagName("repository");
            if (repositoryNode.getLength() == 1) {

                repositoryUrl = repositoryNode.item(0).getAttributes().getNamedItem("url").getTextContent();

            } else {

                log.error("There are more repositories specified in configuration!");
                return null;
            }

            return new GitConfiguration(branchName, repositoryUrl);

        } catch (Exception e) {

            log.error("Error while parsing git configuration! It was probably caused by bad elements name or bad attributes names");
            return null;
        }

    }

    /**
     * Load all antipatterns from catalogue file.
     * @param markdownParser - Markdown parser.
     * @param catalogue - Antipattern catalogue.
     * @return Map of antipatterns.
     */
    public static Map<String, Antipattern> loadAntipatterns(MarkdownParser markdownParser, Catalogue catalogue) {

        log.info("Initializing antipattern list.");

        Map<String, Antipattern> antipatterns = new LinkedHashMap<>();

        // Get all catalogue instances.
        Map<String, List<CatalogueRecord>> catalogueRecords = catalogue.getCatalogueRecords();

        Map<String, String> linkedAntipatterns = new HashMap<>();

        // Iterate through every catalogue instance.
        for (String catalogueInstance : catalogueRecords.keySet()) {

            // Get all antipatterns in instance.
            List<CatalogueRecord> catalogueAntipatterns = catalogueRecords.get(catalogueInstance);

            // Iterate through every antipattern.
            for (CatalogueRecord catalogueAntipattern : catalogueAntipatterns) {

                // If antipattern does not have path, then
                if (catalogueAntipattern.getPath().equals("")) {

                    AntipatternContent content = new AntipatternContent(MarkdownFormatter.getNonExistingAntipatternContent(catalogueAntipattern.getAntipatternName()));
                    Antipattern nonCreatedAntipattern = new Antipattern(catalogueAntipattern.getAntipatternName(), content, "");

                    antipatterns.put(nonCreatedAntipattern.getFormattedName(), nonCreatedAntipattern);

                } else {

                    // Get antipattern file.
                    File antipatternFile = new File(Utils.getRootDir() + "/" + catalogueAntipattern.getPath());

                    // If antipattern file exists.
                    if (antipatternFile.exists()) {

                        // Get antipattern name from filename.
                        String filenameFromStringPath = Utils.getFilenameFromStringPath(catalogueAntipattern.getPath());
                        String filenameToAntipatternName = Utils.formatAntipatternName(filenameFromStringPath);

                        AntipatternContent content = null;

                        // If antipattern name from filename and antipattern name from catalogue isn't equal, it means that current
                        // antipattern linking another antipattern.
                        if (!Utils.isAntipatternNamesEquals(filenameToAntipatternName, catalogueAntipattern.getAntipatternName())) {

                            String formattedName = Utils.formatAntipatternName(catalogueAntipattern.getAntipatternName());
                            linkedAntipatterns.put(formattedName, filenameToAntipatternName);

                        } else {

                            String markdownContent = loadFileContent(catalogueAntipattern.getPath());
                            markdownContent = markdownContent.replace("\r\r", Constants.LINE_BREAKER);

                            content = new AntipatternContent(MarkdownFormatter.formatMarkdownTable(markdownContent));
                        }

                        Antipattern antipattern = new Antipattern(catalogueAntipattern.getAntipatternName(), content, catalogueAntipattern.getPath());

                        if (content != null) {

                            antipattern.setAntipatternHeadings(markdownParser.parseHeadings(antipattern.getName(), content.toString()));
                        }

                        antipatterns.put(antipattern.getFormattedName(), antipattern);

                    } else {

                        log.warn("Antipattern file was not found in path: " + catalogueAntipattern.getPath());
                    }
                }
            }
        }

        // Link antipattern contents to specific antipatterns.
        for (String antipatternName : linkedAntipatterns.keySet()) {

            String linkedAntipatternName = linkedAntipatterns.get(antipatternName);

            // Get linked and linking antipattern.
            Antipattern linkedAntipattern = antipatterns.get(linkedAntipatternName);
            Antipattern linkingAntipattern = antipatterns.get(antipatternName);

            // Set content.
            linkingAntipattern.setContent(linkedAntipattern.getContent());

            linkingAntipattern.setLinking(true);
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
