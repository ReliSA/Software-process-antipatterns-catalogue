package cz.zcu.kiv.spac.controllers;

import cz.zcu.kiv.spac.data.Antipattern;
import cz.zcu.kiv.spac.data.Constants;
import cz.zcu.kiv.spac.template.TableField;
import cz.zcu.kiv.spac.template.TemplateField;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
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
    private List<TemplateField> fieldList;

    // Logger.
    private static Logger log = Logger.getLogger(MainWindowController.class);

    public AntipatternWindowController() {

    }

    @FXML
    public void initialize() {

        /*
        for (TemplateField field : fieldList) {

            System.out.println(field.getName() + ": " + field.getText());
        }
         */

        if (antipattern != null) {

            // TODO: fill fields with values from
        }
    }

    @FXML
    private void saveAP(ActionEvent actionEvent) {

        // TODO: save changes into antipattern.

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

        // TODO: Load fields from template.
        ObservableList<Node> childrens = tabFormPane.getChildren();
        childrens.clear();

        int layoutY = Constants.FIELD_OFFSET_Y;
        for (TemplateField templateField : fieldList) {

            Text templateFieldLabel = new Text(templateField.getText() + ": ");
            templateFieldLabel.setLayoutX(Constants.FIELD_OFFSET_X);
            templateFieldLabel.setLayoutY(layoutY);

            Node field;

            switch (templateField.getType()) {

                case TEXTFIELD:

                    field = new TextField();

                    Bounds bounds = templateFieldLabel.getBoundsInLocal();
                    field.setLayoutX(bounds.getMaxX() + Constants.FIELD_OFFSET_X);
                    field.setLayoutY(layoutY - Constants.TEXTFIELD_OFFSET_Y);

                    childrens.add(field);

                    break;

                case TEXTAREA:

                    field = new TextArea();
                    TextArea textAreaField = (TextArea) field;

                    setRegionBounds(textAreaField, templateFieldLabel);

                    // TODO: TEMPORARY.
                    //layoutY += Constants.TEMP_TABLE_HEIGHT;

                    //childrens.add(field);

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

                        tableViewField.getColumns().add(tableColumn);

                    }

                    // TODO: TEMPORARY.
                    layoutY += Constants.TEMP_TABLE_HEIGHT;

                    childrens.add(field);

                    break;

                default:
                    continue;
            }

            // TODO: Scrollable tab pane.

            childrens.add(templateFieldLabel);


            layoutY += 40;
        }

    }

    private void setRegionBounds(Region field, Text templateFieldLabel) {

        field.setLayoutX(templateFieldLabel.getLayoutX());
        field.setLayoutY(templateFieldLabel.getLayoutY() + Constants.TABLE_OFFSET_Y);
        field.setMinWidth(tabFormPane.getPrefWidth() - (2 * templateFieldLabel.getLayoutX()));

        // TODO: TEMPORARY.
        field.setMaxHeight(Constants.TEMP_TABLE_HEIGHT);
    }

    public void setAntipattern(Antipattern antipattern) {

        this.antipattern = antipattern;
    }

    public void setFieldList(List<TemplateField> fieldList) {

        this.fieldList = fieldList;
    }

}
