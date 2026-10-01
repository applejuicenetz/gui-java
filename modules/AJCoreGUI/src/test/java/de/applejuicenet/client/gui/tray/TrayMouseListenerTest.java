package de.applejuicenet.client.gui.tray;

import org.junit.Test;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.event.MouseEvent;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

public class TrayMouseListenerTest {
    @Test public void singleLeftClicksToggleWindowOnEdt() throws Exception {
        AtomicInteger toggles = new AtomicInteger();
        TrayMouseListener listener = new TrayMouseListener(() -> {
            assertTrue(SwingUtilities.isEventDispatchThread());
            toggles.incrementAndGet();
        }, event -> fail("Left click must not open popup"));
        for (int click = 0; click < 2; click++) {
            listener.mousePressed(event(MouseEvent.MOUSE_PRESSED, MouseEvent.BUTTON1, false, 1));
            listener.mouseReleased(event(MouseEvent.MOUSE_RELEASED, MouseEvent.BUTTON1, false, 1));
            listener.mouseClicked(event(MouseEvent.MOUSE_CLICKED, MouseEvent.BUTTON1, false, 1));
            SwingUtilities.invokeAndWait(() -> {});
            assertEquals(click + 1, toggles.get());
        }
    }

    @Test public void secondClickOfDoubleClickDoesNotUndoToggle() throws Exception {
        AtomicInteger toggles = new AtomicInteger();
        TrayMouseListener listener = new TrayMouseListener(toggles::incrementAndGet, event -> fail());
        listener.mouseClicked(event(MouseEvent.MOUSE_CLICKED, MouseEvent.BUTTON1, false, 1));
        listener.mouseClicked(event(MouseEvent.MOUSE_CLICKED, MouseEvent.BUTTON1, false, 2));
        SwingUtilities.invokeAndWait(() -> {});
        assertEquals(1, toggles.get());
    }

    @Test public void rightClickOpensPopupOnceAcrossPressAndRelease() throws Exception {
        AtomicInteger popups = new AtomicInteger();
        TrayMouseListener listener = new TrayMouseListener(() -> fail("Right click must not toggle"), event -> {
            assertTrue(SwingUtilities.isEventDispatchThread());
            assertEquals(100, event.getXOnScreen());
            assertEquals(200, event.getYOnScreen());
            popups.incrementAndGet();
        });
        listener.mousePressed(event(MouseEvent.MOUSE_PRESSED, MouseEvent.BUTTON3, true, 1));
        listener.mouseReleased(event(MouseEvent.MOUSE_RELEASED, MouseEvent.BUTTON3, true, 1));
        listener.mouseClicked(event(MouseEvent.MOUSE_CLICKED, MouseEvent.BUTTON3, false, 1));
        SwingUtilities.invokeAndWait(() -> {});
        assertEquals(1, popups.get());
    }

    @Test public void controlClickPopupDoesNotAlsoToggleWindow() throws Exception {
        assertPopupGesture(MouseEvent.MOUSE_PRESSED);
    }

    @Test public void popupTriggerOnReleaseDoesNotAlsoToggleWindow() throws Exception {
        assertPopupGesture(MouseEvent.MOUSE_RELEASED);
    }

    private void assertPopupGesture(int trigger) throws Exception {
        AtomicInteger popups = new AtomicInteger();
        AtomicInteger toggles = new AtomicInteger();
        TrayMouseListener listener = new TrayMouseListener(toggles::incrementAndGet,
                event -> popups.incrementAndGet());
        listener.mousePressed(event(MouseEvent.MOUSE_PRESSED, MouseEvent.BUTTON1,
                trigger == MouseEvent.MOUSE_PRESSED, 1));
        listener.mouseReleased(event(MouseEvent.MOUSE_RELEASED, MouseEvent.BUTTON1,
                trigger == MouseEvent.MOUSE_RELEASED, 1));
        listener.mouseClicked(event(MouseEvent.MOUSE_CLICKED, MouseEvent.BUTTON1, false, 1));
        SwingUtilities.invokeAndWait(() -> {});
        assertEquals(1, popups.get());
        assertEquals(0, toggles.get());
        listener.mousePressed(event(MouseEvent.MOUSE_PRESSED, MouseEvent.BUTTON1, false, 1));
        listener.mouseClicked(event(MouseEvent.MOUSE_CLICKED, MouseEvent.BUTTON1, false, 1));
        SwingUtilities.invokeAndWait(() -> {});
        assertEquals(1, toggles.get());
    }

    private MouseEvent event(int type, int button, boolean popupTrigger, int count) {
        return new MouseEvent(new JPanel(), type, 0, 0, 0, 0, 100, 200, count, popupTrigger, button);
    }
}
