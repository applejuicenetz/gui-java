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

    /** Risky share entries of the core settings of this machine, in settings order. */
    public static List<Warning> findWarnings(Collection<ShareEntry> entries) {
        return findWarnings(entries, userHome(), systemDrive());
    }

    static List<Warning> findWarnings(Collection<ShareEntry> entries, String home, String systemDrive) {
        List<Warning> warnings = new ArrayList<>();

        if (entries == null) {
            return warnings;
        }
        for (ShareEntry entry : entries) {
            if (entry == null || entry.getDir() == null || entry.getShareMode() == null
                    || entry.getShareMode() == ShareEntry.SHAREMODE.NOT_SHARED) {
                continue;
            }
            Reason reason = reasonFor(entry.getDir(), home, systemDrive);

            if (reason != null) {
                warnings.add(new Warning(entry.getDir(), reason));
            }
        }
        return warnings;
    }

    /**
     * True for the folders behind the warning: filesystem root, home directory and system drive. Other
     * drives (e.g. a network drive {@code R:}) are a deliberate choice, so they are no risk.
     */
    public static boolean isCritical(String directory) {
        return reasonFor(directory, userHome(), systemDrive()) != null;
    }

    static Reason reasonFor(String directory, String home, String systemDrive) {
        String trimmed = trimSeparators(directory);

        if (trimmed.isEmpty()) {
            return Reason.FILESYSTEM_ROOT;
        }
        if (isDriveRoot(trimmed) && trimmed.equalsIgnoreCase(trimSeparators(systemDrive))) {
            return Reason.DRIVE_ROOT;
        }
        if (isHomeDirectory(directory, home)) {
            return Reason.HOME_DIRECTORY;
        }
        return null;
    }

    private static String userHome() {
        return System.getProperty("user.home");
    }

    /** System drive of this machine, e.g. {@code C:}; {@code C:} when the environment does not tell. */
    private static String systemDrive() {
        String drive = System.getenv("SystemDrive");

        return drive == null || drive.isBlank() ? "C:" : drive;
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
