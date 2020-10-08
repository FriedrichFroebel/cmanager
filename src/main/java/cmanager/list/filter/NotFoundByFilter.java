package cmanager.list.filter;

import cmanager.geo.Geocache;
import cmanager.geo.GeocacheLog;
import cmanager.list.CacheListFilterType;
import java.util.ArrayList;
import java.util.List;

/** Filter geocaches by found status. */
public class NotFoundByFilter extends FilterModel {

    private static final long serialVersionUID = 5585453135104325357L;

    /** The list of usernames who are not allowed to have a found log. */
    private List<String> usernames = new ArrayList<>();

    /** Create a new instance of the filter. */
    public NotFoundByFilter() {
        super(CacheListFilterType.SINGLE_FILTER_VALUE);
        labelLeft2.setText("Not Found by: ");
        runDoModelUpdateNow = this::retrieveUsernames;
    }

    /** Load the usernames from the text field. */
    private void retrieveUsernames() {
        final String input = textField.getText();
        final String[] parts = input.split(",");
        usernames = new ArrayList<>();
        for (final String part : parts) {
            usernames.add(part.trim().toLowerCase());
        }
    }

    /**
     * Check whether the given geocache has not been found by any of the given users.
     *
     * @param geocache The geocache to check.
     * @return The check result.
     */
    @Override
    protected boolean isGood(final Geocache geocache) {
        for (final GeocacheLog log : geocache.getLogs()) {
            for (final String username : usernames) {
                if (log.isFoundLog() && log.isAuthor(username)) {
                    return false;
                }
            }
        }
        return true;
    }
}
