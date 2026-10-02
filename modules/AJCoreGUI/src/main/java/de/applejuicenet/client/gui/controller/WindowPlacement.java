package de.applejuicenet.client.gui.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.List;

public final class WindowPlacement {
    private static final Logger logger = LoggerFactory.getLogger(WindowPlacement.class);

    private WindowPlacement() {
    }

    public static Rectangle normalize(Rectangle bounds) {
        GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        List<Rectangle> screens = new ArrayList<>();
        screens.add(usableBounds(environment.getDefaultScreenDevice()));
        for (GraphicsDevice device : environment.getScreenDevices()) {
            if (device != environment.getDefaultScreenDevice()) {
                screens.add(usableBounds(device));
            }
        }
        return normalize(bounds, screens);
    }

    static Rectangle normalize(Rectangle bounds, List<Rectangle> screens) {
        Rectangle screen = screens.getFirst();
        long largestIntersection = 0;
        if (bounds != null && bounds.width > 0 && bounds.height > 0) {
            for (Rectangle candidate : screens) {
                Rectangle intersection = candidate.intersection(bounds);
                long area = intersection.isEmpty() ? 0 : (long) intersection.width * intersection.height;
                if (area > largestIntersection) {
                    largestIntersection = area;
                    screen = candidate;
                }
            }
        }

        if (bounds == null || bounds.width < 320 || bounds.height < 240) {
            int width = Math.min(screen.width, Math.max(320, screen.width * 4 / 5));
            int height = Math.min(screen.height, Math.max(240, screen.height * 4 / 5));
            return new Rectangle(screen.x + (screen.width - width) / 2,
                    screen.y + (screen.height - height) / 2, width, height);
        }

        int width = Math.min(bounds.width, screen.width);
        int height = Math.min(bounds.height, screen.height);
        int locationX = largestIntersection == 0 ? screen.x + (screen.width - width) / 2
                : Math.max(screen.x, Math.min(bounds.x, screen.x + screen.width - width));
        int locationY = largestIntersection == 0 ? screen.y + (screen.height - height) / 2
                : Math.max(screen.y, Math.min(bounds.y, screen.y + screen.height - height));
        return new Rectangle(locationX, locationY, width, height);
    }

    public static void restore(Frame frame) {
        Rectangle bounds = frame.getBounds();
        Rectangle restored = normalize(bounds);
        if (!restored.equals(bounds)) {
            logger.warn("Repairing main window bounds: {} to {}", bounds, restored);
            frame.setBounds(restored);
        }
        frame.setVisible(true);
        int state = frame.getExtendedState();
        if ((state & Frame.ICONIFIED) != 0) {
            logger.info("Restoring iconified main window");
            frame.setExtendedState(state & ~Frame.ICONIFIED);
        }
        frame.toFront();
        frame.requestFocus();
    }

    private static Rectangle usableBounds(GraphicsDevice device) {
        GraphicsConfiguration configuration = device.getDefaultConfiguration();
        Rectangle bounds = new Rectangle(configuration.getBounds());
        Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(configuration);
        bounds.x += insets.left;
        bounds.y += insets.top;
        bounds.width -= insets.left + insets.right;
        bounds.height -= insets.top + insets.bottom;
        return bounds;
    }
}
