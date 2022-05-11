package cz.zcu.kiv.spac.controllers;

import cz.zcu.kiv.spac.data.Constants;
import cz.zcu.kiv.spac.data.antipattern.label.AntipatternLabel;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.ResourceBundle;

import static cz.zcu.kiv.spac.data.Constants.APP_NAME;

public class LabelsManagerWindowController implements Initializable {

    private static Logger log = LogManager.getLogger(LabelsManagerWindowController.class);

    private static Random random = new Random();

    @FXML
    public Button btnNewReference;
    @FXML
    public ListView<AntipatternLabel> lvLabels;
    @FXML
    public Button btnDone;

    private List<AntipatternLabel> labels;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lvLabels.setCellFactory(CheckBoxListCell.forListView(AntipatternLabel::selectedProperty, new StringConverter<>() {
            @Override
            public String toString(AntipatternLabel label) {
                return label.getName();
            }

            @Override
            public AntipatternLabel fromString(String string) {
                return null;
            }
        }));
    }

    public void initLabels(List<AntipatternLabel> labels) {
        this.labels = labels;
        lvLabels.setItems(FXCollections.observableArrayList(labels));
    }

    public void btnNewLabelAction(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource(Constants.RESOURCE_NEW_LABEL_WINDOW)));
            Parent root = loader.load();

            Stage stage = new Stage();

            // Create new antipattern window controller and set values.
            NewLabelWindowController newReferenceWindowController = loader.getController();

            // Set stage.
            stage.setTitle(APP_NAME);
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            String name = newReferenceWindowController.getLabelName();
            Color color = Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));
            boolean colorUsed = true;
            while (colorUsed) {
                for (AntipatternLabel label : labels) {
                    if (!label.getColor().equals(color)) {
                        colorUsed = false;
                        break;
                    }
                    color = new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256), 255);
                }
            }

            labels.add(new AntipatternLabel(name, color));
            lvLabels.setItems(FXCollections.observableArrayList(labels));
        } catch (Exception e) {
            log.error("Invalid NewLabelWindowController scene.");
        }
    }

    private void close() {
        Stage stage = (Stage) btnDone.getScene().getWindow();
        stage.close();
    }

    public void btnDoneAction(ActionEvent actionEvent) {
        close();
    }

    public List<AntipatternLabel> getLabels() {
        return this.labels;
    }
}
