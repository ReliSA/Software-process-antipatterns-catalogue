package cz.zcu.kiv.spac.controllers;

import cz.zcu.kiv.spac.data.Constants;
import cz.zcu.kiv.spac.data.template.Template;
import cz.zcu.kiv.spac.data.template.TemplateField;
import cz.zcu.kiv.spac.data.template.TemplateFieldType;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class TemplateManagerWindowController implements Initializable {

    private static Logger log = LogManager.getLogger(TemplateManagerWindowController.class);
    @FXML
    Button btnMoveUp;
    @FXML
    Button btnMoveDown;
    @FXML
    Button btnAdd;
    @FXML
    Button btnEdit;
    @FXML
    Button btnRemove;
    @FXML
    TableView<TemplateField> tvFields;
    @FXML
    Button btnDone;

    private Template template;

    private boolean saveChanges = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        TableColumn<TemplateField, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(
                (TableColumn.CellDataFeatures<TemplateField, String> param) -> param.getValue().nameProperty());
        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        nameColumn.setSortable(false);

        TableColumn<TemplateField, String> textColumn = new TableColumn<>("Text");
        textColumn.setCellValueFactory(
                (TableColumn.CellDataFeatures<TemplateField, String> param) -> param.getValue().textProperty());
        textColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        textColumn.setSortable(false);

        TableColumn<TemplateField, TemplateFieldType> typeColumn = new TableColumn<>("Type");
        typeColumn.setCellValueFactory(param -> param.getValue().typeProperty());
        typeColumn.setCellFactory(ComboBoxTableCell.forTableColumn(new StringConverter<>() {
            @Override
            public String toString(TemplateFieldType type) {
                return type != null ? type.getName() : null;
            }

            @Override
            public TemplateFieldType fromString(String type) {
                return type != null ? TemplateFieldType.valueOf(type) : null;
            }
        }, FXCollections.observableArrayList(TemplateFieldType.values())));
        typeColumn.setSortable(false);

        TableColumn<TemplateField, Boolean> requiredColumn = new TableColumn<>("Required");
        requiredColumn.setCellValueFactory(param -> param.getValue().requiredProperty());
        requiredColumn.setCellFactory(CheckBoxTableCell.forTableColumn(requiredColumn));
        requiredColumn.setSortable(false);

        TableColumn<TemplateField, String> defaultValueColumn = new TableColumn<>("Default value");
        defaultValueColumn.setCellValueFactory(param -> param.getValue().defaultValueProperty());
        defaultValueColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        defaultValueColumn.setSortable(false);

        TableColumn<TemplateField, String> placeHolderColumn = new TableColumn<>("Placeholder");
        placeHolderColumn.setCellValueFactory(param -> param.getValue().placeholderProperty());
        placeHolderColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        placeHolderColumn.setSortable(false);

        tvFields.getColumns().addAll(nameColumn, textColumn, typeColumn, requiredColumn, defaultValueColumn, placeHolderColumn);
        tvFields.getSelectionModel().getSelectedItems().addListener((ListChangeListener<TemplateField>) change -> {
            disableButtons(change.getList().isEmpty());

            for (TemplateField templateField : change.getList()) {
                if (templateField.getName().equals("antipattern_name") || templateField.getName().equals("known_as")) {
                    disableButtons(true);
                    break;
                } else {
                    disableButtons(false);
                }
            }
        });
        tvFields.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        tvFields.autosize();
    }

    private void disableButtons(boolean disable) {
        btnMoveUp.setDisable(disable || tvFields.getSelectionModel().getSelectedIndex() == 2);
        btnMoveDown.setDisable(disable || tvFields.getSelectionModel().getSelectedIndex() == (tvFields.getItems().size() - 1));
        btnEdit.setDisable(disable);
        btnRemove.setDisable(disable);
    }

    public void initController(Template template) {
        this.template = template;
        tvFields.setItems(FXCollections.observableArrayList(template.getFieldList()));
    }

    @FXML
    public void btnMoveUpAction(ActionEvent actionEvent) {
        moveUpDown(true);
    }

    @FXML
    public void btnMoveDownAction(ActionEvent actionEvent) {
        moveUpDown(false);
    }

    private void moveUpDown(boolean up) {
        int step = up ? 1 : -1;
        int index = tvFields.getSelectionModel().getSelectedIndex();
        TemplateField selectedItem = tvFields.getSelectionModel().getSelectedItem();
        tvFields.getItems().set(index, tvFields.getItems().get(index - step));
        tvFields.getItems().set(index - step, selectedItem);
        tvFields.getSelectionModel().select(index - step);
    }

    @FXML
    public void btnAddAction(ActionEvent actionEvent) {
        openAddEditWindow(null);
    }

    @FXML
    public void btnEditAction(ActionEvent actionEvent) {
        openAddEditWindow(tvFields.getSelectionModel().getSelectedItem());
    }

    @FXML
    public void btnRemoveAction(ActionEvent actionEvent) {
        TemplateField selected = tvFields.getSelectionModel().getSelectedItem();
        tvFields.getItems().remove(selected);
    }

    private void openAddEditWindow(TemplateField field) {
        Stage stage = new Stage();

        try {
            FXMLLoader loader =
                    new FXMLLoader(Objects.requireNonNull(getClass().getResource(Constants.RESOURCE_NEW_TEMPLATE_FIELD_WINDOW)));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/richtext/rich-text.css").toExternalForm());

            String title = "New field";
            if (field == null) {
                title = "Edit";

            }
            stage.setTitle(title);
            stage.setScene(scene);
            stage.setResizable(true);
            stage.initModality(Modality.APPLICATION_MODAL);

            TemplateFieldWindowController templateManagerWindowController = loader.getController();
            templateManagerWindowController.initField(field);

            stage.showAndWait();

            if (templateManagerWindowController.isSaveChanges()) {
                int index = template.getFieldList().indexOf(field);
                template.getFieldList().remove(field);
                if (index > -1) {
                    template.getFieldList().add(index, templateManagerWindowController.getField());
                } else {
                    template.getFieldList().add(templateManagerWindowController.getField());
                }
                tvFields.getItems().setAll(template.getFieldList());
            }
        } catch (IOException e) {
            log.error("Invalid TemplateManagerWindow scene.");
            e.printStackTrace();
        }
    }

    @FXML
    public void btnDoneAction(ActionEvent actionEvent) {
        ((Stage) tvFields.getScene().getWindow()).close();
    }

    public Template getTemplate() {
        template.getFieldList().clear();
        template.getFieldList().addAll(tvFields.getItems());
        return template;
    }
}
