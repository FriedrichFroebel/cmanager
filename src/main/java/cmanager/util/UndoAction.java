package cmanager.util;

import cmanager.geo.Geocache;
import java.util.ArrayList;
import java.util.List;

/** Wrapper for a geocache list state. */
public class UndoAction {

    /** The geocache list for the current state. */
    private final List<Geocache> state;

    /**
     * Create a new instance with the given list.
     *
     * @param list The geocache list for the current state.
     */
    public UndoAction(final List<Geocache> list) {
        state = new ArrayList<>(list);
    }

    /**
     * Get the saved state.
     *
     * @return The saved geocache list.
     */
    public List<Geocache> getState() {
        return state;
    }
}
