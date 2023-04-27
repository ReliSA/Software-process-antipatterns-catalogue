package cz.zcu.kiv.spac.data.graph;

import cz.zcu.kiv.spac.data.antipattern.Antipattern;
import cz.zcu.kiv.spac.data.antipattern.AntipatternRelation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class GraphGenerator {

    private static Logger log = LogManager.getLogger(GraphGenerator.class);

    private final Set<Antipattern> antipatterns;

    public GraphGenerator(Collection<Antipattern> antipatterns) {
        this.antipatterns = new HashSet<>(antipatterns);
    }

    public void generateDotGraph(String graphName, File file) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            bw.write(String.format("digraph %s {", graphName));
            bw.newLine();

            for (Antipattern at : antipatterns) {
                bw.write("\"" + at.getName() +"\"");
                bw.newLine();
            }

            for (Antipattern antipattern : antipatterns) {
                if (antipattern.getRelations() != null) {
                    for (AntipatternRelation relation : antipattern.getRelations()) {
                        int from = Math.max(relation.getAntipattern().indexOf("[") + 1, 0);
                        int to = relation.getAntipattern().indexOf("]") > 0 ? relation.getAntipattern().indexOf("]") :
                                relation.getAntipattern().length();
                        String antipatternName = relation.getAntipattern().substring(from, to);
                            bw.write("\"" + antipattern.getName() + "\"" + " -> " + "\"" + antipatternName + "\"" + " [label=\"" +
                                    relation.getRelation().replace("\"", "\\\"") + "\"];");
                            bw.newLine();
                    }
                }
            }
            bw.write("}");
        } catch (IOException e) {

            log.error("Error while writing content to file '" + file.getName() + "'");
        }
    }
}
