package cz.zcu.kiv.spac.file;

import cz.zcu.kiv.spac.data.Constants;
import cz.zcu.kiv.spac.data.antipattern.Antipattern;
import cz.zcu.kiv.spac.data.antipattern.AntipatternContent;
import cz.zcu.kiv.spac.data.catalogue.Catalogue;
import cz.zcu.kiv.spac.data.catalogue.CatalogueRecord;
import cz.zcu.kiv.spac.data.template.TemplateFieldType;
import cz.zcu.kiv.spac.data.git.CustomGitObject;
import cz.zcu.kiv.spac.markdown.MarkdownGenerator;
import cz.zcu.kiv.spac.markdown.MarkdownParser;
import cz.zcu.kiv.spac.data.template.TableColumnField;
import cz.zcu.kiv.spac.data.template.TableField;
import cz.zcu.kiv.spac.data.template.Template;
import cz.zcu.kiv.spac.data.template.TemplateField;
import cz.zcu.kiv.spac.utils.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Class for loading files.
 */
public class FileLoader {

    // Logger.
    private static Logger log = LogManager.getLogger(FileLoader.class);

    /**
     * Load configuration (Template + git configuration).
     * @param configurationPath - Path to configuration.
     * @return Template and git configuration.
     */
    public static Template loadTemplate(String configurationPath) {

        Template template;
        CustomGitObject customGitObject;

        log.info("Loading configuration file: " + configurationPath);

        try {

            File configFile = new File(configurationPath);

            // Parse configuration as XML.
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document doc = documentBuilder.parse(configFile);

            doc.getDocumentElement().normalize();

            // Create new template object.
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
                    String defaultValue = attributes.getNamedItem("default_value").getTextContent();
                    String placeholder = attributes.getNamedItem("placeholder").getTextContent();

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
                        templateField = new TemplateField(name, text, field, required, defaultValue, placeholder);
                    }

                    // Add field to list.
                    fieldList.add(templateField);
                }

                template = new Template(fieldList);

            } catch (Exception e) {

                log.error("Error while parsing template! It was probably caused by bad element names or bad attributes names");
                return null;
            }

        } catch (Exception e) {

            return null;
        }

        log.info("Template was loaded successfully.");

        return template;
    }

    /**
     * Load configuration for git (branch name, ...).
     * @param propertiesFilePath - Path to properties file.
     * @return Git configuration.
     */
    public static CustomGitObject loadGitConfiguration(String propertiesFilePath) {

        String branchName = "";
        String repositoryUrl = "";
        String personalAccessToken = "";
        int fetchPeriod = -1;

        // Get username and password from properties file.
        File propertiesFile = new File(propertiesFilePath);

        if (propertiesFile.exists()) {

            Properties properties = new Properties();

            try {

                properties.load(new FileInputStream(propertiesFilePath));
                personalAccessToken = properties.getProperty("personalaccesstoken");
                branchName = properties.getProperty("branch");
                repositoryUrl = properties.getProperty("repository");

                try {
                    fetchPeriod = Integer.parseInt(properties.getProperty("fetchPeriod"));
                } catch (NumberFormatException exception) {
                    log.info("Invalid value of fetchPeriod property from git properties file. Using default value.");
                }

            } catch (Exception e) {

                // Do nothing.
                log.info("Error while retrieving git attributes from properties file!");
                return null;
            }
        }

        log.info("Git attributes was loaded successfully.");
        return new CustomGitObject(branchName, repositoryUrl, personalAccessToken, fetchPeriod);
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

                    AntipatternContent content = new AntipatternContent(MarkdownGenerator.getNonExistingAntipatternContent(catalogueAntipattern.getAntipatternName()));
                    Antipattern nonCreatedAntipattern = new Antipattern(catalogueAntipattern.getAntipatternName(), content, "");

                    antipatterns.put(nonCreatedAntipattern.getFormattedName(), nonCreatedAntipattern);

                } else {

                    // Get antipattern file.
                    File antipatternFile = new File(Utils.getAntipatternFolderPath() + catalogueAntipattern.getPath());

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

                            String markdownContent = loadFileContent(antipatternFile.getAbsolutePath());
                            markdownContent = markdownContent.replace("\r\r", Constants.LINE_BREAKER_CRLF);

                            content = new AntipatternContent(MarkdownGenerator.formatMarkdownTable(markdownContent));
                        }

                        Antipattern antipattern = new Antipattern(catalogueAntipattern.getAntipatternName(), content, catalogueAntipattern.getPath());

                        if (content != null) {

                            antipattern.setAntipatternHeadings(markdownParser.parseHeadings(antipattern, content.toString()));
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
            linkingAntipattern.setLinkedAntipatternName(linkedAntipatternName);

            // Add name of linking antipattern to list of linked antipatterns.
            linkedAntipattern.addLinkedAntipattern(antipatternName);
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

            byte[] encoded = Files.readAllBytes(Paths.get(path));
            return new String(encoded, StandardCharsets.UTF_8);

        } catch (IOException e) {

            log.warn("File '" + path + "' cannot be parsed.");
            return null;
        }
    }
}
