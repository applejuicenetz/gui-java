package de.applejuicenet.client.gui.share;

import org.junit.Test;

import java.util.Locale;

import static org.junit.Assert.assertEquals;

public class ShareSizeFormatterTest {
    @Test
    public void formatsEmptyShareAndSmallSizesAsBytes() {
        assertEquals("0 B", ShareSizeFormatter.format(0, Locale.GERMANY));
        assertEquals("1023 B", ShareSizeFormatter.format(1023, Locale.GERMANY));
    }

    @Test
    public void selectsBinaryUnitsAtExactBoundaries() {
        assertEquals("1,00 KiB", ShareSizeFormatter.format(1L << 10, Locale.GERMANY));
        assertEquals("1,00 MiB", ShareSizeFormatter.format(1L << 20, Locale.GERMANY));
        assertEquals("1,00 GiB", ShareSizeFormatter.format(1L << 30, Locale.GERMANY));
        assertEquals("1,00 TiB", ShareSizeFormatter.format(1L << 40, Locale.GERMANY));
        assertEquals("1,00 PiB", ShareSizeFormatter.format(1L << 50, Locale.GERMANY));
        assertEquals("1,00 EiB", ShareSizeFormatter.format(1L << 60, Locale.GERMANY));
    }

    @Test
    public void formatsGigabyteSharesWithoutIntegerOverflow() {
        assertEquals("5,22 GiB", ShareSizeFormatter.format(5604932649L, Locale.GERMANY));
    }

    @Test
    public void roundsReportedShareAndNasSizesIndependently() {
        assertEquals("5,28 TiB", ShareSizeFormatter.format(5802653279846L, Locale.GERMANY));
        assertEquals("5,27 TiB", ShareSizeFormatter.format(5797860212138L, Locale.GERMANY));
    }

    @Test
    public void respectsLocaleDecimalSeparator() {
        assertEquals("5.28 TiB", ShareSizeFormatter.format(5802653279846L, Locale.US));
    }

    @Test
    public void formatsLargestLongSize() {
        assertEquals("8,00 EiB", ShareSizeFormatter.format(Long.MAX_VALUE, Locale.GERMANY));
    }
}
