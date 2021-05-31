package cz.zcu.kiv.spac;

import cz.zcu.kiv.spac.data.Constants;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

/**
 * Main application class.
 */
public class AntipatternsCatalogue extends Application {

    // Logger.
    private static Logger log = LogManager.getLogger(AntipatternsCatalogue.class);

    public static void main(String[] args) {

        launch(args);
    }

    @Override
    public void start(Stage stage) {

        log.info("Starting Antipattern Catalogue.");

        try {

            // Load main window template.
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(Constants.RESOURCE_MAIN_WINDOW)));
            stage.setTitle(Constants.APP_NAME);
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();

        } catch (Exception e) {

            e.printStackTrace();
            log.error("Invalid MainWindowController scene.");
            System.exit(1);
        }
    }
}
