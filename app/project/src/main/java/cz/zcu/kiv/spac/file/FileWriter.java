package cz.zcu.kiv.spac.file;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

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

            PrintWriter writer;
            writer = new PrintWriter(file);
            writer.println(content);
            writer.close();
            return true;

        } catch (IOException ex) {

            log.error("Error while writing content to file '" + file.getName() + "'");
            return false;
        }
    }
}
