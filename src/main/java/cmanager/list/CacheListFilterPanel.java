package cmanager;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

public abstract class CacheListFilterPanel extends JPanel {

    protected enum FILTER_TYPE {
        BETWEEN_ONE_AND_FIVE_FILTER_VALUE,
        SINGLE_FILTER_VALUE
    }

    private static final long serialVersionUID = -6181151635761995945L;

    private final CacheListFilterPanel THIS = this;
    private JComboBox<Double> comboBoxLeft;
    private JComboBox<Double> comboBoxRight;
    private JLabel labelLeft;
    private JLabel labelRight;
    private final JButton buttonRemove;

    protected boolean inverted = false;
    protected JPanel panel1;
    private final JButton buttonUpdate;
    private final JToggleButton toggleButtonInvert;
    protected JPanel panel2;
    protected JLabel labelLeft2;
    protected JTextField textField;

    private final List<Runnable> runOnRemove = new ArrayList<>();
    protected Runnable runDoModelUpdateNow = null;
    protected final List<Runnable> runOnFilterUpdate = new ArrayList<>();

    /** Create the panel. */
    public CacheListFilterPanel(FILTER_TYPE filterType) {
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

        final JPanel panel = new JPanel();
        add(panel, BorderLayout.CENTER);
        panel.setLayout(new BorderLayout(0, 0));

        final JPanel panelButtons = new JPanel();
        panel.add(panelButtons, BorderLayout.EAST);
        panelButtons.setLayout(new BorderLayout(0, 0));

        final JPanel panel3 = new JPanel();
        panelButtons.add(panel3);

        buttonUpdate = new JButton("Update");
        buttonUpdate.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent actionEvent) {
                        runDoModelUpdateNow.run();
                        for (final Runnable action : runOnFilterUpdate) {
                            action.run();
                        }
                    }
                });

        toggleButtonInvert = new JToggleButton("Invert");
        toggleButtonInvert.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent actionEvent) {
                        inverted = toggleButtonInvert.isSelected();
                        runDoModelUpdateNow.run();
                        for (final Runnable action : runOnFilterUpdate) {
                            action.run();
                        }
                    }
                });

        buttonRemove = new JButton("X");
        buttonRemove.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent actionEvent) {
                        final Container parent = THIS.getParent();
                        parent.remove(THIS);
                        parent.revalidate();

                        for (final Runnable action : runOnRemove) {
                            action.run();
                        }
                    }
                });

        panel3.add(toggleButtonInvert);
        panel3.add(buttonUpdate);
        panel3.add(buttonRemove);

        final JPanel panel4 = new JPanel();
        panel.add(panel4, BorderLayout.CENTER);
        panel4.setLayout(new BoxLayout(panel4, BoxLayout.Y_AXIS));

        if (filterType == FILTER_TYPE.SINGLE_FILTER_VALUE) {
            panel2 = new JPanel();
            panel4.add(panel2);
            panel2.setLayout(new BorderLayout(5, 10));

            labelLeft2 = new JLabel("New label");
            panel2.add(labelLeft2, BorderLayout.WEST);

            textField = new JTextField();
            panel2.add(textField, BorderLayout.CENTER);
            textField.addKeyListener(keyEnterUpdate);
        } else if (filterType == FILTER_TYPE.BETWEEN_ONE_AND_FIVE_FILTER_VALUE) {
            final Double[] values = {1.0, 1.5, 2.0, 2.5, 3.0, 3.5, 4.0, 4.5, 5.0};
            panel1 = new JPanel();
            panel4.add(panel1);
            panel1.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

            final JPanel panelLeft = new JPanel();
            panel1.add(panelLeft);
            panelLeft.setLayout(new BorderLayout(5, 0));

            labelLeft = new JLabel("Label");
            panelLeft.add(labelLeft, BorderLayout.WEST);

            comboBoxLeft = new JComboBox<>(values);
            comboBoxLeft.setMaximumRowCount(values.length);
            comboBoxLeft.addActionListener(
                    new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent actionEvent) {
                            runDoModelUpdateNow.run();
                            for (final Runnable action : runOnFilterUpdate) {
                                action.run();
                            }
                        }
                    });
            panelLeft.add(comboBoxLeft, BorderLayout.EAST);

            final JPanel panelRight = new JPanel();
            panel1.add(panelRight);
            panelRight.setLayout(new BorderLayout(5, 0));

            labelRight = new JLabel("Label");
            panelRight.add(labelRight, BorderLayout.WEST);

            comboBoxRight = new JComboBox<>(values);
            comboBoxRight.setMaximumRowCount(values.length);
            comboBoxRight.setSelectedIndex(values.length - 1);
            comboBoxRight.addActionListener(
                    new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent actionEvent) {
                            runDoModelUpdateNow.run();
                            for (final Runnable action : runOnFilterUpdate) {
                                action.run();
                            }
                        }
                    });
            panelRight.add(comboBoxRight, BorderLayout.EAST);
        }
    }

    public void addRunOnFilterUpdate(Runnable action) {
        runOnFilterUpdate.add(action);
    }

    public void addRemoveAction(Runnable action) {
        runOnRemove.add(action);
    }

    protected JLabel getLabelLeft() {
        return labelLeft;
    }

    protected JLabel getLabelRight() {
        return labelRight;
    }

    protected JButton getButtonRemove() {
        return buttonRemove;
    }

    protected JButton getButtonUpdate() {
        return buttonUpdate;
    }

    protected Double getValueRight() {
        return comboBoxRight.getItemAt(comboBoxRight.getSelectedIndex());
    }

    protected Double getValueLeft() {
        return comboBoxLeft.getItemAt(comboBoxLeft.getSelectedIndex());
    }
}
