package de.applejuicenet.client.gui.download;

import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.powerdownload.AutomaticPowerdownload;
import org.junit.Test;

import javax.swing.SwingUtilities;
import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;

public class AutomaticPowerdownloadThreadingTest {
    @Test(timeout = 10000)
    public void pauseCommandsRunOnWorkerNotOnEdt() throws Exception {
        CountDownLatch applied = new CountDownLatch(1);
        AtomicBoolean onEdt = new AtomicBoolean();
        var facade = new de.applejuicenet.client.facade.ApplejuiceFacade(
                new de.applejuicenet.client.facade.controller.CoreConnectionSettingsHolder("localhost", 9851, "", true)) {
            @Override
            public java.util.Map<Integer, de.applejuicenet.client.facade.entity.Download> getDownloadsSnapshot() {
                return java.util.Map.of();
            }
            @Override
            public void pauseDownload(java.util.List<de.applejuicenet.client.facade.entity.Download> downloads) {
                onEdt.set(SwingUtilities.isEventDispatchThread());
                applied.countDown();
            }
            @Override
            public void setPowerDownload(java.util.List<de.applejuicenet.client.facade.entity.Download> downloads, Integer power) {
            }
        };
        AutomaticPowerdownload worker = new AutomaticPowerdownload(facade);
        try {
            SwingUtilities.invokeAndWait(() -> worker.setPaused(true));
            assertEquals("Pause must only queue commands until worker starts", 1, applied.getCount());
            worker.start();
            assertTrue(applied.await(2, TimeUnit.SECONDS));
            assertFalse("Core command ran on EDT", onEdt.get());
        } finally {
            worker.interrupt();
            worker.join(2000);
        }
        assertFalse("Worker did not stop", worker.isAlive());
    }

    @Test(timeout = 10000)
    public void stopDuringPauseDoesNotSendAnotherCommand() throws Exception {
        CountDownLatch entered = new CountDownLatch(1);
        AtomicBoolean powerSent = new AtomicBoolean();
        var facade = new de.applejuicenet.client.facade.ApplejuiceFacade(
                new de.applejuicenet.client.facade.controller.CoreConnectionSettingsHolder("localhost", 9851, "", true)) {
            @Override public java.util.Map<Integer, de.applejuicenet.client.facade.entity.Download> getDownloadsSnapshot() {
                return java.util.Map.of();
            }
            @Override public void pauseDownload(java.util.List<de.applejuicenet.client.facade.entity.Download> downloads) {
                entered.countDown();
                try { new CountDownLatch(1).await(); }
                catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            }
            @Override public void setPowerDownload(java.util.List<de.applejuicenet.client.facade.entity.Download> downloads, Integer power) {
                powerSent.set(true);
            }
        };
        AutomaticPowerdownload worker = new AutomaticPowerdownload(facade);
        worker.setPaused(true);
        worker.start();
        try {
            assertTrue(entered.await(2, TimeUnit.SECONDS));
        } finally {
            worker.interrupt();
            worker.join(2000);
        }
        assertFalse(worker.isAlive());
        assertFalse("Stopped policy sent another command", powerSent.get());
    }

    private static void language() {
        LanguageSelector.getInstance(Paths.get("../../resources/language/deutsch.properties")
                .toAbsolutePath().normalize().toString());
    }

    @Test(timeout = 10000)
    public void staleCompletionDoesNotResetTheCurrentPowerdownload() throws Exception {
        language();
        AtomicBoolean reset = new AtomicBoolean();
        SwingUtilities.invokeAndWait(() -> {
            PowerDownloadPanel panel = new PowerDownloadPanel(null) {
                @Override
                public void autoPowerdownloadFinished() {
                    reset.set(true);
                }
            };
            try {
                var current = PowerDownloadPanel.class.getDeclaredField("autoPowerdownloadThread");
                current.setAccessible(true);
                AutomaticPowerdownload powerdownload = new AutomaticPowerdownload(null);
                current.set(panel, powerdownload);
                panel.autoPowerdownloadFinished((AutomaticPowerdownload) null);
                assertFalse("Old thread reset current thread", reset.get());
                assertSame(powerdownload, current.get(panel));
                panel.autoPowerdownloadFinished(powerdownload);
                assertTrue(reset.get());
            } catch (ReflectiveOperationException e) {
                throw new AssertionError(e);
            } finally {
                LanguageSelector.getInstance().removeLanguageListener(panel);
            }
        });
    }

    @Test(timeout = 10000)
    public void completionRunsOnTheEventDispatchThread() throws Exception {
        language();
        CountDownLatch completed = new CountDownLatch(1);
        AtomicBoolean onEdt = new AtomicBoolean();
        PowerDownloadPanel[] panel = new PowerDownloadPanel[1];
        SwingUtilities.invokeAndWait(() -> panel[0] = new PowerDownloadPanel(null) {
            @Override
            public void autoPowerdownloadFinished(AutomaticPowerdownload completed2) {
                onEdt.set(SwingUtilities.isEventDispatchThread());
                completed.countDown();
            }
        });
        AutomaticPowerdownload powerdownload = new AutomaticPowerdownload(null);
        powerdownload.setParentToInform(panel[0]);
        powerdownload.start();
        powerdownload.interrupt();
        powerdownload.join(2000);
        assertTrue(completed.await(2, TimeUnit.SECONDS));
        assertTrue("Callback ran outside EDT", onEdt.get());
        SwingUtilities.invokeAndWait(() -> LanguageSelector.getInstance().removeLanguageListener(panel[0]));
    }
}
