package cmanager.gui.dialogs;

import cmanager.exception.UnexpectedLogStatus;
import cmanager.geo.Geocache;
import cmanager.geo.GeocacheComparator;
import cmanager.geo.GeocacheLog;
import cmanager.gui.ExceptionPanel;
import cmanager.gui.GuiUtils;
import cmanager.gui.MainWindow;
import cmanager.gui.components.CacheListView;
import cmanager.gui.components.CachePanel;
import cmanager.gui.components.HintedTextField;
import cmanager.gui.components.LogPanel;
import cmanager.gui.components.Logo;
import cmanager.oc.OcUtil;
import cmanager.oc.ShadowList;
import cmanager.okapi.Okapi;
import cmanager.okapi.User;
import cmanager.settings.Settings;
import cmanager.settings.SettingsKey;
import cmanager.util.DesktopUtil;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.border.EmptyBorder;

/** Dialog for copying a log. */
public class CopyLogDialog extends JFrame {

    private static final long serialVersionUID = 363313395887255591L;

    /** Save the bounds. */
    private static Rectangle savedBounds = new Rectangle(100, 100, 850, 500);

    /** The current instance. */
    private final CopyLogDialog THIS = this;

    /** The left panel. */
    private final JSplitPane splitPane1;

    /** The right panel. */
    private final JSplitPane splitPane2;

    /** The scroll container for the log entries (right column). */
    private final JScrollPane scrollPane;

    /** The hinted text field for the log password. */
    private HintedTextField textFieldPassword = null;

    /**
     * Create the dialog.
     *
     * @param gc The GC geocache instance.
     * @param oc The OC geocache instance.
     * @param logsCopied The list of logs already copied. This will be updated after submitting a
     *     successful log.
     * @param shadowList The shadow list instance to use for posting after having submitted a
     *     successful log.
     */
    public CopyLogDialog(
            final Geocache gc,
            final Geocache oc,
            final List<GeocacheLog> logsCopied,
            final ShadowList shadowList) {
        setResizable(true);
        Logo.setLogo(this);

        setTitle("Copy Logs");
        setBounds(savedBounds);
        getContentPane().setLayout(new BorderLayout());

        final JPanel contentPanel = new JPanel();
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(new BorderLayout(0, 0));

        splitPane1 = new JSplitPane();
        contentPanel.add(splitPane1);

        splitPane2 = new JSplitPane();
        splitPane1.setRightComponent(splitPane2);

        // The left column contains the GC listing.
        final CachePanel panelGc = new CachePanel();
        panelGc.setMinimumSize(new Dimension(100, 100));
        panelGc.setCache(gc, false);
        panelGc.colorize(oc);

        // The middle column contains the OC listing.
        final CachePanel panelOc = new CachePanel();
        panelOc.setMinimumSize(new Dimension(100, 100));
        panelOc.setCache(oc, false);
        panelOc.colorize(gc);

        splitPane1.setLeftComponent(panelGc);
        splitPane2.setLeftComponent(panelOc);

        // The right column contains the log copy functionality.
        final JPanel panelLogs = new JPanel();
        panelLogs.setLayout(new GridBagLayout());

        final GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH;

        // Add an entry for each found log.
        for (final GeocacheLog log : gc.getLogs()) {
            // Skip non-found logs.
            if (!log.isFoundLog()) {
                continue;
            }

            // Skip already posted logs.
            if (logsCopied.contains(log)) {
                continue;
            }

            // Skip logs where the GC user is not the author of.
            final String gcUsername = Settings.getString(SettingsKey.GC_USERNAME);
            if (!log.isAuthor(gcUsername)) {
                continue;
            }

            // Add the panel for the current log.
            final LogPanel logPanel = new LogPanel(log);
            panelLogs.add(logPanel, gbc);
            gbc.gridy++;

            // Add the password field if required.
            if (oc.doesRequirePassword() != null && oc.doesRequirePassword()) {
                final GridBagConstraints gbcPassword = (GridBagConstraints) gbc.clone();
                gbcPassword.weighty = 0;
                gbcPassword.fill = 1;
                gbcPassword.insets = new Insets(0, 10, 10, 0);

                textFieldPassword = new HintedTextField("Log password");
                panelLogs.add(textFieldPassword, gbcPassword);
                gbc.gridy++;
            }

            final GridBagConstraints gbcButton = (GridBagConstraints) gbc.clone();
            gbcButton.weighty = 0;
            gbcButton.fill = 0;
            gbcButton.insets = new Insets(0, 10, 10, 0);
            gbc.gridy++;

            // Add the copy button.
            final JButton button = new JButton("Copy log to opencaching.de");
            if (GeocacheComparator.calculateSimilarity(gc, oc) != 1) {
                button.setBackground(Color.RED);
            }
            button.addActionListener(
                    new ActionListener() {
                        // Perform the actual copy operation.
                        public void actionPerformed(final ActionEvent actionEvent) {
                            MainWindow.actionWithWaitDialog(
                                    () ->
                                            copyLog(
                                                    shadowList,
                                                    gc,
                                                    oc,
                                                    log,
                                                    logPanel,
                                                    button,
                                                    logsCopied),
                                    THIS);
                        }

                        private Geocache oc;
                        private GeocacheLog log;

                        public ActionListener set(final Geocache oc, final GeocacheLog log) {
                            this.oc = oc;
                            this.log = log;
                            return this;
                        }
                    }.set(oc, log));
            panelLogs.add(button, gbcButton);
        }

        // Wrap the log entries with a scroll panel.
        scrollPane = new JScrollPane(panelLogs);
        scrollPane.addComponentListener(
                new ComponentAdapter() {
                    public void componentResized(ComponentEvent componentEvent) {
                        scrollPane.getVerticalScrollBar().setValue(0);
                        scrollPane.getHorizontalScrollBar().setValue(0);
                    }
                });
        splitPane2.setRightComponent(scrollPane);

        // Add the return button.
        final JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
        getContentPane().add(buttonPane, BorderLayout.SOUTH);

        final JButton buttonReturn = new JButton("Return");
        buttonPane.add(buttonReturn);
        buttonReturn.addActionListener(
                actionEvent -> {
                    THIS.dispatchEvent(new WindowEvent(THIS, WindowEvent.WINDOW_CLOSING));
                });

        // Handle close events.
        addWindowListener(
                new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent windowEvent) {
                        savedBounds = THIS.getBounds();
                        super.windowClosing(windowEvent);
                    }
                });

        // Fix the split panes on initialization and when resizing the window.
        THIS.addComponentListener(
                new ComponentAdapter() {
                    public void componentShown(final ComponentEvent componentEvent) {
                        CacheListView.fixSplitPanes(splitPane1, splitPane2);
                    }

                    public void componentResized(final ComponentEvent componentEvent) {
                        CacheListView.fixSplitPanes(splitPane1, splitPane2);
                    }
                });
    }

    /**
     * Copy the given log.
     *
     * @param shadowList The shadow list instance to post the duplicate to.
     * @param gc The GC geocache instance.
     * @param oc The OC geocache instance.
     * @param log The log entry to copy.
     * @param logPanel The panel with the current log.
     * @param button The log copying button.
     * @param logsCopied The list of logs already copied.
     */
    private void copyLog(
            final ShadowList shadowList,
            final Geocache gc,
            final Geocache oc,
            final GeocacheLog log,
            final LogPanel logPanel,
            final JButton button,
            final List<GeocacheLog> logsCopied) {
        try {
            // Contribute to shadow list.
            shadowList.postToShadowList(gc, oc);

            // Retrieve the new log text.
            log.setText(logPanel.getLogText());

            // Retrieve the log password.
            // NOTE: We might want to save the password inside the personal note for this geocache
            // later, see https://www.opencaching.de/okapi/services/caches/save_personal_notes.html
            // For now the current approach should be sufficient, as duplicates requiring log
            // passwords might be rather uncommon anyway.
            if (textFieldPassword != null
                    && oc.doesRequirePassword() != null
                    && oc.doesRequirePassword()) {
                log.setPassword(textFieldPassword.getText());
            }

            // Copy the log and determine its URL.
            final String logId = Okapi.postLog(User.getOkapiUser(), oc, log, true);
            final String logUrl = OcUtil.determineLogUrl(oc, logId);

            // Use button to let the user open the posted log.
            GuiUtils.removeActionListeners(button);
            button.addActionListener(actionEvent -> DesktopUtil.openUrl(logUrl));
            button.setText("Open log on opencaching.de");

            // Remember that we copied the log so the user can not double post it by accident.
            logsCopied.add(log);
        } catch (UnexpectedLogStatus exception) {
            // Handle general log problems separately to provide a better error message.
            ExceptionPanel.showErrorDialog(
                    THIS, exception.getResponseMessage(), "Unexpected log status");
        } catch (Throwable throwable) {
            ExceptionPanel.showErrorDialog(THIS, throwable);
        }
    }
}
