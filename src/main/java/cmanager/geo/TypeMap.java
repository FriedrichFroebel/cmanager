package cmanager.geo;

import cmanager.gui.ExceptionPanel;
import java.util.ArrayList;

public class TypeMap {

    private ArrayList<ArrayList<String>> map = new ArrayList<>();

    public void add(String... key) {
        final ArrayList<String> list = new ArrayList<>(key.length);
        for (final String k : key) {
            list.add(k);
        }
        map.add(list);
    }

    public Integer getLowercase(String key) {
        key = key.toLowerCase();

        for (final ArrayList<String> list : map) {
            for (final String s : list) {
                if (s != null && s.toLowerCase().equals(key)) {
                    return map.indexOf(list);
                }
            }
        }

        ExceptionPanel.display(" ~~ unknown key: " + key + " ~~ ");
        return null;
    }

    public Integer get(String key) {
        for (final ArrayList<String> list : map) {
            for (final String s : list) {
                if (s != null && s.equals(key)) {
                    return map.indexOf(list);
                }
            }
        }

        ExceptionPanel.display(" ~~ unknown key: " + key + " ~~ ");
        return null;
    }

    public String get(int i, int j) {
        final ArrayList<String> list = map.get(i);
        return list.get(j);
    }
}
