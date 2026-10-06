package de.applejuicenet.client.gui.search;

import de.applejuicenet.client.AppleJuiceClient;
import de.applejuicenet.client.fassade.ApplejuiceFassade;
import de.applejuicenet.client.fassade.controller.CoreConnectionSettingsHolder;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.Assert.*;

public class StartSearchThreadingTest {
    private static final java.util.List<String> UNCAUGHT = new java.util.concurrent.CopyOnWriteArrayList<>();
    private static Field fassadeField;
    private static ApplejuiceFassade previous;

    @BeforeClass
    public static void setUp() throws Exception {
        LanguageSelector.getInstance(Paths.get("../../resources/language/deutsch.properties")
                .toAbsolutePath().normalize().toString());
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> UNCAUGHT.add(t.getName() + ": " + e));
        fassadeField = AppleJuiceClient.class.getDeclaredField("ajFassade");
        fassadeField.setAccessible(true);
        previous = (ApplejuiceFassade) fassadeField.get(null);
    }

    @AfterClass
    public static void restore() throws Exception {
        fassadeField.set(null, previous);
    }

    /**
     * Core, der share.xml erst nach der Freigabe beantwortet. Die erste Suche holt die Anteilsliste, ein langsamer
     * Core bremst den Klick also genau dort.
     */
    private static class SlowCore implements AutoCloseable {
        final CountDownLatch shareSeen = new CountDownLatch(1);
        final CountDownLatch searchSeen = new CountDownLatch(1);
        final CountDownLatch release = new CountDownLatch(1);
        final AtomicLong searchRequests = new AtomicLong();
        private final ServerSocket server;
        private final ExecutorService executor = Executors.newCachedThreadPool();
        private final Set<Socket> clients = Collections.newSetFromMap(new ConcurrentHashMap<>());

        SlowCore() throws IOException {
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
                client.setSoTimeout(20000);
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(client.getInputStream(), StandardCharsets.US_ASCII));
                String request = reader.readLine();
                String line;
                while ((line = reader.readLine()) != null && !line.isEmpty()) {
                }
                String body = request.contains("share.xml") ? "<applejuice/>"
                        : request.contains("getsession.xml") ? "<applejuice><session id=\"7\"/></applejuice>"
                        : "<applejuice/>";
                if (request.contains("share.xml")) {
                    shareSeen.countDown();
                    release.await(20, TimeUnit.SECONDS);
                }
                if (request.contains("/function/search")) {
                    searchRequests.incrementAndGet();
                    searchSeen.countDown();
                    client.getOutputStream().write("HTTP/1.1 200 OK\n".getBytes(StandardCharsets.US_ASCII));
                    return;
                }
                byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
                client.getOutputStream().write(("HTTP/1.1 200 OK\nContent-Length: " + bytes.length + "\n\n")
                        .getBytes(StandardCharsets.US_ASCII));
                client.getOutputStream().write(bytes);
            } catch (IOException | InterruptedException ignored) {
            } finally {
                clients.remove(client);
            }
        }

        @Override
        public void close() throws IOException {
            release.countDown();
            server.close();
            for (Socket client : clients) {
                client.close();
            }
            executor.shutdownNow();
        }
    }

    @Test(timeout = 60000)
    public void searchButtonReturnsImmediatelyWhileTheCoreIsSlow() throws Exception {
        try (SlowCore core = new SlowCore()) {
            ApplejuiceFassade fassade = new ApplejuiceFassade(
                    new CoreConnectionSettingsHolder("127.0.0.1", core.port(), "", true));
            fassadeField.set(null, fassade);
            Field shareHolder = ApplejuiceFassade.class.getDeclaredField("shareXML");
            shareHolder.setAccessible(true);
            Class<?> holderType = Class.forName("de.applejuicenet.client.fassade.controller.xml.ShareXMLHolder");
            Constructor<?> holderConstructor = holderType.getDeclaredConstructor(CoreConnectionSettingsHolder.class);
            holderConstructor.setAccessible(true);
            shareHolder.set(fassade, holderConstructor.newInstance(
                    new CoreConnectionSettingsHolder("127.0.0.1", core.port(), "", true)));
            Constructor<SearchController> constructor = SearchController.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            SearchController[] controller = new SearchController[1];
            SwingUtilities.invokeAndWait(() -> {
                try {
                    controller[0] = constructor.newInstance();
                } catch (ReflectiveOperationException e) {
                    throw new AssertionError(e);
                }
            });
            SearchPanel panel = (SearchPanel) controller[0].getComponent();
            JTextField text = panel.getSuchbegriffTxt();
            JButton button = panel.getStartStopBtn();
            long[] clickNanos = new long[1];
            int[] selection = new int[2];
            SwingUtilities.invokeAndWait(() -> {
                text.setText("ubuntu iso");
                long start = System.nanoTime();
                button.doClick(0);
                clickNanos[0] = System.nanoTime() - start;
                selection[0] = text.getSelectionStart();
                selection[1] = text.getSelectionEnd();
            });
            // Der Suchtext wird sofort markiert, ohne auf den Core zu warten
            assertEquals(0, selection[0]);
            assertEquals("ubuntu iso".length(), selection[1]);

            assertTrue("Klick hat auf den Core gewartet: " + clickNanos[0] / 1_000_000 + " ms",
                    clickNanos[0] < 1_500_000_000L);
            assertTrue("Anteilsliste wurde nie angefragt", core.shareSeen.await(10, TimeUnit.SECONDS));
            // Der Core haelt share.xml noch zurueck; der EDT muss trotzdem sofort arbeiten
            long t0 = System.nanoTime();
            SwingUtilities.invokeAndWait(() -> text.setText("weiter"));
            assertTrue("EDT blockiert", System.nanoTime() - t0 < 1_000_000_000L);
            assertEquals(0, core.searchRequests.get());

            core.release.countDown();
            if (!core.searchSeen.await(10, TimeUnit.SECONDS)) {
                fail("Suche kam nach der Freigabe nicht beim Core an. Nicht gefangen: " + UNCAUGHT);
            }
            assertEquals(1, core.searchRequests.get());
        }
    }
}
