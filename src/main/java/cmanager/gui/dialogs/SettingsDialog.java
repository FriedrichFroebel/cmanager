package cmanager.gui.dialogs;

import cmanager.global.Constants;
import cmanager.gui.ExceptionPanel;
import cmanager.okapi.Okapi;
import cmanager.okapi.RequestAuthorizationCallbackInterface;
import cmanager.okapi.User;
import cmanager.settings.Settings;
import cmanager.util.DesktopUtil;
import cmanager.util.ForkUtil;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.Box;
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

public class SettingsDialog extends JDialog {

    private static final long serialVersionUID = -6008083400079798934L;

    private final JDialog THIS = this;
    private final JLabel labelOkapiToken;
    private final JLabel labelUsernameOc;
    private final JButton buttonRequestNewToken;
    private final JTextField textUsernameGc;
    private final JTextField textHeapSize;

    /** Create the frame. */
    public SettingsDialog(JFrame owner) {
        super(owner);

        setTitle("Settings");
        setBounds(100, 100, 450, 300);
        final JPanel contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout(0, 0));

        final JPanel panelButtons = new JPanel();
        contentPane.add(panelButtons, BorderLayout.SOUTH);
        panelButtons.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));

        final JButton buttonSaveApply = new JButton("Save & Apply");
        buttonSaveApply.addActionListener(actionEvent -> applyChanges());
        panelButtons.add(buttonSaveApply);

        final JButton buttonDiscard = new JButton("Discard");
        buttonDiscard.addActionListener(actionEvent -> THIS.setVisible(false));
        panelButtons.add(buttonDiscard);

        final JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        contentPane.add(tabbedPane, BorderLayout.CENTER);

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
        labelUsernameOc.setText(Settings.getString(Settings.Key.OC_USERNAME));
        panelOc.add(labelUsernameOc);

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

        displayOkapiTokenStatus();
        textUsernameGc.setText(Settings.getString(Settings.Key.GC_USERNAME));

        final JPanel panelGeneral = new JPanel();
        panelGeneral.setBorder(new EmptyBorder(10, 10, 10, 10));
        tabbedPane.addTab("General", null, panelGeneral, null);
        final GridBagLayout gblPanelGeneral = new GridBagLayout();
        gblPanelGeneral.rowWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0};
        gblPanelGeneral.columnWeights = new double[] {1.0, 1.0, 0.0};
        panelGeneral.setLayout(gblPanelGeneral);

        final Component verticalStrut1 = Box.createVerticalStrut(20);
        final GridBagConstraints gbcVerticalStrut1 = new GridBagConstraints();
        gbcVerticalStrut1.weighty = 0.1;
        gbcVerticalStrut1.insets = new Insets(0, 0, 5, 5);
        gbcVerticalStrut1.gridx = 0;
        gbcVerticalStrut1.gridy = 0;
        panelGeneral.add(verticalStrut1, gbcVerticalStrut1);

        final JLabel labelCurrentHeapSize = new JLabel("$size");
        final GridBagConstraints gbcLabelCurrentHeapSize = new GridBagConstraints();
        gbcLabelCurrentHeapSize.insets = new Insets(0, 0, 5, 5);
        gbcLabelCurrentHeapSize.gridx = 1;
        gbcLabelCurrentHeapSize.gridy = 1;
        panelGeneral.add(labelCurrentHeapSize, gbcLabelCurrentHeapSize);
        labelCurrentHeapSize.setText(
                Long.valueOf(Runtime.getRuntime().maxMemory() / 1024 / 1024).toString());

        final Component verticalStrut = Box.createVerticalStrut(20);
        final GridBagConstraints gbcVerticalStrut = new GridBagConstraints();
        gbcVerticalStrut.weighty = 0.5;
        gbcVerticalStrut.insets = new Insets(0, 0, 5, 5);
        gbcVerticalStrut.gridx = 0;
        gbcVerticalStrut.gridy = 3;
        panelGeneral.add(verticalStrut, gbcVerticalStrut);

        final JLabel labelApplicationRestartRequired =
                new JLabel("(*) Application restart required.");
        labelApplicationRestartRequired.setFont(new Font("Dialog", Font.PLAIN, 11));
        final GridBagConstraints gbcLabelApplicationRestartRequired = new GridBagConstraints();
        gbcLabelApplicationRestartRequired.gridwidth = 3;
        gbcLabelApplicationRestartRequired.anchor = GridBagConstraints.ABOVE_BASELINE;
        gbcLabelApplicationRestartRequired.gridx = 0;
        gbcLabelApplicationRestartRequired.gridy = 4;
        panelGeneral.add(labelApplicationRestartRequired, gbcLabelApplicationRestartRequired);

        textHeapSize = new JTextField();
        textHeapSize.setHorizontalAlignment(SwingConstants.CENTER);
        final GridBagConstraints gbcTextHeapSize = new GridBagConstraints();
        gbcTextHeapSize.insets = new Insets(0, 0, 5, 0);
        gbcTextHeapSize.gridwidth = 2;
        gbcTextHeapSize.weightx = 1.0;
        gbcTextHeapSize.anchor = GridBagConstraints.NORTHWEST;
        gbcTextHeapSize.gridx = 1;
        gbcTextHeapSize.gridy = 2;
        gbcTextHeapSize.fill = GridBagConstraints.HORIZONTAL;
        panelGeneral.add(textHeapSize, gbcTextHeapSize);
        textHeapSize.setColumns(10);
        textHeapSize.setText(Settings.getString(Settings.Key.HEAP_SIZE));

        final JLabel labelHeapSizeText = new JLabel("Heap size* (MB):");
        labelHeapSizeText.setHorizontalAlignment(SwingConstants.CENTER);
        final GridBagConstraints gbcLabelHeapSizeText = new GridBagConstraints();
        gbcLabelHeapSizeText.insets = new Insets(0, 0, 5, 5);
        gbcLabelHeapSizeText.anchor = GridBagConstraints.NORTHWEST;
        gbcLabelHeapSizeText.gridy = 2;
        gbcLabelHeapSizeText.gridx = 0;
        gbcLabelHeapSizeText.fill = GridBagConstraints.HORIZONTAL;
        panelGeneral.add(labelHeapSizeText, gbcLabelHeapSizeText);

        final JLabel labelCurrentHeapSizeText = new JLabel("Current heap size (MB):");
        final GridBagConstraints gbcLabelCurrentHeapSizeText = new GridBagConstraints();
        gbcLabelCurrentHeapSizeText.insets = new Insets(0, 0, 5, 5);
        gbcLabelCurrentHeapSizeText.gridx = 0;
        gbcLabelCurrentHeapSizeText.gridy = 1;
        panelGeneral.add(labelCurrentHeapSizeText, gbcLabelCurrentHeapSizeText);
    }

    private void displayOkapiTokenStatus() {
        labelOkapiToken.setText("missing or offline");
        Font font = labelOkapiToken.getFont();
        labelOkapiToken.setFont(font.deriveFont(font.getStyle() | Font.ITALIC));
        buttonRequestNewToken.setVisible(true);

        final User user = User.getOkapiUser();
        try {
            if (user.getOkapiToken() != null && Okapi.getUuid(user) != null) {
                labelOkapiToken.setText("okay");
                font = labelOkapiToken.getFont();
                labelOkapiToken.setFont(font.deriveFont(font.getStyle() & ~Font.ITALIC));
                buttonRequestNewToken.setVisible(false);

                final String username = Okapi.getUsername(user);
                Settings.set(Settings.Key.OC_USERNAME, username);
                labelUsernameOc.setText(username);
            }
        } catch (Exception ignored) {
        }
    }

    private void applyChanges() {
        boolean changesWhichNeedRestart = false;

        final String newHeapSize = textHeapSize.getText();
        final String oldHeapSize = Settings.getString(Settings.Key.HEAP_SIZE);
        if ((oldHeapSize != null && !oldHeapSize.equals(newHeapSize))
                || (oldHeapSize == null && newHeapSize.length() > 0)) {
            changesWhichNeedRestart = true;
        }

        Settings.set(Settings.Key.GC_USERNAME, textUsernameGc.getText());
        Settings.set(Settings.Key.HEAP_SIZE, newHeapSize);

        if (changesWhichNeedRestart) {
            final String message =
                    "You have made changes which need cmanager to restart in order be applied.\n"
                            + "Do you want to restart "
                            + Constants.APP_NAME
                            + " now?";
            final int dialogResult =
                    JOptionPane.showConfirmDialog(
                            THIS,
                            message,
                            "Restart " + Constants.APP_NAME + " now?",
                            JOptionPane.YES_NO_OPTION);
            if (dialogResult == JOptionPane.YES_OPTION) {
                try {
                    ForkUtil.runCopyAndExit();
                } catch (Throwable t) {
                    ExceptionPanel.showErrorDialog(THIS, t);
                }
            }
        }

        THIS.setVisible(false);
    }

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
                                public void redirectUrlToUser(String authUrl) {
                                    DesktopUtil.openUrl(authUrl);
                                }
                            });
            displayOkapiTokenStatus();
        } catch (Throwable throwable) {
            ExceptionPanel.showErrorDialog(THIS, throwable);
        }
    }
}
