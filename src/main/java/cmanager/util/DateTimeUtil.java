package cmanager.util;

import java.io.File;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {

    public static boolean isTooOldWithMonths(final LocalDateTime oldTime, int maximumAgeInMonths) {
        final LocalDateTime invalidationTime = oldTime.plusMonths(maximumAgeInMonths);
        final LocalDateTime now = LocalDateTime.now();

        return invalidationTime.isBefore(now);
    }

    public static boolean isTooOldWithMonths(final File file, int maximumAgeInMonths) {
        final LocalDateTime fileModifiedDate =
                LocalDateTime.ofInstant(
                        Instant.ofEpochMilli(file.lastModified()), ZoneId.systemDefault());
        return DateTimeUtil.isTooOldWithMonths(fileModifiedDate, maximumAgeInMonths);
    }

    public static boolean isTooOld(final File file, final LocalDateTime expirationDatetime) {
        final LocalDateTime fileModifiedDate =
                LocalDateTime.ofInstant(
                        Instant.ofEpochMilli(file.lastModified()), ZoneId.systemDefault());
        return expirationDatetime.isAfter(fileModifiedDate);
    }

    /**
     * Parse the given string as an ISO local date time. As this does might not carry any timezone
     * information, we have to explicitly handle this and therefore moved it to an own function.
     *
     * @param string The string to parse.
     * @return The parsed string.
     */
    public static ZonedDateTime parseIsoDateTime(String string) {
        // Prevent errors when directly using a zoned datetime. These do not even show up for
        // the user which makes it hard to debug (this is due to some skipping on errors in the
        // parser).
        // java.time.format.DateTimeParseException: Text '2005-08-19T00:00:00' could not be parsed:
        // Unable to obtain ZonedDateTime from TemporalAccessor: {},ISO resolved to 2005-08-19T00:00
        // of type java.time.format.Parsed

        // Opencaching.de uses `ISO_INSTANT`, Geocaching.com uses `ISO_LOCAL_DATE_TIME`.
        try {
            // Use the easiest approach by just removing the trailing `Z`.
            // Otherwise we would have problems parsing it to a LocalDate.
            // Unable to obtain LocalDate from TemporalAccessor: {MilliOfSecond=0, NanoOfSecond=0,
            // InstantSeconds=1365811200, MicroOfSecon
            // d=0},ISO of type java.time.format.Parsed
            if (string.endsWith("Z")) {
                string = string.substring(0, string.length() - 1);
            }
            final LocalDate localDate =
                    LocalDate.parse(string, DateTimeFormatter.ISO_LOCAL_DATE_TIME);

            return localDate.atStartOfDay(ZoneId.systemDefault());
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return null;
    }
}
