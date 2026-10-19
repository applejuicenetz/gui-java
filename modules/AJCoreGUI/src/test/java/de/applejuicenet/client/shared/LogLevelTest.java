package de.applejuicenet.client.shared;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import org.junit.After;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

import static org.junit.Assert.assertEquals;

/** The log level of the shipped logback.xml is info and can be raised with -Dajgui.loglevel. */
public class LogLevelTest {
    @After
    public void restore() {
        System.clearProperty("ajgui.loglevel");
    }

    private Level configuredRootLevel() throws Exception {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        try (InputStream config = getClass().getResourceAsStream("/logback.xml")) {
            context.reset();
            JoranConfigurator configurator = new JoranConfigurator();
            configurator.setContext(context);
            configurator.doConfigure(config);
        }
        Level level = context.getLogger(Logger.ROOT_LOGGER_NAME).getLevel();
        context.reset();
        return level;
    }

    @Test
    public void defaultLevelIsInfo() throws Exception {
        assertEquals(Level.INFO, configuredRootLevel());
    }

    @Test
    public void levelCanBeRaisedToDebug() throws Exception {
        System.setProperty("ajgui.loglevel", "debug");
        assertEquals(Level.DEBUG, configuredRootLevel());
    }
}
