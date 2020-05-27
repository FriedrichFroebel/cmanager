package cmanager.geo;

import java.io.Serializable;

public class GeocacheContainerType implements Serializable {

    private static final long serialVersionUID = -7786367688930322460L;

    private static final TypeMap CONTAINER = new TypeMap();

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

    private Integer container;

    public GeocacheContainerType(String type) {
        set(type);
    }

    public void set(String container) {
        if (container == null) {
            return;
        }
        container = container.toLowerCase();
        this.container = CONTAINER.getLowercase(container);
    }

    public String asGc() {
        if (container == null) {
            return null;
        }
        return CONTAINER.get(container, 0);
    }

    public boolean equals(GeocacheContainerType other) {
        return container.equals(other.container);
    }
}
