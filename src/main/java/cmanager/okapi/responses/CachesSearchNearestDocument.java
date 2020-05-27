package cmanager.okapi.responses;

/**
 * Container for nearest search results.
 *
 * @see <a href="https://www.opencaching.de/okapi/services/caches/search/nearest.html">OKAPI
 *     documentation</a>
 */
public class CachesSearchNearestDocument {

    /** A list of cache codes. */
    private String[] results;

    /**
     * Whether there are more results for the query, but they were not returned because of the
     * current limit.
     */
    private boolean more;

    /**
     * Get the nearest cache codes.
     *
     * @return The nearest cache codes.
     */
    public String[] getResults() {
        return results;
    }
}
