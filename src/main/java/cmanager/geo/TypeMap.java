package cmanager.geo;

import cmanager.gui.ExceptionPanel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Container for a collection of types. */
public class TypeMap {

    /** The actual internal map. */
    private final List<List<String>> map = new ArrayList<>();

    /**
     * Add the given list of keys to the map.
     *
     * @param key The keys to add.
     */
    public void add(final String... key) {
        final List<String> list = new ArrayList<>(key.length);
        Collections.addAll(list, key);
        map.add(list);
    }

    /**
     * Get the index of the map entry with the given key.
     *
     * @param key The key to search for. It will be converted to lower-case for the search.
     * @return The index of the map entry with the given key.
     */
    public Integer getLowercase(String key) {
        key = key.toLowerCase();

        for (final List<String> list : map) {
            for (final String string : list) {
                if (string != null && string.toLowerCase().equals(key)) {
                    return map.indexOf(list);
                }
            }
        }

        ExceptionPanel.display(" ~~ unknown key: " + key + " ~~ ");
        return null;
    }

    /**
     * Get the index of the map entry with the given key.
     *
     * @param key The key to search for.
     * @return The index of the map entry with the given key.
     */
    public Integer get(final String key) {
        for (final List<String> list : map) {
            for (final String string : list) {
                if (string != null && string.equals(key)) {
                    return map.indexOf(list);
                }
            }
        }

        ExceptionPanel.display(" ~~ unknown key: " + key + " ~~ ");
        return null;
    }

    /**
     * Get the given map entry.
     *
     * @param i The main map slot index.
     * @param j The index of the entry inside the requested slot.
     * @return The requested entry.
     */
    public String get(final int i, final int j) {
        final List<String> list = map.get(i);
        return list.get(j);
    }
}
