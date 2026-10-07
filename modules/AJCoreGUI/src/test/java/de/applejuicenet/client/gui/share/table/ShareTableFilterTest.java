package de.applejuicenet.client.gui.share.table;

import de.applejuicenet.client.fassade.entity.Share;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class ShareTableFilterTest {
    @Test
    public void filtersCaseInsensitiveOnFullPath() {
        Share a = share("/music/Rock/Song.mp3");
        Share b = share("/video/film.avi");
        assertEquals(List.of(a), ShareTable.filterShares(List.of(a, b), "rock"));
        assertEquals(List.of(a, b), ShareTable.filterShares(List.of(a, b), ""));
        assertEquals(List.of(), ShareTable.filterShares(List.of(a, b), "xyz"));
    }

    private static Share share(String name) {
        return new Share() {
            public int getId() { return 1; }
            public String getFilename() { return name; }
            public String getShortfilename() { return name.substring(name.lastIndexOf('/') + 1); }
            public long getSize() { return 1; }
            public String getCheckSum() { return "h"; }
            public int getPrioritaet() { return 1; }
            public long getAskCount() { return 0; }
            public long getLastAsked() { return 0; }
            public long getSearchCount() { return 0; }
            public String getAjfspLink() { return ""; }
        };
    }
}
