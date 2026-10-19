/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.download;

import de.applejuicenet.client.facade.ApplejuiceFacade;
import de.applejuicenet.client.gui.components.GuiController;
import de.applejuicenet.client.gui.components.TklPanel;
import de.applejuicenet.client.gui.components.table.HeaderListener;
import de.applejuicenet.client.gui.components.table.SortButtonRenderer;
import de.applejuicenet.client.gui.components.table.AutoRowHeightTable;
import de.applejuicenet.client.gui.controller.PositionManager;
import de.applejuicenet.client.gui.controller.PositionManagerImpl;
import de.applejuicenet.client.gui.download.table.*;
import de.applejuicenet.client.shared.IconManager;
import de.applejuicenet.client.shared.tablecellrenderer.*;
import org.slf4j.Logger;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;

/**
 *
 * Copyright: General Public License
 *
 */
public class DownloadPanel extends TklPanel {
    private DownloadOverviewPanel downloadOverviewPanel;
    private PowerDownloadPanel powerDownloadPanel;
    private DownloadsTableModel downloadActiveTableModel;
    private AutoRowHeightTable downloadActiveTable;
    private DownloadSourcesTableModel downloadSourceTableModel;
    private AutoRowHeightTable downloadSourceTable;
    private JPopupMenu popup = new JPopupMenu();
    private JScrollPane aScrollPane;
    private JMenuItem cancel;
    private JMenuItem pause;
    private JMenuItem resume;
    private JMenuItem rename;
    private JMenuItem targetFolder;
    private JMenuItem removeFinished;
    private JMenuItem itemReleaseInfo = new JMenuItem();
    private JMenuItem itemCopyToClipboard = new JMenuItem();
    private JMenuItem itemCopyToClipboardWithSources = new JMenuItem();
    private JMenuItem itemOpenWithDefaultProgram = new JMenuItem();
    private JSplitPane splitPane;
    private Logger logger;
    private TableColumn[] downloadColumns = new TableColumn[DownloadsTableModel.CLASS_TYPES.length];
    private TableColumn[] downloadSourceColumns = new TableColumn[DownloadSourcesTableModel.CLASS_TYPES.length];
    private JPopupMenu columnDownloadPopup = new JPopupMenu();
    private JCheckBoxMenuItem[] columnDownloadPopupItems = new JCheckBoxMenuItem[downloadColumns.length];
    private JPopupMenu columnDownloadSourcesPopup = new JPopupMenu();
    private JCheckBoxMenuItem[] columnDownloadSourcesPopupItems = new JCheckBoxMenuItem[downloadSourceColumns.length];
    private JPopupMenu menu;
    private JScrollPane downloadSourcesScrollPane;

    public DownloadPanel(GuiController guiController) {
        super(guiController);
        try {
            downloadOverviewPanel = new DownloadOverviewPanel(this);
            powerDownloadPanel = new PowerDownloadPanel((DownloadController) guiController);
            init();
        } catch (Exception e) {
            logger.error(ApplejuiceFacade.ERROR_MESSAGE, e);
        }
    }

    public JTable getDownloadTable() {
        return downloadActiveTable;
    }

    public JTable getDownloadSourceTable() {
        return downloadSourceTable;
    }

    public TableColumn[] getDownloadTableColumns() {
        return downloadColumns;
    }

    public TableColumn[] getDownloadSourcesTableColumns() {
        return downloadSourceColumns;
    }

    public DownloadOverviewPanel getDownloadOverviewPanel() {
        return downloadOverviewPanel;
    }

    public JScrollPane getScrollPane() {
        return aScrollPane;
    }

    public JSplitPane getSplitPane() {
        return splitPane;
    }

    public PowerDownloadPanel getPowerDownloadPanel() {
        return powerDownloadPanel;
    }

    public DownloadsTableModel getDownloadTableModel() {
        return downloadActiveTableModel;
    }

    public DownloadSourcesTableModel getDownloadSourcesTableModel() {
        return downloadSourceTableModel;
    }

    public JButton getBtnPowerDownload() {
        return powerDownloadPanel.getBtnPowerdownload();
    }

    public JRadioButton getBtnPowerDownloadInactive() {
        return powerDownloadPanel.btnInactive;
    }

    public JRadioButton getBtnPowerDownloadActive() {
        return powerDownloadPanel.btnActive;
    }

    public JTextField getRatioField() {
        return powerDownloadPanel.ratio;
    }

    public JMenuItem getMnuCancel() {
        return cancel;
    }

    public JMenuItem getMnuPause() {
        return pause;
    }

    public JMenuItem getMnuResume() {
        return resume;
    }

    public JMenuItem getMnuRename() {
        return rename;
    }

    public JMenuItem getMnuTargetFolder() {
        return targetFolder;
    }

    public JMenuItem getMnuRemoveFinished() {
        return removeFinished;
    }

    public JMenuItem getMnuReleaseInfo() {
        return itemReleaseInfo;
    }

    public JMenuItem getMnuOpenWithDefaultProgram() {
        return itemOpenWithDefaultProgram;
    }

    public JMenuItem getMnuCopyToClipboard() {
        return itemCopyToClipboard;
    }

    public JMenuItem getMnuCopyToClipboardWithSources() {
        return itemCopyToClipboardWithSources;
    }

    public JPopupMenu getPopup() {
        return popup;
    }

    private void init() throws Exception {
        setLayout(new BorderLayout());
        JPanel topPanel = new JPanel();

        topPanel.setLayout(new GridBagLayout());
        JPanel bottomPanel = new JPanel();

        bottomPanel.setLayout(new BorderLayout());

        cancel = new JMenuItem();
        pause = new JMenuItem();
        resume = new JMenuItem();
        rename = new JMenuItem();
        targetFolder = new JMenuItem();
        removeFinished = new JMenuItem();

        menu = new JPopupMenu();

        IconManager im = IconManager.getInstance();

        cancel.setIcon(im.getIcon("abbrechen"));
        pause.setIcon(im.getIcon("pause"));
        rename.setIcon(im.getIcon("umbenennen"));
        targetFolder.setIcon(im.getIcon("zielordner"));
        removeFinished.setIcon(im.getIcon("bereinigen"));
        resume.setIcon(im.getIcon("pause"));
        itemReleaseInfo.setIcon(im.getIcon("hint"));
        itemCopyToClipboard.setIcon(im.getIcon("clipboard"));
        itemCopyToClipboardWithSources.setIcon(im.getIcon("clipboard"));

        popup.add(resume);
        popup.add(pause);
        popup.add(cancel);
        popup.add(new JSeparator());
        popup.add(rename);
        popup.add(targetFolder);
        popup.add(new JSeparator());
        popup.add(removeFinished);
        popup.add(new JSeparator());
        popup.add(itemCopyToClipboard);
        popup.add(itemCopyToClipboardWithSources);
        popup.add(new JSeparator());
        popup.add(itemReleaseInfo);
        popup.add(itemOpenWithDefaultProgram);

        GridBagConstraints constraints = new GridBagConstraints();

        constraints.anchor = GridBagConstraints.NORTH;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 3;
        constraints.gridheight = 1;

        GridBagConstraints constraints2 = new GridBagConstraints();

        constraints2.anchor = GridBagConstraints.NORTH;
        constraints2.fill = GridBagConstraints.BOTH;
        constraints2.gridx = 0;
        constraints2.gridy = 0;

        constraints.gridwidth = 3;
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.weighty = 0.4;
        constraints.weightx = 1;

        downloadActiveTableModel = new DownloadsTableModel();
        downloadActiveTable = new AutoRowHeightTable(downloadActiveTableModel);
        downloadActiveTable.setFileTypeIcons();
        downloadActiveTable.setDefaultRenderer(String.class, new StringTableCellRenderer());
        downloadActiveTable.getColumnModel().getColumn(0).setCellRenderer(new DownloadTableDownloadFilenameCellRenderer());
        downloadActiveTable.getColumnModel().getColumn(2).setCellRenderer(new SizeTableCellRenderer());
        downloadActiveTable.getColumnModel().getColumn(3).setCellRenderer(new SizeTableCellRenderer());
        downloadActiveTable.getColumnModel().getColumn(4).setCellRenderer(new SpeedTableCellRenderer());
        downloadActiveTable.getColumnModel().getColumn(6).setCellRenderer(new ProgressTableCellRenderer());
        downloadActiveTable.getColumnModel().getColumn(7).setCellRenderer(new SizeTableCellRenderer());
        downloadActiveTable.getColumnModel().getColumn(8).setCellRenderer(new DownloadTablePowerdownloadCellRenderer());

        TableColumnModel model = downloadActiveTable.getColumnModel();
        SortButtonRenderer renderer = new SortButtonRenderer();

        JTableHeader header = downloadActiveTable.getTableHeader();

        header.setDefaultRenderer(renderer);
        header.addMouseListener(new HeaderListener(header, renderer) {
            private PositionManager pm = PositionManagerImpl.getInstance();

            @Override
            public void internalSort(int column, boolean ascent) {
                pm.setDownloadSort(column, ascent);

                super.internalSort(column, ascent);
            }
        });
        for (int i = 0; i < model.getColumnCount(); i++) {
            downloadColumns[i] = model.getColumn(i);
            columnDownloadPopupItems[i] = new JCheckBoxMenuItem((String) downloadColumns[i].getHeaderValue());
            final int x = i;

            columnDownloadPopupItems[i].addActionListener(ae -> {
                if (columnDownloadPopupItems[x].isSelected()) {
                    downloadActiveTable.getColumnModel().addColumn(downloadColumns[x]);
                    PositionManagerImpl.getInstance().setDownloadColumnVisible(x, true);
                    PositionManagerImpl.getInstance()
                            .setDownloadColumnIndex(x,
                                    downloadActiveTable.getColumnModel().getColumnIndex(downloadColumns[x].getIdentifier()));
                } else {
                    downloadActiveTable.getColumnModel().removeColumn(downloadColumns[x]);
                    PositionManagerImpl.getInstance().setDownloadColumnVisible(x, false);
                    for (int y = 0; y < downloadColumns.length; y++) {
                        try {
                            PositionManagerImpl.getInstance()
                                    .setDownloadColumnIndex(y,
                                            downloadActiveTable.getColumnModel()
                                                    .getColumnIndex(downloadColumns[y].getIdentifier()));
                        } catch (IllegalArgumentException niaE) {
                            // column not (yet) in the model: keep the stored position
                        }
                    }
                }
            });
            columnDownloadPopup.add(columnDownloadPopupItems[i]);
        }

        columnDownloadPopupItems[0].setEnabled(false);

        aScrollPane = new JScrollPane(downloadActiveTable);
        aScrollPane.setBackground(downloadActiveTable.getBackground());
        downloadActiveTable.getTableHeader().setBackground(downloadActiveTable.getBackground());
        aScrollPane.getViewport().setOpaque(false);
        topPanel.add(aScrollPane, constraints);

        downloadSourceTableModel = new DownloadSourcesTableModel();
        downloadSourceTable = new AutoRowHeightTable(downloadSourceTableModel);
        downloadSourceTable.setFileTypeIcons();
        downloadSourceTable.setDefaultRenderer(String.class, new StringTableCellRenderer());
        downloadSourceTable.getColumnModel().getColumn(0).setCellRenderer(new DownloadTableFilenameCellRenderer());
        downloadSourceTable.getColumnModel().getColumn(3).setCellRenderer(new SizeTableCellRenderer());
        downloadSourceTable.getColumnModel().getColumn(4).setCellRenderer(new SizeTableCellRenderer());
        downloadSourceTable.getColumnModel().getColumn(5).setCellRenderer(new SpeedTableCellRenderer());
        downloadSourceTable.getColumnModel().getColumn(7).setCellRenderer(new ProgressTableCellRenderer());
        downloadSourceTable.getColumnModel().getColumn(8).setCellRenderer(new SizeTableCellRenderer());
        downloadSourceTable.getColumnModel().getColumn(9).setCellRenderer(new DownloadTablePowerdownloadCellRenderer());
        downloadSourceTable.getColumnModel().getColumn(10).setCellRenderer(new VersionTableCellRenderer());

        renderer = new SortButtonRenderer();

        header = downloadSourceTable.getTableHeader();

        header.setDefaultRenderer(renderer);
        header.addMouseListener(new HeaderListener(header, renderer) {
            private PositionManager pm = PositionManagerImpl.getInstance();

            @Override
            public void internalSort(int column, boolean ascent) {
                pm.setDownloadSourcesSort(column, ascent);

                super.internalSort(column, ascent);
            }
        });
        model = downloadSourceTable.getColumnModel();
        for (int i = 0; i < model.getColumnCount(); i++) {
            downloadSourceColumns[i] = model.getColumn(i);
            columnDownloadSourcesPopupItems[i] = new JCheckBoxMenuItem((String) downloadSourceColumns[i].getHeaderValue());
            final int x = i;

            columnDownloadSourcesPopupItems[i].addActionListener(ae -> {
                if (columnDownloadSourcesPopupItems[x].isSelected()) {
                    downloadSourceTable.getColumnModel().addColumn(downloadSourceColumns[x]);
                    PositionManagerImpl.getInstance().setDownloadSourcesColumnVisible(x, true);
                    PositionManagerImpl.getInstance()
                            .setDownloadSourcesColumnIndex(x,
                                    downloadSourceTable.getColumnModel()
                                            .getColumnIndex(downloadSourceColumns[x].getIdentifier()));
                } else {
                    downloadSourceTable.getColumnModel().removeColumn(downloadSourceColumns[x]);
                    PositionManagerImpl.getInstance().setDownloadSourcesColumnVisible(x, false);
                    for (int y = 0; y < downloadSourceColumns.length; y++) {
                        try {
                            PositionManagerImpl.getInstance()
                                    .setDownloadSourcesColumnIndex(y,
                                            downloadSourceTable.getColumnModel()
                                                    .getColumnIndex(downloadSourceColumns[y].getIdentifier()));
                        } catch (IllegalArgumentException niaE) {
                            // column not (yet) in the model: keep the stored position
                        }
                    }
                }
            });
            columnDownloadSourcesPopup.add(columnDownloadSourcesPopupItems[i]);
        }

        downloadSourcesScrollPane = new JScrollPane(downloadSourceTable);

        downloadSourcesScrollPane.setBackground(downloadSourceTable.getBackground());
        downloadSourceTable.getTableHeader().setBackground(downloadSourceTable.getBackground());
        downloadSourcesScrollPane.getViewport().setOpaque(false);

        constraints.gridwidth = 3;
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.weighty = 0.6;
        constraints.weightx = 1;

        topPanel.add(downloadSourcesScrollPane, constraints);

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weighty = 1;

        bottomPanel.add(powerDownloadPanel, BorderLayout.WEST);
        bottomPanel.add(downloadOverviewPanel, BorderLayout.CENTER);

        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topPanel, bottomPanel);
        splitPane.setBorder(null);
        add(splitPane, BorderLayout.CENTER);

        downloadActiveTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    }

    public JScrollPane getDownloadSourcesScrollPane() {
        return downloadSourcesScrollPane;
    }

    public int[] getDownloadColumnWidths() {
        int[] widths = new int[downloadColumns.length];

        for (int i = 0; i < downloadColumns.length; i++) {
            widths[i] = downloadColumns[i].getWidth();
        }

        return widths;
    }

    public int[] getDownloadSourcesColumnWidths() {
        int[] widths = new int[downloadSourceColumns.length];

        for (int i = 0; i < downloadSourceColumns.length; i++) {
            widths[i] = downloadSourceColumns[i].getWidth();
        }

        return widths;
    }

    public JCheckBoxMenuItem[] getColumnDownloadPopupItems() {
        return columnDownloadPopupItems;
    }

    public JPopupMenu getColumnDownloadPopup() {
        return columnDownloadPopup;
    }

    public JPopupMenu getColumnDownloadSourcesPopup() {
        return columnDownloadSourcesPopup;
    }

    public JCheckBoxMenuItem[] getColumnDownloadSourcesPopupItems() {
        return columnDownloadSourcesPopupItems;
    }
}
