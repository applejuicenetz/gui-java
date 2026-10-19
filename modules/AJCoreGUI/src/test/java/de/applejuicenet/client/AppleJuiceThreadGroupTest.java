package de.applejuicenet.client;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import de.applejuicenet.client.facade.exception.CoreLostException;
import de.applejuicenet.client.facade.exception.WrongPasswordException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import javax.swing.SwingUtilities;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;

public class AppleJuiceThreadGroupTest {
    private final List<String> actions = new ArrayList<>();
    private final AtomicBoolean actionOnEdt = new AtomicBoolean(true);

    @Before
    public void installRecordingActions() {
        AppleJuiceThreadGroup.setFatalActions(new AppleJuiceThreadGroup.FatalActions() {
            public void wrongPassword() {
                record("wrongPassword");
            }

            public void coreLost() {
                record("coreLost");
            }
        });
    }

    @After
    public void restoreActions() {
        AppleJuiceThreadGroup.setFatalActions(null);
    }

    private void record(String action) {
        if (!SwingUtilities.isEventDispatchThread()) actionOnEdt.set(false);
        synchronized (actions) {
            actions.add(action);
        }
    }

    private void flushEdt() throws Exception {
        SwingUtilities.invokeAndWait(() -> {});
    }

    @Test(timeout = 10000)
    public void unexpectedFailuresAreNeverSilencedByLegacyExceptions() throws Exception {
        Logger logger = (Logger) LoggerFactory.getLogger(AppleJuiceThreadGroup.class);
        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.start();
        Level previous = logger.getLevel();
        logger.setLevel(Level.INFO);
        logger.addAppender(appender);
        try {
            Throwable[] failures = {
                    new ArrayIndexOutOfBoundsException("bad index"),
                    new ClassCastException("java.awt.TrayIcon cannot be cast to java.awt.Component"),
                    new IllegalStateException("unexpected failure")
            };
            for (Throwable failure : failures) AppleJuiceThreadGroup.handle(failure);
            flushEdt();
            assertEquals("Every unexpected failure must be visible at INFO configuration", 3, appender.list.size());
            for (int i = 0; i < failures.length; i++) {
                ILoggingEvent event = appender.list.get(i);
                assertEquals(Level.ERROR, event.getLevel());
                assertNotNull(event.getThrowableProxy());
                assertEquals(failures[i].getMessage(), event.getThrowableProxy().getMessage());
            }
            assertTrue("Unexpected failures must not trigger shutdown actions", actions.isEmpty());
        } finally {
            logger.detachAppender(appender);
            logger.setLevel(previous);
            appender.stop();
        }
    }

    @Test(timeout = 10000)
    public void wrongPasswordRunsItsActionOnTheEdtEvenFromAnotherThread() throws Exception {
        CountDownLatch done = new CountDownLatch(1);
        Thread worker = new Thread(() -> {
            AppleJuiceThreadGroup.handle(new WrongPasswordException());
            done.countDown();
        }, "TestWorker");
        worker.start();
        assertTrue(done.await(2, TimeUnit.SECONDS));
        flushEdt();
        assertEquals(List.of("wrongPassword"), actions);
        assertTrue("Fatal action ran outside the EDT", actionOnEdt.get());
    }

    @Test(timeout = 10000)
    public void coreLossRunsItsActionOnTheEdt() throws Exception {
        AppleJuiceThreadGroup.handle(new CoreLostException());
        flushEdt();
        assertEquals(List.of("coreLost"), actions);
        assertTrue("Fatal action ran outside the EDT", actionOnEdt.get());
    }

    @Test(timeout = 10000)
    public void severalFatalFailuresStartTheShutdownOnlyOnce() throws Exception {
        for (int i = 0; i < 3; i++) AppleJuiceThreadGroup.handle(new CoreLostException());
        AppleJuiceThreadGroup.handle(new WrongPasswordException());
        flushEdt();
        assertEquals("Several failing threads must not open several shutdown dialogs", 1, actions.size());
    }

    @Test(timeout = 10000)
    public void failingFatalActionDoesNotBreakLaterErrorHandling() throws Exception {
        Logger logger = (Logger) LoggerFactory.getLogger(AppleJuiceThreadGroup.class);
        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.start();
        logger.addAppender(appender);
        try {
            AppleJuiceThreadGroup.setFatalActions(new AppleJuiceThreadGroup.FatalActions() {
                public void wrongPassword() {
                }

                public void coreLost() {
                    throw new IllegalStateException("dialog could not be shown");
                }
            });
            AppleJuiceThreadGroup.handle(new CoreLostException());
            flushEdt();
            AppleJuiceThreadGroup.handle(new IllegalStateException("later"));
            flushEdt();
            assertTrue("A failing fatal action must be logged", appender.list.stream()
                    .anyMatch(event -> event.getThrowableProxy() != null
                            && "dialog could not be shown".equals(event.getThrowableProxy().getMessage())));
            assertTrue("Later failures must still be handled", appender.list.stream()
                    .anyMatch(event -> event.getThrowableProxy() != null
                            && "later".equals(event.getThrowableProxy().getMessage())));
        } finally {
            logger.detachAppender(appender);
            appender.stop();
        }
    }
}
