package cmanager.oc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mockStatic;

import cmanager.geo.Coordinate;
import cmanager.geo.Geocache;
import cmanager.okapi.Okapi;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

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

    @Test
    public void testFindSingleGeocacheGetCachesAroundIsNull() throws Exception {
        final Geocache geocache =
                new Geocache("GC0", "", new Coordinate(0.0, 0.0), 1.0, 1.0, "Traditional");

        try (final MockedStatic<Okapi> okapiMockedStatic = mockStatic(Okapi.class);
                final MockedStatic<SearchCache> searchCacheMockedStatic =
                        mockStatic(SearchCache.class)) {
            final List<Geocache> okapiRuntimeCache = OcUtil.OKAPI_RUNTIME_CACHE;
            okapiMockedStatic
                    .when(
                            () ->
                                    Okapi.getCachesAround(
                                            null, null, geocache, 0.05, okapiRuntimeCache))
                    .thenReturn(null);
            searchCacheMockedStatic
                    .when(() -> SearchCache.isEmptySearch(geocache, null))
                    .thenReturn(false);
            AtomicReference<Throwable> throwableAtomicReference = new AtomicReference<>(null);

            OcUtil.findSingleGeocache(
                    new AtomicBoolean(false),
                    null,
                    null,
                    null,
                    null,
                    null,
                    new AtomicInteger(),
                    geocache,
                    throwableAtomicReference);
            assertNull(throwableAtomicReference.get());
            okapiMockedStatic.verify(
                    () -> Okapi.getCachesAround(null, null, geocache, 0.05, okapiRuntimeCache));
            searchCacheMockedStatic.verify(() -> SearchCache.isEmptySearch(geocache, null));
        }
    }
}
