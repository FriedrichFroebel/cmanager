package cmanager.gui.components;

import cmanager.global.Constants;
import cmanager.osm.TileAttribution;
import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.OsmTileLoader;
import org.openstreetmap.gui.jmapviewer.interfaces.TileCache;

/** Viewer for an OSM map. */
public class CustomJMapViewer extends JMapViewer {

    private static final long serialVersionUID = 7714507963907032312L;

    /** The first coordinate for the currently selected rectangle. */
    private Point point1 = null;

    /** The second point for the currently selected rectangle. */
    private Point point2 = null;

    /** Create an instance with the given tile cache. */
    public CustomJMapViewer(final TileCache cache) {
        super(cache);

        // See https://operations.osmfoundation.org/policies/tiles/ for the following requirements.

        // Custom user agent.
        final Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", Constants.HTTP_USER_AGENT);
        super.setTileLoader(new OsmTileLoader(this, headers));

        // Add attribution.
        this.attribution.initialize(new TileAttribution());
    }

    /**
     * Mark a rectangle.
     *
     * @param point1 The start point.
     * @param point2 The end point.
     */
    public void setPoints(Point point1, Point point2) {
        this.point1 = point1;
        this.point2 = point2;
        this.repaint();
    }

    /**
     * Draw the actual map.
     *
     * @param graphics The graphics context to use.
     */
    @Override
    public void paint(final Graphics graphics) {
        super.paint(graphics);

        // Display the currently selected rectangle.
        if (point1 != null && point2 != null) {
            final int x1 = Math.min(point1.x, point2.x);
            final int x2 = Math.max(point1.x, point2.x);
            final int y1 = Math.min(point1.y, point2.y);
            final int y2 = Math.max(point1.y, point2.y);

            final Graphics2D graphics2D = (Graphics2D) graphics;
            graphics2D.setStroke(new BasicStroke(2));
            // graphics2D.setColor(new Color(0x2554C7));
            graphics2D.draw(new Rectangle2D.Double(x1, y1, x2 - x1, y2 - y1));
            graphics2D.dispose();
        }
    }
}
