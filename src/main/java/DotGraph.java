import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import java.io.*;
import java.util.*;

public class DotGraph {
    private DefaultDirectedGraph<String, DefaultEdge> graph;
    private List<String> nodes;

    public DotGraph() {
        initializeGraph();
    }

    public void parseGraph(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.contains("->")) {
                    String[] parts = line.replace(";", "").split("->");
                    if (parts.length == 2) {
                        String src = parts[0].trim();
                        String dst = parts[1].trim();
                        addEdge(src, dst);
                    }
                } else if (line.endsWith(";")) {
                    String node = line.replace(";", "").trim();
                    addNode(node);
                }
            }
            System.out.println("[Graph successfully parsed]");
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }

    private void initializeGraph() {
        graph = new DefaultDirectedGraph<>(DefaultEdge.class);
        nodes = new ArrayList<>();
        System.out.println("\n[Graph initialized]");
    }

    public String graphtoString() {
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

    public void outputGraph(String outputPath) {
        String output = graphtoString();
        try (FileWriter writer = new FileWriter(outputPath)) {
            writer.write(output);
            System.out.println("Output successfully written to " + outputPath);
        } catch (IOException e) {
            System.err.println("Error writing file: " + e.getMessage());
        }
    }

    public boolean addNode(String label) {
        if (!graph.containsVertex(label)) {
            graph.addVertex(label);
            nodes.add(label);
            System.out.println("Adding node: " + label);
            return true;
        } else {
            return false;
        }
    }

    public void addNodes(String[] labels) {
        for (String label : labels) {
            addNode(label);
        }
        System.out.println("List of nodes have been added successfully");
    }

    public boolean addEdge(String src, String dst) {
        addNode(src);
        addNode(dst);
        if (!graph.containsEdge(src, dst)) {
            graph.addEdge(src, dst);
            System.out.println("Added Edge: " + src + " -> " + dst);
            return true;
        }
        return false;
    }

    public void outputDOTGraph(String filepath) {
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
        } catch (IOException e) {
            System.err.println("Error writing DOT file: " + e.getMessage());
        }
    }

    public boolean containsNode(String label) {
        return nodes.contains(label);
    }

    public boolean removeNode(String label) {
        if (graph.containsVertex(label)) {
            System.out.println("Removing node: " + label);
            graph.removeVertex(label);
            nodes.remove(label);
            return true;
        } else {
            System.out.println("Node does not exist");
            return false;
        }
    }

    public int removeNodes(String[] labels) {
        int totalRemoved = 0;
        for (String label : labels) {
            if (removeNode(label)) {
                totalRemoved++;
            }
        }
        return totalRemoved;
    }

    public boolean removeEdge(String srcLabel, String dstLabel) {
        if (graph.containsEdge(srcLabel, dstLabel)) {
            System.out.println("Removing edge: " + srcLabel + " -> " + dstLabel);
            graph.removeEdge(srcLabel, dstLabel);
            return true;
        } else {
            System.out.println("Edge " + srcLabel + " -> " + dstLabel + " does not exist");
            return false;
        }
    }

    public static class Path {
        List<String> nodes;

        public Path() {
            this.nodes = new ArrayList<>();
        }

        public String toString() {
            return String.join(" -> ", nodes);
        }
    }

    public enum Algorithm {
        BFS, DFS, Random
    }

    interface TraverseStrategy {
        Path traverse(String src, String dst);
    }

    public Path GraphSearch(String src, String dst, Algorithm algo) {
        if (!graph.containsVertex(src)) {
            throw new IllegalArgumentException("Source node '" + src + "' does not exist in the graph");
        }
        if (!graph.containsVertex(dst)) {
            throw new IllegalArgumentException("Destination node '" + dst + "' does not exist in the graph");
        }

        TraverseStrategy traverseStrategy;
        switch (algo) {
            case BFS:
                traverseStrategy = new bfsTraversal();
                break;
            case DFS:
                traverseStrategy = new dfsTraversal();
                break;
            case Random:
                traverseStrategy = new randomTraversal();
                break;
            default:
                throw new IllegalArgumentException("Unknown traversal algorithm");
        }
        return traverseStrategy.traverse(src, dst);
    }

    abstract static class pathTraversalTemplate implements TraverseStrategy {
        public Path traverse(String src, String dst) {
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
            return null;
        }

        abstract void createLists();

        abstract void addPath(List<String> path);

        abstract boolean traversalEmpty();

        abstract List<String> getNextNode();
    }

    static class bfsTraversal extends pathTraversalTemplate {
        Queue<List<String>> queue;

        @Override
        public void createLists() {
            queue = new LinkedList<>();
        }

        @Override
        public void addPath(List<String> path) {
            queue.add(path);
        }

        @Override
        public boolean traversalEmpty() {
            return queue.isEmpty();
        }

        @Override
        public List<String> getNextNode() {
            return queue.poll();
        }
    }

    static class dfsTraversal extends pathTraversalTemplate {
        Stack<List<String>> stack;

        @Override
        public void createLists() {
            stack = new Stack<>();
        }

        @Override
        public void addPath(List<String> path) {
            stack.push(path);
        }

        @Override
        public boolean traversalEmpty() {
            return stack.isEmpty();
        }

        @Override
        public List<String> getNextNode() {
            return stack.pop();
        }
    }

    static class randomTraversal extends pathTraversalTemplate {
        Random random;

        @Override
        public void createLists() {
            random = new Random();
        }

        @Override
        public void addPath(List<String> path) {
        }

        @Override
        public boolean traversalEmpty() {
            return false;
        }

        @Override
        public List<String> getNextNode() {
            return null;
        }

        @Override
        public Path traverse(String src, String dst) {
            random = new Random();
            Path path = new Path();
            while (true) {
                path.nodes.clear();
                path.nodes.add(src);
                String currNode = src;

                while (!currNode.equals(dst)) {
                    Set<DefaultEdge> edges = graph.outgoingEdgesOf(currNode);
                    List<String> neighbors = new ArrayList<>();

                    for (DefaultEdge edge : edges) {
                        String targetNode = graph.getEdgeTarget(edge);
                        neighbors.add(targetNode);
                    }
                    if (neighbors.isEmpty()) {
                        break;
                    }
                    currNode = neighbors.get(random.nextInt(neighbors.size()));
                    path.nodes.add(currNode);
                    if (currNode.equals(dst)) {
                        return path;
                    }
                }
            }
        }
    }
}

