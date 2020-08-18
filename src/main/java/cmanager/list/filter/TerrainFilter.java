package cmanager.list.filter;

import cmanager.geo.Geocache;

/** Filter geocaches by terrain rating. */
public class TerrainFilter extends FilterModel {

    private static final long serialVersionUID = -6582495781375197847L;

    /** The minimal terrain rating to filter for. */
    private Double terrainMin = 1.0;

    /** The maximal terrain rating to filter for. */
    private Double terrainMax = 5.0;

    /** Create a new instance of the filter. */
    public TerrainFilter() {
        super(FILTER_TYPE.BETWEEN_ONE_AND_FIVE_FILTER_VALUE);
        getLabelLeft().setText("min Terrain:");
        getLabelRight().setText("max Terrain:");

        runDoModelUpdateNow =
                () -> {
                    terrainMin = getValueLeft();
                    terrainMax = getValueRight();
                };
    }

    /**
     * Check whether the given geocache satisfies the specified terrain range.
     *
     * @param geocache The geocache to check.
     * @return The check result.
     */
    @Override
    protected boolean isGood(final Geocache geocache) {
        return geocache.getTerrain() >= terrainMin && geocache.getTerrain() <= terrainMax;
    }
}
