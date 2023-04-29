import java.lang.Math;

public class Edge {
    private Node start;
    private Node end;
    private String id;
    private double weight;

    public Edge(Node start, Node end, String id) {
        this.start = start;
        this.end = end;
        this.id = id;
        this.weight = haversine(start.getLatitude(), start.getLongitude(), end.getLatitude(), end.getLongitude());
    }

    public Node getStart() {
        return this.start;
    }

    public void setStart(Node start) {
        this.start = start;
    }

    public Node getEnd() {
        return this.end;
    }

    public void setEnd(Node end) {
        this.end = end;
    }

    public String getID() {
        return this.id;
    }

    public double getWeight() {
        return this.weight;
    }

    private double haversine(double latitude1, double longitude1, double latitude2, double longitude2) {
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

}
