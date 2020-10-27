package cz.zcu.kiv.spac.controllers;

import cz.zcu.kiv.spac.data.catalogue.Catalogue;
import cz.zcu.kiv.spac.data.Constants;
import cz.zcu.kiv.spac.data.antipattern.Antipattern;
import cz.zcu.kiv.spac.data.catalogue.CatalogueRecord;
import cz.zcu.kiv.spac.file.FileLoader;
import cz.zcu.kiv.spac.file.FileWriter;
import cz.zcu.kiv.spac.markdown.MarkdownFormatter;
import cz.zcu.kiv.spac.markdown.MarkdownParser;
import cz.zcu.kiv.spac.template.Template;
import cz.zcu.kiv.spac.utils.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Controller for Main window.
 */
public class MainWindowController {

    // FXML elements.
    @FXML
    private ListView<String> listAntipatterns;

    @FXML
    private TextField txtFieldAPSearch;

    @FXML
    private WebView wviewAntipatternPreview;

    @FXML
    private CheckBox chckBoxFilterNonCreated;

    // App variables.
    private MarkdownParser markdownParser;
    private Template template;
    private Map<String, Antipattern> antipatterns;
    private Catalogue catalogue;

    // Logger.
    private static Logger log = Logger.getLogger(MainWindowController.class);

    /**
     * Constructor.
     */
    public MainWindowController() {

    }

    @FXML
    public void initialize() {

        // Load configuration.
        template = FileLoader.loadConfiguration(Utils.getRootDir() + "/" + Constants.CONFIGURATION_NAME);

        // If configuration was not loaded correctly.
        if (template == null) {

            log.error("Template file '" + Constants.CATALOGUE_FILE + "' does not exists or it has bad markdown format!");
            System.exit(1);
        }

        // Create new markdown parser.
        markdownParser = new MarkdownParser(template);

        // Get catalogue markdown content.
        String catalogueContent = FileLoader.loadFileContent(Utils.getRootDir() + "/" + Constants.CATALOGUE_FILE);

        // If catalogue file was not loaded correctly.
        if (catalogueContent == null) {

            log.error("Catalogue file '" + Constants.CATALOGUE_FILE + "' does not exists !");
            System.exit(1);
        }

        // Parse catalogue content.
        catalogue = markdownParser.parseCatalogue(catalogueContent);

        // If catalogue content is not correctly writed.
        if (catalogue == null) {

            log.error("Catalogue file has bad markdown format.");
            System.exit(1);
        }

        // Load all antipatterns from catalogue folder.
        antipatterns = FileLoader.loadAntipatterns(markdownParser, catalogue);

        // Add every antipattern to antipattern list element.
        fillAntipatternList();

        // Set css styles for preview.
        wviewAntipatternPreview.getEngine().setUserStyleSheetLocation(getClass().getResource(Constants.RESOURCE_PREVIEW_CSS).toString());

        // Set tooltip for checkbox for filtering noncreated antipatterns.
        chckBoxFilterNonCreated.setTooltip(new Tooltip("If selected, then all antipatterns, which are not created (does not have file in catalogue folder), will be filtered."));
    }

    /**
     * Filter antipattern event for searchbox.
     * Get all antipatterns which matches writed substring.
     */
    @FXML
    private void filterAntipatterns() {

        String searchText = txtFieldAPSearch.getText();

        if (searchText != null) {

            listAntipatterns.getItems().clear();

            for (String aPatternName : antipatterns.keySet()) {

                Antipattern antipattern = antipatterns.get(aPatternName);
                String name = antipattern.getName();

                if (name.toLowerCase().contains(searchText.toLowerCase())) {

                    // Add antipattern to list.
                    listAntipatterns.getItems().add(prepareAntipatternName(antipattern));
                }
            }
        }
    }

    /**
     * Select event for antipattern list.
     * 1 click shows antipattern preview.
     * 2 clicks opens editing window for antipattern.
     * @param mouseEvent - Mouse event.
     */
    @FXML
    private void antipatternSelected(MouseEvent mouseEvent) {

        // Get selected antipattern.
        String item = listAntipatterns.getSelectionModel().getSelectedItem();

        // If no item was selected.
        if (item == null) {

            mouseEvent.consume();
            return;
        }

        item = item.replace(Constants.ANTIPATTERN_NOT_CREATED_SYMBOL, "");

        String formattedName = Utils.formatAntipatternName(item);
        Antipattern selectedAntipattern = antipatterns.get(formattedName);

        // If no antipattern was selected.
        if (selectedAntipattern == null) {

            mouseEvent.consume();
            return;
        }

        // If antipattern was selected by left (primary) button.
        if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {

            if (mouseEvent.getClickCount() == 1) {

                // Show preview.
                wviewAntipatternPreview.getEngine().loadContent(markdownParser.generateHTMLContent(selectedAntipattern.getContent().toString()));

            } else if (mouseEvent.getClickCount() == 2) {

                // Check if selected antipattern contains all needed headings.
                List<String> missingHeadings = template.getHeadingDifferences(selectedAntipattern);

                // If there is any missing heading, open antipattern raw window, otherwise open classic antipattern window for update.
                if (missingHeadings.size() > 0 && selectedAntipattern.isCreated()) {

                    openAntipatternRawWindow(selectedAntipattern, missingHeadings);

                } else {

                    openAntipatternWindow(selectedAntipattern);
                }
            }
        }
    }

    /**
     * Exit main window action for menu item "Exit".
     */
    @FXML
    private void menuExitAction() {

        System.exit(0);
    }

    /**
     * Open antipattern window to create new AP for menu item "New AP".
     */
    @FXML
    private void menuNewAPAction() {

        openAntipatternWindow();
    }

    /**
     * Checkbox action for filtering antipatterns which are not created yet.
     * @param actionEvent - Action event.
     */
    @FXML
    private void chckBoxFilterNonCreatedClicked(ActionEvent actionEvent) {

        fillAntipatternList();
    }

    /**
     * Open antipattern window for creating new antipattern.
     */
    private void openAntipatternWindow() {

        openAntipatternWindow(null);
    }

    /**
     * Fill antipattern list element with antipatterns.
     */
    private void fillAntipatternList() {

        listAntipatterns.getItems().clear();
        for (String aPatternName : antipatterns.keySet()) {

            Antipattern antipattern = antipatterns.get(aPatternName);

            if (chckBoxFilterNonCreated.isSelected() && !antipattern.isCreated()) {

                continue;
            }

            listAntipatterns.getItems().add(prepareAntipatternName(antipattern));
        }
    }

    /**
     * Open antipattern window.
     * @param antipattern - Selected antipattern.
     */
    private void openAntipatternWindow(Antipattern antipattern) {

        try {

            String stageTitle = Constants.APP_NAME;

            // If antipattern is null, then it means that we want to create new antipattern.
            if (antipattern == null || (antipattern != null && !antipattern.isCreated())) {

                stageTitle += " - New Antipattern";

            } else {

                if (antipattern.isLinking()) {

                    displayAntipatternLinkedError(antipattern.getName());
                    return;
                }

                stageTitle += " - Edit Antipattern (" + antipattern.getName() + ")";
            }

            // Create new stage.
            Stage stage = new Stage();

            // Load antipattern window template.
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource(Constants.RESOURCE_ANTIPATTERN_WINDOW)));
            Parent root = loader.load();

            // Create new antipattern window controller and set values.
            AntipatternWindowController antipatternWindowController;
            antipatternWindowController = loader.getController();
            antipatternWindowController.setAntipattern(antipattern);
            antipatternWindowController.setTemplate(template);
            antipatternWindowController.setMarkdownParser(markdownParser);

            // Create Form tab and init values in fields.
            antipatternWindowController.loadAntipatternInfo();

            // Set stage.
            stage.setTitle(stageTitle);
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            // If new antipattern was created in form, then save this antipattern to new file and update catalogue file.
            if (antipatternWindowController.isAntipatternCreated()) {

                Antipattern createdAntipattern = antipatternWindowController.getTempAntipattern();
                addNewAntipatternToCatalogue(createdAntipattern);

                antipatterns = null;
                antipatterns = FileLoader.loadAntipatterns(markdownParser, catalogue);

                fillAntipatternList();
            }

            // If existing antipattern was updated, then replace old antipattern with newer.
            if (antipatternWindowController.isAntipatternUpdated()) {

                Antipattern updatedAntipattern = antipatternWindowController.getTempAntipattern();
                antipattern.setContent(updatedAntipattern.getContent().toString());
                antipattern.setAntipatternHeadings(markdownParser.parseHeadings(antipattern.getName(), antipattern.getContent().toString()));
            }

        } catch (Exception e) {

            log.error("Invalid AntipatternWindowController scene.");
        }
    }

    /**
     * Display alert with message for antipattern update / new window.
     * @param antipatternName - Antipattern name.
     */
    private void displayAntipatternLinkedError(String antipatternName) {

            // Create an alert.
            Alert alert = new Alert(Alert.AlertType.NONE);
            alert.setTitle(Constants.APP_NAME);
            alert.setAlertType(Alert.AlertType.ERROR);
            alert.setHeaderText("Error while opening antipattern window");
            alert.setContentText("Antipattern '" + antipatternName + "' cannot be updated, because it contains link to another antipattern.");
            alert.show();
    }

    /**
     * Open antipattern raw window.
     * @param antipattern - Selected antipattern.
     * @param missingHeadings - Missing headings in antipattern.
     */
    private void openAntipatternRawWindow(Antipattern antipattern, List<String> missingHeadings) {

        try {

            if (antipattern != null && antipattern.isLinking()) {

                displayAntipatternLinkedError(antipattern.getName());
                return;
            }

            String stageTitle = Constants.APP_NAME + " - Raw editing (" + antipattern.getName() + ")";

            // Create new stage.
            Stage stage = new Stage();

            // Load antipattern raw window template.
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource(Constants.RESOURCE_ANTIPATTERN_RAW_WINDOW)));
            Parent root = loader.load();

            // Create new antipattern window controller and set values.
            AntipatternRawWindowController antipatternRawWindowController;
            antipatternRawWindowController = loader.getController();
            antipatternRawWindowController.setTemplate(template);
            antipatternRawWindowController.setAntipattern(antipattern);
            antipatternRawWindowController.setDifferences(missingHeadings);
            antipatternRawWindowController.setParser(markdownParser);

            // Set stage.
            stage.setTitle(stageTitle);
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            // If antipattern was updated, then it means it contains all required headings.
            if (antipatternRawWindowController.isUpdated()) {

                Antipattern tempAntipattern = antipatternRawWindowController.getTempAntipattern();
                antipattern.setAntipatternHeadings(tempAntipattern.getAntipatternHeadings());
                antipattern.setContent(tempAntipattern.getContent().toString());
                antipattern.setAntipatternHeadings(markdownParser.parseHeadings(antipattern.getName(), antipattern.getContent().toString()));

                // Save content changes to file.
                FileWriter.write(new File(Utils.getRootDir() + "/" + antipattern.getPath()), antipattern.getContent().toString());
            }


        } catch (Exception e) {

            log.error("Invalid AntipatternRawWindowController scene.");
        }
    }

    /**
     * Add new antipattern to catalogue file.
     * @param newAntipattern - Created antipattern.
     */
    private void addNewAntipatternToCatalogue(Antipattern newAntipattern) {

        try {

            // Get first letter of antipattern name.
            String firstLetter = newAntipattern.getName().toUpperCase().substring(0, 1);

            // Get list of catalogue records by first letter of name.
            List<CatalogueRecord> catalogueRecords = catalogue.getCatalogueInstance(firstLetter);

            // If catalogue does not contains specific instance, then create new arraylist.
            if (catalogueRecords == null) {

                catalogueRecords = new ArrayList<>();
            }

            // Add created antipattern to list.
            catalogueRecords.add(new CatalogueRecord(newAntipattern.getName(), Constants.CATALOGUE_FOLDER + "/" + Utils.getFilenameFromStringPath(newAntipattern.getPath())));

            // Push it to catalogue.
            catalogue.addCatalogueInstance(firstLetter, catalogueRecords);

            // Sort specific instance.
            catalogue.sortCatalogueInstance(firstLetter);

            // Create new catalogue content.
            String catalogueMarkdownContent = MarkdownFormatter.createCatalogueMarkdownContent(catalogue, antipatterns);

            // Replace old catalogue content with new catalogue content.
            FileWriter.write(new File(Constants.CATALOGUE_FILE), catalogueMarkdownContent);

            log.info("New antipattern '" + newAntipattern.getName() + "' was created successfully and pushed to catalogue.");

        } catch (Exception ee) {

            log.warn("New antipattern '" + newAntipattern.getName() + "' was not added into template because of error.");
        }
    }

    /**
     * Add symbol to antipattern name if that antipattern is not created yet.
     * @param antipattern - Antipattern.
     * @return Updated antipattern name, if antipattern was not created yet.
     */
    private String prepareAntipatternName(Antipattern antipattern) {

        String item = antipattern.getName();

        if (!antipattern.isCreated()) {

            item += Constants.ANTIPATTERN_NOT_CREATED_SYMBOL;
        }

        return item;
    }
}
