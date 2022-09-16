package cz.zcu.kiv.spac.data.graph;

import cz.zcu.kiv.spac.data.antipattern.Antipattern;
import cz.zcu.kiv.spac.data.antipattern.AntipatternRelation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class GraphGenerator {

    private static Logger log = LogManager.getLogger(GraphGenerator.class);

    private final List<Antipattern> antipatterns;

    private final Map<String, String> ap;

    public GraphGenerator(Collection<Antipattern> antipatterns) {
        this.antipatterns = new ArrayList<>(antipatterns);
        this.ap = new HashMap<>();
    }

    public void generateDotGraph(String graphName, File file) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            bw.write(String.format("graph %s {", graphName));
            bw.newLine();

            for (int i = 0; i < antipatterns.size(); i++) {
                Antipattern antipattern = antipatterns.get(i);
                String nodeName = "node" + (i + 1);
                ap.put(antipattern.getName(), nodeName);
                bw.write(nodeName + " [label=\"" + antipattern.getName() + "\"];");
                bw.newLine();
            }

            for (Antipattern antipattern : antipatterns) {
                if (antipattern.getRelations() != null) {
                    for (AntipatternRelation relation : antipattern.getRelations()) {
                        int from = Math.max(relation.getAntipattern().indexOf("[") + 1, 0);
                        int to = relation.getAntipattern().indexOf("]") > 0 ? relation.getAntipattern().indexOf("]") :
                                relation.getAntipattern().length();
                        String name = ap.get(relation.getAntipattern().substring(from, to));
                        if (name != null) {
                            bw.write(ap.get(antipattern.getName()) + " -- " + name + ";");
                            bw.newLine();
                        }
                    }
                }
            }
            bw.write("}");
        } catch (IOException e) {

            log.error("Error while writing content to file '" + file.getName() + "'");
        }
    }
}