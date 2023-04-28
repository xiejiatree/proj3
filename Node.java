import java.util.*;
public class Node {
    String ID; // name from txt file
    double latitude; // latitude and longitude
    double longitude;

    public Node(String i, double x, double y) {
        this.ID = i;
        this.latitude = x;
        this.longitude = y;
    }

    public String getID() {
        return ID;
    }

    public void setID(String iD) {
        ID = iD;
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

