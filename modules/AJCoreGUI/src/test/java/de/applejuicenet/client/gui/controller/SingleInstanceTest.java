package de.applejuicenet.client.gui.controller;

import de.applejuicenet.client.facade.listener.CoreStatusListener.STATUS;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/** The running GUI is detected by an OS file lock; links reach it on a free port named in the profile folder. */
public class SingleInstanceTest {
    @Rule public TemporaryFolder profile = new TemporaryFolder();

    @Test(timeout = 20000)
    public void secondStartIsRejectedWhileFirstRuns() throws Exception {
        Path dir = profile.getRoot().toPath();
        try (InstanceLock first = InstanceLock.acquire(dir)) {
            assertNotNull(first);
            assertThrows(InstanceLock.AlreadyRunningException.class, () -> InstanceLock.acquire(dir));
        }
        try (InstanceLock again = InstanceLock.acquire(dir)) {
            assertNotNull("lock is free after close", again);
        }
    }

    @Test(timeout = 30000)
    public void otherProcessIsRejectedAndCrashLeavesNoLockBehind() throws Exception {
        Path dir = profile.getRoot().toPath();
        Process holder = new ProcessBuilder(Path.of(System.getProperty("java.home"), "bin", "java").toString(),
                "-cp", System.getProperty("java.class.path"), SingleInstanceHolder.class.getName(), dir.toString())
                .redirectErrorStream(true).start();
        try {
            BufferedReader out = new BufferedReader(new InputStreamReader(holder.getInputStream(), StandardCharsets.UTF_8));
            assertEquals("locked", out.readLine());
            assertThrows("a lock held by another process must be refused",
                    InstanceLock.AlreadyRunningException.class, () -> InstanceLock.acquire(dir));
            holder.destroyForcibly();
            assertTrue(holder.waitFor(10, TimeUnit.SECONDS));
            try (InstanceLock afterCrash = InstanceLock.acquire(dir)) {
                assertNotNull("the OS frees the lock when the process dies", afterCrash);
            }
        } finally {
            holder.destroyForcibly();
        }
    }

    @Test(timeout = 20000)
    public void secondStartHandsLinkToRunningGuiWithoutFixedPort() throws Exception {
        Path dir = profile.getRoot().toPath();
        List<String> received = new CopyOnWriteArrayList<>();
        try (LinkListener first = new LinkListener(dir, (link, directory) -> received.add(link))) {
            first.fireStatusChanged(STATUS.STARTED);
            assertThrows(InstanceLock.AlreadyRunningException.class, () -> new LinkListener(dir, (l, d) -> { }));
            LinkListener.forwardLink(dir, "ajfsp://file|a.bin|hash|1024/");
            assertEquals(List.of("ajfsp://file|a.bin|hash|1024/"), received);
        }
    }

    @Test(timeout = 20000)
    public void foreignProgramOnTheOldDefaultPortDoesNotMatter() throws Exception {
        Path dir = profile.getRoot().toPath();
        try (ServerSocket foreign = new ServerSocket(8768, 1, java.net.InetAddress.getByName("localhost"));
             LinkListener gui = new LinkListener(dir, (l, d) -> { })) {
            assertNotEquals(8768, gui.getListenerPort());
        } catch (java.net.BindException busy) {
            try (LinkListener gui = new LinkListener(dir, (l, d) -> { })) {
                assertNotEquals(8768, gui.getListenerPort());
            }
        }
    }

    @Test(timeout = 20000)
    public void staleInfoFileOfACrashedGuiIsReplaced() throws Exception {
        Path dir = profile.getRoot().toPath();
        Files.writeString(dir.resolve(InstanceLock.INFO_FILE), "1\nstale-token\n");
        List<String> received = new CopyOnWriteArrayList<>();
        try (LinkListener gui = new LinkListener(dir, (link, d) -> received.add(link))) {
            gui.fireStatusChanged(STATUS.STARTED);
            LinkListener.forwardLink(dir, "ajfsp://file|b.bin|hash|2048/");
            assertEquals(1, received.size());
        }
    }

    @Test(timeout = 20000)
    public void wrongTokenIsRejectedAndInfoFileIsRemovedOnClose() throws Exception {
        Path dir = profile.getRoot().toPath();
        List<String> received = new CopyOnWriteArrayList<>();
        try (LinkListener gui = new LinkListener(dir, (link, d) -> received.add(link))) {
            gui.fireStatusChanged(STATUS.STARTED);
            assertThrows(java.io.IOException.class,
                    () -> LinkListener.forwardLink(gui.getListenerPort(), "not-the-token", "ajfsp://file|c.bin|h|1/"));
            assertTrue(received.isEmpty());
            assertTrue(Files.exists(dir.resolve(InstanceLock.INFO_FILE)));
        }
        assertFalse(Files.exists(dir.resolve(InstanceLock.INFO_FILE)));
    }

    @Test(timeout = 20000)
    public void missingInfoFileGivesAClearErrorAfterAShortWait() throws Exception {
        Path dir = profile.getRoot().toPath();
        File lockOnly = dir.resolve(InstanceLock.LOCK_FILE).toFile();
        assertTrue(lockOnly.createNewFile());
        java.io.IOException error = assertThrows(java.io.IOException.class, () -> InstanceLock.read(dir, 300));
        assertTrue(error.getMessage(), error.getMessage().contains(InstanceLock.INFO_FILE));
    }
}
