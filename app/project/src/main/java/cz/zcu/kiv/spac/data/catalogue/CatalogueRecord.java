package cz.zcu.kiv.spac.data.catalogue;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CatalogueRecord record = (CatalogueRecord) o;
        return Objects.equals(antipatternName, record.antipatternName) && Objects.equals(path, record.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(antipatternName, path);
    }
}
