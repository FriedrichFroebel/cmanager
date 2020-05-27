package cmanager.oc;

import cmanager.geo.Geocache;

public interface OutputInterface {

    void setProgress(Integer count, Integer max);

    void match(Geocache geocache, Geocache opencache);
}
