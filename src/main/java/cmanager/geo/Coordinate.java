package cmanager.geo;

import cmanager.exception.CoordinateUnparsableException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Container for a geographic coordinate. */
public class Coordinate implements Serializable {

    private static final long serialVersionUID = -3296100424111532545L;

    /** The latitude of the coordinate. */
    private final double latitude;

    /** The longitude of the coordinate. */
    private final double longitude;

    /**
     * Create a new instance with the latitude and longitude given by strings which wrap the actual
     * floating point value.
     *
     * @param latitude The latitude value.
     * @param longitude The longitude value.
     */
    public Coordinate(final String latitude, final String longitude) {
        this(Double.parseDouble(latitude), Double.parseDouble(longitude));
    }

    /**
     * Create a new instance with the complete coordinate given a string.
     *
     * @param input The string to get the latitude and the longitude from.
     */
    public Coordinate(final String input) throws CoordinateUnparsableException {
        /* First part:
         *      N\s*(\d+)[\s|°]\s*((?:\d+\.\d+)|(?:\d+))'*\s*
         * This translates to:
         *      * Match the character "N".
         *      * Matches 0 to infinity whitespace characters.
         *      * Capturing Group 1:
         *          * Matches at least one digit.
         *      * Match either at least one whitespace character or the "°" symbol, optionally
         *        followed by some whitespace characters.
         *      * Capturing Group 2:
         *          * 1st variant: Match at least one digit, followed by a "." and at least one
         *            digit again.
         *          * 2nd variant: Match at least one digit.
         *      * Match 0 to infinity "'" characters.
         *      * Match 0 to infinity whitespace characters.
         *
         * Second part:
         *      E\s*(\d+)[\s|°]\s*((?:\d+\.\d+)|(?:\d+))'*\s*
         * This is the same as for the first part, but with the string starting with the character
         * "E" and using capturing group 3 and 4.
         */
        final Pattern pattern =
                Pattern.compile(
                        "N\\s*(\\d+)[\\s|°]\\s*((?:\\d+\\.\\d+)|(?:\\d+))'*\\s*"
                                + "E\\s*(\\d+)[\\s|°]\\s*((?:\\d+\\.\\d+)|(?:\\d+))'*\\s*");
        final Matcher matcher = pattern.matcher(input);

        // Throw an error if the string does not match the given pattern.
        if (!matcher.find()) {
            throw new CoordinateUnparsableException();
        }

        // Capturing groups are numbered, starting with 1.
        // This will convert the existing 42 43.123 value, where 42 corresponds to group 1 or 3, and
        // 43.123 corresponds to group 2 or 4, to decimal format.
        // The resulting value will be the degree value 42 plus the minute value 43.123 / 60, as 1°
        // corresponds to 60 minutes.
        latitude = Double.parseDouble(matcher.group(1)) + Double.parseDouble(matcher.group(2)) / 60;
        longitude =
                Double.parseDouble(matcher.group(3)) + Double.parseDouble(matcher.group(4)) / 60;

        // As this is the second call to the `find` method, the remaining unparsed data will be
        // handled by this. If there is any unparsed data after the first match which matches our
        // pattern as well, throw an error (although the data will already be set).
        if (matcher.find()) {
            throw new CoordinateUnparsableException();
        }
    }

    /**
     * Create a new instance with the latitude and longitude given by their actual floating point
     * values.
     *
     * @param latitude The latitude value.
     * @param longitude The longitude value.
     */
    public Coordinate(final double latitude, final double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * Get the latitude.
     *
     * @return The latitude.
     */
    public Double getLatitude() {
        return latitude;
    }

    /**
     * Get the longitude.
     *
     * @return The longitude.
     */
    public Double getLongitude() {
        return longitude;
    }

    /**
     * Check if the given coordinate equals the current instance.
     *
     * <p>This will check whether the latitude and longitude values match exactly.
     *
     * @param coordinate The coordinate to check against the current instance.
     * @return Whether the given coordinate and the current instance share the same latitude and
     *     longitude value.
     */
    public boolean equals(Coordinate coordinate) {
        return latitude == coordinate.getLatitude() && longitude == coordinate.getLongitude();
    }

    /**
     * Get the given coordinate as a string, by concatenating the latitude and longitude values
     * using `, `.
     *
     * @return The string representation of the coordinate.
     */
    public String toString() {
        return Double.valueOf(latitude).toString() + ", " + Double.valueOf(longitude).toString();
    }

    /**
     * Calculate the haversine distance between the given coordinate and the current instance.
     *
     * <p>The haversine distance is the shortest distance over the earth's surface, while ignoring
     * the actual surface structure (id est mountains and similar).
     *
     * @param other The coordinate to calculate the distance with.
     * @return The distance between the given coordinate and the current instance.
     */
    public double distanceHaversine(final Coordinate other) {
        // "Haversine" distance.
        // http://www.movable-type.co.uk/scripts/latlong.html

        // Factor to convert the degree value into a radian value.
        final double radianFactor = 2 * Math.PI / 360;

        // Convert the latitude values to radian.
        final double phi1 = latitude * radianFactor;
        final double phi2 = other.latitude * radianFactor;

        // Determine the deltas in radian format.
        final double deltaPhi = (other.latitude - latitude) * radianFactor;
        final double deltaLambda = (other.longitude - longitude) * radianFactor;

        // Do the actual (more expensive) calculations.
        final double a =
                Math.sin(deltaPhi / 2) * Math.sin(deltaPhi / 2)
                        + Math.cos(phi1)
                                * Math.cos(phi2)
                                * Math.sin(deltaLambda / 2)
                                * Math.sin(deltaLambda / 2);
        final double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // The average radius of the earth in kilometres.
        final double R = 6371e3;

        // Return the distance in metres.
        return R * c;
    }

    /**
     * Calculate the haversine distance between the given coordinate and the current instance. Round
     * the result to 3 places.
     *
     * @param other The coordinate to calculate the distance with.
     * @return The distance between the given coordinate and the current instance, rounded to 3
     *     places.
     */
    public double distanceHaversineRounded(final Coordinate other) {
        return round(distanceHaversine(other), 3);
    }

    /**
     * Round the given value to the specified amount of places.
     *
     * <p>This will be done with the `HALF_UP` mode, so the value will be rounded up if the current
     * digit is at least 5.
     *
     * @param value The value to round.
     * @param places The number of places to use for the rounded value.
     * @return The given value rounded to the given number of places.
     */
    public static double round(final double value, final int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }

        BigDecimal bigDecimal = BigDecimal.valueOf(value);
        bigDecimal = bigDecimal.setScale(places, RoundingMode.HALF_UP);
        return bigDecimal.doubleValue();
    }
}
