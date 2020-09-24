package cz.zcu.kiv.spac.controllers;

import cz.zcu.kiv.spac.data.Constants;
import cz.zcu.kiv.spac.data.antipattern.Antipattern;
import cz.zcu.kiv.spac.data.antipattern.AntipatternRelation;
import cz.zcu.kiv.spac.enums.FieldType;
import cz.zcu.kiv.spac.file.FileWriter;
import cz.zcu.kiv.spac.markdown.MarkdownFormatter;
import cz.zcu.kiv.spac.markdown.MarkdownParser;
import cz.zcu.kiv.spac.template.TableField;
import cz.zcu.kiv.spac.template.Template;
import cz.zcu.kiv.spac.template.TemplateField;
import cz.zcu.kiv.spac.utils.Utils;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.steppschuh.markdowngenerator.text.heading.Heading;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.List;
import java.util.Map;

public class AntipatternWindowController {

    @FXML
    private Tab tabPreview;

    @FXML
    private WebView wviewAntipatternPreview;

    @FXML
    private Button btnBack;

    @FXML
    private Button btnSave;

    @FXML
    private AnchorPane tabFormPane;


    private MarkdownParser markdownParser;
    private Antipattern antipattern;
    private Template template;

    // Logger.
    private static Logger log = Logger.getLogger(AntipatternWindowController.class);

    public AntipatternWindowController() {

    }

    @FXML
    public void initialize() {

        // Not needed.
    }

    @FXML
    private void saveAP(ActionEvent actionEvent) {

        Stage stage = (Stage) btnSave.getScene().getWindow();

        if (!getAPContent(false)) {

            return;
        }

        FileChooser fileChooser = new FileChooser();

        //Set extension filter for text files
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Markdown files (*.md)", "*.md");
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.setInitialDirectory(new File(Utils.getRootDir() + "/" + Constants.CATALOGUE_FOLDER));

        //Show save file dialog
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {

            // create a alert
            Alert alert = new Alert(Alert.AlertType.NONE);
            alert.setTitle(Constants.APP_NAME);

            if (FileWriter.write(file, antipattern.getMarkdownContent())) {

                // TODO: Refresh list of antipatterns.

                alert.setAlertType(Alert.AlertType.INFORMATION);
                alert.setHeaderText("File created successfully.");
                alert.setContentText("Antipattern '" + antipattern.getName() + "' was created successfully.");
                alert.show();

                stage.close();

            } else {

                alert.setAlertType(Alert.AlertType.ERROR);
                alert.setHeaderText("Error while creating file.");
                alert.setContentText("Antipattern '" + antipattern.getName() + "' was not created successfully.");
                alert.show();
            }
        }
    }

    @FXML
    public void closeAntipatternWindow(ActionEvent actionEvent) {

        Stage stage = (Stage) btnBack.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void showAPPreview(Event event) {

        Tab selectedTab = (Tab) event.getTarget();
        if (selectedTab.getId().equals(tabPreview.getId()) && selectedTab.isSelected()) {

            getAPContent(true);
            wviewAntipatternPreview.getEngine().loadContent(markdownParser.generateHTMLContent(antipattern.getMarkdownContent()));
            wviewAntipatternPreview.getEngine().setUserStyleSheetLocation(getClass().getResource(Constants.RESOURCE_PREVIEW_CSS).toString());
        }
    }

    void loadAntipatternInfo() {

        // TODO: Set values from loaded antipattern.
        if (antipattern != null) {

        }

        ObservableList<Node> childrens = tabFormPane.getChildren();
        childrens.clear();

        int layoutY = Constants.INIT_Y_LOCATION;
        for (TemplateField templateField : template.getFieldList()) {

            Text templateFieldLabel = new Text(templateField.getText());
            templateFieldLabel.setFont(Constants.NEW_AP_LABEL_FONT);

            if (!templateField.isRequired()) {

                templateFieldLabel.setText(templateFieldLabel.getText() + " (optional)");
            }

            templateFieldLabel.setLayoutX(Constants.FIELD_OFFSET_X);
            templateFieldLabel.setLayoutY(layoutY);

            Node field;

            switch (templateField.getType()) {

                case TEXTFIELD:

                    field = new TextField();
                    TextField textField = (TextField) field;

                    setRegionBounds(textField, templateFieldLabel);

                    layoutY += Constants.TEXTFIELD_OFFSET_Y;

                    childrens.add(field);

                    break;

                case TEXTAREA:

                    field = new TextArea();
                    TextArea textAreaField = (TextArea) field;

                    // Set line break.
                    textAreaField.setWrapText(true);

                    setRegionBounds(textAreaField, templateFieldLabel);

                    textAreaField.setMaxHeight(Constants.TEXTAREA_HEIGHT);
                    layoutY += Constants.TEXTAREA_HEIGHT;

                    childrens.add(field);

                    break;

                case TABLE:

                    TableField tableField = (TableField) templateField;

                    field = new TableView<>();
                    TableView tableViewField = (TableView) field;

                    setRegionBounds(tableViewField, templateFieldLabel);

                    for (String column : tableField.getColumns()) {

                        TableColumn tableColumn = new TableColumn(column);
                        tableColumn.setPrefWidth(tableViewField.getMinWidth() / tableField.getColumns().size());
                        tableColumn.setResizable(false);
                        tableColumn.setCellFactory(TextFieldTableCell.forTableColumn());

                        tableViewField.getColumns().add(tableColumn);

                    }

                    tableViewField.setEditable(true);

                    // TODO: complete adding new row. Maybe add button for adding row below table ?
                    // TODO: add button to every row for deleting ? Or add button below table next to add button for multiselect delete ?
                    tableViewField.setOnMouseClicked((MouseEvent e) -> {

                        AntipatternRelation newPattern = new AntipatternRelation("", "");

                        Node pickedNode = e.getPickResult().getIntersectedNode();


                        if (e.getButton() == MouseButton.PRIMARY) {

                            tableViewField.getItems().add(newPattern);
                        }
                    });

                    tableViewField.setMaxHeight(Constants.TABLE_HEIGHT);
                    layoutY += Constants.TABLE_HEIGHT;

                    childrens.add(field);

                    break;

                default:
                    continue;
            }

            field.setId(templateField.getName());

            childrens.add(templateFieldLabel);

            layoutY += Constants.FIELD_OFFSET_Y;
        }
    }

    private boolean getAPContent(boolean previewed) {

        // TODO: save changes into antipattern.
        // TODO: validate required fields.

        if (antipattern == null) {

            // TODO: somehow manage to get antipattern name.
            antipattern = new Antipattern("", "");
        }

        ObservableList<Node> nodes = tabFormPane.getChildren();

        List<String> fieldNameList = template.getFieldNameList();

        for (Node node : nodes) {

            if (fieldNameList.contains(node.getId())) {

                TemplateField field = template.getField(node.getId());

                String headingName = node.getId();
                String headingText = "";

                System.out.println(node.getId());

                switch (field.getType()) {

                    case TEXTFIELD:

                        headingText = ((TextField) node).getText();
                        break;

                    case TEXTAREA:

                        headingText = ((TextArea) node).getText();
                        break;

                    case TABLE:

                        break;
                }


                // TODO: test if table has at least 1 row with values
                if (!previewed && field.isRequired() && headingText.equals("") && !headingName.equals("related")) {

                    log.warn("Field '" + field.getName() + "' is blank!");

                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle(Constants.APP_NAME);
                    alert.setHeaderText("Blank field!");
                    alert.setContentText("Field '" + field.getName() + "' is blank!");
                    alert.show();

                    return false;

                } else {

                    // TODO: save antipattern name to headings ?
                    antipattern.addAntipatternHeading(headingName, headingText);
                }
            }
        }
        System.out.println();

        // TODO: temporary.
        String markdownContent = MarkdownFormatter.createMarkdownFile(antipattern.getAntipatternHeadings(), template.getFieldList());

        antipattern.setMarkdownContent(markdownContent);
        return true;
    }

    private void setRegionBounds(Region field, Text templateFieldLabel) {

        field.setLayoutX(templateFieldLabel.getLayoutX());
        field.setLayoutY(templateFieldLabel.getLayoutY() + Constants.TABLE_OFFSET_Y);
        field.setMinWidth(tabFormPane.getPrefWidth() - (3 * templateFieldLabel.getLayoutX()) - field.getLayoutX());
    }

    public void setAntipattern(Antipattern antipattern) {

        this.antipattern = antipattern;
    }

    public void setTemplate(Template template) {

        this.template = template;
    }

    public void setMarkdownParser(MarkdownParser markdownParser) {

        this.markdownParser = markdownParser;
    }
}
