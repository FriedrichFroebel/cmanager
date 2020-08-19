package cmanager.gui.components;

import cmanager.geo.Geocache;
import cmanager.geo.GeocacheLog;
import cmanager.util.DesktopUtil;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.LayoutStyle;
import javax.swing.SwingConstants;

/** Panel for showing one geocache entry. */
public class CachePanel extends JPanel {

    private static final long serialVersionUID = -4848832298041708795L;

    /** The associated geocache instance. */
    private Geocache geocache = null;

    /** The main scroll pane. */
    private JScrollPane scrollPaneMain;

    /** The geocache name. */
    private JLabel labelName;

    /** The geocache code. */
    private JLabel labelCode;

    /** The geocache coordinates. */
    private JLabel labelCoordinates;

    /** The geocache type. */
    private JLabel labelType;

    /** The difficulty rating. */
    private JLabel labelDifficulty;

    /** The terrain rating. */
    private JLabel labelTerrain;

    /** The container size. */
    private JLabel labelContainer;

    /** The geocache status. */
    private JLabel labelStatus;

    /** The owner name. */
    private JLabel labelOwner;

    /** The overview panel with the basic data like the container size, ratings etc. */
    private JPanel panelOverview;

    /** The panel with the button to open the geocache online. */
    private JPanel panelLink;

    /** The panel for the complete listing. */
    private JPanel panelListing;

    /** The panel for the listing text. */
    private JPanel panelListingText;

    /** The actual listing text. */
    private JEditorPane editorListing;

    /** The panel for the geocache logs. */
    private JPanel panelLogs;

    /**
     * Set the geocache instance for the panel.
     *
     * <p>This will show the logs.
     *
     * @param geocache The geocache instance to set.
     */
    public void setCache(final Geocache geocache) {
        setCache(geocache, true);
    }

    /**
     * Set the geocache instance for the panel.
     *
     * @param geocache The geocache instance to set.
     * @param showLogs Whether to show the log. This usually is <code>False</code> for log copying
     *     and <code>True</code> within the list view.
     */
    public void setCache(final Geocache geocache, final boolean showLogs) {
        this.geocache = geocache;

        if (this.geocache == null) {
            // Hide the listing if no geocache instance is set.
            panelListing.setVisible(false);
        } else {
            // Display the data of the geocache instance.

            labelName.setText(this.geocache.getName());
            labelCode.setText(this.geocache.getCode());

            final String coordinates =
                    this.geocache.getCoordinate() != null
                            ? this.geocache.getCoordinate().toString()
                            : null;
            labelCoordinates.setText(coordinates);

            labelType.setText(this.geocache.getType().asNiceType());
            labelDifficulty.setText(this.geocache.getDifficulty().toString());
            labelTerrain.setText(this.geocache.getTerrain().toString());
            labelContainer.setText(this.geocache.getContainer().asGc());
            labelStatus.setText(this.geocache.getStatusAsString());
            labelOwner.setText(this.geocache.getOwner());

            String listing = this.geocache.getListingShort();
            if (listing != null && !listing.equals("")) {
                // If there is a short listing text, separate it by two empty lines.
                listing += "<br><br>";
            } else {
                listing = "";
            }
            listing = "<html>" + listing + this.geocache.getListing() + "</html>";

            editorListing.setContentType("text/html");
            editorListing.setText(listing);

            panelLogs.removeAll();

            // Used within the list view, not for log copying.
            if (showLogs) {
                final GridBagConstraints gridBagConstraints = new GridBagConstraints();
                gridBagConstraints.gridx = 0;
                gridBagConstraints.gridy = 0;
                gridBagConstraints.weightx = 1;
                gridBagConstraints.weighty = 1;
                gridBagConstraints.anchor = GridBagConstraints.WEST;
                gridBagConstraints.fill = GridBagConstraints.BOTH;
                for (final GeocacheLog log : this.geocache.getLogs()) {
                    panelLogs.add(new LogPanel(log), gridBagConstraints);
                    gridBagConstraints.gridy++;
                }
                panelLogs.validate();
            }

            adjustToOptimalWidth();

            panelListing.setVisible(true);
        }
    }

    /**
     * Show common and different values between the displayed geocache instance and the given
     * geocache instance.
     *
     * @param geocache2 The geocache instance to compare the current instance against.
     */
    public void colorize(final Geocache geocache2) {
        if (geocache.getCoordinate() != null) {
            colorize(labelCoordinates, geocache.getCoordinate().equals(geocache2.getCoordinate()));
        }
        if (geocache.getStatusAsString() != null) {
            colorize(
                    labelStatus,
                    geocache.getStatusAsString().equals(geocache2.getStatusAsString()));
        }
        if (geocache.getName() != null) {
            colorize(labelName, geocache.getName().equals(geocache2.getName()));
        }
        if (geocache.getOwner() != null) {
            colorize(labelOwner, geocache.getOwner().equals(geocache2.getOwner()));
        }

        colorize(labelType, geocache.getType().equals(geocache2.getType()));
        colorize(
                labelDifficulty,
                Double.compare(geocache.getDifficulty(), geocache2.getDifficulty()) == 0);
        colorize(labelTerrain, Double.compare(geocache.getTerrain(), geocache2.getTerrain()) == 0);
        colorize(labelContainer, geocache.getContainer().equals(geocache2.getContainer()));
    }

    /**
     * Set the color for the given label.
     *
     * @param label The label to colorize.
     * @param good Set to <code>True</code> to use green color, set to <code>False</code> to use red
     *     color.
     */
    private void colorize(final JLabel label, final boolean good) {
        label.setOpaque(true);
        if (good) {
            label.setBackground(Color.GREEN);
        } else {
            label.setBackground(Color.RED);
        }
    }

    /** Adjust the containers to their optimal width. */
    public void adjustToOptimalWidth() {
        int width = scrollPaneMain.getViewport().getVisibleRect().width;
        if (width <= 0) {
            return;
        }

        final int scroll = scrollPaneMain.getVerticalScrollBar().getWidth() + 20;
        width -= scroll;

        Dimension dimension = new Dimension(width, panelOverview.getSize().height);
        panelOverview.setSize(dimension);
        panelOverview.setPreferredSize(dimension);
        dimension = labelName.getMinimumSize();
        dimension.width += 20;
        panelOverview.setMinimumSize(dimension);
        panelOverview.validate();

        dimension = new Dimension(width, panelLink.getSize().height);
        panelLink.setSize(dimension);
        panelLink.setPreferredSize(dimension);
        panelLink.validate();

        panelListing.validate();
        panelLogs.validate();
    }

    /** Create a new cache panel. */
    public CachePanel() {
        initComponents();

        this.addComponentListener(
                new ComponentAdapter() {
                    @Override
                    public void componentResized(ComponentEvent componentEvent) {
                        adjustToOptimalWidth();
                    }
                });

        panelListingText.addComponentListener(
                new ComponentAdapter() {
                    @Override
                    public void componentResized(ComponentEvent componentEvent) {
                        scrollPaneMain.getVerticalScrollBar().setValue(0);
                        scrollPaneMain.getHorizontalScrollBar().setValue(0);
                    }
                });
        scrollPaneMain.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);

        setCache(null);
    }

    /** Initialize all components. */
    // This has been created automatically by NetBeans IDE, but with manual cleanup afterwards.
    private void initComponents() {
        setLayout(new BorderLayout());

        // Main container.
        scrollPaneMain = new JScrollPane();
        scrollPaneMain.setAlignmentX(0.0F);
        scrollPaneMain.setAlignmentY(0.0F);

        // Listing panel.
        panelListing = new JPanel();
        panelListing.setAutoscrolls(true);
        panelListing.setMaximumSize(new Dimension(400, 400));

        // Overview panel with the basic cache data.
        panelOverview = new JPanel();
        panelOverview.setAlignmentX(1.0F);
        panelOverview.setAlignmentY(0.0F);
        panelOverview.addComponentListener(
                new ComponentAdapter() {
                    public void componentResized(ComponentEvent componentEvent) {
                        panelOverviewComponentResized(componentEvent);
                    }
                });

        // Cache name.
        labelName = new JLabel("Cache name");
        labelName.setBackground(new Color(102, 255, 51));
        labelName.setFont(new Font("Dialog", 1, 18));
        labelName.setHorizontalAlignment(SwingConstants.CENTER);

        // Cache code.
        labelCode = new JLabel("Cache code");
        labelCode.setHorizontalAlignment(SwingConstants.CENTER);

        // Coordinates.
        final JLabel labelCoordinatesText = new JLabel("Coordinates:");
        labelCoordinates = new JLabel("Coordinate value");

        // Geocache type.
        final JLabel labelTypeText = new JLabel("Type:");
        labelType = new JLabel("Type value");

        // Difficulty rating.
        final JLabel labelDifficultyText = new JLabel("Difficulty:");
        labelDifficulty = new JLabel("Difficulty rating");

        // Terrain rating.
        final JLabel labelTerrainText = new JLabel("Terrain:");
        labelTerrain = new JLabel("Terrain rating");

        // Container size.
        final JLabel labelContainerText = new JLabel("Container:");
        labelContainer = new JLabel("Container size");

        // Cache status.
        final JLabel labelStatusText = new JLabel("Status:");
        labelStatus = new JLabel("Cache status");

        // Cache owner.
        final JLabel labelOwnerText = new JLabel("Owner:");
        labelOwner = new JLabel("Owner name");

        // Add the single labels to the overview panel.
        final GroupLayout panelOverviewLayout = new GroupLayout(panelOverview);
        panelOverview.setLayout(panelOverviewLayout);

        panelOverviewLayout.setHorizontalGroup(
                panelOverviewLayout
                        .createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(
                                panelOverviewLayout
                                        .createSequentialGroup()
                                        .addGap(12, 12, 12)
                                        .addComponent(
                                                labelName,
                                                GroupLayout.DEFAULT_SIZE,
                                                GroupLayout.DEFAULT_SIZE,
                                                Short.MAX_VALUE))
                        .addGroup(
                                panelOverviewLayout
                                        .createSequentialGroup()
                                        .addGap(12, 12, 12)
                                        .addComponent(
                                                labelCode,
                                                GroupLayout.DEFAULT_SIZE,
                                                512,
                                                Short.MAX_VALUE))
                        .addGroup(
                                panelOverviewLayout
                                        .createSequentialGroup()
                                        .addGap(20, 20, 20)
                                        .addGroup(
                                                panelOverviewLayout
                                                        .createParallelGroup(
                                                                GroupLayout.Alignment.LEADING)
                                                        .addGroup(
                                                                panelOverviewLayout
                                                                        .createSequentialGroup()
                                                                        .addGroup(
                                                                                panelOverviewLayout
                                                                                        .createParallelGroup(
                                                                                                GroupLayout
                                                                                                        .Alignment
                                                                                                        .LEADING)
                                                                                        .addComponent(
                                                                                                labelContainerText)
                                                                                        .addComponent(
                                                                                                labelDifficultyText)
                                                                                        .addComponent(
                                                                                                labelTypeText)
                                                                                        .addComponent(
                                                                                                labelTerrainText)
                                                                                        .addComponent(
                                                                                                labelStatusText))
                                                                        .addContainerGap(
                                                                                GroupLayout
                                                                                        .DEFAULT_SIZE,
                                                                                Short.MAX_VALUE))
                                                        .addGroup(
                                                                panelOverviewLayout
                                                                        .createSequentialGroup()
                                                                        .addGroup(
                                                                                panelOverviewLayout
                                                                                        .createParallelGroup(
                                                                                                GroupLayout
                                                                                                        .Alignment
                                                                                                        .LEADING)
                                                                                        .addComponent(
                                                                                                labelCoordinatesText)
                                                                                        .addComponent(
                                                                                                labelOwnerText))
                                                                        .addPreferredGap(
                                                                                LayoutStyle
                                                                                        .ComponentPlacement
                                                                                        .UNRELATED)
                                                                        .addGroup(
                                                                                panelOverviewLayout
                                                                                        .createParallelGroup(
                                                                                                GroupLayout
                                                                                                        .Alignment
                                                                                                        .LEADING)
                                                                                        .addComponent(
                                                                                                labelOwner)
                                                                                        .addComponent(
                                                                                                labelType)
                                                                                        .addComponent(
                                                                                                labelCoordinates)
                                                                                        .addComponent(
                                                                                                labelDifficulty)
                                                                                        .addComponent(
                                                                                                labelTerrain)
                                                                                        .addComponent(
                                                                                                labelContainer)
                                                                                        .addComponent(
                                                                                                labelStatus))
                                                                        .addGap(
                                                                                0,
                                                                                0,
                                                                                Short
                                                                                        .MAX_VALUE)))));

        panelOverviewLayout.setVerticalGroup(
                panelOverviewLayout
                        .createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(
                                panelOverviewLayout
                                        .createSequentialGroup()
                                        .addGap(12, 12, 12)
                                        .addComponent(labelName)
                                        .addGap(6, 6, 6)
                                        .addComponent(labelCode)
                                        .addGap(12, 12, 12)
                                        .addGroup(
                                                panelOverviewLayout
                                                        .createParallelGroup(
                                                                GroupLayout.Alignment.BASELINE)
                                                        .addComponent(labelCoordinatesText)
                                                        .addComponent(labelCoordinates))
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(
                                                panelOverviewLayout
                                                        .createParallelGroup(
                                                                GroupLayout.Alignment.BASELINE)
                                                        .addComponent(labelTypeText)
                                                        .addComponent(labelType))
                                        .addGap(6, 6, 6)
                                        .addGroup(
                                                panelOverviewLayout
                                                        .createParallelGroup(
                                                                GroupLayout.Alignment.BASELINE)
                                                        .addComponent(labelDifficultyText)
                                                        .addComponent(labelDifficulty))
                                        .addGap(6, 6, 6)
                                        .addGroup(
                                                panelOverviewLayout
                                                        .createParallelGroup(
                                                                GroupLayout.Alignment.BASELINE)
                                                        .addComponent(labelTerrainText)
                                                        .addComponent(labelTerrain))
                                        .addGap(6, 6, 6)
                                        .addGroup(
                                                panelOverviewLayout
                                                        .createParallelGroup(
                                                                GroupLayout.Alignment.BASELINE)
                                                        .addComponent(labelContainerText)
                                                        .addComponent(labelContainer))
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(
                                                panelOverviewLayout
                                                        .createParallelGroup(
                                                                GroupLayout.Alignment.BASELINE)
                                                        .addComponent(labelStatusText)
                                                        .addComponent(labelStatus))
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(
                                                panelOverviewLayout
                                                        .createParallelGroup(
                                                                GroupLayout.Alignment.BASELINE)
                                                        .addComponent(labelOwnerText)
                                                        .addComponent(labelOwner))
                                        .addContainerGap(
                                                GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

        // Button to open cache in web browser.
        final JButton buttonViewOnline = new JButton("View Online");
        buttonViewOnline.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent actionEvent) {
                        buttonViewOnlineActionPerformed(actionEvent);
                    }
                });

        // Add the button to the corresponding panel.
        panelLink = new JPanel();
        final GroupLayout panelLinkLayout = new GroupLayout(panelLink);
        panelLink.setLayout(panelLinkLayout);

        panelLinkLayout.setHorizontalGroup(
                panelLinkLayout
                        .createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(
                                GroupLayout.Alignment.TRAILING,
                                panelLinkLayout
                                        .createSequentialGroup()
                                        .addContainerGap(374, Short.MAX_VALUE)
                                        .addComponent(buttonViewOnline)
                                        .addContainerGap()));
        panelLinkLayout.setVerticalGroup(
                panelLinkLayout
                        .createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(
                                GroupLayout.Alignment.TRAILING,
                                panelLinkLayout
                                        .createSequentialGroup()
                                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(buttonViewOnline)
                                        .addContainerGap()));

        // Listing text.
        editorListing = new JEditorPane();
        editorListing.setEditable(false);

        // Add the listing text to the corresponding panel.
        panelListingText = new JPanel();
        final GroupLayout panelListingTextLayout = new GroupLayout(panelListingText);
        panelListingText.setLayout(panelListingTextLayout);

        panelListingTextLayout.setHorizontalGroup(
                panelListingTextLayout
                        .createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGap(0, 548, Short.MAX_VALUE)
                        .addGroup(
                                panelListingTextLayout
                                        .createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(
                                                editorListing,
                                                GroupLayout.PREFERRED_SIZE,
                                                524,
                                                Short.MAX_VALUE)));
        panelListingTextLayout.setVerticalGroup(
                panelListingTextLayout
                        .createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGap(0, 87, Short.MAX_VALUE)
                        .addGroup(
                                panelListingTextLayout
                                        .createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(
                                                editorListing,
                                                GroupLayout.Alignment.TRAILING,
                                                GroupLayout.DEFAULT_SIZE,
                                                87,
                                                Short.MAX_VALUE)));

        // Log panel.
        panelLogs = new JPanel();
        panelLogs.setLayout(new GridBagLayout());

        // Add the single panels to the overall listing panel.
        final GroupLayout panelListingLayout = new GroupLayout(panelListing);
        panelListing.setLayout(panelListingLayout);

        panelListingLayout.setHorizontalGroup(
                panelListingLayout
                        .createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(
                                panelListingLayout
                                        .createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(
                                                panelListingLayout
                                                        .createParallelGroup(
                                                                GroupLayout.Alignment.LEADING)
                                                        .addComponent(
                                                                panelLink,
                                                                GroupLayout.PREFERRED_SIZE,
                                                                GroupLayout.DEFAULT_SIZE,
                                                                GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(
                                                                panelOverview,
                                                                GroupLayout.PREFERRED_SIZE,
                                                                GroupLayout.DEFAULT_SIZE,
                                                                GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(
                                                                panelLogs,
                                                                GroupLayout.PREFERRED_SIZE,
                                                                523,
                                                                GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(
                                                                panelListingText,
                                                                GroupLayout.Alignment.TRAILING,
                                                                GroupLayout.DEFAULT_SIZE,
                                                                GroupLayout.DEFAULT_SIZE,
                                                                Short.MAX_VALUE))
                                        .addContainerGap()));
        panelListingLayout.setVerticalGroup(
                panelListingLayout
                        .createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(
                                panelListingLayout
                                        .createSequentialGroup()
                                        .addComponent(
                                                panelOverview,
                                                GroupLayout.PREFERRED_SIZE,
                                                GroupLayout.DEFAULT_SIZE,
                                                GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(
                                                panelLink,
                                                GroupLayout.PREFERRED_SIZE,
                                                GroupLayout.DEFAULT_SIZE,
                                                GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(
                                                panelListingText,
                                                GroupLayout.PREFERRED_SIZE,
                                                GroupLayout.DEFAULT_SIZE,
                                                GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(
                                                panelLogs,
                                                GroupLayout.DEFAULT_SIZE,
                                                90,
                                                Short.MAX_VALUE)
                                        .addContainerGap()));

        // Add the listing panel to the scroll pane.
        scrollPaneMain.setViewportView(panelListing);

        // Add the scroll panel to main parent panel.
        // The second parameter has to be `BorderLayout.CENTER` - otherwise the output is wrong!
        // NetBeans IDE provides the (wrong) value `BorderLayout.LINE_END`.
        add(scrollPaneMain, BorderLayout.CENTER);
    }

    /**
     * Handle requests from the user to open the current geocache instance inside the web browser.
     *
     * @param actionEvent The button event.
     */
    private void buttonViewOnlineActionPerformed(final ActionEvent actionEvent) {
        DesktopUtil.openUrl(geocache.getUrl());
    }

    /**
     * Scroll to the top when the overview panel has been resized.
     *
     * @param componentEvent The panel event.
     */
    private void panelOverviewComponentResized(final ComponentEvent componentEvent) {
        scrollPaneMain.getVerticalScrollBar().setValue(0);
    }
}
