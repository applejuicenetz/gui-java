package de.applejuicenet.client.shared;

import org.slf4j.Logger;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public final class StartupTiming {
    private StartupTiming() {
    }

    public static <Result> Result measure(Logger logger, String phase, Supplier<Result> action) {
        long started = System.nanoTime();
        try {
            return action.get();
        } finally {
            logger.info("Startphase {}: {} ms", phase, TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - started));
        }
    }
}
