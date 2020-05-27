package cmanager.geo;

import java.io.Serializable;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public class Waypoint implements Serializable {

    private static final long serialVersionUID = 3154357724453317729L;

    private final Coordinate coordinate;
    private final String code;
    private final String description;
    private final String symbol;
    private final String type;
    private String parent;
    private DateTime date;

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
        this.date = date == null ? null : new DateTime(date, DateTimeZone.UTC);
    }

    public String getDateStrIso8601() {
        if (date == null) {
            return null;
        }

        final DateTimeFormatter formatter = ISODateTimeFormat.dateTime();
        return formatter.print(date);
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
