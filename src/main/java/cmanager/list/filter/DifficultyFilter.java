package cmanager.list.filter;

import cmanager.geo.Geocache;

public class DifficultyFilter extends FilterModel {

    private static final long serialVersionUID = -6582495781375197847L;

    private Double difficultyMin = 1.0;
    private Double difficultyMax = 5.0;

    public DifficultyFilter() {
        super(FILTER_TYPE.BETWEEN_ONE_AND_FIVE_FILTER_VALUE);
        getLabelLeft().setText("min Difficulty:");
        getLabelRight().setText("max Difficulty:");

        runDoModelUpdateNow =
                () -> {
                    difficultyMin = getValueLeft();
                    difficultyMax = getValueRight();
                };
    }

    @Override
    protected boolean isGood(Geocache geocache) {
        return geocache.getDifficulty() >= difficultyMin
                && geocache.getDifficulty() <= difficultyMax;
    }
}
