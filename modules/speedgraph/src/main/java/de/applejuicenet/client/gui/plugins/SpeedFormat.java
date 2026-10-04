package de.applejuicenet.client.gui.plugins;

import java.text.NumberFormat;
import java.util.Locale;

/** Rates remain bytes/second; conversion is presentation only. */
public enum SpeedFormat {
    BINARY("Bytes (1024)", 1024, 1, new String[]{"B/s", "KiB/s", "MiB/s", "GiB/s", "TiB/s", "PiB/s", "EiB/s"}),
    DECIMAL("Bytes (1000)", 1000, 1, new String[]{"B/s", "kB/s", "MB/s", "GB/s", "TB/s", "PB/s", "EB/s"}),
    BITS("Bits (1000)", 1000, 8, new String[]{"bit/s", "kbit/s", "Mbit/s", "Gbit/s", "Tbit/s", "Pbit/s", "Ebit/s"});

    private final String title;
    private final int base;
    private final int multiplier;
    private final String[] units;

    SpeedFormat(String title, int base, int multiplier, String[] units) {
        this.title = title;
        this.base = base;
        this.multiplier = multiplier;
        this.units = units;
    }

    public int unit(double bytes) {
        double value = Math.max(0, bytes) * multiplier;
        int index = 0;
        while (value >= base && index < units.length - 1) {
            value /= base;
            index++;
        }
        return index;
    }

    public String format(double bytes, Locale locale) {
        return format(bytes, unit(bytes), locale);
    }

    public String format(double bytes, int unit, Locale locale) {
        NumberFormat numbers = NumberFormat.getNumberInstance(locale);
        numbers.setMaximumFractionDigits(2);
        numbers.setGroupingUsed(false);
        return numbers.format(Math.max(0, bytes) * multiplier / Math.pow(base, unit)) + " " + units[unit];
    }

    @Override public String toString() { return title; }
}
