package cz.zcu.kiv.spac.markdown;

import cz.zcu.kiv.spac.data.Constants;
import cz.zcu.kiv.spac.data.antipattern.Antipattern;
import cz.zcu.kiv.spac.data.antipattern.AntipatternRelation;
import cz.zcu.kiv.spac.data.antipattern.heading.AntipatternHeading;
import cz.zcu.kiv.spac.data.antipattern.heading.AntipatternTableHeading;
import cz.zcu.kiv.spac.data.antipattern.heading.AntipatternTextHeading;
import cz.zcu.kiv.spac.data.antipattern.label.AntipatternLabel;
import cz.zcu.kiv.spac.data.catalogue.Catalogue;
import cz.zcu.kiv.spac.data.catalogue.CatalogueRecord;
import cz.zcu.kiv.spac.data.reference.Reference;
import cz.zcu.kiv.spac.data.reference.References;
import cz.zcu.kiv.spac.data.template.TableColumnField;
import cz.zcu.kiv.spac.data.template.TableField;
import cz.zcu.kiv.spac.data.template.Template;
import cz.zcu.kiv.spac.data.template.TemplateField;
import cz.zcu.kiv.spac.enums.AntipatternHeadingType;
import cz.zcu.kiv.spac.file.FileWriter;
import cz.zcu.kiv.spac.utils.Utils;
import javafx.scene.paint.Color;
import org.apache.commons.io.FilenameUtils;
import org.jbibtex.BibTeXEntry;
import org.jbibtex.BibTeXObject;
import org.jbibtex.Key;
import org.jbibtex.Value;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Markdown formatter.
 */
public class MarkdownGenerator {

    private static final String LEFT_BRACKET = "{";
    private static final String RIGHT_BRACKET = "}";

    /**
     * Format markdown table.
     * Some antipatterns contains table with column specification.
     * But in some files, column specification is provided only with 2x '-', like "|--|--|".
     * But markdown formatter we used in app needs at least 3x '-' to create HTML table from markdown table.
     * @param markdownContent - Markdown content.
     * @return Formatted markdown table.
     */
    public static String formatMarkdownTable(String markdownContent) {

        // Regex string.
        String patternString = "^(\\|--)+\\|$";

        Pattern pattern = Pattern.compile(patternString);

        StringBuilder newString = new StringBuilder();

        // Iterate through every line in markdown.
        for (String contentLine : markdownContent.split(Constants.LINE_BREAKER_CRLF)) {
            Matcher matcher = pattern.matcher(contentLine);

            // If lines contains markdown table column specification, then format it.
            if (matcher.find()) {

                StringBuilder newParts = new StringBuilder();

                // Go through every block (|--|--| -> |, --|, --|
                for (int i = 0; i < matcher.groupCount(); i++) {

                    // Get matched line.
                    String tableColumnDefine = matcher.group(i);

                    // Split matched line with pipeline, to get each block.
                    String[] parts = tableColumnDefine.split("\\|");

                    // Foreach every block.
                    for (String part : parts) {

                        // If block equals "--", then add one more "-".
                        // It is neccessary to have at least 3x "-" in table definition,
                        // Because flexmark do not recognize 2x "-" as a table definition.
                        if (part.equals("--")) {

                            part += "-";
                        }

                        newParts.append(part).append("|");
                    }
                }

                newString.append(newParts);
                newString.append(Constants.LINE_BREAKER_CRLF);
                continue;
            }

            newString.append(contentLine);
            newString.append(Constants.LINE_BREAKER_CRLF);
        }

        return newString.toString();
    }

    /**
     * Create antipattern markdown content from Form in antipattern window.
     * @param headings - Headings contains field definition and text.
     * @param fieldList - Template field list.
     * @param catalogue - Catalogue.
     * @return Markdown content for antipattern.
     */
    public static String createAntipatternMarkdownContent(Map<String, AntipatternHeading> headings, List<TemplateField> fieldList, Catalogue catalogue) {

        StringBuilder sb = new StringBuilder();

        // Add path to antipattern name.
        sb.append("[Home](" + Constants.README_NAME + ") > [Catalogue](" + Constants.CATALOGUE_NAME + ") > ");

        boolean nameWrited = false;

        int i = 0;

        // Iterate through every template field to extract value for every field.
        for (TemplateField field : fieldList) {

            AntipatternHeading antipatternHeading = headings.get(field.getName());

            // First, we need to write name, which is every time on first position.
            if (!nameWrited) {

                AntipatternTextHeading textHeading = (AntipatternTextHeading) antipatternHeading;

                // Add antipatern name to path.
                sb.append(textHeading.getValue());
                sb.append(Constants.LINE_BREAKER_CRLF);
                sb.append(Constants.LINE_BREAKER_CRLF);
                sb.append(Constants.LINE_BREAKER_CRLF);

                nameWrited = true;

                // Antipattern name.
                sb.append("# ");
                sb.append(textHeading.getValue());

            } else {

                sb.append("## ");
                sb.append(field.getText());

                if (!field.isRequired()) {
                    sb.append(Constants.TEMPLATE_FIELD_OPTIONAL_STRING);
                }

                sb.append(Constants.LINE_BREAKER_CRLF);
                sb.append(Constants.LINE_BREAKER_CRLF);

                // Check textarea and textfield.
                if (antipatternHeading.getType() == AntipatternHeadingType.TEXT) {

                    AntipatternTextHeading textHeading = (AntipatternTextHeading) antipatternHeading;
                    sb.append(textHeading.getValue());

                } else if (antipatternHeading.getType() == AntipatternHeadingType.TABLE) {

                    AntipatternTableHeading tableHeading = (AntipatternTableHeading) antipatternHeading;
                    TableField tableField = (TableField) field;

                    sb.append(createTableHeaderMarkdownContent(tableField));

                    for(AntipatternRelation relation : tableHeading.getRelations()) {

                        CatalogueRecord record = catalogue.getCatalogueRecordByAntipatternName(relation.getAntipattern());

                        if (record == null) {

                            sb.append("|").append(relation.getAntipattern()).append("|").append(relation.getRelation());

                        } else {

                            sb.append("|[").append(record.getAntipatternName()).append("](").
                                    append(record.getPath()).append(")|").append(relation.getRelation());
                            relation.setLinked(true);
                        }

                        sb.append(Constants.LINE_BREAKER_CRLF);
                    }
                }
            }

            if (i < fieldList.size() - 1) {

                sb.append(Constants.LINE_BREAKER_CRLF);
                sb.append(Constants.LINE_BREAKER_CRLF);
            }

            i++;
        }

        return sb.toString();
    }

    /**
     * Existence check for antipattern relations.
     * Iterate through every relation in antipattern and add link to related antipattern.
     * @param antipattern - Currently updated / created antipattern.
     * @param template - Template.
     * @param catalogue - Catalogue
     */
    public static void relationsExistenceCheck(Antipattern antipattern, Template template, Catalogue catalogue) {

        Set<AntipatternRelation> relations = antipattern.getRelations();

        if (relations != null) {

            for (AntipatternRelation relation : relations) {

                if (!relation.isLinked() && catalogue.isAntipatternPresentedInCatalogue(relation.getAntipattern())) {

                    String content = createAntipatternMarkdownContent(antipattern.getAntipatternHeadings(), template.getFieldList(), catalogue);
                    FileWriter.write(new File(Utils.createMarkdownFilename(antipattern)), content);
                    relation.setLinked(true);
                    antipattern.setContent(content);
                }
            }
        }
    }


    /**
     * Create markdown table header.
     * @param tableField - Table field.
     * @return Table header in markdown.
     */
    private static String createTableHeaderMarkdownContent(TableField tableField) {

        StringBuilder sb = new StringBuilder();

        sb.append("|");

        // Append column names.
        for (TableColumnField tableColumnField : tableField.getColumns()) {

            sb.append(tableColumnField.getText()).append("|");
        }

        sb.append(Constants.LINE_BREAKER_CRLF);
        sb.append("|");

        // Append column separators.
        for (int j = 0; j < tableField.getColumns().size(); j++) {

            sb.append("---|");
        }

        sb.append(Constants.LINE_BREAKER_CRLF);

        return sb.toString();
    }

    /**
     * Create markdown catalogue content.
     * @param catalogue - Catalogue.
     * @param antipatterns - Antipatterns
     * @return Markdown content for catalogue.
     */
    public static String createCatalogueMarkdownContent(Catalogue catalogue, Map<String, Antipattern> antipatterns) {

        StringBuilder sb = new StringBuilder();

        Map<String, List<CatalogueRecord>> catalogueRecordMap = catalogue.getCatalogueRecords();

        sb.append("[Home](").append(Utils.getFilenameFromStringPath(Constants.README_NAME)).append(") > Catalogue");
        sb.append(Constants.LINE_BREAKER_CRLF);

        sb.append("# " + Constants.APP_NAME);
        sb.append(Constants.LINE_BREAKER_CRLF);
        sb.append(Constants.LINE_BREAKER_CRLF);

        sb.append("[Template](" + Constants.CATALOGUE_FOLDER + "/" + Constants.TEMPLATE_FILE + ") for new anti-pattern contents.");
        sb.append(Constants.LINE_BREAKER_CRLF);
        sb.append(Constants.LINE_BREAKER_CRLF);
        sb.append(Constants.LINE_BREAKER_CRLF);

        for (String key : catalogueRecordMap.keySet()) {

            List<CatalogueRecord> recordList = catalogueRecordMap.get(key);

            sb.append("## ").append(key);
            sb.append(Constants.LINE_BREAKER_CRLF);

            for (CatalogueRecord record : recordList) {

                if (record.getPath().equals("")) {

                    sb.append(record.getAntipatternName());

                } else {

                    String formattedName = Utils.formatAntipatternName(record.getAntipatternName());
                    Antipattern antipattern = antipatterns.get(formattedName);

                    if (antipattern != null && antipattern.isLinking() && !catalogue.isAntipatternPresentedInCatalogue(antipattern.getName())) {

                        String linkedAntipatternName = Utils.getFilenameFromStringPath(antipattern.getPath());
                        linkedAntipatternName = FilenameUtils.removeExtension(linkedAntipatternName);
                        linkedAntipatternName = Utils.formatAntipatternName(linkedAntipatternName);

                        Antipattern linkedAntipattern = antipatterns.get(linkedAntipatternName);
                        sb.append(record.getAntipatternName()).append(" - _see [").append(linkedAntipattern.getName()).append("](").append(antipattern.getPath()).append(")_");

                    } else {

                        sb.append("[").append(record.getAntipatternName()).append("](").append(record.getPath()).append(")");
                    }
                }

                sb.append(Constants.LINE_BREAKER_CRLF);
                sb.append(Constants.LINE_BREAKER_CRLF);
            }

        }

        return sb.toString();
    }

    /**
     * Convert segment text to markdown text and apply styles.
     * @param text - Segment text.
     * @param bold - True if text must be bold, false if not.
     * @param italic - True if text must be italic, false if not.
     * @param underline - True if text must be underlined, false if not.
     * @param isImage - True if text is path to image, false if only segment text.
     * @return Segment text in markdown.
     */
    public static String convertSegmentText(String text, boolean bold, boolean italic, boolean underline, boolean isImage) {

        String markdownText = text;

        if (isImage) {

            String filename = Utils.getFilenameFromStringPath(text);
            String filenameWithoutExtension = Utils.removeFilenameExtension(filename);

            markdownText = "![" + filenameWithoutExtension + "](" + filename + ")";
        }

        markdownText = applyStyles(markdownText, bold, italic, underline);

        return markdownText;
    }

    /**
     * Generate markdown bullet level from indent.
     * @param indent - Indent.
     * @return Markdown bullet list level.
     */
    public static String createLevelFromIndent(int indent) {

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < indent; i++) {

            sb.append(" ");

            // First level must have only 1 white space between text.
            if (i > 0) {

                sb.append(" ");
            }
        }

        sb.append("- ");

        return sb.toString();
    }

    /**
     * Apply styles (bold, italic, underline) to text.
     * @param text - Text.
     * @param bold - True if text must be bold, false if not.
     * @param italic - True if text must be italic, false if not.
     * @param underline - True if text must be underlined, false if not.
     * @return Text with styles.
     */
    private static String applyStyles(String text, boolean bold, boolean italic, boolean underline) {

        String textWithStyles = text;

        if (bold) {

            textWithStyles = "**" + textWithStyles + "**";
        }

        if (italic) {

            textWithStyles = "_" + textWithStyles + "_";
        }

        if (underline) {

            textWithStyles = "<u>" + textWithStyles + "</u>";
        }

        return textWithStyles;
    }

    /**
     * Get markdown content for nonexisting antipattern.
     * @param antipatternName - Name of nonexisting antipattern.
     * @return Markdown content.
     */
    public static String getNonExistingAntipatternContent(String antipatternName) {

        StringBuilder sb = new StringBuilder();

        sb.append("# " + antipatternName);

        sb.append(Constants.LINE_BREAKER_CRLF);
        sb.append(Constants.LINE_BREAKER_CRLF);

        sb.append("This antipattern was not yet created, only mentioned in antipattern catalogue.");

        return sb.toString();
    }

    /**
     * Convert Map<Key, Value> to Map<String, String> for bibtex fields.
     * @param fields - Bibtex fields.
     * @return Map from bibtex fields containing only strings.
     */
    private static Map<String, String> parseFields(Map<Key, Value> fields) {

        Map<String, String> newFields = new HashMap<>();

        for (Key field : fields.keySet()) {

            String fieldValue = field.getValue();

            Value value = fields.get(field);

            newFields.put(fieldValue, value.toUserString());
        }

        return newFields;
    }

    /**
     * Replace or delete some characters from string.
     * @param str - String.
     * @return String with changes.
     */
    private static String prepareString(String str) {

        return str.replace(LEFT_BRACKET, "").replace(RIGHT_BRACKET, "").replace("`", "\"").replace("'", "\"");
    }

    /**
     * Prepare pages in string - replace.
     * @param pages - Pages in string.
     * @return Replaced - in pages string.
     */
    private static String preparePagesString(String pages) {

        return pages.replace("--", "-");
    }

    private static String extractURLFromNote(String note) {
        String regex = "\\b((?:https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:, .;]*[-a-zA-Z0-9+&@#/%=~_|])";

        // Compile the Regular Expression
        Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);

        // Find the match between string and the regular expression
        Matcher m = p.matcher(note);
        String url = null;
        if (m.find()) {
            url = note.substring(m.start(), m.end());
            int dotIndex = url.indexOf(". ");
            int urlEnd = dotIndex > 0 ? dotIndex : url.length() - 1;
            url = url.substring(0, Math.max(0, urlEnd));
        }

        return url;
    }

    /**
     * Generate markdown for References from bibtex file content.
     * @param objects - List of bibtex entries.
     * @return References in object
     */
    public static References generateReferencesFromBibtex(List<BibTeXObject> objects) {

        Map<String, Reference> referenceMap = new HashMap<>();

        StringBuilder markdownText = new StringBuilder(
                        "[Home](README.md) > References\n" +
                        "# References\n" +
                        "\n" +
                        "_(Ordered alphabetically by the key.)_\n" +
                        "\n");

        List<String> shortcuts = new ArrayList<>();

        Iterator<BibTeXObject> itObject = objects.iterator();

        // Iterate through every reference.
        while (itObject.hasNext()) {

            Reference reference = new Reference();

            BibTeXObject object = itObject.next();

            String strRecord = "";
            BibTeXEntry entry = (BibTeXEntry) object;
            Map<Key, Value> fields = entry.getFields();

            String shortcut = "";

            Map<String, String> stringFields = parseFields(fields);

            // Get author.
            if (stringFields.containsKey("author")) {

                String author = prepareString(stringFields.get("author"));
                shortcut = author.substring(0, 3).toUpperCase();
                strRecord = author;

                // Set reference author.
                reference.setAuthor(author);
            }

            // Get year.
            if (stringFields.containsKey("year")) {

                String year = prepareString(stringFields.get("year"));
                shortcut = shortcut + "'" + year.substring(2).toUpperCase();

                strRecord += " (" + year + ")";
            }

            // Add dot after author and year.
            strRecord += ". ";

            // Get title.
            if (stringFields.containsKey("title")) {

                String title = prepareString(stringFields.get("title"));

                strRecord += title;

                // Set reference title.
                reference.setTitle(title);
            }

            // Get published.
            if (stringFields.containsKey("howpublished")) {

                String howpublished = prepareString(stringFields.get("howpublished"));

                strRecord += " [" + howpublished.toLowerCase() + "]";
            }

            // Add dot after title and published.
            strRecord += ". ";

            // Get note.
            if (stringFields.containsKey("note")) {

                String note = prepareString(stringFields.get("note"));
                reference.setUrl(extractURLFromNote(note));

                strRecord += note.toLowerCase();

            // Get school.
            } else if (stringFields.containsKey("school")) {

                String school = prepareString(stringFields.get("school"));

                strRecord += school;

            // Get journal.
            } else if (stringFields.containsKey("journal")) {

                String journal = prepareString(stringFields.get("journal"));

                strRecord += "*" + journal + "*";

                // Get volume.
                if (stringFields.containsKey("volume")) {

                    String volume = prepareString(stringFields.get("volume"));
                    strRecord += ", " + volume;

                    // Get number.
                    if (stringFields.containsKey("number")) {

                        String number = prepareString(stringFields.get("number"));
                        strRecord += "(" + number + ")";
                    }
                }

                // Get pages.
                if (stringFields.containsKey("pages")) {

                    String pages = preparePagesString(prepareString(stringFields.get("pages")));

                    strRecord += ", " + pages;
                }

                strRecord += ".";

                // Get publisher.
                if (stringFields.containsKey("publisher")) {

                    String publisher = prepareString(stringFields.get("publisher"));
                    strRecord += " " + publisher;
                }

            } else if (stringFields.containsKey("booktitle")) {

                String booktitle = prepareString(stringFields.get("booktitle"));

                strRecord += "*" + booktitle + "*";

                // Get volume.
                if (stringFields.containsKey("volume")) {

                    String volume = prepareString(stringFields.get("volume"));
                    strRecord += " (Vol. " + volume;

                    // Get number.
                    if (stringFields.containsKey("number")) {

                        String number = prepareString(stringFields.get("number"));
                        strRecord += "(" + number + ")";
                    }

                    // Get pages.
                    if (stringFields.containsKey("pages")) {

                        String pages = preparePagesString(prepareString(stringFields.get("pages")));

                        strRecord += ", pp. " + pages;
                    }

                    strRecord += ").";
                }

                // Get publisher.
                if (stringFields.containsKey("publisher")) {

                    String publisher = prepareString(stringFields.get("publisher"));
                    strRecord += " " + publisher;
                }
            }

            String tmpShortcut = shortcut;
            int index = 1;

            // Check if current shortcut is already presented.
            while(shortcuts.contains(tmpShortcut)) {

                index++;
                tmpShortcut = shortcut + "-" + index;
            }

            // Add newly created shortcut to list of shortcuts.
            shortcuts.add(tmpShortcut);
            shortcut = "[" + tmpShortcut + "]";

            strRecord = shortcut + " " + strRecord;

            // Set reference shortcut.
            reference.setShortcut(shortcut);

            // Add reference to list.
            referenceMap.put(shortcut, reference);

            // Add current reference to final markdown text.
            markdownText.append(strRecord);

            if (itObject.hasNext()) {

                markdownText.append("\n");
                markdownText.append("\n");
            }
        }

        return new References(markdownText.toString(), referenceMap);
    }

    /**
     * Parse all references used in antipattern and create a list from them.
     * @param antipatternReferencesInMarkdown - References in antipattern in markdown.
     * @return List of references names.
     */
    public static List<String> parseUsedReferences(String antipatternReferencesInMarkdown) {

        List<String> usedReferences = new ArrayList<>();

        String patternString = "\\[([^\\]\\[\\r\\n]*)\\]";
        Pattern pattern = Pattern.compile(patternString);

        Matcher matcher = pattern.matcher(antipatternReferencesInMarkdown);

        while (matcher.find()) {
            usedReferences.add(matcher.group(0));
        }

        return  usedReferences;
    }

    public static List<AntipatternLabel> parseUsedLabels(String antipatternLabelInMarkdown) {
        List<AntipatternLabel> labelList = new ArrayList<>();
        String[] labels = antipatternLabelInMarkdown.split(" \\[!\\[");
        for (String labelString : labels) {
            int labelStart = labelString.indexOf('-');
            int labelEnd = labelString.indexOf(".svg");
            String[] labelParts = labelString.substring(labelStart + 1, labelEnd).split("-");
            labelList.add(new AntipatternLabel(labelParts[0], Color.web(labelParts[1])));
        }

        return labelList;
    }
}
