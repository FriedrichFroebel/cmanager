package cmanager.gui.components;

import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

/** Utility methods for handling the application log. */
public class Logo {

    /**
     * The logo instance.
     *
     * <p>This is used to avoid having to re-create this for every call to the method.
     */
    private static ImageIcon logo = null;

    /**
     * Set the logo for the given frame.
     *
     * @param frame The frame to set the logo for.
     */
    public static void setLogo(final JFrame frame) {
        if (logo == null) {
            final URL iconURL = frame.getClass().getClassLoader().getResource("images/logo.png");
            if (iconURL != null) {
                logo = new ImageIcon(iconURL);
            }
        }

        if (logo != null) {
            frame.setIconImage(logo.getImage());
        }
    }
}
