package de.applejuicenet.client.gui.share.tree;

import de.applejuicenet.client.facade.entity.ShareEntry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Finds share entries that expose more than the user intended: a whole drive, the
 * filesystem root or the complete home directory.
 */
public final class ShareWarningDetector {
    public enum Reason {
        FILESYSTEM_ROOT,
        DRIVE_ROOT,
        HOME_DIRECTORY
    }

    public record Warning(String directory, Reason reason) {
    }

    private ShareWarningDetector() {
    }

    /** Risky share entries of the core settings, in settings order. */
    public static List<Warning> findWarnings(Collection<ShareEntry> entries, String home) {
        List<Warning> warnings = new ArrayList<>();

        if (entries == null) {
            return warnings;
        }
        for (ShareEntry entry : entries) {
            if (entry == null || entry.getDir() == null || entry.getShareMode() == null
                    || entry.getShareMode() == ShareEntry.SHAREMODE.NOT_SHARED) {
                continue;
            }
            Reason reason = reasonFor(entry.getDir(), home);

            if (reason != null) {
                warnings.add(new Warning(entry.getDir(), reason));
            }
        }
        return warnings;
    }

    static Reason reasonFor(String directory, String home) {
        String trimmed = trimSeparators(directory);

        if (trimmed.isEmpty()) {
            return Reason.FILESYSTEM_ROOT;
        }
        if (isDriveRoot(trimmed)) {
            return Reason.DRIVE_ROOT;
        }
        if (isHomeDirectory(directory, home)) {
            return Reason.HOME_DIRECTORY;
        }
        return null;
    }

    /** Recognises root paths like {@code /}, {@code C:} or {@code C:\} without touching the filesystem. */
    public static boolean isRootPath(String path) {
        if (path == null || path.isEmpty()) {
            return false;
        }
        String trimmed = trimSeparators(path);

        if (trimmed.isEmpty()) {
            return true;
        }
        return isDriveRoot(trimmed);
    }

    private static boolean isDriveRoot(String trimmed) {
        return trimmed.length() == 2 && Character.isLetter(trimmed.charAt(0)) && trimmed.charAt(1) == ':';
    }

    /** True when the share is exactly the user's home directory, not a folder below it. */
    public static boolean isHomeDirectory(String path, String home) {
        if (path == null || home == null) {
            return false;
        }
        String normalizedHome = trimSeparators(home);

        return !normalizedHome.isEmpty() && trimSeparators(path).equalsIgnoreCase(normalizedHome);
    }

    /** Removes trailing path separators so {@code C:\} and {@code C:} compare equal. */
    public static String trimSeparators(String path) {
        if (path == null) {
            return "";
        }
        int end = path.length();

        while (end > 0 && (path.charAt(end - 1) == '/' || path.charAt(end - 1) == '\\')) {
            end--;
        }
        return path.substring(0, end);
    }
}
