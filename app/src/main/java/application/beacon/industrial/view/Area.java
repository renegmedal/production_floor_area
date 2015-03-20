package application.beacon.industrial.view;

/**
 * Created by rene on 2/9/15.
 */
public class Area implements LayoutArea {

    private String name;
    private String beaconId;
    private int x;
    private int y;

    public Area(String name, String beaconId) {
        this.name = name;
        this.beaconId = beaconId;
    }

    public Area(String name, String beaconId, int x, int y) {
        this.name = name;
        this.beaconId = beaconId;
        this.x = x;
        this.y = y;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBeaconId() {
        return beaconId;
    }

    public void setBeaconId(String beaconId) {
        this.beaconId = beaconId;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
