package cmanager.list.filter;

import cmanager.geo.Geocache;
import cmanager.geo.Location;

/** Filter geocaches by the distance from the given location. */
public class DistanceFilter extends FilterModel {

    private static final long serialVersionUID = 1L;

    /** The maximum distance allowed (in kilometres). */
    private Double distanceMax;

    /** The location to determine the distance to. */
    private Location location;

    /** Create a new instance of the filter. */
    public DistanceFilter() {
        super(FILTER_TYPE.SINGLE_FILTER_VALUE);
        labelLeft2.setText("Maximum distance to location (km): ");
        runDoModelUpdateNow = () -> distanceMax = Double.valueOf(textField.getText());
    }

    /**
     * Set the location to determine the distance to.
     *
     * @param location The location to set.
     */
    public void setLocation(final Location location) {
        this.location = location;
    }

    /**
     * Check whether the given geocache is within the given range from the location.
     *
     * @param geocache The geocache to check.
     * @return The check result.
     */
    @Override
    protected boolean isGood(final Geocache geocache) {
        if (location == null || distanceMax == null) {
            return true;
        }

        final double distance = geocache.getCoordinate().distanceHaversine(location);
        return distance < distanceMax;
    }
}
