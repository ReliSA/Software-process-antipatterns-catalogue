package cz.zcu.kiv.spac.controllers;

import cz.zcu.kiv.spac.data.antipattern.Antipattern;
import cz.zcu.kiv.spac.data.antipattern.AntipatternRelation;
import cz.zcu.kiv.spac.data.antipattern.heading.AntipatternHeading;
import cz.zcu.kiv.spac.data.antipattern.heading.AntipatternTextHeading;
import cz.zcu.kiv.spac.data.antipattern.label.AntipatternLabel;
import cz.zcu.kiv.spac.data.graph.GraphGenerator;
import cz.zcu.kiv.spac.markdown.MarkdownParser;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.net.URL;
import java.util.*;

public class DotGraphGeneratorController implements Initializable {

    private static Logger log = LogManager.getLogger(DotGraphGeneratorController.class);

    @FXML
    public CheckBox chckSelectAll;
    @FXML
    public TreeView<Antipattern> tvAntipatterns;
    @FXML
    public Button btnSelectReleated;

    @FXML
    public Label lblSelectedCount;

    @FXML
    public Button btnSave;
    @FXML
    public Button btnBack;

    private List<AntipatternLabel> labels;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tvAntipatterns.setShowRoot(false);
        tvAntipatterns.setCellFactory(TextFieldTreeCell.forTreeView(new StringConverter<>() {
            @Override
            public String toString(Antipattern antipattern) {
                return antipattern.getName();
            }

            @Override
            public Antipattern fromString(String s) {
                return null;
            }
        }));

        tvAntipatterns.getSelectionModel().getSelectedItems().addListener((ListChangeListener<TreeItem<Antipattern>>) c -> {
            int allSize = tvAntipatterns.getRoot().getChildren().size();
            int selectedSize = tvAntipatterns.getSelectionModel().getSelectedItems().size();
            chckSelectAll.setSelected(allSize == selectedSize);
            chckSelectAll.setIndeterminate(0 < selectedSize && selectedSize < allSize);
            btnSelectReleated.setDisable(0 == selectedSize);
            btnSave.setDisable(0 == selectedSize);
            lblSelectedCount.setText(Integer.toString(selectedSize));
        });

        tvAntipatterns.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    public void setAntipatterns(Collection<Antipattern> antipatterns) {
        TreeItem<Antipattern> root = new TreeItem<>();

        for (AntipatternLabel label : labels) {
            TreeItem<Antipattern> labelItem = new TreeItem<>(new Antipattern(label.getName(), null, ""));
            root.getChildren().add(labelItem);
        }

        TreeItem<Antipattern> allItem = new TreeItem<>(new Antipattern("All", null, ""));
        for (Antipattern antipattern : antipatterns) {
            TreeItem<Antipattern> antipatternItem = new TreeItem<>(antipattern);
            allItem.getChildren().add(antipatternItem);
            AntipatternHeading heading = antipattern.getAntipatternHeading("labels");
            if (heading != null) {
                List<AntipatternLabel> usedLabels = MarkdownParser.parseUsedLabels(((AntipatternTextHeading) heading).getValue());
                for (AntipatternLabel label : usedLabels) {
                    for (TreeItem<Antipattern> child : root.getChildren()) {
                        if (child.getValue().getName().equals(label.getName())) {
                            child.getChildren().add(antipatternItem);
                        }
                    }
                }
            }
        }
        root.getChildren().add(allItem);

        tvAntipatterns.setRoot(root);
    }

    public void setLabels(List<AntipatternLabel> labels) {
        this.labels = labels;
    }

    public void btnSaveAction(ActionEvent actionEvent) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save");
        chooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Dot files", "*.dot"),
                new FileChooser.ExtensionFilter("All files", "*.*"));
        File file = chooser.showSaveDialog(tvAntipatterns.getScene().getWindow());
        if (file != null) {
            Set<Antipattern> selectedAntipatterns = new HashSet<>();
            for (TreeItem<Antipattern> antipatternItem : tvAntipatterns.getSelectionModel().getSelectedItems()) {
                selectedAntipatterns.add(antipatternItem.getValue());
            }
            GraphGenerator generator = new GraphGenerator(selectedAntipatterns);
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
        for (TreeItem<Antipattern> antipatternItem : tvAntipatterns.getSelectionModel().getSelectedItems()) {
            Set<AntipatternRelation> relations = antipatternItem.getValue().getRelations();
            if (relations != null) {
                relations.forEach(at -> {
                    tvAntipatterns.getRoot().getChildren().forEach(antipattern1 -> {
                        if (antipattern1.getValue().getName().equals(at.getAntipattern())) {
                            int index = tvAntipatterns.getRoot().getChildren().indexOf(antipattern1);
                            if (index >= 0) {
                                tvAntipatterns.getSelectionModel().select(index);
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
        // TODO:
        if (chckSelectAll.isSelected()) {
            tvAntipatterns.getSelectionModel().selectAll();
        } else {
            tvAntipatterns.getSelectionModel().clearSelection();
        }
    }
}
