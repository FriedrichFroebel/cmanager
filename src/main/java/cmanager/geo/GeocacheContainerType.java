package cmanager.geo;

import java.io.Serializable;

/** Container for a container size. */
public class GeocacheContainerType implements Serializable {

    private static final long serialVersionUID = -7786367688930322460L;

    /** The known container sizes. */
    private static final TypeMap CONTAINER = new TypeMap();

    // Initialize the map.
    static {
        CONTAINER.add("None");
        CONTAINER.add("Nano");
        CONTAINER.add("Micro");
        CONTAINER.add("Small");
        CONTAINER.add("Regular");
        CONTAINER.add("Large");
        CONTAINER.add("Xlarge");
        CONTAINER.add("Other");
        CONTAINER.add("Virtual");
        CONTAINER.add("Not chosen", "not_chosen");
    }

    /** The container size of the current instance. */
    private Integer container;

    /**
     * Create a new instance for type given by the string.
     *
     * @param type The string/name for the type.
     */
    public GeocacheContainerType(final String type) {
        set(type);
    }

    /**
     * Set the container size given by the string.
     *
     * @param container The string/name for the container size.
     */
    public void set(String container) {
        if (container == null) {
            return;
        }

        // Convert the string to lower-case.
        container = container.toLowerCase();

        // Look up the string inside the map and set the instance.
        this.container = CONTAINER.getLowercase(container);
    }

    /**
     * Get the GC name for the current container size.
     *
     * @return The GC name for the current container size.
     */
    public String asGc() {
        if (container == null) {
            return null;
        }
        return CONTAINER.get(container, 0);
    }

    /**
     * Check whether the given container and the current instance are the same.
     *
     * @param other The container to check against.
     * @return Whether the container sizes are equal.
     */
    public boolean equals(GeocacheContainerType other) {
        return container.equals(other.container);
    }
}
