import java.io.*;
import java.util.*;
import java.nio.file.*;

public class Graph {

    /* Implementation of Graph. */

    private Map<Node, List<Node>> adjNodes;
    private List<Edge> edges;
    private Map<String, Node> nodes;
    private String fileName; // name of the file from which the graph was created. Used in App.java for
                             // setting specific properties of window.

    public Graph(String fileName) {

        /*
         * Graph can be instantiated only from a given textfile.
         * Reads the textfile line by line and creates nodes and edges.
         */

        this.fileName = fileName;

        nodes = new HashMap<>();
        edges = new ArrayList<>();
        adjNodes = new HashMap<>();

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("i")) {
                    int tab1 = line.indexOf('\t');
                    int tab2 = line.indexOf('\t', tab1 + 1);
                    int tab3 = line.indexOf('\t', tab2 + 1);

                    String id = line.substring(tab1 + 1, tab2);
                    double latitude = Double.parseDouble(line.substring(tab2 + 1, tab3));
                    double longitude = Double.parseDouble(line.substring(tab3 + 1));

                    Node node = new Node(id, latitude, longitude);
                    nodes.put(id, node);

                } else if (line.startsWith("r")) {
                    int tab1 = line.indexOf('\t');
                    int tab2 = line.indexOf('\t', tab1 + 1);
                    int tab3 = line.indexOf('\t', tab2 + 1);

                    String id = line.substring(tab1 + 1, tab2);
                    String startNodeId = line.substring(tab2 + 1, tab3);
                    String endNodeId = line.substring(tab3 + 1);

                    Node startNode = nodes.get(startNodeId);
                    Node endNode = nodes.get(endNodeId);

                    if (startNode != null && endNode != null) {
                        Edge edge = new Edge(startNode, endNode, id);
                        edges.add(edge);
                    }
                }
            }

            for (Node node : nodes.values()) {
                adjNodes.put(node, new ArrayList<>());
            }

            for (Edge edge : edges) {
                Node node1 = edge.getStart();
                Node node2 = edge.getEnd();

                adjNodes.get(node1).add(node2);
                adjNodes.get(node2).add(node1);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Node getNodeById(String id) {

        /* Used in Dijkstra invocation in CommandLine to get start and end nodes. */

        return nodes.get(id);
    }

    public double getSmallestX() {
        /*
         * Used for finding origin from which all other nodes and edges are mapped. More
         * in App.java
         */

        double min = Double.POSITIVE_INFINITY;
        for (Node n : nodes.values()) {
            if (n.getLatitude() < min) {
                min = n.getLatitude();
            }
        }
        return min;
    }

    public double getSmallestY() {
        /*
         * Used for finding origin from which all other nodes and edges are mapped. More
         * in App.java
         */
        double min = Double.POSITIVE_INFINITY;
        for (Node n : nodes.values()) {
            if (n.getLongitude() < min) {
                min = n.getLongitude();
            }
        }
        return min;
    }

    public List<Node> getAdjacentNodes(Node node) {
        return adjNodes.get(node);
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public List<Node> getNodes() {
        return new ArrayList<>(nodes.values());
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String FileName) {
        this.fileName = FileName;
    }

    private Edge getEdge(Node start, Node end) {
        for (Edge edge : edges) {
            if (edge.getStart().equals(start) && edge.getEnd().equals(end) ||
                    edge.getStart().equals(end) && edge.getEnd().equals(start)) {
                return edge;
            }
        }
        return null;
    }

    public List<Node> dijkstra(Graph graph, Node source, Node destination) {

        /*
         * Implementation of lazy Dijkstra's algorithm to save space. Saves storage
         * space, instead of storing all nodes in
         * the queue initially, only the closest node and its neighbors are stored.
         */

        Map<Node, Double> distance = new HashMap<>(); // Standard for Dijkstra's algorithm. Stores all known distances
                                                      // from source.
        Map<Node, Node> previous = new HashMap<>(); // Saves the route, periodically updated if a more optimal route is
                                                    // found
        Map<Node, Boolean> visited = new HashMap<>(); // Visited nodes to prevent revisiting.
        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingDouble(distance::get)); // Extract the closest
                                                                                                 // node during each
                                                                                                 // iteration

        // Initialize all distances to infinity and visited to false
        for (Node node : graph.getNodes()) {
            distance.put(node, Double.POSITIVE_INFINITY);
            visited.put(node, false);
        }

        // Distance from source to itself is 0
        distance.put(source, 0.0);

        // Add source node to priority queue
        pq.offer(source);

        while (!pq.isEmpty()) {
            // Initially the source node is polled, afterwards the closest sequential node
            // is polled. Saves storage space.
            Node curr = pq.poll();

            // If destination is reached, print path and return.
            if (curr == destination) {
                List<Node> path = new ArrayList<>();
                Node temp = destination;
                double totalWeight = 0;

                while (temp != null) {
                    path.add(temp);
                    Node prev = previous.get(temp);

                    if (prev != null) {
                        totalWeight += graph.getEdge(prev, temp).getWeight();
                    }

                    temp = prev;
                }

                Collections.reverse(path);
                for (Node n : path) {
                    System.out.print(n.getID() + " ");
                }
                System.out.println("Distance travelled: " + totalWeight + " miles");
                return path;
            }

            if (!visited.get(curr)) {
                visited.put(curr, true);
                // edge relaxation.
                for (Node neighbor : graph.getAdjacentNodes(curr)) {
                    double edgeWeight = graph.getEdge(curr, neighbor).getWeight();
                    double alt = distance.get(curr) + edgeWeight;

                    if (alt < distance.get(neighbor)) {
                        distance.put(neighbor, alt);
                        // update the current route if the path through the new neighbor is more
                        // optimal.
                        previous.put(neighbor, curr);
                        pq.offer(neighbor);
                    }
                }
            }
        }

        System.out.println("No path found from " + source + " to " + destination);
        return null;
    }

}
