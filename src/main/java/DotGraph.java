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
            if (line.contains("->")) {
                String[] parts = line.replace(";", "").split("->");
                if (parts.length == 2) {
                    String src = parts[0].trim();
                    String dst = parts[1].trim();
                    addNode(src);
                    addNode(dst);
                    graph.addEdge(src, dst);
                }
            } else if (line.endsWith(";")) {
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

    public static String graphtoString() {
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
        try (FileWriter writer = new FileWriter(outputPath)) {
            writer.write(output);
            System.out.println("Output successfully written to " + outputPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean addNode(String label) {
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
        for (String label : labels) {
            boolean x = addNode(label);
            if (!x) {
                return;
            }
        }
        System.out.println("List of nodes have been added successfully");
    }

    public static boolean addEdge(String src, String dst) {
        if (graph.containsEdge(src, dst)) {
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
        if (Objects.equals(format, "png")) {
            String outputFile = "output.png";
            ProcessBuilder processBuilder = new ProcessBuilder("dot", "-T" + format, path, "-o", outputFile);
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            System.out.println("DOT Graph Image has been successfully created and written to " + outputFile);
            return true;
        } else {
            System.out.print("This API can only output .DOT graphs as PDF images");
            return false;
        }
    }

    public static class Path {
        List<String> nodes;

        public Path() {
            this.nodes = new Vector<>();
        }

        public String toString() {
            return String.join("->", nodes);
        }

        public String randomToString() {
            String result = "Path{nodes=[";
            for (int i = 0; i < nodes.size() - 1; i++) {
                result += "Node{" + nodes.get(i) + "},";
            }
            result += "Node{" + nodes.get(nodes.size() - 1) + "}]}";
            return result;
        }

        public enum Algorithm {
            BFS, DFS, Random
        }

        interface TraverseStrategy {
            Path traverse(String src, String dst);
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
            void createLists() {
                queue = new LinkedList<>();
            }

            @Override
            void addPath(List<String> path) {
                queue.add(path);
            }

            @Override
            boolean traversalEmpty() {
                return queue.isEmpty();
            }

            @Override
            List<String> getNextNode() {
                return queue.poll();
            }
        }

        static class dfsTraversal extends pathTraversalTemplate {
            Stack<List<String>> stack;

            @Override
            void createLists() {
                stack = new Stack<>();
            }

            @Override
            void addPath(List<String> path) {
                stack.push(path);
            }

            @Override
            boolean traversalEmpty() {
                return stack.isEmpty();
            }

            @Override
            List<String> getNextNode() {
                return stack.pop();
            }
        }

        static class randomTraversal extends pathTraversalTemplate {
            Random random;

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

            @Override
            void createLists() {
                random = new Random();
            }

            @Override
            void addPath(List<String> path) {}

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
}
