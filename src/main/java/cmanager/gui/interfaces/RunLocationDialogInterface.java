package cmanager.gui.interfaces;

import cmanager.geo.Geocache;

/** Interface to open the location dialog. */
public interface RunLocationDialogInterface {

    /**
     * Open the dialog with the given geocache instance.
     *
     * @param geocache The geocache instance to use for the location name and position inside the
     *     edit area of the location dialog.
     */
    void openDialog(Geocache geocache);
}
