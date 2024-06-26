package cmanager.geo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import cmanager.exception.CoordinateUnparsableException;
import java.util.Arrays;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Tests for the coordinate container. */
public class CoordinateTest {

    /** Test the instantiation of the class using the different constructors. */
    @Test
    @DisplayName("Test coordinate constructor")
    public void testCreation() {
        final Coordinate coordinate1 = new Coordinate("1.23", "4.56");
        assertEquals(1.23, coordinate1.getLatitude(), 0.0);
        assertEquals(4.56, coordinate1.getLongitude(), 0.0);

        final Coordinate coordinate2 = new Coordinate(1.23, 4.56);
        final String[] coordinate2Parts = coordinate2.toString().split(",");
        assertEquals(1.23, coordinate2.getLatitude(), 0.0);
        assertEquals(4.56, coordinate2.getLongitude(), 0.0);
        assertEquals(1.23, Double.parseDouble(coordinate2Parts[0]), 0.01);
        assertEquals(4.56, Double.parseDouble(coordinate2Parts[1]), 0.01);

        assertTrue(coordinate1.equals(coordinate2)); // assertEquals will fail.
        assertEquals(0, coordinate1.distanceHaversine(coordinate2), 0);

        final Coordinate coordinate3 = new Coordinate(1.23, 4.567);
        assertFalse(coordinate1.equals(coordinate3)); // assertEquals will fail.
        assertEquals(778.1851, coordinate1.distanceHaversine(coordinate3), 0.00009);
        assertEquals(778.185, coordinate1.distanceHaversineRounded(coordinate3), 0);
    }

    /** Test the string conversion with small coordinate values (see issue #44). */
    @Test
    @DisplayName("Test string conversion with small values")
    public void testToStringWithSmallValues() {
        final Coordinate coordinate = new Coordinate(0.00000383, 0.00005);
        assertEquals("3.83E-6, 5.0E-5", coordinate.toShortString());
        assertNotEquals(coordinate.toShortString(), coordinate.toString());
    }

    /** Test the string conversion with a custom delimiter (see issue #44). */
    @Test
    @DisplayName("Test string conversion with custom delimiter")
    public void testToStringWithDelimiter() {
        final Coordinate coordinate = new Coordinate(0.00000383, 0.00005);
        final String result = coordinate.toString("|");
        assertTrue(result.startsWith("0.0000038299"), result);
        assertTrue(result.contains("|0.00005000"), result);
    }

    /** Test the distance calculation. */
    @Test
    @DisplayName("Test distance calculation")
    public void testDistance() {
        final Coordinate coordinate1 = new Coordinate(53.09780, 8.74908);
        final Coordinate coordinate2 = new Coordinate(53.05735, 8.59148);
        assertEquals(11448.0325, coordinate1.distanceHaversine(coordinate2), 0.0009);
    }

    /**
     * Test the parser using the given coordinate string and the expected coordinate values.
     *
     * @param string The coordinate to parse as a string.
     * @param latitude The expected latitude of the coordinate.
     * @param longitude The expected longitude of the coordinate.
     */
    private void parse(final String string, final double latitude, final double longitude) {
        try {
            final Coordinate coordinate = new Coordinate(string);
            assertEquals(latitude, coordinate.getLatitude(), 0.0);
            assertEquals(longitude, coordinate.getLongitude(), 0.00009);
        } catch (CoordinateUnparsableException exception) {
            fail(Arrays.toString(exception.getStackTrace()));
        }
    }

    /**
     * Test the parser using the given coordinate string. This will expect the coordinate to be
     * 53.1073, 8.12945.
     *
     * @param string The coordinate to parse as a string.
     */
    private void parse(final String string) {
        parse(string, 53.1073, 8.12945);
    }

    /** Test parsing differently formatted coordinates. */
    @Test
    @DisplayName("Test parsing different input formatting")
    public void testParsing() {
        parse(" N 53° 06.438' E 008° 07.767' (WGS84)");
        parse("  N53° 06.438' E 008° 07.767' (WGS84)");
        parse("N 53°06.438' E 008° 07.767' (WGS84)");
        parse("N 53 06.438' E 008° 07.767'");
        parse("N 53 06.438 E 008° 07.767' (WGS84)");
        parse("N 53 06.438 E 008 07.767' (WGS84)");
        parse("N 53 06.438E008 07.767' (WGS84)");
        parse("N 53 06E008 07' (WGS84)", 53.1, 8.1166);
        parse("    N 53° 06.438' E 008° 07.767' ");
    }
}
