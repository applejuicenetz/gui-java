package de.applejuicenet.client.facade;

import de.applejuicenet.client.facade.shared.RefreshIntervals;
import org.junit.Test;

import static org.junit.Assert.*;

public class RefreshIntervalsTest {
    @Test
    public void defaultsLieInsideTheirRange() {
        assertTrue(RefreshIntervals.CORE_POLL_DEFAULT_MS >= RefreshIntervals.CORE_POLL_MIN_MS);
        assertTrue(RefreshIntervals.CORE_POLL_DEFAULT_MS <= RefreshIntervals.CORE_POLL_MAX_MS);
        assertTrue(RefreshIntervals.PARTLIST_REFRESH_DEFAULT_MS >= RefreshIntervals.PARTLIST_REFRESH_MIN_MS);
        assertTrue(RefreshIntervals.PARTLIST_REFRESH_DEFAULT_MS <= RefreshIntervals.PARTLIST_REFRESH_MAX_MS);
    }

    @Test
    public void clampKeepsValuesInsideTheRange() {
        assertEquals(500, RefreshIntervals.clamp(1, 500, 10000));
        assertEquals(10000, RefreshIntervals.clamp(Long.MAX_VALUE, 500, 10000));
        assertEquals(2000, RefreshIntervals.clamp(2000, 500, 10000));
        assertEquals(500, RefreshIntervals.clamp(Long.MIN_VALUE, 500, 10000));
    }

    @Test
    public void workersReadTheCurrentValueInsteadOfACopy() {
        long original = RefreshIntervals.partlistRefreshMs();
        try {
            RefreshIntervals.setPartlistRefreshMs(4321);
            assertEquals(4321, RefreshIntervals.partlistRefreshMs());
            RefreshIntervals.setPartlistRefreshMs(1);
            assertEquals(RefreshIntervals.PARTLIST_REFRESH_MIN_MS, RefreshIntervals.partlistRefreshMs());
        } finally {
            RefreshIntervals.setPartlistRefreshMs(original);
        }
    }
}
