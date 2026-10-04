package de.applejuicenet.client.gui.plugins;

import org.junit.Test;
import static org.junit.Assert.*;

public class SpeedHistoryTest {
    @Test public void expiresSamplesByTimeAndBoundsMemory() {
        SpeedHistory history = new SpeedHistory(1000, 3);
        history.add(100, 10, 100);
        history.add(500, 20, 200);
        history.add(1200, 30, 300);
        assertEquals(2, history.snapshot(1200, 1000).size());
        history.add(1300, 40, 400);
        history.add(1400, 50, 500);
        assertEquals(3, history.snapshot(1400, 1000).size());
        assertTrue(history.snapshot(3000, 1000).isEmpty());
    }
    @Test public void marksReconnectAsGapEvenWhenItWasBrief() {
        SpeedHistory history = new SpeedHistory(1000, 3);
        history.add(100, 1, 2);
        history.markGap();
        history.add(200, 3, 4);
        assertTrue(history.snapshot(200, 1000).getLast().startsSegment());
        history.add(300, 5, 6);
        assertFalse(history.snapshot(300, 1000).getLast().startsSegment());
    }

    @Test public void rejectsInvalidRatesAndPreservesImmutableSnapshots() {
        SpeedHistory history = new SpeedHistory(1000, 3);
        history.add(100, 1, 2);
        var snapshot = history.snapshot(100, 1000);
        history.add(200, -1, 2);
        assertEquals(1, history.snapshot(200, 1000).size());
        history.add(300, 3, 4);
        assertEquals(1, snapshot.size());
    }
}
