package cmanager.osm;

import cmanager.gui.ExceptionPanel;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.imageio.ImageIO;
import org.joda.time.DateTime;
import org.openstreetmap.gui.jmapviewer.MemoryTileCache;
import org.openstreetmap.gui.jmapviewer.Tile;
import org.openstreetmap.gui.jmapviewer.interfaces.ICoordinate;
import org.openstreetmap.gui.jmapviewer.interfaces.TileCache;
import org.openstreetmap.gui.jmapviewer.interfaces.TileSource;

public class PersistentTileCache implements TileCache {

    String path;

    final MemoryTileCache memoryTileCache = new MemoryTileCache();
    final ExecutorService service = Executors.newFixedThreadPool(10);

    private boolean online = false;
    private boolean firstTileServed = false;

    public PersistentTileCache(String path) {
        this.path = path + "/";
    }

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

    @Override
    public void clear() {
        memoryTileCache.clear();
    }

    @Override
    public int getCacheSize() {
        return memoryTileCache.getCacheSize();
    }

    @Override
    public Tile getTile(TileSource source, int x, int y, int z) {
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
            // Reload if is older than 3 month.
            DateTime fileTime = new DateTime(file.lastModified());
            final DateTime now = new DateTime();
            fileTime = fileTime.plusMonths(3);
            if (fileTime.isBefore(now)) {
                // only enforce only tile download/update if we are only
                if (online) {
                    return null;
                }
            }

            tile = new Tile(source, x, y, z);
            try {
                tile.loadImage(new FileInputStream(fileName));
                tile.initLoading();
            } catch (IOException e) {
                tile = null;
            }
        }
        return tile;
    }

    @Override
    public int getTileCount() {
        return memoryTileCache.getTileCount();
    }

    private String getFileName(TileSource source, int x, int y, int z) {
        final ICoordinate coordinate = source.tileXYToLatLon(x, y, z);
        return getFileName(coordinate.getLat(), coordinate.getLon(), z);
    }

    private String getFileName(Tile tile) {
        final ICoordinate coordinate = tile.getSource().tileXYToLatLon(tile);
        return getFileName(coordinate.getLat(), coordinate.getLon(), tile.getZoom());
    }

    private String getFileName(Double latitude, Double longitude, Integer zoom) {
        return path
                + latitude.toString()
                + "-"
                + longitude.toString()
                + "-"
                + zoom.toString()
                + ".png";
    }
}
