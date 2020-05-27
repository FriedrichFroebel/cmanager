package cmanager.gpx;

import cmanager.geo.Geocache;
import cmanager.geo.Waypoint;
import cmanager.gui.ExceptionPanel;
import cmanager.xml.Element;
import cmanager.xml.XmlParserCallbackI;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class GpxParserCallback implements XmlParserCallbackI {

    private final ExecutorService service;
    private final List<Geocache> geocaches;
    private final List<Waypoint> waypoints;

    GpxParserCallback(ExecutorService service, List<Geocache> geocaches, List<Waypoint> waypoints) {
        this.service = service;
        this.geocaches = geocaches;
        this.waypoints = waypoints;
    }

    @Override
    public boolean elementFinished(final Element element) {
        if (!element.is("wpt")) {
            return false;
        }

        // Process the current element.
        service.submit(() -> handleElement(element));

        return true;
    }

    public boolean elementLocatedCorrectly(Element element, Element parent) {
        if (element.is("gpx")) {
            return parent.getName() == null;
        }
        if (element.is("wpt")) {
            return parent.is("gpx");
        }

        return true;
    }

    private void handleElement(Element element) {
        Geocache geocache = null;
        Waypoint waypoint = null;

        try {
            waypoint = GpxElementUtil.xmlToWaypoint(element);
            geocache = GpxElementUtil.xmlToCache(element);
        } catch (NullPointerException exception) {
            ExceptionPanel.display(exception);
        }

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
