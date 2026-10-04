package de.applejuicenet.client.gui.plugins.sharetreemap;

import org.junit.Test;
import java.util.List;
import static org.junit.Assert.*;

public class ShareTreeTest {
    @Test public void groupsCorePathsWithoutAccessingLocalFiles() throws Exception {
        // Reflection keeps RED executable before the production class exists.
        Class<?> model = Class.forName("de.applejuicenet.client.gui.plugins.sharetreemap.ShareTree");
        Object root = model.getMethod("fromFiles", List.class).invoke(null, List.of(
                new String[]{"1", "C:\\Share\\Videos\\film.mkv", "60", "ajfsp://one"},
                new String[]{"2", "C:\\Share\\Musik\\song.flac", "40", "ajfsp://two"}));
        assertEquals(100L, root.getClass().getMethod("bytes").invoke(root));
        assertEquals(1, ((List<?>) root.getClass().getMethod("children").invoke(root)).size());
    }
}
