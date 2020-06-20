package cmanager.okapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import cmanager.geo.Coordinate;
import cmanager.geo.Geocache;
import cmanager.oc.OcSite;
import cmanager.oc.SupportedSite;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Tests for the OKAPI-based methods which do not require an user login. */
public class OkapiTest {

    /** Set up the test client. */
    @BeforeAll
    public static void setUp() {
        // Ensure that we are using the German main site.
        OcSite.setSite(SupportedSite.OPENCACHING_DE);
    }

    /** Test the conversion of an username to an UUID with a not existing user. */
    @Test
    @DisplayName("Test user name conversion with not existing user")
    public void testUsernameToUuidNotExisting() throws Exception {
        final String uuid = Okapi.usernameToUuid("This.User.Does.Not.Exist");
        assertNull(uuid);
    }

    /** Test the conversion of an username to an UUID with a valid user. */
    @Test
    @DisplayName("Test user name conversion with an existing user")
    public void testUsernameToUuidValid() throws Exception {
        final String uuid = Okapi.usernameToUuid("cmanagerTestÄccount");
        assertEquals("a912cccd-1c60-11e7-8e90-86c6a7325f31", uuid);
    }

    /** Test the cache getter with an invalid OC code. */
    @Test
    @DisplayName("Test retrieving an invalid cache")
    public void testGetCacheNotExisting() throws Exception {
        final Geocache geocache = Okapi.getCache("This.Cache.Does.Not.Exist");
        assertNull(geocache);
    }

    /** Test the cache getter with a cache having no GC waypoint set. */
    @Test
    @DisplayName("Test retrieving a cache without a GC waypoint")
    public void testGetCacheWithoutGc() throws Exception {
        final Geocache geocache = Okapi.getCache("OC827D");

        assertNotNull(geocache);
        assertEquals("auftanken", geocache.getName());
        assertTrue(geocache.getCoordinate().equals(new Coordinate(49.955717, 8.332967)));
        assertEquals("Tradi", geocache.getType().asNiceType()); // OKAPI has no "Drive-In" type.
        assertNull(geocache.getCodeGc());
        assertEquals(1.0, geocache.getDifficulty(), 0.0);
        assertEquals(2.0, geocache.getTerrain(), 0.0);
        assertTrue(geocache.isArchived());
    }

    /** Test the cache getter with a cache having a GC waypoint set. */
    @Test
    @DisplayName("Test retrieving a cache with a GC waypoint")
    public void testGetCacheWithGc() throws Exception {
        final Geocache geocache = Okapi.getCache("OC11ECF");

        assertNotNull(geocache);
        assertEquals("Gehüpft wie gesprungen", geocache.getName());
        assertTrue(geocache.getCoordinate().equals(new Coordinate(53.019517, 8.5344)));
        assertEquals("Tradi", geocache.getType().asNiceType());
        assertEquals("GC46PY8", geocache.getCodeGc());
        assertEquals(2.0, geocache.getDifficulty(), 0.0);
        assertEquals(1.5, geocache.getTerrain(), 0.0);
        assertTrue(geocache.isArchived());
    }

    /** Test the cache details getter. */
    @Test
    @DisplayName("Test completing the cache details (example 1)")
    public void testCompleteCacheDetailsExample1() throws Exception {
        Geocache geocache = Okapi.getCache("OC827D");
        assertNotNull(geocache);

        geocache = Okapi.completeCacheDetails(geocache);

        assertNotNull(geocache);
        assertNotNull(geocache.getContainer());
        assertEquals("Nano", geocache.getContainer().asGc());
        assertEquals("following", geocache.getOwner());
        assertEquals("", geocache.getListingShort());

        // Adopt once http://redmine.opencaching.de/issues/1045 has beend done.
        final String expected =
                "<p>ein kleiner Drive-in für zwischendurch<br /><br />\nStift mitbringen!</p>\n<p><em>&copy; <a href='https://www.opencaching.de/viewprofile.php?userid=150360'>following</a>, <a href='https://www.opencaching.de/viewcache.php?cacheid=136478'>Opencaching.de</a>, <a href='https://creativecommons.org/licenses/by-nc-nd/3.0/de/'>CC-BY-NC-ND</a>, Stand: ";
        final String listing = geocache.getListing().trim().substring(0, expected.length());
        assertEquals(expected.length(), listing.length());
        assertEquals(expected, listing);

        assertEquals("<magnetisch>", geocache.getHint());

        assertTrue(geocache.doesRequirePassword());
        assertEquals("136478", geocache.getInternalId());
    }

    /** Test the cache details getter. */
    @Test
    @DisplayName("Test completing the cache details (example 1)")
    public void testCompleteCacheDetailsExample2() throws Exception {
        Geocache geocache = Okapi.getCache("OC11ECF");
        assertNotNull(geocache);

        geocache = Okapi.completeCacheDetails(geocache);

        assertNotNull(geocache);
        assertEquals("Micro", geocache.getContainer().asGc());
        assertEquals("Samsung1", geocache.getOwner());
        assertEquals("", geocache.getListingShort());

        // Adopt once http://redmine.opencaching.de/issues/1045 has beend done.
        final String expected =
                "<p><span>In Erinnerung an die schöne Zeit, die ich hier als Teenager mit Pferden in diesem schönen Gelände verbringen durfte:<br />\nEin kleiner Cache für unterwegs, hoffentlich auch eine kleine Herausforderung für euch ;).<br /><br />\nViel Spaß und Erfolg wünschen Samsung1 und Oreas1987.</span></p>\n<p><em>&copy; <a href='https://www.opencaching.de/viewprofile.php?userid=316615'>Samsung1</a>, <a href='https://www.opencaching.de/viewcache.php?cacheid=176512'>Opencaching.de</a>, <a href='https://creativecommons.org/licenses/by-nc-nd/3.0/de/'>CC-BY-NC-ND</a>, Stand: ";
        final String listing = geocache.getListing().trim().substring(0, expected.length());
        assertEquals(expected.length(), listing.length());
        assertEquals(expected, listing);

        assertEquals("", geocache.getHint());

        assertFalse(geocache.doesRequirePassword());
        assertEquals("176512", geocache.getInternalId());
    }

    @Test
    @DisplayName("Test getting the log ID")
    public void testGetLogId() throws Exception {
        final String logId = Okapi.getLogId("4785b5f8-46e0-102b-9919-00163e645ac3");
        assertEquals("278256", logId);
    }
}
