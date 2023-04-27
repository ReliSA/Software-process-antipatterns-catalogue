package cz.zcu.kiv.spac.controllers;

import cz.zcu.kiv.spac.data.antipattern.Antipattern;
import cz.zcu.kiv.spac.data.antipattern.AntipatternRelation;
import cz.zcu.kiv.spac.data.antipattern.heading.AntipatternHeading;
import cz.zcu.kiv.spac.data.antipattern.heading.AntipatternTextHeading;
import cz.zcu.kiv.spac.data.antipattern.label.AntipatternLabel;
import cz.zcu.kiv.spac.data.graph.GraphGenerator;
import cz.zcu.kiv.spac.markdown.MarkdownParser;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
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

    private Set<String> selectedAntipatterns;

    private boolean selecting = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        selectedAntipatterns = new HashSet<>();
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

        tvAntipatterns.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<Antipattern>>() {
            @Override
            public void changed(ObservableValue<? extends TreeItem<Antipattern>> observable, TreeItem<Antipattern> oldValue, TreeItem<Antipattern> newValue) {
                System.out.println("old: " + oldValue);
                System.out.println("new: " + newValue);
            }
        });

        tvAntipatterns.getSelectionModel().getSelectedItems().addListener((ListChangeListener<TreeItem<Antipattern>>) c -> {
            ObservableList<TreeItem<Antipattern>> selected = tvAntipatterns.getSelectionModel().getSelectedItems();
            selected.forEach(treeItem -> selectedAntipatterns.add(treeItem.getValue().getName()));
            List<TreeItem<Antipattern>> rootChildren = tvAntipatterns.getRoot().getChildren();
            for (TreeItem<Antipattern> item : rootChildren) {
                if (selected.contains(item)) {
                    item.getChildren().forEach(antipatternItem -> tvAntipatterns.getSelectionModel().select(antipatternItem));
                }
            }
            btnSelectReleated.setDisable(selectedAntipatterns.isEmpty());
            btnSave.setDisable(selectedAntipatterns.isEmpty());
            lblSelectedCount.setText(Integer.toString(selectedAntipatterns.size()));

            if (!selecting) {
                while (c.next()) {
                    if (c.wasRemoved()) {
                        c.getRemoved().forEach(this::deselectInOtherBranches);
                        tvAntipatterns.getSelectionModel().clearSelection(tvAntipatterns.getSelectionModel().getSelectedIndex());
                    } else if (c.wasAdded()) {
                        c.getList().forEach(item -> {
                            if (!tvAntipatterns.getRoot().equals(item.getParent())) {
                                selectInOtherBranches(item);
                            }
                        });
                    }
                }
            }
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
                            antipatternItem = new TreeItem<>(antipattern);
                            child.getChildren().add(antipatternItem);
                        }
                    }
                }
            }
        }
        root.getChildren().add(allItem);

        tvAntipatterns.setRoot(root);
    }

    private void deselectInOtherBranches(TreeItem<Antipattern> deselectedItem) {/*
        Antipattern deselectedItemValue = deselectedItem.getValue();

        tvAntipatterns.getSelectionModel().getSelectedItems().forEach(item -> {
            if (item.getValue() == deselectedItemValue) {
                tvAntipatterns.getSelectionModel().clearSelection(tvAntipatterns.getRow(item));
            }
        });*/
    }

    private void selectInOtherBranches(TreeItem<Antipattern> selectedItem) {
        selecting = true;
        TreeItem<Antipattern> parent = selectedItem.getParent();
        parent.getParent().getChildren().forEach(item -> {
            int itemIndex = -1;
            for (int i = 0; i < item.getChildren().size(); i++) {
                TreeItem<Antipattern> childItem = item.getChildren().get(i);
                if (childItem.getValue().equals(selectedItem.getValue())) {
                    itemIndex = i;
                    break;
                }
            }
            if (itemIndex > -1) {
                TreeItem<Antipattern> toSelect = item.getChildren().get(itemIndex);
                tvAntipatterns.getSelectionModel().select(toSelect);
            }
        });

        selecting = false;
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
                    int lastIndex = tvAntipatterns.getRoot().getChildren().size() - 1;
                    tvAntipatterns.getRoot().getChildren().get(lastIndex).getChildren().forEach(relatedAntipattern -> {
                        if (at.getAntipattern().contains("[" + relatedAntipattern.getValue().getName() + "]")) {
                            int index = tvAntipatterns.getRoot().getChildren().get(lastIndex).getChildren().indexOf(relatedAntipattern);
                            if (index >= 0) {
                                tvAntipatterns.getSelectionModel().select(relatedAntipattern);
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
}
