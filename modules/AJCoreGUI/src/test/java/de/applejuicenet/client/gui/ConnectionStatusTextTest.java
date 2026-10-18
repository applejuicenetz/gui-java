package de.applejuicenet.client.gui;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ConnectionStatusTextTest {
    @Test
    public void showsConnectionCountWithCaption() {
        assertEquals("42 Verbindungen", AppleJuiceDialog.getConnectionCountText("%d Verbindungen", 42));
    }

    @Test
    public void zeroAndChangingCountsAreDisplayedWithoutStaleValues() {
        assertEquals("12 Verbindungen", AppleJuiceDialog.getConnectionCountText("%d Verbindungen", 12));
        assertEquals("3 Verbindungen", AppleJuiceDialog.getConnectionCountText("%d Verbindungen", 3));
        assertEquals("0 Verbindungen", AppleJuiceDialog.getConnectionCountText("%d Verbindungen", 0));
    }

    @Test
    public void keepsTranslatedCaption() {
        assertEquals("7 connections", AppleJuiceDialog.getConnectionCountText("%d connections", 7));
    }
}
