package cmanager;

import cmanager.geo.Geocache;

public class CacheListFilterCacheName extends CacheListFilterModel {

    private static final long serialVersionUID = -6582495781375197847L;

    private String filterString = "";

    public CacheListFilterCacheName() {
        super(FILTER_TYPE.SINGLE_FILTER_VALUE);
        labelLeft2.setText("Cachename contains: ");
        runDoModelUpdateNow =
                new Runnable() {
                    @Override
                    public void run() {
                        filterString = textField.getText().toLowerCase();
                    }
                };
    }

    @Override
    protected boolean isGood(Geocache geocache) {
        return geocache.getName().toLowerCase().contains(filterString);
    }
}
