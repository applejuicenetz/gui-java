package de.applejuicenet.client.gui.download;

import de.applejuicenet.client.AppleJuiceClient;
import de.applejuicenet.client.fassade.ApplejuiceFassade;
import de.applejuicenet.client.fassade.controller.CoreConnectionSettingsHolder;
import de.applejuicenet.client.fassade.entity.Download;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

public class DownloadOverviewPanelThreadingTest {
    private static ApplejuiceFassade previousFassade;
    private static Field fassadeField;

    @BeforeClass
    public static void setUp() throws Exception {
        LanguageSelector.getInstance(Paths.get("../../resources/language/deutsch.properties")
                .toAbsolutePath().normalize().toString());
        fassadeField = AppleJuiceClient.class.getDeclaredField("ajFassade");
        fassadeField.setAccessible(true);
        previousFassade = (ApplejuiceFassade) fassadeField.get(null);
    }

    private final List<JFrame> frames = new CopyOnWriteArrayList<>();

    @After
    public void disposeFrames() throws Exception {
        SwingUtilities.invokeAndWait(() -> frames.forEach(JFrame::dispose));
        frames.clear();
    }

    /** Der Worker laedt nur, solange die Tabelle sichtbar ist; das Panel braucht ein angezeigtes Fenster. */
    private static JFrame show(DownloadOverviewPanel panel) {
        JFrame frame = new JFrame();
        frame.add(panel);
        frame.setSize(600, 400);
        frame.setVisible(true);
        return frame;
    }

    @AfterClass
    public static void restore() throws Exception {
        fassadeField.set(null, previousFassade);
    }

    /** Label, das jede Aenderung ausserhalb des EDT meldet. */
    private static class CheckedLabel extends JLabel {
        final Set<String> violations = ConcurrentHashMap.newKeySet();
        private boolean constructed;

        CheckedLabel() {
            constructed = true;
        }

        @Override
        public void setText(String text) {
            if (constructed && !SwingUtilities.isEventDispatchThread()) {
                violations.add("setText(" + text + ") auf " + Thread.currentThread().getName());
            }
            super.setText(text);
        }
    }

    private static Download download(int id) throws Exception {
        Class<?> clazz = Class.forName("de.applejuicenet.client.fassade.controller.xml.DownloadDO");
        Constructor<?> constructor = clazz.getDeclaredConstructor(int.class);
        constructor.setAccessible(true);
        Object download = constructor.newInstance(id);
        Method status = clazz.getDeclaredMethod("setStatus", int.class);
        status.setAccessible(true);
        status.invoke(download, Download.PAUSIERT);
        Method name = clazz.getDeclaredMethod("setFilename", String.class);
        name.setAccessible(true);
        name.invoke(download, "datei" + id + ".bin");
        return (Download) download;
    }

    @Test(timeout = 30000)
    public void partListIsPublishedOnlyOnEventDispatchThread() throws Exception {
        try (PartListCore core = new PartListCore(Set.of())) {
            fassadeField.set(null, new ApplejuiceFassade(
                    new CoreConnectionSettingsHolder("127.0.0.1", core.port(), "", true)));
            AtomicReference<DownloadOverviewPanel> panelRef = new AtomicReference<>();
            CheckedLabel label = new CheckedLabel();
            SwingUtilities.invokeAndWait(() -> {
                DownloadOverviewPanel panel = new DownloadOverviewPanel(null);
                try {
                    Field labelField = DownloadOverviewPanel.class.getDeclaredField("actualDLDateiName");
                    labelField.setAccessible(true);
                    labelField.set(panel, label);
                    Field checkbox = DownloadOverviewPanel.class.getDeclaredField("holeListe");
                    checkbox.setAccessible(true);
                    ((JCheckBox) checkbox.get(panel)).setSelected(true);
                } catch (ReflectiveOperationException e) {
                    throw new AssertionError(e);
                }
                panel.fireLanguageChanged();
                frames.add(show(panel));
                panelRef.set(panel);
            });
            DownloadOverviewPanel panel = panelRef.get();
            Download first = download(1);

            SwingUtilities.invokeAndWait(() -> panel.setDownload(first));
            awaitLabel(label, "datei1.bin");
            // Dateiname erscheint vor der Core-Antwort; erst der Zusatz " - " belegt die veroeffentlichte Partliste.
            awaitLabel(label, " - ");
            SwingUtilities.invokeAndWait(() -> panel.setDownload(null));
            assertTrue(core.requests.get() >= 1);
            assertEquals("Verstoesse gegen den EDT-Vertrag: " + label.violations, Set.of(), label.violations);
        }
    }

    @Test(timeout = 30000)
    public void switchingSelectionNeverShowsTheEarlierFileAfterwards() throws Exception {
        CountDownLatch releaseFirst = new CountDownLatch(1);
        try (PartListCore core = new PartListCore(Set.of(1), releaseFirst)) {
            fassadeField.set(null, new ApplejuiceFassade(
                    new CoreConnectionSettingsHolder("127.0.0.1", core.port(), "", true)));
            AtomicReference<DownloadOverviewPanel> panelRef = new AtomicReference<>();
            CheckedLabel label = new CheckedLabel();
            SwingUtilities.invokeAndWait(() -> {
                DownloadOverviewPanel panel = new DownloadOverviewPanel(null);
                try {
                    Field labelField = DownloadOverviewPanel.class.getDeclaredField("actualDLDateiName");
                    labelField.setAccessible(true);
                    labelField.set(panel, label);
                    Field checkbox = DownloadOverviewPanel.class.getDeclaredField("holeListe");
                    checkbox.setAccessible(true);
                    ((JCheckBox) checkbox.get(panel)).setSelected(true);
                } catch (ReflectiveOperationException e) {
                    throw new AssertionError(e);
                }
                panel.fireLanguageChanged();
                frames.add(show(panel));
                panelRef.set(panel);
            });
            DownloadOverviewPanel panel = panelRef.get();

            SwingUtilities.invokeAndWait(() -> {
                try {
                    panel.setDownload(download(1));
                } catch (Exception e) {
                    throw new AssertionError(e);
                }
            });
            assertTrue(core.delayedRequestSeen.await(10, TimeUnit.SECONDS));
            SwingUtilities.invokeAndWait(() -> {
                try {
                    panel.setDownload(download(2));
                } catch (Exception e) {
                    throw new AssertionError(e);
                }
            });
            awaitLabel(label, "datei2.bin");
            releaseFirst.countDown();
            Thread.sleep(500);
            SwingUtilities.invokeAndWait(() -> assertTrue(label.getText(), label.getText().contains("datei2.bin")));
            assertEquals(Set.of(), label.violations);
        }
    }

    private static void awaitLabel(JLabel label, String expected) throws Exception {
        long end = System.nanoTime() + TimeUnit.SECONDS.toNanos(10);
        AtomicReference<String> text = new AtomicReference<>("");
        while (System.nanoTime() < end) {
            SwingUtilities.invokeAndWait(() -> text.set(label.getText()));
            if (text.get() != null && text.get().contains(expected)) {
                return;
            }
            Thread.sleep(50);
        }
        fail("Label zeigt nicht " + expected + ": " + text.get());
    }

    private static class PartListCore implements AutoCloseable {
        final java.util.concurrent.atomic.AtomicInteger requests = new java.util.concurrent.atomic.AtomicInteger();
        final CountDownLatch delayedRequestSeen = new CountDownLatch(1);
        private final ServerSocket server;
        private final ExecutorService executor = Executors.newCachedThreadPool();
        private final Set<Integer> delayedIds;
        private final CountDownLatch release;
        private final Set<Socket> clients = Collections.newSetFromMap(new ConcurrentHashMap<>());

        PartListCore(Set<Integer> delayedIds) throws IOException {
            this(delayedIds, new CountDownLatch(0));
        }

        PartListCore(Set<Integer> delayedIds, CountDownLatch release) throws IOException {
            this.delayedIds = delayedIds;
            this.release = release;
            server = new ServerSocket(0, 50, InetAddress.getByName("127.0.0.1"));
            executor.submit(() -> {
                while (!server.isClosed()) {
                    try {
                        Socket client = server.accept();
                        clients.add(client);
                        executor.submit(() -> serve(client));
                    } catch (IOException closed) {
                        return;
                    }
                }
            });
        }

        int port() {
            return server.getLocalPort();
        }

        private void serve(Socket client) {
            try (client) {
                client.setSoTimeout(15000);
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(client.getInputStream(), StandardCharsets.US_ASCII));
                String request = reader.readLine();
                while (reader.readLine() != null && !reader.readLine().isEmpty()) {
                    break;
                }
                requests.incrementAndGet();
                String body;
                if (request.contains("getsession.xml")) {
                    body = "<applejuice><session id=\"7\"/></applejuice>";
                } else if (request.contains("downloadpartlist.xml")) {
                    int id = Integer.parseInt(request.replaceAll(".*[?&]id=(\\d+).*", "$1"));
                    if (delayedIds.contains(id)) {
                        delayedRequestSeen.countDown();
                        release.await(15, TimeUnit.SECONDS);
                    }
                    body = "<applejuice><fileinformation filesize=\"1000\"/>"
                            + "<part fromposition=\"0\" type=\"0\"/><part fromposition=\"500\" type=\"-1\"/></applejuice>";
                } else {
                    body = "<applejuice/>";
                }
                byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
                client.getOutputStream().write(("HTTP/1.1 200 OK\nContent-Length: " + bytes.length + "\n\n")
                        .getBytes(StandardCharsets.US_ASCII));
                client.getOutputStream().write(bytes);
                client.getOutputStream().flush();
            } catch (IOException | InterruptedException ignored) {
            } finally {
                clients.remove(client);
            }
        }

        @Override
        public void close() throws IOException {
            server.close();
            for (Socket client : clients) {
                client.close();
            }
            executor.shutdownNow();
        }
    }
}
