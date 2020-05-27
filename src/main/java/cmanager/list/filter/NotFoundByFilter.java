package cmanager.list.filter;

import cmanager.geo.Geocache;
import cmanager.geo.GeocacheLog;
import java.util.ArrayList;
import java.util.List;

public class NotFoundByFilter extends FilterModel {

    private static final long serialVersionUID = 5585453135104325357L;

    private List<String> usernames = new ArrayList<>();

    public NotFoundByFilter() {
        super(FILTER_TYPE.SINGLE_FILTER_VALUE);
        labelLeft2.setText("Not Found by: ");
        runDoModelUpdateNow = this::retrieveUsernames;
    }

    private void retrieveUsernames() {
        final String input = textField.getText();
        final String[] parts = input.split(",");
        usernames = new ArrayList<>();
        for (final String part : parts) {
            usernames.add(part.trim().toLowerCase());
        }
    }

    @Override
    protected boolean isGood(Geocache geocache) {
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
