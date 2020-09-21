package cz.zcu.kiv.spac.controllers;

import cz.zcu.kiv.spac.data.antipattern.Antipattern;
import cz.zcu.kiv.spac.data.Constants;
import cz.zcu.kiv.spac.template.TableField;
import cz.zcu.kiv.spac.template.Template;
import cz.zcu.kiv.spac.utils.Utils;
import cz.zcu.kiv.spac.enums.FieldType;
import cz.zcu.kiv.spac.template.TemplateField;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class MainWindowController {

    // FXML elements.
    @FXML
    private ListView<String> listAntipatterns;

    @FXML
    public TextFlow txtFlowAntipatternPreview;

    @FXML
    private TextField txtFieldAPSearch;


    // App variables.
    private Template template;
    private Map<String, Antipattern> antipatterns;


    // Logger.
    private static Logger log = Logger.getLogger(MainWindowController.class);

    public MainWindowController() {

    }

    @FXML
    public void initialize() {

        loadConfiguration();

        log.info("Initializing antipattern list.");

        antipatterns = new LinkedHashMap<>();

        File folder = new File(Utils.getRootDir() + "/" + Constants.CATALOGUE_FOLDER);
        File[] listOfFiles = folder.listFiles();

        for (File file : listOfFiles) {

            if (file.isFile()) {

                String aPatternName = FilenameUtils.removeExtension(file.getName());

                if (!Constants.TEMPLATE_FILES.contains(aPatternName)) {

                    try {

                        antipatterns.put(aPatternName, new Antipattern(aPatternName, Files.readString(file.toPath())));
                        listAntipatterns.getItems().add(aPatternName);

                    } catch (IOException e) {

                        continue;
                    }
                }
            }
        }

        log.info("Antipattern list initialized, loaded " + antipatterns.size() + " antipatterns");

    }

    @FXML
    private void filterAntipatterns() {

        String searchText = txtFieldAPSearch.getText();

        if (searchText != null) {

            listAntipatterns.getItems().clear();

            for (String aPatternName : antipatterns.keySet()) {

                if (aPatternName.toLowerCase().contains(searchText.toLowerCase())) {

                    listAntipatterns.getItems().add(FilenameUtils.removeExtension(aPatternName));
                }
            }

            log.info("Searched phrase \"" +  searchText + "\", found " + listAntipatterns.getItems().size() + " antipatterns");
        }
    }

    @FXML
    private void antipatternSelected(MouseEvent mouseEvent) {

        String item = listAntipatterns.getSelectionModel().getSelectedItem();

        if (item == null) {
            mouseEvent.consume();
        }

        if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {

            if (mouseEvent.getClickCount() == 1) {

                // TODO: open AP in preview.
                ObservableList<javafx.scene.Node> childrens = txtFlowAntipatternPreview.getChildren();
                childrens.clear();
                childrens.add(new Text(antipatterns.get(item).getContent()));

                System.out.println("1-click: " + item);

            } else if (mouseEvent.getClickCount() == 2) {

                openAntipatternWindow(antipatterns.get(item));
            }
        }
    }

    @FXML
    private void menuExitAction() {

        // TODO: maybe close stage and end application in main method ?
        System.exit(0);
    }

    @FXML
    private void menuNewAPAction() {

        openAntipatternWindow();
    }

    private void loadConfiguration() {

        List<TemplateField> fieldList = new ArrayList<>();

        log.info("Loading configuration file: " + Constants.CONFIGURATION_NAME);

        try {

            File configFile = new File(Utils.getRootDir() + "/" + Constants.CONFIGURATION_NAME);

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
                        String columnName = columns.item(j).getAttributes().getNamedItem("text").getTextContent();;

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
            return;
        }

        log.info("Configuration was loaded successfully.");
    }

    private void openAntipatternWindow() {

        openAntipatternWindow(null);
    }

    private void openAntipatternWindow(Antipattern antipattern) {

        try {

            Stage stage = new Stage();

            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/windows/AntipatternWindow.fxml")));
            Parent root = loader.load();

            AntipatternWindowController antipatternWindowController = loader.<AntipatternWindowController>getController();
            antipatternWindowController.setAntipattern(antipattern);
            antipatternWindowController.setTemplate(template);
            antipatternWindowController.loadAntipatternInfo();

            stage.setTitle(Constants.APP_NAME);
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();

        } catch (Exception e) {

            log.error("Invalid AntipatternWindowController scene.");
            return;
        }
    }
}
