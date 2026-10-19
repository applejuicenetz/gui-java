package de.applejuicenet.client.shared;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import de.applejuicenet.client.facade.exception.CoreLostException;
import de.applejuicenet.client.facade.exception.IllegalArgumentException;
import de.applejuicenet.client.facade.exception.WebSiteNotFoundException;
import de.applejuicenet.client.facade.exception.WrongPasswordException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class CoreCallTest {
    private final List<Throwable> uncaught = new CopyOnWriteArrayList<>();
    private final ListAppender<ILoggingEvent> appender = new ListAppender<>();
    private Thread.UncaughtExceptionHandler previousHandler;
    private Logger logger;

    @Before
    public void capture() {
        previousHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler((thread, error) -> uncaught.add(error));
        logger = (Logger) LoggerFactory.getLogger(CoreCall.class);
        appender.start();
        logger.addAppender(appender);
    }

    @After
    public void restore() {
        logger.detachAppender(appender);
        appender.stop();
        Thread.setDefaultUncaughtExceptionHandler(previousHandler);
    }

    private static Thread run(String name, CoreCall.Task task) throws InterruptedException {
        return CoreCall.runInBackground(name, task);
    }

    @Test(timeout = 10000)
    public void callerIsNotBlockedAndTheThreadIsNamed() throws Exception {
        CountDownLatch inside = new CountDownLatch(1);
        CountDownLatch release = new CountDownLatch(1);
        String[] seenName = new String[1];
        Thread worker = run("PauseDownload", () -> {
            seenName[0] = Thread.currentThread().getName();
            inside.countDown();
            release.await();
        });
        try {
            assertTrue("The task never started", inside.await(2, TimeUnit.SECONDS));
            assertTrue("The caller must get its thread back while the task still runs", worker.isAlive());
            assertEquals("PauseDownload", seenName[0]);
        } finally {
            release.countDown();
            worker.join(2000);
        }
        assertFalse(worker.isAlive());
    }

    @Test(timeout = 10000)
    public void coreFailuresAreLoggedAsWarningsAndNotLeftUncaught() throws Exception {
        run("Task", () -> {
            throw new WebSiteNotFoundException(WebSiteNotFoundException.INPUT_ERROR);
        }).join(2000);
        run("Task", () -> {
            throw new IllegalArgumentException("invalid id");
        }).join(2000);
        assertEquals("Core failures must not reach the default handler: " + uncaught, 0, uncaught.size());
        long warnings = appender.list.stream().filter(e -> e.getLevel() == Level.WARN).count();
        assertEquals("Every core failure is reported once as a warning", 2, warnings);
        assertTrue("The warning names the failed task", appender.list.stream()
                .anyMatch(e -> e.getFormattedMessage().contains("Task")));
    }

    @Test(timeout = 10000)
    public void lostCoreAndWrongPasswordStillReachTheDefaultHandler() throws Exception {
        run("A", () -> {
            throw new CoreLostException();
        }).join(2000);
        run("B", () -> {
            throw new WrongPasswordException();
        }).join(2000);
        assertEquals("These two must keep triggering the shutdown handling: " + uncaught, 2, uncaught.size());
        assertTrue(uncaught.get(0) instanceof CoreLostException);
        assertTrue(uncaught.get(1) instanceof WrongPasswordException);
    }

    @Test(timeout = 10000)
    public void unexpectedFailuresAreNotHiddenEither() throws Exception {
        run("C", () -> {
            throw new IllegalStateException("bug");
        }).join(2000);
        assertEquals(1, uncaught.size());
        assertTrue(uncaught.get(0) instanceof IllegalStateException);
    }
}
