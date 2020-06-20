package cmanager.okapi;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import cmanager.geo.Coordinate;
import cmanager.geo.Geocache;
import cmanager.oc.OcSite;
import cmanager.oc.SupportedSite;
import cmanager.okapi.helper.SiteHelper;
import cmanager.okapi.helper.TestClient;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests for the OKAPI-based methods which perform write operations or depend on existing logs by
 * the test user.
 */
@Disabled
public class OkapiRealInteractionTest {

    /** The test client instance to use. */
    private static TestClient testClient = null;

    /**
     * Set up the test client. By not doing this for each test, this should reduce the overall load
     * on the Opencaching.de site.
     */
    @BeforeAll
    public static void setUp() throws Exception {
        // Set the correct site.
        OcSite.setSite(SupportedSite.OPENCACHING_DE_TESTING);
        SiteHelper.setSite(SupportedSite.OPENCACHING_DE_TESTING);

        testClient = new TestClient();
        final boolean loggedIn = testClient.login();
        assertTrue(loggedIn);

        System.out.println("OKAPI token is uninitialized. Fetching ...");
        assertNotNull(testClient.requestToken());
    }

    /** Test logging the specified test cache. */
    @Test
    @DisplayName("Test updating the found status with success")
    public void testUpdateFoundStatusSuccess() throws Exception {
        final Geocache geocache =
                new Geocache("OC13A45", "test", new Coordinate(0, 0), 0.0, 0.0, "Tradi");
        assertNull(geocache.getIsFound());

        Okapi.updateFoundStatus(testClient, geocache);
        assertTrue(geocache.getIsFound());
    }

    /** Test logging the specified test cache. */
    @Test
    @DisplayName("Test updating the found status without success")
    public void testUpdateFoundStatusFailure() throws Exception {
        final Geocache geocache =
                new Geocache("OC0BEF", "test", new Coordinate(0, 0), 0.0, 0.0, "Tradi");
        assertNull(geocache.getIsFound());

        // Logging should not be successful. TODO: Why?
        Okapi.updateFoundStatus(testClient, geocache);
        assertFalse(geocache.getIsFound());
    }

    /**
     * Test getting the caches around a given position. This uses a user filter and checks that one
     * cache is not present as it already has been logged.
     */
    @Test
    @DisplayName("Test getting the caches around with a user filter")
    public void testGetCachesAroundWithUserFilter() throws Exception {
        // TODO: Adapt the values to represent a real test server cache.
        final List<Geocache> caches =
                Okapi.getCachesAround(
                        testClient,
                        Okapi.getUuid(testClient),
                        00.21667,
                        000.61667,
                        1.0,
                        new ArrayList<>());
        assertNotNull(caches);

        boolean containsCache = false;
        for (final Geocache geocache : caches) {
            if (geocache.toString()
                    .equals(
                            "1.0/5.0 OC13A45 (Tradi) -- 0.216667, 0.616667 -- cmanager TEST cache")) {
                containsCache = true;
                break;
            }
        }

        assertFalse(containsCache);
    }
}
