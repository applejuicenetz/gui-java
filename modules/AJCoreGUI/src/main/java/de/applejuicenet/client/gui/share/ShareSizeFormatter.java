package de.applejuicenet.client.gui.share;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

final class ShareSizeFormatter {
    private static final String[] UNITS = {"B", "KiB", "MiB", "GiB", "TiB", "PiB", "EiB"};

    private ShareSizeFormatter() {
    }

    static String format(long bytes) {
        return format(bytes, Locale.getDefault(Locale.Category.FORMAT));
    }

    static String format(long bytes, Locale locale) {
        if (bytes < 1024) {
            return bytes + " B";
        }

        double size = bytes;
        int unit = 0;
        while (size >= 1024 && unit < UNITS.length - 1) {
            size /= 1024;
            unit++;
        }
        DecimalFormat formatter = new DecimalFormat("0.00", DecimalFormatSymbols.getInstance(locale));
        return formatter.format(size) + " " + UNITS[unit];
    }
}
