public class Node {
    private String id;
    private double latitude;
    private double longitude;

    public Node(String id, double latitude, double longitude) {

        /*
         * Generic Implementation of Node class.
         * It is public because it used in App.java.
         */

        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getID() {
        return id;
    }

    public void setID(String id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
