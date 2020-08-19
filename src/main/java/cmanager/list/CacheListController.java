package cmanager.list;

import cmanager.geo.Geocache;
import cmanager.geo.Location;
import cmanager.gui.ExceptionPanel;
import cmanager.gui.components.CacheListView;
import cmanager.gui.interfaces.RunLocationDialogInterface;
import cmanager.list.filter.FilterModel;
import cmanager.settings.Settings;
import cmanager.util.ObjectHelper;
import java.awt.Dimension;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.table.TableColumn;

/** Cache list handler. */
public class CacheListController {

    /** The actual cache list controllers available. */
    private static final List<CacheListController> controllerList = new ArrayList<>();

    /**
     * Create a new cache list controller with the given values.
     *
     * @param desktop The pane to add the views to.
     * @param menuWindows The windows menu.
     * @param relativeLocation The relative location.
     * @param path The filepath to the cache list.
     * @param runLocationDialog The location dialog runner.
     * @return The newly created cache list controller.
     */
    public static CacheListController newCacheListController(
            final JDesktopPane desktop,
            final JMenu menuWindows,
            final Location relativeLocation,
            final String path,
            RunLocationDialogInterface runLocationDialog)
            throws Throwable {
        final CacheListController cacheListController =
                new CacheListController(
                        desktop, menuWindows, relativeLocation, path, runLocationDialog);
        controllerList.add(cacheListController);
        return cacheListController;
    }

    /**
     * Remove the given cache list controller.
     *
     * @param cacheListController The controller to remove.
     */
    public static void remove(final CacheListController cacheListController) {
        controllerList.remove(cacheListController);
    }

    /**
     * Get the cache list controller for the given frame.
     *
     * @param internalFrame The frame to get the controller for.
     * @return The controller for the given frame.
     */
    private static CacheListController getCacheListController(final JInternalFrame internalFrame) {
        for (final CacheListController cacheListController : controllerList) {
            if (cacheListController.view == internalFrame) {
                return cacheListController;
            }
        }
        return null;
    }

    /**
     * Get the cache list controller for the top view.
     *
     * @param desktop The pane to get the top view from.
     * @return The requested controller.
     */
    public static CacheListController getTopViewCacheController(final JDesktopPane desktop) {
        if (desktop.getAllFrames().length == 0) {
            return null;
        }

        final JInternalFrame internalFrame = desktop.getAllFrames()[0];
        return CacheListController.getCacheListController(internalFrame);
    }

    /**
     * Get the cache list view for the top view.
     *
     * @param desktop The pane to get the top view from.
     * @return The cache list view for the top view.
     */
    public static CacheListView getTopView(final JDesktopPane desktop) {
        return (CacheListView) desktop.getAllFrames()[0];
    }

    /**
     * Set the relative location for all cache list controllers.
     *
     * @param relativeLocation The relative location to set.
     */
    public static void setAllRelativeLocations(final Location relativeLocation) {
        for (final CacheListController cacheListController : controllerList) {
            cacheListController.setRelativeLocation(relativeLocation);
        }
    }

    /**
     * Store the persistence information.
     *
     * <p>This will add the information for the top view as the last element.
     *
     * @param desktop The pane to get the top view from.
     * @throws IOException Something went wrong with storing the data.
     */
    public static void storePersistenceInfo(final JDesktopPane desktop) throws IOException {
        final CacheListController top = getTopViewCacheController(desktop);

        // The top view controller will be saved at last.
        final ArrayList<PersistenceInfo> persistenceInfos = new ArrayList<>();
        for (final CacheListController cacheListController : controllerList) {
            if (cacheListController == top) {
                continue;
            }
            persistenceInfos.add(cacheListController.getPersistenceInfo());
        }
        if (top != null) {
            persistenceInfos.add(top.getPersistenceInfo());
        }

        Settings.setSerialized(Settings.Key.CLC_LIST, persistenceInfos);
    }

    /**
     * Load the persistence information.
     *
     * @param desktop The pane to add the views to and to use for displaying errors.
     * @param menuWindows The windows menu.
     * @param relativeLocation The relative location.
     * @param path The filepath to the cache list.
     * @param runLocationDialog The location dialog runner.
     * @throws ClassNotFoundException Something went wrong when deserializing the data. This has
     *     been added for a problem introduced by refactoring the class structure with version 0.5.
     */
    public static void reopenPersistentCacheListControllers(
            final JDesktopPane desktop,
            final JMenu menuWindows,
            final Location relativeLocation,
            RunLocationDialogInterface runLocationDialog)
            throws ClassNotFoundException {
        // Load the data.
        List<PersistenceInfo> persistenceInfoList;
        try {
            persistenceInfoList = Settings.getSerialized(Settings.Key.CLC_LIST);
            if (persistenceInfoList == null) {
                return;
            }
        } catch (ClassNotFoundException exception) {
            // Forward separately as this probably indicates a serialization issue.
            throw exception;
        } catch (Throwable throwable) {
            ExceptionPanel.showErrorDialog(desktop, throwable);
            return;
        }

        // Create the controllers.
        for (final PersistenceInfo persistenceInfo : persistenceInfoList) {
            if (persistenceInfo == null) {
                continue;
            }

            try {
                if (new File(persistenceInfo.getPath()).exists()) {
                    newCacheListController(
                            desktop,
                            menuWindows,
                            relativeLocation,
                            persistenceInfo.getPath(),
                            runLocationDialog);
                }
            } catch (Throwable throwable) {
                ExceptionPanel.showErrorDialog(desktop, throwable);
            }
        }
    }

    /**
     * Check if all cache list controllers are saved.
     *
     * @return Whether all cache list controllers are saved.
     */
    public static boolean areAllSaved() {
        for (final CacheListController cacheListController : controllerList) {
            if (!cacheListController.isSaved()) {
                return false;
            }
        }

        return true;
    }

    /** The corresponding cache list model. */
    private final CacheListModel cacheListModel = new CacheListModel();

    /** The corresponding cache list view. */
    private CacheListView view = null;

    /** The path to the GPX list file. */
    private Path path = null;

    /** The current controller. */
    private final CacheListController THIS = this;

    /** Status variable indicating whether the list is dirty (= modified and unsaved). */
    private Boolean modifiedAndUnsaved = null;

    /** The windows menu. */
    private JMenuItem menuWindow = null;

    @SuppressWarnings("unused")
    private CacheListController() {}

    /**
     * Create a new instance with the given values.
     *
     * @param desktop The pane to add the views to.
     * @param menuWindows The windows menu.
     * @param relativeLocation The relative location.
     * @param path The filepath to the cache list.
     * @param runLocationDialog The location dialog runner.
     */
    public CacheListController(
            final JDesktopPane desktop,
            final JMenu menuWindows,
            final Location relativeLocation,
            final String path,
            RunLocationDialogInterface runLocationDialog)
            throws Throwable {
        // Load the model.
        if (path != null) {
            cacheListModel.load(path);
        }

        setRelativeLocation(relativeLocation);

        // Set up the view.
        view = new CacheListView(this, runLocationDialog);
        view.setMinimumSize(new Dimension(100, 100));
        view.setClosable(true);
        view.setResizable(true);
        view.setVisible(true);

        if (path == null) {
            modifiedAndUnsaved = true;
        } else {
            modifiedAndUnsaved = false;
            this.path = Paths.get(path);
        }

        // Handle close events manually.
        view.setDefaultCloseOperation(JInternalFrame.DO_NOTHING_ON_CLOSE);
        view.addInternalFrameListener(
                new InternalFrameAdapter() {
                    @Override
                    public void internalFrameClosing(InternalFrameEvent internalFrameEvent) {
                        // Get the storage status.
                        final boolean saved = THIS.isSaved();

                        // The list is saved, so we can exit normally.
                        if (saved) {
                            CacheListController.remove(THIS);
                            menuWindows.remove(menuWindow);
                            internalFrameEvent.getInternalFrame().dispose();
                            return;
                        }

                        // The list might be unsaved. Let the user choose the next step.
                        final int option =
                                JOptionPane.showConfirmDialog(
                                        THIS.view,
                                        "You may have unsaved list changes.\n"
                                                + "Do you want to return to the list tab?",
                                        "Unsaved changes",
                                        JOptionPane.YES_NO_OPTION,
                                        JOptionPane.WARNING_MESSAGE);

                        // The user does not want to save the modified list. Close the tab.
                        if (option == JOptionPane.NO_OPTION) {
                            CacheListController.remove(THIS);
                            menuWindows.remove(menuWindow);
                            internalFrameEvent.getInternalFrame().dispose();
                        }

                        // The user wants to keep the tab open for now.
                    }
                });

        desktop.add(view);
        try {
            view.setMaximum(true);
        } catch (PropertyVetoException ignored) {
        }

        // Add the columns.
        final JTable table = view.getTable();
        setWidth(table, CacheListTableColumn.CACHE_NAME.getColumnIndex(), 150);
        setWidth(table, CacheListTableColumn.CACHE_TYPE.getColumnIndex(), 60);
        setWidth(table, CacheListTableColumn.DIFFICULTY_RATING.getColumnIndex(), 60);
        setWidth(table, CacheListTableColumn.TERRAIN_RATING.getColumnIndex(), 60);
        setWidth(table, CacheListTableColumn.CACHE_OWNER.getColumnIndex(), 150);

        this.menuWindow = new JMenuItem("");
        menuWindows.add(this.menuWindow);
        this.menuWindow.addActionListener(actionEvent -> desktop.moveToFront(view));

        updateOverallOptics();
    }

    /**
     * Update the cache list model from the given file.
     *
     * @param path The file to update the model with.
     * @throws Throwable Something went wrong when loading the data.
     */
    public void addFromFile(final String path) throws Throwable {
        cacheListModel.load(path);
        cachesAddedOrRemoved();
    }

    /**
     * Get the corresponding model.
     *
     * @return The corresponding model.
     */
    public CacheListModel getModel() {
        return cacheListModel;
    }

    /**
     * Get the corresponding view.
     *
     * @return The corresponding view.
     */
    public CacheListView getView() {
        return view;
    }

    /**
     * Set the relative location.
     *
     * @param relativeLocation The relative location to set.
     */
    public void setRelativeLocation(final Location relativeLocation) {
        cacheListModel.setRelativeLocation(relativeLocation);
    }

    /**
     * Get the name of the list file.
     *
     * @return The name of the list file.
     */
    private String getName() {
        if (path != null) {
            return path.getFileName().toString();
        }
        return null;
    }

    /** Update the list title and entry count. */
    private void updateTitleAndCount() {
        String title = getName();
        if (title == null) {
            title = "unnamed";
        }
        if (modifiedAndUnsaved) {
            title += "*";
        }
        view.setTitle(title);
        menuWindow.setText(title);

        int count = cacheListModel.size();
        view.setCacheCount(count);

        for (final Geocache geocache : cacheListModel.getList()) {
            count += geocache.getWaypoints().size();
        }
        view.setWaypointCount(count, cacheListModel.getOrphans().size());
    }

    /** Handle list item change events. */
    private void cachesAddedOrRemoved() {
        modifiedAndUnsaved = true;
        updateOverallOptics();
    }

    /** Update the GUI to represent possible item changes. */
    private void updateOverallOptics() {
        updateTitleAndCount();
        view.resetView();
        view.updateCachePanelToSelection();
        view.updateMapMarkers();
    }

    /**
     * Add the given filter.
     *
     * @param filter The filter to add.
     */
    public void addFilter(final FilterModel filter) {
        cacheListModel.addFilter(filter);
        view.addFilter(filter);
        updateOverallOptics();
    }

    /**
     * Remove the given filter.
     *
     * @param filter The filter to remove.
     */
    public void removeFilter(final FilterModel filter) {
        cacheListModel.removeFilter(filter);
        updateOverallOptics();
    }

    /** Indicate that the filters have been updated. */
    public void filtersUpdated() {
        updateOverallOptics();
    }

    /** Remove the caches from the model if they do not match the filter. */
    public void removeCachesNotInFilter() {
        cacheListModel.removeCachesNotInFilter();
        cachesAddedOrRemoved();
    }

    /**
     * Save the current list to the given GPX file.
     *
     * @param pathToGpx The file to save the current list to.
     * @throws Throwable Something went wrong with writing the data.
     */
    public void store(final Path pathToGpx) throws Throwable {
        this.path = pathToGpx;
        cacheListModel.store(getName(), pathToGpx.toString());

        modifiedAndUnsaved = false;
        updateTitleAndCount();
    }

    /**
     * Get the path to the list file.
     *
     * @return The path to the list file.
     */
    public Path getPath() {
        return path;
    }

    /**
     * Set the width of the specified table column.
     *
     * @param table The table to update.
     * @param columnIndex The index of the column to set the width for.
     * @param size The width to set.
     */
    public static void setWidth(final JTable table, final int columnIndex, final int size) {
        final TableColumn column = table.getColumnModel().getColumn(columnIndex);
        column.setPreferredWidth(size);
    }

    /** Remove the geocaches which are selected in the GUI at the moment. */
    public void removeSelectedCaches() {
        final List<Geocache> removeList = view.getSelectedCaches();
        if (removeList.size() > 0) {
            cacheListModel.removeCaches(removeList);
            cachesAddedOrRemoved();
        }
    }

    /** List for copying geocaches. */
    private static final ArrayList<Geocache> copyList = new ArrayList<>();

    /** Copy the currently selected caches. */
    public void copySelected() {
        final List<Geocache> selected = view.getSelectedCaches();
        copyList.clear();
        copyList.ensureCapacity(selected.size());
        for (final Geocache geocache : selected) {
            copyList.add(ObjectHelper.copy(geocache));
        }
    }

    /** Cut the currently selected caches. */
    public void cutSelected() {
        final List<Geocache> selected = view.getSelectedCaches();
        copyList.clear();
        copyList.ensureCapacity(selected.size());
        copyList.addAll(selected);
        if (!copyList.isEmpty()) {
            cacheListModel.removeCaches(copyList);
            cachesAddedOrRemoved();
        }
    }

    /** Paste the caches selected for copy in the previous step. */
    public void pasteSelected() {
        if (!copyList.isEmpty()) {
            cacheListModel.addCaches(copyList);
            cachesAddedOrRemoved();
        }
    }

    /**
     * Get the corresponding table model.
     *
     * @return The corresponding table model.
     */
    public CacheListTableModel getTableModel() {
        return cacheListModel.getTableModel();
    }

    /** Revert the list to the previous state. */
    public void replayLastUndoAction() {
        cacheListModel.replayLastUndoAction();
        modifiedAndUnsaved = true;
        cachesAddedOrRemoved();
    }

    /**
     * Get the number of undo actions.
     *
     * @return The number of undo actions.
     */
    public int getUndoActionCount() {
        return cacheListModel.getUndoActionCount();
    }

    /**
     * Get the persistence information.
     *
     * @return The persistence information.
     */
    private PersistenceInfo getPersistenceInfo() {
        if (path == null) {
            return null;
        }

        return new PersistenceInfo(path.toString());
    }

    /**
     * Check whether the list is saved.
     *
     * @return Whether the list is saved or not.
     */
    public boolean isSaved() {
        return modifiedAndUnsaved != null && !modifiedAndUnsaved;
    }
}
