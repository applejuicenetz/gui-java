package de.applejuicenet.client.gui.share;

import de.applejuicenet.client.facade.entity.Share;
import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Collectors;

final class ShareListFormat {
    private ShareListFormat() {
    }

    private static String nameOf(Share share) {
        String name = share.getShortfilename();
        return name == null ? "" : name;
    }

    /** Line breaks would shift the three-lines-per-entry layout of an AJL file, so they become spaces. */
    static String singleLine(String text) {
        return text.replace("\r\n", " ").replace('\r', ' ').replace('\n', ' ').strip();
    }

    private static boolean exportable(Share share) {
        return share != null && !singleLine(nameOf(share)).isEmpty()
                && share.getCheckSum() != null && !share.getCheckSum().isBlank();
    }

    static Share[] sorted(Share[] shares, boolean descending) {
        Share[] copy = Arrays.stream(shares).filter(java.util.Objects::nonNull).toArray(Share[]::new);
        Comparator<Share> byName = Comparator.comparing(ShareListFormat::nameOf, String.CASE_INSENSITIVE_ORDER);
        Arrays.sort(copy, descending ? byName.reversed() : byName);
        return copy;
    }

    static String ajfsp(Share[] shares) {
        return ajfsp(shares, false);
    }

    static String ajfsp(Share[] shares, boolean descending) {
        return Arrays.stream(sorted(shares, descending)).map(Share::getAjfspLink).filter(java.util.Objects::nonNull)
                .collect(Collectors.joining("\r\n"));
    }

    static String ajl(Share[] shares) {
        return ajl(shares, false);
    }

    static String ajl(Share[] shares, boolean descending) {
        StringBuilder text = new StringBuilder("\r\nYou need an appleJuice GUI to open this file. You can get one here: "
                + "https://applejuicenetz.github.io\r\n\r\n"
                + "Do not modify this file!\r\n-----\r\n100\r\n");
        for (Share share : sorted(shares, descending)) {
            if (!exportable(share)) {
                continue;
            }
            text.append(singleLine(nameOf(share))).append("\r\n")
                    .append(singleLine(share.getCheckSum())).append("\r\n")
                    .append(share.getSize()).append("\r\n");
        }
        return text.toString();
    }
}
