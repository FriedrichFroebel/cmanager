package cmanager.list;

import java.io.Serializable;

/** Persistence information container for lists, wrapping the path. */
class PersistenceInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** The path pointing to the list file. */
    private final String path;

    /**
     * Create a new instance with the given path.
     *
     * @param path The path pointing to the list file.
     */
    public PersistenceInfo(String path) {
        this.path = path;
    }

    /**
     * Get the path pointing to the list file.
     *
     * @return The path.
     */
    public String getPath() {
        return path;
    }
}
