package cmanager.gui.dialogs;

import cmanager.gui.ExceptionPanel;
import cmanager.okapi.Okapi;
import cmanager.okapi.RequestAuthorizationCallbackInterface;
import cmanager.okapi.User;
import cmanager.settings.Settings;
import cmanager.settings.SettingsKey;
import cmanager.util.DesktopUtil;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

/** Dialog to handle the settings. */
public class SettingsDialog extends JDialog {

    private static final long serialVersionUID = -6008083400079798934L;

    /** The current instance. */
    private final JDialog THIS = this;

    /** The label displaying the OKAPI token status. */
    private final JLabel labelOkapiToken;

    /** The label with the username on OC. */
    private final JLabel labelUsernameOc;

    /** The button to request a new OKAPI token. */
    private final JButton buttonRequestNewToken;

    /** The text field for the username on GC. */
    private final JTextField textUsernameGc;

    /**
     * Create the dialog.
     *
     * @param owner The parent frame.
     */
    public SettingsDialog(final JFrame owner) {
        super(owner);

        setTitle("Settings");
        setBounds(100, 100, 450, 300);

        // Basic container.
        final JPanel contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout(0, 0));

        // Buttons inside the footer.
        final JPanel panelButtons = new JPanel();
        contentPane.add(panelButtons, BorderLayout.SOUTH);
        panelButtons.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));

        final JButton buttonSaveApply = new JButton("Save & Apply");
        buttonSaveApply.addActionListener(actionEvent -> applyChanges());
        panelButtons.add(buttonSaveApply);

        final JButton buttonDiscard = new JButton("Discard");
        buttonDiscard.addActionListener(actionEvent -> THIS.setVisible(false));
        panelButtons.add(buttonDiscard);

        // Configuration tabs.
        final JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        contentPane.add(tabbedPane, BorderLayout.CENTER);

        // OC tab.
        final JPanel panelOc = new JPanel();
        panelOc.setBorder(new EmptyBorder(10, 10, 10, 10));
        tabbedPane.addTab("opencaching.de", null, panelOc, null);
        final SpringLayout springLayoutPanelOc = new SpringLayout();
        panelOc.setLayout(springLayoutPanelOc);

        final JLabel labelOkapiTokenText = new JLabel("OKAPI Token:");
        springLayoutPanelOc.putConstraint(
                SpringLayout.NORTH, labelOkapiTokenText, 40, SpringLayout.NORTH, panelOc);
        springLayoutPanelOc.putConstraint(
                SpringLayout.WEST, labelOkapiTokenText, 10, SpringLayout.WEST, panelOc);
        panelOc.add(labelOkapiTokenText);

        labelOkapiToken = new JLabel("New label");
        springLayoutPanelOc.putConstraint(
                SpringLayout.NORTH, labelOkapiToken, 0, SpringLayout.NORTH, labelOkapiTokenText);
        springLayoutPanelOc.putConstraint(
                SpringLayout.WEST, labelOkapiToken, 101, SpringLayout.EAST, labelOkapiTokenText);
        panelOc.add(labelOkapiToken);

        buttonRequestNewToken = new JButton("Request new token");
        springLayoutPanelOc.putConstraint(
                SpringLayout.SOUTH, buttonRequestNewToken, 0, SpringLayout.SOUTH, panelOc);
        springLayoutPanelOc.putConstraint(
                SpringLayout.EAST, buttonRequestNewToken, 0, SpringLayout.EAST, panelOc);
        buttonRequestNewToken.addActionListener(actionEvent -> requestOkapiToken());
        panelOc.add(buttonRequestNewToken);

        final JLabel labelUsernameOcText = new JLabel("OC Username: ");
        springLayoutPanelOc.putConstraint(
                SpringLayout.NORTH,
                labelUsernameOcText,
                6,
                SpringLayout.SOUTH,
                labelOkapiTokenText);
        springLayoutPanelOc.putConstraint(
                SpringLayout.WEST, labelUsernameOcText, 0, SpringLayout.WEST, labelOkapiTokenText);
        panelOc.add(labelUsernameOcText);

        labelUsernameOc = new JLabel("");
        springLayoutPanelOc.putConstraint(
                SpringLayout.NORTH, labelUsernameOc, 6, SpringLayout.SOUTH, labelOkapiToken);
        springLayoutPanelOc.putConstraint(
                SpringLayout.WEST, labelUsernameOc, 204, SpringLayout.WEST, panelOc);
        springLayoutPanelOc.putConstraint(
                SpringLayout.EAST, labelUsernameOc, -31, SpringLayout.EAST, panelOc);
        labelUsernameOc.setHorizontalAlignment(SwingConstants.LEFT);
        labelUsernameOc.setText(Settings.getString(SettingsKey.OC_USERNAME));
        panelOc.add(labelUsernameOc);

        // GC tab.
        final JPanel panelGc = new JPanel();
        tabbedPane.addTab("geocaching.com", null, panelGc, null);
        final GridBagLayout gblPanelGc = new GridBagLayout();
        gblPanelGc.columnWidths = new int[] {215, 215, 0};
        gblPanelGc.rowHeights = new int[] {201, 0};
        gblPanelGc.columnWeights = new double[] {0.0, 0.0, Double.MIN_VALUE};
        gblPanelGc.rowWeights = new double[] {0.0, Double.MIN_VALUE};
        panelGc.setLayout(gblPanelGc);

        final JLabel labelUsername = new JLabel("Username:");
        final GridBagConstraints gbcLabelUsername = new GridBagConstraints();
        gbcLabelUsername.gridwidth = 50;
        gbcLabelUsername.anchor = GridBagConstraints.NORTHWEST;
        gbcLabelUsername.insets = new Insets(20, 20, 0, 5);
        gbcLabelUsername.gridx = 0;
        gbcLabelUsername.gridy = 0;
        panelGc.add(labelUsername, gbcLabelUsername);

        textUsernameGc = new JTextField();
        final GridBagConstraints gbcTextUsernameGc = new GridBagConstraints();
        gbcTextUsernameGc.weighty = 0.5;
        gbcTextUsernameGc.insets = new Insets(20, 0, 0, 0);
        gbcTextUsernameGc.anchor = GridBagConstraints.NORTH;
        gbcTextUsernameGc.fill = GridBagConstraints.HORIZONTAL;
        gbcTextUsernameGc.gridx = 1;
        gbcTextUsernameGc.gridy = 0;
        panelGc.add(textUsernameGc, gbcTextUsernameGc);
        textUsernameGc.setColumns(10);

        // Load the data from the settings.
        displayOkapiTokenStatus();
        textUsernameGc.setText(Settings.getString(SettingsKey.GC_USERNAME));
    }

    /** Display the OKAPI token status. */
    private void displayOkapiTokenStatus() {
        labelOkapiToken.setText("missing or offline");
        Font font = labelOkapiToken.getFont();
        labelOkapiToken.setFont(font.deriveFont(font.getStyle() | Font.ITALIC));
        buttonRequestNewToken.setVisible(true);

        // Indicate when there is an OKAPI token available and disable the request button.
        final User user = User.getOkapiUser();
        try {
            if (user.getOkapiToken() != null && Okapi.getUuid(user) != null) {
                labelOkapiToken.setText("okay");
                font = labelOkapiToken.getFont();
                labelOkapiToken.setFont(font.deriveFont(font.getStyle() & ~Font.ITALIC));
                buttonRequestNewToken.setVisible(false);

                final String username = Okapi.getUsername(user);
                Settings.set(SettingsKey.OC_USERNAME, username);
                labelUsernameOc.setText(username);
            }
        } catch (Exception ignored) {
        }
    }

    /** Apply the changes to the settings. */
    private void applyChanges() {
        // Save the non-OC settings.
        Settings.set(SettingsKey.GC_USERNAME, textUsernameGc.getText());
        THIS.setVisible(false);
    }

    /** Request the OKAPI token. */
    private void requestOkapiToken() {
        try {
            User.getOkapiUser()
                    .requestOkapiToken(
                            new RequestAuthorizationCallbackInterface() {
                                @Override
                                public String getPin() {
                                    return JOptionPane.showInputDialog(
                                            null,
                                            "Please look at your browser and enter the PIN from opencaching.de");
                                }

                                @Override
                                public void redirectUrlToUser(final String authUrl) {
                                    DesktopUtil.openUrl(authUrl);
                                }
                            });
            displayOkapiTokenStatus();
        } catch (Throwable throwable) {
            ExceptionPanel.showErrorDialog(THIS, throwable);
        }
    }
}
