package cmanager;

import cmanager.geo.Geocache;
import cmanager.geo.Location;

public class CacheListFilterDistance extends CacheListFilterModel {

    private static final long serialVersionUID = 1L;

    private Double distanceMax;
    private Location location;

    public CacheListFilterDistance() {
        super(FILTER_TYPE.SINGLE_FILTER_VALUE);
        labelLeft2.setText("Maximum distance to location (km): ");
        runDoModelUpdateNow =
                new Runnable() {
                    @Override
                    public void run() {
                        distanceMax = Double.valueOf(textField.getText());
                    }
                };
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    protected boolean isGood(Geocache geocache) {
        if (location == null || distanceMax == null) {
            return true;
        }

        final double distance = geocache.getCoordinate().distanceHaversine(location);
        return distance < distanceMax;
    }
}
