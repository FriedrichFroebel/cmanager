package cmanager.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

/** Dialog to show while a long running task is active. */
public class WaitDialog extends JDialog {

    private static final long serialVersionUID = -1281433856562055074L;

    /** Create the dialog. */
    public WaitDialog() {
        setBounds(100, 100, 450, 100);
        setUndecorated(true);
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        getContentPane().setLayout(new BorderLayout());
        final JPanel contentPanel = new JPanel();
        contentPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(new BorderLayout(0, 0));

        final JLabel labelPleaseStandBy = new JLabel("Please stand by...");
        labelPleaseStandBy.setHorizontalAlignment(SwingConstants.CENTER);
        contentPanel.add(labelPleaseStandBy);
    }
}
