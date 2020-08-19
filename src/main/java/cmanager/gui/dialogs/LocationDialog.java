package cmanager.gui.dialogs;

import cmanager.geo.Coordinate;
import cmanager.geo.Geocache;
import cmanager.geo.Location;
import cmanager.geo.LocationList;
import cmanager.gui.ExceptionPanel;
import cmanager.okapi.Okapi;
import cmanager.okapi.User;
import cmanager.util.ThreadStore;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

/** Dialog to handle the configured locations. */
public class LocationDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    /** The list of locations. */
    private final JTable table;

    /** The name of the selected location. */
    private final JTextField textName;

    /** The latitude of the selected location. */
    private final JTextField textLatitude;

    /** The longitude of the selected location. */
    private final JTextField textLongitude;

    /** Whether some of the values have been modified. */
    public boolean modified = false;

    /** The current instance. */
    private final LocationDialog THIS = this;

    /**
     * Create the dialog.
     *
     * @param owner The parent frame.
     */
    public LocationDialog(final JFrame owner) {
        super(owner);

        setTitle("Locations");
        getContentPane().setLayout(new BorderLayout());

        // Create the table model.
        final String[] columnNames = {"Name", "Lat", "Lon"};
        final String[][] dataValues = {};
        final DefaultTableModel tableModel = new DefaultTableModel(dataValues, columnNames);

        // Load the table data.
        try {
            final List<Location> locations = LocationList.getList().getLocations();
            for (final Location location : locations) {
                final String[] values = {
                    location.getName(),
                    location.getLatitude().toString(),
                    location.getLongitude().toString()
                };
                tableModel.addRow(values);
            }
        } catch (Exception exception) {
            ExceptionPanel.showErrorDialog(this, exception);
        }

        // The main panel.
        final JPanel panelMaster = new JPanel();
        panelMaster.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(panelMaster, BorderLayout.CENTER);
        panelMaster.setLayout(new BorderLayout(0, 0));

        // The panel for outer buttons (bottom line).
        final JPanel buttonPaneOuter = new JPanel();
        panelMaster.add(buttonPaneOuter, BorderLayout.SOUTH);
        buttonPaneOuter.setBorder(null);
        buttonPaneOuter.setLayout(new BorderLayout(0, 0));

        final JPanel buttonPaneOkCancel = new JPanel();
        buttonPaneOkCancel.setBorder(null);
        buttonPaneOuter.add(buttonPaneOkCancel, BorderLayout.SOUTH);
        buttonPaneOkCancel.setLayout(new FlowLayout(FlowLayout.RIGHT));

        final JButton buttonOk = new JButton("OK");
        buttonOk.addActionListener(actionEvent -> applyChanges());
        buttonPaneOkCancel.add(buttonOk);

        final JButton buttonCancel = new JButton("Cancel");
        buttonCancel.addActionListener(actionEvent -> dispose());
        buttonPaneOkCancel.add(buttonCancel);

        // The panel for the edit buttons.
        final JPanel buttonPanelEdit = new JPanel();
        buttonPanelEdit.setBorder(new LineBorder(new Color(0, 0, 0)));
        buttonPaneOuter.add(buttonPanelEdit, BorderLayout.NORTH);
        buttonPanelEdit.setLayout(new BorderLayout(0, 0));

        final JPanel panelText = new JPanel();
        buttonPanelEdit.add(panelText, BorderLayout.NORTH);
        final GridBagLayout gblPanelText = new GridBagLayout();
        gblPanelText.columnWidths = new int[] {215, 215, 0};
        gblPanelText.rowHeights = new int[] {19, 19, 19, 0};
        gblPanelText.columnWeights = new double[] {0.0, 0.0, Double.MIN_VALUE};
        gblPanelText.rowWeights = new double[] {0.0, 0.0, 0.0, Double.MIN_VALUE};
        panelText.setLayout(gblPanelText);

        // The name for the location.
        final JLabel labelName = new JLabel("Name:");
        final GridBagConstraints gbcLabelName = new GridBagConstraints();
        gbcLabelName.fill = GridBagConstraints.BOTH;
        gbcLabelName.insets = new Insets(0, 0, 5, 5);
        gbcLabelName.gridx = 0;
        gbcLabelName.gridy = 0;
        panelText.add(labelName, gbcLabelName);

        textName = new JTextField();
        final GridBagConstraints gbcTextName = new GridBagConstraints();
        gbcTextName.fill = GridBagConstraints.BOTH;
        gbcTextName.insets = new Insets(0, 0, 5, 0);
        gbcTextName.gridx = 1;
        gbcTextName.gridy = 0;
        panelText.add(textName, gbcTextName);
        textName.setColumns(10);

        // The latitude for the location.
        final JLabel labelLatitude = new JLabel("Lat:");
        final GridBagConstraints gbcLabelLatitude = new GridBagConstraints();
        gbcLabelLatitude.fill = GridBagConstraints.BOTH;
        gbcLabelLatitude.insets = new Insets(0, 0, 5, 5);
        gbcLabelLatitude.gridx = 0;
        gbcLabelLatitude.gridy = 1;
        panelText.add(labelLatitude, gbcLabelLatitude);

        textLatitude = new JTextField();
        final GridBagConstraints gbcTextLatitude = new GridBagConstraints();
        gbcTextLatitude.fill = GridBagConstraints.BOTH;
        gbcTextLatitude.insets = new Insets(0, 0, 5, 0);
        gbcTextLatitude.gridx = 1;
        gbcTextLatitude.gridy = 1;
        panelText.add(textLatitude, gbcTextLatitude);
        textLatitude.setColumns(10);

        // The longitude for the location.
        final JLabel labelLongitude = new JLabel("Lon:");
        final GridBagConstraints gbcLabelLongitude = new GridBagConstraints();
        gbcLabelLongitude.fill = GridBagConstraints.BOTH;
        gbcLabelLongitude.insets = new Insets(0, 0, 0, 5);
        gbcLabelLongitude.gridx = 0;
        gbcLabelLongitude.gridy = 2;
        panelText.add(labelLongitude, gbcLabelLongitude);

        textLongitude = new JTextField();
        final GridBagConstraints gbcTextLongitude = new GridBagConstraints();
        gbcTextLongitude.fill = GridBagConstraints.BOTH;
        gbcTextLongitude.gridx = 1;
        gbcTextLongitude.gridy = 2;
        panelText.add(textLongitude, gbcTextLongitude);
        textLongitude.setColumns(10);

        // Add the edit buttons.
        final JPanel panelButton = new JPanel();
        buttonPanelEdit.add(panelButton, BorderLayout.SOUTH);

        final JButton buttonRemove = new JButton("Remove");
        buttonRemove.addActionListener(actionEvent -> removeRow());
        panelButton.add(buttonRemove);

        final JButton buttonUpdate = new JButton("Update");
        buttonUpdate.addActionListener(actionEvent -> updateRow());
        panelButton.add(buttonUpdate);

        final JButton buttonAdd = new JButton("Add");
        buttonAdd.addActionListener(actionEvent -> addRow());
        panelButton.add(buttonAdd);

        final JSeparator separator = new JSeparator();
        panelButton.add(separator);

        final JButton buttonRetrieve = new JButton("OKAPI Coordinates");
        buttonRetrieve.setEnabled(false);
        buttonRetrieve.setFont(new Font("Dialog", Font.BOLD, 9));
        buttonRetrieve.addActionListener(actionEvent -> retrieveOkapiCoordinates());
        panelButton.add(buttonRetrieve);

        // The panel for the table.
        final JPanel contentPanel = new JPanel();
        panelMaster.add(contentPanel);
        contentPanel.setLayout(new FlowLayout());
        contentPanel.setBorder(null);

        table = new JTable(tableModel);
        table.getSelectionModel().addListSelectionListener(listSelectionEvent -> handleSelection());
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFillsViewportHeight(true);

        final JScrollPane scrollPane =
                new JScrollPane(
                        table,
                        JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        contentPanel.add(scrollPane);

        pack();

        final ThreadStore threadStore = new ThreadStore();
        threadStore.addAndRun(new Thread(() -> checkOkapiStatus(buttonRetrieve)));
    }

    /**
     * Set the data from the given geocache instance inside the edit area.
     *
     * @param geocache The geocache instance to set the data from.
     */
    public void setGeocache(final Geocache geocache) {
        textName.setText(geocache.getName());
        textLatitude.setText(geocache.getCoordinate().getLatitude().toString());
        textLongitude.setText(geocache.getCoordinate().getLongitude().toString());
    }

    /**
     * Apply the changes to the location list.
     *
     * <p>This will close the dialog at the end.
     */
    private void applyChanges() {
        try {
            final ArrayList<Location> locations = new ArrayList<>();

            final DefaultTableModel defaultTableModel = ((DefaultTableModel) table.getModel());
            for (int i = 0; i < defaultTableModel.getRowCount(); i++) {
                final Location location =
                        new Location(
                                (String) table.getValueAt(i, 0),
                                Double.parseDouble((String) table.getValueAt(i, 1)),
                                Double.parseDouble((String) table.getValueAt(i, 2)));
                locations.add(location);
            }

            LocationList.getList().setLocations(locations);
        } catch (Throwable throwable) {
            ExceptionPanel.showErrorDialog(THIS, throwable);
            return;
        }

        modified = true;
        dispose();
    }

    /** Remove the currently selected row. */
    private void removeRow() {
        final int row = table.getSelectedRow();
        if (row == -1) {
            return;
        }
        ((DefaultTableModel) table.getModel()).removeRow(row);
    }

    /** Update the currently selected row with the data from the edit area. */
    private void updateRow() {
        final int row = table.getSelectedRow();
        if (row == -1) {
            return;
        }

        try {
            new Location(
                    textName.getText(),
                    Double.parseDouble(textLatitude.getText()),
                    Double.parseDouble(textLongitude.getText()));
        } catch (Throwable throwable) {
            ExceptionPanel.showErrorDialog(THIS, throwable);
            return;
        }

        final DefaultTableModel defaultTableModel = ((DefaultTableModel) table.getModel());
        defaultTableModel.setValueAt(textName.getText(), row, 0);
        defaultTableModel.setValueAt(textLatitude.getText(), row, 1);
        defaultTableModel.setValueAt(textLongitude.getText(), row, 2);
    }

    /** Add a new row with the data from the edit area. */
    private void addRow() {
        try {
            new Location(
                    textName.getText(),
                    Double.parseDouble(textLatitude.getText()),
                    Double.parseDouble(textLongitude.getText()));
        } catch (Throwable throwable) {
            ExceptionPanel.showErrorDialog(THIS, throwable);
            return;
        }

        final DefaultTableModel defaultTableModel = ((DefaultTableModel) table.getModel());
        final String[] values = {
            textName.getText(), textLatitude.getText(), textLongitude.getText()
        };
        defaultTableModel.addRow(values);
    }

    /**
     * Retrieve the home coordinates of the user from the OKAPI and display it inside the edit area.
     */
    private void retrieveOkapiCoordinates() {
        final User user = User.getOkapiUser();
        try {
            final Coordinate coordinate = Okapi.getHomeCoordinates(user);
            textName.setText("OKAPI Home Coordinate");
            textLatitude.setText(coordinate.getLatitude().toString());
            textLongitude.setText(coordinate.getLongitude().toString());
        } catch (Exception exception) {
            ExceptionPanel.showErrorDialog(THIS, exception);
        }
    }

    /** Show the data of the currently selected row inside the edit area. */
    private void handleSelection() {
        final int row = table.getSelectedRow();
        if (row == -1) {
            return;
        }

        textName.setText((String) table.getValueAt(row, 0));
        textLatitude.setText((String) table.getValueAt(row, 1));
        textLongitude.setText((String) table.getValueAt(row, 2));
    }

    /**
     * Check the OKAPI login status for the current user.
     *
     * @param buttonRetrieve The button to enable if the user has valid login data.
     */
    private void checkOkapiStatus(final JButton buttonRetrieve) {
        final User user = User.getOkapiUser();
        try {
            if (user.getOkapiToken() != null && Okapi.getUuid(user) != null) {
                buttonRetrieve.setEnabled(true);
            }
        } catch (Exception ignored) {
        }
    }
}
