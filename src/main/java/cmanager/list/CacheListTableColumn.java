package cmanager.list;

/** Available cache list table columns. */
public enum CacheListTableColumn {

    /** The geocache code. */
    CACHE_CODE(0),

    /** The geocache name. */
    CACHE_NAME(1),

    /** The geocache type. */
    CACHE_TYPE(2),

    /** The difficulty rating. */
    DIFFICULTY_RATING(3),

    /** The terrain rating. */
    TERRAIN_RATING(4),

    /** The latitude of the position. */
    COORDINATE_LATITUDE(5),

    /** The longitude of the position. */
    COORDINATE_LONGITUDE(6),

    /** The name of the geocache owner. */
    CACHE_OWNER(7),

    /** The distance of this cache from the home location. */
    DISTANCE(8),

    /** The date of the latest found log. */
    LATEST_FOUND_LOG(9);

    /** The corresponding column index. */
    private final int columnIndex;

    /** @param columnIndex Set the corresponding column index. */
    CacheListTableColumn(int columnIndex) {
        this.columnIndex = columnIndex;
    }

    /**
     * Get the column index for the specified enumeration value.
     *
     * @return The column index of the given enumeration value.
     */
    public int getColumnIndex() {
        return columnIndex;
    }
}
