package cz.zcu.kiv.spac.controllers;

import cz.zcu.kiv.spac.data.Constants;
import cz.zcu.kiv.spac.data.antipattern.Antipattern;
import cz.zcu.kiv.spac.data.antipattern.AntipatternContent;
import cz.zcu.kiv.spac.data.antipattern.AntipatternRelation;
import cz.zcu.kiv.spac.data.antipattern.heading.AntipatternHeading;
import cz.zcu.kiv.spac.data.antipattern.heading.AntipatternTableHeading;
import cz.zcu.kiv.spac.data.antipattern.heading.AntipatternTextHeading;
import cz.zcu.kiv.spac.data.catalogue.Catalogue;
import cz.zcu.kiv.spac.enums.AntipatternHeadingType;
import cz.zcu.kiv.spac.file.FileWriter;
import cz.zcu.kiv.spac.markdown.MarkdownGenerator;
import cz.zcu.kiv.spac.markdown.MarkdownParser;
import cz.zcu.kiv.spac.data.template.TableColumnField;
import cz.zcu.kiv.spac.data.template.TableField;
import cz.zcu.kiv.spac.data.template.Template;
import cz.zcu.kiv.spac.data.template.TemplateField;
import cz.zcu.kiv.spac.richtext.RichTextArea;
import cz.zcu.kiv.spac.utils.Utils;
import javafx.beans.Observable;
import javafx.beans.property.StringProperty;
import javafx.collections.*;
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
import javafx.stage.Stage;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

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
    private Catalogue catalogue;

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

        // Do nothing.
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

        // If antipattern is null, then it means that we want to create new antipattern.
        if (antipattern == null) {

            // Create antipattern file.
            File file = new File(Utils.createMarkdownFilename(tempAntipattern));

            try {

                if (!file.createNewFile()) {

                    log.error("File " + file.getAbsolutePath() + "cannot be created!");
                }

                if (file.exists()) {

                    // Save antipattern content to new file.
                    if (saveAntipatternToFile(file)) {

                        Utils.showAlertWindow(Alert.AlertType.INFORMATION, Constants.APP_NAME,
                                "Antipattern created successfully.",
                                "Antipattern '" + tempAntipattern.getName() + "' was created successfully.");

                        tempAntipattern.setPath(file.getPath());
                        antipatternCreated = true;

                        stage.close();

                    } else {

                        Utils.showAlertWindow(Alert.AlertType.ERROR, Constants.APP_NAME,
                                "Error while creating Antipattern.",
                                "Antipattern '" + tempAntipattern.getName() + "' was not created successfully.");
                    }
                }

            } catch (Exception e) {

                log.error("File " + file.getAbsolutePath() + "cannot be created!");
            }

        } else {

            // Save antipattern content to already existing antipattern file.
            if (saveAntipatternToFile(new File(antipattern.getPath()))) {

                Utils.showAlertWindow(Alert.AlertType.INFORMATION, Constants.APP_NAME,
                        "Antipattern updated successfully.",
                        "Antipattern '" + antipattern.getName() + "' was updated successfully.");

                antipatternUpdated = true;

                stage.close();

            } else {

                Utils.showAlertWindow(Alert.AlertType.ERROR, Constants.APP_NAME,
                        "Error while updating Antipattern.",
                        "Antipattern '" + antipattern.getName() + "' was not updated successfully.");

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

                wviewAntipatternPreview.getEngine().loadContent(markdownParser.generateHTMLContent(tempAntipattern.getContent().toString()));
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

        return FileWriter.write(file, tempAntipattern.getContent().toString());
    }

    /**
     * Create form and if any antipattern is updated, then fill fields with values.
     * @param stage - Stage.
     */
    void loadAntipatternInfo(Stage stage) {

        boolean disableAntipatternName = false;
        boolean antipatternNameFieldPassed = false;

        if (antipattern != null) {

            disableAntipatternName = true;
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

                templateFieldLabel.setText(templateFieldLabel.getText() + Constants.TEMPLATE_FIELD_OPTIONAL_STRING);
            }

            // Set label position.
            templateFieldLabel.setLayoutX(Constants.FIELD_OFFSET_X);
            templateFieldLabel.setLayoutY(layoutY);

            Node field;

            AntipatternHeading heading = null;

            if (antipattern != null) {

                heading = antipattern.getAntipatternHeading(templateField.getName(), templateField.isRequired());
            }

            // Create specific field by its type.
            switch (templateField.getType()) {

                case TEXTFIELD:

                    // Create textfield.
                    field = new TextField();
                    TextField textField = (TextField) field;

                    textField.setText(templateField.getDefaultValue());
                    textField.setPromptText(templateField.getPlaceholder());

                    // Get value from antipattern heading.
                    if (heading != null) {

                        textField.setText(((AntipatternTextHeading) heading).getValue());
                    }

                    // Set bounds for field.
                    setRegionBounds(textField, templateFieldLabel);

                    // Add offset to Y layout for next element.
                    layoutY += Constants.TEXTFIELD_OFFSET_Y;

                    // If form is for updating, then disable antipattern name field.
                    if (!antipatternNameFieldPassed) {

                        antipatternNameFieldPassed = true;

                        if (disableAntipatternName) {

                             if (antipattern != null && !antipattern.isCreated()) {

                                textField.setText(antipattern.getName());
                            }

                            textField.setDisable(true);
                        }
                    }

                    // Add field to tab.
                    childrens.add(field);

                    break;

                case TEXTAREA:

                    // Create textarea.
                    field = new RichTextArea(stage);
                    RichTextArea textAreaField = (RichTextArea) field;

                    // TODO: parse elements from heading and put them in richtext.
                    // TODO: maybe it will be in markdown format -> i think better.

                    /*
                    textAreaField.setText(templateField.getDefaultValue());
                    textAreaField.setPromptText(templateField.getPlaceholder());

                    // Get value from antipattern heading.
                    if (heading != null) {

                        textAreaField.setText(((AntipatternTextHeading) heading).getValue());
                    }
                     */

                    // Set bounds for field.
                    setRegionBounds(textAreaField, templateFieldLabel);

                    // Set maximum height for textarea.
                    textAreaField.setMaxHeight(Constants.TEXTAREA_HEIGHT);
                    textAreaField.setMinHeight(Constants.TEXTAREA_HEIGHT);

                    // Add offset to Y layout for next element.
                    layoutY += Constants.TEXTAREA_HEIGHT;


                    //RichTextArea textArea = new RichTextArea(this);

                    // Add textarea to tab.
                    childrens.add(field);

                    break;

                case TABLE:

                    // Create table.
                    field = new TableView<>();
                    TableView tableViewField = (TableView) field;

                    // Allow multiple select in table.
                    tableViewField.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

                    // Set bounds for field.
                    setRegionBounds(tableViewField, templateFieldLabel);

                    // Add columns to table specified in template.
                    tableViewField.getColumns().addAll(prepareTableColumns((TableField) templateField, tableViewField, tableViewField.getMinWidth()));

                    // Editable table.
                    tableViewField.setEditable(true);

                    // Add observable for antipattern name for relation.
                    ObservableList<AntipatternRelation> tableItemsPrepare =
                            FXCollections.observableArrayList(relation -> new Observable[] {relation.getAntipatternProperty(), relation.getRelationProperty()});

                    tableItemsPrepare.addListener((ListChangeListener.Change<? extends AntipatternRelation> c) -> {

                        while(c.next()) {

                            if (c.wasAdded() || c.wasUpdated()) {

                                checkDuplicate(tableItemsPrepare, c);
                            }
                        }
                    });

                    tableViewField.setItems(tableItemsPrepare);

                    // Add css class.
                    tableViewField.getStyleClass().add("table-view");

                    // Set maximum height.
                    tableViewField.setMaxHeight(Constants.TABLE_HEIGHT);

                    // Add offset to Y layout for next element.
                    layoutY += Constants.TABLE_HEIGHT;

                    // Add a button for creating new rows in table.
                    Button addRowButton = createButton("Add row", tableViewField.getLayoutX(), layoutY);

                    // Click event for add button.
                    addRowButton.setOnAction((event) -> {

                        // TODO: MAYBE IN FUTURE: Not good, because if someone add another column for table, then it will fail.
                        AntipatternRelation antipatternRelation = new AntipatternRelation();

                        // Iterate through every column in template field.
                        for (TableColumnField column : ((TableField) templateField).getColumns()) {

                            // Get factory value for column.
                            String valueFactory = prepareColumnValueFactory(column.getText());

                            StringProperty property = antipatternRelation.getProperty(valueFactory);

                            if (property != null) {

                                property.setValue(column.getDefaultValue());
                                tableViewField.getItems().add(antipatternRelation);

                            } else {

                                System.out.println("Error while adding new row in antipattern relation.");
                            }
                        }
                    });


                    // Add add button to tab.
                    childrens.add(addRowButton);

                    // Create delete button.
                    Button deleteRowsButton = createButton("Delete rows", addRowButton.getLayoutX() + addRowButton.getPrefWidth() + Constants.TABLE_BUTTON_OFFSET, layoutY);

                    // Click event for delete button.
                    deleteRowsButton.setOnAction((event) -> {

                        // Get selected indexes in table.
                        ObservableList<Integer> selectedItems = tableViewField.getSelectionModel().getSelectedIndices();

                        // Create temporary items-
                        ObservableList tableItems = tableViewField.getItems();
                        ObservableList tempTableItems =  FXCollections.observableArrayList(tableItems);

                        // Remove all selected items by his index in table.
                        for (Integer selectedRowIndex : selectedItems) {

                            AntipatternRelation relation = (AntipatternRelation) tableItems.get(selectedRowIndex);
                            tempTableItems.remove(relation);
                        }

                        tableViewField.setItems(tempTableItems);
                    });

                    // Add delete button to tab.
                    childrens.add(deleteRowsButton);

                    // Add offset to Y layout for next element.
                    layoutY += addRowButton.getPrefHeight() + Constants.BUTTON_OFFSET;

                    // Set all relations.
                    if (heading != null) {

                        AntipatternTableHeading tableHeading = (AntipatternTableHeading) heading;

                        tableViewField.getItems().addAll(tableHeading.getRelations());
                    }

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
     * Check if list contains duplicate after adding new item to table / changing current.
     * @param list - List with items in table.
     * @param c - Current change.
     */
    private void checkDuplicate(ObservableList list, ListChangeListener.Change c) {

        AntipatternRelation relation = (AntipatternRelation) c.getList().get(c.getFrom());

        if (Collections.frequency(list, relation) > 1) {

            Utils.showAlertWindow(Alert.AlertType.WARNING, Constants.APP_NAME,
                    "Adding Anti-pattern relation",
                    "There are duplicates in table, please remove them (they will be removed automatically after saving).");
        }
    }

    /**
     * Create table columns specified in template.
     * @param tableField - Table field.
     * @param tableViewWidth - Width of table view.
     * @return List of table columns.
     */
    private List<TableColumn> prepareTableColumns(TableField tableField, TableView tableView, Double tableViewWidth) {

        List<TableColumn> columns = new ArrayList<>();

        // Iterate through every column in template field.
        for (TableColumnField column : tableField.getColumns()) {

            // Get factory value for column.
            String valueFactory = prepareColumnValueFactory(column.getText());

            // Create new table column.
            TableColumn<AntipatternRelation, String> tableColumn = new TableColumn<>(column.getText());

            // Set table column width.
            tableColumn.setPrefWidth((tableViewWidth / tableField.getColumns().size()));

            // Set cell as textfield.
            tableColumn.setCellFactory(TextFieldTableCell.forTableColumn());
            // Set cell value as antipattern relation.
            tableColumn.setCellValueFactory(new PropertyValueFactory<>(valueFactory));

            // Set value of antipattern relation property (like antipattern name, relation with antipattern).
            tableColumn.setOnEditCommit(event -> {

                // Get updated antipattern.
                AntipatternRelation row = event.getRowValue();

                try {

                    // Set new value for updated antipattern property.
                    BeanUtils.setProperty(row, valueFactory, event.getNewValue());

                } catch (InvocationTargetException | IllegalAccessException e) {

                    log.warn("Cannot save value '" + event.getNewValue() + "' to column '" + column + "'");
                }
            });

            // Not resizable.
            tableColumn.setResizable(false);

            // Columns not reorderable.
            tableColumn.setReorderable(false);

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

        if (antipattern == null) {

            tempAntipattern = new Antipattern("", new AntipatternContent(""), "");
        }

        tempAntipattern.getAntipatternHeadings().clear();

        // Get all tab elements.
        ObservableList<Node> nodes = tabFormPane.getChildren();

        // Get names of all fields in tab.
        List<String> fieldNameList = template.getFieldNameList();

        boolean firstHeadingAdded = false;
        boolean knownAsAdded = false;

        // Iterate through every field on tab.
        for (Node node : nodes) {

            // If field name is represented in template.
            if (fieldNameList.contains(node.getId())) {

                // Get template field.
                TemplateField field = template.getField(node.getId());

                String headingName = node.getId();

                AntipatternHeading heading = null;

                // Get text from field.
                switch (field.getType()) {

                    case TEXTFIELD:

                        heading = new AntipatternTextHeading(((TextField) node).getText());
                        heading.setType(AntipatternHeadingType.TEXT);
                        break;

                    case TEXTAREA:

                        heading = new AntipatternTextHeading(((RichTextArea) node).getText());
                        heading.setType(AntipatternHeadingType.TEXT);
                        break;

                    case TABLE:

                        TableView table = (TableView) node;

                        ObservableList<AntipatternRelation> relations = table.getItems();
                        Set<AntipatternRelation> relationsSet = new LinkedHashSet<>();
                        relationsSet.addAll(relations);

                        heading = new AntipatternTableHeading(relationsSet);
                        heading.setType(AntipatternHeadingType.TABLE);
                        break;

                    default:

                        log.error("Undefined field type for field: " + headingName);
                }

                heading.setHeadingText(field.getText());
                heading.setHeadingName(headingName);

                // Check textarea and textfield.
                if (heading.getType() == AntipatternHeadingType.TEXT) {

                    AntipatternTextHeading textHeading = (AntipatternTextHeading) heading;

                    // If value of textarea / textfield is blank space, return error alert.
                    // Otherwise, add heading to temporary antipattern.
                    if (!previewed && field.isRequired() && textHeading.getValue().equals("")) {

                        log.warn("Field '" + field.getText() + "' is blank!");

                        Utils.showAlertWindow(Alert.AlertType.ERROR, Constants.APP_NAME,
                                "Blank field!",
                                "Field '" + field.getText() + "' is blank!");

                        return false;

                    } else {

                        // Add new heading to antipattern.
                        tempAntipattern.addAntipatternHeading(headingName, heading);

                        if (!firstHeadingAdded) {

                            // First heading is always antipattern name.
                            firstHeadingAdded = true;
                            tempAntipattern.setName(textHeading.getValue());

                        } else if (!knownAsAdded) {

                            // Second heading will be 'known as'.
                            // Split values in 'known as' by ; and add it to the list of linked antipatterns.
                            String[] linkedAntipatterns = textHeading.getValue().split(";");

                            for (String linkedAntipattern : linkedAntipatterns) {

                                if (linkedAntipattern.contains(field.getDefaultValue().toUpperCase()) || linkedAntipattern.contains(field.getDefaultValue().toLowerCase())) {

                                    continue;
                                }

                                if (antipattern == null || (antipattern != null && !antipattern.getLinkingAntipatterns().contains(linkedAntipattern))) {

                                    if (catalogue.isAntipatternPresentedInCatalogue(linkedAntipattern)) {

                                        log.warn("Column 'known as' contains antipattern name '" + linkedAntipattern + "', which is already presented in catalogue!");

                                        Utils.showAlertWindow(Alert.AlertType.WARNING, Constants.APP_NAME,
                                                "Existing antipattern",
                                                "Column 'known as' contains antipattern name '" + linkedAntipattern + "', which is already presented in catalogue!");

                                        return false;
                                    }
                                }

                                tempAntipattern.addLinkedAntipattern(linkedAntipattern);
                            }

                            knownAsAdded = true;
                        }
                    }

                } else if (heading.getType() == AntipatternHeadingType.TABLE){

                    assert heading instanceof AntipatternTableHeading;

                    // Check Table.
                    TableView table = (TableView) node;

                    // If table does not contain any antipattern relation, then show alert.
                    if (!previewed && field.isRequired() && (table.getItems() == null || table.getItems().size() == 0)) {

                        log.warn("Field '" + field.getText() + "' does not contains any record!");

                        Utils.showAlertWindow(Alert.AlertType.ERROR, Constants.APP_NAME,
                                "No records in table!",
                                "Field '" + field.getText() + "' does not contains any record!");

                        return false;

                    } else {

                        // Add new heading to antipattern.
                        tempAntipattern.addAntipatternHeading(headingName, heading);
                        tempAntipattern.setRelationsHeadingName(headingName);
                    }
                }
            }
        }

        // Create markdown content from headings.
        String markdownContent = MarkdownGenerator.createAntipatternMarkdownContent(tempAntipattern.getAntipatternHeadings(), template.getFieldList(), catalogue);

        // Set created markdown content to antipattern.
        tempAntipattern.setContent(markdownContent);
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

            AntipatternContent tempContent = new AntipatternContent(antipattern.getContent().toString());
            tempAntipattern = new Antipattern(antipattern.getName(), tempContent, antipattern.getPath());
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

    public void setCatalogue(Catalogue catalogue) {

        this.catalogue = catalogue;
    }
}
