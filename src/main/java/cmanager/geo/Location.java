package cmanager.geo;

public class Location extends Coordinate {

    private static final long serialVersionUID = 1L;

    private String name;

    public Location(String name, double latitude, double longitude) throws Exception {
        super(latitude, longitude);
        setName(name);
    }

    public void setName(String name) throws Exception {
        name = name.trim();
        if (name.equals("")) {
            throw new IllegalArgumentException("Name must not be empty.");
        }

        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return getName();
    }
}
