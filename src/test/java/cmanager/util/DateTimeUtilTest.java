package cmanager.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class DateTimeUtilTest {

    @Test
    @DisplayName("Test month-based invalidation with up-to-date datetimes")
    public void testMonthInvalidationUpToDate() {
        assertFalse(DateTimeUtil.isTooOldWithMonths(LocalDateTime.now(), 3));
        assertFalse(
                DateTimeUtil.isTooOldWithMonths(
                        LocalDateTime.now().minusMonths(2).minusDays(25), 3));
        assertFalse(
                DateTimeUtil.isTooOldWithMonths(
                        LocalDateTime.now().minusMonths(3).plusMinutes(5), 3));
    }

    @Test
    @DisplayName("Test month-based invalidation with outdated datetimes")
    public void testMonthInvalidationOutdated() {
        assertTrue(
                DateTimeUtil.isTooOldWithMonths(
                        LocalDateTime.now().minusMonths(3).minusMinutes(1), 3));
        assertTrue(DateTimeUtil.isTooOldWithMonths(LocalDateTime.now().minusMonths(4), 3));
    }

    @Test
    @DisplayName("Test the ISO datetime parser")
    public void testParseIsoDateTime() {
        final ZonedDateTime dateTimeOpencachingDe =
                DateTimeUtil.parseIsoDateTime("2013-04-13T00:00:00Z");
        final ZonedDateTime dateTimeGeocachingCom =
                DateTimeUtil.parseIsoDateTime("2013-04-13T00" + ":00:00");
        assertEquals(dateTimeOpencachingDe, dateTimeGeocachingCom);
    }
}
