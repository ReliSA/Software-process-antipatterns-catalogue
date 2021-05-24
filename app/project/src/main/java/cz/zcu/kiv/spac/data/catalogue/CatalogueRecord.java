package cz.zcu.kiv.spac.data.catalogue;

import org.jetbrains.annotations.NotNull;

/**
 * CLass representing catalogue record in catalogue file.
 */
public class CatalogueRecord implements Comparable<CatalogueRecord> {

    private String antipatternName;
    private String path;

    /**
     * Constructor.
     * @param antipatternName - Antipattern name.
     * @param path - Relative path to antipattern file in catalogue.
     */
    public CatalogueRecord(String antipatternName, String path) {

        this.antipatternName = antipatternName;
        this.path = path;
    }

    public String getAntipatternName() {

        return antipatternName;
    }

    public void setAntipatternName(String antipatternName) {

        this.antipatternName = antipatternName;
    }

    public String getPath() {

        return path;
    }

    public void setPath(String path) {

        this.path = path;
    }

    @Override
    public int compareTo(@NotNull CatalogueRecord catalogueRecord) {

        return this.getAntipatternName().compareTo(catalogueRecord.getAntipatternName());
    }
}
