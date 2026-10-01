package de.applejuicenet.client.fassade.controller.xml;

import de.applejuicenet.client.fassade.controller.CoreConnectionSettingsHolder;
import de.applejuicenet.client.fassade.entity.Share;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class ShareRefreshTest {
    private String share(int id, String name, long size) {
        return "<share id='" + id + "' filename='/share/" + name + "' shortfilename='" + name
                + "' size='" + size + "' checksum='hash' priority='1' lastasked='0' askcount='0' searchcount='0'/>";
    }

    @Test
    public void refreshRemovesDeletedFilesAndReplacesMovedFiles() throws Exception {
        String first = "<applejuice>" + share(1, "old.bin", 3L << 30) + share(2, "deleted.bin", 2L << 30) + "</applejuice>";
        String next = "<applejuice>" + share(1, "moved.bin", 1L << 30) + "</applejuice>";
        try (CoreResponses core = new CoreResponses(first, next, "<applejuice/>")) {
            ShareXMLHolder holder = core.holder();
            Map<Integer, Share> original = holder.getShare();
            assertEquals(2, original.size());
            Map<Integer, Share> refreshed = holder.getShare();
            assertEquals(1, refreshed.size());
            assertFalse(refreshed.containsKey(2));
            assertEquals("moved.bin", refreshed.get(1).getShortfilename());
            assertEquals(1L << 30, refreshed.values().stream().mapToLong(Share::getSize).sum());
            assertEquals("old.bin", original.get(1).getShortfilename());
            assertTrue(holder.getShare().isEmpty());
            core.await();
        }
    }

    @Test
    public void failedRefreshKeepsPreviousSnapshotIntact() throws Exception {
        String first = "<applejuice>" + share(1, "kept.bin", 1024) + "</applejuice>";
        try (CoreResponses core = new CoreResponses(first, "<applejuice>" + share(2, "partial.bin", 2048))) {
            ShareXMLHolder holder = core.holder();
            Map<Integer, Share> original = holder.getShare();
            assertThrows(RuntimeException.class, holder::update);
            assertEquals(1, original.size());
            assertFalse(original.containsKey(2));
            java.lang.reflect.Field map = ShareXMLHolder.class.getDeclaredField("shareMap");
            map.setAccessible(true);
            assertSame(original, map.get(holder));
            core.await();
        }
    }

    private static class CoreResponses implements AutoCloseable {
        private final ServerSocket server;
        private final ExecutorService executor = Executors.newSingleThreadExecutor();
        private final Future<?> task;

        CoreResponses(String... responses) throws Exception {
            server = new ServerSocket(0, 50, InetAddress.getByName("127.0.0.1"));
            server.setSoTimeout(5000);
            task = executor.submit(() -> {
                try {
                    for (String response : responses) {
                        try (Socket client = server.accept()) {
                            client.setSoTimeout(5000);
                            BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream(), StandardCharsets.US_ASCII));
                            String line;
                            while ((line = reader.readLine()) != null && !line.isEmpty()) { }
                            byte[] body = response.getBytes(StandardCharsets.UTF_8);
                            // Match the core's response format consumed by HtmlLoader.
                            client.getOutputStream().write(("HTTP/1.1 200 OK\nContent-Length: " + body.length + "\n\n")
                                    .getBytes(StandardCharsets.US_ASCII));
                            client.getOutputStream().write(body);
                            client.getOutputStream().flush();
                        }
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }

        ShareXMLHolder holder() throws Exception {
            return new ShareXMLHolder(new CoreConnectionSettingsHolder("127.0.0.1", server.getLocalPort(), "", true));
        }

        void await() throws Exception { task.get(5, TimeUnit.SECONDS); }

        public void close() throws Exception {
            server.close();
            executor.shutdownNow();
        }
    }
}
