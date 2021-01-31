package cz.zcu.kiv.spac.utils;

import cz.zcu.kiv.spac.data.Constants;
import cz.zcu.kiv.spac.data.git.PreviewFileContentLine;
import org.apache.commons.io.FilenameUtils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Stream;

/**
 * Class with utils methods.
 */
public class Utils {

    /**
     * Get root dir of application.
     * @return Root dir in string.
     */
    public static String getRootDir() {

        return Paths.get(".").normalize().toAbsolutePath().toString();
    }

    /**
     * Get file name from path in string format.
     * @param path - Path in string.
     * @return File name.
     */
    public static String getFilenameFromStringPath(String path) {

        Path p = Paths.get(path);
        return p.getFileName().toString();
    }

    /**
     * Format antipattern filename into antipattern name for comparing.
     * @param filename - Antipattern name as filename.
     * @return Formatted antipattern name.
     */
    public static String formatAntipatternName(String filename) {

        String antipatternName = FilenameUtils.removeExtension(filename);
        antipatternName = antipatternName.replace("_", " ");
        antipatternName = antipatternName.replace("'", "").replace("’", "");

        return antipatternName;
    }

    /**
     * Compare two antipatterns by their names.
     * @param antipatternName1 - First antipattern name.
     * @param antipatternName2 - Second antipattern name.
     * @return True if both names are same, false if not.
     */
    public static boolean isAntipatternNamesEquals(String antipatternName1, String antipatternName2) {

        antipatternName1 = antipatternName1.replace("'", "").replace("’", "").replace("_", " ");;
        antipatternName2 = antipatternName2.replace("'", "").replace("’", "").replace("_", " ");;

        return antipatternName1.equalsIgnoreCase(antipatternName2);
    }

    /**
     * Replace line breakers with html '<br>' tag.
     * @param content - String with line breakers.
     * @return String with html line breakers.
     */
    public static String replaceLineBreakersForHTMLNewLine(String content) {

        String newContent = content.replaceAll(Constants.LINE_BREAKER_CRLF, "<br>");
        newContent = newContent.replaceAll(Constants.LINE_BREAKER_LF, "<br>");
        return newContent;
    }

    /**
     * Parse string into list of lines.
     * @param content - String.
     * @return List of lines.
     */
    public static List<String> parseStringByLines(String content) {

        String[] arrayLines = content.lines().toArray(String[]::new);
        return Arrays.asList(arrayLines);
    }

    /**
     * Get current date in string specific format.
     * @return - Date in string.
     */
    public static String getCurrentDateInString() {

        return new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(Calendar.getInstance().getTime());
    }
}
