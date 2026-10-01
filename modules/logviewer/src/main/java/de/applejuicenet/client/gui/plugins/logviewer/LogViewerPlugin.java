/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.plugins.logviewer;

import de.applejuicenet.client.AppleJuiceClient;
import de.applejuicenet.client.fassade.ApplejuiceFassade;
import de.applejuicenet.client.gui.plugins.PluginConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.nio.file.Files;
import java.util.Map;
import java.util.Properties;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/plugin_src/logviewer/src/de/applejuicenet/client/gui/plugins/logviewer/LogViewerPlugin.java,v 1.4 2009/01/23 09:58:24 maj0r Exp $
 *
 * <p>Titel: AppleJuice Core-GUI</p>
 * <p>Beschreibung: GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: GPL</p>
 *
 * @author loevenwong <timo@loevenwong.de>
 */
public class LogViewerPlugin extends PluginConnector {
    private static Logger logger;
    private static final String path = AppleJuiceClient.getPath() + File.separator + "logs";
    private final JTextPane logPane = new JTextPane();
    private final SortedStringListModel listModel = new SortedStringListModel();
    private final JList list = new JList(listModel);

    public LogViewerPlugin(Properties pluginsProperties, Map<String, Properties> languageFiles, ImageIcon icon,
                           Map<String, ImageIcon> availableIcons) {
        super(pluginsProperties, languageFiles, icon, availableIcons);
        logger = LoggerFactory.getLogger(getClass());
        try {
            setLayout(new BorderLayout());
            logPane.setBackground(getBackground());
            logPane.setContentType("text/html");
            logPane.setEditable(false);
            logPane.setBorder(null);
            list.setCellRenderer(new FileNameListCellRenderer());
            JButton deleteAllButton = new JButton("Alle Logs löschen");

            deleteAllButton.addActionListener(e -> {
                logger.debug("LogViewer: Button 'Alle Logs loeschen' ausgeloest");
                deleteAllLogfiles();
            });
            JPanel listPanel = new JPanel(new BorderLayout());

            listPanel.add(new JScrollPane(list), BorderLayout.CENTER);
            listPanel.add(deleteAllButton, BorderLayout.SOUTH);
            JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listPanel, new JScrollPane(logPane));
            add(splitPane, BorderLayout.CENTER);
            readLogDir();
            list.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent event) {
                    showContextMenu(event);
                }

                @Override
                public void mouseReleased(MouseEvent event) {
                    showContextMenu(event);
                }
            });
            list.addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) {
                    doDisplayLogfile();
                }
            });
        } catch (Exception e) {
            logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
        }
    }

    private void showContextMenu(MouseEvent event) {
        if (!event.isPopupTrigger()) {
            return;
        }

        logger.debug("LogViewer: Kontextmenue angefordert bei ({}, {})", event.getX(), event.getY());
        int index = list.locationToIndex(event.getPoint());

        if (index < 0 || !list.getCellBounds(index, index).contains(event.getPoint())) {
            return;
        }

        list.setSelectedIndex(index);
        logger.debug("LogViewer: Kontextmenue fuer Eintrag {}", index);
        File logFile = (File) listModel.getElementAt(index);
        JPopupMenu popup = new JPopupMenu();
        JMenuItem deleteItem = new JMenuItem("Löschen");

        deleteItem.addActionListener(e -> deleteLogfile(logFile));
        popup.add(deleteItem);
        popup.show(list, event.getX(), event.getY());
    }

    private void deleteLogfile(File logFile) {
        logger.debug("LogViewer: Loeschen angeklickt fuer {}", logFile.getAbsolutePath());
        int answer = JOptionPane.showConfirmDialog(this, "Logdatei \"" + logFile.getName() + "\" wirklich löschen?",
                                                   "Logdatei löschen", JOptionPane.YES_NO_OPTION,
                                                   JOptionPane.WARNING_MESSAGE);

        logger.debug("LogViewer: Rueckfrage beantwortet mit {} (YES={})", answer, JOptionPane.YES_OPTION);

        if (answer != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            logger.debug("LogViewer: loesche {} (existiert={}, schreibbar={}, Ordner schreibbar={})",
                         logFile.getAbsolutePath(), logFile.exists(), logFile.canWrite(),
                         logFile.getParentFile() != null && logFile.getParentFile().canWrite());
            Files.delete(logFile.toPath());
            logger.debug("LogViewer: geloescht, existiert noch={}", logFile.exists());
            listModel.remove(logFile);
            logPane.setText("");
        } catch (Exception e) {
            logger.error("LogViewer: Loeschen fehlgeschlagen: " + logFile.getAbsolutePath(), e);
            JOptionPane.showMessageDialog(this, "Logdatei konnte nicht gelöscht werden:\n" + e.getMessage(),
                                          "Logdatei löschen", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteAllLogfiles() {
        File[] logFiles = listModel.getFiles();

        logger.debug("LogViewer: Alle Logs loeschen angeklickt, {} Eintraege, Ordner {}", logFiles.length, path);

        if (logFiles.length == 0) {
            return;
        }

        int answer = JOptionPane.showConfirmDialog(this, "Alle " + logFiles.length + " Logdateien wirklich löschen?",
                                                   "Alle Logs löschen", JOptionPane.YES_NO_OPTION,
                                                   JOptionPane.WARNING_MESSAGE);

        logger.debug("LogViewer: Rueckfrage beantwortet mit {} (YES={})", answer, JOptionPane.YES_OPTION);

        if (answer != JOptionPane.YES_OPTION) {
            return;
        }

        int failed = 0;

        for (File logFile : logFiles) {
            try {
                Files.delete(logFile.toPath());
                logger.debug("LogViewer: geloescht {}", logFile.getName());
                listModel.remove(logFile);
            } catch (Exception e) {
                failed++;
                logger.error("LogViewer: Loeschen fehlgeschlagen: " + logFile.getAbsolutePath(), e);
            }
        }

        logger.debug("LogViewer: Alle loeschen fertig, fehlgeschlagen={}, verbleibend={}", failed, listModel.getSize());
        logPane.setText("");

        if (failed > 0) {
            JOptionPane.showMessageDialog(this, failed + " Logdateien konnten nicht gelöscht werden.",
                                          "Alle Logs löschen", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void doDisplayLogfile() {
        try {
            File selectedLog = (File) list.getSelectedValue();

            if (selectedLog == null) {
                return;
            }

            logPane.setPage("file://localhost/" + selectedLog.getAbsolutePath());
        } catch (Exception e) {
            logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
        }
    }

    private void readLogDir() {
        File logPath = new File(path);

        logger.debug("LogViewer: lese Logordner {} (Ordner={})", logPath.getAbsolutePath(), logPath.isDirectory());

        if (!logPath.isDirectory()) {
            return;
        }

        File[] htmlFiles = logPath.listFiles((dir, name) -> name.endsWith(".html"));

        listModel.setData(htmlFiles);
    }

    public void fireLanguageChanged() {
    }

    public void registerSelected() {
    }

    public void fireContentChanged(de.applejuicenet.client.fassade.listener.DataUpdateListener.DATALISTENER_TYPE arg0, Object arg1) {
    }
}
