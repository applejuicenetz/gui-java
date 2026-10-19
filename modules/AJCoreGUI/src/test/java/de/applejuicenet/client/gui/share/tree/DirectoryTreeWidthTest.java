package de.applejuicenet.client.gui.share.tree;

import org.junit.Test;

import javax.swing.*;
import java.awt.*;

import static org.junit.Assert.assertEquals;

public class DirectoryTreeWidthTest {
    @Test
    public void directoryTreePrefersAWideColumn() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            Dimension preferred = new DirectoryTree().getPreferredScrollableViewportSize();

            assertEquals(240, preferred.width);
        });
    }
}
