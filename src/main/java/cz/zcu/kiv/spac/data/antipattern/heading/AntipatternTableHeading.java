package cz.zcu.kiv.spac.data.antipattern.heading;

import cz.zcu.kiv.spac.data.antipattern.AntipatternRelation;

import java.util.List;

/**
 * Class representing table heading for TableView with antipattern relations.
 */
public class AntipatternTableHeading extends AntipatternHeading {

    private List<AntipatternRelation> relations;

    /**
     * Constructor.
     * @param relations - Antipattern relations.
     */
    public AntipatternTableHeading(List<AntipatternRelation> relations) {

        this.relations = relations;
    }

    public List<AntipatternRelation> getRelations() {

        return relations;
    }
}
