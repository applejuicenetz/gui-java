/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */
package de.applejuicenet.client.gui.controller;

import de.applejuicenet.client.AppleJuiceClient;
import de.applejuicenet.client.facade.ApplejuiceFacade;
import de.applejuicenet.client.facade.exception.IllegalArgumentException;
import de.applejuicenet.client.facade.listener.CoreStatusListener;
import de.applejuicenet.client.shared.AjlFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.function.BiConsumer;

public class LinkListener extends Thread implements CoreStatusListener, AutoCloseable {
    private static final String WEB_PREFIX = "web+ajfsp://";
    private static final Logger logger = LoggerFactory.getLogger(LinkListener.class);
    private final ServerSocket listen;
    private final InstanceLock instanceLock;
    private final String token;
    private final BiConsumer<String, String> linkProcessor;
    private final List<Link> linkCache = new ArrayList<>();
    private boolean ready;

    /** @throws InstanceLock.AlreadyRunningException if another GUI of this profile is running */
    public LinkListener() throws IOException {
        this(profileDirectory(), LinkListener::submitToCore);
        ApplejuiceFacade.addCoreStatusListener(this);
    }

    LinkListener(Path profile, BiConsumer<String, String> linkProcessor) throws IOException {
        this.linkProcessor = linkProcessor;
        instanceLock = InstanceLock.acquire(profile);
        try {
            listen = new ServerSocket(0, 50, InetAddress.getLoopbackAddress());
            token = instanceLock.publish(listen.getLocalPort());
        } catch (IOException | RuntimeException e) {
            instanceLock.close();
            throw e;
        }
        logger.debug("Link listener started on port {}", listen.getLocalPort());
        setName("LinkListenerThread");
        setDaemon(true);
        start();
    }

    @Override
    public void run() {
        while (!listen.isClosed()) {
            try (Socket client = listen.accept()) {
                client.setSoTimeout(5000);
                if (!client.getInetAddress().isLoopbackAddress()) {
                    continue;
                }
                BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream(), StandardCharsets.UTF_8));
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream(), StandardCharsets.UTF_8));
                try {
                    boolean accepted = handleMessage(reader.readLine());
                    writer.write(accepted ? "OK\n" : "ERROR\n");
                } catch (Exception e) {
                    logger.warn("Link/AJL hand-over failed", e);
                    writer.write("ERROR\n");
                }
                writer.flush();
            } catch (SocketException e) {
                if (!listen.isClosed()) {
                    logger.warn("LinkListener connection failed", e);
                }
            } catch (IOException e) {
                logger.warn("LinkListener connection failed", e);
            }
        }
    }

    private boolean handleMessage(String line) throws IOException {
        String prefix = token + "|";
        if (line == null || !line.startsWith(prefix)) {
            return false;
        }
        String command = line.substring(prefix.length());
        if (command.startsWith("-ajl=")) {
            String path = new String(Base64.getUrlDecoder().decode(command.substring(5)), StandardCharsets.UTF_8);
            processAjl(new File(path), "");
            return true;
        }
        if (command.startsWith("-link=") || command.startsWith("ajfsp://") || command.startsWith("web+ajfsp://")) {
            int start = command.indexOf("ajfsp://");
            if (start >= 0) {
                processLink(command.substring(start), "");
                return true;
            }
        }
        return false;
    }

    private static final long INFO_WAIT_MILLIS = 5000;

    /** Folder that holds the instance lock: the profile folder of this GUI. */
    private static Path profileDirectory() {
        return Path.of(AppleJuiceClient.getPath());
    }

    public static void forwardAjl(File file) throws IOException {
        forwardAjl(profileDirectory(), file);
    }

    static void forwardAjl(Path profile, File file) throws IOException {
        InstanceLock.Info info = InstanceLock.read(profile, INFO_WAIT_MILLIS);
        forwardAjl(info.port(), info.token(), file);
    }

    static void forwardAjl(int port, String token, File file) throws IOException {
        String path = Base64.getUrlEncoder().encodeToString(file.getAbsolutePath().getBytes(StandardCharsets.UTF_8));
        forwardMessage(port, token, "-ajl=" + path);
    }

    public static void forwardLink(String link) throws IOException {
        forwardLink(profileDirectory(), link);
    }

    static void forwardLink(Path profile, String link) throws IOException {
        InstanceLock.Info info = InstanceLock.read(profile, INFO_WAIT_MILLIS);
        forwardLink(info.port(), info.token(), link);
    }

    static void forwardLink(int port, String token, String link) throws IOException {
        forwardMessage(port, token, link);
    }

    private static void forwardMessage(int port, String token, String message) throws IOException {
        try (Socket socket = new Socket(InetAddress.getLoopbackAddress(), port)) {
            socket.setSoTimeout(5000);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
            writer.write(token + "|" + message + "\n");
            writer.flush();
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            if (!"OK".equals(reader.readLine())) {
                throw new IOException("The running GUI did not accept the link/AJL file");
            }
        }
    }

    public void processAjl(File file, String directory) throws IOException {
        for (String link : AjlFile.readLinks(file)) {
            queueLink(new Link(link, directory));
        }
    }

    @Override
    public synchronized void fireStatusChanged(STATUS newStatus) {
        if (newStatus == STATUS.STARTED && !ready) {
            ready = true;
            ApplejuiceFacade.removeCoreStatusListener(this);
            for (Link link : linkCache) {
                linkProcessor.accept(link.link, link.directory);
            }
            linkCache.clear();
        }
    }

    public void processLink(String link, String directory) {
        if (link.regionMatches(true, 0, WEB_PREFIX, 0, WEB_PREFIX.length())) {
            link = link.substring("web+".length());
        }
        link = link.replace("%7C", "|").replace("%20", ".");
        queueLink(new Link(link, directory));
    }

    private synchronized void queueLink(Link link) {
        if (ready) {
            linkProcessor.accept(link.link, link.directory);
        } else {
            linkCache.add(link);
        }
    }

    private static void submitToCore(String link, String directory) {
        try {
            AppleJuiceClient.getAjFacade().processLink(link, directory);
        } catch (IllegalArgumentException e) {
            logger.warn(ApplejuiceFacade.ERROR_MESSAGE, e);
        }
    }

    int getListenerPort() {
        return listen.getLocalPort();
    }

    String getToken() {
        return token;
    }

    @Override
    public void close() throws IOException {
        ApplejuiceFacade.removeCoreStatusListener(this);
        try {
            listen.close();
        } finally {
            instanceLock.close();
        }
    }

    private static class Link {
        private final String link;
        private final String directory;

        Link(String link, String directory) {
            this.link = link;
            this.directory = directory;
        }
    }
}
