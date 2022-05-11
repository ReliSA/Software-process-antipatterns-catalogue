package cz.zcu.kiv.spac.controllers;

import cz.zcu.kiv.spac.data.antipattern.Antipattern;
import cz.zcu.kiv.spac.data.antipattern.AntipatternRelation;
import cz.zcu.kiv.spac.data.graph.GraphGenerator;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.ResourceBundle;
import java.util.Set;

public class DotGraphGeneratorController implements Initializable {

    private static Logger log = LogManager.getLogger(DotGraphGeneratorController.class);

    @FXML
    public CheckBox chckSelectAll;
    @FXML
    public ListView<Antipattern> lvAntipatterns;
    @FXML
    public Button btnSelectReleated;

    @FXML
    public Label lblSelectedCount;

    @FXML
    public Button btnSave;
    @FXML
    public Button btnBack;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        lvAntipatterns.setCellFactory(TextFieldListCell.forListView(new StringConverter<>() {
            @Override
            public String toString(Antipattern antipattern) {
                return antipattern.getName();
            }

            @Override
            public Antipattern fromString(String s) {
                return null;
            }
        }));

        lvAntipatterns.getSelectionModel().getSelectedItems().addListener((ListChangeListener<Antipattern>) c -> {
            int allSize = lvAntipatterns.getItems().size();
            int selectedSize = lvAntipatterns.getSelectionModel().getSelectedItems().size();
            chckSelectAll.setSelected(allSize == selectedSize);
            chckSelectAll.setIndeterminate(0 < selectedSize && selectedSize < allSize);
            btnSelectReleated.setDisable(0 == selectedSize);
            btnSave.setDisable(0 == selectedSize);
            lblSelectedCount.setText(Integer.toString(selectedSize));
        });

        lvAntipatterns.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    public void setAntipatterns(Collection<Antipattern> antipatterns) {
        this.lvAntipatterns.getItems().setAll(antipatterns);
    }

    public void btnSaveAction(ActionEvent actionEvent) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save");
        chooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Dot files", "*.dot"),
                new FileChooser.ExtensionFilter("All files", "*.*"));
        File file = chooser.showSaveDialog(lvAntipatterns.getScene().getWindow());
        if (file != null) {
            GraphGenerator generator = new GraphGenerator(lvAntipatterns.getSelectionModel().getSelectedItems());
            generator.generateDotGraph(file.getName().substring(0, file.getName().indexOf('.')), file);

            showInfoAlert();
        }
    }

    private void showInfoAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle("Save");
        alert.setHeaderText("DOT graph saved");
        alert.setContentText("DOT graph has been successfully saved");

        alert.showAndWait();
    }

    public void btnSelectRelatedAction(ActionEvent actionEvent) {
        for (Antipattern antipattern : lvAntipatterns.getSelectionModel().getSelectedItems()) {
            Set<AntipatternRelation> relations = antipattern.getRelations();
            if (relations != null) {
                relations.forEach(at -> {
                    lvAntipatterns.getItems().forEach(antipattern1 -> {
                        if (antipattern1.getName().equals(at.getAntipattern())) {
                            int index = lvAntipatterns.getItems().indexOf(antipattern1);
                            if (index >= 0) {
                                lvAntipatterns.getSelectionModel().select(index);
                            }
                        }
                    });
                });
            }
        }
    }

    public void btnBackAction(ActionEvent actionEvent) {
        ((Stage) btnBack.getScene().getWindow()).close();
    }

    public void chckSelectAllAction(ActionEvent actionEvent) {
        if (chckSelectAll.isSelected()) {
            lvAntipatterns.getSelectionModel().selectAll();
        } else {
            lvAntipatterns.getSelectionModel().clearSelection();
        }
    }
}
