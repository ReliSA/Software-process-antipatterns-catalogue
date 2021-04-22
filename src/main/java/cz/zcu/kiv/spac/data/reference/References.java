package cz.zcu.kiv.spac.data.reference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Class representing list of references.
 */
public class References {

    private String markdownFormat;
    private Map<String, Reference> referenceMap;

    /**
     * Constructor.
     * @param markdownFormat - Markdown format of all references.
     * @param referenceMap - Map of references, key is shortcut.
     */
    public References(String markdownFormat, Map<String, Reference> referenceMap) {

        this.markdownFormat = markdownFormat;
        this.referenceMap = referenceMap;
    }

    public String getMarkdownFormat() {

        return markdownFormat;
    }

    public Map<String, Reference> getReferenceMap() {

        return referenceMap;
    }

    /**
     * Prepare references to add them into info box.
     * @return List of items containing references.
     */
    public List<String> getListForInfobox() {

        List<String> comboboxItems = new ArrayList<>();

        for (String shortcut : referenceMap.keySet()) {

            Reference reference = referenceMap.get(shortcut);

            comboboxItems.add(shortcut + " " + reference.getAuthor() + ". " + reference.getTitle());
        }

        return comboboxItems;
    }
}
