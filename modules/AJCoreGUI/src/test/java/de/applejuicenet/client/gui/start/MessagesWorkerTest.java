package de.applejuicenet.client.gui.start;

import org.junit.Test;
import javax.swing.*;
import java.util.concurrent.atomic.AtomicBoolean;
import static org.junit.Assert.*;

public class MessagesWorkerTest {
    @Test(timeout = 10000)
    public void interruptedRequestDoesNotRetryOrPublish() throws Exception {
        java.util.concurrent.CountDownLatch entered = new java.util.concurrent.CountDownLatch(1);
        java.util.concurrent.atomic.AtomicInteger requests = new java.util.concurrent.atomic.AtomicInteger();
        JLabel version = new JLabel("unchanged");
        MessagesWorker worker = new MessagesWorker(version, new JTextPane(), () -> "test", () -> "http://localhost/%s", url -> {
            requests.incrementAndGet();
            entered.countDown();
            try { new java.util.concurrent.CountDownLatch(1).await(); }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new de.applejuicenet.client.facade.exception.NoAccessException("cancelled", e);
            }
            return "news";
        });
        worker.start();
        try { assertTrue(entered.await(2, java.util.concurrent.TimeUnit.SECONDS)); }
        finally { worker.interrupt(); worker.join(2000); }
        SwingUtilities.invokeAndWait(() -> {});
        assertFalse(worker.isAlive());
        assertEquals(1, requests.get());
        assertEquals("unchanged", version.getText());
    }

    @Test(timeout = 10000)
    public void cancelledWorkerDoesNotRequestNewsOrUpdateUi() throws Exception {
        AtomicBoolean requested = new AtomicBoolean();
        JLabel version = new JLabel("unchanged");
        JTextPane messages = new JTextPane();
        MessagesWorker worker = new MessagesWorker(version, messages, () -> "test", () -> "http://localhost/%s", url -> {
            requested.set(true);
            return "<html>news</html>";
        });
        worker.interrupt();
        worker.start();
        worker.join(2000);
        SwingUtilities.invokeAndWait(() -> {});
        assertFalse(worker.isAlive());
        assertEquals("unchanged", version.getText());
        assertFalse("Cancelled worker touched unavailable GUI state", requested.get());
    }
}
