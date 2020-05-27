package cmanager.list;

import java.io.Serializable;

class PersistenceInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String path;

    public PersistenceInfo(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
