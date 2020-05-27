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

public class CustomJMapViewer extends JMapViewer {

    private static final long serialVersionUID = 7714507963907032312L;

    private Point point1 = null;
    private Point point2 = null;

    public CustomJMapViewer(TileCache cache) {
        super(cache);

        // See https://operations.osmfoundation.org/policies/tiles/ for the following requirements.

        // Custom user agent.
        final Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", Constants.HTTP_USER_AGENT);
        super.setTileLoader(new OsmTileLoader(this, headers));

        // Add attribution.
        this.attribution.initialize(new TileAttribution());
    }

    public void setPoints(Point p1, Point p2) {
        this.point1 = p1;
        this.point2 = p2;
        this.repaint();
    }

    @Override
    public void paint(Graphics graphics) {
        super.paint(graphics);

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
