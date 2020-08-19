package cmanager.oc;

import cmanager.geo.Geocache;

/** Interface for measuring output progress. */
public interface OutputInterface {

    /**
     * Set the current progress value.
     *
     * @param count The number of the current value.
     * @param max The maximum number.
     */
    void setProgress(Integer count, Integer max);

    /**
     * Indicate a match between the two geocache instance.
     *
     * @param geocache The geocache instance of the match.
     * @param opencache The corresponding opencache instance.
     */
    void match(Geocache geocache, Geocache opencache);
}
