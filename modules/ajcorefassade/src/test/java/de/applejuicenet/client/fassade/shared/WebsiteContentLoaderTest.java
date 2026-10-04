package de.applejuicenet.client.fassade.shared;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import de.applejuicenet.client.fassade.exception.NoAccessException;
import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.http.HttpTimeoutException;
import java.nio.charset.Charset;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

public class WebsiteContentLoaderTest {
    @Test(timeout = 10000)
    public void silentServerTimesOut() throws Exception {
        try (WebServer server = new WebServer()) {
            server.handle(exchange -> server.waitForClose());
            assertTimeout(server);
        }
    }

    @Test(timeout = 10000)
    public void stalledBodyTimesOut() throws Exception {
        try (WebServer server = new WebServer()) {
            server.handle(exchange -> {
                exchange.sendResponseHeaders(200, 20);
                exchange.getResponseBody().write("partial".getBytes(Charset.defaultCharset()));
                exchange.getResponseBody().flush();
                server.waitForClose();
            });
            assertTimeout(server);
        }
    }

    @Test(timeout = 10000)
    public void returnsContentWithoutLineBreaksAndSendsUserAgent() throws Exception {
        AtomicReference<String> userAgent = new AtomicReference<>();
        try (WebServer server = new WebServer()) {
            server.handle(exchange -> {
                userAgent.set(exchange.getRequestHeaders().getFirst("User-Agent"));
                respond(exchange, 200, "hello\r\nworld\nGrüße");
            });
            assertEquals("helloworldGrüße", WebsiteContentLoader.getWebsiteContent(server.url()));
            assertTrue(userAgent.get().startsWith("ajcorefassade; Java/"));
        }
    }

    @Test(timeout = 10000)
    public void followsRedirects() throws Exception {
        try (WebServer server = new WebServer()) {
            server.handle(exchange -> {
                if (exchange.getRequestURI().getPath().equals("/")) {
                    exchange.getResponseHeaders().set("Location", "/content");
                    exchange.sendResponseHeaders(302, -1);
                    exchange.close();
                } else {
                    respond(exchange, 200, "redirected");
                }
            });
            assertEquals("redirected", WebsiteContentLoader.getWebsiteContent(server.url()));
        }
    }

    @Test(timeout = 10000)
    public void rejectsHttpErrors() throws Exception {
        try (WebServer server = new WebServer()) {
            server.handle(exchange -> respond(exchange, 404, "missing"));
            NoAccessException error = assertThrows(NoAccessException.class,
                    () -> WebsiteContentLoader.getWebsiteContent(server.url()));
            assertTrue(error.getCause() instanceof IOException);
            assertEquals("HTTP status 404", error.getCause().getMessage());
        }
    }

    @Test(timeout = 10000)
    public void preservesInterruptFlag() throws Exception {
        try (WebServer server = new WebServer()) {
            server.handle(exchange -> server.waitForClose());
            try {
                Thread.currentThread().interrupt();
                NoAccessException error = assertThrows(NoAccessException.class,
                        () -> WebsiteContentLoader.getWebsiteContent(server.url()));
                assertTrue(error.getCause() instanceof InterruptedException);
                assertTrue(Thread.currentThread().isInterrupted());
            } finally {
                Thread.interrupted();
            }
        }
    }

    @Test
    public void rejectsNonPositiveTimeouts() {
        assertThrows(IllegalArgumentException.class,
                () -> WebsiteContentLoader.getWebsiteContent("http://127.0.0.1/", 0, 1));
        assertThrows(IllegalArgumentException.class,
                () -> WebsiteContentLoader.getWebsiteContent("http://127.0.0.1/", 1, 0));
    }

    private void assertTimeout(WebServer server) {
        NoAccessException error = assertThrows(NoAccessException.class,
                () -> WebsiteContentLoader.getWebsiteContent(server.url(), 1000, 200));
        assertTrue(error.getCause() instanceof HttpTimeoutException);
    }

    private static void respond(HttpExchange exchange, int status, String content) throws IOException {
        byte[] bytes = content.getBytes(Charset.defaultCharset());
        exchange.sendResponseHeaders(status, bytes.length);
        try (exchange) {
            exchange.getResponseBody().write(bytes);
        }
    }

    private static class WebServer implements AutoCloseable {
        private final HttpServer server;
        private final ExecutorService executor = Executors.newCachedThreadPool();
        private final CountDownLatch closeRequested = new CountDownLatch(1);

        WebServer() throws IOException {
            server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
            server.setExecutor(executor);
        }

        void handle(HttpHandler handler) {
            server.createContext("/", handler);
            server.start();
        }

        String url() {
            return "http://127.0.0.1:" + server.getAddress().getPort() + "/";
        }

        void waitForClose() {
            try {
                closeRequested.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        public void close() {
            closeRequested.countDown();
            server.stop(0);
            executor.shutdownNow();
        }
    }
}
