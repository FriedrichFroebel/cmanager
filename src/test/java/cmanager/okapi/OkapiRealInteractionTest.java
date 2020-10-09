package cmanager.okapi;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import cmanager.exception.UnexpectedLogStatus;
import cmanager.geo.Coordinate;
import cmanager.geo.Geocache;
import cmanager.geo.GeocacheLog;
import cmanager.oc.OcSite;
import cmanager.oc.SupportedSite;
import cmanager.okapi.helper.SiteHelper;
import cmanager.okapi.helper.TestClient;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests for the OKAPI-based methods which perform write operations or depend on existing logs by
 * the test user.
 */
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

    /** Test updating the found status of a logged cache. */
    @Test
    @DisplayName("Test updating the found status of logged cache")
    public void testUpdateFoundStatusLoggedCache() throws Exception {
        final Geocache geocache =
                new Geocache("OC12A70", "test", new Coordinate(0, 0), 0.0, 0.0, "Tradi");
        assertNull(geocache.getIsFound());

        Okapi.updateFoundStatus(testClient, geocache);
        assertTrue(geocache.getIsFound());
    }

    /** Test updating the found status of an unlogged cache. */
    @Test
    @DisplayName("Test updating the found status of an unlogged cache")
    public void testUpdateFoundStatusUnloggedCache() throws Exception {
        final Geocache geocache =
                new Geocache("OC12A6F", "test", new Coordinate(0, 0), 0.0, 0.0, "Tradi");
        assertNull(geocache.getIsFound());

        Okapi.updateFoundStatus(testClient, geocache);
        assertFalse(geocache.getIsFound());
    }

    /**
     * Test getting the caches around a given position. This does not use a user filter, but checks
     * that one cache is present.
     */
    @Test
    @DisplayName("Test getting the caches around without a user filter")
    public void testGetCachesAroundWithoutUserFilter() throws Exception {
        final List<Geocache> caches =
                Okapi.getCachesAround(null, null, 00.23333, 000.61667, 1.0, new ArrayList<>());
        assertNotNull(caches);
        assertTrue(caches.size() >= 1);

        boolean containsCache = false;
        for (final Geocache geocache : caches) {
            if (geocache.toString()
                    .equals("1.0/3.5 OC12A70 (Tradi) -- 0.233333, 0.616667 -- Logged cache")) {
                containsCache = true;
                break;
            }
        }

        assertTrue(containsCache);
    }

    /**
     * Test getting the caches around a given position. This uses a user filter and checks that one
     * cache is not present as it already has been logged.
     */
    @Test
    @DisplayName("Test getting the caches around with a user filter")
    public void testGetCachesAroundWithUserFilter() throws Exception {
        final List<Geocache> caches =
                Okapi.getCachesAround(
                        testClient,
                        Okapi.getUuid(testClient),
                        00.23333,
                        000.61667,
                        1.0,
                        new ArrayList<>());
        assertNotNull(caches);

        boolean containsCache = false;
        for (final Geocache geocache : caches) {
            if (geocache.toString()
                    .equals("1.0/3.5 OC12A70 (Tradi) -- 0.233333, 0.616667 -- Logged cache")) {
                containsCache = true;
                break;
            }
        }

        assertFalse(containsCache);
    }

    /** Test logging an unlogged cache. */
    @Test
    @DisplayName("Test logging a cache")
    public void testLoggingUnloggedCache() throws Exception {
        final Geocache geocache =
                new Geocache("OC12A6F", "test", new Coordinate(0, 0), 0.0, 0.0, "Tradi");
        final GeocacheLog log =
                new GeocacheLog(
                        "Found it",
                        SiteHelper.getUsername(),
                        "testLoggingUnloggedCache",
                        "2020-06-21T19:00:00Z");

        // Make sure that the cache has not been found. This might indicate a leftover from a
        // previous test run.
        Okapi.updateFoundStatus(testClient, geocache);
        assertFalse(geocache.getIsFound());

        // Post the log.
        final String logUuid = Okapi.postLog(testClient, geocache, log, false);
        Okapi.updateFoundStatus(testClient, geocache);
        assertTrue(geocache.getIsFound());

        // Clean up for the next test run by deleting the posted log.
        assertNotNull(logUuid);
        Okapi.deleteLog(testClient, logUuid);
        Okapi.updateFoundStatus(testClient, geocache);
        assertFalse(geocache.getIsFound());
    }

    /** Test logging an unlogged password-protected cache. */
    @Test
    @DisplayName("Test logging a password-protected cache")
    public void testLoggingPasswordProtectedCache() throws Exception {
        final Geocache geocache =
                new Geocache("OC12A71", "test", new Coordinate(0, 0), 0.0, 0.0, "Tradi");
        final GeocacheLog log =
                new GeocacheLog(
                        "Found it",
                        SiteHelper.getUsername(),
                        "testLoggingPasswordProtectedCache",
                        "2020-06-21T19:00:00Z");

        // Indicate that this cache requires a password.
        geocache.setRequiresPassword(true);

        // Make sure that the cache has not been found. This might indicate a leftover from a
        // previous test run.
        Okapi.updateFoundStatus(testClient, geocache);
        assertFalse(geocache.getIsFound());

        // Try to post the log without a password/with an empty password. This should fail.
        String logUuid;
        try {
            logUuid = Okapi.postLog(testClient, geocache, log, false);
            Okapi.deleteLog(testClient, logUuid);
            fail("Posting log for password-protected cache with wrong password did not fail.");
        } catch (UnexpectedLogStatus ignored) {
        }

        // Make sure again that the cache has not been found.
        Okapi.updateFoundStatus(testClient, geocache);
        assertFalse(geocache.getIsFound());

        // Set the correct log password.
        log.setPassword("cmanagerTest");

        // Post the log, using the correct log password this time.
        logUuid = Okapi.postLog(testClient, geocache, log, false);
        Okapi.updateFoundStatus(testClient, geocache);
        assertTrue(geocache.getIsFound());

        // Clean up for the next test run by deleting the posted log.
        assertNotNull(logUuid);
        Okapi.deleteLog(testClient, logUuid);
        Okapi.updateFoundStatus(testClient, geocache);
        assertFalse(geocache.getIsFound());
    }
}
