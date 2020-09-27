package cz.zcu.kiv.spac.controllers;

import cz.zcu.kiv.spac.data.Constants;
import cz.zcu.kiv.spac.data.antipattern.Antipattern;
import cz.zcu.kiv.spac.data.antipattern.AntipatternRelation;
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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for antipattern window.
 */
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
    private Antipattern tempAntipattern;
    private Template template;

    private boolean antipatternUpdated = false;
    private boolean antipatternCreated = false;

    // Logger.
    private static Logger log = Logger.getLogger(AntipatternWindowController.class);

    /**
     * Constructor.
     */
    public AntipatternWindowController() {

    }

    /**
     * Initialize method for antipattern window.
     */
    @FXML
    public void initialize() {

        // Not needed.
    }

    /**
     * Save antipattern values.
     * @param actionEvent - Save button click action event.
     */
    @FXML
    private void saveAP(ActionEvent actionEvent) {

        Stage stage = (Stage) btnSave.getScene().getWindow();

        // If any required field is not filled, then cancel saving.
        if (!getAPContent(false)) {

            return;
        }

        // Create an alert.
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setTitle(Constants.APP_NAME);

        // If antipattern is null, then it means that we want to create new antipattern.
        if (antipattern == null) {

            FileChooser fileChooser = new FileChooser();

            // Set extension filter for markdown files.
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Markdown files (*.md)", "*.md");
            fileChooser.getExtensionFilters().add(extFilter);
            fileChooser.setInitialDirectory(new File(Utils.getRootDir() + "/" + Constants.CATALOGUE_FOLDER));

            // Show save file dialog.
            File file = fileChooser.showSaveDialog(stage);

            if (file != null) {

                // Save antipattern content to new file.
                if (saveAntipatternToFile(file)) {

                    alert.setAlertType(Alert.AlertType.INFORMATION);
                    alert.setHeaderText("Antipattern created successfully.");
                    alert.setContentText("Antipattern '" + tempAntipattern.getName() + "' was created successfully.");
                    alert.show();

                    tempAntipattern.setPath(file.getPath());
                    antipatternCreated = true;

                    stage.close();

                } else {

                    alert.setAlertType(Alert.AlertType.ERROR);
                    alert.setHeaderText("Error while creating Antipattern.");
                    alert.setContentText("Antipattern '" + tempAntipattern.getName() + "' was not created successfully.");
                    alert.show();
                }
            }

        } else {

            // Save antipattern content to already existing antipattern file.
            if (saveAntipatternToFile(new File(antipattern.getPath()))) {

                alert.setAlertType(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Antipattern updated successfully.");
                alert.setContentText("Antipattern '" + antipattern.getName() + "' was updated successfully.");
                alert.show();

                antipatternUpdated = true;

                stage.close();

            } else {

                alert.setAlertType(Alert.AlertType.ERROR);
                alert.setHeaderText("Error while updating Antipattern.");
                alert.setContentText("Antipattern '" + antipattern.getName() + "' was not updated successfully.");
                alert.show();

                return;
            }
        }
    }

    /**
     * Close antipattern window.
     * @param actionEvent Button back click action event.
     */
    @FXML
    public void closeAntipatternWindow(ActionEvent actionEvent) {

        Stage stage = (Stage) btnBack.getScene().getWindow();
        stage.close();
    }

    /**
     * Show preview of new / updated antipattern.
     * @param event - Event.
     */
    @FXML
    private void showAPPreview(Event event) {

        Tab selectedTab = (Tab) event.getTarget();
        if (selectedTab.getId().equals(tabPreview.getId()) && selectedTab.isSelected()) {

            // If every required field is filled, then show preview as html page.
            if (getAPContent(true)) {

                wviewAntipatternPreview.getEngine().loadContent(markdownParser.generateHTMLContent(tempAntipattern.getMarkdownContent()));
                wviewAntipatternPreview.getEngine().setUserStyleSheetLocation(getClass().getResource(Constants.RESOURCE_PREVIEW_CSS).toString());

            } else {

                log.warn("Something went wrong with parsing markdown content in preview.");
            }
        }
    }

    /**
     * Save antipattern to file.
     * @param file - Antipattern file.
     * @return True if saving antipattern content to file was successful, otherwise false.
     */
    private boolean saveAntipatternToFile(File file) {

        return FileWriter.write(file, tempAntipattern.getMarkdownContent());
    }

    /**
     * Create form and if any antipattern is updated, then fill fields with values.
     */
    void loadAntipatternInfo() {

        // TODO: Set values from loaded antipattern.
        if (antipattern != null) {

        }

        // Get tab elements.
        ObservableList<Node> childrens = tabFormPane.getChildren();

        // Clear every element on tab.
        childrens.clear();

        // Get init Y location for first element.
        int layoutY = Constants.INIT_Y_LOCATION;

        // Iterate through every field in template.
        for (TemplateField templateField : template.getFieldList()) {

            // Create label for input field.
            Text templateFieldLabel = new Text(templateField.getText());
            templateFieldLabel.setFont(Constants.NEW_AP_LABEL_FONT);

            // If field is not required, add additional info to label.
            if (!templateField.isRequired()) {

                templateFieldLabel.setText(templateFieldLabel.getText() + " (optional)");
            }

            // Set label position.
            templateFieldLabel.setLayoutX(Constants.FIELD_OFFSET_X);
            templateFieldLabel.setLayoutY(layoutY);

            Node field;

            // Create specific field by its type.
            switch (templateField.getType()) {

                case TEXTFIELD:

                    // Create textfield.
                    field = new TextField();
                    TextField textField = (TextField) field;

                    // Set bounds for field.
                    setRegionBounds(textField, templateFieldLabel);

                    // Add offset to Y layout for next element.
                    layoutY += Constants.TEXTFIELD_OFFSET_Y;

                    // Add field to tab.
                    childrens.add(field);

                    break;

                case TEXTAREA:

                    /*
                    // TODO: HTMLEditor as Rich TextArea ?
                    // https://stackoverflow.com/questions/10075841/how-to-hide-the-controls-of-htmleditor
                    field = new HTMLEditor();
                    HTMLEditor htmlEditorField = (HTMLEditor) field;

                    setRegionBounds(htmlEditorField, templateFieldLabel);

                    htmlEditorField.setMaxHeight(Constants.TEXTAREA_HEIGHT);
                    layoutY += Constants.TEXTAREA_HEIGHT;
                     */

                    // Create textarea.
                    field = new TextArea();
                    TextArea textAreaField = (TextArea) field;

                    // Set line break.
                    textAreaField.setWrapText(true);

                    // Set bounds for field.
                    setRegionBounds(textAreaField, templateFieldLabel);

                    // Set maximum height for textarea.
                    textAreaField.setMaxHeight(Constants.TEXTAREA_HEIGHT);
                    // Add offset to Y layout for next element.
                    layoutY += Constants.TEXTAREA_HEIGHT;

                    // Add textarea to tab.
                    childrens.add(field);

                    break;

                case TABLE:

                    // Create table.
                    field = new TableView<AntipatternRelation>();
                    TableView tableViewField = (TableView) field;

                    // Set bounds for field.
                    setRegionBounds(tableViewField, templateFieldLabel);

                    // Add columns to table specified in template.
                    tableViewField.getColumns().addAll(prepareTableColumns((TableField) templateField, tableViewField.getMinWidth()));

                    // Editable table.
                    tableViewField.setEditable(true);

                    // Set maximum height.
                    tableViewField.setMaxHeight(Constants.TABLE_HEIGHT);

                    // Add offset to Y layout for next element.
                    layoutY += Constants.TABLE_HEIGHT;

                    // Add a button for creating new rows in table.
                    Button addRowButton = createButton("Add row", tableViewField.getLayoutX(), layoutY);

                    // Click event for button.
                    addRowButton.setOnAction((event) -> {

                        tableViewField.getItems().add(new AntipatternRelation("Antipattern name", "Relation"));
                    });

                    // Add button to tab.
                    childrens.add(addRowButton);

                    // Add offset to Y layout for next element.
                    layoutY += addRowButton.getPrefHeight() + Constants.BUTTON_OFFSET;

                    // Add table to tab.
                    childrens.add(field);

                    break;

                default:
                    continue;
            }

            // Set field id for parsing.
            field.setId(templateField.getName());

            // Add label to tab.
            childrens.add(templateFieldLabel);

            // Add offset to Y layout for next element.
            layoutY += Constants.FIELD_OFFSET_Y;
        }
    }

    /**
     * Create table columns specified in template.
     * @param tableField - Table field.
     * @param tableViewWidth - Width of table view.
     * @return List of table columns.
     */
    private List<TableColumn> prepareTableColumns(TableField tableField, Double tableViewWidth) {

        List<TableColumn> columns = new ArrayList<>();

        // Iterate through every column in template field.
        for (String column : tableField.getColumns()) {

            // Get factory value for column.
            String valueFactory = prepareColumnValueFactory(column);

            // Create new table column.
            TableColumn<AntipatternRelation, String> tableColumn = new TableColumn<AntipatternRelation, String>(column);

            // Set table column width.
            tableColumn.setPrefWidth(tableViewWidth / tableField.getColumns().size());

            // Not resizable.
            tableColumn.setResizable(false);

            // TODO: new Cell factory and Cell Value factory.
            tableColumn.setCellFactory(TextFieldTableCell.forTableColumn());
            tableColumn.setCellValueFactory(new PropertyValueFactory<AntipatternRelation, String>(valueFactory));

            columns.add(tableColumn);
        }

        return columns;
    }

    /**
     * Replace specified characters in columnName to get factory column name.
     * @param columnName - Raw column name.
     * @return Factory column name.
     */
    private String prepareColumnValueFactory(String columnName) {

        String valueFactory = columnName.replace("-", "");
        valueFactory = valueFactory.replace(" ", "");
        valueFactory = valueFactory.replace("_", "");

        return valueFactory.toLowerCase();
    }

    /**
     * Create button.
     * @param text - Button text.
     * @param layoutX - Button X position.
     * @param layoutY - Button Y position.
     * @return New Button.
     */
    private Button createButton(String text, Double layoutX, double layoutY) {

        Button button = new Button(text);
        button.setPrefWidth(btnBack.getPrefWidth());
        button.setPrefHeight(btnBack.getPrefHeight());
        button.setLayoutX(layoutX);
        button.setLayoutY(layoutY + Constants.BUTTON_OFFSET);

        return button;
    }

    /**
     * Get text from fields and create antipattern headings.
     * @param previewed - True if content is only for preview, false if it is for saving.
     * @return True of every required field is filled.
     */
    private boolean getAPContent(boolean previewed) {

        // TODO: save changes into antipattern.

        if (antipattern == null) {

            // TODO: somehow manage to get antipattern name.
            tempAntipattern = new Antipattern("", "", "");
        }

        // Get all tab elements.
        ObservableList<Node> nodes = tabFormPane.getChildren();

        // Get names of all fields in tab.
        List<String> fieldNameList = template.getFieldNameList();

        boolean firstHeadingAdded = false;

        // Iterate through every field on tab.
        for (Node node : nodes) {

            // If field name is represented in template.
            if (fieldNameList.contains(node.getId())) {

                // Get template field.
                TemplateField field = template.getField(node.getId());

                String headingName = node.getId();
                String headingText = "";

                // Get text from field.
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
                // Check if every required field is filled if previewd is true.
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
                    // Add new heading to antipattern.
                    tempAntipattern.addAntipatternHeading(headingName, headingText);

                    if (!firstHeadingAdded) {

                        firstHeadingAdded = true;
                        tempAntipattern.setName(headingText);
                    }
                }
            }
        }

        // TODO: temporary.
        // Create markdown content from headings.
        String markdownContent = MarkdownFormatter.createMarkdownTemplateFile(tempAntipattern.getAntipatternHeadings(), template.getFieldList());

        // Set created markdown content to antipattern.
        tempAntipattern.setMarkdownContent(markdownContent);
        return true;
    }

    /**
     * Set bounds for field.
     * @param field - Field.
     * @param templateFieldLabel - Label for field.
     */
    private void setRegionBounds(Region field, Text templateFieldLabel) {

        field.setLayoutX(templateFieldLabel.getLayoutX());
        field.setLayoutY(templateFieldLabel.getLayoutY() + Constants.TABLE_OFFSET_Y);

        // Calculate field width.
        // (3 * templateFieldLabel.getLayoutX()) - x3 is for better offset from right.
        field.setMinWidth(tabFormPane.getPrefWidth() - (3 * templateFieldLabel.getLayoutX()) - field.getLayoutX());
        field.setMaxWidth(tabFormPane.getPrefWidth() - (3 * templateFieldLabel.getLayoutX()) - field.getLayoutX());
    }

    public void setAntipattern(Antipattern antipattern) {

        this.antipattern = antipattern;

        if (antipattern != null) {

            // Set new temporary antipattern (only available when antipatternWindow is showed).
            tempAntipattern = new Antipattern(antipattern.getName(), antipattern.getMarkdownContent(), antipattern.getPath());
            tempAntipattern.setAntipatternHeadings(antipattern.getAntipatternHeadings());
        }
    }

    public void setTemplate(Template template) {

        this.template = template;
    }

    void setMarkdownParser(MarkdownParser markdownParser) {

        this.markdownParser = markdownParser;
    }

    boolean isAntipatternUpdated() {

        return antipatternUpdated;
    }

    public Antipattern getTempAntipattern() {

        return this.tempAntipattern;
    }

    public boolean isAntipatternCreated() {

        return antipatternCreated;
    }
}
