import java.io.*;
import java.util.*;
import java.nio.file.*;
import java.util.stream.*;

public class Graph {

    private Map<Node, List<Node>> adjNodes;
    private List<Edge> edges;
    private List<Node> nodes;
    private String fileName;

    public Graph(String fileName) {
        this.fileName = fileName;

        nodes = new ArrayList<>();
        edges = new ArrayList<>();
        adjNodes = new HashMap<Node, List<Node>>();

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
                            nodes.add(node);
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

                            Node startNode = getNodeById(startNodeId);
                            Node endNode = getNodeById(endNodeId);

                            if (startNode != null && endNode != null) {
                                Edge edge = new Edge(startNode, endNode, id);
                                edges.add(edge);
                            }
                        });
            }
            // add all nodes to the graph

            for (Node node : nodes) {
                System.out.println("adding node");
                adjNodes.put(node, new ArrayList<>());
            }
            // add all adjacent nodes
            for (Edge edge : edges) {
                System.out.println("adding edge");
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
        for (Node node : nodes) {
            if (node.getID().equals(id)) {
                return node;
            }
        }
        System.out.println("NO NODE OF THIS NAME EXISTS");
        return null;

    }

    public double getSmallestX() {
        double min = nodes.get(0).getLatitude();
        for (Node n : nodes) {
            if (n.getLatitude() < min) {
                min = n.getLatitude();
            }
        }
        return min;
    }

    public double getSmallestY() {
        double min = nodes.get(0).getLongitude();
        for (Node n : nodes) {
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
        return nodes;
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
