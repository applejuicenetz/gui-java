package de.applejuicenet.client.gui.controller;

import javax.swing.JComponent;
import javax.swing.TransferHandler;
import java.awt.Component;
import java.awt.Container;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ContainerAdapter;
import java.awt.event.ContainerEvent;
import java.awt.event.InputEvent;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

public final class AjfspTransferHandler extends TransferHandler {
    private final TransferHandler original;
    private final Consumer<String> linkConsumer;

    private AjfspTransferHandler(TransferHandler original, Consumer<String> linkConsumer) {
        this.original = original;
        this.linkConsumer = linkConsumer;
    }

    public static void install(Component component, Consumer<String> linkConsumer) {
        if (component instanceof JComponent swingComponent) {
            if (swingComponent.getTransferHandler() instanceof AjfspTransferHandler) {
                return;
            }
            swingComponent.setTransferHandler(new AjfspTransferHandler(
                    swingComponent.getTransferHandler(), linkConsumer));
        }
        if (component instanceof Container container) {
            for (Component child : container.getComponents()) {
                install(child, linkConsumer);
            }
            container.addContainerListener(new ContainerAdapter() {
                @Override
                public void componentAdded(ContainerEvent event) {
                    install(event.getChild(), linkConsumer);
                }
            });
        }
    }

    static List<String> readLinks(Transferable transferable) {
        LinkedHashSet<String> links = new LinkedHashSet<>();
        for (DataFlavor flavor : transferable.getTransferDataFlavors()) {
            try {
                String text;
                if (flavor.isFlavorTextType()) {
                    try (BufferedReader reader = new BufferedReader(flavor.getReaderForText(transferable))) {
                        text = reader.lines().collect(java.util.stream.Collectors.joining("\n"));
                    }
                } else if (flavor.isMimeTypeEqual("application/x-java-url")) {
                    text = transferable.getTransferData(flavor).toString();
                } else {
                    continue;
                }
                for (String line : text.split("\\R")) {
                    String link = line.trim().replaceAll("(?i)%7c", "|");
                    if (link.regionMatches(true, 0, "web+ajfsp://", 0, 12)) {
                        link = link.substring(4);
                    }
                    String lower = link.toLowerCase(Locale.ROOT);
                    int requiredSeparators;
                    if (lower.startsWith("ajfsp://file|")) {
                        requiredSeparators = 3;
                    } else if (lower.startsWith("ajfsp://server|")) {
                        requiredSeparators = 2;
                    } else {
                        continue;
                    }
                    if (link.chars().filter(character -> character == '|').count() >= requiredSeparators) {
                        links.add(link);
                    }
                }
            } catch (Exception ignored) {
            }
        }
        return new ArrayList<>(links);
    }

    boolean importLinks(Transferable transferable) {
        List<String> links = readLinks(transferable);
        links.forEach(linkConsumer);
        return !links.isEmpty();
    }

    @Override
    public boolean canImport(TransferSupport support) {
        if (support.isDrop() && !readLinks(support.getTransferable()).isEmpty()) {
            if ((support.getSourceDropActions() & COPY) != 0) {
                support.setDropAction(COPY);
            }
            return true;
        }
        return original != null && original.canImport(support);
    }

    @Override
    public boolean importData(TransferSupport support) {
        if (support.isDrop() && importLinks(support.getTransferable())) {
            return true;
        }
        return original != null && original.importData(support);
    }

    @Override
    public boolean canImport(JComponent component, DataFlavor[] flavors) {
        return original != null && original.canImport(component, flavors);
    }

    @Override
    public boolean importData(JComponent component, Transferable transferable) {
        return original != null && original.importData(component, transferable);
    }

    @Override
    public int getSourceActions(JComponent component) {
        return original == null ? NONE : original.getSourceActions(component);
    }

    @Override
    public void exportAsDrag(JComponent component, InputEvent event, int action) {
        if (original != null) {
            original.exportAsDrag(component, event, action);
        }
    }

    @Override
    public void exportToClipboard(JComponent component, Clipboard clipboard, int action) {
        if (original != null) {
            original.exportToClipboard(component, clipboard, action);
        }
    }
}
