package cmanager.geo;

/** Container for a location (= named coordinate). */
public class Location extends Coordinate {

    private static final long serialVersionUID = 1L;

    /** The name of the location. */
    private String name;

    /**
     * Create a new instance with the given values.
     *
     * @param name The name of the location.
     * @param latitude The latitude value.
     * @param longitude The longitude value.
     */
    public Location(final String name, final double latitude, final double longitude)
            throws Exception {
        super(latitude, longitude);
        setName(name);
    }

    /**
     * Set the name of the location.
     *
     * @param name The name of the location.
     */
    public void setName(String name) throws Exception {
        name = name.trim();
        if (name.equals("")) {
            throw new IllegalArgumentException("Name must not be empty.");
        }

        this.name = name;
    }

    /**
     * Get the name of the location.
     *
     * @return The name of the location.
     */
    public String getName() {
        return name;
    }

    /**
     * Get the string representation of the given location.
     *
     * @return The name of the location.
     */
    public String toString() {
        return getName();
    }
}
