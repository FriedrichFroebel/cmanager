package cmanager.list.filter;

import cmanager.geo.Geocache;

public class TerrainFilter extends FilterModel {

    private static final long serialVersionUID = -6582495781375197847L;

    private Double terrainMin = 1.0;
    private Double terrainMax = 5.0;

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

    @Override
    protected boolean isGood(Geocache geocache) {
        return geocache.getTerrain() >= terrainMin && geocache.getTerrain() <= terrainMax;
    }
}
