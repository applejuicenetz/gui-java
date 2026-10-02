package de.applejuicenet.client.gui.plugins.logviewer;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.FileAppender;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Iterator;

final class LogFileProtection {
    private LogFileProtection() {
    }

    static boolean isActive(File file) {
        return isActive(file, (LoggerContext) LoggerFactory.getILoggerFactory());
    }

    static boolean isActive(File file, LoggerContext context) {
        try {
            File canonicalFile = file.getCanonicalFile();
            for (ch.qos.logback.classic.Logger logger : context.getLoggerList()) {
                Iterator<Appender<ILoggingEvent>> appenders = logger.iteratorForAppenders();
                while (appenders.hasNext()) {
                    Appender<ILoggingEvent> appender = appenders.next();
                    if (appender instanceof FileAppender<?> fileAppender && fileAppender.isStarted()
                            && fileAppender.getFile() != null
                            && canonicalFile.equals(new File(fileAppender.getFile()).getCanonicalFile())) {
                        return true;
                    }
                }
            }
            return false;
        } catch (IOException exception) {
            LoggerFactory.getLogger(LogFileProtection.class)
                    .warn("LogViewer: Logdatei kann nicht sicher geprueft werden: {}", file, exception);
            return true;
        }
    }

    static boolean deleteIfInactive(File file) throws IOException {
        return deleteIfInactive(file, (LoggerContext) LoggerFactory.getILoggerFactory());
    }

    static boolean deleteIfInactive(File file, LoggerContext context) throws IOException {
        if (isActive(file, context)) {
            return false;
        }
        Files.delete(file.toPath());
        return true;
    }
}
