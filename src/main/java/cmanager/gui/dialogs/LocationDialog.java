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

public class LocationDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private final JTable table;
    private final JTextField textName;
    private final JTextField textLatitude;
    private final JTextField textLongitude;

    public boolean modified = false;

    private final LocationDialog THIS = this;

    /** Create the dialog. */
    public LocationDialog(JFrame owner) {
        super(owner);

        setTitle("Locations");
        getContentPane().setLayout(new BorderLayout());

        final String[] columnNames = {"Name", "Lat", "Lon"};
        final String[][] dataValues = {};
        final DefaultTableModel tableModel = new DefaultTableModel(dataValues, columnNames);

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

        final JPanel panelMaster = new JPanel();
        panelMaster.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(panelMaster, BorderLayout.CENTER);
        panelMaster.setLayout(new BorderLayout(0, 0));

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

    public void setGeocache(Geocache geocache) {
        textName.setText(geocache.getName());
        textLatitude.setText(geocache.getCoordinate().getLatitude().toString());
        textLongitude.setText(geocache.getCoordinate().getLongitude().toString());
    }

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

    private void removeRow() {
        final int row = table.getSelectedRow();
        if (row == -1) {
            return;
        }
        ((DefaultTableModel) table.getModel()).removeRow(row);
    }

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

    private void retrieveOkapiCoordinates() {
        final User user = User.getOKAPIUser();
        try {
            final Coordinate coordinate = Okapi.getHomeCoordinates(user);
            textName.setText("OKAPI Home Coordinate");
            textLatitude.setText(coordinate.getLatitude().toString());
            textLongitude.setText(coordinate.getLongitude().toString());
        } catch (Exception exception) {
            ExceptionPanel.showErrorDialog(THIS, exception);
        }
    }

    private void handleSelection() {
        final int row = table.getSelectedRow();
        if (row == -1) {
            return;
        }

        textName.setText((String) table.getValueAt(row, 0));
        textLatitude.setText((String) table.getValueAt(row, 1));
        textLongitude.setText((String) table.getValueAt(row, 2));
    }

    private void checkOkapiStatus(final JButton buttonRetrieve) {
        final User user = User.getOKAPIUser();
        try {
            if (user.getOkapiToken() != null && Okapi.getUuid(user) != null) {
                buttonRetrieve.setEnabled(true);
            }
        } catch (Exception ignored) {
        }
    }
}
