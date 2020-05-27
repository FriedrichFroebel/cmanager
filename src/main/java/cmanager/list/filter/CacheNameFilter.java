package cmanager.list.filter;

import cmanager.geo.Geocache;

public class CacheNameFilter extends FilterModel {

    private static final long serialVersionUID = -6582495781375197847L;

    private String filterString = "";

    public CacheNameFilter() {
        super(FILTER_TYPE.SINGLE_FILTER_VALUE);
        labelLeft2.setText("Cache name contains: ");
        runDoModelUpdateNow = () -> filterString = textField.getText().toLowerCase();
    }

    @Override
    protected boolean isGood(Geocache geocache) {
        return geocache.getName().toLowerCase().contains(filterString);
    }
}
