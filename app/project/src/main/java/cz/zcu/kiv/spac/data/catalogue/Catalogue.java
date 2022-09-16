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

    /**
     * Check of antipattern is presented in catalogue in any instance.
     * @param knownAs - Antipattern's known As.
     * @return True of antipattern is presented in catalogue, false otherwise.
     */
    public boolean isAntipatternPresentedInCatalogue(String knownAs) {

        CatalogueRecord record = getCatalogueRecordByAntipatternName(knownAs);
        if (record != null) {

            return true;
        }

        return false;
    }

    /**
     * Check of antipattern is presented in catalogue in any instance.
     * @param knownAs - Antipattern's known As.
     * @return True of antipattern is presented in catalogue, false otherwise.
     */
    public boolean isAntipatternPresentedInCatalogue(String knownAs, String antipatternName) {

        CatalogueRecord record = getCatalogueRecordByAntipatternName(knownAs);
        if (record != null) {

            if (record.getPath().toLowerCase().contains(antipatternName.toLowerCase())) {
            if (record.getPath().toLowerCase().contains(antipatternName.toLowerCase()
                    .replace(" ", "_")
                    .replace("'", "")
                    .replace("’", ""))) {

                return false;
            }

            return true;
        }

        return false;
    }

    /**
     * Check of antipattern is presented in catalogue in any instance.
     * @param antipatternName - Name of antipattern.
     * @return Catalogue record if antipatternName is presented, null otherwise.
     */
    public CatalogueRecord getCatalogueRecordByAntipatternName(String antipatternName) {

        // Check if key is presented in map.
        String key = antipatternName.toUpperCase().substring(0, 1);

        if (catalogueRecords.containsKey(key)) {

            // Check if antipattern name is presented in list of catalogue records.
            List<CatalogueRecord> records = getCatalogueInstance(key);

            for (CatalogueRecord record : records) {

                if (record.getAntipatternName().equals(antipatternName)) {

                    return record;
                }
            }

            return null;

        } else {

            return null;
        }
    }

    /**
     * Delete catalogue record by antipattern name.
     * @param antipatternName - Name of antipattern.
     * @return True if catalogue record was deleted successfully, false otherwise.
     */
    public boolean deleteCatalogueRecord(String antipatternName) {

        // Check if key is presented in map.
        String key = antipatternName.toUpperCase().substring(0, 1);

        if (catalogueRecords.containsKey(key)) {

            // Check if antipattern name is presented in list of catalogue records.
            List<CatalogueRecord> records = getCatalogueInstance(key);

            for (CatalogueRecord record : records) {

                if (record.getAntipatternName().equals(antipatternName)) {

                    records.remove(record);
                    return true;
                }
            }

            return false;

        } else {

            return false;
        }
    }

    public Map<String, List<CatalogueRecord>> getCatalogueRecords() {

        return this.catalogueRecords;
    }

    public List<CatalogueRecord> getCatalogueInstance(String key) {

        if (this.catalogueRecords.containsKey(key)) {
            return this.catalogueRecords.get(key);
        }

        return null;
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
