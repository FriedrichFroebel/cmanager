package cmanager;

import cmanager.geo.Geocache;
import java.util.ArrayList;
import java.util.List;

public class UndoAction {

    private final List<Geocache> state;

    public UndoAction(List<Geocache> list) {
        state = new ArrayList<>(list);
    }

    public List<Geocache> getState() {
        return state;
    }
}
