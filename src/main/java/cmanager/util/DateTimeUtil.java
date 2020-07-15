package cmanager.util;

import java.io.File;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/** Collection of utility methods for date and time handling. */
public class DateTimeUtil {

    /**
     * Check if the given datetime is older than the given number of months.
     *
     * @param oldTime The datetime to check the invalidation for.
     * @param maximumAgeInMonths The maximum age in months allowed.
     * @return Whether the given datetime is too old.
     */
    public static boolean isTooOldWithMonths(final LocalDateTime oldTime, int maximumAgeInMonths) {
        final LocalDateTime invalidationTime = oldTime.plusMonths(maximumAgeInMonths);
        final LocalDateTime now = LocalDateTime.now();

        return invalidationTime.isBefore(now);
    }

    /**
     * Check if the given file is older than the given number of months.
     *
     * @param file The file to check the modification time for.
     * @param maximumAgeInMonths The maximum age in months allowed.
     * @return Whether the file is too old.
     */
    public static boolean isTooOldWithMonths(final File file, int maximumAgeInMonths) {
        final LocalDateTime fileModifiedDate =
                LocalDateTime.ofInstant(
                        Instant.ofEpochMilli(file.lastModified()), ZoneId.systemDefault());
        return DateTimeUtil.isTooOldWithMonths(fileModifiedDate, maximumAgeInMonths);
    }

    /**
     * Check if the given file is too old.
     *
     * @param file The file to check the modification time for.
     * @param expirationDatetime The datetime when the file should expire.
     * @return Whether the file is too old.
     */
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

    /**
     * Check if the given datetimes only differ by the given day offset. The offset is applied on
     * both negative and positive direction to make the comparison independent of the parameter time
     * order.
     *
     * @param dateTime1 The first datetime.
     * @param dateTime2 The second datetime.
     * @param dayOffset The maximum offset in days allowed.
     * @return If the two datetimes only differ in the given number of days.
     */
    public static boolean isInDayRange(
            final ZonedDateTime dateTime1, final ZonedDateTime dateTime2, final int dayOffset) {
        if (dateTime1 == null || dateTime2 == null) {
            return false;
        }

        final ZonedDateTime dateTime1Minus = dateTime1.minusDays(dayOffset);
        final ZonedDateTime dateTime1Plus = dateTime1.plusDays(dayOffset);

        return dateTime1Minus.isBefore(dateTime2) && dateTime2.isBefore(dateTime1Plus);
    }
}
