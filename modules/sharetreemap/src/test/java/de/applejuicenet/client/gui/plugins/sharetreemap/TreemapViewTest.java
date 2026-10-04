package de.applejuicenet.client.gui.plugins.sharetreemap;

import org.junit.Test;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import static org.junit.Assert.*;

public class TreemapViewTest {
    private static void layout(Container container) {
        container.doLayout();
        for (Component child : container.getComponents()) {
            if (child instanceof Container nested) layout(nested);
        }
    }
    @Test public void rendersSizeMapAndSupportsNavigation() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            try {
                Class<?> viewClass = Class.forName("de.applejuicenet.client.gui.plugins.sharetreemap.TreemapView");
                JPanel view = (JPanel) viewClass.getConstructor().newInstance();
                ShareTree root = ShareTree.fromFiles(List.of(new String[]{"1", "/Videos/a.mkv", "60", "one"},
                        new String[]{"2", "/Audio/b.flac", "40", "two"}));
                viewClass.getMethod("setTree", ShareTree.class).invoke(view, root);
                view.setSize(840, 500); layout(view);
                BufferedImage image = new BufferedImage(840, 500, BufferedImage.TYPE_INT_RGB);
                Graphics2D g = image.createGraphics(); view.printAll(g); g.dispose();
                assertNotEquals(image.getRGB(200, 200), image.getRGB(700, 200));
                String screenshot = System.getProperty("treemap.screenshot");
                if (screenshot != null) javax.imageio.ImageIO.write(image, "png", new java.io.File(screenshot));
                viewClass.getMethod("open", ShareTree.class).invoke(view, root.children().getFirst());
                assertEquals(root.children().getFirst(), viewClass.getMethod("current").invoke(view));
                viewClass.getMethod("back").invoke(view);
                assertEquals(root, viewClass.getMethod("current").invoke(view));
            } catch (Exception e) { throw new AssertionError(e); }
        });
    }
}
