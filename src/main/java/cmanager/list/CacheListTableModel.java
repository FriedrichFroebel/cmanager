package cmanager.list;

import cmanager.geo.Geocache;
import cmanager.geo.GeocacheLog;
import cmanager.settings.Settings;
import cmanager.settings.SettingsKey;
import java.time.ZonedDateTime;
import javax.swing.table.AbstractTableModel;

/** Data model for the cache list table. */
public class CacheListTableModel extends AbstractTableModel {

    private static final long serialVersionUID = -6159661237715863643L;

    /** The cache list model. */
    private final CacheListModel cacheListModel;

    /**
     * Create a new instance with the given model.
     *
     * @param cacheListModel The cache list model to use.
     */
    public CacheListTableModel(CacheListModel cacheListModel) {
        this.cacheListModel = cacheListModel;
    }

    /**
     * Get the name string for the given column index.
     *
     * @param columnIndex The colum index to get the name for.
     * @return The name string for the given column.
     */
    public String getColumnName(final int columnIndex) {
        switch (columnIndex) {
            case 0:
                return "Code";
            case 1:
                return "Name";
            case 2:
                return "Type";
            case 3:
                return "Difficulty";
            case 4:
                return "Terrain";
            case 5:
                return "Lat";
            case 6:
                return "Lon";
            case 7:
                return "Owner";
            case 8:
                return "Distance (km)";
            case 9:
                return "Found";
        }

        return null;
    }

    /**
     * Get the data type (class) for the given column index.
     *
     * @param columnIndex The colum index to get the name for.
     * @return The data type (class) for the given column.
     */
    public Class<?> getColumnClass(final int columnIndex) {
        switch (columnIndex) {
            case 0:
            case 1:
            case 2:
                return String.class;

            case 3:
            case 4:
            case 5:
            case 6:
                return Double.class;

            case 7:
                return String.class;

            case 8:
                return Double.class;

            case 9:
                return ZonedDateTime.class;
        }

        return null;
    }

    /**
     * Get the total number of columns.
     *
     * @return The total number of columns. This will always be 10.
     */
    @Override
    public int getColumnCount() {
        return 10;
    }

    /**
     * Get the number of rows.
     *
     * @return The number of rows.
     */
    @Override
    public int getRowCount() {
        return cacheListModel.THIS.size();
    }

    /**
     * Get the geocache instance for the given row index.
     *
     * @param rowIndex The row index to get the geocache instance for.
     * @return The geocache instance for the given row.
     */
    public Geocache getObject(final int rowIndex) {
        return cacheListModel.THIS.get(rowIndex);
    }

    /**
     * Get the value for the given table entry.
     *
     * @param rowIndex The row index to get the geocache instance with.
     * @param columnIndex The column index to get the respective field from the geocache instance
     *     with.
     * @return The requested cell content.
     */
    @Override
    public Object getValueAt(final int rowIndex, final int columnIndex) {
        final Geocache geocache = getObject(rowIndex);

        switch (columnIndex) {
            case 0:
                return geocache.getCode();
            case 1:
                return geocache.getName();
            case 2:
                return geocache.getType().asNiceType();
            case 3:
                return geocache.getDifficulty();
            case 4:
                return geocache.getTerrain();
            case 5:
                return geocache.getCoordinate().getLatitude();
            case 6:
                return geocache.getCoordinate().getLongitude();
            case 7:
                final String owner = geocache.getOwner();
                return owner != null ? owner : "";
            case 8:
                return cacheListModel.relativeLocation != null
                        ? geocache.getCoordinate()
                                .distanceHaversineRounded(cacheListModel.relativeLocation)
                        : "";
            case 9:
                final ZonedDateTime date =
                        geocache.getMostRecentFoundLog(
                                Settings.getString(SettingsKey.GC_USERNAME),
                                Settings.getString(SettingsKey.OC_USERNAME));
                return date == null ? null : GeocacheLog.getDateStrIso8601NoTime(date);

            default:
                return null;
        }
    }
}
