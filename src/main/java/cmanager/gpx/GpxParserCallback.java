package cmanager.gpx;

import cmanager.geo.Geocache;
import cmanager.geo.Waypoint;
import cmanager.gui.ExceptionPanel;
import cmanager.xml.Element;
import cmanager.xml.XmlParserCallbackInterface;
import java.util.List;
import java.util.concurrent.ExecutorService;

/** Callback class for the GPX parser forwarding the parsing and saving the parsed data. */
public class GpxParserCallback implements XmlParserCallbackInterface {

    /** The parser service to use. */
    private final ExecutorService service;

    /** The list of deserialized geocache instances. */
    private final List<Geocache> geocaches;

    /** The list of deserialized waypoints. */
    private final List<Waypoint> waypoints;

    /**
     * Create a new instance with the given values.
     *
     * @param service The service to use for parsing tasks.
     * @param geocaches The list to write the deserialized geocaches to.
     * @param waypoints The list to write the deserialized waypoints to.
     */
    GpxParserCallback(
            ExecutorService service,
            final List<Geocache> geocaches,
            final List<Waypoint> waypoints) {
        this.service = service;
        this.geocaches = geocaches;
        this.waypoints = waypoints;
    }

    /**
     * Indicate whether the given GPX element is finished.
     *
     * <p>This will schedule the current element for parsing if required.
     *
     * @param element The element to handle.
     * @return Whether handling the element has been finished.
     */
    @Override
    public boolean elementFinished(final Element element) {
        if (!element.is("wpt")) {
            return false;
        }

        // Process the current element.
        service.submit(() -> handleElement(element));

        return true;
    }

    /**
     * Check whether the given GPX element is located correctly.
     *
     * @param element The element to check the location for.
     * @param parent The parent element.
     * @return Whether the given GPX element is located correctly.
     */
    @Override
    public boolean elementLocatedCorrectly(final Element element, final Element parent) {
        // The `gpx` element is the root element. If it does not have a parent element, it is
        // located correctly.
        if (element.is("gpx")) {
            return parent.getName() == null;
        }

        // The `wpt` elements are direct children of the `gpx` element.
        if (element.is("wpt")) {
            return parent.is("gpx");
        }

        return true;
    }

    /**
     * Handle the given element.
     *
     * <p>This will perform the actual parsing and will save the deserialized data inside the
     * respective list.
     *
     * @param element The element to handle.
     */
    private void handleElement(final Element element) {
        Geocache geocache = null;
        Waypoint waypoint = null;

        // Deserialize the given element.
        try {
            waypoint = GpxElementUtil.xmlToWaypoint(element);
            geocache = GpxElementUtil.xmlToCache(element);
        } catch (NullPointerException exception) {
            ExceptionPanel.display(exception);
        }

        // Add the deserialized objects to the list.
        if (geocache != null) {
            synchronized (geocaches) {
                geocaches.add(geocache);
            }
        } else if (waypoint != null) {
            synchronized (waypoints) {
                waypoints.add(waypoint);
            }
        }
    }
}
