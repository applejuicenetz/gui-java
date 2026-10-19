package de.applejuicenet.client.facade.controller.xml;

import de.applejuicenet.client.facade.controller.CoreConnectionSettingsHolder;
import de.applejuicenet.client.facade.entity.Directory;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.*;

public class DirectoryXMLHolderTest {
    private static final String LISTING = "<applejuice><filesystem seperator=\"/\">"
            + "<dir name=\"incoming\" isfilesystem=\"false\" type=\"3\" path=\"/data/incoming\"/>"
            + "<dir name=\"music\" isfilesystem=\"false\" type=\"3\" path=\"\"/>"
            + "</filesystem></applejuice>";

    private static class Core implements AutoCloseable {
        private final ServerSocket server;
        private final ExecutorService executor = Executors.newSingleThreadExecutor();

        Core(String body) throws Exception {
            server = new ServerSocket(0, 50, InetAddress.getByName("127.0.0.1"));
            executor.submit(() -> {
                while (!server.isClosed()) {
                    try (Socket client = server.accept()) {
                        client.setSoTimeout(5000);
                        BufferedReader reader = new BufferedReader(
                                new InputStreamReader(client.getInputStream(), StandardCharsets.US_ASCII));
                        String line;
                        while ((line = reader.readLine()) != null && !line.isEmpty()) {
                        }
                        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
                        client.getOutputStream().write(("HTTP/1.1 200 OK\nContent-Length: " + bytes.length + "\n\n")
                                .getBytes(StandardCharsets.US_ASCII));
                        client.getOutputStream().write(bytes);
                        client.getOutputStream().flush();
                    } catch (Exception closed) {
                        return null;
                    }
                }
                return null;
            });
        }

        DirectoryXMLHolder holder() throws Exception {
            return new DirectoryXMLHolder(new CoreConnectionSettingsHolder("127.0.0.1", server.getLocalPort(), "", true));
        }

        @Override
        public void close() throws Exception {
            server.close();
            executor.shutdownNow();
        }
    }

    @Test(timeout = 15000)
    public void listsFoldersAndBuildsMissingPaths() throws Exception {
        try (Core core = new Core(LISTING)) {
            List<Directory> directories = core.holder().getDirectories("/data");
            assertEquals(2, directories.size());
            assertEquals("incoming", directories.get(0).getName());
            assertEquals("/data/incoming", directories.get(0).getPath());
            assertEquals("a missing path is derived from the parent", "/data/music", directories.get(1).getPath());
        }
    }

    @Test(timeout = 15000)
    public void consecutiveCallsDoNotMixTheirResults() throws Exception {
        try (Core core = new Core(LISTING)) {
            DirectoryXMLHolder holder = core.holder();
            List<Directory> first = holder.getDirectories("/data");
            List<Directory> second = holder.getDirectories("/data");
            assertEquals(2, first.size());
            assertEquals("the first result must not grow with the second call", 2, first.size());
            assertEquals(2, second.size());
            assertNotSame(first, second);
        }
    }

    @Test(timeout = 30000)
    public void concurrentCallsKeepTheirOwnResults() throws Exception {
        // The share tree and the directory chooser both load folders; two lazy loads can overlap.
        try (Core core = new Core(LISTING)) {
            DirectoryXMLHolder holder = core.holder();
            ExecutorService pool = Executors.newFixedThreadPool(6);
            try {
                java.util.List<java.util.concurrent.Future<Integer>> results = new java.util.ArrayList<>();
                for (int i = 0; i < 24; i++) {
                    results.add(pool.submit(() -> holder.getDirectories("/data").size()));
                }
                for (java.util.concurrent.Future<Integer> result : results) {
                    assertEquals("every call must see exactly its own two folders", Integer.valueOf(2),
                            result.get(20, java.util.concurrent.TimeUnit.SECONDS));
                }
            } finally {
                pool.shutdownNow();
            }
        }
    }

    @Test(timeout = 15000)
    public void answerWithoutFilesystemElementIsReportedNotHiddenAsNullPointer() throws Exception {
        try (Core core = new Core("<applejuice></applejuice>")) {
            try {
                core.holder().getDirectories("/data");
                fail("an unusable answer must fail");
            } catch (RuntimeException expected) {
                assertFalse("the cause must say what is wrong, not be a bare NullPointerException: " + expected,
                        expected.getCause() instanceof NullPointerException);
            }
        }
    }
}
