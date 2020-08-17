package cmanager.geo;

import cmanager.util.DateTimeUtil;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/** Container for a waypoint. */
public class Waypoint implements Serializable {

    private static final long serialVersionUID = 3154357724453317729L;

    /** The position of the waypoint. */
    private final Coordinate coordinate;

    /** The code of the waypoint. */
    private final String code;

    /** The waypoint description. */
    private final String description;

    /** The waypoint symbol. */
    private final String symbol;

    /** The waypoint type. */
    private final String type;

    /** The parent (geocache) of the waypoint. */
    private String parent;

    /** The date associated with the waypoint. */
    private ZonedDateTime date;

    /**
     * Create a new instance with the given values.
     *
     * @param coordinate The waypoint position.
     * @param code The waypoint code.
     * @param description The waypoint description.
     * @param symbol The waypoint symbol.
     * @param type The waypoint type.
     * @param parent The waypoint parent.
     */
    public Waypoint(
            final Coordinate coordinate,
            final String code,
            final String description,
            final String symbol,
            final String type,
            final String parent) {
        if (code == null) {
            throw new NullPointerException();
        }

        this.coordinate = coordinate;
        this.code = code;
        this.description = description;
        this.symbol = symbol;
        this.type = type;
        this.parent = parent;
        this.date = null;
    }

    /**
     * Set the date.
     *
     * @param date The date to set, in ISO-8601 format.
     */
    public void setDate(final String date) {
        this.date = date == null ? null : DateTimeUtil.parseIsoDateTime(date);
    }

    /**
     * Get the date in ISO-8601 format.
     *
     * @return The date, formatted with ISO-8601.
     */
    public String getDateStrIso8601() {
        if (date == null) {
            return null;
        }

        return date.format(DateTimeFormatter.ISO_INSTANT);
    }

    /**
     * Get the position of the waypoint.
     *
     * @return The position of the waypoint.
     */
    public Coordinate getCoordinate() {
        return coordinate;
    }

    /**
     * Get the code of the waypoint.
     *
     * @return The waypoint code.
     */
    public String getCode() {
        return code;
    }

    /**
     * Get the waypoint description.
     *
     * @return The waypoint description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Get the waypoint symbol.
     *
     * @return The waypoint symbol.
     */
    public String getSymbol() {
        return symbol;
    }

    /**
     * Get the waypoint type.
     *
     * @return The waypoint type.
     */
    public String getType() {
        return type;
    }

    /**
     * Get the parent (geocache) of the waypoint.
     *
     * @return The parent (geocache) of the waypoint.
     */
    public String getParent() {
        return parent;
    }

    /**
     * Set the parent (geocache) of the waypoint.
     *
     * @param parent The parent (geocache) to set.
     */
    public void setParent(final String parent) {
        this.parent = parent;
    }
}
