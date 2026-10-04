package de.applejuicenet.client.gui.plugins.versionchecker.panels;

import java.math.BigInteger;
import java.util.*;

/** Bounded observations, not a census of unique network users. */
public final class VersionStats {
    public record Row(String version, int count, Map<Integer, Integer> systems) {}
    public record Snapshot(int total, List<Row> versions, Map<Integer, Integer> systems) {}
    private record Contact(String version, int system) {}
    private final LinkedHashMap<String, Contact> contacts = new LinkedHashMap<>();
    private final int capacity;

    public VersionStats() { this(50000); }
    public VersionStats(int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException();
        this.capacity = capacity;
    }

    public synchronized void observe(String key, String version, int system) {
        String name = version == null || version.isBlank() ? "Unbekannt" : version.strip();
        contacts.put(key, new Contact(name, system >= 1 && system <= 7 ? system : 0));
        while (contacts.size() > capacity) contacts.remove(contacts.keySet().iterator().next());
    }

    public synchronized void clear() { contacts.clear(); }

    public synchronized Snapshot snapshot() {
        Map<String, Map<Integer, Integer>> versions = new HashMap<>();
        Map<Integer, Integer> systems = new TreeMap<>();
        for (var contact : contacts.values()) {
            versions.computeIfAbsent(contact.version(), k -> new TreeMap<>()).merge(contact.system(), 1, Integer::sum);
            systems.merge(contact.system(), 1, Integer::sum);
        }
        List<Row> rows = versions.entrySet().stream()
                .map(e -> new Row(e.getKey(), e.getValue().values().stream().mapToInt(Integer::intValue).sum(), Map.copyOf(e.getValue())))
                .sorted((a, b) -> compareVersions(b.version(), a.version())).toList();
        return new Snapshot(contacts.size(), rows, Map.copyOf(systems));
    }

    private static int compareVersions(String a, String b) {
        boolean numericA = a.matches("[0-9]+(\\.[0-9]+)*");
        boolean numericB = b.matches("[0-9]+(\\.[0-9]+)*");
        if (numericA != numericB) return numericA ? 1 : -1;
        if (!numericA) return a.compareToIgnoreCase(b);
        String[] partsA = a.split("\\.");
        String[] partsB = b.split("\\.");
        for (int i = 0; i < Math.max(partsA.length, partsB.length); i++) {
            BigInteger x = i < partsA.length ? new BigInteger(partsA[i]) : BigInteger.ZERO;
            BigInteger y = i < partsB.length ? new BigInteger(partsB[i]) : BigInteger.ZERO;
            int comparison = x.compareTo(y);
            if (comparison != 0) return comparison;
        }
        return a.compareTo(b);
    }
}
