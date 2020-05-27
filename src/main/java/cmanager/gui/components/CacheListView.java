package cmanager.gui.components;

import cmanager.geo.Geocache;
import cmanager.geo.GeocacheType;
import cmanager.global.Compatibility;
import cmanager.global.Constants;
import cmanager.gui.interfaces.RunLocationDialogI;
import cmanager.list.CacheListController;
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
import org.openstreetmap.gui.jmapviewer.DefaultMapController;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.MapMarkerDot;
import org.openstreetmap.gui.jmapviewer.interfaces.MapMarker;

public class CacheListView extends JInternalFrame {

    private static final long serialVersionUID = -3610178481183679565L;

    private final CacheListController cacheListController;
    private final JTable table;
    private final CachePanel cachePanel;
    private final JLabel labelCacheCount;
    private final JLabel lblWaypointsCount;
    private final CustomJMapViewer mapViewer;
    private final JPanel panelFilters;

    private Point popupPoint;

    /** Create the frame. */
    public CacheListView(
            final CacheListController cacheListController,
            final RunLocationDialogI runLocationDialog) {
        this.cacheListController = cacheListController;

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

        final DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(8).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(9).setCellRenderer(centerRenderer);

        panelFilters = new JPanel();
        panelFilters.setVisible(false);
        getContentPane().add(panelFilters, BorderLayout.NORTH);
        panelFilters.setLayout(new BoxLayout(panelFilters, BoxLayout.Y_AXIS));

        final JScrollPane scrollPane =
                new JScrollPane(
                        table,
                        JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setMinimumSize(new Dimension(300, 300));

        final JSplitPane splitPane1 = new JSplitPane();
        getContentPane().add(splitPane1, BorderLayout.CENTER);
        splitPane1.setLeftComponent(scrollPane);

        final JSplitPane splitPane2 = new JSplitPane();
        splitPane1.setRightComponent(splitPane2);
        splitPane2.setVisible(false);

        cachePanel = new CachePanel();
        cachePanel.setVisible(false);
        splitPane2.setLeftComponent(cachePanel);

        final JPanel panelMap = new JPanel();
        panelMap.setVisible(false);
        splitPane2.setRightComponent(panelMap);
        panelMap.setLayout(new BorderLayout(0, 0));

        mapViewer =
                new CustomJMapViewer(new PersistentTileCache(Constants.CACHE_FOLDER + "maps.osm/"));
        mapViewer.setFocusable(true);
        panelMap.add(mapViewer, BorderLayout.CENTER);

        final JPanel panel2 = new JPanel();
        panelMap.add(panel2, BorderLayout.SOUTH);

        final JLabel labelMapHelp =
                new JLabel("Drag map with right mouse, selection box with left mouse.");
        labelMapHelp.setFont(new Font("Dialog", Font.BOLD, 9));
        panel2.add(labelMapHelp);

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

        final JPanel panelBar = new JPanel();
        getContentPane().add(panelBar, BorderLayout.SOUTH);
        panelBar.setLayout(new BorderLayout(0, 0));

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

        final JPanel panel1 = new JPanel();
        panel.add(panel1, BorderLayout.SOUTH);
        panel1.setLayout(new BorderLayout(10, 0));

        lblWaypointsCount = new JLabel("0 Waypoints");
        lblWaypointsCount.setHorizontalAlignment(SwingConstants.CENTER);
        lblWaypointsCount.setFont(new Font("Dialog", Font.BOLD, 10));
        panel1.add(lblWaypointsCount, BorderLayout.NORTH);

        final JPanel panelSelected = new JPanel();
        panel1.add(panelSelected, BorderLayout.SOUTH);
        panelSelected.setLayout(new BorderLayout(10, 0));
        panelSelected.setVisible(false);

        final JSeparator separator = new JSeparator();
        panelSelected.add(separator, BorderLayout.NORTH);

        final JPanel panel4 = new JPanel();
        panelSelected.add(panel4, BorderLayout.SOUTH);
        panel4.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

        final JLabel labelSelected = new JLabel("0");
        labelSelected.setFont(new Font("Dialog", Font.PLAIN, 10));
        panel4.add(labelSelected);

        final JLabel label1 = new JLabel("Selected");
        label1.setFont(new Font("Dialog", Font.PLAIN, 10));
        panel4.add(label1);

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

    public void updateCachePanelToSelection() {
        final CacheListTableModel model = (CacheListTableModel) table.getModel();
        if (table.getSelectedRows().length == 1) {
            final Geocache geocache =
                    model.getObject(table.convertRowIndexToModel(table.getSelectedRow()));
            cachePanel.setCache(geocache);
        }
        if (table.getSelectedRows().length == 0) {
            cachePanel.setCache(null);
        }
    }

    private boolean doNotUpdateMakers = false;

    public void updateMapMarkers() {
        if (doNotUpdateMakers) {
            return;
        }

        mapViewer.removeAllMapMarkers();

        final CacheListTableModel tableModel = (CacheListTableModel) table.getModel();
        if (table.getSelectedRows().length > 0) {
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

    private void addMapMarker(Geocache geocache) {
        final MapMarkerDot mapMarkerDot = new MapMarkerCache(geocache);
        mapViewer.addMapMarker(mapMarkerDot);
    }

    private static class MapMarkerCache extends MapMarkerDot {

        private final Geocache geocache;

        public MapMarkerCache(Geocache geocache) {
            super(
                    new org.openstreetmap.gui.jmapviewer.Coordinate(
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

        public void setColor(Color color) {
            super.setColor(Color.BLACK);
            super.setBackColor(color);
        }

        public Geocache getCache() {
            return geocache;
        }
    }

    private List<Geocache> getMapSelectedCaches(Point point1, Point point2) {
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

    public void addToTableSelection(Geocache geocache) {
        final List<Geocache> list = new ArrayList<>();
        list.add(geocache);
        addToTableSelection(list);
    }

    public void addToTableSelection(final List<Geocache> listIn) {
        doNotUpdateMakers = true;

        final LinkedList<Geocache> list = new LinkedList<>(listIn);

        final CacheListTableModel tableModel = (CacheListTableModel) table.getModel();
        for (int i = 0; !listIn.isEmpty() && i < table.getRowCount(); i++) {
            final Geocache geocacheTable = tableModel.getObject(table.convertRowIndexToModel(i));

            Iterator<Geocache> iterator = list.iterator();
            while (iterator.hasNext()) {
                final Geocache geocache = iterator.next();
                if (geocacheTable == geocache) {
                    table.addRowSelectionInterval(i, i); // slow -> disableUpdateMakers
                    iterator.remove();
                    break;
                }
            }
        }

        doNotUpdateMakers = false;
        updateMapMarkers();
    }

    private void addRowSelectionInterval(int intervalStart, int intervalEnd) {
        if (intervalStart > intervalEnd) {
            return;
        }

        table.addRowSelectionInterval(intervalStart, intervalEnd);
    }

    public void invertTableSelection() {
        doNotUpdateMakers = true;

        if (table.getSelectedRowCount() == 0) {
            table.selectAll();
        } else {
            final int[] selection = table.getSelectedRows();
            table.clearSelection();

            addRowSelectionInterval(0, selection[0] - 1); // Preceding rows.
            for (int i = 0; i < selection.length - 1; i++) {
                addRowSelectionInterval(selection[i] + 1, selection[i + 1] - 1);
            }
            // Proceeding rows.
            addRowSelectionInterval(selection[selection.length - 1] + 1, table.getRowCount() - 1);
        }

        doNotUpdateMakers = false;
        updateMapMarkers();
    }

    private Geocache getMapFocusedCache(Point point) {
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

    public List<Geocache> getSelectedCaches() {
        final CacheListTableModel model = (CacheListTableModel) table.getModel();
        final List<Geocache> selected = new ArrayList<>();
        for (final int row : table.getSelectedRows()) {
            final Geocache geocache = model.getObject(table.convertRowIndexToModel(row));
            selected.add(geocache);
        }
        return selected;
    }

    public void resetView() {
        updateTableView();
        cachePanel.setCache(null);
    }

    public void updateTableView() {
        ((AbstractTableModel) table.getModel()).fireTableDataChanged();
    }

    public static void fixSplitPanes(JSplitPane pane1, JSplitPane pane2) {
        if (fixSplitPane(pane2, 0.5)) {
            fixSplitPane(pane1, 0.3);
        } else {
            fixSplitPane(pane1, 0.5);
        }
    }

    public static boolean fixSplitPane(JSplitPane pane, double dividerLocation) {
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

    public void setCacheCount(Integer count) {
        labelCacheCount.setText(count.toString());
    }

    public void setWaypointCount(Integer count, Integer orphans) {
        String text = count.toString() + " Waypoints";
        if (orphans > 0) {
            text = text + " (" + orphans.toString() + " Orphans)";
        }
        lblWaypointsCount.setText(text);
    }

    public void addFilter(final FilterModel filter) {
        filter.addRemoveAction(() -> cacheListController.removeFilter(filter));
        filter.addRunOnFilterUpdate(cacheListController::filtersUpdated);

        panelFilters.add(filter);
        panelFilters.setVisible(true);
        panelFilters.revalidate();
    }

    public void tableSelectAllNone() {
        if (table.getSelectedRowCount() == table.getRowCount()) {
            table.clearSelection();
        } else {
            table.selectAll();
        }
    }

    public JTable getTable() {
        return table;
    }

    public JLabel getLabelCacheCount() {
        return labelCacheCount;
    }

    public JMapViewer getMapViewer() {
        return mapViewer;
    }
}
