import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;

import static org.junit.Assert.*;

public class GraphTest {
    String file;
    String filepath;
    DefaultDirectedGraph<String, DefaultEdge> directedGraph;

    @Before
    public void setup() throws IOException {
        file = "graphfile.dot";
        filepath = getClass().getResource(file).getPath();
        DotGraph.parseGraph(filepath);
    }

    @Test
    public void parseTest() throws IOException {
        int totalNodes = DotGraph.getNodes();
        assertEquals(5, totalNodes);
        int totalEdges = DotGraph.getEdges();
        assertEquals(7, totalEdges);

    }

    @Test
    public void outputGraph() throws IOException {
        String outputPath = "output.txt";
        DotGraph.outputGraph(outputPath);
        File outputFile = new File(outputPath);
        assertTrue("The output file was not created.", outputFile.exists()); //checks that the output file was created
        StringBuilder content = new StringBuilder(); //read output file contents
        try (BufferedReader reader = new BufferedReader(new FileReader(outputFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
        }
        assertTrue("Output text file empty!", outputFile.length() > 0);
    }

    @Test
    public void graphToStringTest() throws IOException {
        String recievedOutput = DotGraph.graphtoString();
        assertFalse("Graph was not outputted as string!", recievedOutput.isEmpty());
    }
}
