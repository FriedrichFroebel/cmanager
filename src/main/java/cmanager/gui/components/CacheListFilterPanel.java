package cmanager.gui.components;

import cmanager.list.CacheListFilterType;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;

/** Panel for one filter. */
public abstract class CacheListFilterPanel extends JPanel {

    private static final long serialVersionUID = -6181151635761995945L;

    /** The current panel instance. */
    private final CacheListFilterPanel THIS = this;

    /** The minimum value for a range filter. */
    private JComboBox<Double> comboBoxLeft;

    /** The maximum value for a range filter. */
    private JComboBox<Double> comboBoxRight;

    /** The label for the minimum value for a range filter. */
    private JLabel labelLeft;

    /** The label for a maximum value for a range filter. */
    private JLabel labelRight;

    /** Remove the current filter. */
    private final JButton buttonRemove;

    /** Whether this filter is inverted. */
    protected boolean inverted = false;

    /** The panel holding the labels and boxes for a range filter. */
    protected JPanel panel1;

    /** Update the filter results. */
    private final JButton buttonUpdate;

    /** Button to invert the filter. */
    private final JToggleButton toggleButtonInvert;

    /** The panel holding the label and text field for a single value filter. */
    protected JPanel panel2;

    /** The label for a single value filter. */
    protected JLabel labelLeft2;

    /** The value for a single value filter. */
    protected JTextField textField;

    /** The actions to run when removing the filter. */
    private final List<Runnable> runOnRemove = new ArrayList<>();

    /**
     * The action to run to update the model.
     *
     * <p>This is used for the explicit update and inversion requests.
     */
    protected Runnable runDoModelUpdateNow = null;

    /** The actions to run when updating the filter. */
    protected final List<Runnable> runOnFilterUpdate = new ArrayList<>();

    /**
     * Create the panel.
     *
     * @param filterType The type of the filter to use for this panel.
     */
    public CacheListFilterPanel(CacheListFilterType filterType) {
        // Handle entering ENTER like clicking the update button.
        final KeyAdapter keyEnterUpdate =
                new KeyAdapter() {
                    @Override
                    public void keyPressed(KeyEvent keyEvent) {
                        if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER) {
                            buttonUpdate.doClick();
                        }
                    }
                };

        setLayout(new BorderLayout(0, 0));

        // The main panel.
        final JPanel panel = new JPanel();
        add(panel, BorderLayout.CENTER);
        panel.setLayout(new BorderLayout(0, 0));

        // The panel for the buttons (update, invert, remove).
        final JPanel panelButtons = new JPanel();
        panel.add(panelButtons, BorderLayout.EAST);
        panelButtons.setLayout(new BorderLayout(0, 0));

        final JPanel panel3 = new JPanel();
        panelButtons.add(panel3);

        buttonUpdate = new JButton("Update");
        buttonUpdate.addActionListener(
                actionEvent -> {
                    runDoModelUpdateNow.run();
                    for (final Runnable action : runOnFilterUpdate) {
                        action.run();
                    }
                });

        toggleButtonInvert = new JToggleButton("Invert");
        toggleButtonInvert.addActionListener(
                actionEvent -> {
                    inverted = toggleButtonInvert.isSelected();
                    runDoModelUpdateNow.run();
                    for (final Runnable action : runOnFilterUpdate) {
                        action.run();
                    }
                });

        buttonRemove = new JButton("X");
        buttonRemove.addActionListener(
                actionEvent -> {
                    final Container parent = THIS.getParent();
                    parent.remove(THIS);
                    parent.revalidate();

                    for (final Runnable action : runOnRemove) {
                        action.run();
                    }
                });

        panel3.add(toggleButtonInvert);
        panel3.add(buttonUpdate);
        panel3.add(buttonRemove);

        // The panel for the actual filter data.
        final JPanel panel4 = new JPanel();
        panel.add(panel4, BorderLayout.CENTER);
        panel4.setLayout(new BoxLayout(panel4, BoxLayout.Y_AXIS));

        if (filterType == CacheListFilterType.SINGLE_FILTER_VALUE) {
            // Initialize the panel.
            panel2 = new JPanel();
            panel4.add(panel2);
            panel2.setLayout(new BorderLayout(5, 10));

            // Add the label.
            labelLeft2 = new JLabel("New label");
            panel2.add(labelLeft2, BorderLayout.WEST);

            // Add the text field.
            textField = new JTextField();
            panel2.add(textField, BorderLayout.CENTER);
            textField.addKeyListener(keyEnterUpdate);
        } else if (filterType == CacheListFilterType.BETWEEN_ONE_AND_FIVE_FILTER_VALUE) {
            // The values to use.
            final Double[] values = {1.0, 1.5, 2.0, 2.5, 3.0, 3.5, 4.0, 4.5, 5.0};

            // Initialize the panel.
            panel1 = new JPanel();
            panel4.add(panel1);
            panel1.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

            // Add the minimum value settings.
            final JPanel panelLeft = new JPanel();
            panel1.add(panelLeft);
            panelLeft.setLayout(new BorderLayout(5, 0));

            labelLeft = new JLabel("Label");
            panelLeft.add(labelLeft, BorderLayout.WEST);

            comboBoxLeft = new JComboBox<>(values);
            comboBoxLeft.setMaximumRowCount(values.length);
            comboBoxLeft.addActionListener(
                    actionEvent -> {
                        runDoModelUpdateNow.run();
                        for (final Runnable action : runOnFilterUpdate) {
                            action.run();
                        }
                    });
            panelLeft.add(comboBoxLeft, BorderLayout.EAST);

            // Add the maximum value settings.
            final JPanel panelRight = new JPanel();
            panel1.add(panelRight);
            panelRight.setLayout(new BorderLayout(5, 0));

            labelRight = new JLabel("Label");
            panelRight.add(labelRight, BorderLayout.WEST);

            comboBoxRight = new JComboBox<>(values);
            comboBoxRight.setMaximumRowCount(values.length);
            comboBoxRight.setSelectedIndex(values.length - 1);
            comboBoxRight.addActionListener(
                    actionEvent -> {
                        runDoModelUpdateNow.run();
                        for (final Runnable action : runOnFilterUpdate) {
                            action.run();
                        }
                    });
            panelRight.add(comboBoxRight, BorderLayout.EAST);
        }
    }

    /**
     * Add the action to run on filter updates.
     *
     * @param action The action to add.
     */
    public void addRunOnFilterUpdate(final Runnable action) {
        runOnFilterUpdate.add(action);
    }

    /**
     * Add the action to run on filter removals.
     *
     * @param action The action to add.
     */
    public void addRemoveAction(final Runnable action) {
        runOnRemove.add(action);
    }

    /**
     * Get the label for the minimum value of a range filter.
     *
     * @return The label for the minimum value of a range filter.
     */
    protected JLabel getLabelLeft() {
        return labelLeft;
    }

    /**
     * Get the label for the maximum value of a range filter.
     *
     * @return The label for the maximum value of a range filter.
     */
    protected JLabel getLabelRight() {
        return labelRight;
    }

    /**
     * Get the button for removing the filter.
     *
     * @return The button to remove the filter.
     */
    protected JButton getButtonRemove() {
        return buttonRemove;
    }

    /**
     * Get the button to request a data update.
     *
     * @return The button to update the results.
     */
    protected JButton getButtonUpdate() {
        return buttonUpdate;
    }

    /**
     * Get the minimum value of a range filter.
     *
     * @return The minimum value for a range filter.
     */
    protected Double getValueRight() {
        return comboBoxRight.getItemAt(comboBoxRight.getSelectedIndex());
    }

    /**
     * Get the maximum value of a range filter.
     *
     * @return The maximum value for a range filter.
     */
    protected Double getValueLeft() {
        return comboBoxLeft.getItemAt(comboBoxLeft.getSelectedIndex());
    }
}
