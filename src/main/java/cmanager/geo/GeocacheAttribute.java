package cmanager.geo;

import java.io.Serializable;

public class GeocacheAttribute implements Serializable {

    private static final long serialVersionUID = 7108209620393435595L;

    private final int id;
    private final int inc; // 1 = positive, 0 = negative.
    private final String description;

    public GeocacheAttribute(int id, int inc, String description) {
        if (description == null) {
            throw new IllegalArgumentException();
        }

        this.id = id;
        this.inc = inc;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public int getInc() {
        return inc;
    }

    public String getDescription() {
        return description;
    }
}
