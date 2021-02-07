package cz.zcu.kiv.spac.data.antipattern;

import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Class representing antipattern relation, which is used in 'Related Anti-patterns' table.
 */
public class AntipatternRelation {

    private boolean linked;
    private StringProperty antipattern;
    private StringProperty relation;

    /**
     * Constructor.
     */
    public AntipatternRelation() {

        antipattern = new SimpleStringProperty(this, "antipattern");
        relation = new SimpleStringProperty(this, "relation");

        antipattern.setValue("");
        relation.setValue("");
    }

    /**
     * Constructor.
     * @param antipattern - Antipattern name.
     * @param relation - Relation to antipattern.
     */
    public AntipatternRelation(String antipattern, String relation) {

        this();
        this.relation.setValue(relation);
        this.linked = false;

        setAntipattern(antipattern);
    }

    public String getRelation() {

        return relation.getValue();
    }

    public String getAntipattern() {

        return antipattern.getValue();
    }

    public void setRelation(String relation) {

        this.relation.setValue(relation);
    }

    public StringProperty getProperty(String valueFactory) {

        if (this.antipattern.getName().equals(valueFactory)) {

            return this.antipattern;
        }

        if (this.relation.getName().equals(valueFactory)) {

            return this.relation;
        }

        return null;
    }

    public void setAntipattern(String antipattern) {

        String value = "";
        if (antipattern.matches("^\\[.*\\]\\(.*\\)$")) {

            value = antipattern.substring(antipattern.indexOf("[") + 1, antipattern.indexOf("]"));

            linked = true;

        } else {

            value = antipattern;
        }

        this.antipattern.setValue(value);
    }

    public boolean isLinked() {

        return linked;
    }

    public void setLinked(boolean linked) {

        this.linked = linked;
    }

    @Override
    public int hashCode() {

        return getAntipattern().hashCode();
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {

            return true;
        }

        if (obj == null) {

            return false;
        }

        if (getClass() != obj.getClass()) {

            return false;
        }

        AntipatternRelation other = (AntipatternRelation) obj;

        if (!getAntipattern().equals(other.getAntipattern())) {

            return false;
        }

        return true;
    }

    public StringProperty getAntipatternProperty() {

        return this.antipattern;
    }

    public StringProperty getRelationProperty() {

        return this.relation;
    }
}
