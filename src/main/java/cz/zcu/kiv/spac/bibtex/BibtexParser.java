package cz.zcu.kiv.spac.bibtex;

import cz.zcu.kiv.spac.controllers.AntipatternWindowController;
import org.apache.log4j.Logger;
import org.jbibtex.*;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;

/**
 * Class which contains methods using JBibtex parser.
 */
public class BibtexParser {

    // Logger.
    private static Logger log = Logger.getLogger(BibtexParser.class);

    /**
     * Parse bibtex references..
     * @param file - References.
     * @return BibtexDatabase object
     */
    public static BibTeXDatabase parseBibTeX(File file)  {

        Reader reader = null;

        try {

            reader = new FileReader(file);
            BibTeXParser parser = new BibTeXParser(){

                @Override
                public void checkStringResolution(Key key, BibTeXString string){

                    if(string == null){

                        log.warn("Unresolved string: \"" + key.getValue() + "\"");
                    }
                }

                @Override
                public void checkCrossReferenceResolution(Key key, BibTeXEntry entry){

                    if(entry == null){

                       log.warn("Unresolved cross-reference: \"" + key.getValue() + "\"");
                    }
                }
            };

            return parser.parse(reader);

        } catch (Exception e) {

            log.warn("Error while parsing bibtex file: " + e.toString());

        } finally {

            // Close reader if still opened.
            try {

                reader.close();

            } catch (Exception e) {

                // Do nothing.
            }
        }

        return null;
    }
}
