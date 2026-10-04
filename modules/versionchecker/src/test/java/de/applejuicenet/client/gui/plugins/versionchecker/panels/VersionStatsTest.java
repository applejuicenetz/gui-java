package de.applejuicenet.client.gui.plugins.versionchecker.panels;

import org.junit.Test;
import static org.junit.Assert.*;

public class VersionStatsTest {
    @Test public void separatesDirectionsAndReplacesChangedObservations() {
        VersionStats stats = new VersionStats();
        stats.observe("download:1", "0.31.10", 1);
        stats.observe("upload:1", "0.31.10", 2);
        stats.observe("download:1", "0.31.10", 1);
        assertEquals(2, stats.snapshot().total());
        stats.observe("download:1", "0.31.11", 3);
        assertEquals(2, stats.snapshot().total());
        assertEquals(2, stats.snapshot().versions().size());
        assertEquals(0, stats.snapshot().systems().getOrDefault(1, 0).intValue());
        stats.clear();
        assertEquals(0, stats.snapshot().total());
    }
    @Test public void sortsNumericVersionsSafelyAndBoundsSessionMemory() {
        VersionStats stats = new VersionStats(3);
        stats.observe("a", "0.31.9", 2);
        stats.observe("b", "0.31.10", 2);
        stats.observe("c", "beta", 99);
        assertEquals("0.31.10", stats.snapshot().versions().getFirst().version());
        stats.observe("d", null, 0);
        assertEquals(3, stats.snapshot().total());
        assertEquals(2, stats.snapshot().systems().get(0).intValue());
        assertTrue(stats.snapshot().versions().stream().anyMatch(v -> v.version().equals("Unbekannt")));
    }
}
