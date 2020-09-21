package cz.zcu.kiv.spac.controllers;

import cz.zcu.kiv.spac.data.antipattern.Antipattern;
import cz.zcu.kiv.spac.data.Constants;
import cz.zcu.kiv.spac.data.antipattern.AntipatternRelation;
import cz.zcu.kiv.spac.enums.FieldType;
import cz.zcu.kiv.spac.template.TableField;
import cz.zcu.kiv.spac.template.Template;
import cz.zcu.kiv.spac.template.TemplateField;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.util.List;

public class AntipatternWindowController {

    @FXML
    private Button btnBack;

    @FXML
    private Button btnSave;

    @FXML
    private AnchorPane tabFormPane;


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

        // TODO: save changes into antipattern.
        // TODO: validate required fields.

        ObservableList<Node> nodes = tabFormPane.getChildren();

        List<String> fieldNameList = template.getFieldNameList();

        for (Node node : nodes) {

            if (fieldNameList.contains(node.getId())) {

                FieldType type = template.getFieldType(node.getId());
                System.out.print(node.getId());

                switch (type) {

                    case TEXTFIELD:

                        System.out.print(": " + ((TextField) node).getText());
                        break;

                    case TEXTAREA:

                        System.out.print(": " + ((TextArea) node).getText());
                        break;

                    case TABLE:

                        break;
                }

                System.out.println();
            }
        }

        if (false) {

            Stage stage = (Stage) btnSave.getScene().getWindow();
            stage.close();
        }
    }

    @FXML
    public void closeAntipatternWindow(ActionEvent actionEvent) {

        Stage stage = (Stage) btnBack.getScene().getWindow();
        stage.close();
    }

    public void loadAntipatternInfo() {

        // TODO: Set values from loaded antipattern.
        ObservableList<Node> childrens = tabFormPane.getChildren();
        childrens.clear();

        int layoutY = Constants.INIT_Y_LOCATION;
        for (TemplateField templateField : template.getFieldList()) {

            // TODO: set bold text + font ?
            Text templateFieldLabel = new Text(templateField.getText());

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

    private void setRegionBounds(Region field, Text templateFieldLabel) {

        field.setLayoutX(templateFieldLabel.getLayoutX());
        field.setLayoutY(templateFieldLabel.getLayoutY() + Constants.TABLE_OFFSET_Y);
        setFieldMinWidth(field, templateFieldLabel);
    }

    private void setFieldMinWidth(Region field, Text templateFieldLabel) {

        field.setMinWidth(tabFormPane.getPrefWidth() - (3 * templateFieldLabel.getLayoutX()) - field.getLayoutX());
    }

    public void setAntipattern(Antipattern antipattern) {

        this.antipattern = antipattern;
    }

    public void setTemplate(Template template) {

        this.template = template;
    }

}
