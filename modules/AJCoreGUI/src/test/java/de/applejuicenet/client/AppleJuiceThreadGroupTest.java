package de.applejuicenet.client;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import javax.swing.SwingUtilities;
import static org.junit.Assert.*;

public class AppleJuiceThreadGroupTest {
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
            SwingUtilities.invokeAndWait(() -> {});
            assertEquals("Every unexpected failure must be visible at INFO configuration", 3, appender.list.size());
            for (int i = 0; i < failures.length; i++) {
                ILoggingEvent event = appender.list.get(i);
                assertEquals(Level.ERROR, event.getLevel());
                assertNotNull(event.getThrowableProxy());
                assertEquals(failures[i].getMessage(), event.getThrowableProxy().getMessage());
            }
        } finally {
            logger.detachAppender(appender);
            logger.setLevel(previous);
            appender.stop();
        }
    }
}
