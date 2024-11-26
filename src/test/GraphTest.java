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
        assertEquals(8, totalNodes);
        int totalEdges = DotGraph.getEdges();
        assertEquals(9, totalEdges);
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
        boolean edgeAdded = DotGraph.addEdge(src, dst);
        assertTrue("Edge added was not added.", edgeAdded);


        //Add edge with new nodes
        src = "f";
        dst = "g";
        edgeAdded = DotGraph.addEdge(src, dst);
        assertTrue("Edge added was not added.", edgeAdded);
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
        System.out.println("Attempting to remove node that does not exist:");
        label = "t";
        DotGraph.removeNode(label);
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeNodesTest() throws IOException {
        //removing nodes that do exist
        String[] labels1 = {"x", "y", "z"};
        System.out.println("Adding nodes x, y, and z");
        DotGraph.addNodes(labels1);
        System.out.println("Removing nodes x, y, and z");
        int totalRemoved1 = DotGraph.removeNodes(labels1);
        assertEquals("Nodes 'x', 'y', and 'z' were not successfully removed.", 3, totalRemoved1);

        //removing nodes where some exist and some do not exist
        System.out.println("Adding nodes x, y, and z");
        DotGraph.addNodes(labels1);
        String[] labels3 = {"u", "w", "x", "y", "z"};
        System.out.println("Removing nodes u, w, x, y, and z");
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
        System.out.println("Attempting to remove an edge that does not exist:");
        src = "t";
        dst = "u";
        DotGraph.removeEdge(src, dst); //throws exception because t->u does not exist
    }

    @Test
   public void GraphSearchTest() throws IOException { //updated
       //successful bfs test
       String src = "a";
       String dst = "f";


       //Testing BFS
       DotGraph.Algorithm algo = DotGraph.Algorithm.BFS;
       DotGraph.Path result = DotGraph.GraphSearch(src, dst, algo);
       assertNotNull("Path between 'a' and 'f' should exist for BFS", result);


       //Testing DFS
       algo = DotGraph.Algorithm.DFS;
       result = DotGraph.GraphSearch(src, dst, algo);
       assertNotNull("Path between 'a' and 'f' should exist for DFS", result);


       //creating new edge to test invalid path
       src = "z";
       dst = "a";
       DotGraph.addEdge(src, dst);


       //path does not exist
       src = "d";
       dst = "a";


       //Testing BFS for invalid path
       algo = DotGraph.Algorithm.BFS;
       result = DotGraph.GraphSearch(src, dst, algo);
       assertNull("Path between 'd' and 'a' should not exist", result);


       //Testing DFS for invalid path
       algo = DotGraph.Algorithm.DFS;
       result = DotGraph.GraphSearch(src, dst, algo);
       assertNull("Path between 'd' and 'a' should not exist", result);


       file = "input.dot"; //resource file
       filepath = getClass().getResource(file).getPath();
       DotGraph.parseGraph(filepath);


       src = "a";
       dst = "c";
       algo = DotGraph.Algorithm.Random;
       for(int i = 0; i < 5; i++){
           System.out.println("\nrandom testing");
           result = DotGraph.GraphSearch(src, dst, algo);
       }
       assertNotNull("Path between 'a' and 'c' should not exist", result);
   }

    @Test(expected = IllegalArgumentException.class)
    public void GraphSearchBadSrcTest() throws IOException {
        DotGraph.Algorithm algo = DotGraph.Algorithm.BFS; //only one algorithm is tested due to an error being thrown
        String src = "z";
        String dst = "e";
        DotGraph.Path result = DotGraph.GraphSearch(src, dst, algo);
    }

    @Test(expected = IllegalArgumentException.class)
    public void GraphSearchBadDstTest() throws IOException {
        DotGraph.Algorithm algo = DotGraph.Algorithm.BFS; //only one algorithm is tested due to an error being thrown
        String src = "a";
        String dst = "z";
        DotGraph.Path result = DotGraph.GraphSearch(src, dst, algo);
    }
}
