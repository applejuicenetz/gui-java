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

    @Test(timeout = 10000)
    public void coreStopCancelsBlockedPolicyAndRemovesListener() throws Exception {
        CountDownLatch entered = new CountDownLatch(1);
        var facade = new de.applejuicenet.client.facade.ApplejuiceFacade(
                new de.applejuicenet.client.facade.controller.CoreConnectionSettingsHolder("localhost", 9851, "", true)) {
            @Override public java.util.Map<Integer, de.applejuicenet.client.facade.entity.Download> getDownloadsSnapshot() {
                entered.countDown();
                try { new CountDownLatch(1).await(); }
                catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                return java.util.Map.of();
            }
        };
        var listenersField = de.applejuicenet.client.facade.ApplejuiceFacade.class.getDeclaredField("coreListener");
        listenersField.setAccessible(true);
        var listeners = (java.util.Set<?>) listenersField.get(null);
        int initialListeners = listeners.size();
        AutomaticPowerdownload worker = new AutomaticPowerdownload(facade);
        worker.start();
        try {
            assertTrue(entered.await(2, TimeUnit.SECONDS));
            var notifyStatus = de.applejuicenet.client.facade.ApplejuiceFacade.class.getDeclaredMethod(
                    "informCoreStatusListener", de.applejuicenet.client.facade.listener.CoreStatusListener.STATUS.class);
            notifyStatus.setAccessible(true);
            notifyStatus.invoke(facade, de.applejuicenet.client.facade.listener.CoreStatusListener.STATUS.CLOSED);
            worker.join(1000);
            assertFalse("Policy survived core stop", worker.isAlive());
            assertEquals("Policy leaked status listener", initialListeners, listeners.size());
        } finally {
            worker.interrupt();
            worker.join(2000);
        }
    }

    @Test(timeout = 10000)
    public void stopDuringSnapshotDoesNotSendPowerCommand() throws Exception {
        CountDownLatch entered = new CountDownLatch(1);
        AtomicBoolean powerSent = new AtomicBoolean();
        var constructor = Class.forName("de.applejuicenet.client.facade.controller.xml.DownloadDO")
                .getDeclaredConstructor(int.class);
        constructor.setAccessible(true);
        var download = (de.applejuicenet.client.facade.entity.Download) constructor.newInstance(1);
        var facade = new de.applejuicenet.client.facade.ApplejuiceFacade(
                new de.applejuicenet.client.facade.controller.CoreConnectionSettingsHolder("localhost", 9851, "", true)) {
            @Override public java.util.Map<Integer, de.applejuicenet.client.facade.entity.Download> getDownloadsSnapshot() {
                entered.countDown();
                try { new CountDownLatch(1).await(); }
                catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                return java.util.Map.of(1, download);
            }
            @Override public void setPowerDownload(java.util.List<de.applejuicenet.client.facade.entity.Download> downloads, Integer power) {
                powerSent.set(true);
            }
        };
        AutomaticPowerdownload worker = new AutomaticPowerdownload(facade);
        worker.start();
        try {
            assertTrue(entered.await(2, TimeUnit.SECONDS));
        } finally {
            worker.interrupt();
            worker.join(2000);
        }
        assertFalse("Worker did not stop", worker.isAlive());
        assertFalse("Stopped policy sent power command after snapshot", powerSent.get());
    }

    @Test(timeout = 10000)
    public void informationUpdateDoesNotReadSwingFieldsOnThePollerThread() throws Exception {
        language();
        var panelHolder = new PowerDownloadPanel[1];
        var offEdtReads = new java.util.concurrent.atomic.AtomicInteger();
        SwingUtilities.invokeAndWait(() -> panelHolder[0] = new PowerDownloadPanel(null));
        PowerDownloadPanel panel = panelHolder[0];
        try {
            var threadField = PowerDownloadPanel.class.getDeclaredField("autoPowerdownloadThread");
            threadField.setAccessible(true);
            AutomaticPowerdownload policy = new AutomaticPowerdownload(null);
            threadField.set(panel, policy);
            var from = PowerDownloadPanel.class.getDeclaredField("creditThresholdFrom");
            from.setAccessible(true);
            from.setLong(panel, 5L * 1048576L);
            var to = PowerDownloadPanel.class.getDeclaredField("creditThresholdTo");
            to.setAccessible(true);
            to.setLong(panel, 1L * 1048576L);
            for (String name : new String[] {"autoFrom", "autoTo"}) {
                var field = PowerDownloadPanel.class.getDeclaredField(name);
                field.setAccessible(true);
                var original = (javax.swing.JTextField) field.get(panel);
                field.set(panel, new javax.swing.JTextField("5") {
                    @Override public String getText() {
                        if (!SwingUtilities.isEventDispatchThread()) offEdtReads.incrementAndGet();
                        return super.getText();
                    }
                });
            }
            Thread poller = new Thread(() -> panel.fireContentChanged(
                    de.applejuicenet.client.facade.listener.DataUpdateListener.DATALISTENER_TYPE.INFORMATION_CHANGED,
                    new de.applejuicenet.client.facade.entity.Information() {
                        public long getSessionUpload() { return 0; }
                        public long getSessionDownload() { return 0; }
                        public long getCredits() { return 1; }
                        public long getUploadSpeed() { return 0; }
                        public long getDownloadSpeed() { return 0; }
                        public long getOpenConnections() { return 0; }
                        public long getMaxUploadPositions() { return 0; }
                        public int getConnectionStatus() { return 0; }
                        public String getServerName() { return ""; }
                        public String getExternalIP() { return ""; }
                        public de.applejuicenet.client.facade.entity.Server getServer() { return null; }
                    }), "TestPoller");
            poller.start();
            poller.join(3000);
            SwingUtilities.invokeAndWait(() -> {});
            assertFalse("Poller did not finish", poller.isAlive());
            assertEquals("Swing fields were read on the poller thread", 0, offEdtReads.get());
        } finally {
            SwingUtilities.invokeAndWait(() -> LanguageSelector.getInstance().removeLanguageListener(panel));
        }
    }

    @Test(timeout = 10000)
    public void pausedPolicyPausesNewLoadsOffThePollerThread() throws Exception {
        language();
        var panelHolder = new PowerDownloadPanel[1];
        SwingUtilities.invokeAndWait(() -> panelHolder[0] = new PowerDownloadPanel(null));
        PowerDownloadPanel panel = panelHolder[0];
        CountDownLatch commandStarted = new CountDownLatch(1);
        CountDownLatch release = new CountDownLatch(1);
        var ctor = Class.forName("de.applejuicenet.client.facade.controller.xml.DownloadDO")
                .getDeclaredConstructor(int.class);
        ctor.setAccessible(true);
        var download = (de.applejuicenet.client.facade.entity.Download) ctor.newInstance(1);
        var statusSetter = Class.forName("de.applejuicenet.client.facade.controller.xml.DownloadDO")
                .getDeclaredMethod("setStatus", int.class);
        statusSetter.setAccessible(true);
        statusSetter.invoke(download, de.applejuicenet.client.facade.entity.Download.SEARCHING_LOADING);
        var facade = new de.applejuicenet.client.facade.ApplejuiceFacade(
                new de.applejuicenet.client.facade.controller.CoreConnectionSettingsHolder("localhost", 9851, "", true)) {
            @Override public void pauseDownload(java.util.List<de.applejuicenet.client.facade.entity.Download> downloads) {
                commandStarted.countDown();
                try { release.await(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            }
        };
        var facadeField = de.applejuicenet.client.AppleJuiceClient.class.getDeclaredField("ajFacade");
        facadeField.setAccessible(true);
        Object previous = facadeField.get(null);
        facadeField.set(null, facade);
        Thread poller = null;
        try {
            var threadField = PowerDownloadPanel.class.getDeclaredField("autoPowerdownloadThread");
            threadField.setAccessible(true);
            AutomaticPowerdownload policy = new AutomaticPowerdownload(null);
            policy.setPaused(true);
            threadField.set(panel, policy);
            java.util.Map<Integer, de.applejuicenet.client.facade.entity.Download> content = new java.util.HashMap<>();
            content.put(1, download);
            poller = new Thread(() -> panel.fireContentChanged(
                    de.applejuicenet.client.facade.listener.DataUpdateListener.DATALISTENER_TYPE.DOWNLOAD_CHANGED, content),
                    "TestPoller");
            poller.start();
            assertTrue("Pause command was never sent", commandStarted.await(2, TimeUnit.SECONDS));
            poller.join(500);
            assertFalse("Slow pause command held the poller thread", poller.isAlive());
        } finally {
            release.countDown();
            if (poller != null) poller.join(2000);
            facadeField.set(null, previous);
            SwingUtilities.invokeAndWait(() -> LanguageSelector.getInstance().removeLanguageListener(panel));
        }
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
