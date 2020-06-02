package cmanager.list;

import cmanager.geo.Geocache;
import cmanager.geo.GeocacheLog;
import cmanager.settings.Settings;
import java.time.ZonedDateTime;
import javax.swing.table.AbstractTableModel;

public class CacheListTableModel extends AbstractTableModel {

    private static final long serialVersionUID = -6159661237715863643L;

    private final CacheListModel cacheListModel;

    public CacheListTableModel(CacheListModel cacheListModel) {
        this.cacheListModel = cacheListModel;
    }

    public String getColumnName(int column) {
        switch (column) {
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

    public Class<?> getColumnClass(int columnIndex) {
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

    @Override
    public int getColumnCount() {
        return 10;
    }

    @Override
    public int getRowCount() {
        return cacheListModel.THIS.size();
    }

    public Geocache getObject(int row) {
        return cacheListModel.THIS.get(row);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
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
                                Settings.getString(Settings.Key.GC_USERNAME),
                                Settings.getString(Settings.Key.OC_USERNAME));
                return date == null ? null : GeocacheLog.getDateStrIso8601NoTime(date);

            default:
                return null;
        }
    }
}
