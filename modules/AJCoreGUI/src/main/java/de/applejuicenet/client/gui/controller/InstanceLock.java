package de.applejuicenet.client.gui.controller;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.security.SecureRandom;
import java.util.HexFormat;

/**
 * Detects a running GUI with an operating-system file lock in the profile folder. The system releases the
 * lock when the process ends or crashes, so a dead GUI never blocks the next start. The running GUI writes
 * its free local port and a random token into {@link #INFO_FILE}; a second start reads both to hand over links.
 */
public final class InstanceLock implements AutoCloseable {
    public static final String LOCK_FILE = "instance.lock";
    public static final String INFO_FILE = "instance.info";

    /** The lock is held by another process, so a GUI is running. */
    public static final class AlreadyRunningException extends IOException {
        public AlreadyRunningException() {
            super("An instance of the GUI is already running.");
        }
    }

    /** Port and token of the running GUI. */
    public record Info(int port, String token) {
    }

    private final Path directory;
    private final FileChannel channel;
    private final FileLock lock;

    private InstanceLock(Path directory, FileChannel channel, FileLock lock) {
        this.directory = directory;
        this.channel = channel;
        this.lock = lock;
    }

    /** @throws AlreadyRunningException if another process (or this one) already holds the lock */
    public static InstanceLock acquire(Path directory) throws IOException {
        Files.createDirectories(directory);
        FileChannel channel = FileChannel.open(directory.resolve(LOCK_FILE),
                StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        try {
            FileLock lock = channel.tryLock();
            if (lock == null) {
                channel.close();
                throw new AlreadyRunningException();
            }
            return new InstanceLock(directory, channel, lock);
        } catch (OverlappingFileLockException sameProcess) {
            channel.close();
            throw new AlreadyRunningException();
        } catch (IOException | RuntimeException e) {
            channel.close();
            throw e;
        }
    }

    /** Publishes the port and a fresh random token; returns the token. Written atomically, readable by the owner only. */
    public String publish(int port) throws IOException {
        byte[] bytes = new byte[16];
        new SecureRandom().nextBytes(bytes);
        String token = HexFormat.of().formatHex(bytes);
        Path target = directory.resolve(INFO_FILE);
        Path temp = Files.createTempFile(directory, "instance", ".tmp");
        try {
            try {
                Files.setPosixFilePermissions(temp, java.nio.file.attribute.PosixFilePermissions.fromString("rw-------"));
            } catch (UnsupportedOperationException noPosix) {
                // Windows: the profile folder already belongs to the user
            }
            Files.writeString(temp, port + "\n" + token + "\n", StandardCharsets.UTF_8);
            Files.move(temp, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } finally {
            Files.deleteIfExists(temp);
        }
        return token;
    }

    /**
     * Reads port and token of the running GUI. The GUI holds the lock before it publishes, so a missing file is
     * waited for briefly instead of reported at once.
     */
    public static Info read(Path directory, long waitMillis) throws IOException {
        Path file = directory.resolve(INFO_FILE);
        long deadline = System.nanoTime() + waitMillis * 1_000_000L;
        while (true) {
            try {
                String[] lines = Files.readString(file, StandardCharsets.UTF_8).split("\n");
                if (lines.length >= 2 && !lines[1].isBlank()) {
                    return new Info(Integer.parseInt(lines[0].trim()), lines[1].trim());
                }
            } catch (java.nio.file.NoSuchFileException | NumberFormatException notYet) {
                // fall through to retry
            }
            if (System.nanoTime() >= deadline) {
                throw new IOException("The running GUI has not published " + INFO_FILE + " in " + directory);
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new java.io.InterruptedIOException("Interrupted while waiting for " + INFO_FILE);
            }
        }
    }

    @Override
    public void close() throws IOException {
        try {
            Files.deleteIfExists(directory.resolve(INFO_FILE));
        } finally {
            try {
                lock.release();
            } finally {
                channel.close();
            }
        }
    }
}
