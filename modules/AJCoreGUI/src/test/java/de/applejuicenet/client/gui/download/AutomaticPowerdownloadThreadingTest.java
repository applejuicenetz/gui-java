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
