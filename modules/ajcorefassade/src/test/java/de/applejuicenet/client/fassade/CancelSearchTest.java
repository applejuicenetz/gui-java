package de.applejuicenet.client.fassade;

import de.applejuicenet.client.fassade.controller.CoreConnectionSettingsHolder;
import de.applejuicenet.client.fassade.entity.Search;
import de.applejuicenet.client.fassade.entity.SearchEntry;
import de.applejuicenet.client.fassade.shared.FileType;
import de.applejuicenet.client.fassade.shared.ZLibUtils;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

public class CancelSearchTest {
    @Test(timeout = 15000)
    public void blockedCancellationDoesNotOverlapAndStopClosesConnection() throws Exception {
        try (CoreStub core = new CoreStub("cancelsearch", false)) {
            ApplejuiceFassade fassade = new ApplejuiceFassade(core.settings());
            try {
                Search search = search(101, 0);
                fassade.cancelSearch(search);
                assertTrue(core.blockedRequest.await(5, TimeUnit.SECONDS));
                for (int index = 0; index < 10; index++) {
                    fassade.cancelSearch(search);
                }
                Thread.sleep(4500);
                assertEquals(1, core.cancelRequests.get());
                assertEquals(1, core.maxActiveRequests.get());
                assertEquals(1, workerCount("ApplejuiceFassadeCancelSearch-101"));
                fassade.stopXMLCheck();
                assertTrue(core.clientClosed.await(2, TimeUnit.SECONDS));
                awaitWorkerCount("ApplejuiceFassadeCancelSearch-101", 0);
                assertEquals(1, core.cancelRequests.get());
            } finally {
                fassade.stopXMLCheck();
            }
        }
    }

    @Test(timeout = 15000)
    public void failedCancellationRetriesSeriallyAndSuccessfulWorkerIsRemoved() throws Exception {
        try (CoreStub core = new CoreStub(null, true)) {
            ApplejuiceFassade fassade = new ApplejuiceFassade(core.settings());
            try {
                Search search = search(102, 0);
                fassade.cancelSearch(search);
                assertTrue(core.secondCancelRequest.await(7, TimeUnit.SECONDS));
                awaitWorkerCount("ApplejuiceFassadeCancelSearch-102", 0);
                assertEquals(2, core.cancelRequests.get());
                assertEquals(1, core.maxActiveRequests.get());
                fassade.cancelSearch(search);
                assertTrue(core.thirdCancelRequest.await(2, TimeUnit.SECONDS));
                awaitWorkerCount("ApplejuiceFassadeCancelSearch-102", 0);
                assertEquals(3, core.cancelRequests.get());
            } finally {
                fassade.stopXMLCheck();
            }
        }
    }

    @Test(timeout = 10000)
    public void stopDuringInitialDelayDoesNotSendCancellation() throws Exception {
        try (CoreStub core = new CoreStub(null, false)) {
            ApplejuiceFassade fassade = new ApplejuiceFassade(core.settings());
            try {
                fassade.cancelSearch(search(103, System.currentTimeMillis()));
                awaitWorkerCount("ApplejuiceFassadeCancelSearch-103", 1);
                fassade.stopXMLCheck();
                awaitWorkerCount("ApplejuiceFassadeCancelSearch-103", 0);
                assertEquals(0, core.cancelRequests.get());
            } finally {
                fassade.stopXMLCheck();
            }
        }
    }

    @Test(timeout = 15000)
    public void stopClosesBlockedPollingConnectionAndAllowsRestart() throws Exception {
        try (CoreStub core = new CoreStub("modified.xml", false)) {
            ApplejuiceFassade fassade = new ApplejuiceFassade(core.settings());
            try {
                fassade.startXMLCheck();
                assertTrue(core.blockedRequest.await(5, TimeUnit.SECONDS));
                fassade.stopXMLCheck();
                assertTrue(core.clientClosed.await(2, TimeUnit.SECONDS));
                awaitWorkerCount("ApplejuiceFassadeXMLCheckThread", 0);
                fassade.startXMLCheck();
                assertTrue(core.secondBlockedRequest.await(5, TimeUnit.SECONDS));
                fassade.stopXMLCheck();
                awaitWorkerCount("ApplejuiceFassadeXMLCheckThread", 0);
            } finally {
                fassade.stopXMLCheck();
            }
        }
    }

    @Test(timeout = 10000)
    public void stopDuringStartupClosesConnectionWithoutReportingFailure() throws Exception {
        try (CoreStub core = new CoreStub("information.xml", false)) {
            ApplejuiceFassade fassade = new ApplejuiceFassade(core.settings());
            AtomicReference<Throwable> failure = new AtomicReference<>();
            try {
                fassade.startXMLCheck();
                assertTrue(core.blockedRequest.await(5, TimeUnit.SECONDS));
                Thread poller = Thread.getAllStackTraces().keySet().stream()
                        .filter(thread -> thread.isAlive() && thread.getName().equals("ApplejuiceFassadeXMLCheckThread"))
                        .findFirst().orElseThrow();
                poller.setUncaughtExceptionHandler((thread, error) -> failure.set(error));
                fassade.stopXMLCheck();
                assertTrue(core.clientClosed.await(2, TimeUnit.SECONDS));
                poller.join(2000);
                assertFalse(poller.isAlive());
                assertNull(failure.get());
            } finally {
                fassade.stopXMLCheck();
            }
        }
    }

    private static void awaitWorkerCount(String name, int expected) throws InterruptedException {
        long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(5);
        while (workerCount(name) != expected && System.nanoTime() < deadline) {
            Thread.sleep(10);
        }
        assertEquals(expected, workerCount(name));
    }

    private static int workerCount(String name) {
        int count = 0;
        for (Thread thread : Thread.getAllStackTraces().keySet()) {
            if (thread.isAlive() && thread.getName().equals(name)) {
                count++;
            }
        }
        return count;
    }

    private static Search search(int id, long creationTime) {
        return new Search() {
            public long getCreationTime() { return creationTime; }
            public int getId() { return id; }
            public boolean isChanged() { return false; }
            public String getSuchText() { return "test"; }
            public int getOffeneSuchen() { return 0; }
            public int getGefundenDateien() { return 0; }
            public int getDurchsuchteClients() { return 0; }
            public void addFilter(FileType filter) { }
            public void removeFilter(FileType filter) { }
            public void clearFilter() { }
            public List<SearchEntry> getAllSearchEntries() { return Collections.emptyList(); }
            public SearchEntry getSearchEntryById(int entryId) { return null; }
            public List<SearchEntry> getSearchEntries() { return Collections.emptyList(); }
            public boolean isRunning() { return true; }
            public void setChanged(boolean changed) { }
            public long getEntryCount() { return 0; }
        };
    }

    private static class CoreStub implements AutoCloseable {
        private final ServerSocket server;
        private final ExecutorService executor = Executors.newCachedThreadPool();
        private final Set<Socket> clients = ConcurrentHashMap.newKeySet();
        private final String blockedEndpoint;
        private final boolean failFirstCancellation;
        private final AtomicInteger cancelRequests = new AtomicInteger();
        private final AtomicInteger activeRequests = new AtomicInteger();
        private final AtomicInteger maxActiveRequests = new AtomicInteger();
        private final AtomicInteger blockedRequests = new AtomicInteger();
        private final CountDownLatch blockedRequest = new CountDownLatch(1);
        private final CountDownLatch secondBlockedRequest = new CountDownLatch(1);
        private final CountDownLatch clientClosed = new CountDownLatch(1);
        private final CountDownLatch secondCancelRequest = new CountDownLatch(1);
        private final CountDownLatch thirdCancelRequest = new CountDownLatch(1);

        CoreStub(String blockedEndpoint, boolean failFirstCancellation) throws IOException {
            this.blockedEndpoint = blockedEndpoint;
            this.failFirstCancellation = failFirstCancellation;
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

        CoreConnectionSettingsHolder settings() throws Exception {
            return new CoreConnectionSettingsHolder("127.0.0.1", server.getLocalPort(), "", true);
        }

        private void serve(Socket client) {
            boolean cancellation = false;
            try (client) {
                client.setSoTimeout(10000);
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(client.getInputStream(), StandardCharsets.US_ASCII));
                String request = reader.readLine();
                String line;
                while ((line = reader.readLine()) != null && !line.isEmpty()) {
                }
                cancellation = request.contains("cancelsearch");
                int cancelCount = 0;
                if (cancellation) {
                    maxActiveRequests.accumulateAndGet(activeRequests.incrementAndGet(), Math::max);
                    cancelCount = cancelRequests.incrementAndGet();
                    if (cancelCount == 2) {
                        secondCancelRequest.countDown();
                    } else if (cancelCount == 3) {
                        thirdCancelRequest.countDown();
                    }
                }
                if (blockedEndpoint != null && request.contains(blockedEndpoint)) {
                    if (blockedRequests.incrementAndGet() == 2) {
                        secondBlockedRequest.countDown();
                    }
                    blockedRequest.countDown();
                    assertEquals(-1, reader.read());
                    clientClosed.countDown();
                } else if (cancellation) {
                    String status = failFirstCancellation && cancelCount == 1 ? "503 Unavailable" : "200 OK";
                    client.getOutputStream().write(("HTTP/1.1 " + status + "\n").getBytes(StandardCharsets.US_ASCII));
                } else {
                    String body = request.contains("getsession.xml")
                            ? "<applejuice><session id=\"7\"/></applejuice>"
                            : "<applejuice><version>0.35.185.93</version></applejuice>";
                    byte[] bytes = request.contains("mode=zip") ? ZLibUtils.compress(body)
                            : body.getBytes(StandardCharsets.UTF_8);
                    client.getOutputStream().write(("HTTP/1.1 200 OK\nContent-Length: " + bytes.length + "\n\n")
                            .getBytes(StandardCharsets.US_ASCII));
                    client.getOutputStream().write(bytes);
                }
            } catch (IOException closed) {
            } finally {
                if (cancellation) {
                    activeRequests.decrementAndGet();
                }
                clients.remove(client);
            }
        }

        public void close() throws Exception {
            server.close();
            for (Socket client : clients) {
                client.close();
            }
            executor.shutdownNow();
            assertTrue(executor.awaitTermination(2, TimeUnit.SECONDS));
        }
    }
}
