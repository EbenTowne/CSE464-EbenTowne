import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import java.io.*;
import java.util.Objects;
import java.util.Vector;

public class DotGraph {

    public static class Path{
        Vector<String> nodes;

        public Path(){
            nodes = new Vector<>();
        }

        public void addNode(String node){
            nodes.add(node);
        }

        public String toString(){
            return String.join("->", nodes);
        }
    }

    static DefaultDirectedGraph<String, DefaultEdge> graph;
    static Vector<String> nodes;

    public static DefaultDirectedGraph<String, DefaultEdge> parseGraph(String filename) throws IOException {
        initializeGraph();
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line;

        while ((line = reader.readLine()) != null) {
            line = line.trim();
            // Check for node labels by identifying "node;" format
            if (line.contains("->")) {
                // Parse edges in the format "a -> b;"
                String[] parts = line.replace(";", "").split("->");
                if (parts.length == 2) {
                    String src = parts[0].trim();
                    String dst = parts[1].trim();
                    //System.out.println(src + " -> " + dst);
                    if (!nodes.contains(src)) {
                        nodes.add(src);
                        graph.addVertex(src);
                    }
                    if (!nodes.contains(dst)) {
                        nodes.add(dst);
                        graph.addVertex(dst);
                    }
                    graph.addEdge(src, dst);
                }
            } else if (line.endsWith(";")) {
                // Parse standalone nodes
                String node = line.replace(";", "").trim();
                if (!nodes.contains(node)) {
                    nodes.add(node);
                    graph.addVertex(node);
                    //System.out.println(node);
                }
            }
        }
        System.out.println("Graph has been successfully parsed");
        reader.close();
        return graph;
    }

    private static void initializeGraph() {
        graph = new DefaultDirectedGraph<>(DefaultEdge.class);
        nodes = new Vector<>();
        System.out.println("\nGraph initialized");
    }

    public static String graphtoString(){
        int nodeCount = 0;
        int edgeCount = 0;
        StringBuilder output = new StringBuilder();

        System.out.println("Node List: ");
        output.append("Node List: " + "\n");
        for (String node : nodes) {
            System.out.println(node);
            output.append(node).append("; " + "\n");
            nodeCount++;
        }
        output.append("Total node count: ").append(nodeCount + "\n");
        System.out.println("\nEdge List: ");
        output.append("Edge List: " + "\n");
        for (DefaultEdge edge : graph.edgeSet()) {
            String source = graph.getEdgeSource(edge);
            String dest = graph.getEdgeTarget(edge);
            System.out.println(source + " -> " + dest);
            output.append(source).append(" -> ").append(dest).append("; " + "\n");
            edgeCount++;
        }
        System.out.println("Total edge count: " + edgeCount);
        output.append("Total edge count: ").append(edgeCount + "\n");
        return output.toString();
    }

    public static void outputGraph(String outputPath) {
        String output = DotGraph.graphtoString();
        try(FileWriter writer = new FileWriter(outputPath)){
            writer.write(output);
            System.out.println("Output successfully written to " + outputPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean addNode(String label){
        if (!graph.containsVertex(label)) {
            graph.addVertex(label);
            nodes.add(label);
            System.out.println("Node added: " + label);
        } else {
            return false;
        }
        return true;
    }


    public static boolean addNodes(String[] labels) {
        for(String label : labels){
            boolean x = addNode(label);
            if(x == false){
                return false;
            }
        }
        System.out.println("List of nodes have been added successfully");
        return true;
    }

    public static int addEdge(String src, String dst){
        if(graph.containsEdge(src, dst)){
            return 1;
        }
        if(!containsNode(src)){
            addNode(src);
            System.out.println("Source node '" + src + "' has been added");
        }
        if(!containsNode(dst)){
            addNode(dst);
            System.out.println("Destination node '" + dst + "' has been added");
        }
        graph.addEdge(src, dst);
        System.out.println("Edge '" + src + "->" + dst + "' has been added");
        return 0;
    }

    public static void outputDOTGraph(String filepath) throws IOException {
        try (FileWriter fileWriter = new FileWriter(filepath)) {
            fileWriter.write("digraph G {\n");
            for (String node : nodes) {
                fileWriter.write("    " + node + ";\n");
            }
            for (DefaultEdge edge : graph.edgeSet()) {
                String source = graph.getEdgeSource(edge);
                String dest = graph.getEdgeTarget(edge);
                fileWriter.write("    " + source + " -> " + dest + ";\n");
            }
            fileWriter.write("}\n");
            System.out.println("DOT Graph has been created and written to " + filepath);
        }
    }
    public static boolean outputGraphics(String path, String format) throws IOException, InterruptedException {
        if(Objects.equals(format, "png")){
            String outputFile = "output.png";
            ProcessBuilder processBuilder = new ProcessBuilder("dot", "-T" + format, path, "-o", outputFile);
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            System.out.println("DOT Graph Image has been successfully created and written to " + outputFile);
            return true;
        }
        else{
            System.out.print("This API can only output .DOT graphs as PDF images");
            return false;
        }
    }

    public static int getNodes(){
        int count = 0;
        for (String node : nodes) {
            count++;
        }
        return count;
    }

    public static int getEdges(){
        int count = 0;
        for (DefaultEdge edge : graph.edgeSet()) {
            count++;
        }
        return count;
    }

    public static boolean containsNode(String label) {
        for (String node : nodes) {
            if (node == label) {
                return true;
            }
        }
        return false;
    }

    public static boolean removeNode(String label) {
        if(graph.containsVertex(label)){
            System.out.println("Removing node " + label);
            graph.removeVertex(label);
            nodes.remove(label);
            return true;
        }
        else{
            System.out.println("Node " + label + " does not exist");
            throw new IllegalArgumentException("Node " + label + " does not exist in the graph");
        }
    }

    public static int removeNodes(String[] labels) {
        int totalRemoved = 0;
        for(String label : labels){
            boolean result = removeNode(label);
            if(result){
                totalRemoved++;
            }
        }
        return totalRemoved;
    }

    public static boolean removeEdge(String srcLabel, String dstLabel){
        if(graph.containsEdge(srcLabel, dstLabel)){
            System.out.println("Removing edge " + srcLabel + "->" + dstLabel);
            graph.removeEdge(srcLabel, dstLabel);
            return true;
        }
        else{
            System.out.println("Edge " + srcLabel + "->" + dstLabel + " does not exist");
            throw new IllegalArgumentException("Edge " + srcLabel + "->" + dstLabel + " was not found in the graph");
        }
    }

    public static Path GraphSearch(String src, String dst){
        if(!graph.containsVertex(src)){
            System.out.println("Source node '" + src + "' does not exist");
            throw new IllegalArgumentException("Source node '" + src + "' does not exist in the graph");
        }
        if(!graph.containsVertex(dst)){
            System.out.println("Destination node '" + dst + "' does not exist");
            throw new IllegalArgumentException("Destination node '" + dst + "' does not exist in the graph");
        }

        Path path = new Path();

        Vector<String> startPath = new Vector<>();
        Vector<Vector<String>> queue = new Vector<>();
        Vector<String> visited = new Vector<>();

        startPath.add(src);
        queue.add(startPath);

        while(!queue.isEmpty()){
            Vector<String> currPath = queue.remove(0);
            String currNode = currPath.lastElement();

            if(currNode.equals(dst)){
                path.nodes = currPath;
                return path;
            }
            if(!visited.contains(currNode)) {
                visited.add(currNode);

                for (DefaultEdge edge : graph.outgoingEdgesOf(currNode)) {
                    String targetNode = graph.getEdgeTarget(edge);

                    if (!visited.contains(targetNode)) {
                        Vector<String> newPath = new Vector<>(currPath);
                        newPath.add(targetNode);
                        queue.add(newPath);
                    }
                }
            }
        }
        return null;
    }
}
