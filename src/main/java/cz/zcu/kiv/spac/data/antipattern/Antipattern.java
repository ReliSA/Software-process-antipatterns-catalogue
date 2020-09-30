package cz.zcu.kiv.spac.data.antipattern;

import cz.zcu.kiv.spac.data.antipattern.heading.AntipatternHeading;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Class representing antipattern.
 */
public class Antipattern {

    private String name;
    private String markdownContent;
    private String path;

    private Map<String, AntipatternHeading> antipatternHeadings;

    /**
     * Constructor.
     * @param name - Antipattern name.
     * @param markdownContent - Antipattern content in markdown.
     * @param path - Path to antipattern file.
     */
    public Antipattern(String name, String markdownContent, String path) {

        this.name = name;
        this.markdownContent = markdownContent;
        this.path = path;
        this.antipatternHeadings = new LinkedHashMap<>();
    }

    public String getMarkdownContent() {

        return markdownContent;
    }

    public void setMarkdownContent(String markdownContent) {

        this.markdownContent = markdownContent;
    }

    public String getName() {

        return name;
    }

    public Map<String, AntipatternHeading> getAntipatternHeadings() {

        return antipatternHeadings;
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
    }
}
