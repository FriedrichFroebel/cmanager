package cmanager.list.filter;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import cmanager.geo.Coordinate;
import cmanager.geo.Geocache;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Test the filter model runner. */
public class FilterModelTest {

    /** Test filtering with an empty list. This should not produce an error. */
    @Test
    @DisplayName("Test filtering with an empty list")
    public void testEmptyCacheList() {
        final FilterModel filterModel = new CacheNameFilter();
        final List<Geocache> emptyList = new ArrayList<>(0);

        assertDoesNotThrow(() -> filterModel.getFiltered(emptyList));
        assertEquals(0, filterModel.getFiltered(emptyList).size());
    }

    /** Test regular filtering. */
    @Test
    @DisplayName("Test filtering with some elements in the list")
    public void testRegularFiltering() {
        final FilterModel filterModel = new CacheNameFilter();

        final List<Geocache> list = new ArrayList<>(3);
        list.add(new Geocache("GC1234", "test", new Coordinate(0, 0), 0.0, 0.0, "Tradi"));
        list.add(new Geocache("GC2345", "test1", new Coordinate(1, 2), 0.0, 0.0, "Tradi"));
        list.add(new Geocache("GC3456", "test2", new Coordinate(2, 3), 0.0, 0.0, "Tradi"));

        final List<Geocache> filtered = filterModel.getFiltered(list);
        assertTrue(filtered.size() > 0);
        assertTrue(filtered.size() <= 3);
    }
}
