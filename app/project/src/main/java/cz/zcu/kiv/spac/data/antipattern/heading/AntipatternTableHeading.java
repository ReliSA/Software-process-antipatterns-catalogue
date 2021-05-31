package cz.zcu.kiv.spac.data.antipattern.heading;

import cz.zcu.kiv.spac.data.antipattern.AntipatternRelation;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Class representing table heading for TableView with antipattern relations.
 */
public class AntipatternTableHeading extends AntipatternHeading {

    private Set<AntipatternRelation> relations;
    private List<String> columns;

    /**
     * Constructor.
     * @param relations - Antipattern relations.
     */
    public AntipatternTableHeading(Set<AntipatternRelation> relations) {

        this.relations = relations;
        this.columns = new ArrayList<>();
    }

    public AntipatternTableHeading() {

        this.relations = new LinkedHashSet<>();
        this.columns = new ArrayList<>();
    }

    public Set<AntipatternRelation> getRelations() {

        return relations;
    }

    public void addRelation(String antipattern, String stringRelation) {

        // Replace whitespace characters at start and end of antipattern name and relation info.
        if (antipattern.charAt(0) == ' ') {

            antipattern = antipattern.substring(1);
        }

        if (antipattern.charAt(antipattern.length() - 1) == ' ') {

            antipattern = antipattern.substring(0, antipattern.length() - 1);
        }

        if (stringRelation.charAt(0) == ' ') {

            stringRelation = stringRelation.substring(1);
        }

        if (stringRelation.charAt(stringRelation.length() - 1) == ' ') {

            stringRelation = stringRelation.substring(0, stringRelation.length() - 1);
        }

        AntipatternRelation relation = new AntipatternRelation(antipattern, stringRelation);
        relations.add(relation);
    }

    public void addColumn(String column) {

        columns.add(column);
    }

    public List<String> getColumns() {

        return this.columns;
    }
}
