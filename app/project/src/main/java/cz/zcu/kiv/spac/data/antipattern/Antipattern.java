package cz.zcu.kiv.spac.data.antipattern;

import cz.zcu.kiv.spac.data.antipattern.heading.AntipatternHeading;
import cz.zcu.kiv.spac.data.antipattern.heading.AntipatternTableHeading;
import cz.zcu.kiv.spac.data.antipattern.label.AntipatternLabel;
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
    private String linkedAntipatternName;

    private Map<String, AntipatternHeading> antipatternHeadings;
    private String relationsHeadingName;
    private List<String> linkingAntipatterns;

    private List<AntipatternLabel> labels;

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
        this.linkingAntipatterns = new ArrayList<>();
        this.labels = new ArrayList<>();

        relationsHeadingName = "";
        linkedAntipatternName = "";

        if (!path.contains(name.replace(" ", "_"))) {

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

    public AntipatternHeading getAntipatternHeading(String headingName) {

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

        for (AntipatternHeading heading : antipatternHeadings.values()) {

            antipatternHeadingsTexts.add(heading.getHeadingText());
        }

        return antipatternHeadingsTexts;
    }

    public void addAntipatternHeading(String key, AntipatternHeading heading) {

        this.antipatternHeadings.put(key, heading);
    }

    public void setAntipatternHeadings(Map<String, AntipatternHeading> antipatternHeadings) {

        this.antipatternHeadings = new LinkedHashMap<>(antipatternHeadings);
    }

    public void addLinkedAntipattern(String antipatternName) {

        this.linkingAntipatterns.add(antipatternName);
    }

    public List<String> getLinkingAntipatterns() {

        return linkingAntipatterns;
    }

    public Set<AntipatternRelation> getRelations() {

        AntipatternTableHeading relationsHeading = (AntipatternTableHeading) antipatternHeadings.get(relationsHeadingName);

        if (relationsHeading != null) {

            return relationsHeading.getRelations();

        } else {

            return null;
        }

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

    public void setRelationsHeadingName(String relationsHeadingName) {

        this.relationsHeadingName = relationsHeadingName;
    }

    public String getRelationsHeadingName() {

        return this.relationsHeadingName;
    }

    public String getLinkedAntipatternName() {

        return linkedAntipatternName;
    }

    public void setLinkedAntipatternName(String linkedAntipatternName) {

        this.linkedAntipatternName = linkedAntipatternName;
    }

    public List<AntipatternLabel> getLabels() {
        return this.labels;
    }

    public void setLabels(List<AntipatternLabel> labels) {

        this.labels = labels;
    }
}
