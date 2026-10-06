package de.applejuicenet.client.fassade;

import de.applejuicenet.client.fassade.controller.CoreConnectionSettingsHolder;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/** Ein haengender Netzaufruf darf keine anderen Fassadenaufrufe blockieren. */
public class FassadeLockingTest {
    @org.junit.BeforeClass
    public static void separatorIsSetByTheFirstInformationRequestInProduction() {
        ApplejuiceFassade.separator = "/";
    }

    private static class Core implements AutoCloseable {
        final CountDownLatch processLinkSeen = new CountDownLatch(1);
        final CountDownLatch release = new CountDownLatch(1);
        private final ServerSocket server;
        private final ExecutorService executor = Executors.newCachedThreadPool();
        private final Set<Socket> clients = ConcurrentHashMap.newKeySet();

        Core() throws IOException {
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
                client.setSoTimeout(30000);
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(client.getInputStream(), StandardCharsets.US_ASCII));
                String request = reader.readLine();
                String line;
                while ((line = reader.readLine()) != null && !line.isEmpty()) {
                }
                if (request.contains("processlink")) {
                    processLinkSeen.countDown();
                    release.await(30, TimeUnit.SECONDS);
                }
                String body = request.contains("information.xml") ? "<applejuice><information/></applejuice>" : "ok";
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
    public void slowProcessLinkDoesNotBlockTheAvailabilityCheck() throws Exception {
        try (Core core = new Core()) {
            ApplejuiceFassade fassade = new ApplejuiceFassade(
                    new CoreConnectionSettingsHolder("127.0.0.1", core.port(), "", true));
            ExecutorService caller = Executors.newCachedThreadPool();
            try {
                Future<String> link = caller.submit(() -> fassade.processLink("ajfsp://file|a.bin|0123456789abcdef0123456789abcdef|1", ""));
                if (!core.processLinkSeen.await(10, TimeUnit.SECONDS)) {
                    String cause = link.isDone() ? String.valueOf(assertThrows(java.util.concurrent.ExecutionException.class,
                            link::get).getCause()) : "noch nicht fertig";
                    fail("processLink erreichte den Core nicht: " + cause);
                }

                Future<Integer> available = caller.submit(fassade::isCoreAvailable);
                try {
                    assertEquals(0, (int) available.get(5, TimeUnit.SECONDS));
                } catch (java.util.concurrent.TimeoutException e) {
                    fail("isCoreAvailable wartet auf den Monitor der Fassade, obwohl nur processLink haengt");
                }
                assertFalse(link.isDone());
                core.release.countDown();
                assertNotNull(link.get(10, TimeUnit.SECONDS));
            } finally {
                core.release.countDown();
                caller.shutdownNow();
            }
        }
    }

    /**
     * Die erste share.xml-Anfrage haengt, die zweite wird sofort beantwortet. Ein zweiter Aufrufer muss sein
     * Ergebnis bekommen, ohne auf den haengenden ersten zu warten.
     */
    @Test(timeout = 60000)
    public void secondShareRequestIsServedWhileTheFirstOneHangs() throws Exception {
        CountDownLatch firstSeen = new CountDownLatch(1);
        CountDownLatch release = new CountDownLatch(1);
        java.util.concurrent.atomic.AtomicInteger shareRequests = new java.util.concurrent.atomic.AtomicInteger();
        try (ServerSocket server = new ServerSocket(0, 50, InetAddress.getByName("127.0.0.1"))) {
            ExecutorService pool = Executors.newCachedThreadPool();
            pool.submit(() -> {
                while (!server.isClosed()) {
                    try {
                        Socket client = server.accept();
                        pool.submit(() -> {
                            try (client) {
                                BufferedReader reader = new BufferedReader(
                                        new InputStreamReader(client.getInputStream(), StandardCharsets.US_ASCII));
                                String request = reader.readLine();
                                if (request.contains("share.xml") && shareRequests.incrementAndGet() == 1) {
                                    firstSeen.countDown();
                                    release.await(30, TimeUnit.SECONDS);
                                }
                                byte[] bytes = "<applejuice><share id=\"1\" filename=\"a\" size=\"5\"/></applejuice>"
                                        .getBytes(StandardCharsets.UTF_8);
                                client.getOutputStream().write(("HTTP/1.1 200 OK\nContent-Length: " + bytes.length
                                        + "\n\n").getBytes(StandardCharsets.US_ASCII));
                                client.getOutputStream().write(bytes);
                            } catch (IOException | InterruptedException ignored) {
                            }
                        });
                    } catch (IOException closed) {
                        return;
                    }
                }
            });
            try {
                Class<?> type = Class.forName("de.applejuicenet.client.fassade.controller.xml.ShareXMLHolder");
                var constructor = type.getDeclaredConstructor(CoreConnectionSettingsHolder.class);
                constructor.setAccessible(true);
                Object holder = constructor.newInstance(
                        new CoreConnectionSettingsHolder("127.0.0.1", server.getLocalPort(), "", true));
                Method getShare = type.getMethod("getShare");
                Future<Object> first = pool.submit(() -> getShare.invoke(holder));
                assertTrue(firstSeen.await(10, TimeUnit.SECONDS));

                Future<Object> second = pool.submit(() -> getShare.invoke(holder));
                try {
                    assertNotNull(second.get(5, TimeUnit.SECONDS));
                } catch (java.util.concurrent.TimeoutException e) {
                    fail("zweiter getShare wartet auf den haengenden ersten Abruf");
                }
                assertFalse("erster Abruf sollte noch haengen", first.isDone());
                release.countDown();
                assertNotNull(first.get(10, TimeUnit.SECONDS));
            } finally {
                release.countDown();
                pool.shutdownNow();
            }
        }
    }
}
