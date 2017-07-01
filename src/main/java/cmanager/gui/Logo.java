package cmanager.gui;

import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import cmanager.util.ForkUtil;

public class Logo
{
    private static ImageIcon logo = null;

    public static void setLogo(JFrame frame)
    {
        if (logo == null)
        {
            URL iconURL = frame.getClass().getClassLoader().getResource("images/logo.jpg");
            if (iconURL != null)
            {
                logo = new ImageIcon(iconURL);
            }
        }

        if (logo != null) {
            frame.setIconImage(logo.getImage());
        }
    }
}
