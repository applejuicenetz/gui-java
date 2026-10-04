package de.applejuicenet.client.gui.plugins.sharetreemap;

import org.junit.Test;
import java.util.Map;
import static org.junit.Assert.*;

public class UploadActivityTest {
    @Test public void countsOnlyPositiveObservedDeltasAndResetsBaselineAcrossGaps() throws Exception {
        Class<?> type = Class.forName("de.applejuicenet.client.gui.plugins.sharetreemap.UploadActivity");
        Object tracker = type.getConstructor().newInstance();
        var update = type.getMethod("observe", int.class, int.class, long.class, long.class);
        update.invoke(tracker, 1, 20, 0L, 1000L);
        update.invoke(tracker, 1, 20, 0L, 1250L);
        update.invoke(tracker, 1, 20, 0L, 1250L);
        assertEquals(Map.of(20, 250L), type.getMethod("snapshot").invoke(tracker));
        update.invoke(tracker, 1, 20, 5000L, 5100L);
        assertEquals(Map.of(20, 250L), type.getMethod("snapshot").invoke(tracker));
        type.getMethod("gap").invoke(tracker);
        update.invoke(tracker, 1, 20, 0L, 9000L);
        assertEquals(Map.of(20, 250L), type.getMethod("snapshot").invoke(tracker));
        type.getMethod("clear").invoke(tracker);
        update.invoke(tracker, 1, 20, 0L, 9000L);
        assertEquals(Map.of(), type.getMethod("snapshot").invoke(tracker));
    }
}
