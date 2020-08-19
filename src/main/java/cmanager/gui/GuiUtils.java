package cmanager.gui;

import java.awt.event.ActionListener;
import javax.swing.JButton;

/** Utility methods regarding the GUI. */
public class GuiUtils {

    /** Remove all action listeners from the given button. */
    public static void removeActionListeners(final JButton button) {
        final ActionListener[] actionListeners = button.getActionListeners();
        for (final ActionListener actionListener : actionListeners) {
            button.removeActionListener(actionListener);
        }
    }
}
