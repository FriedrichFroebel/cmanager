package cmanager.geo;

import cmanager.exception.CoordinateUnparsableException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Coordinate implements Serializable {

    private static final long serialVersionUID = -3296100424111532545L;

    private final double latitude;
    private final double longitude;

    public Coordinate(String latitude, String longitude) {
        this(Double.parseDouble(latitude), Double.parseDouble(longitude));
    }

    public Coordinate(String input) throws CoordinateUnparsableException {
        final Pattern pattern =
                Pattern.compile(
                        "N\\s*(\\d+)[\\s|°]\\s*((?:\\d+\\.\\d+)|(?:\\d+))'*\\s*"
                                + "E\\s*(\\d+)[\\s|°]\\s*((?:\\d+\\.\\d+)|(?:\\d+))'*\\s*");
        final Matcher matcher = pattern.matcher(input);

        if (!matcher.find()) {
            throw new CoordinateUnparsableException();
        }

        latitude = Double.parseDouble(matcher.group(1)) + Double.parseDouble(matcher.group(2)) / 60;
        longitude =
                Double.parseDouble(matcher.group(3)) + Double.parseDouble(matcher.group(4)) / 60;

        if (matcher.find()) {
            throw new CoordinateUnparsableException();
        }
    }

    public Coordinate(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public boolean equals(Coordinate coordinate) {
        return latitude == coordinate.getLatitude() && longitude == coordinate.getLongitude();
    }

    public String toString() {
        return Double.valueOf(latitude).toString() + ", " + Double.valueOf(longitude).toString();
    }

    public double distanceHaversine(Coordinate other) {
        // "Haversine" distance.
        // http://www.movable-type.co.uk/scripts/latlong.html

        final double radianFactor = 2 * Math.PI / 360;

        final double phi1 = latitude * radianFactor;
        final double phi2 = other.latitude * radianFactor;
        final double deltaPhi = (other.latitude - latitude) * radianFactor;
        final double deltaLambda = (other.longitude - longitude) * radianFactor;

        final double a =
                Math.sin(deltaPhi / 2) * Math.sin(deltaPhi / 2)
                        + Math.cos(phi1)
                                * Math.cos(phi2)
                                * Math.sin(deltaLambda / 2)
                                * Math.sin(deltaLambda / 2);
        final double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        final double R = 6371e3; // metres
        return R * c;
    }

    public double distanceHaversineRounded(Coordinate c2) {
        return round(distanceHaversine(c2), 3);
    }

    public static double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }

        BigDecimal bigDecimal = BigDecimal.valueOf(value);
        bigDecimal = bigDecimal.setScale(places, RoundingMode.HALF_UP);
        return bigDecimal.doubleValue();
    }
}
