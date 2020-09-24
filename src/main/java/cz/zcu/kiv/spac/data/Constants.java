package cz.zcu.kiv.spac.data;

import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Constants {

    public static final String APP_NAME = "Antipattern Catalogue";
    public static final String CATALOGUE_FOLDER = "catalogue";
    public static final String CONFIGURATION_NAME = "config.xml";
    public static final List<String> TEMPLATE_FILES = new ArrayList<>(Arrays.asList("template", "template-terms"));

    public static final int FIELD_OFFSET_X = 10;
    public static final int FIELD_OFFSET_Y = 40;
    public static final int INIT_Y_LOCATION = 20;
    public static final int TEXTFIELD_OFFSET_Y = 15;
    public static final int TABLE_OFFSET_Y = 10;
    public static final int TEXTAREA_HEIGHT = 100;
    public static final int TABLE_HEIGHT = 200;

    public static final Font NEW_AP_LABEL_FONT = Font.font("Arial", FontWeight.BOLD, 16);

    public static final String RESOURCE_ANTIPATTERN_WINDOW = "/windows/AntipatternWindow.fxml";
    public static final String RESOURCE_MAIN_WINDOW = "/windows/MainWindow.fxml";
    public static final String RESOURCE_PREVIEW_CSS = "/css/antipatternPreview.css";

    public static final String CATALOGUE_NAME = "../Antipatterns_catalogue.md";
    public static final String README_NAME = "../README.md";
}
