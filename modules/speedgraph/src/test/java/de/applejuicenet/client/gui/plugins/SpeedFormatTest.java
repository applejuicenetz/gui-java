package de.applejuicenet.client.gui.plugins;

import org.junit.Test;
import java.util.Locale;
import static org.junit.Assert.*;

public class SpeedFormatTest {
    @Test public void convertsBytesWithoutOverflowAndUsesExplicitUnits() {
        assertEquals("1 MiB/s", SpeedFormat.BINARY.format(1048576, Locale.GERMANY));
        assertEquals("1,5 KiB/s", SpeedFormat.BINARY.format(1536, Locale.GERMANY));
        assertEquals("1 MB/s", SpeedFormat.DECIMAL.format(1000000, Locale.GERMANY));
        assertEquals("8 Mbit/s", SpeedFormat.BITS.format(1000000, Locale.GERMANY));
        assertEquals("0 B/s", SpeedFormat.BINARY.format(0, Locale.GERMANY));
        assertFalse(SpeedFormat.BITS.format(Long.MAX_VALUE, Locale.US).startsWith("-"));
    }
}
