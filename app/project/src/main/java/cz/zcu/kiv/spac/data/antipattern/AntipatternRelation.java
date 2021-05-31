package cz.zcu.kiv.spac.data.antipattern;

/**
 * Class representing antipattern relation, which is used in 'Related Anti-patterns' table.
 */
public class AntipatternRelation {

    private boolean linked;
    private String antipattern;
    private String relation;

    /**
     * Constructor.
     */
    public AntipatternRelation() {

        antipattern = "";
        relation = "";
    }

    /**
     * Constructor.
     * @param antipattern - Antipattern name.
     * @param relation - Relation to antipattern.
     */
    public AntipatternRelation(String antipattern, String relation) {

        this.antipattern = antipattern;
        this.relation = relation;
    }

    public String getRelation() {

        return relation;
    }

    public String getAntipattern() {

        return antipattern;
    }

    public void setRelation(String relation) {

        this.relation = relation;
    }

    public void setAntipattern(String antipattern) {

        String value = "";
        if (antipattern.matches("^\\[.*\\]\\(.*\\)$")) {

            value = antipattern.substring(antipattern.indexOf("[") + 1, antipattern.indexOf("]"));

            linked = true;

        } else {

            value = antipattern;
        }

        this.antipattern = value;
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
}
