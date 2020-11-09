package cz.zcu.kiv.spac.data.antipattern.heading;

import cz.zcu.kiv.spac.data.antipattern.AntipatternRelation;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing table heading for TableView with antipattern relations.
 */
public class AntipatternTableHeading extends AntipatternHeading {

    private List<AntipatternRelation> relations;
    private List<String> columns;

    /**
     * Constructor.
     * @param relations - Antipattern relations.
     */
    public AntipatternTableHeading(List<AntipatternRelation> relations) {

        this.relations = relations;
        this.columns = new ArrayList<>();
    }

    public AntipatternTableHeading() {

        this.relations = new ArrayList<>();
        this.columns = new ArrayList<>();
    }

    public List<AntipatternRelation> getRelations() {

        return relations;
    }

    public void addRelation(String antipattern, String relation) {

        relations.add(new AntipatternRelation(antipattern.replace(" ", ""), relation.replace(" ", "")));
    }

    public void addColumn(String column) {

        columns.add(column);
    }

    public List<String> getColumns() {

        return this.columns;
    }
}
