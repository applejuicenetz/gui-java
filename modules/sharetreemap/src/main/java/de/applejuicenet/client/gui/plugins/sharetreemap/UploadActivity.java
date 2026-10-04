package de.applejuicenet.client.gui.plugins.sharetreemap;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/** Lower bound: only positive position deltas between observed samples count. */
public class UploadActivity {
    private final Map<Integer, Sample> previous = new HashMap<>();
    private final Map<Integer, Long> totals = new HashMap<>();

    public synchronized void observe(int uploadId, int shareId, long from, long position) {
        if (shareId < 0 || from < 0 || position < from) return;
        Sample sample = new Sample(shareId, from, position);
        Sample old = previous.put(uploadId, sample);
        if (old == null || old.shareId != shareId || old.from != from || position <= old.position) return;
        long delta = position - old.position;
        long total = totals.getOrDefault(shareId, 0L);
        totals.put(shareId, Long.MAX_VALUE - total < delta ? Long.MAX_VALUE : total + delta);
    }
    public synchronized void retain(Set<Integer> ids) { previous.keySet().retainAll(ids); }
    public synchronized Map<Integer, Long> snapshot() { return Map.copyOf(totals); }
    public synchronized void gap() { previous.clear(); }
    public synchronized void clear() { previous.clear(); totals.clear(); }
    private record Sample(int shareId, long from, long position) { }
}
