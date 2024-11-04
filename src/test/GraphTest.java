import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import org.junit.After;
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
        file = "graphfile.dot"; //resource file
        filepath = getClass().getResource(file).getPath();
        DotGraph.parseGraph(filepath);
    }

    @Test
    public void parseTest() throws IOException {
        int totalNodes = DotGraph.getNodes();
        assertEquals(6, totalNodes);
        int totalEdges = DotGraph.getEdges();
        assertEquals(6, totalEdges);
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

    @Test
    public void addNodeTest(){
        //Add single node
        String label = "v";
        DotGraph.addNode(label);
        assertTrue("Node 'v' was not added.", DotGraph.containsNode("v"));


        //Add multiple nodes at the same time
        String[] labels = {"x", "y", "z"};
        DotGraph.addNodes(labels);
        assertTrue("Node 'x' was not added.", DotGraph.containsNode("x"));
        assertTrue("Node 'y' was not added.", DotGraph.containsNode("y"));
        assertTrue("Node 'z' was not added.", DotGraph.containsNode("z"));


        //Add existing node
        label = "x";
        boolean labelExists = DotGraph.addNode(label);
        assertTrue("Node 'x' was added when it is a duplicate", labelExists = true);
    }

    @Test
    public void addEdgeTest(){
        //Add edge with existing nodes
        String src = "b";
        String dst = "c";
        int edgeAdded = DotGraph.addEdge(src, dst);
        assertTrue("Edge added was not added.", edgeAdded == 0);


        //Add edge with new nodes
        src = "f";
        dst = "g";
        edgeAdded = DotGraph.addEdge(src, dst);
        assertTrue("Edge added was not added.", edgeAdded == 0);
    }

    @Test
    public void outputImageTest() throws IOException, InterruptedException {
        boolean fileCreated = DotGraph.outputGraphics(filepath, "png");
        assertTrue("Output graphic file was not created!", fileCreated);
    }

    @Test
    public void outputDOTGraph() throws IOException {
        String filePath = "output.dot";
        DotGraph.outputDOTGraph(filePath);
        File outputFile = new File(filePath);
        assertTrue("The output file was not created.", outputFile.exists()); //checks that the output file was created
        StringBuilder fileContent = new StringBuilder(); //read output file contents
        try (BufferedReader reader = new BufferedReader(new FileReader(outputFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                fileContent.append(line);
            }
        }
        assertTrue("Output DOT file empty!", fileContent.length() > 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeNodeTest() throws IOException {
        //removing node that does exist
        String label = "z";
        DotGraph.addNode(label);
        boolean status = DotGraph.removeNode(label);
        assertTrue("Node 'z' was not successfully removed.", status);

        //removing node that does NOT exist (throws exception)
        label = "t";
        DotGraph.removeNode(label);
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeNodesTest() throws IOException {
        //removing nodes that do exist
        String[] labels1 = {"x", "y", "z"};
        DotGraph.addNodes(labels1);
        int totalRemoved1 = DotGraph.removeNodes(labels1);
        assertEquals("Nodes 'x', 'y', and 'z' were not successfully removed.", 3, totalRemoved1);

        //removing nodes where some exist and some do not exist
        DotGraph.addNodes(labels1);
        String[] labels3 = {"u", "w", "x", "y", "z"};
        int totalRemoved2 = DotGraph.removeNodes(labels3);
        assertEquals("Node 'u' and 'w' were removed when they do not exist", 3, totalRemoved2);

        //removing nodes where none of the nodes exist (throws exception)
        String[] labels2 = {"l", "m", "n", "o"};
        DotGraph.removeNodes(labels2); //throws exception because l, m, n, and o do not exist
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeEdge() throws IOException {
        //removing edge that does exist
        String src = "a";
        String dst = "d";
        boolean result = DotGraph.removeEdge(src, dst);
        assertTrue("Edge was not successfully removed.", result);

        //removing edge that does NOT exist (throws exception)
        src = "t";
        dst = "u";
        DotGraph.removeEdge(src, dst); //throws exception because t->u does not exist
    }

    @Test
    public void GraphSearchTest() throws IOException {
        //successful bfs test
        String src = "a";
        String dst = "f";
        DotGraph.Path result = DotGraph.GraphSearch(src, dst);
        assertNotNull("Path between 'a' and 'e' should exist", result);

        //creating new edge to test invalid path
        src = "g";
        dst = "a";
        DotGraph.addEdge(src, dst);

        //path does not exist
        src = "d";
        dst = "g";
        DotGraph.Path result2 = DotGraph.GraphSearch(src, dst);
        assertNull("Path between 'd' and 'g' should not exist", result2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void GraphSearchBadSrcTest() throws IOException {
        String src = "z";
        String dst = "e";
        DotGraph.Path result = DotGraph.GraphSearch(src, dst);
    }

    @Test(expected = IllegalArgumentException.class)
    public void GraphSearchBadDstTest() throws IOException {
        String src = "a";
        String dst = "z";
        DotGraph.Path result = DotGraph.GraphSearch(src, dst);
    }
}
