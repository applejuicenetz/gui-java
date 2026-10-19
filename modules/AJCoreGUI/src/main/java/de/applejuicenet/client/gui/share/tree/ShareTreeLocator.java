package de.applejuicenet.client.gui.share.tree;

import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Walks the lazily loaded share directory tree down to a configured directory.
 * Only the ancestors of the target are requested; the tree is never expanded completely.
 */
public final class ShareTreeLocator {
    private ShareTreeLocator() {
    }

    /** Ancestor paths of {@code path}, starting with the tree root entry and ending with the path itself. */
    public static List<String> prefixes(String path, String separator) {
        List<String> prefixes = new ArrayList<>();

        if (path == null || path.isEmpty()) {
            return prefixes;
        }

        String[] rawSegments = path.split("[\\\\/]+");
        List<String> segments = new ArrayList<>();

        for (String segment : rawSegments) {
            if (!segment.isEmpty()) {
                segments.add(segment);
            }
        }

        if (segments.isEmpty()) {
            return prefixes;
        }

        StringBuilder current = new StringBuilder();
        int start;

        if (path.startsWith("/") || path.startsWith("\\")) {
            current.append(separator);
            prefixes.add(separator);
            start = 0;
        } else {
            current.append(segments.get(0)).append(separator);
            prefixes.add(current.toString());
            start = 1;
        }

        for (int index = start; index < segments.size(); index++) {
            if (index > start) {
                current.append(separator);
            }
            current.append(segments.get(index));
            prefixes.add(current.toString());
        }

        return prefixes;
    }

    /**
     * Returns the deepest reachable path for {@code targetPath}, or {@code null} when not even the first
     * segment exists. {@code nodePath} tells the path of a node; comparison ignores case.
     */
    public static TreePath locate(TreeModel model, String targetPath, String separator, Function<Object, String> nodePath) {
        return locate(model, targetPath, separator, nodePath, node -> false);
    }

    /**
     * Like {@link #locate(TreeModel, String, String, Function)}, but looks through {@code container}
     * nodes when a segment is not a direct child. On Windows the drives are not below the tree root but
     * below "Desktop" and "This PC", which have no path of their own.
     */
    public static TreePath locate(TreeModel model, String targetPath, String separator,
                                  Function<Object, String> nodePath, Predicate<Object> container) {
        TreePath result = new TreePath(model.getRoot());

        for (String prefix : prefixes(targetPath, separator)) {
            TreePath found = find(model, result, prefix, nodePath, container);

            if (found == null) {
                break;
            }

            result = found;
        }

        return result.getPathCount() > 1 ? result : null;
    }

    private static TreePath find(TreeModel model, TreePath parentPath, String path,
                                 Function<Object, String> nodePath, Predicate<Object> container) {
        Object parent = parentPath.getLastPathComponent();
        List<Object> containers = new ArrayList<>();

        for (int index = 0; index < model.getChildCount(parent); index++) {
            Object child = model.getChild(parent, index);
            String childPath = nodePath.apply(child);

            if (childPath != null && childPath.equalsIgnoreCase(path)) {
                return parentPath.pathByAddingChild(child);
            }
            if (container.test(child)) {
                containers.add(child);
            }
        }

        for (Object child : containers) {
            TreePath found = find(model, parentPath.pathByAddingChild(child), path, nodePath, container);

            if (found != null) {
                return found;
            }
        }

        return null;
    }
}
