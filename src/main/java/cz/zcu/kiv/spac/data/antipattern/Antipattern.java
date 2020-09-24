package cz.zcu.kiv.spac.data.antipattern;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Antipattern {

    private String name;
    private String markdownContent;

    private Map<String, String> antipatternHeadings;

    public Antipattern(String name, String markdownContent) {

        this.name = name;
        this.markdownContent = markdownContent;
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

    public Map<String, String> getAntipatternHeadings() {

        return antipatternHeadings;
    }

    public void addAntipatternHeading(String key, String value) {

        this.antipatternHeadings.put(key, value);
    }
}
