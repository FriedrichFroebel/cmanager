package cmanager.geo;

import java.io.Serializable;

/** Container for a geocache type. */
public class GeocacheType implements Serializable {

    private static final long serialVersionUID = 3852716526968673048L;

    /** The known geocache types. */
    private static final TypeMap TYPE = new TypeMap();

    // Initialize the map.
    static {
        //     pretty name       GC                 OC
        TYPE.add("Tradi", "Traditional Cache", "Traditional");
        TYPE.add("Drive-In", null, "Drive-In");
        TYPE.add("Moving", null, "Moving");
        TYPE.add("Other", null, "Other");
        TYPE.add("Math/Physics", null, "Math/Physics");
        TYPE.add("Multi", "Multi-cache", "Multi");
        TYPE.add("Mystery", "Unknown Cache", "Quiz");

        TYPE.add("Virtual", "Virtual Cache", "Virtual");
        TYPE.add("Webcam", "Webcam Cache", null);
        TYPE.add("Earth", "Earthcache", null);
        TYPE.add("Reverse", null, "Locationless (Reverse) Cache");

        TYPE.add("L.Box", "Letterbox Hybrid", null);
        TYPE.add("Event", "Event Cache", "Event");
        TYPE.add("Mega-Event", "Mega-Event Cache", null);
        TYPE.add("Giga-Event", "Giga-Event Cache", null);
        TYPE.add("CITO", "Cache In Trash Out Event", null);
        TYPE.add("Wherigo", "Wherigo Cache", null);
        TYPE.add("GPS AE", "GPS Adventures Exhibit", null);
        TYPE.add("Project Ape", "Project Ape Cache", null);
        TYPE.add("Community Celebration", "Community Celebration Event", null);
    }

    /**
     * Get the type for a traditional cache.
     *
     * @return The type for a traditional cache.
     */
    public static GeocacheType getTradiType() {
        return new GeocacheType("Tradi");
    }

    /**
     * Get the type for a multi cache.
     *
     * @return The type for a multi cache.
     */
    public static GeocacheType getMultiType() {
        return new GeocacheType("Multi");
    }

    /**
     * Get the type for a mystery cache.
     *
     * @return The type for a mystery cache.
     */
    public static GeocacheType getMysteryType() {
        return new GeocacheType("Mystery");
    }

    /** The geocache type of the current instance. */
    private final int type;

    /**
     * Create a new instance for the given type.
     *
     * @param type The geocache type.
     */
    public GeocacheType(final int type) {
        this.type = type;
    }

    /**
     * Create a new instance for the type given by the string.
     *
     * @param type The type string.
     */
    public GeocacheType(final String type) {
        this.type = TYPE.getLowercase(type);
    }

    /** Get the nice (colloquial) type name for the current instance. */
    public String asNiceType() {
        return TYPE.get(type, 0);
    }

    /**
     * Get the GC name for the current instance.
     *
     * <p>This falls back to the OC name for attributes not available on GC.
     *
     * @return The GC name.
     */
    public String asGcType() {
        final String gc = TYPE.get(type, 1);
        return gc != null ? gc : TYPE.get(type, 2);
    }

    /**
     * Check whether this is one of the available event types.
     *
     * @return Whether this is an event type, id est the GC type contains the substring "Event".
     */
    public boolean isEventType() {
        return asGcType().contains("Event");
    }

    /**
     * Check whether the given type and the current instance are the same.
     *
     * @param other The type to check against.
     * @return Whether the types are equal.
     */
    public boolean equals(GeocacheType other) {
        return this.type == other.type;
    }
}
