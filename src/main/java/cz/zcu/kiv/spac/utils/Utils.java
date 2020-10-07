package cz.zcu.kiv.spac.utils;

import org.apache.commons.io.FilenameUtils;

import java.nio.file.Path;
import java.nio.file.Paths;

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

    public static String formatAntipatternName(String filename) {

        String antipatternName = FilenameUtils.removeExtension(filename);
        antipatternName = antipatternName.replace("_", " ");
        antipatternName = antipatternName.replace("'", "").replace("’", "");

        return antipatternName;
    }

    public static boolean isAntipatternNamesEquals(String antipatternName1, String antipatternName2) {

        antipatternName1 = antipatternName1.replace("'", "").replace("’", "");
        antipatternName2 = antipatternName2.replace("'", "").replace("’", "");;

        return antipatternName1.equals(antipatternName2);
    }
}
