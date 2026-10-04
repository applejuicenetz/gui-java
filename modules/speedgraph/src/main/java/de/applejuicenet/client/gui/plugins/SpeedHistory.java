package de.applejuicenet.client.gui.plugins;

import java.util.ArrayDeque;
import java.util.List;

/** Bounded event history, independent of component dimensions. */
final class SpeedHistory {
    record Sample(long time, long upload, long download, boolean startsSegment) {
        Sample(long time, long upload, long download) { this(time, upload, download, false); }
    }
    private boolean gap;

    synchronized void markGap() { gap = true; }
    private final ArrayDeque<Sample> samples = new ArrayDeque<>();
    private final long retention;
    private final int capacity;

    SpeedHistory(long retention, int capacity) {
        if (retention <= 0 || capacity <= 0) throw new IllegalArgumentException();
        this.retention = retention;
        this.capacity = capacity;
    }

    synchronized void add(long time, long upload, long download) {
        if (upload < 0 || download < 0) return;
        if (!samples.isEmpty() && time < samples.getLast().time()) return;
        while (!samples.isEmpty() && (samples.getFirst().time() < time - retention || samples.size() >= capacity)) {
            samples.removeFirst();
        }
        samples.addLast(new Sample(time, upload, download, gap));
        gap = false;
    }

    synchronized List<Sample> snapshot(long now, long window) {
        return samples.stream().filter(s -> s.time() >= now - window && s.time() <= now).toList();
    }
}
