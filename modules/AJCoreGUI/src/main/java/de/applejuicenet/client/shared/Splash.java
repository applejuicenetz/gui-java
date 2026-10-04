package de.applejuicenet.client.shared;

import javax.swing.*;
import java.awt.*;

public class Splash extends JDialog {
    public Splash(Frame parent, ImageIcon icon) {
        super(parent);
        Rectangle screen = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();

        setUndecorated(true);
        if (getGraphicsConfiguration().getDevice().isWindowTranslucencySupported(
                GraphicsDevice.WindowTranslucency.PERPIXEL_TRANSLUCENT)) {
            setBackground(new Color(0, 0, 0, 0));
        }
        JLabel logo = new JLabel(icon);
        logo.setOpaque(false);
        setContentPane(logo);
        getRootPane().setOpaque(false);
        pack();
        setLocation(screen.x + (screen.width - getWidth()) / 2,
                screen.y + (screen.height - getHeight()) / 3);
    }
}
