package de.applejuicenet.client.gui.controller;

import org.junit.Test;

import javax.swing.SwingUtilities;
import java.awt.Frame;
import java.awt.Rectangle;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

public class WindowPlacementTest {
    private final Rectangle primary = new Rectangle(0, 0, 1920, 1040);

    @Test
    public void preservesVisibleBoundsWithoutMutatingInput() {
        Rectangle saved = new Rectangle(200, 100, 1000, 700);
        Rectangle restored = WindowPlacement.normalize(saved, List.of(primary));
        assertEquals(saved, restored);
        assertNotSame(saved, restored);
    }

    @Test
    public void recentersWindowsFromDisconnectedMonitors() {
        assertEquals(new Rectangle(460, 170, 1000, 700),
                WindowPlacement.normalize(new Rectangle(2500, 200, 1000, 700), List.of(primary)));
    }

    @Test
    public void repairsWindowsMinimizedSentinelCoordinates() {
        assertEquals(new Rectangle(460, 170, 1000, 700),
                WindowPlacement.normalize(new Rectangle(-32000, -32000, 1000, 700), List.of(primary)));
    }

    @Test
    public void preservesNegativeCoordinatesOnConnectedMonitors() {
        Rectangle secondary = new Rectangle(-1920, -200, 1920, 1080);
        Rectangle saved = new Rectangle(-1700, -100, 1000, 700);
        assertEquals(saved, WindowPlacement.normalize(saved, List.of(primary, secondary)));
    }

    @Test
    public void keepsTitleBarAndWindowInsideUsableScreen() {
        assertEquals(new Rectangle(920, 0, 1000, 700),
                WindowPlacement.normalize(new Rectangle(1600, -200, 1000, 700), List.of(primary)));
    }

    @Test
    public void respectsTaskbarInsetsAndScreenOrigin() {
        Rectangle usable = new Rectangle(40, 30, 1880, 1010);
        assertEquals(usable,
                WindowPlacement.normalize(new Rectangle(0, 0, 1920, 1080), List.of(usable)));
    }

    @Test
    public void replacesMissingAndInvalidSizes() {
        Rectangle fallback = new Rectangle(192, 104, 1536, 832);
        assertEquals(fallback, WindowPlacement.normalize(null, List.of(primary)));
        assertEquals(fallback,
                WindowPlacement.normalize(new Rectangle(0, 0, 0, -1), List.of(primary)));
        assertEquals(fallback,
                WindowPlacement.normalize(new Rectangle(0, 0, 160, 28), List.of(primary)));
    }

    @Test
    public void fitsSmallScreens() {
        Rectangle screen = new Rectangle(0, 0, 300, 200);
        assertEquals(screen, WindowPlacement.normalize(null, List.of(screen)));
    }

    @Test
    public void choosesMonitorWithLargestIntersection() {
        Rectangle secondary = new Rectangle(1920, 0, 1920, 1040);
        assertEquals(new Rectangle(1920, 100, 1000, 700),
                WindowPlacement.normalize(new Rectangle(1800, 100, 1000, 700), List.of(primary, secondary)));
    }

    @Test
    public void restoresIconifiedWindowAndPreservesMaximizedState() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            TestFrame frame = new TestFrame();
            try {
                frame.setBounds(-32000, -32000, 800, 600);
                frame.setExtendedState(Frame.ICONIFIED | Frame.MAXIMIZED_BOTH);
                WindowPlacement.restore(frame);
                assertEquals(Frame.MAXIMIZED_BOTH, frame.getExtendedState());
                assertTrue(frame.isVisible());
                assertEquals(WindowPlacement.normalize(frame.getBounds()), frame.getBounds());
            } finally {
                frame.dispose();
            }
        });
    }

    private static class TestFrame extends Frame {
        private int state;

        @Override
        public int getExtendedState() {
            return state;
        }

        @Override
        public void setExtendedState(int state) {
            this.state = state;
        }
    }
}
