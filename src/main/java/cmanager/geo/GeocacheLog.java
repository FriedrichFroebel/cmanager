package cmanager.geo;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/** Container for a geocache log. */
public class GeocacheLog implements Serializable {

    private static final long serialVersionUID = -2611937420437874774L;

    /** The known log types. */
    public static final TypeMap TYPE = new TypeMap();

    // Initialize the map.
    static {
        TYPE.add("Found it");
        TYPE.add("Didn't find it");
        TYPE.add("Write note", "Note");
        TYPE.add("Needs Maintenance");
        TYPE.add("Needs Archived");

        TYPE.add("Will Attend");
        TYPE.add("Attended");
        TYPE.add("Announcement");

        TYPE.add("Webcam Photo Taken");

        TYPE.add("Temporarily Disable Listing");
        TYPE.add("Enable Listing");
        TYPE.add("Owner Maintenance");
        TYPE.add("Update Coordinates");

        TYPE.add("Post Reviewer Note");
        TYPE.add("Publish Listing");
        TYPE.add("Retract Listing");
        TYPE.add("Archive");
        TYPE.add("Unarchive");
    }

    /** The log type. */
    private int type;

    /** The author of the log. */
    private String author;

    /** The log text. */
    private String text;

    /** The date of the log. */
    private ZonedDateTime date;

    /** The log password. */
    private String password;

    /**
     * Create a new instance with the given values.
     *
     * @param type The log type.
     * @param author The log author.
     * @param text The log text.
     * @param date The log date.
     */
    public GeocacheLog(
            final String type, final String author, final String text, final String date) {
        setType(type);
        setDate(date);

        if (author == null || text == null) {
            throw new NullPointerException();
        }

        this.author = author;
        this.text = text;
        this.password = "";
    }

    /**
     * Create a new instance with the given values.
     *
     * @param type The log type.
     * @param author The log author.
     * @param text The log text.
     * @param date The log date.
     * @param password The log password.
     */
    public GeocacheLog(
            final String type,
            final String author,
            final String text,
            final String date,
            final String password) {
        setType(type);
        setDate(date);

        if (author == null || text == null) {
            throw new NullPointerException();
        }

        this.author = author;
        this.text = text;
        this.password = password;
    }

    /**
     * Set the log type.
     *
     * @param type The string/name for the log type.
     */
    public void setType(String type) {
        type = type.toLowerCase();
        this.type = TYPE.getLowercase(type);
    }

    /**
     * Get the log type as a string.
     *
     * @return The log type as a string.
     */
    public String getTypeStr() {
        return TYPE.get(type, 0);
    }

    /**
     * Set the date of the log.
     *
     * @param date The date to set. This should follow the ISO-8601 format.
     */
    public void setDate(final String date) {
        // <groundspeak:date>2015-08-16T19:00:00Z</groundspeak:date>
        this.date = ZonedDateTime.parse(date);
    }

    /**
     * Set the log text.
     *
     * <p>Please note that this may contain HTML code for a complete HTML document when retrieved
     * from the editor pane, so we have to extract the body here.
     *
     * <p>This method may not produce the exact same result as given in the XML file input. We might
     * want to replace this solution with something like
     * https://stackoverflow.com/questions/1859686/getting-raw-text-from-jtextpane.
     *
     * @param text The text to set.
     */
    public void setText(String text) {
        // Do not set empty log texts.
        if (text == null || text.isEmpty()) {
            return;
        }

        // Use the given text if we have no HTML body tags.
        if (!text.contains("<body>") || !text.contains("</body>")) {
            this.text = text;
            return;
        }

        // Split on the body start tag and ensure that there actually is a body again.
        String[] parts = text.split("<body>");
        if (parts.length != 2) {
            this.text = text;
            return;
        }

        // Retrieve the body itself.
        final String body = parts[1].split("</body>")[0];

        // Trim all lines.
        final String[] lines = body.split("\n");
        final StringBuilder linesBuilder = new StringBuilder();
        for (final String line : lines) {
            linesBuilder.append(line.trim());
            linesBuilder.append("\n");
        }
        final String bodyTrim = linesBuilder.toString().trim();

        // If the text is empty, set the original text again.
        if (bodyTrim.isEmpty()) {
            this.text = text;
            return;
        }

        // Set the body as the new log text.
        this.text = bodyTrim;
    }

    /**
     * Set the log password.
     *
     * @param password The password to set.
     */
    public void setPassword(final String password) {
        this.password = password;
    }

    /**
     * Get the log password.
     *
     * <p>This is only relevant for OC caches as GC does not support log passwords.
     *
     * @return The log password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Get the author of the log.
     *
     * @return The author of the log.
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Check whether the given user is the author of the log entry.
     *
     * @param name The username to check for.
     * @return Whether the given user is the author of the log entry.
     */
    public boolean isAuthor(final String name) {
        return author.toLowerCase().equals(name.toLowerCase());
    }

    /**
     * Check whether this is a found log.
     *
     * @return Whether this is a found log.
     */
    public boolean isFoundLog() {
        final String typeStr = getTypeStr();
        return typeStr.equals("Found it")
                || typeStr.equals("Attended")
                || typeStr.equals("Webcam Photo Taken");
    }

    /**
     * Get the log text.
     *
     * @return The log text.
     */
    public String getText() {
        return text;
    }

    /**
     * Get the log date.
     *
     * @return The log date.
     */
    public ZonedDateTime getDate() {
        return date;
    }

    /**
     * Get the log date in the format `dd.MM.yyyy HH:mm`.
     *
     * @return The log date as a string of the mentioned format.
     */
    public String getDateStr() {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        return date.format(formatter);
    }

    /**
     * Get the log date as an ISO-8601 string.
     *
     * @return The log date as a string formatted using ISO-8601.
     */
    public String getDateStrIso8601() {
        return date.format(DateTimeFormatter.ISO_INSTANT);
    }

    /**
     * Format the given date as ISO-8601 while ignoring the information.
     *
     * @param date The date to format.
     * @return The formatted date.
     */
    public static String getDateStrIso8601NoTime(final ZonedDateTime date) {
        return date.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    /**
     * Get the log date as an ISO-8601 string without the time.
     *
     * @return The log date as a string formatted using ISO-8601 without time information.
     */
    public String getDateStrIso8601NoTime() {
        return getDateStrIso8601NoTime(date);
    }

    /**
     * Get the log type for the OKAPI.
     *
     * <p>This is required as the OKAPI does not seem to accept "Webcam Photo Taken" logs for webcam
     * caches, but requires a "Found it" log.
     *
     * @link https://www.opencaching.de/okapi/services/logs/submit.html
     * @param geocache The geocache instance this log belongs to. This may be needed to distinguish
     *     the different cache types in the future, but is not used for now.
     * @return The OKAPI log type.
     */
    public String getOkapiType(final Geocache geocache) {
        final String logType = getTypeStr();

        // Webcam caches require a "Found it".
        if (logType.equals("Webcam Photo Taken")) {
            return "Found it";
        }

        // This is no special case.
        return logType;
    }

    /**
     * Check whether the given log and the current instance are the same.
     *
     * @param log The log to check against.
     * @return Whether the logs are equal.
     */
    public boolean equals(GeocacheLog log) {
        return type == log.type
                && date.equals(log.date)
                && author.equals(log.author)
                && text.equals(log.text);
    }
}
