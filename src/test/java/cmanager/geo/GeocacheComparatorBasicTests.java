package cmanager.geo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Basic tests for the geocache comparator. */
public class GeocacheComparatorBasicTests {

    /** Test the with the cache names being exactly the same. */
    @Test
    @DisplayName("Test cache names being exactly the same")
    public void testNameExactlyTheSame() {
        final Geocache geocache1 =
                new Geocache("OCTEST", "Test name", new Coordinate(1.0, 1.0), 1.0, 1.0, "Tradi");
        final Geocache geocache2 =
                new Geocache("GCTEST", "Test name", new Coordinate(1.0, 1.0), 1.0, 1.0, "Tradi");

        final double similarity = GeocacheComparator.calculateSimilarity(geocache1, geocache2);
        assertEquals(1.0, similarity, 0.0);
    }

    /** Test the with the cache names being completely different. */
    @Test
    @DisplayName("Test cache names being completely different")
    public void testNameCompletelyDifferent() {
        final Geocache geocache1 =
                new Geocache("OCTEST", "Dummy", new Coordinate(1.0, 1.0), 1.0, 1.0, "Tradi");
        final Geocache geocache2 =
                new Geocache("GCTEST", "Test name", new Coordinate(1.0, 1.0), 1.0, 1.0, "Tradi");

        final double similarity = GeocacheComparator.calculateSimilarity(geocache1, geocache2);
        // Same coordinate, same difficulty and terrain rating, same type.
        assertEquals(0.8, similarity, 0.0);
    }

    /** Test the with the cache names only differing in their padding. */
    @Test
    @DisplayName("Test cache names differing in their whitespace padding")
    public void testNameDifferentWhitespacePadding() {
        final Geocache geocache1 =
                new Geocache("OCTEST", "Test name", new Coordinate(1.0, 1.0), 1.0, 1.0, "Tradi");
        final Geocache geocache2 =
                new Geocache(
                        "GCTEST", "    Test name    ", new Coordinate(1.0, 1.0), 1.0, 1.0, "Tradi");

        final double similarity = GeocacheComparator.calculateSimilarity(geocache1, geocache2);
        assertEquals(1.0, similarity, 0.0);
    }

    /** Test date filtering for an event with a valid duplicate. */
    @Test
    @DisplayName("Test event date filtering with valid duplicate")
    public void testEventDateFilteringValid() {
        final Geocache geocache1 =
                new Geocache("GCTEST", "Test name", new Coordinate(1.0, 1.0), 1.0, 1.0, "Event");
        geocache1.setDateHidden("2019-04-06T19:00:00");

        final Geocache geocache2 =
                new Geocache("OCTEST", "Test name", new Coordinate(1.0, 1.0), 1.0, 1.0, "Event");
        geocache2.setDateHidden("2019-04-06T00:00:00+02:00");

        final double similarity = GeocacheComparator.calculateSimilarity(geocache1, geocache2);
        assertEquals(1.0, similarity, 0.0);
    }

    /** Test date filtering for an event with an invalid duplicate. */
    @Test
    @DisplayName("Test event date filtering with invalid pair")
    public void testEventDateFilteringInvalid() {
        final Geocache geocache1 =
                new Geocache("GCTEST", "Test name", new Coordinate(1.0, 1.0), 1.0, 1.0, "Event");
        geocache1.setDateHidden("2018-11-09T00:00:00");

        final Geocache geocache2 =
                new Geocache("OCTEST", "Test name", new Coordinate(1.0, 1.0), 1.0, 1.0, "Event");
        geocache2.setDateHidden("2019-06-19T00:00:00+02:00");

        final double similarity = GeocacheComparator.calculateSimilarity(geocache1, geocache2);
        assertTrue(similarity < 1.0);
    }
}
