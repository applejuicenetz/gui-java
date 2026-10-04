package de.applejuicenet.client.fassade;

import de.applejuicenet.client.fassade.controller.CoreConnectionSettingsHolder;
import de.applejuicenet.client.fassade.controller.xml.ModifiedXMLHolder;
import de.applejuicenet.client.fassade.shared.ZLibUtils;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import static org.junit.Assert.*;

public class PollingRecoveryTest {
    private static final String SESSION = "<applejuice><session id=\"7\"/></applejuice>";
    private static final String INFORMATION = "<applejuice><version>0.35.185.93</version></applejuice>";
    private static final String MODIFIED = "<applejuice><information/><networkinfo users=\"1\" files=\"1\" "
            + "filesize=\"1\" firewalled=\"false\" ip=\"1.2.3.4\" tryconnecttoserver=\"-1\" "
            + "connectedwithserverid=\"-1\" connectedsince=\"0\"/></applejuice>";

    @Test(timeout = 15000)
    public void failedParseDoesNotBlockLaterUpdates() throws Exception {
        AtomicInteger modifiedRequests = new AtomicInteger();
        try (CoreStub core = new CoreStub(request -> {
            if (request.contains("getsession.xml")) {
                return SESSION;
            }
            return modifiedRequests.incrementAndGet() == 1 ? "<applejuice><information>" : MODIFIED;
        })) {
            ModifiedXMLHolder holder = new ModifiedXMLHolder(core.settings(), null);

            assertThrows(RuntimeException.class, holder::update);
            assertTrue(holder.update());
            assertEquals(2, modifiedRequests.get());
            assertNotNull(holder.getNetworkInfo());
            assertTrue(holder.isNetworkInfoChanged());
        }
    }

    @Test(timeout = 15000)
    public void stopEndsPollerAndAllowsRestart() throws Exception {
        try (CoreStub core = new CoreStub(request -> {
            if (request.contains("getsession.xml")) {
                return SESSION;
            }
            return request.contains("modified.xml") ? MODIFIED : INFORMATION;
        })) {
            ApplejuiceFassade fassade = new ApplejuiceFassade(core.settings());

            fassade.setUpdateInterval(20);
            fassade.startXMLCheck();
            fassade.startXMLCheck();
            awaitPollerCount(1);
            fassade.stopXMLCheck();
            awaitPollerCount(0);
            fassade.startXMLCheck();
            awaitPollerCount(1);
            fassade.stopXMLCheck();
            awaitPollerCount(0);
        }
    }

    private static void awaitPollerCount(int expected) throws InterruptedException {
        long deadline = System.currentTimeMillis() + 5000;

        while (pollerCount() != expected && System.currentTimeMillis() < deadline) {
            Thread.sleep(20);
        }
        assertEquals(expected, pollerCount());
    }

    private static int pollerCount() {
        int count = 0;

        for (Thread thread : Thread.getAllStackTraces().keySet()) {
            if (thread.isAlive() && thread.getName().equals("ApplejuiceFassadeXMLCheckThread")) {
                count++;
            }
        }
        return count;
    }

    private static class CoreStub implements AutoCloseable {
        private final ServerSocket server;
        private final ExecutorService executor = Executors.newCachedThreadPool();
        private final Function<String, String> responder;

        CoreStub(Function<String, String> responder) throws IOException {
            this.responder = responder;
            server = new ServerSocket(0, 50, InetAddress.getByName("127.0.0.1"));
            executor.submit(() -> {
                while (!server.isClosed()) {
                    try {
                        Socket client = server.accept();
                        executor.submit(() -> serve(client));
                    } catch (IOException closed) {
                        return null;
                    }
                }
                return null;
            });
        }

        CoreConnectionSettingsHolder settings() throws Exception {
            return new CoreConnectionSettingsHolder("127.0.0.1", server.getLocalPort(), "", true);
        }

        private void serve(Socket client) {
            try (client) {
                client.setSoTimeout(5000);
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(client.getInputStream(), StandardCharsets.US_ASCII));
                String request = reader.readLine();
                String line;
                while ((line = reader.readLine()) != null && !line.isEmpty()) {
                }
                String body = responder.apply(request);
                byte[] bytes = request.contains("mode=zip") ? ZLibUtils.compress(body)
                        : body.getBytes(StandardCharsets.UTF_8);
                ByteArrayOutputStream response = new ByteArrayOutputStream();
                response.write(("HTTP/1.1 200 OK\nContent-Length: " + bytes.length + "\n\n")
                        .getBytes(StandardCharsets.US_ASCII));
                response.write(bytes);
                OutputStream out = client.getOutputStream();
                out.write(response.toByteArray());
                out.flush();
            } catch (IOException ignored) {
            }
        }

        public void close() throws IOException {
            server.close();
            executor.shutdownNow();
        }
    }
}
