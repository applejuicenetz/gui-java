package de.applejuicenet.client.shared;

import de.applejuicenet.client.facade.exception.CoreLostException;
import de.applejuicenet.client.facade.exception.IllegalArgumentException;
import de.applejuicenet.client.facade.exception.WebSiteNotFoundException;
import de.applejuicenet.client.facade.exception.WrongPasswordException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Runs a blocking core command off the calling thread, usually the EDT.
 *
 * A command that the core rejects or cannot answer is logged as a warning with the task name. A
 * lost core or a wrong password is passed on unchanged: the default handler turns exactly these two
 * into the shutdown dialog. Every other failure is a bug and stays visible as an uncaught exception.
 */
public final class CoreCall {
    private static final Logger logger = LoggerFactory.getLogger(CoreCall.class);

    /** A core command. May throw the checked facade exception. */
    @FunctionalInterface
    public interface Task {
        void run() throws Exception;
    }

    private CoreCall() {
    }

    /** Starts the task on a named virtual thread and returns that thread. */
    public static Thread runInBackground(String name, Task task) {
        return Thread.ofVirtual().name(name).start(() -> {
            try {
                task.run();
            } catch (WebSiteNotFoundException | IllegalArgumentException failure) {
                logger.warn("Core command {} failed: {}", name, failure.toString());
            } catch (CoreLostException | WrongPasswordException fatal) {
                throw fatal;
            } catch (RuntimeException bug) {
                throw bug;
            } catch (Exception checked) {
                logger.warn("Core command {} failed: {}", name, checked.toString());
            }
        });
    }
}
