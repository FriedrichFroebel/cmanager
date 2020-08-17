package cmanager.geo;

import cmanager.settings.Settings;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** A list of locations (= named coordinates). */
public class LocationList {

    /** The static class instance. */
    private static LocationList locationList;

    /**
     * Get the location list.
     *
     * @return The location list.
     */
    public static LocationList getList() {
        if (locationList == null) {
            locationList = new LocationList();
        }

        return locationList;
    }

    /** The actual list. */
    private List<Location> locations = null;

    private LocationList() {}

    /** Load the location list from the settings. */
    private void load() throws ClassNotFoundException, IOException {
        locations = Settings.getSerialized(Settings.Key.LOCATION_LIST);
    }

    /**
     * Get the locations.
     *
     * @return The locations.
     */
    public List<Location> getLocations() {
        if (locations == null) {
            try {
                load();
            } catch (ClassNotFoundException | IOException ignored) {
            }
        }

        // Loading failed.
        if (locations == null) {
            locations = new ArrayList<>();
        }

        return locations;
    }

    /**
     * Set the given locations and save it.
     *
     * @param newLocations The locations to set.
     */
    public void setLocations(final ArrayList<Location> newLocations) throws IOException {
        Settings.setSerialized(Settings.Key.LOCATION_LIST, newLocations);
        locations = newLocations;
    }
}
