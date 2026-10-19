/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.search;

import de.applejuicenet.client.shared.CoreCall;
import de.applejuicenet.client.AppleJuiceClient;
import de.applejuicenet.client.facade.ApplejuiceFacade;
import de.applejuicenet.client.facade.entity.Search;
import de.applejuicenet.client.facade.entity.SearchEntry;
import de.applejuicenet.client.facade.exception.IllegalArgumentException;
import de.applejuicenet.client.facade.shared.FileType;
import de.applejuicenet.client.gui.AppleJuiceDialog;
import de.applejuicenet.client.gui.components.table.SortButtonRenderer;
import de.applejuicenet.client.gui.components.table.AutoRowHeightTable;
import de.applejuicenet.client.gui.components.table.SortableTableModel;
import de.applejuicenet.client.gui.controller.PositionManager;
import de.applejuicenet.client.gui.controller.PositionManagerImpl;
import de.applejuicenet.client.gui.controller.TableColumnSettings;
import de.applejuicenet.client.gui.search.table.SearchEntryIconRenderer;
import de.applejuicenet.client.gui.search.table.SearchEntrySizeRenderer;
import de.applejuicenet.client.gui.search.table.SearchTableModel;
import de.applejuicenet.client.shared.IconManager;
import de.applejuicenet.client.shared.ReleaseInfo;
import de.applejuicenet.client.shared.SoundPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Copyright: General Public License
 */
public class SearchResultPanel extends JPanel {
    private static String openSearches = "%i offene Suchen";
    private static String foundFilesList = "%i found files";
    private static String searchedClients = "%i durchsuchte Clients";
    private static String loadLink = "Link";
    private static String stopSearch = "Suche stoppen";
    private static String alreadyLoaded;
    private static String invalidLink;
    private static String linkFailure;
    private static String dialogTitle;
    private static String releaseInfo;
    private static String[] columns;
    private final Logger logger;
    private AutoRowHeightTable searchResultTable;
    private SearchTableModel searchResultTableModel;
    private Search search;
    private JButton cancelSearchButton = new JButton();
    private JLabel label1 = new JLabel();
    private JLabel label2 = new JLabel();
    private JLabel label3 = new JLabel();
    private JPopupMenu popup = new JPopupMenu();
    private JMenuItem item1 = new JMenuItem();
    private JMenuItem mnuReleaseInfo = new JMenuItem();
    private JToggleButton[] filterButtons;
    private TableColumn[] tableColumns = new TableColumn[3];

    public SearchResultPanel(Search aSearch) {
        search = aSearch;
        logger = LoggerFactory.getLogger(getClass());
        try {
            init();
        } catch (Exception e) {
            logger.error(ApplejuiceFacade.ERROR_MESSAGE, e);
        }
    }

    public Search getSearch() {
        return search;
    }

    private void init() throws Exception {
        IconManager im = IconManager.getInstance();

        item1.setText(loadLink);
        item1.setIcon(im.getIcon("download"));
        cancelSearchButton.setText(stopSearch);
        cancelSearchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                cancelSearchButton.setEnabled(false);
                try {
                    AppleJuiceClient.getAjFacade().cancelSearch(search);
                } catch (IllegalArgumentException e) {
                    logger.error(ApplejuiceFacade.ERROR_MESSAGE, e);
                }
            }
        });
        popup.add(item1);

        mnuReleaseInfo.setIcon(im.getIcon("hint"));
        mnuReleaseInfo.setText(releaseInfo);
        mnuReleaseInfo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                int[] sel = searchResultTable.getSelectedRows();

                if (null == sel || sel.length < 1) {
                    return;
                }

                SearchEntry curSearchEntry = searchResultTableModel.getRow(sel[0]);

                ReleaseInfo.handle(curSearchEntry.getFileNames()[0].getFileName(), curSearchEntry.getChecksum(), curSearchEntry.getSize());
            }
        });
        popup.add(mnuReleaseInfo);
        setLayout(new BorderLayout());
        updateNumbers();
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton all = new JButton(IconManager.getInstance().getIcon("abbrechen"));

        buttonPanel.add(all);

        FileType[] allTypes = FileType.values();

        filterButtons = new JToggleButton[allTypes.length];
        for (int i = 0; i < allTypes.length; i++) {
            filterButtons[i] = new JToggleButton(IconManager.getInstance().getIcon(allTypes[i].toString()));
            filterButtons[i].addActionListener(new FilterAdapter(allTypes[i]));
            buttonPanel.add(filterButtons[i]);
        }

        all.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                for (int i = 0; i < filterButtons.length; i++) {
                    filterButtons[i].setSelected(false);
                }

                search.clearFilter();
                searchResultTableModel.fireTableDataChanged();
            }
        });

        add(buttonPanel, BorderLayout.NORTH);
        searchResultTableModel = new SearchTableModel(search);
        searchResultTable = new AutoRowHeightTable(searchResultTableModel);
        searchResultTable.setFileTypeIcons();

        searchResultTable.setDefaultRenderer(SearchEntry.class, new SearchEntryIconRenderer());
        searchResultTable.setDefaultRenderer(Long.class, new SearchEntrySizeRenderer());

        add(new JScrollPane(searchResultTable), BorderLayout.CENTER);
        JPanel southPanel = new JPanel(new BorderLayout());
        JPanel textPanel = new JPanel(new FlowLayout());

        textPanel.add(label1);
        textPanel.add(label2);
        textPanel.add(label3);

        JPanel panel1 = new JPanel(new FlowLayout());

        panel1.add(cancelSearchButton);
        southPanel.add(panel1, BorderLayout.WEST);
        southPanel.add(textPanel, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);
        MouseAdapter popupMouseAdapter = new MouseAdapter() {
            public void mousePressed(MouseEvent me) {
                if (SwingUtilities.isRightMouseButton(me)) {
                    Point p = me.getPoint();
                    int row = searchResultTable.rowAtPoint(p);

                    if (row != -1 && !searchResultTable.getSelectionModel().isSelectedIndex(row)) {
                        searchResultTable.getSelectionModel().setSelectionInterval(row, row);
                    }
                }

                maybeShowPopup(me);
            }

            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                maybeShowPopup(e);
            }

            private void maybeShowPopup(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    int[] sel = searchResultTable.getSelectedRows();

                    if (null != sel && sel.length > 0) {
                        popup.show(searchResultTable, e.getX(), e.getY());
                    }
                }
            }
        };

        searchResultTable.addMouseListener(popupMouseAdapter);
        item1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                int[] sel = searchResultTable.getSelectedRows();

                if (null != sel && sel.length > 0) {
                    SearchEntry curSearchEntry;

                    for (int curRow : sel) {
                        curSearchEntry = searchResultTableModel.getRow(curRow);
                        StringBuilder toCopy = new StringBuilder();

                        toCopy.append("ajfsp://file|");
                        toCopy.append(curSearchEntry.getFileNames()[0].getFileName() + "|" + curSearchEntry.getChecksum() + "|" +
                                curSearchEntry.getSize() + "/");
                        String link = toCopy.toString();

                        processLink(link);
                    }
                }
            }
        });

        TableColumnModel model = searchResultTable.getColumnModel();
        SortButtonRenderer renderer = new SortButtonRenderer();

        for (int i = 0; i < tableColumns.length; i++) {
            tableColumns[i] = model.getColumn(i);
            tableColumns[i].setHeaderRenderer(renderer);
        }

        JTableHeader header = searchResultTable.getTableHeader();
        searchResultTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        TableColumnSettings.installCompact(searchResultTable, "search", tableColumns);

        SortMouseAdapter sortMouseAdapter = new SortMouseAdapter(header, renderer);

        header.addMouseListener(sortMouseAdapter);

        PositionManager pm = PositionManagerImpl.getInstance();
        int[] sort = pm.getSearchSort();

        sortMouseAdapter.sort(sort[0], sort[1] == 1);
        if (!search.isRunning()) {
            cancelSearchButton.setEnabled(false);
        }
    }

    private void processLink(final String link) {
        CoreCall.runInBackground("ProcessLink", () -> {
            {
                final String result = AppleJuiceClient.getAjFacade().processLink(link, "");

                SoundPlayer.getInstance().playSound(SoundPlayer.LOADING);
                if (result.indexOf("ok") != 0) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            String message = null;

                            if (result.indexOf("already downloaded") != -1) {
                                message = alreadyLoaded.replaceAll("%s", link);
                            } else if (result.indexOf("incorrect link") != -1) {
                                message = invalidLink.replaceAll("%s", link);
                            } else if (result.indexOf("failure") != -1) {
                                message = linkFailure;
                            }

                            if (message != null) {
                                JOptionPane.showMessageDialog(AppleJuiceDialog.getApp(), message, dialogTitle,
                                        JOptionPane.OK_OPTION | JOptionPane.INFORMATION_MESSAGE);
                            }
                        }
                    });
                }
            }
        });
    }

    public static void setTexts(String[] texts, String[] tableColumns) {
        openSearches = texts[0];
        foundFilesList = texts[1];
        searchedClients = texts[2];
        loadLink = texts[3];
        stopSearch = texts[4];
        alreadyLoaded = texts[5];
        invalidLink = texts[6];
        linkFailure = texts[7];
        dialogTitle = texts[8];
        releaseInfo = texts[9];
        columns = tableColumns;
    }

    public void setSearchSnapshot(Search snapshot) {
        search = snapshot;
        FileType[] types = FileType.values();
        search.clearFilter();
        for (int i = 0; i < filterButtons.length; i++) {
            if (!filterButtons[i].isSelected()) {
                search.addFilter(types[i]);
            }
        }
        searchResultTableModel.setSearchSnapshot(snapshot);
    }

    public void updateSearchContent() {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    updateSearchContent();

                }
            });
            return;
        }

        try {
            searchResultTableModel.forceResort();
            TableColumnSettings.refreshCompact(searchResultTable);
            searchResultTable.revalidate();
            searchResultTable.repaint();
            updateNumbers();
        } catch (Exception e) {
            logger.error(ApplejuiceFacade.ERROR_MESSAGE, e);
        }
    }

    public void updateNumbers() {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    updateNumbers();

                }
            });
            return;
        }

        List<SearchEntry> searchEntries = search.getAllSearchEntries();

        label1.setText(openSearches.replaceFirst("%i", Integer.toString(search.getOpenSearches())));
        label2.setText(foundFilesList.replaceFirst("%i", Integer.toString(searchEntries.size())));
        label3.setText(searchedClients.replaceFirst("%i", Integer.toString(search.getSearchedClients())));
    }

    public void changeLanguage() {
        try {
            item1.setText(loadLink);
            mnuReleaseInfo.setText(releaseInfo);
            cancelSearchButton.setText(stopSearch);
            label1.setText(openSearches.replaceFirst("%i", Integer.toString(search.getOpenSearches())));
            label2.setText(foundFilesList.replaceFirst("%i", Long.toString(search.getEntryCount())));
            label3.setText(searchedClients.replaceFirst("%i", Integer.toString(search.getSearchedClients())));
            TableColumnModel columnModel = searchResultTable.getColumnModel();

            for (int i = 0; i < columnModel.getColumnCount(); i++) {
                columnModel.getColumn(i).setHeaderValue(columns[i]);
            }
        } catch (Exception e) {
            logger.error(ApplejuiceFacade.ERROR_MESSAGE, e);
        }
    }

    class SortMouseAdapter extends MouseAdapter {
        private JTableHeader header;
        private SortButtonRenderer renderer;
        private PositionManager pm = PositionManagerImpl.getInstance();

        public SortMouseAdapter(JTableHeader header, SortButtonRenderer renderer) {
            this.header = header;
            this.renderer = renderer;
            header.repaint();
        }

        public void mouseClicked(MouseEvent e) {
            if (e.getButton() != MouseEvent.BUTTON1) {
                return;
            }

            int col = header.columnAtPoint(e.getPoint());
            int curSelectedColumn = renderer.getSelectedColumn();
            boolean ascent;

            if (col == curSelectedColumn) {
                ascent = !renderer.getState();
            } else {
                ascent = true;
            }

            pm.setSearchSort(col, ascent);
            sort(col, ascent);
        }

        @SuppressWarnings("unchecked")
        public void sort(int column, boolean ascent) {
            renderer.setSelectedColumn(column, ascent);
            header.repaint();

            if (header.getTable().isEditing()) {
                header.getTable().getCellEditor().stopCellEditing();
            }

            int sortCol = header.getTable().convertColumnIndexToModel(column);

            ((SortableTableModel) header.getTable().getModel()).sortByColumn(sortCol, ascent);
            header.repaint();
        }
    }


    private class FilterAdapter implements ActionListener {
        private FileType filter;

        public FilterAdapter(FileType newFilter) {
            filter = newFilter;
        }

        public void actionPerformed(ActionEvent ae) {
            JToggleButton source = (JToggleButton) ae.getSource();

            if (source.isSelected()) {
                search.removeFilter(filter);
            } else {
                search.addFilter(filter);
            }

            searchResultTableModel.fireTableDataChanged();
        }
    }
}
