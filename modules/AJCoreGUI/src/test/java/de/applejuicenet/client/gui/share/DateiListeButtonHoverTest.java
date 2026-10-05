package de.applejuicenet.client.gui.share;

import de.applejuicenet.client.gui.controller.LanguageSelector;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class DateiListeButtonHoverTest {
    @BeforeClass
    public static void loadLanguage() {
        LanguageSelector.getInstance(Paths.get("../../resources/language/deutsch.properties")
                .toAbsolutePath().normalize().toString());
    }

    @Test
    public void hoverDoesNotChangeButtonSizeOrLayout() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            JFrame owner = new JFrame();
            DateiListeDialog dialog = new DateiListeDialog(owner, false);
            try {
                List<JLabel> buttons = new ArrayList<>();
                collect(dialog, buttons);
                assertEquals(3, buttons.size());
                for (JLabel button : buttons) {
                    Dimension before = button.getPreferredSize();
                    Dimension parentBefore = button.getParent().getPreferredSize();
                    MouseEvent entered = new MouseEvent(button, MouseEvent.MOUSE_ENTERED, 0, 0, 1, 1, 0, false);
                    for (MouseListener l : button.getMouseListeners()) {
                        l.mouseEntered(entered);
                    }
                    assertEquals(before, button.getPreferredSize());
                    assertEquals(parentBefore, button.getParent().getPreferredSize());
                    MouseEvent exited = new MouseEvent(button, MouseEvent.MOUSE_EXITED, 0, 0, 1, 1, 0, false);
                    for (MouseListener l : button.getMouseListeners()) {
                        l.mouseExited(exited);
                    }
                    assertEquals(before, button.getPreferredSize());
                }
            } finally {
                dialog.dispose();
                owner.dispose();
            }
        });
    }

    private static void collect(Container container, List<JLabel> out) {
        for (Component c : container.getComponents()) {
            if (c instanceof JLabel label && label.getMouseListeners().length > 0) {
                out.add(label);
            }
            if (c instanceof Container child) {
                collect(child, out);
            }
        }
    }
}
