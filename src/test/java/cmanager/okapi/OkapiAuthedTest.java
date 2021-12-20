package cmanager.okapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Tests for the OKAPI-based methods which require a valid user login. */
public class OkapiAuthedTest {

    /** The test client instance to use. */
    private static TestClient testClient = null;

    /**
     * Set up the test client. By not doing this for each test, this should reduce the overall load
     * on the Opencaching.de site.
     */
    @BeforeAll
    public static void setUp() throws Exception {
        // Set the correct site.
        OcSite.setSite(SupportedSite.OPENCACHING_DE);
        SiteHelper.setSite(SupportedSite.OPENCACHING_DE);

        testClient = new TestClient();
        final boolean loggedIn = testClient.login();
        assertTrue(loggedIn);

        System.out.println("OKAPI token is uninitialized. Fetching ...");
        assertNotNull(testClient.requestToken());
    }

    /**
     * Test getting the caches around a given position. This does not use a user filter and just
     * checks that at least 3 matches are present.
     */
    @Test
    @DisplayName("Test getting the caches around")
    public void testGetCachesAroundBasic() throws Exception {
        final List<Geocache> caches =
                Okapi.getCachesAround(null, null, 53.01952, 008.53440, 1.0, new ArrayList<>());
        assertNotNull(caches);
        assertTrue(caches.size() >= 3, Integer.toString(caches.size()));
    }

    /**
     * Test getting the caches around a given position. This does not use a user filter, but checks
     * that one cache is present.
     */
    @Test
    @DisplayName("Test getting the caches around without a user filter")
    public void testGetCachesAroundWithoutUserFilter() throws Exception {
        final List<Geocache> caches =
                Okapi.getCachesAround(null, null, 00.21667, 000.61667, 1.0, new ArrayList<>());
        assertNotNull(caches);
        assertTrue(caches.size() >= 1);

        boolean containsCache = false;
        for (final Geocache geocache : caches) {
            if (geocache.getCode().equals("OC13A45")
                    && geocache.getName().equals("cmanager TEST cache")) {
                containsCache = true;
                break;
            }
        }
        assertTrue(containsCache);
    }

    /**
     * Test getting the caches around a given position. This uses a user filter and checks that one
     * cache is present.
     */
    @Test
    @DisplayName("Test getting the caches around with a user filter")
    public void testGetCachesAroundWithUserFilter() throws Exception {
        // Determine corresponding UUID:
        // https://www.opencaching.de/okapi/services/users/by_username?username=cmanagerTestÄccount&consumer_key=XXXX&fields=uuid
        final List<Geocache> caches =
                Okapi.getCachesAround(
                        testClient,
                        "a912cccd-1c60-11e7-8e90-86c6a7325f31",
                        00.21667,
                        000.61667,
                        1.0,
                        new ArrayList<>());
        assertNotNull(caches);

        boolean containsCache = false;
        for (final Geocache geocache : caches) {
            if (geocache.getCode().equals("OC13A45")
                    && geocache.getName().equals("cmanager TEST cache")) {
                containsCache = true;
                break;
            }
        }
        // The account has logged the cache.
        assertFalse(containsCache);
    }

    /** Test getting the UUID of the test user. */
    @Test
    @DisplayName("Test getting the user ID ot the test user")
    public void testGetUuid() throws Exception {
        // UUID of the user `cmanager` used for testing.
        assertEquals("23fe0d6a-9cf3-11ea-8df9-d516ed642eb6", Okapi.getUuid(testClient));
    }

    /** Test getting the user name of the test user. */
    @Test
    @DisplayName("Test getting the user name of the test user")
    public void testGetUsername() throws Exception {
        assertEquals(SiteHelper.getUsername(), Okapi.getUsername(testClient));
    }

    /** Test getting the home coordinates of the test user. */
    @Test
    @DisplayName("Test getting home coordinates of the test user")
    public void testGetHomeCoordinates() throws Exception {
        // Home coordinates of the user `cmanager` used for testing.
        final Coordinate expected = new Coordinate("N 42° 42.000 E 042° 42.000");
        final Coordinate actual = Okapi.getHomeCoordinates(testClient);
        assertNotNull(actual);
        assertTrue(expected.equals(actual));
    }
}
