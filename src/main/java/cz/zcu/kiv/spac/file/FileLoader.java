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
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class FileLoader {

    // Logger.
    private static Logger log = Logger.getLogger(FileLoader.class);

    public static Template loadConfiguration(String configurationPath) {

        Template template;

        List<TemplateField> fieldList = new ArrayList<>();

        log.info("Loading configuration file: " + configurationPath);

        try {

            File configFile = new File(configurationPath);

            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document doc = documentBuilder.parse(configFile);

            doc.getDocumentElement().normalize();

            NodeList fields = doc.getElementsByTagName("field");

            for (int i = 0; i < fields.getLength(); i++) {

                Node fieldNode = fields.item(i);
                NamedNodeMap attributes = fieldNode.getAttributes();

                String name = attributes.getNamedItem("name").getTextContent();
                String text = attributes.getNamedItem("text").getTextContent();
                FieldType field = FieldType.valueOf(attributes.getNamedItem("field").getTextContent().toUpperCase());
                boolean required = attributes.getNamedItem("required").getTextContent().equals("yes");

                TemplateField templateField;

                if (field == FieldType.TABLE) {

                    templateField = new TableField(name, text, field, required);

                    NodeList columns = ((Element) fieldNode).getElementsByTagName("column");

                    for (int j = 0; j < columns.getLength(); j++) {
                        String columnName = columns.item(j).getAttributes().getNamedItem("text").getTextContent();

                        ((TableField) templateField).addColumn(columnName);
                    }

                } else {

                    templateField = new TemplateField(name, text, field, required);
                }

                fieldList.add(templateField);
            }

            template = new Template(fieldList);

        } catch (Exception e) {

            log.error("Configuration is not valid!");
            return null;
        }

        log.info("Configuration was loaded successfully.");

        return template;
    }

    public static Map<String, Antipattern> loadAntipatterns(MarkdownParser markdownParser, String antipatternFolder) {

        log.info("Initializing antipattern list.");

        Map<String, Antipattern> antipatterns = new LinkedHashMap<>();

        File folder = new File(antipatternFolder);
        File[] listOfFiles = folder.listFiles();

        if (listOfFiles != null) {

            for (File file : listOfFiles) {

                if (file.isFile()) {

                    String aPatternName = FilenameUtils.removeExtension(file.getName());

                    if (!Constants.TEMPLATE_FILES.contains(aPatternName)) {

                        try {

                            String markdownContent = Files.readString(file.toPath());
                            markdownContent = MarkdownFormatter.formatMarkdownTable(markdownContent);

                            // TODO: do parse.
                            //markdownParser.parse(markdownContent);

                            antipatterns.put(aPatternName, new Antipattern(aPatternName, markdownContent));

                        } catch (IOException ignored) {

                        }
                    }
                }
            }
        }

        log.info("Antipattern list initialized, loaded " + antipatterns.size() + " antipatterns");

        return antipatterns;
    }
}
