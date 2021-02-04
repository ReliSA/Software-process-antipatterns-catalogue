package cz.zcu.kiv.spac.utils;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.jgit.diff.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

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

    /**
     * Get differences between two file contents (used for comparing file in 2 revisions)
     * @param content1 - Content of file from previous commit.
     * @param content2 - Current content file.
     * @return Differences in git diff command output format.
     */
    public static String getFilesDifference(String content1, String content2) {

        OutputStream out = new ByteArrayOutputStream();

        try {

            RawText rt1 = new RawText(content1.getBytes());
            RawText rt2 = new RawText(content2.getBytes());
            EditList diffList = new EditList();
            diffList.addAll(new HistogramDiff().diff(RawTextComparator.WS_IGNORE_ALL, rt1, rt2));
            new DiffFormatter(out).format(diffList, rt1, rt2);

        } catch (IOException e) {

            e.printStackTrace();
        }

        return out.toString();
    }
}
