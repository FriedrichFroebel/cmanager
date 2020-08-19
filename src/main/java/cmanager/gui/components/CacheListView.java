package cmanager.gui.components;

import cmanager.geo.Geocache;
import cmanager.geo.GeocacheType;
import cmanager.global.Compatibility;
import cmanager.global.Constants;
import cmanager.gui.interfaces.RunLocationDialogInterface;
import cmanager.list.CacheListController;
import cmanager.list.CacheListTableColumn;
import cmanager.list.CacheListTableModel;
import cmanager.list.filter.FilterModel;
import cmanager.osm.PersistentTileCache;
import cmanager.util.DesktopUtil;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.DefaultMapController;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.MapMarkerDot;
import org.openstreetmap.gui.jmapviewer.interfaces.MapMarker;

/** Frame for viewing a cache list. */
public class CacheListView extends JInternalFrame {

    private static final long serialVersionUID = -3610178481183679565L;

    /** The associated cache list controller containing the data. */
    private final CacheListController cacheListController;

    /** The data table. */
    private final JTable table;

    /** The associated cache panel. */
    private final CachePanel cachePanel;

    /** The number of caches inside this list. */
    private final JLabel labelCacheCount;

    /** The number of waypoints inside this list. */
    private final JLabel labelWaypointCount;

    /** The associated map viewer. */
    private final CustomJMapViewer mapViewer;

    /** The panel containing all the filters. */
    private final JPanel panelFilters;

    /** The last position where the user clicked to inside the table. */
    private Point popupPoint;

    /**
     * Create the frame.
     *
     * @param cacheListController The cache list to display.
     * @param runLocationDialog The location dialog runner.
     */
    public CacheListView(
            final CacheListController cacheListController,
            final RunLocationDialogInterface runLocationDialog) {
        this.cacheListController = cacheListController;

        // Handle close events manually.
        setDefaultCloseOperation(JInternalFrame.DO_NOTHING_ON_CLOSE);

        // Initialize the table model.
        final AbstractTableModel tableModel = cacheListController.getTableModel();
        table = new JTable(tableModel);
        table.setRowSelectionAllowed(true);
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        table.getSelectionModel()
                .addListSelectionListener(
                        listSelectionEvent -> {
                            updateCachePanelToSelection();
                            updateMapMarkers();
                        });
        table.setAutoCreateRowSorter(true);

        // Add the table columns.
        final DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        final CacheListTableColumn[] centeredColumns = {
            CacheListTableColumn.DIFFICULTY_RATING,
            CacheListTableColumn.TERRAIN_RATING,
            CacheListTableColumn.COORDINATE_LATITUDE,
            CacheListTableColumn.COORDINATE_LONGITUDE,
            CacheListTableColumn.DISTANCE,
            CacheListTableColumn.LATEST_FOUND_LOG
        };
        for (final CacheListTableColumn centeredColumn : centeredColumns) {
            table.getColumnModel()
                    .getColumn(centeredColumn.getColumnIndex())
                    .setCellRenderer(centerRenderer);
        }

        // Add the filter panel.
        panelFilters = new JPanel();
        panelFilters.setVisible(false);
        getContentPane().add(panelFilters, BorderLayout.NORTH);
        panelFilters.setLayout(new BoxLayout(panelFilters, BoxLayout.Y_AXIS));

        // Make the table scrollable.
        final JScrollPane scrollPane =
                new JScrollPane(
                        table,
                        JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setMinimumSize(new Dimension(300, 300));

        // The scrollable table is the first component of the split pane.
        final JSplitPane splitPane1 = new JSplitPane();
        getContentPane().add(splitPane1, BorderLayout.CENTER);
        splitPane1.setLeftComponent(scrollPane);

        final JSplitPane splitPane2 = new JSplitPane();
        splitPane1.setRightComponent(splitPane2);
        splitPane2.setVisible(false);

        // The cache panel is the second component of the split pane.
        cachePanel = new CachePanel();
        cachePanel.setVisible(false);
        splitPane2.setLeftComponent(cachePanel);

        // The map panel is the third component of the split pane.
        final JPanel panelMap = new JPanel();
        panelMap.setVisible(false);
        splitPane2.setRightComponent(panelMap);
        panelMap.setLayout(new BorderLayout(0, 0));

        // Configure the map.
        mapViewer =
                new CustomJMapViewer(new PersistentTileCache(Constants.CACHE_FOLDER + "maps.osm/"));
        mapViewer.setFocusable(true);
        panelMap.add(mapViewer, BorderLayout.CENTER);

        final JPanel panelMapHelp = new JPanel();
        panelMap.add(panelMapHelp, BorderLayout.SOUTH);

        final JLabel labelMapHelp =
                new JLabel("Drag map with right mouse, selection box with left mouse.");
        labelMapHelp.setFont(new Font("Dialog", Font.BOLD, 9));
        panelMapHelp.add(labelMapHelp);

        // Make map movable with mouse.
        final DefaultMapController mapController = new DefaultMapController(mapViewer);
        mapController.setMovementMouseButton(MouseEvent.BUTTON3);

        mapViewer.addMouseListener(
                new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent mouseEvent) {
                        super.mouseClicked(mouseEvent);

                        if (mouseEvent.getButton() == MouseEvent.BUTTON1) {
                            final Point point = mouseEvent.getPoint();

                            // Handle attribution clicks.
                            mapViewer.getAttribution().handleAttribution(point, true);

                            // Handle geocaches.
                            final Geocache geocache = getMapFocusedCache(point);
                            if (geocache == null) {
                                return;
                            }

                            if (mouseEvent.getClickCount() == 1
                                    && ((mouseEvent.getModifiersEx() & InputEvent.CTRL_DOWN_MASK)
                                            != 0)) {
                                DesktopUtil.openUrl(geocache.getUrl());
                            } else if (mouseEvent.getClickCount() == 1) {
                                cachePanel.setCache(geocache);
                            }
                        }
                    }
                });
        mapViewer.addMouseMotionListener(
                new MouseAdapter() {
                    @Override
                    public void mouseMoved(MouseEvent mouseEvent) {
                        final Point point = mouseEvent.getPoint();
                        final Geocache geocache = getMapFocusedCache(point);

                        String tip = null;
                        if (geocache != null) {
                            tip = geocache.getName();
                        }
                        mapViewer.setToolTipText(tip);
                    }
                });

        // Box selection.
        final MouseAdapter mouseAdapter =
                new MouseAdapter() {
                    private Point start = null;
                    private Point end = null;

                    public void mouseReleased(MouseEvent mouseEvent) {
                        if (end == null || start == null) {
                            return;
                        }

                        final List<Geocache> list =
                                getMapSelectedCaches(start, mouseEvent.getPoint());
                        table.clearSelection();
                        addToTableSelection(list);

                        start = null;
                        end = null;
                        mapViewer.setPoints(null, null);
                    }

                    public void mousePressed(MouseEvent mouseEvent) {
                        if (mouseEvent.getButton() == MouseEvent.BUTTON1) {
                            start = mouseEvent.getPoint();
                        } else {
                            start = null;
                        }
                    }

                    public void mouseDragged(MouseEvent mouseEvent) {
                        if (start == null) {
                            return;
                        }

                        end = mouseEvent.getPoint();
                        mapViewer.setPoints(start, end);
                    }
                };
        mapViewer.addMouseListener(mouseAdapter);
        mapViewer.addMouseMotionListener(mouseAdapter);

        // The bottom bar.
        final JPanel panelBar = new JPanel();
        getContentPane().add(panelBar, BorderLayout.SOUTH);
        panelBar.setLayout(new BorderLayout(0, 0));

        // This panel contains the geocache and waypoint counts.
        final JPanel panel = new JPanel();
        panelBar.add(panel, BorderLayout.EAST);
        panel.setLayout(new BorderLayout(0, 0));

        final JPanel panelCaches = new JPanel();
        panel.add(panelCaches, BorderLayout.NORTH);
        panelCaches.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));

        labelCacheCount = new JLabel("0");
        panelCaches.add(labelCacheCount);

        final JLabel labelCaches = new JLabel("Caches");
        panelCaches.add(labelCaches);

        final JPanel panelCounts = new JPanel();
        panel.add(panelCounts, BorderLayout.SOUTH);
        panelCounts.setLayout(new BorderLayout(10, 0));

        labelWaypointCount = new JLabel("0 Waypoints");
        labelWaypointCount.setHorizontalAlignment(SwingConstants.CENTER);
        labelWaypointCount.setFont(new Font("Dialog", Font.BOLD, 10));
        panelCounts.add(labelWaypointCount, BorderLayout.NORTH);

        final JPanel panelSelected = new JPanel();
        panelCounts.add(panelSelected, BorderLayout.SOUTH);
        panelSelected.setLayout(new BorderLayout(10, 0));
        panelSelected.setVisible(false);

        final JSeparator separatorSelected = new JSeparator();
        panelSelected.add(separatorSelected, BorderLayout.NORTH);

        final JPanel panelSelectedLabel = new JPanel();
        panelSelected.add(panelSelectedLabel, BorderLayout.SOUTH);
        panelSelectedLabel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

        final JLabel labelSelected = new JLabel("0");
        labelSelected.setFont(new Font("Dialog", Font.PLAIN, 10));
        panelSelectedLabel.add(labelSelected);

        final JLabel labelSelectedText = new JLabel("Selected");
        labelSelectedText.setFont(new Font("Dialog", Font.PLAIN, 10));
        panelSelectedLabel.add(labelSelectedText);

        table.getSelectionModel()
                .addListSelectionListener(
                        listSelectionEvent -> {
                            final int selected = table.getSelectedRowCount();
                            if (selected == 0) {
                                panelSelected.setVisible(false);
                            } else {
                                labelSelected.setText(Integer.valueOf(selected).toString());
                                panelSelected.setVisible(true);
                            }
                        });

        // Add the buttons for toggling the three main list views.
        final JPanel panelButtons = new JPanel();
        panelBar.add(panelButtons, BorderLayout.WEST);

        final JToggleButton toggleButtonList = new JToggleButton("List");
        toggleButtonList.addActionListener(
                actionEvent -> {
                    scrollPane.setVisible(toggleButtonList.isSelected());
                    fixSplitPanes(splitPane1, splitPane2);
                });
        toggleButtonList.setSelected(true);
        panelButtons.add(toggleButtonList);

        final JToggleButton toggleButtonMap = new JToggleButton("Map");
        toggleButtonMap.addActionListener(
                actionEvent -> {
                    panelMap.setVisible(toggleButtonMap.isSelected());
                    fixSplitPanes(splitPane1, splitPane2);

                    SwingUtilities.invokeLater(() -> getMapViewer().setDisplayToFitMapMarkers());
                });

        final JToggleButton toggleButtonCache = new JToggleButton("Cache");
        toggleButtonCache.addActionListener(
                actionEvent -> {
                    cachePanel.setVisible(toggleButtonCache.isSelected());
                    fixSplitPanes(splitPane1, splitPane2);
                });
        panelButtons.add(toggleButtonCache);
        panelButtons.add(toggleButtonMap);

        // Handle table row clicks and add a menu to add the current geocache as a new location.
        table.addMouseListener(
                new MouseAdapter() {
                    public void mouseReleased(MouseEvent mouseEvent) {
                        popupPoint = mouseEvent.getPoint();
                    }
                });

        final JPopupMenu popupMenu = new JPopupMenu();
        table.setComponentPopupMenu(popupMenu);

        final JMenuItem menuLocationDialog = new JMenuItem("Add as Location");
        menuLocationDialog.addActionListener(
                actionEvent -> {
                    final int row = table.rowAtPoint(popupPoint);
                    final CacheListTableModel model = (CacheListTableModel) table.getModel();
                    final Geocache geocache = model.getObject(table.convertRowIndexToModel(row));
                    runLocationDialog.openDialog(geocache);
                });
        popupMenu.add(menuLocationDialog);

        // Remove shortcut keys.
        table.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .getParent()
                .remove(KeyStroke.getKeyStroke('C', Compatibility.SHORTCUT_KEY_MASK));
        table.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .getParent()
                .remove(KeyStroke.getKeyStroke('V', Compatibility.SHORTCUT_KEY_MASK));
        table.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .getParent()
                .remove(KeyStroke.getKeyStroke('X', Compatibility.SHORTCUT_KEY_MASK));
        table.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .getParent()
                .remove(KeyStroke.getKeyStroke('A', Compatibility.SHORTCUT_KEY_MASK));
        table.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .getParent()
                .remove(KeyStroke.getKeyStroke('I', Compatibility.SHORTCUT_KEY_MASK));
        table.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .getParent()
                .remove(KeyStroke.getKeyStroke('Z', Compatibility.SHORTCUT_KEY_MASK));
    }

    /** Show the selected geocache inside the cache panel. */
    public void updateCachePanelToSelection() {
        final CacheListTableModel model = (CacheListTableModel) table.getModel();
        if (table.getSelectedRows().length == 1) {
            // Show the selected geocache.
            final Geocache geocache =
                    model.getObject(table.convertRowIndexToModel(table.getSelectedRow()));
            cachePanel.setCache(geocache);
        }
        if (table.getSelectedRows().length == 0) {
            cachePanel.setCache(null);
        }
    }

    /** Whether to update map markers. */
    private boolean doNotUpdateMarkers = false;

    /**
     * Update map markers according to the current list selection.
     *
     * <p>This will be skipped if <code>doNotUpdateMarkers</code> is set to <code>True</code>.
     */
    public void updateMapMarkers() {
        if (doNotUpdateMarkers) {
            return;
        }

        mapViewer.removeAllMapMarkers();

        final CacheListTableModel tableModel = (CacheListTableModel) table.getModel();
        if (table.getSelectedRows().length > 0) {
            // Add markers for all selected geocaches.
            for (final int selection : table.getSelectedRows()) {
                final Geocache geocache =
                        tableModel.getObject(table.convertRowIndexToModel(selection));
                addMapMarker(geocache);
            }
        } else {
            for (final Geocache geocache : cacheListController.getModel().getList()) {
                addMapMarker(geocache);
            }
        }

        mapViewer.setDisplayToFitMapMarkers();
    }

    /**
     * Add the given map marker.
     *
     * @param geocache The geocache to add a marker for.
     */
    private void addMapMarker(final Geocache geocache) {
        final MapMarkerDot mapMarkerDot = new MapMarkerCache(geocache);
        mapViewer.addMapMarker(mapMarkerDot);
    }

    /** Map marker for a geocache. */
    // TODO: Move to own class.
    private static class MapMarkerCache extends MapMarkerDot {

        /** The geocache instance for this marker. */
        private final Geocache geocache;

        /**
         * Create a new marker for the given geocache.
         *
         * @param geocache The geocache to create the marker for.
         */
        public MapMarkerCache(final Geocache geocache) {
            super(
                    new Coordinate(
                            geocache.getCoordinate().getLatitude(),
                            geocache.getCoordinate().getLongitude()));
            this.geocache = geocache;

            setName("");

            if (geocache.getType().equals(GeocacheType.getTradiType())) {
                setColor(new Color(0x009900));
            } else if (geocache.getType().equals(GeocacheType.getMultiType())) {
                setColor(new Color(0xFFCC00));
            } else if (geocache.getType().equals(GeocacheType.getMysteryType())) {
                setColor(new Color(0x0066FF));
            } else {
                setColor(Color.GRAY);
            }
        }

        /**
         * Set the given color for the marker.
         *
         * @param color The color to set.
         */
        public void setColor(final Color color) {
            super.setColor(Color.BLACK);
            super.setBackColor(color);
        }

        /**
         * Get the geocache instance for this marker.
         *
         * @return The associated geocache instance.
         */
        public Geocache getCache() {
            return geocache;
        }
    }

    /**
     * Get the geocaches which are currently selected on the map.
     *
     * @param point1 The start position.
     * @param point2 The end position.
     * @return The selected geocaches.
     */
    private List<Geocache> getMapSelectedCaches(final Point point1, final Point point2) {
        final List<Geocache> list = new ArrayList<>();
        if (point1 == null || point2 == null) {
            return list;
        }

        final int x1 = Math.min(point1.x, point2.x);
        final int x2 = Math.max(point1.x, point2.x);
        final int y1 = Math.min(point1.y, point2.y);
        final int y2 = Math.max(point1.y, point2.y);

        for (final MapMarker mapMarker : mapViewer.getMapMarkerList()) {
            final MapMarkerCache mapMarkerCache = (MapMarkerCache) mapMarker;
            final Point markerPosition =
                    mapViewer.getMapPosition(mapMarker.getLat(), mapMarker.getLon());

            if (markerPosition != null
                    && markerPosition.x >= x1
                    && markerPosition.x <= x2
                    && markerPosition.y >= y1
                    && markerPosition.y <= y2) {
                list.add(mapMarkerCache.getCache());
            }
        }
        return list;
    }

    /**
     * Add the given geocache to the current table selection.
     *
     * @param geocache The geocache to add.
     */
    public void addToTableSelection(final Geocache geocache) {
        final List<Geocache> list = new ArrayList<>();
        list.add(geocache);
        addToTableSelection(list);
    }

    /**
     * Add the given geocaches to the current table selection.
     *
     * @param listIn The geocaches to add.
     */
    public void addToTableSelection(final List<Geocache> listIn) {
        doNotUpdateMarkers = true;

        final LinkedList<Geocache> list = new LinkedList<>(listIn);

        final CacheListTableModel tableModel = (CacheListTableModel) table.getModel();
        for (int i = 0; !listIn.isEmpty() && i < table.getRowCount(); i++) {
            final Geocache geocacheTable = tableModel.getObject(table.convertRowIndexToModel(i));

            Iterator<Geocache> iterator = list.iterator();
            while (iterator.hasNext()) {
                final Geocache geocache = iterator.next();
                if (geocacheTable == geocache) {
                    // Ass this is rather slow, we disable marker updates beforehand.
                    table.addRowSelectionInterval(i, i);
                    iterator.remove();
                    break;
                }
            }
        }

        // Enable marker updates again and perform one as we have changed data.
        doNotUpdateMarkers = false;
        updateMapMarkers();
    }

    /**
     * Add the given interval to the current table row selection.
     *
     * @param intervalStart The index of the row to start at.
     * @param intervalEnd The index of the row to end with.
     */
    private void addRowSelectionInterval(final int intervalStart, final int intervalEnd) {
        if (intervalStart > intervalEnd) {
            return;
        }

        table.addRowSelectionInterval(intervalStart, intervalEnd);
    }

    /** Invert the current table selection. */
    public void invertTableSelection() {
        doNotUpdateMarkers = true;

        if (table.getSelectedRowCount() == 0) {
            table.selectAll();
        } else {
            final int[] selection = table.getSelectedRows();
            table.clearSelection();

            // Preceding rows.
            addRowSelectionInterval(0, selection[0] - 1);

            for (int i = 0; i < selection.length - 1; i++) {
                addRowSelectionInterval(selection[i] + 1, selection[i + 1] - 1);
            }

            // Proceeding rows.
            addRowSelectionInterval(selection[selection.length - 1] + 1, table.getRowCount() - 1);
        }

        doNotUpdateMarkers = false;
        updateMapMarkers();
    }

    /**
     * Get the geocache which is currently focused on the map.
     *
     * @param point The current focus position.
     * @return The corresponding geocache.
     */
    private Geocache getMapFocusedCache(final Point point) {
        final int x = point.x + 3;
        final int y = point.y + 3;
        final List<MapMarker> mapMarkers = mapViewer.getMapMarkerList();

        for (final MapMarker marker : mapMarkers) {
            final MapMarkerCache mapMarkerCache = (MapMarkerCache) marker;

            final Point MarkerPosition =
                    mapViewer.getMapPosition(mapMarkerCache.getLat(), mapMarkerCache.getLon());
            if (MarkerPosition != null) {
                final int centerX = MarkerPosition.x;
                final int centerY = MarkerPosition.y;

                // Calculate the radius from the touch to the center of the dot.
                final double circleRadius =
                        Math.sqrt(
                                (((centerX - x) * (centerX - x)) + (centerY - y) * (centerY - y)));

                if (circleRadius < 10) {
                    return mapMarkerCache.getCache();
                }
            }
        }

        return null;
    }

    /**
     * Get the currently selected geocaches.
     *
     * @return The currently selected geocaches.
     */
    public List<Geocache> getSelectedCaches() {
        final CacheListTableModel model = (CacheListTableModel) table.getModel();
        final List<Geocache> selected = new ArrayList<>();
        for (final int row : table.getSelectedRows()) {
            final Geocache geocache = model.getObject(table.convertRowIndexToModel(row));
            selected.add(geocache);
        }
        return selected;
    }

    /** Notify the table about data changes and hide the cache panel. */
    public void resetView() {
        updateTableView();
        cachePanel.setCache(null);
    }

    /** Notify the table about data changes. */
    public void updateTableView() {
        ((AbstractTableModel) table.getModel()).fireTableDataChanged();
    }

    /**
     * Fix the split panes.
     *
     * @param pane1 The first pane.
     * @param pane2 The second pane.
     */
    public static void fixSplitPanes(final JSplitPane pane1, final JSplitPane pane2) {
        if (fixSplitPane(pane2, 0.5)) {
            fixSplitPane(pane1, 0.3);
        } else {
            fixSplitPane(pane1, 0.5);
        }
    }

    /**
     * Fix the split panes.
     *
     * <p>If both of the components of the pane are visible, the given divider location will be set.
     * Otherwise a divider size is set to 0.
     *
     * @param pane The pane to fix.
     * @param dividerLocation The divider location to set.
     * @return Whether both of the components of the pane are visible.
     */
    public static boolean fixSplitPane(final JSplitPane pane, final double dividerLocation) {
        boolean returnValue;
        pane.setVisible(
                pane.getLeftComponent().isVisible() || pane.getRightComponent().isVisible());
        if (pane.getLeftComponent().isVisible() && pane.getRightComponent().isVisible()) {
            pane.setDividerSize(new JSplitPane().getDividerSize());
            pane.setDividerLocation(dividerLocation);
            returnValue = true;
        } else {
            pane.setDividerSize(0);
            returnValue = false;
        }

        pane.revalidate();
        pane.repaint();
        return returnValue;
    }

    /**
     * Update the number of geocaches.
     *
     * @param count The value to set.
     */
    public void setCacheCount(final Integer count) {
        labelCacheCount.setText(count.toString());
    }

    /**
     * Update the number of waypoints.
     *
     * @param count The number of waypoints to set.
     * @param orphans The number of orphaned waypoints to set.
     */
    public void setWaypointCount(final Integer count, final Integer orphans) {
        String text = count.toString() + " Waypoints";
        if (orphans > 0) {
            text = text + " (" + orphans.toString() + " Orphans)";
        }
        labelWaypointCount.setText(text);
    }

    /**
     * Add the given filter.
     *
     * @param filter The filter to add.
     */
    public void addFilter(final FilterModel filter) {
        filter.addRemoveAction(() -> cacheListController.removeFilter(filter));
        filter.addRunOnFilterUpdate(cacheListController::filtersUpdated);

        panelFilters.add(filter);
        panelFilters.setVisible(true);
        panelFilters.revalidate();
    }

    /** Clear the table selection if all rows are selected, select all rows otherwise. */
    public void tableSelectAllNone() {
        if (table.getSelectedRowCount() == table.getRowCount()) {
            table.clearSelection();
        } else {
            table.selectAll();
        }
    }

    /**
     * Get the table instance.
     *
     * @return The table.
     */
    public JTable getTable() {
        return table;
    }

    /**
     * Get the cache count label.
     *
     * @return The cache count label.
     */
    public JLabel getLabelCacheCount() {
        return labelCacheCount;
    }

    /**
     * Get the map viewer.
     *
     * @return The map viewer.
     */
    public JMapViewer getMapViewer() {
        return mapViewer;
    }
}
