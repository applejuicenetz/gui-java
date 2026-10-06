package de.applejuicenet.client.gui.download;

import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.powerdownload.AutomaticPowerdownloadPolicy;
import org.junit.Test;

import javax.swing.SwingUtilities;
import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;

public class PolicyCompletionThreadingTest {
    @Test(timeout = 10000)
    public void staleCompletionDoesNotResetTheCurrentPolicy() throws Exception {
        LanguageSelector.getInstance(Paths.get("../../resources/language/deutsch.properties")
                .toAbsolutePath().normalize().toString());
        AtomicBoolean reset = new AtomicBoolean();
        SwingUtilities.invokeAndWait(() -> {
            PowerDownloadPanel panel = new PowerDownloadPanel(null) {
                @Override
                public void autoPwdlFinished() {
                    reset.set(true);
                }
            };
            try {
                var completion = PowerDownloadPanel.class.getMethod("autoPwdlFinished", AutomaticPowerdownloadPolicy.class);
                var current = PowerDownloadPanel.class.getDeclaredField("autoPwdlThread");
                current.setAccessible(true);
                AutomaticPowerdownloadPolicy policy = new AutomaticPowerdownloadPolicy(null) {
                    public boolean initAction() { return false; }
                    public void doAction() { }
                    public void informPaused() { }
                    public String getVersion() { return "test"; }
                    public String getDescription() { return "test"; }
                    public String getAuthor() { return "test"; }
                    public String toString() { return "test"; }
                };
                current.set(panel, policy);
                completion.invoke(panel, new Object[] {null});
                assertFalse("Old policy reset current policy", reset.get());
                assertSame(policy, current.get(panel));
                completion.invoke(panel, policy);
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
        LanguageSelector.getInstance(Paths.get("../../resources/language/deutsch.properties")
                .toAbsolutePath().normalize().toString());
        CountDownLatch completed = new CountDownLatch(1);
        AtomicBoolean onEdt = new AtomicBoolean();
        PowerDownloadPanel[] panel = new PowerDownloadPanel[1];
        SwingUtilities.invokeAndWait(() -> panel[0] = new PowerDownloadPanel(null) {
            @Override
            public void autoPwdlFinished(AutomaticPowerdownloadPolicy completedPolicy) {
                onEdt.set(SwingUtilities.isEventDispatchThread());
                completed.countDown();
            }
        });
        AutomaticPowerdownloadPolicy policy = new AutomaticPowerdownloadPolicy(null) {
            public boolean initAction() { return false; }
            public void doAction() { }
            public void informPaused() { }
            public String getVersion() { return "test"; }
            public String getDescription() { return "test"; }
            public String getAuthor() { return "test"; }
            public String toString() { return "test"; }
        };
        policy.setParentToInform(panel[0]);
        policy.start();
        policy.join(2000);
        assertTrue(completed.await(2, TimeUnit.SECONDS));
        assertTrue("Policy callback ran outside EDT", onEdt.get());
        SwingUtilities.invokeAndWait(() -> LanguageSelector.getInstance().removeLanguageListener(panel[0]));
    }
}
