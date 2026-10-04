package de.applejuicenet.client.gui.plugins.sharetreemap;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Immutable snapshot of Core paths; never reads the local filesystem. */
public record ShareTree(String name, String path, int id, long bytes, String link, List<ShareTree> children) {
    public boolean directory() { return id < 0; }

    public static ShareTree fromFiles(List<String[]> files) {
        Builder root = new Builder("Share", "", -1, 0, "");
        for (String[] file : files) {
            long bytes = Math.max(0, Long.parseLong(file[2]));
            String path = file[1].replace('\\', '/');
            String[] parts = path.split("/+", -1);
            Builder parent = root;
            String prefix = path.startsWith("/") ? "/" : "";
            for (int i = 0; i < parts.length; i++) {
                if (parts[i].isEmpty()) continue;
                prefix += parts[i];
                if (i == parts.length - 1) {
                    parent.children.add(new Builder(parts[i], path, Integer.parseInt(file[0]), bytes, file[3]));
                } else {
                    Builder next = null;
                    for (Builder child : parent.children) {
                        if (child.id < 0 && child.name.equals(parts[i])) { next = child; break; }
                    }
                    if (next == null) {
                        next = new Builder(parts[i], prefix, -1, 0, "");
                        parent.children.add(next);
                    }
                    parent = next;
                    prefix += "/";
                }
            }
        }
        return root.freeze();
    }

    private static class Builder {
        final String name, path, link;
        final int id;
        final long bytes;
        final List<Builder> children = new ArrayList<>();
        Builder(String name, String path, int id, long bytes, String link) {
            this.name = name; this.path = path; this.id = id; this.bytes = bytes; this.link = link;
        }
        ShareTree freeze() {
            List<ShareTree> nodes = children.stream().map(Builder::freeze)
                    .sorted(Comparator.comparingLong(ShareTree::bytes).reversed()).toList();
            long total = bytes;
            for (ShareTree node : nodes) total = Long.MAX_VALUE - total < node.bytes ? Long.MAX_VALUE : total + node.bytes;
            return new ShareTree(name, path, id, total, link, nodes);
        }
    }
}
