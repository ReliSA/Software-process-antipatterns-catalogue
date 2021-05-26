package cz.zcu.kiv.spac.file;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Class contains static methods for writing content to files.
 */
public class FileWriter {

    // Logger.
    private static Logger log = LogManager.getLogger(FileWriter.class);

    /**
     * Write content to file.
     * @param file - File.
     * @param content - Content.
     * @return True if writing into file was successful, false if not.
     */
    public static boolean write(File file, String content) {

        try {

            Writer out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(file), StandardCharsets.UTF_8));
            out.write(content + "\n");
            out.close();
            return true;

        } catch (IOException ex) {

            log.error("Error while writing content to file '" + file.getName() + "'");
            return false;
        }
    }
}
