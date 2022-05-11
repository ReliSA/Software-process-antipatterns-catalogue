package cz.zcu.kiv.spac.file;

import cz.zcu.kiv.spac.data.Constants;
import cz.zcu.kiv.spac.data.antipattern.label.AntipatternLabel;
import cz.zcu.kiv.spac.data.template.TableColumnField;
import cz.zcu.kiv.spac.data.template.TableField;
import cz.zcu.kiv.spac.data.template.Template;
import cz.zcu.kiv.spac.data.template.TemplateField;
import cz.zcu.kiv.spac.utils.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * Class contains static methods for writing content to files.
 */
public class FileWriter {

    // Logger.
    private static Logger log = LogManager.getLogger(FileWriter.class);

    /**
     * Write content to file.
     *
     * @param file    - File.
     * @param content - Content.
     * @return True if writing into file was successful, false if not.
     */
    public static boolean write(File file, String content) {

        try {

            Writer out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(file), StandardCharsets.UTF_8));
            out.write(content + "\n");
            out.close();
            return true;

        } catch (IOException ex) {

            log.error("Error while writing content to file '" + file.getName() + "'");
            return false;
        }
    }

    /**
     * Write personall access token to file.
     *
     * @param personalAccessToken - PAT.
     * @param branch              - branch name.
     * @param repository          - Repository URL.
     */
    public static void writePAT(String personalAccessToken, String branch, String repository) {

        Properties properties = new Properties();
        try (OutputStream outputStream = new FileOutputStream(Utils.getRootDir() + "/" + Constants.PROPERTIES_NAME)) {

            properties.setProperty("personalaccesstoken", personalAccessToken);
            properties.setProperty("branch", branch);
            properties.setProperty("repository", repository);
            properties.store(outputStream, null);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Save anti-pattern template.
     *
     * @param configurationPath Path to template.
     * @param template          Anti-pattern template.
     */
    public static void saveTemplate(String configurationPath, Template template) {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.newDocument();
            doc.setXmlStandalone(true);

            Element configurationElement = doc.createElement("configuration");
            Element antipatternElement = doc.createElement("antipattern");
            Element fieldsElement = doc.createElement("fields");
            antipatternElement.appendChild(fieldsElement);
            configurationElement.appendChild(antipatternElement);
            doc.appendChild(configurationElement);

            for (TemplateField field : template.getFieldList()) {
                Element fieldElement = doc.createElement("field");
                fieldElement.setAttribute("name", field.getName());
                fieldElement.setAttribute("text", field.getText());
                fieldElement.setAttribute("field", field.getType().getDef());
                fieldElement.setAttribute("required", field.isRequired() ? "yes" : "no");
                fieldElement.setAttribute("default_value", field.getDefaultValue());
                fieldElement.setAttribute("placeholder", field.getPlaceholder());

                if (field instanceof TableField) {
                    for (TableColumnField column : ((TableField) field).getColumns()) {
                        Element columnElement = doc.createElement("column");
                        columnElement.setAttribute("text", column.getText());
                        columnElement.setAttribute("default_value", column.getDefaultValue());
                        fieldElement.appendChild(columnElement);
                    }
                }
                fieldsElement.appendChild(fieldElement);
            }

            Element labelsElement = doc.createElement("labels");
            configurationElement.appendChild(labelsElement);

            for (AntipatternLabel label : template.getLabelList()) {
                Element labelElement = doc.createElement("label");
                labelElement.setAttribute("color", "#" + Utils.getColorRGBHexString(label.getColor()));
                labelElement.setTextContent(label.getName());
                labelsElement.appendChild(labelElement);
            }

            DOMSource source = new DOMSource(doc);

            StreamResult file = new StreamResult(new File(configurationPath));

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(source, file);
        } catch (ParserConfigurationException | TransformerException e) {
            log.error("Error while saving template file!");
        }
    }
}
