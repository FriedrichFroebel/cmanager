package cmanager.geo;

import cmanager.global.Constants;
import cmanager.util.ObjectHelper;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.joda.time.DateTime;

public class Geocache implements Serializable, Comparable<String> {

    private static final long serialVersionUID = 6173771530979347662L;

    private final String code;
    private String name;
    private Coordinate coordinate;
    private Double difficulty;
    private Double terrain;
    private GeocacheType type;
    private GeocacheContainerType container;
    private String owner = null;
    private String codeGc = null; // Linked cache on GC.
    private String listing = null;
    private String listingShort = null;
    private String hint = null;
    private Boolean requiresPassword = null;

    private Boolean archived = null;
    private Boolean available = null;
    private Integer id = null; // Required by "Garmin etrex 10".

    private Boolean gcPremium = null;
    private Integer favoritePoints = null;

    private Boolean isFound = null;

    private List<GeocacheAttribute> attributes = new ArrayList<>();
    private final List<GeocacheLog> logs = new ArrayList<>();
    private final List<Waypoint> waypoints = new ArrayList<>();

    public Geocache(
            String code,
            String name,
            Coordinate coordinate,
            Double difficulty,
            Double terrain,
            String type)
            throws NullPointerException {
        if (code == null
                || name == null
                || coordinate == null
                || difficulty == null
                || terrain == null
                || type == null) {
            throw new NullPointerException();
        }

        this.code = code;
        this.name = name;
        this.coordinate = coordinate;
        this.difficulty = difficulty;
        this.terrain = terrain;
        this.type = new GeocacheType(type);
    }

    public String toString() {
        return difficulty.toString()
                + "/"
                + terrain.toString()
                + " "
                + code
                + " ("
                + type.asNiceType()
                + ") -- "
                + coordinate.toString()
                + " -- "
                + name;
    }

    public boolean hasVolatileStart() {
        return this.type.equals(GeocacheType.getMysteryType());
    }

    public void update(Geocache geocache) {
        update(geocache, true, true);
    }

    public void update(Geocache geocache, boolean override, boolean copyLogs) {
        if (!code.equals(geocache.code)) {
            return;
        }

        if (override) {
            name = ObjectHelper.getBest(name, geocache.name);
            coordinate = ObjectHelper.getBest(coordinate, geocache.coordinate);
            difficulty = ObjectHelper.getBest(this.getDifficulty(), geocache.getDifficulty());
            terrain = ObjectHelper.getBest(terrain, geocache.terrain);
            type = geocache.type;
            container = ObjectHelper.getBest(container, geocache.container);
            owner = ObjectHelper.getBest(owner, geocache.owner);
            codeGc = ObjectHelper.getBest(codeGc, geocache.codeGc);
            setListing(ObjectHelper.getBest(getListing(), geocache.getListing()));
            listingShort = ObjectHelper.getBest(listingShort, geocache.listingShort);
            hint = ObjectHelper.getBest(hint, geocache.hint);
            archived = ObjectHelper.getBest(archived, geocache.archived);
            available = ObjectHelper.getBest(available, geocache.available);

            attributes = ObjectHelper.getBest(attributes, geocache.attributes);
        }
        if (copyLogs) {
            for (final GeocacheLog newLog : geocache.logs) {
                boolean match = false;
                for (final GeocacheLog oldLog : logs) {
                    if (newLog.equals(oldLog)) {
                        match = true;
                        break;
                    }
                }
                if (!match) {
                    logs.add(newLog);
                }
            }
        }
    }

    public String getUrl() {
        if (isGc()) {
            return "https://www.geocaching.com/geocache/" + code;
        }
        if (isOc()) {
            return Constants.SITE_BASE + code;
        }

        return null;
    }

    public String getStatusAsString() {
        if (archived == null || available == null) {
            return null;
        }

        if (archived) {
            return "archived";
        }
        if (available) {
            return "available";
        }
        return "disabled";
    }

    public boolean isOc() {
        return code.substring(0, 2).toUpperCase().equals("OC");
    }

    public boolean isGc() {
        return code.substring(0, 2).toUpperCase().equals("GC");
    }

    public void addLog(GeocacheLog geocacheLog) {
        logs.add(geocacheLog);
    }

    public void addLogs(List<GeocacheLog> logs) {
        this.logs.addAll(logs);
    }

    public List<GeocacheLog> getLogs() {
        return logs;
    }

    public DateTime getMostRecentFoundLog(String usernameGC, String usernameOc) {
        GeocacheLog mostRecentLog = null;

        for (final GeocacheLog log : logs) {
            if (log.isFoundLog()) {
                if ((usernameGC != null && log.isAuthor(usernameGC))
                        || (usernameOc != null && log.isAuthor(usernameOc))) {
                    if (mostRecentLog == null) {
                        mostRecentLog = log;
                    } else if (log.getDate().isAfter(mostRecentLog.getDate())) {
                        mostRecentLog = log;
                    }
                }
            }
        }

        return mostRecentLog == null ? null : mostRecentLog.getDate();
    }

    public List<Waypoint> getWaypoints() {
        return waypoints;
    }

    public void addWaypoint(Waypoint waypoint) {
        waypoint.setParent(code);
        waypoints.add(waypoint);
    }

    public void addWaypoints(List<Waypoint> waypoints) {
        this.waypoints.addAll(waypoints);
    }

    public List<GeocacheAttribute> getAttributes() {
        return attributes;
    }

    public void addAttributes(List<GeocacheAttribute> attributes) {
        this.attributes.addAll(attributes);
    }

    public void addAttribute(GeocacheAttribute attribute) {
        attributes.add(attribute);
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public Integer getFavoritePoints() {
        return favoritePoints;
    }

    public void setFavoritePoints(Integer favoritePoints) {
        this.favoritePoints = favoritePoints;
    }

    public Boolean isGcPremium() {
        return gcPremium;
    }

    public void setGcPremium(Boolean gcPremium) {
        this.gcPremium = gcPremium;
    }

    public void setArchived(Boolean archived) {
        this.archived = archived;
    }

    public Boolean isArchived() {
        return archived;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public Boolean isAvailable() {
        return available;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public String getHint() {
        return hint;
    }

    public void setCodeGC(String gc) {
        codeGc = gc;
    }

    public String getCodeGC() {
        return codeGc;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public Double getDifficulty() {
        return difficulty;
    }

    public Double getTerrain() {
        return terrain;
    }

    public GeocacheType getType() {
        return type;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getListing() {
        return listing;
    }

    public void setListing(String listing) {
        this.listing = listing;
    }

    public GeocacheContainerType getContainer() {
        return this.container;
    }

    public void setContainer(String container) {
        this.container = new GeocacheContainerType(container);
    }

    public String getListingShort() {
        return listingShort;
    }

    public void setListingShort(String listingShort) {
        this.listingShort = listingShort;
    }

    public Boolean getIsFound() {
        return isFound;
    }

    public void setIsFound(Boolean isFound) {
        this.isFound = isFound;
    }

    @Override
    public int compareTo(String string) {
        return code.compareTo(string);
    }

    public void setRequiresPassword(Boolean requiresPassword) {
        this.requiresPassword = requiresPassword;
    }

    public Boolean doesRequirePassword() {
        return requiresPassword;
    }
}
