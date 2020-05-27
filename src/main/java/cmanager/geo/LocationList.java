package cmanager.geo;

import cmanager.settings.Settings;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LocationList {

    private static LocationList locationList;

    public static LocationList getList() {
        if (locationList == null) {
            locationList = new LocationList();
        }
        return locationList;
    }

    private List<Location> locations = null;

    private LocationList() {}

    private void load() throws ClassNotFoundException, IOException {
        locations = Settings.getSerialized(Settings.Key.LOCATION_LIST);
    }

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

    public void setLocations(ArrayList<Location> newLocations) throws IOException {
        Settings.setSerialized(Settings.Key.LOCATION_LIST, newLocations);
        locations = newLocations;
    }
}
