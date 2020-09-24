package cz.zcu.kiv.spac.controllers;

import cz.zcu.kiv.spac.data.Constants;
import cz.zcu.kiv.spac.data.antipattern.Antipattern;
import cz.zcu.kiv.spac.file.FileLoader;
import cz.zcu.kiv.spac.markdown.MarkdownParser;
import cz.zcu.kiv.spac.template.Template;
import cz.zcu.kiv.spac.utils.Utils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import java.util.Map;
import java.util.Objects;

public class MainWindowController {

    // FXML elements.
    @FXML
    private ListView<String> listAntipatterns;

    @FXML
    private TextField txtFieldAPSearch;

    @FXML
    private WebView wviewAntipatternPreview;


    // App variables.
    private MarkdownParser markdownParser;
    private Template template;
    private Map<String, Antipattern> antipatterns;


    // Logger.
    private static Logger log = Logger.getLogger(MainWindowController.class);

    public MainWindowController() {

    }

    @FXML
    public void initialize() {

        template = FileLoader.loadConfiguration(Utils.getRootDir() + "/" + Constants.CONFIGURATION_NAME);

        markdownParser = new MarkdownParser(template);

        antipatterns = FileLoader.loadAntipatterns(markdownParser, Utils.getRootDir() + "/" + Constants.CATALOGUE_FOLDER);

        for (String aPatternName : antipatterns.keySet()) {

            listAntipatterns.getItems().add(aPatternName);
        }

        // Set css styles.
        wviewAntipatternPreview.getEngine().setUserStyleSheetLocation(getClass().getResource(Constants.RESOURCE_PREVIEW_CSS).toString());

    }

    @FXML
    private void filterAntipatterns() {

        String searchText = txtFieldAPSearch.getText();

        if (searchText != null) {

            listAntipatterns.getItems().clear();

            for (String aPatternName : antipatterns.keySet()) {

                if (aPatternName.toLowerCase().contains(searchText.toLowerCase())) {

                    listAntipatterns.getItems().add(FilenameUtils.removeExtension(aPatternName));
                }
            }

            log.info("Searched phrase \"" +  searchText + "\", found " + listAntipatterns.getItems().size() + " antipatterns");
        }
    }

    @FXML
    private void antipatternSelected(MouseEvent mouseEvent) {

        String item = listAntipatterns.getSelectionModel().getSelectedItem();

        if (item == null) {

            mouseEvent.consume();
            return;
        }

        Antipattern selectedAntipattern = antipatterns.get(item);

        if (selectedAntipattern == null) {

            mouseEvent.consume();
            return;
        }

        if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {

            if (mouseEvent.getClickCount() == 1) {

                wviewAntipatternPreview.getEngine().loadContent(markdownParser.generateHTMLContent(selectedAntipattern.getMarkdownContent()));

            } else if (mouseEvent.getClickCount() == 2) {

                openAntipatternWindow(selectedAntipattern);
            }
        }
    }

    @FXML
    private void menuExitAction() {

        System.exit(0);
    }

    @FXML
    private void menuNewAPAction() {

        openAntipatternWindow();
    }

    private void openAntipatternWindow() {

        openAntipatternWindow(null);
    }

    private void openAntipatternWindow(Antipattern antipattern) {

        try {

            Stage stage = new Stage();

            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource(Constants.RESOURCE_ANTIPATTERN_WINDOW)));
            Parent root = loader.load();

            AntipatternWindowController antipatternWindowController = loader.getController();
            antipatternWindowController.setAntipattern(antipattern);
            antipatternWindowController.setTemplate(template);
            antipatternWindowController.setMarkdownParser(markdownParser);
            antipatternWindowController.loadAntipatternInfo();

            String stageTitle = Constants.APP_NAME;

            if (antipattern == null) {

                stageTitle += " - New Antipattern";

            } else {

                stageTitle += " - Edit Antipattern (" + antipattern.getName() + ")";
            }

            stage.setTitle(stageTitle);
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();

        } catch (Exception e) {

            log.error("Invalid AntipatternWindowController scene.");
        }
    }
}
