package de.applejuicenet.client.gui.share;

import de.applejuicenet.client.facade.entity.Share;
import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Collectors;

final class ShareListFormat {
    private ShareListFormat() {
    }

    static Share[] sorted(Share[] shares, boolean descending) {
        Share[] copy = shares.clone();
        Comparator<Share> byName = Comparator.comparing(Share::getShortfilename, String.CASE_INSENSITIVE_ORDER);
        Arrays.sort(copy, descending ? byName.reversed() : byName);
        return copy;
    }

    static String ajfsp(Share[] shares) {
        return ajfsp(shares, false);
    }

    static String ajfsp(Share[] shares, boolean descending) {
        return Arrays.stream(sorted(shares, descending)).map(Share::getAjfspLink).collect(Collectors.joining("\r\n"));
    }

    static String ajl(Share[] shares) {
        return ajl(shares, false);
    }

    static String ajl(Share[] shares, boolean descending) {
        StringBuilder text = new StringBuilder("\r\nYou need an appleJuice GUI to open this file. You can get one here: "
                + "https://applejuicenetz.github.io\r\n\r\n"
                + "Do not modify this file!\r\n-----\r\n100\r\n");
        for (Share share : sorted(shares, descending)) {
            text.append(share.getShortfilename()).append("\r\n")
                    .append(share.getCheckSum()).append("\r\n")
                    .append(share.getSize()).append("\r\n");
        }
        return text.toString();
    }
}
