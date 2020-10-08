package cmanager.list.filter;

import cmanager.geo.Geocache;
import cmanager.list.CacheListFilterType;

/** Filter geocaches by difficulty rating. */
public class DifficultyFilter extends FilterModel {

    private static final long serialVersionUID = -6582495781375197847L;

    /** The minimal difficulty rating to filter for. */
    private Double difficultyMin = 1.0;

    /** The maximal difficulty rating to filter for. */
    private Double difficultyMax = 5.0;

    /** Create a new instance of the filter. */
    public DifficultyFilter() {
        super(CacheListFilterType.BETWEEN_ONE_AND_FIVE_FILTER_VALUE);
        getLabelLeft().setText("min Difficulty:");
        getLabelRight().setText("max Difficulty:");

        runDoModelUpdateNow =
                () -> {
                    difficultyMin = getValueLeft();
                    difficultyMax = getValueRight();
                };
    }

    /**
     * Check whether the given geocache satisfies the specified difficulty range.
     *
     * @param geocache The geocache to check.
     * @return The check result.
     */
    @Override
    protected boolean isGood(final Geocache geocache) {
        return geocache.getDifficulty() >= difficultyMin
                && geocache.getDifficulty() <= difficultyMax;
    }
}
