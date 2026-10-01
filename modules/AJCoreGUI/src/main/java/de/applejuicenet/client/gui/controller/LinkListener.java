/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */
package de.applejuicenet.client.gui.controller;

import de.applejuicenet.client.AppleJuiceClient;
import de.applejuicenet.client.fassade.ApplejuiceFassade;
import de.applejuicenet.client.fassade.exception.IllegalArgumentException;
import de.applejuicenet.client.fassade.listener.CoreStatusListener;
import de.applejuicenet.client.shared.AjlFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class LinkListener extends Thread implements CoreStatusListener, AutoCloseable {
    private static final Logger logger = LoggerFactory.getLogger(LinkListener.class);
    private final ServerSocket listen;
    private final Supplier<String> passwordSupplier;
    private final BiConsumer<String, String> linkProcessor;
    private final List<Link> linkCache = new ArrayList<>();
    private boolean ready;

    public LinkListener() throws IOException {
        this(OptionsManagerImpl.getInstance().getLinkListenerPort(),
                () -> OptionsManagerImpl.getInstance().getRemoteSettings().getOldPassword(),
                LinkListener::submitToCore);
        ApplejuiceFassade.addCoreStatusListener(this);
    }

    LinkListener(int port, Supplier<String> passwordSupplier, BiConsumer<String, String> linkProcessor) throws IOException {
        this.passwordSupplier = passwordSupplier;
        this.linkProcessor = linkProcessor;
        listen = new ServerSocket(port, 50, InetAddress.getByName("localhost"));
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
                    logger.warn("Link-/AJL-Übergabe fehlgeschlagen", e);
                    writer.write("ERROR\n");
                }
                writer.flush();
            } catch (SocketException e) {
                if (!listen.isClosed()) {
                    logger.warn("LinkListener-Verbindung fehlgeschlagen", e);
                }
            } catch (IOException e) {
                logger.warn("LinkListener-Verbindung fehlgeschlagen", e);
            }
        }
    }

    private boolean handleMessage(String line) throws IOException {
        String prefix = passwordSupplier.get() + "|";
        if (line == null || !line.startsWith(prefix)) {
            return false;
        }
        String command = line.substring(prefix.length());
        if (command.startsWith("-ajl=")) {
            String path = new String(Base64.getUrlDecoder().decode(command.substring(5)), StandardCharsets.UTF_8);
            processAjl(new File(path), "");
            return true;
        }
        if (command.startsWith("-link=") || command.startsWith("ajfsp://")) {
            int start = command.indexOf("ajfsp://");
            if (start >= 0) {
                processLink(command.substring(start), "");
                return true;
            }
        }
        return false;
    }

    public static void forwardAjl(File file) throws IOException {
        forwardAjl(OptionsManagerImpl.getInstance().getLinkListenerPort(),
                OptionsManagerImpl.getInstance().getRemoteSettings().getOldPassword(), file);
    }

    static void forwardAjl(int port, String password, File file) throws IOException {
        String path = Base64.getUrlEncoder().encodeToString(file.getAbsolutePath().getBytes(StandardCharsets.UTF_8));
        try (Socket socket = new Socket("localhost", port)) {
            socket.setSoTimeout(5000);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
            writer.write(password + "|-ajl=" + path + "\n");
            writer.flush();
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            if (!"OK".equals(reader.readLine())) {
                throw new IOException("AJL-Datei wurde von der laufenden GUI nicht angenommen");
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
            ApplejuiceFassade.removeCoreStatusListener(this);
            for (Link link : linkCache) {
                linkProcessor.accept(link.link, link.directory);
            }
            linkCache.clear();
        }
    }

    public void processLink(String link, String directory) {
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
            AppleJuiceClient.getAjFassade().processLink(link, directory);
        } catch (IllegalArgumentException e) {
            logger.warn(ApplejuiceFassade.ERROR_MESSAGE, e);
        }
    }

    int getListenerPort() {
        return listen.getLocalPort();
    }

    @Override
    public void close() throws IOException {
        ApplejuiceFassade.removeCoreStatusListener(this);
        listen.close();
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
