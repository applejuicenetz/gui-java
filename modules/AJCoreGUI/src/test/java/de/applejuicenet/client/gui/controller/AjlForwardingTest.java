package de.applejuicenet.client.gui.controller;

import de.applejuicenet.client.fassade.listener.CoreStatusListener.STATUS;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.junit.Assert.*;

public class AjlForwardingTest {
    @Rule public TemporaryFolder files = new TemporaryFolder();

    private File ajl(String name, String records) throws Exception {
        File file = files.newFile(name);
        Files.writeString(file.toPath(), "appleJuice Linkliste\n-----\n100\n" + records, StandardCharsets.UTF_8);
        return file;
    }

    private String send(LinkListener listener, String message) throws Exception {
        try (Socket socket = new Socket("localhost", listener.getListenerPort())) {
            socket.setSoTimeout(5000);
            Writer writer = new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8);
            writer.write(message + "\n");
            writer.flush();
            return new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8)).readLine();
        }
    }

    @Test
    public void osLinksReachRunningGuiAndWaitForCoreStartup() throws Exception {
        List<String> received = new CopyOnWriteArrayList<>();
        try (LinkListener listener = new LinkListener(0, () -> "secret", (link, dir) -> received.add(link))) {
            LinkListener.forwardLink(listener.getListenerPort(), "secret", "ajfsp://file%7CGrüße.bin%7Chash%7C5368709120/");
            assertTrue(received.isEmpty());
            listener.fireStatusChanged(STATUS.STARTED);
            LinkListener.forwardLink(listener.getListenerPort(), "secret", "ajfsp://file|zweite.bin|hash2|1024/");
            assertEquals(Arrays.asList("ajfsp://file|Grüße.bin|hash|5368709120/",
                    "ajfsp://file|zweite.bin|hash2|1024/"), received);
        }
    }

    @Test
    public void runningGuiReceivesMultipleFilesWithUnicodePaths() throws Exception {
        List<String> received = new CopyOnWriteArrayList<>();
        File first = ajl("Grüße mit Leerzeichen eins.AJL", "München.bin\nhash1\n5368709120\n");
        File second = ajl("zwei.ajl", "Größe %20.bin\nhash2\n2147483648\n");
        try (LinkListener listener = new LinkListener(0, () -> "secret", (link, dir) -> received.add(link))) {
            listener.fireStatusChanged(STATUS.STARTED);
            LinkListener.forwardAjl(listener.getListenerPort(), "secret", first);
            LinkListener.forwardAjl(listener.getListenerPort(), "secret", second);
            assertEquals(Arrays.asList("ajfsp://file|München.bin|hash1|5368709120/",
                    "ajfsp://file|Größe %20.bin|hash2|2147483648/"), received);
        }
    }

    @Test
    public void firstStartQueuesFilesUntilCoreIsReady() throws Exception {
        List<String> received = new CopyOnWriteArrayList<>();
        File file = ajl("start.ajl", "one.bin\nhash1\n1024\ntwo.bin\nhash2\n2048\n");
        try (LinkListener listener = new LinkListener(0, () -> "secret", (link, dir) -> received.add(dir + ":" + link))) {
            listener.processAjl(file, "/incoming");
            assertTrue(received.isEmpty());
            listener.fireStatusChanged(STATUS.STARTED);
            assertEquals(Arrays.asList("/incoming:ajfsp://file|one.bin|hash1|1024/",
                    "/incoming:ajfsp://file|two.bin|hash2|2048/"), received);
            listener.fireStatusChanged(STATUS.STARTED);
            assertEquals(2, received.size());
        }
    }

    @Test
    public void forwardedFilesAlsoQueueWhileCoreIsStarting() throws Exception {
        List<String> received = new CopyOnWriteArrayList<>();
        File file = ajl("wait.ajl", "wait.bin\nhash\n1024\n");
        try (LinkListener listener = new LinkListener(0, () -> "secret", (link, dir) -> received.add(link))) {
            LinkListener.forwardAjl(listener.getListenerPort(), "secret", file);
            assertTrue(received.isEmpty());
            listener.fireStatusChanged(STATUS.STARTED);
            assertEquals(Arrays.asList("ajfsp://file|wait.bin|hash|1024/"), received);
        }
    }

    @Test
    public void malformedRequestsDoNotStopListener() throws Exception {
        List<String> received = new CopyOnWriteArrayList<>();
        File file = ajl("valid.ajl", "valid.bin\nhash\n1024\n");
        try (LinkListener listener = new LinkListener(0, () -> "secret", (link, dir) -> received.add(link))) {
            listener.fireStatusChanged(STATUS.STARTED);
            assertEquals("ERROR", send(listener, "secret|-ajl=!bad-base64"));
            assertEquals("ERROR", send(listener, "secret|-link=invalid"));
            assertEquals("ERROR", send(listener, ""));
            LinkListener.forwardAjl(listener.getListenerPort(), "secret", file);
            assertEquals(1, received.size());
        }
    }

    @Test
    public void wrongPasswordRejectedAndLegacyLinksStillWork() throws Exception {
        List<String> received = new CopyOnWriteArrayList<>();
        File file = ajl("valid.ajl", "valid.bin\nhash\n1024\n");
        try (LinkListener listener = new LinkListener(0, () -> "secret", (link, dir) -> received.add(link))) {
            listener.fireStatusChanged(STATUS.STARTED);
            assertThrows(java.io.IOException.class, () -> LinkListener.forwardAjl(listener.getListenerPort(), "secretExtra", file));
            assertTrue(received.isEmpty());
            assertEquals("OK", send(listener, "secret|-link=ajfsp://file%7Ctest.bin%7Chash%7C1024/"));
            assertEquals("OK", send(listener, "secret|ajfsp://file|raw.bin|hash|1024/"));
            assertEquals(Arrays.asList("ajfsp://file|test.bin|hash|1024/", "ajfsp://file|raw.bin|hash|1024/"), received);
        }
    }

    @Test
    public void incompleteAjlDoesNotImportPartialFileList() throws Exception {
        List<String> received = new CopyOnWriteArrayList<>();
        File file = ajl("incomplete.ajl", "valid.bin\nhash\n1024\nbroken.bin\nhash\n");
        try (LinkListener listener = new LinkListener(0, () -> "secret", (link, dir) -> received.add(link))) {
            listener.fireStatusChanged(STATUS.STARTED);
            assertThrows(java.io.IOException.class, () -> LinkListener.forwardAjl(listener.getListenerPort(), "secret", file));
            assertTrue(received.isEmpty());
        }
    }
}
