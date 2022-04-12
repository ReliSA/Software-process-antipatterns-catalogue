package cz.zcu.kiv.spac.controllers;

import cz.zcu.kiv.spac.data.template.TableColumnField;
import cz.zcu.kiv.spac.data.template.TableField;
import cz.zcu.kiv.spac.data.template.TemplateField;
import cz.zcu.kiv.spac.data.template.TemplateFieldType;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class TemplateFieldWindowController implements Initializable {

    @FXML
    public TextField txtText;
    @FXML
    public TextField txtName;
    @FXML
    public TextField txtPlaceholder;
    @FXML
    public TextField txtDefaultValue;
    @FXML
    public ComboBox<TemplateFieldType> cmbType;
    @FXML
    public CheckBox chckRequired;
    @FXML
    public TableView<TableColumnField> tvColumns;
    @FXML
    public Button btnCancel;
    @FXML
    public Button btnSave;
    @FXML
    public Button btnMoveUp;
    @FXML
    public Button btnMoveDown;
    @FXML
    public Button btnAdd;
    @FXML
    public Button btnRemove;

    private boolean saveChanges = false;
    private TemplateField field;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        TableColumn<TableColumnField, String> textColumn = new TableColumn<>("Text");
        textColumn.setCellValueFactory(new PropertyValueFactory<>("text"));
        textColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        TableColumn<TableColumnField, String> defaultValueColumn = new TableColumn<>("Default value");
        defaultValueColumn.setCellValueFactory(new PropertyValueFactory<>("defaultValue"));
        defaultValueColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        tvColumns.getColumns().addAll(textColumn, defaultValueColumn);

        cmbType.setItems(FXCollections.observableArrayList(TemplateFieldType.values()));
        cmbType.valueProperty().addListener((observable, oldValue, newValue) -> btnAdd.setDisable(newValue != TemplateFieldType.TABLE));

        btnAdd.setDisable(cmbType.getSelectionModel().getSelectedItem() != TemplateFieldType.TABLE);
        tvColumns.getSelectionModel().getSelectedItems()
                .addListener((ListChangeListener<TableColumnField>) c -> disableButtons(c.getList().isEmpty()));
        tvColumns.setEditable(true);
    }

    private void disableButtons(boolean disable) {
        btnAdd.setDisable(cmbType.getSelectionModel().getSelectedItem() != TemplateFieldType.TABLE);
        btnMoveUp.setDisable(disable);
        btnMoveDown.setDisable(disable);
        btnRemove.setDisable(disable);
    }

    private void initColumnsTable() {
        if (field != null && field.getType() == TemplateFieldType.TABLE) {
            tvColumns.setDisable(false);
            btnAdd.setDisable(false);
            tvColumns.getItems().addAll(((TableField) field).getColumns());
        } else {
            tvColumns.setDisable(true);
            disableButtons(true);
        }
    }

    public void initField(TemplateField field) {
        this.field = field;

        if (this.field != null) {
            this.txtName.setText(this.field.getName());
            this.txtText.setText(this.field.getText());
            this.cmbType.setValue(this.field.getType());
            this.chckRequired.setSelected(this.field.isRequired());
            this.txtDefaultValue.setText(this.field.getDefaultValue());
            this.txtPlaceholder.setText(this.field.getPlaceholder());

            initColumnsTable();
        }
    }

    private void close() {
        ((Stage) tvColumns.getScene().getWindow()).close();
    }

    public void btnCancelAction(ActionEvent actionEvent) {
        saveChanges = false;
        close();
    }

    public void btnSaveAction(ActionEvent actionEvent) {
        saveChanges = true;
        close();
    }

    public boolean isSaveChanges() {
        return saveChanges;
    }

    public void btnAddAction(ActionEvent actionEvent) {
        tvColumns.getItems().add(tvColumns.getItems().size(), new TableColumnField("", ""));
        tvColumns.edit(tvColumns.getItems().size() - 1, tvColumns.getColumns().get(0));
    }

    public void btnRemoveAction(ActionEvent actionEvent) {
        tvColumns.getItems().remove(tvColumns.getSelectionModel().getSelectedItem());
    }

    public void btnMoveDownAction(ActionEvent actionEvent) {
        moveUpDown(false);
    }

    public void btnMoveUpAction(ActionEvent actionEvent) {
        moveUpDown(true);
    }

    private void moveUpDown(boolean up) {
        int step = up ? 1 : -1;
        int index = tvColumns.getSelectionModel().getSelectedIndex();
        TableColumnField selectedItem = tvColumns.getSelectionModel().getSelectedItem();
        tvColumns.getItems().set(index, tvColumns.getItems().get(index - step));
        tvColumns.getItems().set(index - step, selectedItem);
        tvColumns.getSelectionModel().select(index - step);
    }

    public TemplateField getField() {
        if (field == null) {
            field = createField();
        } else {
            field.setText(txtText.getText());
            field.setName(txtName.getText());
            field.setType(cmbType.getSelectionModel().getSelectedItem());
            field.setRequired(chckRequired.isSelected());
            field.setDefaultValue(txtDefaultValue.getText());
            field.setPlaceholder(txtPlaceholder.getText());
        }
        if (field instanceof TableField) {
            ((TableField) field).setColumns(tvColumns.getItems());
        }

        return field;
    }

    private TemplateField createField() {
        TemplateField field;
        if (cmbType.getSelectionModel().getSelectedItem() == TemplateFieldType.TABLE) {
            field = new TableField(txtName.getText(), txtText.getText(), cmbType.getSelectionModel().getSelectedItem(),
                    chckRequired.isSelected());
        } else {
            field = new TemplateField(txtName.getText(), txtText.getText(), cmbType.getSelectionModel().getSelectedItem(),
                    chckRequired.isSelected(), txtDefaultValue.getText(), txtPlaceholder.getText());
        }
        if (field instanceof TableField) {
            ((TableField) field).setColumns(tvColumns.getItems());
        }

        return field;
    }
}
