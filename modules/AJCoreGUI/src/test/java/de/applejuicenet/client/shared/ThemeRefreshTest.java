package de.applejuicenet.client.shared;

import com.formdev.flatlaf.FlatLightLaf;
import com.jgoodies.looks.plastic.Plastic3DLookAndFeel;
import org.junit.Test;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.Component;
import java.awt.Container;
import java.awt.GraphicsEnvironment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ThemeRefreshTest {
    @Test
    public void plasticComboButtonsAreRemovedBeforeTheTreeUpdate() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            LookAndFeel previous = UIManager.getLookAndFeel();
            JFrame frame = null;
            try {
                UIManager.setLookAndFeel(new Plastic3DLookAndFeel());
                frame = new JFrame("ThemeRefreshTest");
                JComboBox<String> plain = new JComboBox<>(new String[] {"a"});
                JComboBox<String> editable = new JComboBox<>(new String[] {"b"});
                editable.setEditable(true);
                frame.getContentPane().add(plain, java.awt.BorderLayout.NORTH);
                frame.getContentPane().add(editable, java.awt.BorderLayout.SOUTH);
                frame.pack();
                if (!GraphicsEnvironment.isHeadless()) {
                    frame.setVisible(true);
                }
                assertTrue("Plastic installs its own combo button", hasChild(plain, "PlasticComboBoxButton"));
                assertTrue(hasChild(editable, "PlasticComboBoxButton"));

                UIManager.setLookAndFeel(new FlatLightLaf());
                ThemeRefresh.refresh(frame);

                assertFalse("stale Plastic combo button left in the tree",
                        hasChild(plain, "PlasticComboBoxButton"));
                assertFalse(hasChild(editable, "PlasticComboBoxButton"));
                assertTrue(plain.getUI() instanceof com.formdev.flatlaf.ui.FlatComboBoxUI);
                assertEquals(2, plain.getComponentCount());
                assertEquals(3, editable.getComponentCount());
            } catch (Exception failure) {
                throw new AssertionError(failure);
            } finally {
                if (frame != null) {
                    frame.dispose();
                }
                try {
                    UIManager.setLookAndFeel(previous);
                } catch (Exception failure) {
                    throw new AssertionError(failure);
                }
            }
        });
    }

    private static boolean hasChild(Container container, String className) {
        for (Component child : container.getComponents()) {
            if (child.getClass().getName().contains(className)) {
                return true;
            }
        }
        return false;
    }
}
