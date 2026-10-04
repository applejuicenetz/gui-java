package de.applejuicenet.client.gui.plugins.logviewer;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.function.Predicate;

final class LogRetention {
    record Result(List<File> files, int deleted, int failed) {}

    static Result clean(Path directory, int limit, Predicate<File> active) throws IOException {
        if (!Files.exists(directory)) return new Result(List.of(), 0, 0);
        List<File> files;
        try (var entries = Files.list(directory)) {
            files = new ArrayList<>(entries.filter(p -> Files.isRegularFile(p, LinkOption.NOFOLLOW_LINKS))
                    .filter(p -> p.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(".html"))
                    .map(Path::toFile).sorted(Comparator.comparingLong(File::lastModified).reversed()
                            .thenComparing(File::getName)).toList());
        }
        Set<File> keep = new HashSet<>();
        files.stream().filter(active).forEach(keep::add);
        for (File file : files) {
            if (keep.size() >= limit) break;
            keep.add(file);
        }
        int deleted = 0;
        int failed = 0;
        for (File file : List.copyOf(files)) {
            if (keep.contains(file)) continue;
            try {
                if (!active.test(file) && !Files.isSymbolicLink(file.toPath())) {
                    Files.delete(file.toPath());
                    files.remove(file);
                    deleted++;
                }
            } catch (IOException e) { failed++; }
        }
        return new Result(List.copyOf(files), deleted, failed);
    }
}
