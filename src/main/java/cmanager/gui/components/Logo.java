package cmanager.gui.components;

import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class Logo {

    private static ImageIcon logo = null;

    public static void setLogo(JFrame frame) {
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
