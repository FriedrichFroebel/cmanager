package cmanager.gui.dialogs;

import cmanager.global.Constants;
import cmanager.global.Version;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class AboutDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private final AboutDialog THIS = this;

    /** Create the dialog. */
    public AboutDialog() {
        setTitle("About " + Constants.APP_NAME);

        setBounds(100, 100, 450, 300);
        getContentPane().setLayout(new BorderLayout());
        JLayeredPane contentPanel = new JLayeredPane();
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);

        final GridBagLayout gbcContentPanel = new GridBagLayout();
        gbcContentPanel.columnWidths = new int[] {0, 0};
        gbcContentPanel.rowHeights = new int[] {0, 0, 0, 0, 0, 0, 0};
        gbcContentPanel.columnWeights = new double[] {1.0, Double.MIN_VALUE};
        gbcContentPanel.rowWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
        contentPanel.setLayout(gbcContentPanel);

        final JLabel labelAppName = new JLabel(Constants.APP_NAME);
        labelAppName.setFont(new Font("Dialog", Font.BOLD, 15));
        final GridBagConstraints gbcLabelAppName = new GridBagConstraints();
        gbcLabelAppName.insets = new Insets(0, 0, 5, 0);
        gbcLabelAppName.anchor = GridBagConstraints.NORTH;
        gbcLabelAppName.gridx = 0;
        gbcLabelAppName.gridy = 0;
        contentPanel.add(labelAppName, gbcLabelAppName);

        final JLabel labelVersion = new JLabel(Version.VERSION);
        labelVersion.setFont(new Font("Dialog", Font.PLAIN, 12));
        final GridBagConstraints gbcLabelVersion = new GridBagConstraints();
        gbcLabelVersion.insets = new Insets(0, 0, 5, 0);
        gbcLabelVersion.gridx = 0;
        gbcLabelVersion.gridy = 1;
        contentPanel.add(labelVersion, gbcLabelVersion);

        final JLabel labelAuthor =
                new JLabel(
                        "<html>"
                                + "<b>Original code by</b> "
                                + "Samsung1 - jm@rq-project.net"
                                + "<br/>"
                                + "<b>Modifications by</b> "
                                + "FriedrichFr&ouml;bel"
                                + "</html>");
        labelAuthor.setFont(new Font("Dialog", Font.PLAIN, 12));
        final GridBagConstraints gbcLabelAuthor = new GridBagConstraints();
        gbcLabelAuthor.insets = new Insets(40, 0, 5, 0);
        gbcLabelAuthor.gridx = 0;
        gbcLabelAuthor.gridy = 3;
        contentPanel.add(labelAuthor, gbcLabelAuthor);

        final JLabel labelThanks =
                new JLabel("Special thanks to the great people at forum.opencaching.de.");
        labelThanks.setFont(new Font("Dialog", Font.PLAIN, 12));
        final GridBagConstraints gbcLabelThanks = new GridBagConstraints();
        gbcLabelThanks.insets = new Insets(80, 0, 5, 0);
        gbcLabelThanks.gridx = 0;
        gbcLabelThanks.gridy = 5;
        contentPanel.add(labelThanks, gbcLabelThanks);

        final JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
        getContentPane().add(buttonPane, BorderLayout.SOUTH);

        final JButton buttonClose = new JButton("Close");
        buttonClose.addActionListener(actionEvent -> THIS.setVisible(false));
        buttonClose.setActionCommand("OK");
        buttonPane.add(buttonClose);
        getRootPane().setDefaultButton(buttonClose);

        setResizable(false);
        super.pack();
    }
}
