package cmanager.gui.components;

import cmanager.geo.Geocache;
import cmanager.geo.GeocacheType;
import java.awt.Color;
import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.MapMarkerDot;

/** Map marker for a geocache. */
class MapMarkerGeocache extends MapMarkerDot {

    /** The geocache instance for this marker. */
    private final Geocache geocache;

    /**
     * Create a new marker for the given geocache.
     *
     * @param geocache The geocache to create the marker for.
     */
    public MapMarkerGeocache(final Geocache geocache) {
        super(
                new Coordinate(
                        geocache.getCoordinate().getLatitude(),
                        geocache.getCoordinate().getLongitude()));
        this.geocache = geocache;

        setName("");

        if (geocache.getType().equals(GeocacheType.getTradiType())) {
            setColor(new Color(0x009900));
        } else if (geocache.getType().equals(GeocacheType.getMultiType())) {
            setColor(new Color(0xFFCC00));
        } else if (geocache.getType().equals(GeocacheType.getMysteryType())) {
            setColor(new Color(0x0066FF));
        } else {
            setColor(Color.GRAY);
        }
    }

    /**
     * Set the given color for the marker.
     *
     * @param color The color to set.
     */
    public void setColor(final Color color) {
        super.setColor(Color.BLACK);
        super.setBackColor(color);
    }

    /**
     * Get the geocache instance for this marker.
     *
     * @return The associated geocache instance.
     */
    public Geocache getCache() {
        return geocache;
    }
}
