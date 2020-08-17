package cmanager.geo;

import cmanager.oc.OcSite;
import cmanager.settings.Settings;
import cmanager.util.DateTimeUtil;
import cmanager.util.ObjectHelper;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/** Container for geocache data. */
public class Geocache implements Serializable, Comparable<String> {

    private static final long serialVersionUID = 6173771530979347662L;

    /** The code of the geocache (typically GC code or OC code). */
    private final String code;

    /** The name of the geocache. */
    private String name;

    /** The position/coordinate of the geocache. */
    private Coordinate coordinate;

    /** The difficulty rating of the geocache. */
    private Double difficulty;

    /** The terrain rating of the geocache. */
    private Double terrain;

    /** The type of the geocache. */
    private GeocacheType type;

    /** The container size of the geocache. */
    private GeocacheContainerType container;

    /** The name of the geocache owner. */
    private String owner = null;

    /**
     * The GC code of the cache linked if this is an OC instance and there is a duplicate set by the
     * OC owner.
     */
    private String codeGc = null;

    /** The listing text of the geocache. */
    private String listing = null;

    /** The short listing text of the geocache. */
    private String listingShort = null;

    /** The hint for the geocache. */
    private String hint = null;

    /** The URL to open this geocache inside the web browser. */
    private String url = null;

    /**
     * Whether this geocache requires a password.
     *
     * <p>This is only supported by OC.
     */
    private Boolean requiresPassword = null;

    /**
     * The internal ID of the geocache.
     *
     * <p>This is only used for OC.
     */
    private String internalId = null;

    /**
     * The date when this geocache has been hidden.
     *
     * <p>This is mostly relevant for event caches.
     */
    private ZonedDateTime dateHidden;

    /** Whether this geocache ist archived. */
    private Boolean archived = null;

    /** Whether this geocache is available for search (not disabled). */
    private Boolean available = null;

    /**
     * Some geocache ID.
     *
     * <p>This seems to be required for some GPS devices, like the Garmin eTrex 10.
     */
    private Integer id = null;

    /** When this is a GC cache, whether it is a premium cache. */
    private Boolean gcPremium = null;

    /** The number of favorite points for this geocache. */
    private Integer favoritePoints = null;

    /** Whether this geocache has been found by the user. */
    private Boolean isFound = null;

    /** The attributes of the geocache. */
    private List<GeocacheAttribute> attributes = new ArrayList<>();

    /**
     * The logs for the geocache.
     *
     * <p>This list might be incomplete, depending on the data source and its verbosity.
     */
    private final List<GeocacheLog> logs = new ArrayList<>();

    /** The waypoints for the geocache. */
    private final List<Waypoint> waypoints = new ArrayList<>();

    /**
     * Create a new geocache instance with the given values.
     *
     * @param code The code of the geocache.
     * @param name The name of the geocache.
     * @param coordinate The position of the geocache.
     * @param difficulty The difficulty rating of the geocache.
     * @param terrain The terrain rating of the geocache.
     * @param type The geocache type string.
     */
    public Geocache(
            final String code,
            final String name,
            final Coordinate coordinate,
            final Double difficulty,
            final Double terrain,
            final String type)
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

    /**
     * Create a new geocache instance with the given values.
     *
     * <p>This is mainly used for creating the basic copy of the current instance.
     *
     * @param code The code of the geocache.
     * @param name The name of the geocache.
     * @param coordinate The position of the geocache.
     * @param difficulty The difficulty rating of the geocache.
     * @param terrain The terrain rating of the geocache.
     * @param type The geocache type.
     */
    public Geocache(
            final String code,
            final String name,
            final Coordinate coordinate,
            final Double difficulty,
            final Double terrain,
            final GeocacheType type)
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
        this.type = type;
    }

    /**
     * Create a string representation of the geocache, including the difficulty and terrain rating
     * as well as the code, type string and name.
     *
     * @return The string representation of the geocache.
     */
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

    /**
     * Check if the geocache has a volatile start.
     *
     * @return Whether the geocache has a volatile start. This is true iff the geocache is a mystery
     *     cache.
     */
    public boolean hasVolatileStart() {
        return this.type.equals(GeocacheType.getMysteryType());
    }

    /**
     * Update the current instance with the given data.
     *
     * <p>This will override existing values and copy the logs.
     *
     * @param geocache The geocache to update the current instance with.
     */
    public void update(final Geocache geocache) {
        update(geocache, true, true);
    }

    /**
     * Update the current instance with the given data.
     *
     * <p>No change will happen if the geocache codes to not match.
     *
     * @param geocache The geocache to update the current instance with.
     * @param override Whether to override the existing data.
     * @param copyLogs Whether to copy the log entries.
     */
    public void update(final Geocache geocache, final boolean override, final boolean copyLogs) {
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
            url = ObjectHelper.getBest(url, geocache.url);
            requiresPassword = ObjectHelper.getBest(requiresPassword, geocache.requiresPassword);
            internalId = ObjectHelper.getBest(internalId, geocache.internalId);
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

    /**
     * Get the URL for this geocache.
     *
     * <p>This will use the URL field if available. Otherwise the URL will be constructed using the
     * geocache code.
     *
     * @return The URL to open the geocache inside a web browser.
     */
    public String getUrl() {
        if (url != null && !url.isEmpty()) {
            return url;
        }

        if (isGc()) {
            return "https://www.geocaching.com/geocache/" + code;
        }

        // TODO: Add real support for the different sites here.
        if (isOc()) {
            return OcSite.getBaseUrl() + code;
        }

        return null;
    }

    /**
     * Set the URL for this geocache.
     *
     * @param url The URL to set.
     */
    public void setUrl(final String url) {
        this.url = url;
    }

    /**
     * Set the internal ID of the geocache.
     *
     * <p>This is only used for OC at the moment.
     *
     * @param internalId The ID to set.
     */
    public void setInternalId(final String internalId) {
        this.internalId = internalId;
    }

    /**
     * Get the internal ID of the geocache.
     *
     * @return The internal ID of the geocache. At the moment this will always be `null` for GC
     *     caches.
     */
    public String getInternalId() {
        return internalId;
    }

    /**
     * Set the date when this geocache has been hidden.
     *
     * @param dateHidden The date to set as a string. This should be in ISO-8601 format, with or
     *     without a timezone.
     */
    public void setDateHidden(final String dateHidden) {
        if (dateHidden == null) {
            this.dateHidden = null;
            return;
        }

        try {
            this.dateHidden = ZonedDateTime.parse(dateHidden);
        } catch (DateTimeParseException exception) {
            this.dateHidden = DateTimeUtil.parseIsoDateTime(dateHidden);
        }
    }

    /**
     * Set the date when this geocache has been hidden.
     *
     * @param dateHidden The date to set.
     */
    public void setDateHidden(final ZonedDateTime dateHidden) {
        this.dateHidden = dateHidden;
    }

    /**
     * Get the date when this geocache has been hidden.
     *
     * <p>This is mainly used for event caches.
     *
     * @return The date when this geocache has been hidden.
     */
    public ZonedDateTime getDateHidden() {
        return dateHidden;
    }

    /**
     * Get the date when this geocache has been hidden as an ISO-8601 string.
     *
     * @return The date when this geocache has been hidden, as an ISO-8601 string.
     */
    public String getDateHiddenStrIso8601() {
        return dateHidden.format(DateTimeFormatter.ISO_INSTANT);
    }

    /**
     * Get the geocache status as a string.
     *
     * @return The geocache status as a string.
     */
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

    /**
     * Check whether this is an OC cache.
     *
     * @return Whether this is an OC cache.
     */
    public boolean isOc() {
        return code.substring(0, 2).toUpperCase().equals("OC");
    }

    /**
     * Check whether this is a GC cache.
     *
     * @return Whether this is a GC cache.
     */
    public boolean isGc() {
        return code.substring(0, 2).toUpperCase().equals("GC");
    }

    /**
     * Add the given log to the geocache.
     *
     * @param geocacheLog The log to add.
     */
    public void addLog(final GeocacheLog geocacheLog) {
        logs.add(geocacheLog);
    }

    /**
     * Add the given list of logs to the geocache.
     *
     * @param logs The logs to add.
     */
    public void addLogs(final List<GeocacheLog> logs) {
        this.logs.addAll(logs);
    }

    /**
     * Get the logs for this geocache.
     *
     * @return The logs for this geocache.
     */
    public List<GeocacheLog> getLogs() {
        return logs;
    }

    /**
     * Get the most recent found log for the given user.
     *
     * @param usernameGc The name of the user on GC.
     * @param usernameOc The name of the user on OC.
     * @return The date of the most recent found log for the given user.
     */
    public ZonedDateTime getMostRecentFoundLog(final String usernameGC, final String usernameOc) {
        GeocacheLog mostRecentLog = null;

        for (final GeocacheLog log : logs) {
            if (log.isFoundLog()) {
                // TODO: Why is there no check for the platform here? In some edge cases this might
                // be a problem.
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

    /**
     * Get the waypoints for this geocache.
     *
     * @return The waypoints for this geocache.
     */
    public List<Waypoint> getWaypoints() {
        return waypoints;
    }

    /**
     * Add the given waypoint to the geocache.
     *
     * @param waypoint The waypoint to add.
     */
    public void addWaypoint(final Waypoint waypoint) {
        waypoint.setParent(code);
        waypoints.add(waypoint);
    }

    /**
     * Add the given waypoints to the geocache.
     *
     * @param waypoints The waypoints to add.
     */
    public void addWaypoints(final List<Waypoint> waypoints) {
        this.waypoints.addAll(waypoints);
    }

    /**
     * Get the attributes of this geocache.
     *
     * @return The attributes of this geocache.
     */
    public List<GeocacheAttribute> getAttributes() {
        return attributes;
    }

    /**
     * Add the given attributes to the geocache.
     *
     * @param attributes The attributes to add.
     */
    public void addAttributes(final List<GeocacheAttribute> attributes) {
        this.attributes.addAll(attributes);
    }

    /**
     * Add the given attribute to the geocache.
     *
     * @param attribute The attribute to add.
     */
    public void addAttribute(final GeocacheAttribute attribute) {
        attributes.add(attribute);
    }

    /**
     * Set the ID of the geocache.
     *
     * @param id The ID to set.
     */
    public void setId(final Integer id) {
        this.id = id;
    }

    /**
     * Get the ID of the geocache.
     *
     * @return The ID of this geocache.
     */
    public Integer getId() {
        return id;
    }

    /**
     * Get the number of favorite points for this geocache.
     *
     * @return The number of favorite points for this geocache.
     */
    public Integer getFavoritePoints() {
        return favoritePoints;
    }

    /**
     * Set the number of favorite points for this geocache.
     *
     * @param favoritePoints The number to set.
     */
    public void setFavoritePoints(final Integer favoritePoints) {
        this.favoritePoints = favoritePoints;
    }

    /**
     * Get the GC premium status of this geocache.
     *
     * @return Whether this is a GC premium cache.
     */
    public Boolean isGcPremium() {
        return gcPremium;
    }

    /**
     * Set the GC premium status of this geocache.
     *
     * @param gcPremium The status to set.
     */
    public void setGcPremium(final Boolean gcPremium) {
        this.gcPremium = gcPremium;
    }

    /**
     * Set the archived status of this geocache.
     *
     * @param archived The status to set.
     */
    public void setArchived(final Boolean archived) {
        this.archived = archived;
    }

    /**
     * Get the archived status of this geocache.
     *
     * @return Whether this geocache is archived.
     */
    public Boolean isArchived() {
        return archived;
    }

    /**
     * Set the availability status of this geocache.
     *
     * @param available The status to set.
     */
    public void setAvailable(final Boolean available) {
        this.available = available;
    }

    /**
     * Get the availability status of this geocache.
     *
     * @return Whether this geocache is available.
     */
    public Boolean isAvailable() {
        return available;
    }

    /**
     * Set the hint for this geocache.
     *
     * @param hint The hint to set.
     */
    public void setHint(final String hint) {
        this.hint = hint;
    }

    /**
     * Get the hint for this geocache.
     *
     * @return The hint for this geocache.
     */
    public String getHint() {
        return hint;
    }

    /**
     * Set the GC code for this geocache.
     *
     * @param gc The GC code to set.
     */
    public void setCodeGc(final String gc) {
        codeGc = gc;
    }

    /**
     * Get the GC code of this geocache.
     *
     * @return The GC code of this geocache.
     */
    public String getCodeGc() {
        return codeGc;
    }

    /**
     * Get the code of this geocache.
     *
     * @return The code of this geocache.
     */
    public String getCode() {
        return code;
    }

    /**
     * Get the name of this geocache.
     *
     * @return The name of this geocache.
     */
    public String getName() {
        return name;
    }

    /**
     * Get the position of this geocache.
     *
     * @return The position of this geocache.
     */
    public Coordinate getCoordinate() {
        return coordinate;
    }

    /**
     * Get the difficulty rating for this geocache.
     *
     * @return The difficulty rating for this geocache.
     */
    public Double getDifficulty() {
        return difficulty;
    }

    /**
     * Get the terrain rating for this geocache.
     *
     * @return The terrain rating for this geocache.
     */
    public Double getTerrain() {
        return terrain;
    }

    /**
     * Get the type of this geocache.
     *
     * @return The type of this geocache.
     */
    public GeocacheType getType() {
        return type;
    }

    /**
     * Get the name of the cache owner.
     *
     * @return The name of the cache owner.
     */
    public String getOwner() {
        return owner;
    }

    /**
     * Set the name of the cache owner.
     *
     * @param owner The name to set.
     */
    public void setOwner(final String owner) {
        this.owner = owner;
    }

    /**
     * Get the listing text of this geocache.
     *
     * @return The listing text of this geocache.
     */
    public String getListing() {
        return listing;
    }

    /**
     * Set the listing text for this geocache.
     *
     * @param listing The text to set.
     */
    public void setListing(final String listing) {
        this.listing = listing;
    }

    /**
     * Get the container size of this geocache.
     *
     * @return The container size of this geocache.
     */
    public GeocacheContainerType getContainer() {
        return this.container;
    }

    /**
     * Set the container size for this geocache.
     *
     * @param container The container size to set.
     */
    public void setContainer(final String container) {
        this.container = new GeocacheContainerType(container);
    }

    /**
     * Get the short listing text of this geocache.
     *
     * @return The short listing text of this geocache.
     */
    public String getListingShort() {
        return listingShort;
    }

    /**
     * Set the short listing text for this geocache.
     *
     * @param listingShort The text to set.
     */
    public void setListingShort(final String listingShort) {
        this.listingShort = listingShort;
    }

    /**
     * Get the found status of this geocache.
     *
     * @return The found status of this geocache.
     */
    public Boolean getIsFound() {
        return isFound;
    }

    /**
     * Set the found status for this geocache.
     *
     * @param isFound The status to set.
     */
    public void setIsFound(final Boolean isFound) {
        this.isFound = isFound;
    }

    /**
     * Compare the current instance to the given one.
     *
     * <p>This will compare the given string to the geocache code of the current instance.
     *
     * @param string The geocache code to compare with.
     * @return The comparison result.
     */
    @Override
    public int compareTo(String string) {
        return code.compareTo(string);
    }

    /**
     * Set the password requirement status for this geocache.
     *
     * @param requiresPassword The status to set.
     */
    public void setRequiresPassword(final Boolean requiresPassword) {
        this.requiresPassword = requiresPassword;
    }

    /**
     * Get the password requirement status for this geocache.
     *
     * <p>This is useful for OC caches only, as GC does not support log passwords.
     *
     * @return Whether logging this geocache requires a password or not.
     */
    public Boolean doesRequirePassword() {
        return requiresPassword;
    }

    /**
     * Check whether this geocache has a found log by the configured GC user.
     *
     * @return Whether this cache has a found log by the configured GC user.
     */
    public boolean hasFoundLogByGcUser() {
        // Abort if this is not a GC cache.
        if (!isGc()) {
            return false;
        }

        // Get the configured username.
        // Abort if the value appears to be unset.
        final String usernameGc = Settings.getString(Settings.Key.GC_USERNAME);
        if (usernameGc == null || usernameGc.isEmpty()) {
            return false;
        }

        for (final GeocacheLog log : getLogs()) {
            // We are only interested in found logs.
            if (!log.isFoundLog()) {
                continue;
            }
            // If we have found a log, we can stop.
            if (log.isAuthor(usernameGc)) {
                return true;
            }
        }

        // We did not find a log.
        return false;
    }

    /**
     * Return a minimal (basic) copy of this geocache instance.
     *
     * <p>This is required to make the duplicate search more reliable. Otherwise we would compare
     * the container size and owner name as well in a second run. This is due to the caching done
     * using the OKAPI runtime cache. See issue #34 for more details about this.
     *
     * @return A minimal copy of the current geocache. This only has the code, name, coordinate,
     *     difficulty and terrain rating, the type, GC code, availability status attributes and the
     *     hidden date.
     */
    public Geocache getBasicCopy() {
        final Geocache geocache =
                new Geocache(
                        getCode(),
                        getName(),
                        getCoordinate(),
                        getDifficulty(),
                        getTerrain(),
                        getType());
        geocache.setCodeGc(getCodeGc());
        geocache.setAvailable(isAvailable());
        geocache.setArchived(isArchived());
        geocache.setDateHidden(getDateHidden());

        return geocache;
    }
}
