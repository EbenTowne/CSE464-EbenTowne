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
        System.out.println("[Graph successfully parsed]");
        reader.close();
    }

    private static void initializeGraph() {
        graph = new DefaultDirectedGraph<>(DefaultEdge.class);
        nodes = new Vector<>();
        System.out.println("\n[Graph initialized]");
    }

    public static String graphtoString(){
        int nodeCount = 0;
        int edgeCount = 0;
        StringBuilder output = new StringBuilder();

        System.out.println("Node List: ");
        output.append("Node List: \n");
        for (String node : nodes) {
            System.out.println(node + ";");
            output.append(node).append(";\n");
            nodeCount++;
        }
        System.out.println("Total node count: " + nodeCount);
        System.out.println("\nEdge List: ");
        output.append("Edge List: \n");
        for (DefaultEdge edge : graph.edgeSet()) {
            String source = graph.getEdgeSource(edge);
            String dest = graph.getEdgeTarget(edge);
            System.out.println(source + " -> " + dest + ";");
            output.append(source).append(" -> ").append(dest).append(";\n");
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
            System.out.println("Adding node: " + label);
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
        System.out.println("[List of nodes added successfully]");
    }

    public static boolean addEdge(String src, String dst){
        if(graph.containsEdge(src, dst)){
            return false;
        }
        addNode(src);
        addNode(dst);
        graph.addEdge(src, dst);
        System.out.println("Added Edge: " + src + " -> " + dst);
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
            graph.removeVertex(label);
            nodes.remove(label);
            System.out.println("Removed node: " + label);
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
            graph.removeEdge(srcLabel, dstLabel);
            System.out.println("Removed edge: " + srcLabel + "->" + dstLabel);
            return true;
        }
        else{
            System.out.println("Edge " + srcLabel + "->" + dstLabel + " does not exist");
            throw new IllegalArgumentException("Edge " + srcLabel + "->" + dstLabel + " was not found in the graph");
        }
    }

    public static class Path{
       List<String> nodes;
        
       public Path(){
           this.nodes = new Vector<>();
       }
       public String toString(){
           return String.join("->", nodes);
       }
       public String randomToString(){
           String result = "Path{nodes=[";
           for(int i = 0; i < nodes.size()-1; i++){
               result += "Node{" + nodes.get(i) + "},";
           }
           result += "Node{" + nodes.get(nodes.size()-1) + "}]}";
           return result;
       }
    }


    public enum Algorithm {
        BFS, DFS, Random
    }

    //Interface for strategy design pattern
    interface TraverseStrategy {
        Path traverse(String src, String dst);
    }

    //Context Class for strategy design pattern
    public static Path GraphSearch(String src, String dst, Algorithm algo) {
        if(!graph.containsVertex(src)){ //if src does not exist
            System.out.println("Source node '" + src + "' does not exist");
            throw new IllegalArgumentException("Source node '" + src + "' does not exist in the graph");
        }
        if(!graph.containsVertex(dst)){ //if dst does not exist
            System.out.println("Destination node '" + dst + "' does not exist");
            throw new IllegalArgumentException("Destination node '" + dst + "' does not exist in the graph");
        }
        
        TraverseStrategy traverseStrategy;
        if (algo == Algorithm.BFS) {
            traverseStrategy = new bfsTraversal();
            System.out.println("Using BFS Strategy");
        }
        else if (algo == Algorithm.DFS) {
            traverseStrategy = new dfsTraversal();
            System.out.println("Using BFS Strategy");
        }
        else if (algo == Algorithm.Random){
            traverseStrategy = new randomTraversal();
            System.out.println("Using Random Strategy");
        }
        else
        {
            System.out.println("Error: Invalid algo!");
            return null;
        }
        return traverseStrategy.traverse(src, dst);
    }

    //abstract class that defines the template method
    //concrete strategy for strategy design pattern
    abstract static class pathTraversalTemplate implements TraverseStrategy {
        //Template Method
        public Path traverse(String src, String dst){
            Path path = new Path();
            createLists(); //Create Queue/Stack
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
                        if (!visited.contains(targetNode)) {
                            List<String> newPath = new ArrayList<>(currPath);
                            newPath.add(targetNode);
                            addPath(newPath);
                        }
                    }
                }
            }
            System.out.println("Error: Path was not found between " + src + " and " + dst + "!");
            return null;
        }

        //Abstract Methods for template design pattern
        abstract void createLists();
        abstract void addPath(List<String> path);
        abstract boolean traversalEmpty();
        abstract List<String> getNextNode();
    }

    //BFS Concrete Class
    static class bfsTraversal extends pathTraversalTemplate {
        //Queue for BFS
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


    static class dfsTraversal extends pathTraversalTemplate {
        //Stack for DFS
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

    static class randomTraversal extends pathTraversalTemplate {
       //Use random to randomly select neighboring node
       Random random;


       @Override
       public Path traverse(String src, String dst){
           random = new Random();
           Path path = new Path();
           while(true){
               //clear path nodes for multiple testings
               path.nodes.clear();
               //add source node to path
               path.nodes.add(src);
               System.out.println("visiting " + path.randomToString());
               String currNode = src;


               //loop indefinitely
               while(!currNode.equals(dst)){
                   Set<DefaultEdge> edges = graph.outgoingEdgesOf(currNode);
                   List<String> neighbors = new ArrayList<>();


                   //add all neighbors for the current node to the list of neighbors
                   for (DefaultEdge edge : edges) {
                       String targetNode = graph.getEdgeTarget(edge);
                       neighbors.add(targetNode);
                   }
                   
                   //if there are no neighbors (cannot go further), reset the search
                   if(neighbors.isEmpty()){
                       break;
                   }
                   
                   //select a random neighbor from the list of neighbors
                   currNode = neighbors.get(random.nextInt(neighbors.size()));
                   path.nodes.add(currNode);
                   System.out.println("visiting " + path.randomToString() + " ");
                   //destination node is found
                   if(currNode.equals(dst)){
                       System.out.println(path.randomToString());
                       return path;
                   }
               }
           }
       }


       @Override
       void createLists() {
           random = new Random();
       }


       @Override
       void addPath(List<String> path) {
       }


       @Override
       boolean traversalEmpty() {
           return false;
       }


       @Override
       List<String> getNextNode() {
           return null;
       }
   }
}

