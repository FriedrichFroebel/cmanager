package cmanager.oc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import cmanager.geo.Coordinate;
import cmanager.geo.Geocache;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Test Opencaching utilities. */
public class OcUtilTest {

    /** Test retrieving the log URL for a given opencache log. */
    @Test
    @DisplayName("Test coordinate constructor")
    public void testLogUrlRetrieval() {
        final Geocache opencache =
                new Geocache("", "", new Coordinate(0.0, 0.0), 1.0, 1.0, "Traditional");
        opencache.setInternalId("103011");

        final String logUrl = OcUtil.determineLogUrl(opencache, "278256");

        assertEquals(OcSite.getBaseUrl() + "viewcache.php?cacheid=103011&log=A#log278256", logUrl);
    }
}
