package cmanager;

import cmanager.geo.Geocache;

public class CacheListFilterTerrain extends CacheListFilterModel {

    private static final long serialVersionUID = -6582495781375197847L;

    private Double terrainMin = 1.0;
    private Double terrainMax = 5.0;

    public CacheListFilterTerrain() {
        super(FILTER_TYPE.BETWEEN_ONE_AND_FIVE_FILTER_VALUE);
        getLabelLeft().setText("min Terrain:");
        getLabelRight().setText("max Terrain:");

        runDoModelUpdateNow =
                new Runnable() {
                    @Override
                    public void run() {
                        terrainMin = getValueLeft();
                        terrainMax = getValueRight();
                    }
                };
    }

    @Override
    protected boolean isGood(Geocache geocache) {
        return geocache.getTerrain() >= terrainMin && geocache.getTerrain() <= terrainMax;
    }
}
