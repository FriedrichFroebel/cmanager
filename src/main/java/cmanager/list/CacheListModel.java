package cmanager;

import cmanager.geo.Geocache;
import cmanager.geo.GeocacheLog;
import cmanager.geo.Location;
import cmanager.geo.Waypoint;
import cmanager.gpx.GPX;
import cmanager.settings.Settings;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.joda.time.DateTime;

public class CacheListModel {

    private List<Geocache> list = new ArrayList<>();
    private final LinkedList<Waypoint> orphanedWaypoints = new LinkedList<>();
    private final CacheListModel THIS = this;
    private Location relativeLocation;

    private boolean reFilteringRequired = true;
    private final List<CacheListFilterModel> filters = new ArrayList<>();
    private List<Geocache> listFiltered;

    private final int MAX_UNDO_COUNT = 300;
    private final List<UndoAction> undoActions = new ArrayList<>();

    private void addCache(Geocache geocache) {
        list.add(geocache);
        matchOrphans(geocache);

        reFilteringRequired = true;
    }

    public void addFilter(CacheListFilterModel filter) {
        filter.addRunOnFilterUpdate(
                new Runnable() {
                    public void run() {
                        reFilteringRequired = true;
                    }
                });
        filters.add(filter);

        reFilteringRequired = true;
    }

    public void removeFilter(CacheListFilterModel filter) {
        Iterator<CacheListFilterModel> iterator = filters.iterator();
        while (iterator.hasNext()) {
            final CacheListFilterModel cacheListFilterModel = iterator.next();
            if (cacheListFilterModel == filter) {
                iterator.remove();
                break;
            }
        }

        reFilteringRequired = true;
    }

    public void filterUpdate() {
        reFilteringRequired = true;
    }

    public void setRelativeLocation(Location relativeLocation) {
        this.relativeLocation = relativeLocation;
    }

    private void matchOrphans(Geocache geocache) {
        orphanedWaypoints.removeIf(waypoint -> addWaypointToCache(geocache, waypoint));
    }

    private static boolean addWaypointToCache(Geocache geocache, Waypoint waypoint) {
        final String parent = waypoint.getParent();
        if (parent != null) {
            if (geocache.getCode().equals(parent)) {
                geocache.addWaypoint(waypoint);
                return true;
            }
        } else {
            String name = waypoint.getCode();
            name = name.substring(2);
            name = "GC" + name;

            if (geocache.getCode().equals(name)) {
                geocache.addWaypoint(waypoint);
                return true;
            }
        }
        return false;
    }

    public void removeCaches(List<Geocache> removeList) {
        recordUndoAction();

        for (final Geocache remove : removeList) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i) == remove) {
                    list.remove(i);
                    break;
                }
            }
        }

        reFilteringRequired = true;
    }

    public void addCaches(List<Geocache> addList) {
        recordUndoAction();

        for (final Geocache geocacheAdd : addList) {
            boolean match = false;
            for (final Geocache geocacheOld : list)
                if (geocacheOld.getCode().equals(geocacheAdd.getCode())) {
                    match = true;
                    geocacheOld.update(geocacheAdd);
                    break;
                }
            if (!match) {
                addCache(geocacheAdd);
            }
        }

        reFilteringRequired = true;
    }

    public List<Geocache> getList() {
        if (!reFilteringRequired) {
            return listFiltered;
        }

        List<Geocache> filtered = new ArrayList<>(list);
        for (final CacheListFilterModel filter : filters) {
            filtered = filter.getFiltered(filtered);
        }

        reFilteringRequired = false;
        listFiltered = filtered;
        return filtered;
    }

    public void removeCachesNotInFilter() {
        recordUndoAction();

        final List<Geocache> filterList = getList();

        list.removeIf(geocache -> !filterList.contains(geocache));
    }

    public LinkedList<Waypoint> getOrphans() {
        return orphanedWaypoints;
    }

    public int size() {
        return getList().size();
    }

    public Geocache get(int index) {
        return getList().get(index);
    }

    public void load(String pathToGpx) throws Throwable {
        FileHelper.processFiles(
                pathToGpx,
                new FileHelper.InputAction() {
                    public void process(InputStream inputStream) throws Throwable {
                        final List<Geocache> geocacheList = new ArrayList<>();
                        final List<Waypoint> waypointList = new ArrayList<>();

                        GPX.loadFromStream(inputStream, geocacheList, waypointList);

                        orphanedWaypoints.addAll(waypointList);
                        for (final Geocache geocache : list) {
                            matchOrphans(geocache);
                        }

                        for (final Geocache geocache : geocacheList) {
                            addCache(geocache);
                        }
                    }
                });

        reFilteringRequired = true;
    }

    public void store(String listName, String pathToGpx) throws Throwable {
        GPX.saveToFile(list, listName, pathToGpx);
    }

    private void recordUndoAction() {
        undoActions.add(new UndoAction(list));
        if (undoActions.size() > MAX_UNDO_COUNT) {
            undoActions.remove(0);
        }
    }

    public void replayLastUndoAction() {
        if (undoActions.size() == 0) {
            return;
        }
        final UndoAction action = undoActions.remove(undoActions.size() - 1);
        list = action.getState();
        reFilteringRequired = true;
    }

    public int getUndoActionCount() {
        return undoActions.size();
    }

    public CacheListTableModel getTableModel() {
        return new CacheListTableModel();
    }

    public class CacheListTableModel extends AbstractTableModel {

        private static final long serialVersionUID = -6159661237715863643L;

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
                    return DateTime.class;
            }

            return null;
        }

        @Override
        public int getColumnCount() {
            return 10;
        }

        @Override
        public int getRowCount() {
            return THIS.size();
        }

        public Geocache getObject(int row) {
            return THIS.get(row);
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
                    return relativeLocation != null
                            ? geocache.getCoordinate().distanceHaversineRounded(relativeLocation)
                            : "";
                case 9:
                    final DateTime date =
                            geocache.getMostRecentFoundLog(
                                    Settings.getString(Settings.Key.GC_USERNAME),
                                    Settings.getString(Settings.Key.OC_USERNAME));
                    return date == null ? null : GeocacheLog.getDateStrIso8601NoTime(date);

                default:
                    return null;
            }
        }
    }
}
