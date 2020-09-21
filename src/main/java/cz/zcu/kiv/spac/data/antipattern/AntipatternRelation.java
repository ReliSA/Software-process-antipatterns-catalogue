package cz.zcu.kiv.spac.data.antipattern;

public class AntipatternRelation {

    private String antipatternName;
    private String relation;

    public AntipatternRelation(String antipatternName, String relation) {

        this.antipatternName = antipatternName;
        this.relation = relation;
    }

    public String getRelation() {

        return relation;
    }

    public String getAntipatternName() {

        return antipatternName;
    }
}
