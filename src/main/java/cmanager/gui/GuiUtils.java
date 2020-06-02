package cmanager.gui;

import java.awt.event.ActionListener;
import javax.swing.JButton;

public class GuiUtils {

    public static void removeActionListeners(final JButton button) {
        final ActionListener[] actionListeners = button.getActionListeners();
        for (final ActionListener actionListener : actionListeners) {
            button.removeActionListener(actionListener);
        }
    }
}
