import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import java.io.*;
import java.util.*;

public class DotGraph {
    private static DefaultDirectedGraph<String, DefaultEdge> graph;
    private static Vector<String> nodes;

    public static void parseGraph(String filename) throws IOException {
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
                    addNode(src);
                    addNode(dst);
                    graph.addEdge(src, dst);
                }
            } else if (line.endsWith(";")) {
                // Parse standalone nodes
                String node = line.replace(";", "").trim();
                nodes.add(node);
                graph.addVertex(node);
            }
        }
        System.out.println("Graph has been successfully parsed");
        reader.close();
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
            System.out.println(node + ";");
            nodeCount++;
        }
        System.out.println("Total node count: " + nodeCount);
        System.out.println("\nEdge List: ");
        output.append("Edge List: " + "\n");
        for (DefaultEdge edge : graph.edgeSet()) {
            String source = graph.getEdgeSource(edge);
            String dest = graph.getEdgeTarget(edge);
            System.out.println(source + " -> " + dest + ";");
            edgeCount++;
        }
        System.out.println("Total edge count: " + edgeCount);
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
            return true;
        } else {
            return false;
        }
    }


    public static void addNodes(String[] labels) {
        for(String label : labels){
            boolean x = addNode(label);
            if(!x){
                return;
            }
        }
        System.out.println("List of nodes have been added successfully");
    }

    public static boolean addEdge(String src, String dst){
        if(graph.containsEdge(src, dst)){
            return false;
        }
        addNode(src);
        addNode(dst);
        System.out.println("Source node '" + src + "' and destination node '" + dst + "' have both been added");
        graph.addEdge(src, dst);
        System.out.println("Edge '" + src + "->" + dst + "' has been added");
        return true;
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
            System.out.print(node);
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
            System.out.println("Node does not exist");
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

    public static class Path{
        List<String> nodes;
        //path constructor
        public Path(){
            nodes = new Vector<>();
        }
        public String toString(){
            return String.join("->", nodes);
        }
    }

    public enum Algorithm {
        BFS, DFS
    }

    public static Path GraphSearch(String src, String dst, Algorithm algo){
        if(!graph.containsVertex(src)){ //if src does not exist
            System.out.println("Source node '" + src + "' does not exist");
            throw new IllegalArgumentException("Source node '" + src + "' does not exist in the graph");
        }
        if(!graph.containsVertex(dst)){ //if dst does not exist
            System.out.println("Destination node '" + dst + "' does not exist");
            throw new IllegalArgumentException("Destination node '" + dst + "' does not exist in the graph");
        }
        pathTraversal traversedPath;
        if (algo == Algorithm.BFS) {
            traversedPath = new bfsTraversal();
        }
        else if (algo == Algorithm.DFS) {
            traversedPath = new dfsTraversal();
        }
        else
        {
            return null;
        }
        return traversedPath.traverse(src, dst);
    }

    abstract static class pathTraversal {
        public final Path traverse(String src, String dst){
            Path path = new Path();
            createLists();
            Set<String> visited = new HashSet<>();

            List<String> startPath = new ArrayList<>();
            startPath.add(src);
            addPath(startPath);

            while (!traversalEmpty()) {
                List<String> currPath = getNextNode();
                String currNode = currPath.get(currPath.size() - 1);

                if (currNode.equals(dst)) {
                    path.nodes = new ArrayList<>(currPath);
                    System.out.println("Path Found: " + path.toString());
                    return path;
                }

                if (!visited.contains(currNode)) {
                    visited.add(currNode);
                    for (DefaultEdge edge : graph.outgoingEdgesOf(currNode)) {
                        String targetNode = graph.getEdgeTarget(edge);
                        if(!visited.contains(targetNode)){
                            List<String> newPath = new ArrayList<>(currPath);
                            newPath.add(targetNode);
                            addPath(newPath);
                        }
                    }
                }
            }
            System.out.println("Path was not found between " + src + " and " + dst);
            return null;
        }

        abstract void createLists();
        abstract void addPath(List<String> path);
        abstract boolean traversalEmpty();
        abstract List<String> getNextNode();
    }

    static class bfsTraversal extends pathTraversal {
        Queue<List<String>> queue;

        //used to create queue (unique to bfs)
        @Override
        public void createLists(){
            queue = new LinkedList<>();
        }

        //used to update bfs path (unique given the use of queue)
        @Override
        public void addPath(List<String> path) {
            queue.add(path);
        }

        //check if queue is empty (unique to bfs)
        @Override
        public boolean traversalEmpty() {
            if (queue.isEmpty()) {
                return true;
            }
            return false;
        }

        //get next node (unique given the use of queue)
        @Override
        public List<String> getNextNode(){
            return queue.poll();
        }
    }

    static class dfsTraversal extends pathTraversal {
        Stack<List<String>> stack;

        //used to create stack (unique to dfs)
        @Override
        public void createLists(){
            stack = new Stack<>();
        }

        //updates dfs path (unique given the use of a stack)
        @Override
        public void addPath(List<String> path) {
            stack.push(path);
        }

        //check if stack is empty (unique to dfs)
        @Override
        public boolean traversalEmpty() {
            if (stack.isEmpty()) {
                return true;
            } else {
                return false;
            }
        }

        //get next node in stack (unique to dfs)
        @Override
        public List<String> getNextNode(){
            return stack.pop();
        }
    }
}
