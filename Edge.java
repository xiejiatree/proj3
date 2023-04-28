import java.lang.Math;
public class Edge {
    Node node1;
    Node node2;
    String id;
    double weight; // distance

    public Node getNode1() {
        return node1;
    }

    public void setNode1(Node node1) {
        this.node1 = node1;
    }

    public Node getNode2() {
        return node2;
    }

    public void setNode2(Node node2) {
        this.node2 = node2;
    }

    public String getID() {
        return this.id;
    }

    public double getWeight(){
        return this.weight;
    }

    double haversine(double latitude1, double longitude1, double latitude2, double longitude2) {
        double d;
        double radius = 3958.756; // earth radius in miles
        latitude1 = Math.toRadians(latitude1);
        longitude1 = Math.toRadians(longitude1);
        latitude2 = Math.toRadians(latitude2);
        longitude2 = Math.toRadians(longitude2);
        d = 2 * radius *
                Math.asin(Math.sqrt(
                        Math.pow(Math.sin((latitude2 - latitude1) / 2), 2) +
                                Math.cos(latitude1) * Math.cos(latitude2) *
                                        Math.pow(Math.sin((longitude2 - longitude1) / 2), 2)));
        return d;
    }

    public Edge(Node o, Node t, String id) {
        this.node1 = o;
        this.node2 = t;
        this.id = id;
        double w = haversine(node1.getLatitude(), node1.getLongitude(), node2.getLatitude(), node2.getLongitude());
        this.weight = w;
    }
}
