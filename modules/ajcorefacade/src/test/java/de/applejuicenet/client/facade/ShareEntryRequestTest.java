package de.applejuicenet.client.facade;

import de.applejuicenet.client.facade.controller.CoreConnectionSettingsHolder;
import de.applejuicenet.client.facade.entity.ShareEntry.SHAREMODE;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.*;

public class ShareEntryRequestTest {
    private static String settingsXml(String... directories) {
        StringBuilder shares = new StringBuilder();
        for (String directory : directories) {
            shares.append("<directory name=\"").append(directory).append("\" sharemode=\"subdirectory\"/>");
        }
        return "<applejuice><nick>tester</nick><port>9851</port><xmlport>9851</xmlport>"
                + "<maxupload>100</maxupload><maxdownload>200</maxdownload><maxconnections>500</maxconnections>"
                + "<maxsourcesperfile>600</maxsourcesperfile><autoconnect>true</autoconnect>"
                + "<speedperslot>5</speedperslot><maxnewconnectionsperturn>10</maxnewconnectionsperturn>"
                + "<incomingdirectory>/in</incomingdirectory><temporarydirectory>/tmp</temporarydirectory>"
                + "<shares>" + shares + "</shares></applejuice>";
    }

    private static final class Core implements AutoCloseable {
        final List<String> setSettings = new CopyOnWriteArrayList<>();
        private final ServerSocket server;
        private final ExecutorService executor = Executors.newCachedThreadPool();
        private final String settings;

        Core(String settings) throws IOException {
            this.settings = settings;
            server = new ServerSocket(0, 50, InetAddress.getByName("127.0.0.1"));
            executor.submit(() -> {
                while (!server.isClosed()) {
                    try {
                        Socket client = server.accept();
                        executor.submit(() -> serve(client));
                    } catch (IOException closed) {
                        return;
                    }
                }
            });
        }

        CoreConnectionSettingsHolder holder() throws Exception {
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
                if (request.contains("/function/setsettings")) {
                    setSettings.add(URLDecoder.decode(request.split(" ")[1], StandardCharsets.UTF_8));
                    client.getOutputStream().write("HTTP/1.1 200 OK\nContent-Length: 2\n\nok"
                            .getBytes(StandardCharsets.US_ASCII));
                    return;
                }
                byte[] body = (request.contains("settings.xml") ? settings
                        : "<applejuice><session id=\"7\"/></applejuice>").getBytes(StandardCharsets.UTF_8);
                client.getOutputStream().write(("HTTP/1.1 200 OK\nContent-Length: " + body.length + "\n\n")
                        .getBytes(StandardCharsets.US_ASCII));
                client.getOutputStream().write(body);
            } catch (IOException ignored) {
            }
        }

        @Override
        public void close() throws IOException {
            server.close();
            executor.shutdownNow();
        }
    }

    private static int countShares(String request) {
        String value = request.replaceAll(".*[?&]countshares=([0-9]+).*", "$1");
        return Integer.parseInt(value);
    }

    private static long directoriesIn(String request) {
        return java.util.regex.Pattern.compile("[?&]sharedirectory[0-9]+=").matcher(request).results().count();
    }

    @Test(timeout = 15000)
    public void addingAFolderSendsTheSumOfOldAndNewEntriesAsCount() throws Exception {
        try (Core core = new Core(settingsXml("/a", "/b", "/c"))) {
            ApplejuiceFacade facade = new ApplejuiceFacade(core.holder());
            facade.addShareEntry(List.of("/new"), SHAREMODE.SUBDIRECTORY);
            assertEquals(1, core.setSettings.size());
            String request = core.setSettings.get(0);
            assertEquals("The announced count must match the number of directories that follow: " + request,
                    directoriesIn(request), countShares(request));
            assertEquals(4, countShares(request));
        }
    }

    @Test(timeout = 15000)
    public void addingToAnEmptyShareAndSeveralFoldersCountsCorrectly() throws Exception {
        try (Core core = new Core(settingsXml())) {
            ApplejuiceFacade facade = new ApplejuiceFacade(core.holder());
            facade.addShareEntry(List.of("/x", "/y"), SHAREMODE.SINGLEDIRECTORY);
            String request = core.setSettings.get(0);
            assertEquals(directoriesIn(request), countShares(request));
            assertEquals(2, countShares(request));
        }
    }

    @Test(timeout = 15000)
    public void removingAFolderSendsTheRemainingCount() throws Exception {
        try (Core core = new Core(settingsXml("/a", "/b", "/c"))) {
            ApplejuiceFacade facade = new ApplejuiceFacade(core.holder());
            facade.removeShareEntry(List.of("/b"));
            String request = core.setSettings.get(0);
            assertEquals(directoriesIn(request), countShares(request));
            assertEquals(2, countShares(request));
        }
    }
}
