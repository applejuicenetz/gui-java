package de.applejuicenet.client.gui.options;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SpeedInputFieldTest {
    @Test
    public void formatsKbAndMb() {
        assertEquals("6104", SpeedInputField.format(6104, false));
        assertEquals("5.96", SpeedInputField.format(6104, true));
        assertEquals("0.00", SpeedInputField.format(0, true));
    }

    @Test
    public void parsesKbAndMbWithCommaOrDot() {
        assertEquals(6104, SpeedInputField.parseKb("6104", false, 1));
        assertEquals(1536, SpeedInputField.parseKb("1,5", true, 1));
        assertEquals(1536, SpeedInputField.parseKb("1.5", true, 1));
        assertEquals(2048, SpeedInputField.parseKb(" 2 ", true, 1));
    }

    @Test
    public void invalidInputKeepsFallback() {
        assertEquals(7, SpeedInputField.parseKb("", false, 7));
        assertEquals(7, SpeedInputField.parseKb("abc", true, 7));
        assertEquals(7, SpeedInputField.parseKb("-3", false, 7));
    }
}
