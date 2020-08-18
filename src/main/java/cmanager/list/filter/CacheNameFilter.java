package cmanager.list.filter;

import cmanager.geo.Geocache;

/** Filter geocaches by their name. */
public class CacheNameFilter extends FilterModel {

    private static final long serialVersionUID = -6582495781375197847L;

    /** The string to filter for. */
    private String filterString = "";

    /** Create a new instance of the filter. */
    public CacheNameFilter() {
        super(FILTER_TYPE.SINGLE_FILTER_VALUE);
        labelLeft2.setText("Cache name contains: ");
        runDoModelUpdateNow = () -> filterString = textField.getText().toLowerCase();
    }

    /**
     * Check whether the given geocache has a name containing the requested string.
     *
     * @param geocache The geocache to check.
     * @return The check result.
     */
    @Override
    protected boolean isGood(final Geocache geocache) {
        return geocache.getName().toLowerCase().contains(filterString);
    }
}
