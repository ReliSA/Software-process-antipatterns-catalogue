package cz.zcu.kiv.spac.data.catalogue;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CLass representing catalogue file.
 */
public class Catalogue {

    private Map<String, List<CatalogueRecord>> catalogueRecords;

    /**
     * Constructor.
     */
    public Catalogue() {

        catalogueRecords = new HashMap<>();
    }

    /**
     * Add catalogue instance.
     * Catalogue instance is defined by alphabet ('A', 'B', ...) and contains list of antipatterns which start with instance char.
     * @param key - 'A', 'B', ....
     * @param records - List of catalogue records (antipattern list, which starts with key).
     */
    public void addCatalogueInstance(String key, List<CatalogueRecord> records) {

        this.catalogueRecords.put(key, records);
    }

    public Map<String, List<CatalogueRecord>> getCatalogueRecords() {

        return this.catalogueRecords;
    }

    public List<CatalogueRecord> getCatalogueInstance(String key) {

        return this.catalogueRecords.get(key);
    }

    /**
     * Sort records in catalogue instance.
     * @param key - Key of catalogue instance ('A', 'B', ...).
     */
    public void sortCatalogueInstance(String key) {

        List<CatalogueRecord> records = catalogueRecords.get(key);
        Collections.sort(records);

        catalogueRecords.put(key, records);
    }
}
