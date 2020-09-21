package cz.zcu.kiv.spac.utils;

import java.nio.file.Paths;

public class Utils {

    public static String getRootDir() {

        return Paths.get(".").normalize().toAbsolutePath().toString();
    }
}
