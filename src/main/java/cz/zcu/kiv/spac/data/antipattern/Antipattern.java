package cz.zcu.kiv.spac.data.antipattern;

import cz.zcu.kiv.spac.data.Constants;
import cz.zcu.kiv.spac.data.antipattern.heading.AntipatternHeading;
import cz.zcu.kiv.spac.utils.Utils;

import java.util.*;

/**
 * Class representing antipattern.
 */
public class Antipattern {

    private String name;
    private String formattedName;
    private AntipatternContent content;
    private String path;

    private boolean created;
    private boolean linking;

    private Map<String, AntipatternHeading> antipatternHeadings;

    /**
     * Constructor.
     * @param name - Antipattern name.
     * @param content - Antipattern content in markdown.
     * @param path - Path to antipattern file.
     */
    public Antipattern(String name, AntipatternContent content, String path) {

        this.name = name;
        this.formattedName = Utils.formatAntipatternName(name);
        this.content = content;
        this.path = path;
        this.antipatternHeadings = new LinkedHashMap<>();

        if (path.equals("")) {

            created = false;

        } else {

            created = true;
        }

        linking = false;
    }

    public AntipatternContent getContent() {

        return content;
    }

    public void setContent(AntipatternContent content) {

        this.content = content;
    }

    public void setContent(String content) {

        this.content.setContent(content);
    }

    public String getName() {

        return name;
    }

    public Map<String, AntipatternHeading> getAntipatternHeadings() {

        return antipatternHeadings;
    }

    public AntipatternHeading getAntipatternHeading(String headingName, boolean isRequired) {

        if (!isRequired) {

            headingName += Constants.TEMPLATE_FIELD_OPTIONAL_STRING;
        }

        if (antipatternHeadings.containsKey(headingName)) {

            return antipatternHeadings.get(headingName);
        }

        return null;
    }

    public List<String> getAntipatternHeadingsTexts() {

        if (antipatternHeadings == null) {

            return null;
        }

        List<String> antipatternHeadingsTexts = new ArrayList<>();

        for (AntipatternHeading antipatternHeading : antipatternHeadings.values()) {

            antipatternHeadingsTexts.add(antipatternHeading.getHeadingText());
        }

        return antipatternHeadingsTexts;
    }

    public void addAntipatternHeading(String key, AntipatternHeading heading) {

        this.antipatternHeadings.put(key, heading);
    }

    public void setAntipatternHeadings(Map<String, AntipatternHeading> antipatternHeadings) {

        this.antipatternHeadings = new HashMap<>(antipatternHeadings);
    }

    public String getPath() {

        return this.path;
    }

    public void setPath(String path) {

        this.path = path;
    }

    public void setName(String name) {

        this.name = name;
        this.formattedName = Utils.formatAntipatternName(name);
    }

    public boolean isCreated() {

        return created;
    }

    public boolean isLinking() {

        return linking;
    }

    public void setLinking(boolean linking) {

        this.linking = linking;
    }

    public String getFormattedName() {

        return formattedName;
    }
}
