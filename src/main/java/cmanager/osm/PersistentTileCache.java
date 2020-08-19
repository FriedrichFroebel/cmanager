package cmanager.osm;

import cmanager.gui.ExceptionPanel;
import cmanager.util.DateTimeUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.imageio.ImageIO;
import org.openstreetmap.gui.jmapviewer.MemoryTileCache;
import org.openstreetmap.gui.jmapviewer.Tile;
import org.openstreetmap.gui.jmapviewer.interfaces.ICoordinate;
import org.openstreetmap.gui.jmapviewer.interfaces.TileCache;
import org.openstreetmap.gui.jmapviewer.interfaces.TileSource;

/** OSM tile caching. */
public class PersistentTileCache implements TileCache {

    /** The directory to use as cache. */
    String path;

    /** The in-memory cache. */
    final MemoryTileCache memoryTileCache = new MemoryTileCache();

    /** The tile loader service. */
    final ExecutorService service = Executors.newFixedThreadPool(10);

    /** Whether the application is online or not. */
    private boolean online = false;

    /**
     * Whether the first tile has already been served.
     *
     * <p>This is used for checking the online status.
     */
    private boolean firstTileServed = false;

    /**
     * Create a new instance with the given path.
     *
     * @param path The cache directory to use. This cannot have a trailing slash!
     */
    public PersistentTileCache(final String path) {
        this.path = path + "/";
    }

    /**
     * Add the given tile to the cache.
     *
     * @param tile The tile to add.
     */
    @Override
    public void addTile(final Tile tile) {
        // A tile has been downloaded thus we are online.
        if (!online) {
            online = true;
            memoryTileCache.clear();
        }

        service.submit(() -> loadTile(tile));

        memoryTileCache.addTile(tile);
    }

    /**
     * Load the given tile and store it on disk.
     *
     * @param tile The tile to load.
     */
    private void loadTile(final Tile tile) {
        // Wait for tile to load.
        while (!tile.isLoaded() && !tile.hasError()) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException ignored) {
            }
        }

        // Skip tile on error.
        if (tile.hasError()) {
            return;
        }

        // Store tile to disk.
        final String fileName = getFileName(tile);
        final File outputFile = new File(fileName);
        try {
            outputFile.mkdirs();
            ImageIO.write(tile.getImage(), "png", outputFile);
        } catch (Exception exception) {
            ExceptionPanel.showErrorDialog(null, exception);
        }
    }

    /** Clear the in-memory cache. */
    @Override
    public void clear() {
        memoryTileCache.clear();
    }

    /**
     * Get the size of the in-memory cache.
     *
     * @return The size of the cache.
     */
    @Override
    public int getCacheSize() {
        return memoryTileCache.getCacheSize();
    }

    /**
     * Retrieve the requested tile from the cache.
     *
     * @param source The tile source.
     * @param x The tile number on the X axis of the tile to be retrieved.
     * @param y The tile number on the Y axis of the tile to be retrieved.
     * @param z The zoom level of the tile to be retrieved.
     * @return The requested tile or <code>null</code> if the tile is not present in the cache.
     */
    @Override
    public Tile getTile(final TileSource source, final int x, final int y, final int z) {
        // Deny serving very first tile in order to trigger download for this tile and thus to
        // check whether we are online. This tile is unimportant since the display of JMapViewer is
        // relocated after adding caches.
        if (!firstTileServed) {
            firstTileServed = true;
            return null;
        }

        // Tile in memory cache?
        Tile tile = memoryTileCache.getTile(source, x, y, z);
        if (tile != null) {
            return tile;
        }

        // Tile on disk?
        final String fileName = getFileName(source, x, y, z);
        final File file = new File(fileName);
        if (file.exists()) {
            // Reload if is older than 3 months.
            if (DateTimeUtil.isTooOldWithMonths(file, 3)) {
                // Only enforce tile download/update if we are online.
                if (online) {
                    return null;
                }
            }

            tile = new Tile(source, x, y, z);
            try {
                tile.loadImage(new FileInputStream(fileName));
                tile.initLoading();
            } catch (IOException exception) {
                tile = null;
            }
        }
        return tile;
    }

    /**
     * Get the number of tiles inside the in-memory cache.
     *
     * @return The number of tiles inside the in-memory cache.
     */
    @Override
    public int getTileCount() {
        return memoryTileCache.getTileCount();
    }

    /**
     * Get the name of the file for the requested tile.
     *
     * @param source The tile source.
     * @param x The tile number on the X axis of the tile to be retrieved.
     * @param y The tile number on the Y axis of the tile to be retrieved.
     * @param z The zoom level of the tile to be retrieved.
     * @return The name of the file for the requested tile.
     */
    private String getFileName(final TileSource source, final int x, final int y, final int z) {
        final ICoordinate coordinate = source.tileXYToLatLon(x, y, z);
        return getFileName(coordinate.getLat(), coordinate.getLon(), z);
    }

    /**
     * Get the name of the file for the requested tile.
     *
     * @param tile The tile to get the filename for.
     * @return The name of the file for the requested tile.
     */
    private String getFileName(final Tile tile) {
        final ICoordinate coordinate = tile.getSource().tileXYToLatLon(tile);
        return getFileName(coordinate.getLat(), coordinate.getLon(), tile.getZoom());
    }

    /**
     * Get the name of the file for the requested tile.
     *
     * @param latitude The latitude of the tile to be retrieved.
     * @param longitude The longitude of the tile to be retrieved.
     * @param zoom The zoom level of the tile to be retrieved.
     * @return The name of the file for the requested tile.
     */
    private String getFileName(final Double latitude, Double longitude, Integer zoom) {
        return path
                + latitude.toString()
                + "-"
                + longitude.toString()
                + "-"
                + zoom.toString()
                + ".png";
    }
}
