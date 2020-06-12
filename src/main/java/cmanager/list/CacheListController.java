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

public class CacheListController {

    private static final List<CacheListController> controllerList = new ArrayList<>();

    public static CacheListController newCacheListController(
            JDesktopPane desktop,
            JMenu menuWindows,
            Location relativeLocation,
            String path,
            RunLocationDialogInterface runLocationDialog)
            throws Throwable {
        final CacheListController cacheListController =
                new CacheListController(
                        desktop, menuWindows, relativeLocation, path, runLocationDialog);
        controllerList.add(cacheListController);
        return cacheListController;
    }

    public static void remove(CacheListController cacheListController) {
        controllerList.remove(cacheListController);
    }

    private static CacheListController getCacheListController(JInternalFrame internalFrame) {
        for (final CacheListController cacheListController : controllerList) {
            if (cacheListController.view == internalFrame) {
                return cacheListController;
            }
        }
        return null;
    }

    public static CacheListController getTopViewCacheController(JDesktopPane desktop) {
        if (desktop.getAllFrames().length == 0) {
            return null;
        }

        final JInternalFrame jInternalFrame = desktop.getAllFrames()[0];
        return CacheListController.getCacheListController(jInternalFrame);
    }

    public static CacheListView getTopView(JDesktopPane desktop) {
        return (CacheListView) desktop.getAllFrames()[0];
    }

    public static void setAllRelativeLocations(Location relativeLocation) {
        for (final CacheListController cacheListController : controllerList) {
            cacheListController.setRelativeLocation(relativeLocation);
        }
    }

    public static void storePersistenceInfo(JDesktopPane desktop) throws IOException {
        final CacheListController top = getTopViewCacheController(desktop);

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

    public static void reopenPersistentCacheListControllers(
            JDesktopPane desktop,
            JMenu menuWindows,
            Location relativeLocation,
            RunLocationDialogInterface runLocationDialog) {
        List<PersistenceInfo> persistenceInfoList;
        try {
            persistenceInfoList = Settings.getSerialized(Settings.Key.CLC_LIST);
            if (persistenceInfoList == null) {
                return;
            }
        } catch (Throwable throwable) {
            ExceptionPanel.showErrorDialog(desktop, throwable);
            return;
        }

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

    public static boolean areAllSaved() {
        for (final CacheListController cacheListController : controllerList) {
            if (!cacheListController.isSaved()) {
                return false;
            }
        }

        return true;
    }

    private final CacheListModel cacheListModel = new CacheListModel();
    private CacheListView view = null;
    private Path path = null;
    private final CacheListController THIS = this;
    private Boolean modifiedAndUnsaved = null;
    private JMenuItem menuWindow = null;

    @SuppressWarnings("unused")
    private CacheListController() {}

    public CacheListController(
            final JDesktopPane desktop,
            final JMenu menuWindows,
            Location relativeLocation,
            String path,
            RunLocationDialogInterface runLocationDialog)
            throws Throwable {
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
                                        "You may have unsaved list changes.\nDo you want to return to the list tab?",
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

    public void addFromFile(String path) throws Throwable {
        cacheListModel.load(path);
        cachesAddedOrRemoved();
    }

    public CacheListModel getModel() {
        return cacheListModel;
    }

    public CacheListView getView() {
        return view;
    }

    public void setRelativeLocation(Location relativeLocation) {
        cacheListModel.setRelativeLocation(relativeLocation);
    }

    private String getName() {
        if (path != null) {
            return path.getFileName().toString();
        }
        return null;
    }

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

    private void cachesAddedOrRemoved() {
        modifiedAndUnsaved = true;
        updateOverallOptics();
    }

    private void updateOverallOptics() {
        updateTitleAndCount();
        view.resetView();
        view.updateCachePanelToSelection();
        view.updateMapMarkers();
    }

    public void addFilter(FilterModel filter) {
        cacheListModel.addFilter(filter);
        view.addFilter(filter);
        updateOverallOptics();
    }

    public void removeFilter(FilterModel filter) {
        cacheListModel.removeFilter(filter);
        updateOverallOptics();
    }

    public void filtersUpdated() {
        updateOverallOptics();
    }

    public void removeCachesNotInFilter() {
        cacheListModel.removeCachesNotInFilter();
        cachesAddedOrRemoved();
    }

    public void store(Path pathToGpx) throws Throwable {
        this.path = pathToGpx;
        cacheListModel.store(getName(), pathToGpx.toString());

        modifiedAndUnsaved = false;
        updateTitleAndCount();
    }

    public Path getPath() {
        return path;
    }

    public static void setWidth(JTable table, int index, int size) {
        final TableColumn column = table.getColumnModel().getColumn(index);
        column.setPreferredWidth(size);
    }

    public void removeSelectedCaches() {
        final List<Geocache> removeList = view.getSelectedCaches();
        if (removeList.size() > 0) {
            cacheListModel.removeCaches(removeList);
            cachesAddedOrRemoved();
        }
    }

    private static final ArrayList<Geocache> copyList = new ArrayList<>();

    public void copySelected() {
        final List<Geocache> selected = view.getSelectedCaches();
        copyList.clear();
        copyList.ensureCapacity(selected.size());
        for (final Geocache geocache : selected) {
            copyList.add(ObjectHelper.copy(geocache));
        }
    }

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

    public void pasteSelected() {
        if (!copyList.isEmpty()) {
            cacheListModel.addCaches(copyList);
            cachesAddedOrRemoved();
        }
    }

    public CacheListTableModel getTableModel() {
        return cacheListModel.getTableModel();
    }

    public void replayLastUndoAction() {
        cacheListModel.replayLastUndoAction();
        modifiedAndUnsaved = true;
        cachesAddedOrRemoved();
    }

    public int getUndoActionCount() {
        return cacheListModel.getUndoActionCount();
    }

    private PersistenceInfo getPersistenceInfo() {
        if (path == null) {
            return null;
        }

        return new PersistenceInfo(path.toString());
    }

    public boolean isSaved() {
        return modifiedAndUnsaved != null && !modifiedAndUnsaved;
    }
}
