import java.io.*;
import java.util.*;
import java.nio.file.*;
import java.util.stream.*;

public class Graph {

    private Map<Node, List<Node>> adjNodes;
    private List<Edge> edges;
    private List<Node> nodes;

    public Graph(List<Node> n, List<Edge> e) {
        adjNodes = new HashMap<>();
        nodes = n;
        edges = e;

        // add all nodes to the graph
        for (Node node : nodes) {
            adjNodes.put(node, new ArrayList<>());
        }
        // add all adjacent nodes
        for (Edge edge : edges) {
            Node node1 = edge.getNode1();
            Node node2 = edge.getNode2();

            adjNodes.get(node1).add(node2);
            adjNodes.get(node2).add(node1);
        }
    }

    public List<Node> getAdjacentNodes(Node node) {
        return adjNodes.get(node);
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public double getWeight(Node node1, Node node2) {
        for (Edge edge : edges) {
            if (edge.getNode1().equals(node1) ||
                    edge.getNode1().equals(node2) ||
                    edge.getNode2().equals(node1) ||
                    edge.getNode2().equals(node2)) {
                return edge.getWeight();
            }
        }
        return -1.0;
    }

    static Node getNodeById(List<Node> nodes, String nodeID, String roadID, boolean start) {
        for (Node n : nodes) {
            if (n.getID().equals(nodeID)) {
                return n;
            }
        }
        if (start) {
            System.out.println("Failed to find correct start Node for road " + roadID);
        } else if (!start) {
            System.out.println("Failed to find correct end Node for road " + roadID);
        }
        return nodes.get(0);
    }

    public Node getNodeById(String nodeID) {
        Node zero = nodes.get(0);
        for (Node n : nodes) {
            if (n.getID().equals(nodeID)) {
                return n;
            }
        }
        System.out.println("NO NODE OF THIS NAME EXISTS");
        return zero;

    }

    // returns a graph.

    static Graph readData(String fileName) throws IOException {
        Map<String, Node> nodes = new HashMap<>();
        List<Edge> edges = new ArrayList<>();

        try {
            // Process nodes
            try (Stream<String> nodeLines = Files.lines(Paths.get(fileName))) {
                nodeLines.filter(line -> line.startsWith("i"))
                        .forEach(line -> {
                            String[] data = line.split("\t");
                            String id = data[1];
                            double latitude = Double.parseDouble(data[2]);
                            double longitude = Double.parseDouble(data[3]);
                            Node node = new Node(id, latitude, longitude);
                            nodes.put(id, node);
                        });
            }

            // Process edges
            try (Stream<String> edgeLines = Files.lines(Paths.get(fileName))) {
                edgeLines.filter(line -> line.startsWith("r"))
                        .forEach(line -> {
                            String[] data = line.split("\t");
                            String id = data[1];
                            String startNodeId = data[2];
                            String endNodeId = data[3];

                            Node startNode = nodes.get(startNodeId);
                            Node endNode = nodes.get(endNodeId);

                            if (startNode != null && endNode != null) {
                                Edge edge = new Edge(startNode, endNode, id);
                                edges.add(edge);
                            }
                        });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Graph g = new Graph(new ArrayList<>(nodes.values()), edges);
        return g;
    }

    public double getSmallestx() {
        double min = nodes.get(0).getLatitude();
        for (Node n : nodes) {
            if (n.getLatitude() < min) {
                min = n.getLatitude();
            }
        }
        return min;
    }

    public double getSmallesty() {
        double min = nodes.get(0).getLongitude();
        for (Node n : nodes) {
            if (n.getLongitude() < min) {
                min = n.getLongitude();
            }
        }
        return min;
    }

    public void dijkstra(Graph graph, Node source, Node destination) {
        Map<Node, Double> distance = new HashMap<>();
        Map<Node, Node> previous = new HashMap<>();
        Map<Node, Boolean> visited = new HashMap<>();
        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingDouble(distance::get));

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
            Node curr = pq.poll();

            // If destination is reached, print path and return
            if (curr == destination) {
                List<Node> path = new ArrayList<>();
                Node temp = destination;
                double totalWeight = 0;

                while (temp != null) {
                    path.add(temp);
                    Node prev = previous.get(temp);

                    if (prev != null) {
                        totalWeight += graph.getWeight(prev, temp);
                    }

                    temp = prev;
                }

                Collections.reverse(path);
                for(Node n: path){
                    System.out.print(n.getID()+ " ");
                }
                System.out.println("Distance travelled: " + totalWeight + " miles");
                return;
            }

            if (!visited.get(curr)) {
                visited.put(curr, true);
                //edge relaxation. 
                for (Node neighbor : graph.getAdjacentNodes(curr)) {
                    double edgeWeight = graph.getWeight(curr, neighbor);
                    double alt = distance.get(curr) + edgeWeight;

                    if (alt < distance.get(neighbor)) {
                        distance.put(neighbor, alt);
                        previous.put(neighbor, curr);
                        pq.offer(neighbor);
                    }
                }
            }
        }

        System.out.println("No path found from " + source + " to " + destination);
    }

}
