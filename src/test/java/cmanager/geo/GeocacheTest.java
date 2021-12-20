package cmanager.geo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import cmanager.oc.OcSite;
import cmanager.oc.SupportedSite;
import cmanager.util.ObjectHelper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Tests for the geocache container. */
public class GeocacheTest {

    /** Set the OC site. */
    @BeforeAll
    public static void setUp() {
        // Set the correct site.
        OcSite.setSite(SupportedSite.OPENCACHING_DE);
    }

    /** Test the constructor with valid parameters. */
    @Test
    @DisplayName("Test constructor with valid parameters")
    public void testConstructorValid() {
        new Geocache("OC1234", "test", new Coordinate(0, 0), 0.0, 0.0, "Tradi");
    }

    /** Test the constructor with the cache code being null. */
    @Test
    @DisplayName("Test constructor with code being null")
    public void testConstructorCodeCannotBeNull() {
        assertThrows(
                NullPointerException.class,
                () -> {
                    new Geocache(null, "test", new Coordinate(0, 0), 0.0, 0.0, "Tradi");
                });
    }

    /** Test the constructor with the cache name being null. */
    @Test
    @DisplayName("Test constructor with name being null")
    public void testConstructorNameCannotBeNull() {
        assertThrows(
                NullPointerException.class,
                () -> {
                    new Geocache("OC1234", null, new Coordinate(0, 0), 0.0, 0.0, "Tradi");
                });
    }

    /** Test the constructor with the cache coordinate being null. */
    @Test
    @DisplayName("Test constructor with coordinate being null")
    public void testConstructorCoordinateCannotBeNull() {
        assertThrows(
                NullPointerException.class,
                () -> {
                    new Geocache("OC1234", "test", null, 0.0, 0.0, "Tradi");
                });
    }

    /** Test the constructor with the cache difficulty rating being null. */
    @Test
    @DisplayName("Test constructor with difficulty being null")
    public void testConstructorDifficultyCannotBeNull() {
        assertThrows(
                NullPointerException.class,
                () -> {
                    new Geocache("OC1234", "test", new Coordinate(0, 0), null, 0.0, "Tradi");
                });
    }

    /** Test the constructor with the cache terrain rating being null. */
    @Test
    @DisplayName("Test constructor with terrain being null")
    public void testConstructorTerrainCannotBeNull() {
        assertThrows(
                NullPointerException.class,
                () -> {
                    new Geocache("OC1234", "test", new Coordinate(0, 0), 0.0, null, "Tradi");
                });
    }

    /** Test the constructor with the cache type being null. */
    @Test
    @DisplayName("Test constructor with type being null")
    public void testConstructorTypeCannotBeNull() {
        assertThrows(
                NullPointerException.class,
                () -> {
                    new Geocache("OC1234", "test", new Coordinate(0, 0), 0.0, 0.0, (String) null);
                });
    }

    /** Test the data interpretation with an OC tradi. */
    @Test
    @DisplayName("Test data interpretation with an OC tradi")
    public void testDataInterpretationOpencachingTradi() {
        final Geocache geocache =
                new Geocache("OC1234", "test", new Coordinate(0, 0), 0.0, 0.0, "Tradi");

        assertTrue(geocache.isOc());
        assertFalse(geocache.isGc());
        assertFalse(geocache.hasVolatileStart());
        assertEquals("https://www.opencaching.de/OC1234", geocache.getUrl());
    }

    /** Test the data interpretation with a GC tradi. */
    @Test
    @DisplayName("Test data interpretation with a GC tradi")
    public void testDataInterpretationGeocachingTradi() {
        final Geocache geocache =
                new Geocache("GC1234", "test", new Coordinate(0, 0), 0.0, 0.0, "Tradi");

        assertTrue(geocache.isGc());
        assertFalse(geocache.isOc());
        assertFalse(geocache.hasVolatileStart());
        assertEquals("https://www.geocaching.com/geocache/GC1234", geocache.getUrl());
    }

    /** Test data interpretation with an OC mystery cache. */
    @Test
    @DisplayName("Test data interpretation with an OC mystery")
    public void testDataInterpretationOpencachingMystery() {
        final Geocache geocache =
                new Geocache("OC1234", "test", new Coordinate(0, 0), 0.0, 0.0, "Mystery");
        assertTrue(geocache.hasVolatileStart());
    }

    /** Test the serialization. */
    @Test
    @DisplayName("Test the serialization")
    public void testSerialize() {
        final Geocache geocache1 =
                new Geocache("OC1234", "test", new Coordinate(0, 0), 0.0, 0.0, "Tradi");
        final Geocache geocache2 = ObjectHelper.copy(geocache1);
        assertNotNull(geocache2);
    }

    @Test
    @DisplayName("Test the string representation")
    public void testToString() {
        final Geocache geocache =
                new Geocache(
                        "OC1234",
                        "test name",
                        new Coordinate(00.21667, 000.61667),
                        1.0,
                        2.5,
                        "Tradi");
        assertEquals(
                "1.0/2.5 OC1234 (Tradi) -- 0.21667, 0.61667 -- test name", geocache.toString());
    }
}
