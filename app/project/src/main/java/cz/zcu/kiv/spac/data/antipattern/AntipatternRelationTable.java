package cz.zcu.kiv.spac.data.antipattern;

import cz.zcu.kiv.spac.markdown.MarkdownParser;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Class representing object in antipattern relation table.
 */
public class AntipatternRelationTable {

    private StringProperty antipattern;
    private StringProperty relation;
    private StringProperty rrelation;

    /**
     * Constructor.
     */
    public AntipatternRelationTable() {

        antipattern = new SimpleStringProperty(this, "antipattern");
        relation = new SimpleStringProperty(this, "relation");
        rrelation = new SimpleStringProperty(this, "rrelation");

        antipattern.setValue("");
        relation.setValue("");
        rrelation.setValue("");
    }

    /**
     * Constructor.
     * @param antipattern - Antipattern name.
     * @param relation - Relation to antipattern.
     * @param relatedAntipatternRelation - Relation for related antipattern.
     */
    public AntipatternRelationTable(String antipattern, String relation, String relatedAntipatternRelation) {

        this();
        this.relation.setValue(relation);
        this.rrelation.setValue(relatedAntipatternRelation);

        setAntipattern(antipattern);
    }

    public String getRelation() {

        return relation.getValue();
    }

    public String getAntipattern() {

        return antipattern.getValue();
    }

    public String getRrelation() {

        return rrelation.getValue();
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

        if (this.rrelation.getName().equals(valueFactory)) {

            return this.rrelation;
        }

        return null;
    }

    public void setRrelation(String relatedAntipatternRelation) {

        this.rrelation.setValue(relatedAntipatternRelation);
    }

    public void setAntipattern(String antipattern) {

        this.antipattern.setValue(MarkdownParser.parseAntipatternFromTableRecord(antipattern));
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

        AntipatternRelationTable other = (AntipatternRelationTable) obj;

        if (!getAntipattern().equals(other.getAntipattern())) {

            return false;
        }

        return true;
    }

    public StringProperty antipatternProperty() {

        return this.antipattern;
    }

    public StringProperty relationProperty() {

        return this.relation;
    }

    public StringProperty rrelationProperty() {

        return this.rrelation;
    }
}
