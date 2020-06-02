package cmanager.geo;

import cmanager.util.DateTimeUtil;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Waypoint implements Serializable {

    private static final long serialVersionUID = 3154357724453317729L;

    private final Coordinate coordinate;
    private final String code;
    private final String description;
    private final String symbol;
    private final String type;
    private String parent;
    private ZonedDateTime date;

    public Waypoint(
            Coordinate coordinate,
            String code,
            String description,
            String symbol,
            String type,
            String parent) {
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

    public void setDate(String date) {
        this.date = date == null ? null : DateTimeUtil.parseIsoDateTime(date);
    }

    public String getDateStrIso8601() {
        if (date == null) {
            return null;
        }

        return date.format(DateTimeFormatter.ISO_INSTANT);
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getType() {
        return type;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }
}
