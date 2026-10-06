package de.applejuicenet.client.fassade.shared;

import de.applejuicenet.client.fassade.exception.WebSiteNotFoundException;
import de.applejuicenet.client.fassade.exception.WrongPasswordException;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;

public class HtmlLoaderTest {
    private static final int CONNECT_TIMEOUT_MILLIS = 1000;
    private static final int READ_TIMEOUT_MILLIS = 200;
    private static final String COMMAND = "/xml/information.xml?password=test";

    @Test(timeout = 10000)
    public void interruptClosesSilentGetConnection() throws Exception {
        assertInterruptClosesConnection("", HtmlLoader.GET, COMMAND);
    }

    @Test(timeout = 10000)
    public void interruptClosesPartialBodyConnection() throws Exception {
        assertInterruptClosesConnection("HTTP/1.1 200 OK\nContent-Length: 4\n\nab", HtmlLoader.GET, COMMAND);
    }

    @Test(timeout = 10000)
    public void interruptClosesCompressedConnection() throws Exception {
        assertInterruptClosesConnection("HTTP/1.1 200 OK\nContent-Length: 4\n\n", HtmlLoader.GET,
                COMMAND + "&mode=zip");
    }

    @Test(timeout = 10000)
    public void interruptClosesSilentPostConnection() throws Exception {
        assertInterruptClosesConnection("", HtmlLoader.POST, "/function/cancelsearch?password=test&id=1");
    }

    private void assertInterruptClosesConnection(String response, int method, String command) throws Exception {
        AtomicReference<Throwable> failure = new AtomicReference<>();
        AtomicBoolean interrupted = new AtomicBoolean();
        try (CoreResponse core = new CoreResponse(response)) {
            Thread requestThread = new Thread(() -> {
                try {
                    HtmlLoader.getHtmlXMLContent("127.0.0.1", core.port(), method, command, true,
                            CONNECT_TIMEOUT_MILLIS, 60000);
                } catch (Throwable error) {
                    failure.set(error);
                } finally {
                    interrupted.set(Thread.currentThread().isInterrupted());
                }
            }, "HtmlLoaderInterruptTest");
            try {
                requestThread.start();
                assertTrue(core.requestReceived.await(5, TimeUnit.SECONDS));
                requestThread.interrupt();
                requestThread.join(1000);
                assertFalse("Interrupted request still blocks", requestThread.isAlive());
                assertTrue(interrupted.get());
                assertTrue(failure.get() instanceof WebSiteNotFoundException);
                assertTrue(failure.get().getCause() instanceof IOException);
                core.awaitClientClose();
            } finally {
                core.close();
                requestThread.interrupt();
                requestThread.join(1000);
            }
        }
    }

    @Test(timeout = 10000)
    public void silentCoreTimesOutAndClosesConnection() throws Exception {
        assertReadTimeout("", HtmlLoader.GET, COMMAND);
    }

    @Test(timeout = 10000)
    public void partialHeaderTimesOutAndClosesConnection() throws Exception {
        assertReadTimeout("HTTP/1.1 200 OK\nContent-Length: ", HtmlLoader.GET, COMMAND);
    }

    @Test(timeout = 10000)
    public void partialBodyTimesOutAndClosesConnection() throws Exception {
        assertReadTimeout("HTTP/1.1 200 OK\nContent-Length: 4\n\nab", HtmlLoader.GET, COMMAND);
    }

    @Test(timeout = 10000)
    public void stalledCompressedBodyTimesOutAndClosesConnection() throws Exception {
        assertReadTimeout("HTTP/1.1 200 OK\nContent-Length: 4\n\n", HtmlLoader.GET, COMMAND + "&mode=zip");
    }

    @Test(timeout = 10000)
    public void silentPostTimesOutAndClosesConnection() throws Exception {
        assertReadTimeout("", HtmlLoader.POST, "/function/cancelsearch?password=test&id=1");
    }

    @Test(timeout = 10000)
    public void connectionIsClosedAfterResponse() throws Exception {
        String body = "<applejuice/>";
        try (CoreResponse core = new CoreResponse("HTTP/1.1 200 OK\nContent-Length: " + body.length() + "\n\n" + body)) {
            assertEquals(body, request(core, HtmlLoader.GET, true));
            core.awaitClientClose();
        }
    }

    @Test(timeout = 10000)
    public void truncatedPlainBodyFailsInsteadOfReturningPartialContent() throws Exception {
        try (CoreResponse core = new CoreResponse("HTTP/1.1 200 OK\nContent-Length: 20\n\n<applejuice/>", true)) {
            WebSiteNotFoundException error = assertThrows(WebSiteNotFoundException.class,
                    () -> request(core, HtmlLoader.GET, true));
            assertTrue(error.getCause() instanceof java.io.EOFException);
            core.awaitClientClose();
        }
    }

    @Test(timeout = 10000)
    public void rejectsInvalidContentLength() throws Exception {
        for (String length : new String[] {"-5", "abc", "99999999999"}) {
            try (CoreResponse core = new CoreResponse("HTTP/1.1 200 OK\nContent-Length: " + length + "\n\nxx")) {
                assertThrows(WebSiteNotFoundException.class, () -> request(core, HtmlLoader.GET, true));
            }
        }
    }

    @Test(timeout = 10000)
    public void plainBodySplitOverSeveralPacketsIsComplete() throws Exception {
        String body = "<applejuice>" + "x".repeat(100000) + "</applejuice>";
        try (CoreResponse core = new CoreResponse("HTTP/1.1 200 OK\nContent-Length: " + body.length() + "\n\n" + body)) {
            assertEquals(body, request(core, HtmlLoader.GET, true));
            core.awaitClientClose();
        }
    }

    @Test(timeout = 10000)
    public void zipBodyIsInflated() throws Exception {
        String body = "<applejuice>" + "abc".repeat(5000) + "</applejuice>";
        byte[] zipped = ZLibUtils.compress(body);
        try (CoreResponse core = new CoreResponse(("HTTP/1.1 200 OK\nContent-Length: " + zipped.length + "\n\n").getBytes(StandardCharsets.US_ASCII), zipped)) {
            assertEquals(body, HtmlLoader.getHtmlXMLContent("127.0.0.1", core.port(), HtmlLoader.GET,
                    COMMAND + "&mode=zip", true, CONNECT_TIMEOUT_MILLIS, READ_TIMEOUT_MILLIS));
            core.awaitClientClose();
        }
    }

    @Test(timeout = 10000)
    public void truncatedZipBodyFails() throws Exception {
        byte[] zipped = ZLibUtils.compress("<applejuice>" + "abc".repeat(5000) + "</applejuice>");
        byte[] cut = java.util.Arrays.copyOf(zipped, zipped.length / 2);
        try (CoreResponse core = new CoreResponse(("HTTP/1.1 200 OK\nContent-Length: " + zipped.length + "\n\n").getBytes(StandardCharsets.US_ASCII), cut, true)) {
            WebSiteNotFoundException error = assertThrows(WebSiteNotFoundException.class,
                    () -> HtmlLoader.getHtmlXMLContent("127.0.0.1", core.port(), HtmlLoader.GET,
                            COMMAND + "&mode=zip", true, CONNECT_TIMEOUT_MILLIS, READ_TIMEOUT_MILLIS));
            assertTrue(String.valueOf(error.getCause()), error.getCause() instanceof java.util.zip.DataFormatException);
        }
    }

    @Test(timeout = 10000)
    public void connectionIsClosedAfterSuccessfulPost() throws Exception {
        try (CoreResponse core = new CoreResponse("HTTP/1.1 200 OK\n")) {
            assertEquals(StringConstants.HTTP_1_1_200_OK, request(core, HtmlLoader.POST, true));
            core.awaitClientClose();
        }
    }

    @Test(timeout = 10000)
    public void commandsWithoutResultDoNotWaitForResponseAndCloseConnection() throws Exception {
        try (CoreResponse core = new CoreResponse("")) {
            assertEquals(StringConstants.OK, request(core, HtmlLoader.POST, false));
            core.awaitClientClose();
        }
    }

    @Test(timeout = 10000)
    public void wrongPasswordClosesConnection() throws Exception {
        try (CoreResponse core = new CoreResponse("HTTP/1.1 302 Found\nLocation: /wrongpassword\n")) {
            assertThrows(WrongPasswordException.class, () -> request(core, HtmlLoader.GET, true));
            core.awaitClientClose();
        }
    }

    @Test(timeout = 10000)
    public void invalidIdClosesConnection() throws Exception {
        try (CoreResponse core = new CoreResponse("HTTP/1.1 200 OK\nerror: invalid id\n")) {
            assertEquals(StringConstants.EMPTY, request(core, HtmlLoader.GET, true));
            core.awaitClientClose();
        }
    }

    @Test(timeout = 10000)
    public void refusedConnectionKeepsOriginalCause() throws Exception {
        int closedPort;
        try (ServerSocket server = new ServerSocket(0, 50, InetAddress.getByName("127.0.0.1"))) {
            closedPort = server.getLocalPort();
        }
        WebSiteNotFoundException error = assertThrows(WebSiteNotFoundException.class,
                () -> HtmlLoader.getHtmlXMLContent("127.0.0.1", closedPort, HtmlLoader.GET, COMMAND, true,
                        CONNECT_TIMEOUT_MILLIS, READ_TIMEOUT_MILLIS));
        assertTrue(error.getCause() instanceof IOException);
    }

    @Test
    public void rejectsTimeoutsThatWouldAllowUnboundedWaiting() {
        assertThrows(IllegalArgumentException.class, () -> HtmlLoader.getHtmlXMLContent("127.0.0.1", 1,
                HtmlLoader.GET, COMMAND, true, 0, READ_TIMEOUT_MILLIS));
        assertThrows(IllegalArgumentException.class, () -> HtmlLoader.getHtmlXMLContent("127.0.0.1", 1,
                HtmlLoader.GET, COMMAND, true, CONNECT_TIMEOUT_MILLIS, 0));
    }

    private void assertReadTimeout(String response, int method, String command) throws Exception {
        try (CoreResponse core = new CoreResponse(response)) {
            WebSiteNotFoundException error = assertThrows(WebSiteNotFoundException.class,
                    () -> HtmlLoader.getHtmlXMLContent("127.0.0.1", core.port(), method, command, true,
                            CONNECT_TIMEOUT_MILLIS, READ_TIMEOUT_MILLIS));
            assertTrue(error.getCause() instanceof SocketTimeoutException);
            core.awaitClientClose();
        }
    }

    private String request(CoreResponse core, int method, boolean withResult) {
        return HtmlLoader.getHtmlXMLContent("127.0.0.1", core.port(), method, COMMAND, withResult,
                CONNECT_TIMEOUT_MILLIS, READ_TIMEOUT_MILLIS);
    }

    private static class CoreResponse implements AutoCloseable {
        private final CountDownLatch requestReceived = new CountDownLatch(1);
        private final ServerSocket server;
        private final ExecutorService executor = Executors.newSingleThreadExecutor();
        private final Future<?> task;
        private volatile Socket client;

        CoreResponse(String response) throws IOException {
            this(response, false);
        }

        CoreResponse(String response, boolean endOfResponse) throws IOException {
            this(response.getBytes(StandardCharsets.US_ASCII), new byte[0], endOfResponse);
        }

        CoreResponse(byte[] header, byte[] body) throws IOException {
            this(header, body, false);
        }

        CoreResponse(byte[] header, byte[] body, boolean endOfResponse) throws IOException {
            server = new ServerSocket(0, 50, InetAddress.getByName("127.0.0.1"));
            server.setSoTimeout(5000);
            task = executor.submit(() -> {
                try (Socket socket = server.accept()) {
                    client = socket;
                    socket.setSoTimeout(5000);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.US_ASCII));
                    String line;
                    while ((line = reader.readLine()) != null && !line.isEmpty()) {
                    }
                    if (header.length > 0) {
                        socket.getOutputStream().write(header);
                        socket.getOutputStream().write(body);
                        socket.getOutputStream().flush();
                        if (endOfResponse) {
                            socket.shutdownOutput();
                        }
                    }
                    requestReceived.countDown();
                    try {
                        assertEquals(-1, reader.read());
                    } catch (SocketException connectionReset) {
                        assertFalse(socket.isClosed());
                    }
                }
                return null;
            });
        }

        int port() {
            return server.getLocalPort();
        }

        void awaitClientClose() throws Exception {
            task.get(5, TimeUnit.SECONDS);
        }

        public void close() throws IOException {
            try {
                server.close();
                Socket currentClient = client;
                if (currentClient != null) {
                    currentClient.close();
                }
            } finally {
                executor.shutdownNow();
            }
        }
    }
}
