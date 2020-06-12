package cmanager.gui.components;

import cmanager.geo.GeocacheLog;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;

public class LogPanel extends JPanel {

    private static final long serialVersionUID = 6835060632928395055L;

    private JLabel labelDate;
    private JLabel labelAuthor;
    private JLabel labelType;
    private JEditorPane editorLog;

    public LogPanel(GeocacheLog log) {
        initComponents();

        labelDate.setText(log.getDateStr());
        labelAuthor.setText(log.getAuthor());
        labelType.setText(log.getTypeStr());

        editorLog.setText(log.getText());
    }

    public String getLogText() {
        return editorLog.getText();
    }

    // This has been created automatically by NetBeans IDE, but with manual cleanup afterwards.
    private void initComponents() {
        setLayout(new GridBagLayout());

        final JPanel panelMain = new JPanel();
        panelMain.setLayout(new GridBagLayout());

        GridBagConstraints gridBagConstraints;

        // Log date.
        final JLabel labelDateText = new JLabel("Date:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new Insets(12, 12, 0, 0);
        panelMain.add(labelDateText, gridBagConstraints);

        labelDate = new JLabel("Log date");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new Insets(12, 4, 0, 0);
        panelMain.add(labelDate, gridBagConstraints);

        // Log author.
        final JLabel labelAuthorText = new JLabel("Author:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new Insets(6, 12, 0, 0);
        panelMain.add(labelAuthorText, gridBagConstraints);

        labelAuthor = new JLabel("Log author");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new Insets(6, 6, 0, 0);
        panelMain.add(labelAuthor, gridBagConstraints);

        // Log type.
        final JLabel labelTypeText = new JLabel("Type:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new Insets(6, 12, 0, 0);
        panelMain.add(labelTypeText, gridBagConstraints);

        labelType = new JLabel("Log type");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new Insets(6, 4, 0, 0);
        panelMain.add(labelType, gridBagConstraints);

        // Log text.
        editorLog = new JEditorPane();
        editorLog.setText("The text of the log entry.\n\nThis can have multiple lines as well.");
        editorLog.setMinimumSize(null);
        editorLog.setPreferredSize(null);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(6, 12, 12, 15);
        panelMain.add(editorLog, gridBagConstraints);

        // Log separator.
        final JSeparator separatorLogEntry = new JSeparator();
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        panelMain.add(separatorLogEntry, gridBagConstraints);

        // Add the main panel to the parent panel.
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(panelMain, gridBagConstraints);
    }
}
