/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */
package de.applejuicenet.client.gui.download;


/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/download/DownloadOverviewPanel.java,v 1.8 2009/02/01 14:49:17 maj0r Exp $
 *
 * <p>Title: AppleJuice Client-GUI</p>
 * <p>Description: Official GUI for the appleJuice core developed by muhviehstarr</p>
 * <p>Copyright: General Public License</p>
 *
 * @author Maj0r <aj@tkl-soft.de>
 */

import de.applejuicenet.client.AppleJuiceClient;
import de.applejuicenet.client.facade.ApplejuiceFacade;
import de.applejuicenet.client.facade.entity.Download;
import de.applejuicenet.client.facade.entity.DownloadSource;
import de.applejuicenet.client.facade.entity.PartList;
import de.applejuicenet.client.facade.exception.WebSiteNotFoundException;
import de.applejuicenet.client.facade.shared.RefreshIntervals;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.listener.LanguageListener;
import de.applejuicenet.client.shared.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

public class DownloadOverviewPanel extends JPanel implements LanguageListener {
    private DownloadPartListPanel actualDlOverviewTable;
    private JLabel actualDLFileName = new JLabel();
    private JLabel label5 = new JLabel();
    private JLabel label4 = new JLabel();
    private JLabel label3 = new JLabel();
    private JLabel label2 = new JLabel();
    private JLabel label1 = new JLabel();
    private final Logger logger;
    private JCheckBox fetchList = new JCheckBox();
    private PartListWorkerThread partListWorkerThread = null;
    private DownloadPanel downloadPanel;
    private String available;
    private DecimalFormat decimalFormat = new DecimalFormat("#.##");
    private Object lastPartlistObject = null;

    public DownloadOverviewPanel(DownloadPanel parent) {
        logger = LoggerFactory.getLogger(getClass());
        try {
            downloadPanel = parent;
            actualDlOverviewTable = DownloadPartListPanel.getInstance();
            init();
            LanguageSelector.getInstance().addLanguageListener(this);
        } catch (Exception e) {
            logger.error(ApplejuiceFacade.ERROR_MESSAGE, e);
        }
    }

    private void init() {
        fetchList.setSelected(Settings.getSettings().isDownloadOverview());
        fetchList.addActionListener(e -> {
            if (!fetchList.isSelected() && null != partListWorkerThread) {
                partListWorkerThread.interrupt();
                partListWorkerThread = null;
                actualDLFileName.setText(null);
                actualDlOverviewTable.setPartList(null, null);
            } else if (fetchList.isSelected() && null != lastPartlistObject) {
                if (lastPartlistObject instanceof Download) {
                    setDownload((Download) lastPartlistObject);
                } else {
                    setDownloadSource((DownloadSource) lastPartlistObject);
                }
            }

            Settings settings = Settings.getSettings();

            settings.setDownloadOverview(fetchList.isSelected());
            settings.save();
        });
        setLayout(new BorderLayout());
        JPanel tempPanel1 = new JPanel();

        tempPanel1.setLayout(new FlowLayout());

        JLabel yellow = new JLabel("     ");

        yellow.setOpaque(true);
        yellow.setBackground(Color.YELLOW);
        tempPanel1.add(yellow);
        tempPanel1.add(label5);

        JLabel blue = new JLabel("     ");

        blue.setOpaque(true);
        blue.setBackground(Color.BLUE);
        tempPanel1.add(blue);
        tempPanel1.add(label4);

        JLabel red = new JLabel("     ");

        red.setOpaque(true);
        red.setBackground(Color.RED);
        tempPanel1.add(red);
        tempPanel1.add(label3);

        JLabel black = new JLabel("     ");

        black.setOpaque(true);
        black.setBackground(Color.BLACK);
        tempPanel1.add(black);
        tempPanel1.add(label2);

        JLabel green = new JLabel("     ");

        green.setOpaque(true);
        green.setBackground(Color.GREEN);
        tempPanel1.add(green);
        tempPanel1.add(label1);

        JPanel panel3 = new JPanel(new BorderLayout());

        panel3.add(fetchList, BorderLayout.WEST);
        panel3.add(tempPanel1, BorderLayout.CENTER);

        add(panel3, BorderLayout.NORTH);
        actualDLFileName.setPreferredSize(new Dimension(actualDLFileName.getPreferredSize().width, 17));
        JPanel panel1 = new JPanel(new BorderLayout());

        panel1.add(actualDLFileName, BorderLayout.NORTH);
        panel1.add(actualDlOverviewTable, BorderLayout.CENTER);
        add(panel1, BorderLayout.CENTER);
    }

    public void setDownload(Download download) {
        lastPartlistObject = download;
        if (!fetchList.isSelected()) {
            return;
        }

        try {
            if (partListWorkerThread != null) {
                if (partListWorkerThread.isInterrupted()) {
                    partListWorkerThread = null;
                } else {
                    if (partListWorkerThread.getObjectDO() == download) {
                        return;
                    } else {
                        partListWorkerThread.cancel();
                        partListWorkerThread = null;
                    }
                }
            }

            partListWorkerThread = new PartListWorkerThread();
            partListWorkerThread.setDownload(download);
            partListWorkerThread.start();
        } catch (Exception e) {
            if (partListWorkerThread != null) {
                partListWorkerThread.cancel();
                partListWorkerThread = null;
            }

            logger.error(ApplejuiceFacade.ERROR_MESSAGE, e);
        }
    }

    /** Returns the currently shown source or null when the part list shows a download or nothing. */
    public DownloadSource getCurrentSource() {
        return lastPartlistObject instanceof DownloadSource source ? source : null;
    }

    public void setDownloadSource(DownloadSource downloadSource) {
        lastPartlistObject = downloadSource;
        if (!fetchList.isSelected()) {
            return;
        }

        try {
            if (partListWorkerThread != null) {
                if (partListWorkerThread.isInterrupted()) {
                    partListWorkerThread = null;
                } else {
                    partListWorkerThread.cancel();
                    partListWorkerThread = null;
                }
            }

            partListWorkerThread = new PartListWorkerThread();
            partListWorkerThread.setDownloadSourceDO(downloadSource);
            partListWorkerThread.start();
        } catch (Exception e) {
            if (partListWorkerThread != null) {
                partListWorkerThread.cancel();
                partListWorkerThread = null;
            }

            logger.error(ApplejuiceFacade.ERROR_MESSAGE, e);
        }
    }

    public void fireLanguageChanged() {
        try {
            LanguageSelector languageSelector = LanguageSelector.getInstance();

            label5.setText(languageSelector.getFirstAttributeByTagName("javagui.downloadform.activetransfer"));
            label4.setText(languageSelector.getFirstAttributeByTagName("mainform.Label4.caption"));
            label3.setText(languageSelector.getFirstAttributeByTagName("mainform.Label3.caption"));
            label2.setText(languageSelector.getFirstAttributeByTagName("mainform.Label2.caption"));
            label1.setText(languageSelector.getFirstAttributeByTagName("mainform.Label1.caption"));
            fetchList.setText(languageSelector.getFirstAttributeByTagName("javagui.downloadform.showpartlist"));
            available = languageSelector.getFirstAttributeByTagName("javagui.downloadform.available");
        } catch (Exception e) {
            logger.error(ApplejuiceFacade.ERROR_MESSAGE, e);
        }
    }

    private class PartListWorkerThread extends Thread {
        private volatile Object objectDO = null;
        private volatile boolean firstRun = true;

        public void run() {
            while (!interrupted()) {
                Object current = objectDO;

                if (current == null) {
                    break;
                }

                boolean shortPause = false;

                if (!actualDlOverviewTable.isShowing()) {
                    try {
                        sleep(RefreshIntervals.partlistRefreshMs());
                        continue;
                    } catch (InterruptedException iE) {
                        interrupt();
                        break;
                    }
                }

                if (current instanceof Download download) {
                    if (download.getStatus() == Download.PAUSED ||
                            download.getStatus() == Download.SEARCHING_LOADING) {
                        shortPause = workDownloadDO(download);
                    } else {
                        objectDO = null;
                        break;
                    }
                } else {
                    shortPause = workDownloadSourceDO((DownloadSource) current);
                }

                if (shortPause) {
                    try {
                        sleep(RefreshIntervals.partlistRefreshMs());
                        firstRun = false;
                        continue;
                    } catch (InterruptedException iE) {
                        interrupt();
                    }
                } else {
                    break;
                }
            }
        }

        public Object getObjectDO() {
            return objectDO;
        }

        /**
         * Publishes results only on the EDT and only while this worker is still the current one.
         * Cancelling and replacing also happen on the EDT; a late result of an earlier selection is
         * thereby discarded.
         */
        private void publish(String text, PartList partList, Integer id) {
            SwingUtilities.invokeLater(() -> {
                if (partListWorkerThread == this && !isInterrupted()) {
                    actualDLFileName.setText(text);
                    actualDlOverviewTable.setPartList(partList, id);
                }
            });
        }

        private void publishText(String text) {
            SwingUtilities.invokeLater(() -> {
                if (partListWorkerThread == this && !isInterrupted()) {
                    actualDLFileName.setText(text);
                }
            });
        }

        private boolean workDownloadDO(Download download) {
            if (download.getStatus() != Download.FINISHING && download.getStatus() != Download.FINISHED) {
                String fileNameText = " " + download.getFilename() + " (" + download.getTemporaryFileNumber() + ".data) ";

                if (firstRun) {
                    publishText(fileNameText);
                }

                PartList partList = null;

                try {
                    partList = AppleJuiceClient.getAjFacade().getPartList(download);
                } catch (WebSiteNotFoundException wsnfE) {
                    // the core is probably overloaded right now
                    logger.debug("Part list of download {} not available: {}", download.getId(), wsnfE.toString());
                    partList = null;
                }

                if (partList != null && !isInterrupted()) {
                    String tmp = available.replaceFirst("%s", decimalFormat.format(partList.getPercentAvailable()));

                    publish(fileNameText + " - " + tmp, partList, Integer.valueOf(download.getId()));
                }

                return true;
            } else {
                return false;
            }
        }

        private boolean workDownloadSourceDO(DownloadSource downloadSource) {
            PartList partList;
            String tmp = downloadSource.getFilename() + " (" + downloadSource.getNickname() + ")";

            publishText(tmp);
            try {
                partList = AppleJuiceClient.getAjFacade().getPartList(downloadSource);
            } catch (WebSiteNotFoundException ex) {
                // the core is probably overloaded right now
                logger.debug("Part list of source {} not available: {}", downloadSource.getId(), ex.toString());
                partList = null;
            }

            if (partList != null && !isInterrupted()) {
                publish(tmp + " - " + available.replaceFirst("%s", decimalFormat.format(partList.getPercentAvailable())),
                        partList, Integer.valueOf(downloadSource.getId()));
            }

            return true;
        }

        public void setDownload(Download download) {
            if (download == null) {
                objectDO = null;
                clear();
            } else if (objectDO != download) {
                objectDO = download;
                clear();
            }
        }

        public void setDownloadSourceDO(DownloadSource downloadSource) {
            if (downloadSource == null) {
                objectDO = null;
                clear();
            } else if (objectDO != downloadSource) {
                objectDO = downloadSource;
                clear();
            }
        }

        private void clear() {
            actualDLFileName.setText("");
            actualDlOverviewTable.setPartList(null, null);
        }

        public void cancel() {
            interrupt();
        }
    }
}
