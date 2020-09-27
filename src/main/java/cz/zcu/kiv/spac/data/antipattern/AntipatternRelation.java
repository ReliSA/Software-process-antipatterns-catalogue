package cz.zcu.kiv.spac.data.antipattern;

/**
 * Class representing antipattern relation, which is used in 'Related Anti-patterns' table.
 */
public class AntipatternRelation {

    private String antipattern;
    private String relation;

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

        this.antipattern = antipattern;
    }
}
