package de.applejuicenet.client.gui;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ConnectionStatusTextTest {
    @Test
    public void showsExistingConnectionCountToTheRightOfStatus() {
        assertEquals("Verbunden | 42 offene Verbindungen",
                AppleJuiceDialog.getConnectionStatusText("Verbunden", "%d offene Verbindungen", 42));
    }

    @Test
    public void zeroAndChangingCountsAreDisplayedWithoutStaleValues() {
        assertEquals("Verbunden | 12 offene Verbindungen",
                AppleJuiceDialog.getConnectionStatusText("Verbunden", "%d offene Verbindungen", 12));
        assertEquals("Verbunden | 3 offene Verbindungen",
                AppleJuiceDialog.getConnectionStatusText("Verbunden", "%d offene Verbindungen", 3));
        assertEquals("Nicht verbunden | 0 offene Verbindungen",
                AppleJuiceDialog.getConnectionStatusText("Nicht verbunden", "%d offene Verbindungen", 0));
    }

    @Test
    public void keepsTranslatedCaptionAndConnectionStatus() {
        assertEquals("Connected | Open connections: 7",
                AppleJuiceDialog.getConnectionStatusText("Connected", "Open connections: %d", 7));
    }
}
