package cmanager.list;

import cmanager.geo.Geocache;
import cmanager.geo.Location;
import cmanager.geo.Waypoint;
import cmanager.gpx.Gpx;
import cmanager.list.filter.FilterModel;
import cmanager.util.FileHelper;
import cmanager.util.UndoAction;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class CacheListModel {

    private List<Geocache> list = new ArrayList<>();
    private final LinkedList<Waypoint> orphanedWaypoints = new LinkedList<>();
    final CacheListModel THIS = this;
    Location relativeLocation;

    private boolean reFilteringRequired = true;
    private final List<FilterModel> filters = new ArrayList<>();
    private List<Geocache> listFiltered;

    private static final int MAX_UNDO_COUNT = 300;
    private final List<UndoAction> undoActions = new ArrayList<>();

    private void addCache(Geocache geocache) {
        list.add(geocache);
        matchOrphans(geocache);

        reFilteringRequired = true;
    }

    public void addFilter(FilterModel filter) {
        filter.addRunOnFilterUpdate(() -> reFilteringRequired = true);
        filters.add(filter);

        reFilteringRequired = true;
    }

    public void removeFilter(FilterModel filter) {
        Iterator<FilterModel> iterator = filters.iterator();
        while (iterator.hasNext()) {
            final FilterModel filterModel = iterator.next();
            if (filterModel == filter) {
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
        for (final FilterModel filter : filters) {
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

                        Gpx.loadFromStream(inputStream, geocacheList, waypointList);

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
        Gpx.saveToFile(list, listName, pathToGpx);
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
        return new CacheListTableModel(this);
    }
}
