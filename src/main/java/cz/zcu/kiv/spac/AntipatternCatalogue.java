package cz.zcu.kiv.spac;

import cz.zcu.kiv.spac.data.Constants;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.util.Objects;

public class AntipatternCatalogue extends Application {

    // Logger.
    private static Logger log = Logger.getLogger(AntipatternCatalogue.class);

    public static void main(String[] args) {

        configureLogger();
        launch(args);
    }

    @Override
    public void start(Stage stage) {

        log.info("Starting Antipattern Catalogue.");

        try {

            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(Constants.RESOURCE_MAIN_WINDOW)));
            stage.setTitle(Constants.APP_NAME);
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();

        } catch (Exception e) {

            log.error("Invalid MainWindowController scene.");
            System.exit(1);
        }
    }

    private static void configureLogger() {

        BasicConfigurator.resetConfiguration();
        BasicConfigurator.configure();

        Logger.getRootLogger().setLevel(Level.INFO);
    }
}
