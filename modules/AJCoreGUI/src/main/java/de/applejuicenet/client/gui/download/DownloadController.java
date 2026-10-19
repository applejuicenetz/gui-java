/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.download;

import de.applejuicenet.client.shared.CoreCall;
import de.applejuicenet.client.AppleJuiceClient;
import de.applejuicenet.client.facade.ApplejuiceFacade;
import de.applejuicenet.client.facade.entity.*;
import de.applejuicenet.client.facade.event.DownloadDataPropertyChangeEvent;
import de.applejuicenet.client.facade.exception.IllegalArgumentException;
import de.applejuicenet.client.gui.AppleJuiceDialog;
import de.applejuicenet.client.gui.components.GuiController;
import de.applejuicenet.client.gui.components.GuiControllerActionListener;
import de.applejuicenet.client.gui.components.table.HeaderListener;
import de.applejuicenet.client.gui.components.util.Value;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.controller.OptionsManagerImpl;
import de.applejuicenet.client.gui.controller.PositionManager;
import de.applejuicenet.client.gui.controller.PositionManagerImpl;
import de.applejuicenet.client.gui.controller.TableColumnSettings;
import de.applejuicenet.client.gui.controller.TableSelection;
import de.applejuicenet.client.gui.download.table.DownloadSourcesTableModel;
import de.applejuicenet.client.gui.download.table.DownloadsTableModel;
import de.applejuicenet.client.gui.options.IncomingDirSelectionDialog;
import de.applejuicenet.client.gui.upload.HeaderPopupListener;
import de.applejuicenet.client.gui.upload.UploadMouseMotionListener;
import de.applejuicenet.client.shared.DesktopTools;
import de.applejuicenet.client.shared.ReleaseInfo;
import de.applejuicenet.client.shared.SoundPlayer;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DownloadController extends GuiController {
    private static DownloadController instance = null;
    private static final int CANCEL = 0;
    private static final int COPY_TO_CLIPBOARD = 1;
    private static final int COPY_TO_CLIPBOARD_WITH_SOURCES = 2;
    private static final int OPEN_WITH_DEFAULT_PROGRAM = 16;
    private static final int PAUSE = 4;
    private static final int RESUME = 5;
    private static final int RENAME = 6;
    private static final int CHANGE_TARGET_FOLDER = 7;
    private static final int REMOVE_FINISHED = 8;
    private static final int SHOW_PARTLIST = 9;
    private static final int START_DOWNLOAD = 10;
    private static final int SHOW_PARTLIST_PER_BUTTON = 11;
    private static final int START_POWERDOWNLOAD = 12;
    private static final int HEADER_DOWNLOAD_POPUP = 13;
    private static final int HEADER_DOWNLOAD_SOURCES_POPUP = 14;
    private static final int HEADER_DOWNLOAD_DRAGGED = 15;
    private static final int HEADER_DOWNLOAD_SOURCES_DRAGGED = 17;
    private static final int RELEASE_INFO = 18;
    private DownloadPanel downloadPanel;
    private boolean initialized = false;
    private String dialogTitle;
    private String cancelDownload;
    private DownloadPartListWatcher downloadPartListWatcher;
    private boolean firstUpdate = true;
    private boolean isFirstDownloadPropertyChanged = true;
    private volatile boolean selected = false;
    private int autoPartListId = -1;
    private boolean autoPartListEligible;
    private String alreadyLoaded;
    private String invalidLink;
    private String linkFailure;

    private DownloadController() {
        super();
        downloadPanel = new DownloadPanel(this);
        try {
            init();
            AppleJuiceClient.getAjFacade().addDataUpdateListener(this, DATALISTENER_TYPE.DOWNLOAD_CHANGED);

        } catch (Exception e) {
            logger.error(ApplejuiceFacade.ERROR_MESSAGE, e);
        }
    }

    public static synchronized DownloadController getInstance() {
        if (null == instance) {
            instance = new DownloadController();
        }

        return instance;
    }

    private void init() {
        downloadPanel.getMnuCancel().addActionListener(new GuiControllerActionListener(this, CANCEL));
        downloadPanel.getMnuCopyToClipboard().addActionListener(new GuiControllerActionListener(this, COPY_TO_CLIPBOARD));
        downloadPanel.getMnuCopyToClipboardWithSources()
                .addActionListener(new GuiControllerActionListener(this, COPY_TO_CLIPBOARD_WITH_SOURCES));
        downloadPanel.getMnuPause().addActionListener(new GuiControllerActionListener(this, PAUSE));
        downloadPanel.getMnuResume().addActionListener(new GuiControllerActionListener(this, RESUME));
        downloadPanel.getMnuRename().addActionListener(new GuiControllerActionListener(this, RENAME));
        downloadPanel.getMnuTargetFolder().addActionListener(new GuiControllerActionListener(this, CHANGE_TARGET_FOLDER));
        downloadPanel.getMnuRemoveFinished().addActionListener(new GuiControllerActionListener(this, REMOVE_FINISHED));
        downloadPanel.getMnuReleaseInfo().addActionListener(new GuiControllerActionListener(this, RELEASE_INFO));
        downloadPanel.getBtnPowerDownload().addActionListener(new GuiControllerActionListener(this, START_POWERDOWNLOAD));
        if (AppleJuiceClient.getAjFacade().isLocalhost()) {
            if (DesktopTools.isAdvancedSupported()) {
                downloadPanel.getMnuOpenWithDefaultProgram()
                        .addActionListener(new GuiControllerActionListener(this, OPEN_WITH_DEFAULT_PROGRAM));
            } else {
                downloadPanel.getMnuOpenWithDefaultProgram().setVisible(false);
            }
        } else {
            downloadPanel.getMnuOpenWithDefaultProgram().setEnabled(false);
        }

        downloadPanel.getDownloadTable().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    int selected = downloadPanel.getDownloadTable().getSelectedRow();

                    if (selected == -1) {
                        downloadPartListWatcher.setDownloadNode((Download) null);
                        return;
                    }

                    Download download = downloadPanel.getDownloadTableModel().getRow(selected);

                    downloadClicked(download);
                    if (downloadPanel.getDownloadSourcesTableModel().setDownload(download)) {
                        downloadPanel.getDownloadSourcesTableModel().fireTableDataChanged();
                    }
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    int row = downloadPanel.getDownloadTable().rowAtPoint(e.getPoint());

                    if (!downloadPanel.getDownloadTable().getSelectionModel().isSelectedIndex(row)) {
                        downloadPanel.getDownloadTable().getSelectionModel().setSelectionInterval(row, row);
                    }

                    maybeShowDownloadPopup(e);
                }
            }
        });
        downloadPanel.getDownloadTable().addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent ke) {
                switch (ke.getKeyCode()) {

                    case KeyEvent.VK_F2: {
                        renameDownload();
                        break;
                    }

                    case KeyEvent.VK_F3: {
                        changeTargetDir();
                        break;
                    }

                    case KeyEvent.VK_F5: {
                        pause();
                        break;
                    }

                    case KeyEvent.VK_F6: {
                        resume();
                        break;
                    }

                    default:
                        break;
                }
            }
        });
        downloadPanel.getDownloadSourceTable().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    int selected = downloadPanel.getDownloadSourceTable().getSelectedRow();

                    if (selected == -1) {
                        return;
                    }

                    DownloadSource downloadSource = downloadPanel.getDownloadSourcesTableModel().getRow(selected);

                    downloadSourceClicked(downloadSource);
                }
            }
        });
        downloadPartListWatcher = new DownloadPartListWatcher(this);

        JTableHeader header = downloadPanel.getDownloadTable().getTableHeader();

        header.addMouseListener(new HeaderPopupListener(this, HEADER_DOWNLOAD_POPUP));
        header.addMouseMotionListener(new UploadMouseMotionListener(this, HEADER_DOWNLOAD_DRAGGED));

        header = downloadPanel.getDownloadSourceTable().getTableHeader();

        header.addMouseListener(new HeaderPopupListener(this, HEADER_DOWNLOAD_SOURCES_POPUP));
        header.addMouseMotionListener(new UploadMouseMotionListener(this, HEADER_DOWNLOAD_SOURCES_DRAGGED));

        LanguageSelector.getInstance().addLanguageListener(this);
    }

    public Value[] getCustomizedValues() {
        return new Value[0];
    }

    public void fireAction(int actionId, Object source) {
        switch (actionId) {

            case HEADER_DOWNLOAD_DRAGGED: {
                headerDownloadsDragged();
                break;
            }

            case HEADER_DOWNLOAD_SOURCES_DRAGGED: {
                headerDownloadSourcesDragged();
                break;
            }

            case HEADER_DOWNLOAD_POPUP: {
                headerDownloadsPopup((MouseEvent) source);
                break;
            }

            case HEADER_DOWNLOAD_SOURCES_POPUP: {
                headerDownloadSourcesPopup((MouseEvent) source);
                break;
            }

            case CANCEL: {
                cancelDownload();
                break;
            }

            case COPY_TO_CLIPBOARD: {
                copyDownloadLinkToClipboard();
                break;
            }

            case COPY_TO_CLIPBOARD_WITH_SOURCES: {
                copyDownloadLinkToClipboardWithSources();
                break;
            }

            case OPEN_WITH_DEFAULT_PROGRAM: {
                openWithDefaultProgram();
                break;
            }

            case PAUSE: {
                pause();
                break;
            }

            case RESUME: {
                resume();
                break;
            }

            case RENAME: {
                renameDownload();
                break;
            }

            case CHANGE_TARGET_FOLDER: {
                changeTargetDir();
                break;
            }

            case REMOVE_FINISHED: {
                clearReadyDownloads();
                break;
            }

            case START_POWERDOWNLOAD: {
                startPowerDownload();
                break;
            }

            case RELEASE_INFO: {
                showReleaseInfo();
                break;
            }

            default:
                logger.error("Unregistered event id {}", actionId);
        }
    }

    private void showReleaseInfo() {
        Download[] selectedItems = getSelectedDownloads();

        if (selectedItems == null || selectedItems.length < 1) {
            return;
        }

        Download curDownload = selectedItems[0];

        ReleaseInfo.handle(curDownload.getFilename(), curDownload.getHash(), curDownload.getSize());
    }

    private void headerDownloadsDragged() {
        PositionManager pm = PositionManagerImpl.getInstance();
        TableColumnModel columnModel = downloadPanel.getDownloadTable().getColumnModel();
        TableColumn[] columns = downloadPanel.getDownloadTableColumns();

        for (int i = 0; i < columns.length; i++) {
            pm.setDownloadColumnIndex(i, columnModel.getColumnIndex(columns[i].getIdentifier()));
        }
    }

    private void headerDownloadSourcesDragged() {
        PositionManager pm = PositionManagerImpl.getInstance();
        TableColumnModel columnModel = downloadPanel.getDownloadSourceTable().getColumnModel();
        TableColumn[] columns = downloadPanel.getDownloadSourcesTableColumns();

        for (int i = 0; i < columns.length; i++) {
            pm.setDownloadSourcesColumnIndex(i, columnModel.getColumnIndex(columns[i].getIdentifier()));
        }
    }

    private void headerDownloadsPopup(MouseEvent e) {
        TableColumn[] columns = downloadPanel.getDownloadTableColumns();
        JCheckBoxMenuItem[] columnPopupItems = downloadPanel.getColumnDownloadPopupItems();
        TableColumnModel tableColumnModel = downloadPanel.getDownloadTable().getColumnModel();

        for (int i = 1; i < columns.length; i++) {
            try {
                tableColumnModel.getColumnIndex(columns[i].getIdentifier());
                columnPopupItems[i].setSelected(true);
            } catch (java.lang.IllegalArgumentException iaE) {
                columnPopupItems[i].setSelected(false);
            }
        }

        downloadPanel.getColumnDownloadPopup().show(downloadPanel.getDownloadTable().getTableHeader(), e.getX(), e.getY());
    }

    private void headerDownloadSourcesPopup(MouseEvent e) {
        TableColumn[] columns = downloadPanel.getDownloadSourcesTableColumns();
        JCheckBoxMenuItem[] columnPopupItems = downloadPanel.getColumnDownloadSourcesPopupItems();
        TableColumnModel tableColumnModel = downloadPanel.getDownloadSourceTable().getColumnModel();

        for (int i = 1; i < columns.length; i++) {
            try {
                tableColumnModel.getColumnIndex(columns[i].getIdentifier());
                columnPopupItems[i].setSelected(true);
            } catch (java.lang.IllegalArgumentException iaE) {
                columnPopupItems[i].setSelected(false);
            }
        }

        downloadPanel.getColumnDownloadSourcesPopup().show(downloadPanel.getDownloadSourceTable().getTableHeader(), e.getX(), e.getY());
    }

    public JComponent getComponent() {
        return downloadPanel;
    }

    private boolean handleDownloadDataPropertyChangeEvent(DownloadDataPropertyChangeEvent event) {
        return false;
    }

    private void clearReadyDownloads() {
        CoreCall.runInBackground("CleanDownloadList", () -> AppleJuiceClient.getAjFacade().cleanDownloadList());
        downloadPanel.getPowerDownloadPanel().getBtnPowerdownload().setEnabled(false);
        downloadPanel.getPowerDownloadPanel().setPowerdownloadValue(0);
        downloadPanel.getDownloadTable().getSelectionModel().clearSelection();
        autoPartListId = -1;
        downloadPartListWatcher.setDownloadNode((Download) null);
    }

    private void downloadSourceClicked(DownloadSource downloadSource) {
        tryGetPartList(downloadSource);
    }

    private void downloadClicked(Download download) {
        tryGetPartList(download);

        if (!downloadPanel.getPowerDownloadPanel().isAutomaticPowerdownloadActive()) {
            downloadPanel.getPowerDownloadPanel().getBtnPowerdownload().setEnabled(true);
            if (download.getStatus() == Download.SEARCHING_LOADING || download.getStatus() == Download.PAUSED) {
                downloadPanel.getPowerDownloadPanel().setPowerdownloadValue(download.getPowerDownload());
            } else {
                downloadPanel.getPowerDownloadPanel().getBtnPowerdownload().setEnabled(false);
                downloadPanel.getPowerDownloadPanel().setPowerdownloadValue(0);
            }
        }
    }

    private void startPowerDownload() {
        try {
            Download[] selectedDownloads = getSelectedDownloads();

            if (selectedDownloads != null && selectedDownloads.length != 0) {
                int powerDownload = 0;

                if (!downloadPanel.getBtnPowerDownloadInactive().isSelected()) {
                    String temp = downloadPanel.getRatioField().getText();
                    double power = 2.2;

                    try {
                        power = Double.parseDouble(temp);
                    } catch (NumberFormatException nfE) {
                        logger.error(ApplejuiceFacade.ERROR_MESSAGE, nfE);

                        downloadPanel.getRatioField().setText("2.2");
                    }

                    powerDownload = (int) (power * 10 - 10);
                }

                List<Download> temp = new ArrayList<Download>();

                for (Download curDownload : selectedDownloads) {
                    if (curDownload.getStatus() == Download.PAUSED || curDownload.getStatus() == Download.SEARCHING_LOADING) {
                        temp.add(curDownload);
                    }
                }

                int requestedPower = powerDownload;
                boolean playSound = downloadPanel.getBtnPowerDownloadActive().isSelected();
                CoreCall.runInBackground("SetPowerDownload", () -> {
                    AppleJuiceClient.getAjFacade().setPowerDownload(temp, requestedPower);
                    if (playSound) {
                        SoundPlayer.getInstance().playSound(SoundPlayer.POWER);
                    }
                });
            }
        } catch (Exception ex) {
            logger.error(ApplejuiceFacade.ERROR_MESSAGE, ex);
        }
    }

    private void maybeShowDownloadPopup(MouseEvent e) {
        int[] selected = downloadPanel.getDownloadTable().getSelectedRows();

        if (null == selected || selected.length == 0) {
            return;
        }

        Download[] selectedDownloads = new Download[selected.length];

        for (int i = 0; i < selected.length; i++) {
            selectedDownloads[i] = downloadPanel.getDownloadTableModel().getRow(selected[i]);
        }

        boolean paused = false;
        boolean running = false;

        downloadPanel.getMnuTargetFolder().setEnabled(true);
        for (Download curDownload : selectedDownloads) {
            if (curDownload.getStatus() == Download.SEARCHING_LOADING) {
                running = true;
            } else if (curDownload.getStatus() == Download.PAUSED) {
                paused = true;
            }
        }

        downloadPanel.getMnuRename().setEnabled(selected.length == 1);
        downloadPanel.getMnuCopyToClipboard().setEnabled(selected.length == 1);
        downloadPanel.getMnuCopyToClipboardWithSources().setEnabled(selected.length == 1);
        downloadPanel.getMnuPause().setEnabled(running);
        downloadPanel.getMnuResume().setEnabled(paused);
        downloadPanel.getPopup().show(downloadPanel.getDownloadTable(), e.getX(), e.getY());
    }

    private void tryGetPartList(DownloadSource downloadSource) {
        downloadPartListWatcher.setDownloadNode(downloadSource);
    }

    private void tryGetPartList(Download download) {
        downloadPartListWatcher.setDownloadNode(download);
    }

    private Download[] getSelectedDownloads() {
        try {
            int[] selected = downloadPanel.getDownloadTable().getSelectedRows();

            if (null == selected || selected.length == 0) {
                return new Download[0];
            }

            Download[] selectedDownloads = new Download[selected.length];

            for (int i = 0; i < selected.length; i++) {
                selectedDownloads[i] = downloadPanel.getDownloadTableModel().getRow(selected[i]);
            }

            return selectedDownloads;
        } catch (Exception e) {
            logger.error(ApplejuiceFacade.ERROR_MESSAGE, e);

            return new Download[0];
        }
    }

    private void renameDownload() {
        Download[] selectedItems = getSelectedDownloads();

        if (selectedItems != null && selectedItems.length == 1) {
            Download curDownload = selectedItems[0];
            RenameDownloadDialog renameDownloadDialog = new RenameDownloadDialog(AppleJuiceDialog.getApp(), curDownload);

            renameDownloadDialog.setVisible(true);
            String newName = renameDownloadDialog.getNewName();

            if (newName == null) {
                return;
            } else {
                if (curDownload.getFilename().compareTo(newName) != 0) {
                    CoreCall.runInBackground("RenameDownload",
                            () -> AppleJuiceClient.getAjFacade().renameDownload(curDownload, newName));
                }
            }
        }
    }

    private void pause() {
        Download[] selectedDownloads = getSelectedDownloads();

        if (selectedDownloads != null && selectedDownloads.length != 0 &&
                !downloadPanel.getPowerDownloadPanel().isAutomaticPowerdownloadActive()) {
            final List<Download> pause = new ArrayList<Download>();

            for (Download curDownload : selectedDownloads) {
                if (curDownload.getStatus() == Download.SEARCHING_LOADING) {
                    pause.add(curDownload);
                }
            }

            if (pause.size() > 0) {
                CoreCall.runInBackground("PauseDownload", () -> AppleJuiceClient.getAjFacade().pauseDownload(pause));
            }
        }
    }

    private void resume() {
        Download[] selectedDownloads = getSelectedDownloads();

        if (selectedDownloads != null && selectedDownloads.length != 0 &&
                !downloadPanel.getPowerDownloadPanel().isAutomaticPowerdownloadActive()) {
            final List<Download> resume = new ArrayList<Download>();

            for (Download curDownload : selectedDownloads) {
                if (curDownload.getStatus() == Download.PAUSED) {
                    resume.add(curDownload);
                }
            }

            if (resume.size() > 0) {
                CoreCall.runInBackground("ResumeDownload", () -> AppleJuiceClient.getAjFacade().resumeDownload(resume));
            }
        }
    }

    private void changeTargetDir() {
        Download[] selectedDownloads = getSelectedDownloads();

        if (selectedDownloads == null || selectedDownloads.length == 0) {
            return;
        }

        String selectedDir = null;

        for (Download curDownload : selectedDownloads) {
            selectedDir = curDownload.getTargetDirectory();
            if (null != selectedDir && selectedDir.trim().length() > 0) {
                break;
            }
        }

        String[] dirs = AppleJuiceClient.getAjFacade().getCurrentIncomingDirs();
        IncomingDirSelectionDialog incomingDirSelectionDialog = new IncomingDirSelectionDialog(AppleJuiceDialog.getApp(), dirs,
                selectedDir);

        incomingDirSelectionDialog.setVisible(true);
        String newName = incomingDirSelectionDialog.getSelectedIncomingDir();

        if (newName == null) {
            return;
        } else {
            newName = newName.trim();
            if (newName.indexOf(File.separator) == 0 || newName.indexOf(ApplejuiceFacade.separator) == 0) {
                newName = newName.substring(1);
            }
        }

        List<Download> toChange = new ArrayList<Download>();

        for (Download curDownload : selectedDownloads) {
            if (!newName.equals(curDownload.getTargetDirectory())) {
                toChange.add(curDownload);
            }
        }

        if (toChange.size() > 0) {
            String targetDirectory = newName;
            CoreCall.runInBackground("SetTargetDirectory",
                    () -> AppleJuiceClient.getAjFacade().setTargetDir(toChange, targetDirectory));
        }
    }

    private void openWithDefaultProgram() {
        Download[] selectedDownloads = getSelectedDownloads();

        if (selectedDownloads.length == 1) {
            for (Download curDownload : selectedDownloads) {
                Integer shareId = curDownload.getShareId();

                try {
                    Share share = (Share) AppleJuiceClient.getAjFacade().getObjectById(shareId);

                    if (share != null) {
                        String filename = share.getFilename();

                        DesktopTools.open(new File(filename));
                    }
                } catch (IllegalArgumentException e) {
                    logger.error(ApplejuiceFacade.ERROR_MESSAGE, e);
                }
            }
        }
    }

    private void copyDownloadLinkToClipboardWithSources() {
        Download[] selectedDownloads = getSelectedDownloads();

        if (selectedDownloads.length == 1) {
            Download curDownload = selectedDownloads[0];
            Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
            StringBuilder toCopy = new StringBuilder();

            toCopy.append("ajfsp://file|");
            boolean copyToClipboard = false;

            toCopy.append(curDownload.getFilename() + "|" + curDownload.getHash() + "|" + curDownload.getSize());
            copyToClipboard = true;

            if (copyToClipboard) {
                long port = AppleJuiceClient.getAjFacade().getAJSettings().getPort();
                Information information = AppleJuiceClient.getAjFacade().getInformation();

                toCopy.append("|");
                toCopy.append(information.getExternalIP());
                toCopy.append(":");
                toCopy.append(port);
                if (information.getConnectionStatus() == Information.CONNECTED) {
                    Server server = information.getServer();

                    if (server != null) {
                        toCopy.append(":");
                        toCopy.append(server.getHost());
                        toCopy.append(":");
                        toCopy.append(server.getPort());
                    }
                }

                toCopy.append("/");
                StringSelection contents = new StringSelection(toCopy.toString());

                cb.setContents(contents, null);
            }
        }
    }

    private void copyDownloadLinkToClipboard() {
        Download[] selectedDownloads = getSelectedDownloads();

        if (selectedDownloads.length == 1) {
            Download curDownload = selectedDownloads[0];
            Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
            StringBuilder toCopy = new StringBuilder();

            toCopy.append("ajfsp://file|");
            toCopy.append(curDownload.getFilename() + "|" + curDownload.getHash() + "|" + curDownload.getSize() + "/");
            StringSelection contents = new StringSelection(toCopy.toString());

            cb.setContents(contents, null);
        }
    }

    private void cancelDownload() {
        Download[] selectedDownloads = getSelectedDownloads();

        if (selectedDownloads.length != 0) {
            int result = JOptionPane.showConfirmDialog(AppleJuiceDialog.getApp(), cancelDownload, dialogTitle,
                    JOptionPane.YES_NO_OPTION);

            if (result == JOptionPane.YES_OPTION) {
                final List<Download> cancel = new ArrayList<Download>();

                Collections.addAll(cancel, selectedDownloads);

                if (cancel.size() > 0) {
                    CoreCall.runInBackground("CancelDownload", () -> {
                        AppleJuiceClient.getAjFacade().cancelDownload(cancel);
                        SoundPlayer.getInstance().playSound(SoundPlayer.CANCELLED);
                    });
                }
            }
        }
    }

    public void updateDownloadPolling() {
        AppleJuiceClient.getAjFacade()
                .setDownloadPolling(selected || downloadPanel.getPowerDownloadPanel().isAutomaticPowerdownloadActive());
    }

    public void componentSelected() {
        try {
            selected = true;
            updateDownloadPolling();
            if (!initialized) {
                initialized = true;
                firstUpdate = false;
                int width = downloadPanel.getScrollPane().getWidth() - 18;
                PositionManager pm = PositionManagerImpl.getInstance();

                TableColumn[] columnsDownload = downloadPanel.getDownloadTableColumns();
                TableColumn[] columnsDownloadSources = downloadPanel.getDownloadSourcesTableColumns();
                int[] sortDownloads = null;
                int[] sortDownloadSources = {2, 1};

                if (pm.isLegal()) {
                    boolean[] visibilities = pm.getDownloadColumnVisibilities();
                    int[] indices = pm.getDownloadColumnIndices();
                    ArrayList<TableColumn> visibleColumns = new ArrayList<TableColumn>();

                    for (int i = 0; i < columnsDownload.length; i++) {
                        downloadPanel.getDownloadTable().removeColumn(columnsDownload[i]);
                        if (visibilities[i]) {
                            visibleColumns.add(columnsDownload[i]);
                        }
                    }

                    int pos = -1;

                    for (int i = 0; i < visibleColumns.size(); i++) {
                        for (int x = 0; x < columnsDownload.length; x++) {
                            if (visibleColumns.contains(columnsDownload[x]) && indices[x] == pos + 1) {
                                downloadPanel.getDownloadTable().addColumn(columnsDownload[x]);
                                pos++;
                                break;
                            }
                        }
                    }

                    visibilities = pm.getDownloadSourcesColumnVisibilities();
                    indices = pm.getDownloadSourcesColumnIndices();
                    sortDownloads = pm.getDownloadSort();

                    visibleColumns = new ArrayList<TableColumn>();

                    for (int i = 0; i < columnsDownloadSources.length; i++) {
                        downloadPanel.getDownloadSourceTable().removeColumn(columnsDownloadSources[i]);
                        if (visibilities[i]) {
                            visibleColumns.add(columnsDownloadSources[i]);
                        }
                    }

                    pos = -1;

                    sortDownloadSources = pm.getDownloadSourcesSort();
                    for (int i = 0; i < visibleColumns.size(); i++) {
                        for (int x = 0; x < columnsDownloadSources.length; x++) {
                            if (visibleColumns.contains(columnsDownloadSources[x]) && indices[x] == pos + 1) {
                                downloadPanel.getDownloadSourceTable().addColumn(columnsDownloadSources[x]);
                                pos++;
                                break;
                            }
                        }
                    }
                }

                downloadPanel.getDownloadTable().setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                downloadPanel.getDownloadSourceTable().setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                columnsDownload[9].setMaxWidth(280);
                int statusMinWidth = com.formdev.flatlaf.util.UIScale.scale(190);
                columnsDownload[1].setMinWidth(statusMinWidth);
                columnsDownloadSources[1].setMinWidth(statusMinWidth);
                TableColumnSettings.installCompact(downloadPanel.getDownloadTable(), "download", columnsDownload);
                TableColumnSettings.installCompact(downloadPanel.getDownloadSourceTable(), "downloadsources", columnsDownloadSources);
                int loc = downloadPanel.getSplitPane().getHeight() - downloadPanel.getSplitPane().getDividerSize() -
                        downloadPanel.getPowerDownloadPanel().getPreferredSize().height;

                downloadPanel.getSplitPane().setDividerLocation(loc);
                if (null != sortDownloads) {
                    for (MouseListener curMl : downloadPanel.getDownloadTable().getTableHeader().getMouseListeners()) {
                        if (curMl instanceof HeaderListener) {
                            ((HeaderListener) curMl).sort(sortDownloads[0], sortDownloads[1] == 1);
                        }
                    }
                }

                if (null != sortDownloadSources) {
                    for (MouseListener curMl : downloadPanel.getDownloadSourceTable().getTableHeader().getMouseListeners()) {
                        if (curMl instanceof HeaderListener) {
                            ((HeaderListener) curMl).sort(sortDownloadSources[0], sortDownloadSources[1] == 1);
                        }
                    }
                }
            }

            TableColumnSettings.refreshCompact(downloadPanel.getDownloadTable());
            TableColumnSettings.refreshCompact(downloadPanel.getDownloadSourceTable());
            selectSingleDownload();
            downloadPanel.getDownloadTable().revalidate();
            downloadPanel.getDownloadTable().repaint();
            if (downloadPanel.getDownloadSourcesScrollPane().isVisible()) {
                downloadPanel.getDownloadSourceTable().revalidate();
                downloadPanel.getDownloadSourceTable().repaint();
            }
        } catch (Exception e) {
            logger.error(ApplejuiceFacade.ERROR_MESSAGE, e);
        }
    }

    public void componentLostSelection() {
        selected = false;
        autoPartListId = -1;
        updateDownloadPolling();
        downloadPartListWatcher.setDownloadNode((Download) null);
    }

    private void selectSingleDownload() {
        JTable table = downloadPanel.getDownloadTable();
        boolean newlySelected = TableSelection.selectOnlyRow(table);
        if (table.getRowCount() != 1) {
            return;
        }
        Download download = downloadPanel.getDownloadTableModel().getRow(0);
        boolean partListEligible = download.getStatus() == Download.SEARCHING_LOADING || download.getStatus() == Download.PAUSED;
        boolean partListStale = autoPartListId != download.getId() || autoPartListEligible != partListEligible;
        if (newlySelected || partListStale || downloadPanel.getDownloadSourcesTableModel().getDownload() != download) {
            autoPartListId = download.getId();
            autoPartListEligible = partListEligible;
            downloadClicked(download);
            if (downloadPanel.getDownloadSourcesTableModel().setDownload(download)) {
                downloadPanel.getDownloadSourcesTableModel().forceResort();
                downloadPanel.getDownloadSourcesTableModel().fireTableDataChanged();
                TableColumnSettings.refreshCompact(downloadPanel.getDownloadSourceTable());
            }
        }
    }

    protected void languageChanged() {
        LanguageSelector languageSelector = LanguageSelector.getInstance();
        String text = languageSelector.getFirstAttributeByTagName("mainform.Label14.caption");

        dialogTitle = languageSelector.getFirstAttributeByTagName("mainform.caption");
        cancelDownload = languageSelector.getFirstAttributeByTagName("mainform.msgdlgtext5");
        String[] tableColumns = new String[DownloadsTableModel.CLASS_TYPES.length];

        tableColumns[0] = languageSelector.getFirstAttributeByTagName("mainform.queue.col0caption");
        tableColumns[1] = languageSelector.getFirstAttributeByTagName("mainform.queue.col1caption");
        tableColumns[2] = languageSelector.getFirstAttributeByTagName("mainform.queue.col2caption");
        tableColumns[3] = languageSelector.getFirstAttributeByTagName("mainform.queue.col3caption");
        tableColumns[4] = languageSelector.getFirstAttributeByTagName("mainform.queue.col4caption");
        tableColumns[5] = languageSelector.getFirstAttributeByTagName("mainform.queue.col5caption");
        tableColumns[6] = languageSelector.getFirstAttributeByTagName("mainform.queue.col6caption");
        tableColumns[7] = languageSelector.getFirstAttributeByTagName("mainform.queue.col7caption");
        tableColumns[8] = languageSelector.getFirstAttributeByTagName("mainform.queue.col8caption");
        tableColumns[9] = "Zielverzeichnis";
        TableColumn[] columns = downloadPanel.getDownloadTableColumns();
        JCheckBoxMenuItem[] columnPopupItems = downloadPanel.getColumnDownloadPopupItems();

        for (int i = 0; i < columns.length; i++) {
            columns[i].setHeaderValue(tableColumns[i]);
            columnPopupItems[i].setText(tableColumns[i]);
        }

        tableColumns = new String[DownloadSourcesTableModel.CLASS_TYPES.length];

        tableColumns[0] = languageSelector.getFirstAttributeByTagName("mainform.queue.col0caption");
        tableColumns[1] = languageSelector.getFirstAttributeByTagName("mainform.queue.col1caption");
        tableColumns[2] = languageSelector.getFirstAttributeByTagName("mainform.uploads.col1caption");
        tableColumns[3] = languageSelector.getFirstAttributeByTagName("mainform.queue.col2caption");
        tableColumns[4] = languageSelector.getFirstAttributeByTagName("mainform.queue.col3caption");
        tableColumns[5] = languageSelector.getFirstAttributeByTagName("mainform.queue.col4caption");
        tableColumns[6] = languageSelector.getFirstAttributeByTagName("mainform.queue.col5caption");
        tableColumns[7] = languageSelector.getFirstAttributeByTagName("mainform.queue.col6caption");
        tableColumns[8] = languageSelector.getFirstAttributeByTagName("mainform.queue.col7caption");
        tableColumns[9] = languageSelector.getFirstAttributeByTagName("mainform.queue.col8caption");
        tableColumns[10] = languageSelector.getFirstAttributeByTagName("mainform.queue.col9caption");

        columns = downloadPanel.getDownloadSourcesTableColumns();
        columnPopupItems = downloadPanel.getColumnDownloadSourcesPopupItems();
        for (int i = 0; i < columns.length; i++) {
            columns[i].setHeaderValue(tableColumns[i]);
            columnPopupItems[i].setText(tableColumns[i]);
        }

        downloadPanel.getMnuReleaseInfo().setText(languageSelector.getFirstAttributeByTagName("releaseinfo.menu"));
        downloadPanel.getMnuCancel().setText(languageSelector.getFirstAttributeByTagName("mainform.canceldown.caption"));
        downloadPanel.getMnuPause().setText(languageSelector.getFirstAttributeByTagName("mainform.pausedown.caption") + " [F5]");
        downloadPanel.getMnuResume().setText(languageSelector.getFirstAttributeByTagName("mainform.resumedown.caption") + " [F6]");
        downloadPanel.getMnuRename().setText(languageSelector.getFirstAttributeByTagName("mainform.renamefile.caption") + " [F2]");
        downloadPanel.getMnuTargetFolder().setText(languageSelector.getFirstAttributeByTagName("mainform.changetarget.caption") +
                " [F3]");
        downloadPanel.getMnuRemoveFinished()
                .setText(languageSelector.getFirstAttributeByTagName("mainform.Clearfinishedentries1.caption"));
        downloadPanel.getMnuCopyToClipboard().setText(languageSelector.getFirstAttributeByTagName("mainform.getlink1.caption"));
        downloadPanel.getMnuCopyToClipboardWithSources()
                .setText(languageSelector.getFirstAttributeByTagName("javagui.downloadform.getlinkwithsources"));
        downloadPanel.getMnuOpenWithDefaultProgram()
                .setText(languageSelector.getFirstAttributeByTagName("javagui.options.standard.startwithdefault"));
        String remoteHint = AppleJuiceClient.getAjFacade().isLocalhost() ? null
                : languageSelector.getFirstAttributeByTagName("javagui.remotecore.hint");
        downloadPanel.getMnuOpenWithDefaultProgram().setToolTipText(remoteHint);
        alreadyLoaded = languageSelector.getFirstAttributeByTagName("javagui.downloadform.alreadyloaded");
        invalidLink = languageSelector.getFirstAttributeByTagName("javagui.downloadform.invalidlink");
        linkFailure = languageSelector.getFirstAttributeByTagName("javagui.downloadform.otherlinkerrorlong");
    }

    @SuppressWarnings("unchecked")
    protected void contentChanged(DATALISTENER_TYPE type, final Object content) {
        if (!SwingUtilities.isEventDispatchThread()) {
            final Map<Integer, Download> snapshot = new HashMap<Integer, Download>((Map<Integer, Download>) content);

            SwingUtilities.invokeLater(() -> contentChanged(type, snapshot));
            return;
        }

        Map<Integer, Download> downloads = (Map<Integer, Download>) content;

        final boolean downloadChanged = downloadPanel.getDownloadTableModel().setDownloads(downloads);
        Download curDownload = downloadPanel.getDownloadSourcesTableModel().getDownload();

        boolean sourcesChanged = false;

        if (null != curDownload) {
            Download freshDownload = downloads.get(curDownload.getId());

            sourcesChanged = downloadPanel.getDownloadSourcesTableModel().setDownload(freshDownload);
        }

        if (selected && (downloadChanged || sourcesChanged)) {
            if (downloadChanged) {
                downloadPanel.getDownloadTableModel().forceResort();
                TableColumnSettings.refreshCompact(downloadPanel.getDownloadTable());
                downloadPanel.getDownloadTable().revalidate();
                downloadPanel.getDownloadTable().repaint();
            }

            if (sourcesChanged) {
                downloadPanel.getDownloadSourcesTableModel().forceResort();
                TableColumnSettings.refreshCompact(downloadPanel.getDownloadSourceTable());
                downloadPanel.getDownloadSourceTable().revalidate();
                downloadPanel.getDownloadSourceTable().repaint();
            }
        }
        if (selected) {
            selectSingleDownload();
            stopPartListIfSourceGone();
        }
    }

    private void stopPartListIfSourceGone() {
        DownloadSource current = downloadPanel.getDownloadOverviewPanel().getCurrentSource();

        if (current == null) {
            return;
        }
        Download parent = downloadPanel.getDownloadSourcesTableModel().getDownload();

        if (parent == null || (parent.getStatus() != Download.SEARCHING_LOADING && parent.getStatus() != Download.PAUSED)) {
            downloadPartListWatcher.setDownloadNode((Download) null);
            return;
        }
        for (DownloadSource source : downloadPanel.getDownloadSourcesTableModel().getContent()) {
            if (source.getId() == current.getId()) {
                return;
            }
        }
        downloadPartListWatcher.setDownloadNode((Download) null);
    }
}
