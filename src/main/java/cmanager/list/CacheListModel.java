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

/** Data model for a cache list. */
public class CacheListModel {

    /** The actual cache list. */
    private List<Geocache> list = new ArrayList<>();

    /** The list of orphaned waypoints. */
    private final LinkedList<Waypoint> orphanedWaypoints = new LinkedList<>();

    /** A pointer to this class instance. */
    final CacheListModel THIS = this;

    /** The relative location. */
    Location relativeLocation;

    /** Indicator whether the list needs to be re-filtered. */
    private boolean reFilteringRequired = true;

    /** The filters to apply. */
    private final List<FilterModel> filters = new ArrayList<>();

    /** The filtered cache list. */
    private List<Geocache> listFiltered;

    /** The maximum number of undo actions allowed. */
    private static final int MAX_UNDO_COUNT = 300;

    /** The undo actions for this list. */
    private final List<UndoAction> undoActions = new ArrayList<>();

    /**
     * Add the given geocache to the list.
     *
     * @param geocache The geocache instance to add.
     */
    private void addCache(final Geocache geocache) {
        list.add(geocache);
        matchOrphans(geocache);

        reFilteringRequired = true;
    }

    /**
     * Add the given filter.
     *
     * @param filter The filter instance to add.
     */
    public void addFilter(final FilterModel filter) {
        filter.addRunOnFilterUpdate(() -> reFilteringRequired = true);
        filters.add(filter);

        reFilteringRequired = true;
    }

    /**
     * Remove the given filter.
     *
     * @param filter The filter to remove.
     */
    public void removeFilter(final FilterModel filter) {
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

    /**
     * Indicate a filter update.
     *
     * <p>This basically sets the "dirty" variable.
     */
    public void filterUpdate() {
        reFilteringRequired = true;
    }

    /**
     * Set the relative location.
     *
     * @param relativeLocation The location to set.
     */
    public void setRelativeLocation(final Location relativeLocation) {
        this.relativeLocation = relativeLocation;
    }

    /**
     * Match the given geocache with the orphaned waypoints.
     *
     * <p>This will add the waypoint to the given geocache and remove the waypoint entry from the
     * list of orphaned waypoints if possible.
     *
     * @param geocache The geocache to match.
     */
    private void matchOrphans(final Geocache geocache) {
        orphanedWaypoints.removeIf(waypoint -> addWaypointToCache(geocache, waypoint));
    }

    /**
     * Add the given waypoint to the given geocache.
     *
     * @param geocache The geocache to add the waypoint to.
     * @param waypoint The waypoint to add.
     * @return `True` if the waypoint belongs to the geocache, `False` otherwise.
     */
    private static boolean addWaypointToCache(final Geocache geocache, final Waypoint waypoint) {
        final String parent = waypoint.getParent();

        if (parent != null) {
            // The waypoint already has a parent, so check if the given geocache is the parent.
            if (geocache.getCode().equals(parent)) {
                geocache.addWaypoint(waypoint);
                return true;
            }
        } else {
            // The waypoint does not yet have a parent, so build the deduced geocache code from the
            // waypoint code and check if the given geocache is the (deduced) parent.
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

    /**
     * Remove the given geocaches from the list.
     *
     * @param removeList The list of geocaches to remove.
     */
    public void removeCaches(final List<Geocache> removeList) {
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

    /**
     * Add the given geocaches to the list.
     *
     * @param addList The list of geocaches to add.
     */
    public void addCaches(final List<Geocache> addList) {
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

    /**
     * Get the filtered list.
     *
     * @return The filtered list.
     */
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

    /** Remove the geocaches from the complete list which do not match the filters. */
    public void removeCachesNotInFilter() {
        recordUndoAction();

        final List<Geocache> filterList = getList();

        list.removeIf(geocache -> !filterList.contains(geocache));
    }

    /**
     * Get the orphaned waypoints.
     *
     * @return The orphaned waypoints.
     */
    public LinkedList<Waypoint> getOrphans() {
        return orphanedWaypoints;
    }

    /**
     * Get the size of the filtered list.
     *
     * @return The size of the filtered list.
     */
    public int size() {
        return getList().size();
    }

    /**
     * Get the requested entry from the filtered list.
     *
     * @param index The index of the entry to retrieve.
     * @return The requested entry from the filtered list.
     */
    public Geocache get(final int index) {
        return getList().get(index);
    }

    /**
     * Load the data from the given GPX file.
     *
     * @param pathToGpx The GPX file to get the data from.
     * @throws Throwable Something went wrong with loading the dat.a
     */
    public void load(final String pathToGpx) throws Throwable {
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

    /**
     * Save the unfiltered list with the given name to the given GPX file.
     *
     * @param listName The name to use for the list.
     * @param pathToGpx The GPX file to write to.
     * @throws Throwable Something went wrong when writing the data.
     */
    public void store(final String listName, final String pathToGpx) throws Throwable {
        Gpx.saveToFile(list, listName, pathToGpx);
    }

    /** Record a new undo action by saving the current state of the unfiltered list. */
    private void recordUndoAction() {
        undoActions.add(new UndoAction(list));
        if (undoActions.size() > MAX_UNDO_COUNT) {
            undoActions.remove(0);
        }
    }

    /** Revert the unfiltered list to the previous (recorded) version. */
    public void replayLastUndoAction() {
        if (undoActions.size() == 0) {
            return;
        }
        final UndoAction action = undoActions.remove(undoActions.size() - 1);
        list = action.getState();
        reFilteringRequired = true;
    }

    /**
     * Get the number of undo actions available.
     *
     * @return The number of undo actions available.
     */
    public int getUndoActionCount() {
        return undoActions.size();
    }

    /**
     * Get the associated table model.
     *
     * @return The associated table model.
     */
    public CacheListTableModel getTableModel() {
        return new CacheListTableModel(this);
    }
}
