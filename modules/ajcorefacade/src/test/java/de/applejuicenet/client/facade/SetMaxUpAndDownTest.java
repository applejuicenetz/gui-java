package de.applejuicenet.client.facade;

import de.applejuicenet.client.facade.controller.CoreConnectionSettingsHolder;
import org.junit.Test;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class SetMaxUpAndDownTest {
    @Test(timeout = 20000)
    public void unreachableCoreDoesNotEscapeAsUncaughtException() throws Exception {
        List<Throwable> uncaught = new CopyOnWriteArrayList<>();
        List<String> threadNames = new CopyOnWriteArrayList<>();
        Thread.UncaughtExceptionHandler previous = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler((thread, error) -> {
            threadNames.add(thread.getName());
            uncaught.add(error);
        });
        int closedPort;
        try (ServerSocket server = new ServerSocket(0, 1, InetAddress.getByName("127.0.0.1"))) {
            closedPort = server.getLocalPort();
        }
        try {
            ApplejuiceFacade facade = new ApplejuiceFacade(
                    new CoreConnectionSettingsHolder("127.0.0.1", closedPort, "", true));
            facade.setMaxUpAndDown(100L, 200L);
            // a failing request ends the worker within the request timeouts
            Thread.sleep(TimeUnit.SECONDS.toMillis(2));
            assertEquals("A failed settings request escaped as an uncaught exception on " + threadNames
                    + ": " + uncaught, 0, uncaught.size());
        } finally {
            Thread.setDefaultUncaughtExceptionHandler(previous);
        }
    }

    @Test(timeout = 5000)
    public void invalidLimitsAreRejectedBeforeAnyThreadStarts() throws Exception {
        ApplejuiceFacade facade = new ApplejuiceFacade(
                new CoreConnectionSettingsHolder("127.0.0.1", 9, "", true));
        for (Long[] pair : new Long[][] {{null, 5L}, {5L, null}, {0L, 5L}, {5L, -1L}}) {
            try {
                facade.setMaxUpAndDown(pair[0], pair[1]);
                fail("expected IllegalArgumentException");
            } catch (de.applejuicenet.client.facade.exception.IllegalArgumentException expected) {
                // rejected synchronously
            }
        }
    }
}
