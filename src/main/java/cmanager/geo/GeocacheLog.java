package cmanager.geo;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class GeocacheLog implements Serializable {

    private static final long serialVersionUID = -2611937420437874774L;

    public static final TypeMap TYPE = new TypeMap();

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

    public void setType(String type) {
        type = type.toLowerCase();
        this.type = TYPE.getLowercase(type);
    }

    public String getTypeStr() {
        return TYPE.get(type, 0);
    }

    private int type;
    private String author;
    private String text;
    private ZonedDateTime date;
    private String password;

    public GeocacheLog(String type, String author, String text, String date) {
        setType(type);
        setDate(date);

        if (author == null || text == null) {
            throw new NullPointerException();
        }

        this.author = author;
        this.text = text;
        this.password = "";
    }

    public GeocacheLog(String type, String author, String text, String date, String password) {
        setType(type);
        setDate(date);

        if (author == null || text == null) {
            throw new NullPointerException();
        }

        this.author = author;
        this.text = text;
        this.password = password;
    }

    public void setDate(String date) {
        // <groundspeak:date>2015-08-16T19:00:00Z</groundspeak:date>
        // ISO 8601
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

    public String getAuthor() {
        return author;
    }

    public boolean isAuthor(String name) {
        return author.toLowerCase().equals(name.toLowerCase());
    }

    public boolean isFoundLog() {
        final String typeStr = getTypeStr();
        return typeStr.equals("Found it")
                || typeStr.equals("Attended")
                || typeStr.equals("Webcam Photo Taken");
    }

    public String getText() {
        return text;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public String getDateStr() {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        return date.format(formatter);
    }

    public String getDateStrIso8601() {
        return date.format(DateTimeFormatter.ISO_INSTANT);
    }

    public static String getDateStrIso8601NoTime(ZonedDateTime date) {
        return date.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    public String getDateStrIso8601NoTime() {
        return getDateStrIso8601NoTime(date);
    }

    public String getPassword() {
        return password;
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
    public String getOkapiType(Geocache geocache) {
        final String logType = getTypeStr();

        // Webcam caches require a "Found it".
        if (logType.equals("Webcam Photo Taken")) {
            return "Found it";
        }

        // This is no special case.
        return logType;
    }

    public boolean equals(GeocacheLog log) {
        return type == log.type
                && date.equals(log.date)
                && author.equals(log.author)
                && text.equals(log.text);
    }
}
