package de.applejuicenet.client.gui.plugins.logviewer;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.FileAppender;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LogFileProtectionTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private final LoggerContext context = new LoggerContext();

    @After
    public void closeAppenders() {
        context.stop();
    }

    @Test
    public void deletesArchivedLogButKeepsActiveLog() throws Exception {
        File active = folder.newFile("current.html");
        File archived = folder.newFile("old.html");
        attach(active, org.slf4j.Logger.ROOT_LOGGER_NAME);

        assertTrue(LogFileProtection.deleteIfInactive(archived, context));
        assertFalse(archived.exists());
        assertFalse(LogFileProtection.deleteIfInactive(active, context));
        assertTrue(active.exists());
    }

    @Test
    public void recognizesEquivalentPathsAndNonRootAppenders() throws Exception {
        File active = folder.newFile("current.html");
        attach(active, "plugin.logger");
        File alias = new File(active.getParentFile(), "." + File.separator + active.getName());

        assertTrue(LogFileProtection.isActive(alias, context));
        assertFalse(LogFileProtection.deleteIfInactive(alias, context));
        assertTrue(active.exists());
    }

    @Test
    public void allowsDeletionAfterAppenderStops() throws Exception {
        File file = folder.newFile("current.html");
        FileAppender<ILoggingEvent> appender = attach(file, org.slf4j.Logger.ROOT_LOGGER_NAME);
        appender.stop();

        assertTrue(LogFileProtection.deleteIfInactive(file, context));
        assertFalse(file.exists());
    }

    @Test
    public void rechecksActiveStateAtDeletionTime() throws Exception {
        File file = folder.newFile("current.html");
        assertFalse(LogFileProtection.isActive(file, context));
        attach(file, org.slf4j.Logger.ROOT_LOGGER_NAME);

        assertFalse(LogFileProtection.deleteIfInactive(file, context));
        assertTrue(file.exists());
    }

    private FileAppender<ILoggingEvent> attach(File file, String loggerName) {
        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(context);
        encoder.setPattern("%msg%n");
        encoder.start();
        FileAppender<ILoggingEvent> appender = new FileAppender<>();
        appender.setContext(context);
        appender.setName(file.getName());
        appender.setFile(file.getPath());
        appender.setEncoder(encoder);
        appender.start();
        assertTrue(appender.isStarted());
        context.getLogger(loggerName).addAppender(appender);
        return appender;
    }
}
