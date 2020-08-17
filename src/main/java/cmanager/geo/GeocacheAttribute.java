package cmanager.geo;

import java.io.Serializable;

/** Container for a geocache attribute. */
public class GeocacheAttribute implements Serializable {

    private static final long serialVersionUID = 7108209620393435595L;

    /** The ID of the attribute. */
    private final int id;

    /**
     * The "direction" of the attribute.
     *
     * <p>This will be 1 if the attribute is set as positive, and 0 if the attribute is set as
     * negative.
     */
    private final int inc;

    /** The attribute description. */
    private final String description;

    /**
     * Create a new attribute with the given values.
     *
     * @param id The ID of the attribute.
     * @param inc The "direction" of the attribute.
     * @param description The attribute description.
     */
    public GeocacheAttribute(final int id, final int inc, final String description) {
        if (description == null) {
            throw new IllegalArgumentException();
        }

        this.id = id;
        this.inc = inc;
        this.description = description;
    }

    /**
     * Get the ID of the attribute.
     *
     * @return The ID of the attribute.
     */
    public int getId() {
        return id;
    }

    /**
     * Get the "direction" of the attribute.
     *
     * @return The "direction" of the attribute.
     */
    public int getInc() {
        return inc;
    }

    /**
     * Get the attribute description.
     *
     * @return The attribute description.
     */
    public String getDescription() {
        return description;
    }
}
