package cz.zcu.kiv.spac.utils;

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

    public static String getFilenameFromStringPath(String path) {

        Path p = Paths.get(path);
        return p.getFileName().toString();
    }
}
