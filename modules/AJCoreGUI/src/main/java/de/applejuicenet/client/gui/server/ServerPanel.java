/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.server;

import de.applejuicenet.client.shared.CoreCall;
import de.applejuicenet.client.gui.DialogLocation;

import de.applejuicenet.client.AppleJuiceClient;
import de.applejuicenet.client.facade.ApplejuiceFacade;
import de.applejuicenet.client.facade.entity.Information;
import de.applejuicenet.client.facade.entity.Server;
import de.applejuicenet.client.facade.exception.IllegalArgumentException;
import de.applejuicenet.client.facade.listener.DataUpdateListener;
import de.applejuicenet.client.facade.shared.NetworkInfo;
import de.applejuicenet.client.gui.AppleJuiceDialog;
import de.applejuicenet.client.gui.RegisterI;
import de.applejuicenet.client.gui.components.table.HeaderListener;
import de.applejuicenet.client.gui.components.table.SortButtonRenderer;
import de.applejuicenet.client.gui.components.table.AutoRowHeightTable;
import de.applejuicenet.client.gui.components.table.SortableTableModel;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.controller.OptionsManagerImpl;
import de.applejuicenet.client.gui.controller.PositionManager;
import de.applejuicenet.client.gui.controller.PositionManagerImpl;
import de.applejuicenet.client.gui.controller.TableColumnSettings;
import de.applejuicenet.client.gui.controller.TableSelection;
import de.applejuicenet.client.gui.listener.LanguageListener;
import de.applejuicenet.client.gui.server.table.ServerTableCellRenderer;
import de.applejuicenet.client.gui.server.table.ServerTableDateCellRenderer;
import de.applejuicenet.client.gui.server.table.ServerTableModel;
import de.applejuicenet.client.shared.IconManager;
import de.applejuicenet.client.shared.PopupMenuSupport;
import de.applejuicenet.client.shared.SoundPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Vector;

/**
 *
 * Copyright: General Public License
 *
 */
public class ServerPanel extends JPanel implements LanguageListener, DataUpdateListener, RegisterI {
    private static ServerPanel instance;
    private AutoRowHeightTable serverTable;
    private JButton searchServer = new JButton();
    private JPopupMenu popup = PopupMenuSupport.create();
    private JPopupMenu popup2 = PopupMenuSupport.create();
    private JPopupMenu popup3 = PopupMenuSupport.create();
    private JMenuItem connectingText;
    private JMenuItem delete1;
    private JMenuItem add2;
    private JMenuItem add3;
    private JMenuItem add1;
    private JMenuItem delete2;
    private JMenuItem itemCopyToClipboard;
    private JLabel connectedText = new JLabel();
    private JLabel tryingToConnect = new JLabel();
    private JLabel older24h = new JLabel();
    private JLabel younger24h = new JLabel();
    private Logger logger;
    private boolean initialized = false;
    private String warningTitle;
    private String warningMessage;
    private boolean tabSelected;

    private ServerPanel() {
        logger = LoggerFactory.getLogger(getClass());
        try {
            init();
        } catch (Exception e) {
            logger.error(ApplejuiceFacade.ERROR_MESSAGE, e);
        }
    }

    public static synchronized ServerPanel getInstance() {
        if (instance == null) {
            instance = new ServerPanel();
        }

        return instance;
    }

    private void init() throws Exception {
        setLayout(new BorderLayout());
        LanguageSelector.getInstance().addLanguageListener(this);

        IconManager im = IconManager.getInstance();

        connectingText = new JMenuItem();
        connectingText.setIcon(im.getIcon("serververbinden"));
        add1 = new JMenuItem();
        add1.setIcon(im.getIcon("serverneu"));
        delete1 = new JMenuItem();
        delete1.setIcon(im.getIcon("serverloeschen"));
        add2 = new JMenuItem();
        add2.setIcon(im.getIcon("serverneu"));
        add3 = new JMenuItem();
        add3.setIcon(im.getIcon("serverneu"));
        delete2 = new JMenuItem();
        delete2.setIcon(im.getIcon("serverloeschen"));
        itemCopyToClipboard = new JMenuItem();
        itemCopyToClipboard.setIcon(im.getIcon("clipboard"));

        popup.add(connectingText);
        popup.add(add3);
        popup.add(itemCopyToClipboard);
        popup.add(delete1);
        popup2.add(add2);
        popup3.add(add1);
        popup3.add(delete2);
        connectingText.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                int selected = serverTable.getSelectedRow();
                Server server = ((ServerTableModel) serverTable.getModel()).getRow(selected);
                ApplejuiceFacade af = AppleJuiceClient.getAjFacade();

                if (af.getInformation().getConnectionStatus() == Information.CONNECTED) {
                    NetworkInfo netInfo = af.getNetworkInfo();
                    long timestamp = af.getLastCoreTimestamp().longValue();

                    if (timestamp == 0) {
                        /**
                         * No reference data has been fetched yet. We use our own time, hoping that
                         * they match.
                         */
                        timestamp = System.currentTimeMillis();
                    }

                    long timeDiff = timestamp - netInfo.getConnectionTime();
                    int minutes = (int) (timeDiff / 60000);

                    if (minutes < 0) {
                        minutes = 0;
                    }

                    if (minutes <= 30) {
                        String tmp = warningMessage.replaceAll("%s", Integer.toString(minutes));
                        int result = JOptionPane.showConfirmDialog(AppleJuiceDialog.getApp(), tmp, warningTitle,
                                JOptionPane.YES_NO_OPTION);

                        if (result != JOptionPane.YES_OPTION) {
                            return;
                        }
                    }
                }

                // Core call blocks on a slow core: not on the EDT
                CoreCall.runInBackground("ConnectToServer", () -> {
                    af.connectToServer(server);
                    SoundPlayer.getInstance().playSound(SoundPlayer.CONNECT);
                });
            }
        });

        ActionListener deleteServerListener = new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                int[] selected = serverTable.getSelectedRows();

                if (selected.length > 0) {
                    Server server = null;
                    List<Server> toRemove = new Vector<Server>();

                    for (int j : selected) {
                        server = ((ServerTableModel) serverTable.getModel()).getRow(j);
                        if (server != null) {
                            toRemove.add(server);
                        }
                    }

                    CoreCall.runInBackground("RemoveServers",
                            () -> AppleJuiceClient.getAjFacade().removeServer(toRemove));
                }
            }
        };

        ActionListener clipboardListener = ae -> {
            int[] selectedItems = serverTable.getSelectedRows();

            if (selectedItems.length == 1) {
                Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
                StringBuilder toCopy = new StringBuilder();

                toCopy.append("ajfsp://server|");
                Server server = ((ServerTableModel) serverTable.getModel()).getRow(selectedItems[0]);

                toCopy.append(server.getHost());
                toCopy.append("|");
                toCopy.append(server.getPort());
                toCopy.append("/");
                StringSelection contents = new StringSelection(toCopy.toString());

                cb.setContents(contents, null);
            }
        };

        itemCopyToClipboard.addActionListener(clipboardListener);
        delete1.addActionListener(deleteServerListener);
        delete2.addActionListener(deleteServerListener);
        ActionListener newServerListener = new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                NewServerDialog newServerDialog = new NewServerDialog(AppleJuiceDialog.getApp(), true);
                DialogLocation.center(newServerDialog);
                newServerDialog.setVisible(true);
                if (newServerDialog.isLegal()) {
                    final String link = newServerDialog.getLink();

                    CoreCall.runInBackground("AddServer", () -> AppleJuiceClient.getAjFacade().processLink(link, ""));
                }
            }
        };

        add2.addActionListener(newServerListener);
        add3.addActionListener(newServerListener);
        add1.addActionListener(newServerListener);

        JPanel panel1 = new JPanel();

        panel1.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        constraints.anchor = GridBagConstraints.NORTH;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 0;

        searchServer.addActionListener(ae -> {
            CoreCall.runInBackground("SearchServers", () -> {
                String ServerListURL = OptionsManagerImpl.getInstance().getServerListURL();
                ApplejuiceFacade af = AppleJuiceClient.getAjFacade();
                String[] server = af.getNetworkKnownServers(ServerListURL);

                if (server == null || server.length == 0) {
                    return;
                }

                for (int i = 0; i < server.length; i++) {
                    try {
                        af.processLink(server[i], "");
                    } catch (IllegalArgumentException e) {
                        // one rejected entry must not stop the rest of the list
                        logger.warn("Server entry {} was rejected: {}", server[i], e.toString());
                    }
                }
            });
        });
        panel1.add(searchServer, constraints);
        constraints.gridx = 1;
        constraints.weightx = 1;
        panel1.add(new JLabel(), constraints);
        add(panel1, BorderLayout.NORTH);
        serverTable = new AutoRowHeightTable();
        serverTable.setRowIcons(im.getIcon("serververbunden"), im.getIcon("serverversuche"),
                im.getIcon("aelter24h"), im.getIcon("juenger24h"));
        serverTable.setModel(new ServerTableModel());
        serverTable.setShowGrid(false);
        serverTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        SortButtonRenderer renderer = new SortButtonRenderer();
        TableColumnModel model = serverTable.getColumnModel();
        int n = model.getColumnCount();

        for (int i = 0; i < n; i++) {
            model.getColumn(i).setHeaderRenderer(renderer);
            model.getColumn(i).setPreferredWidth(model.getColumn(i).getWidth());
        }

        JTableHeader header = serverTable.getTableHeader();

        header.addMouseListener(new HeaderListener(header, renderer) {
            private PositionManager pm = PositionManagerImpl.getInstance();

            @Override
            public void internalSort(int column, boolean ascent) {
                pm.setServerSort(column, ascent);

                super.internalSort(column, ascent);
            }
        });

        serverTable.setDefaultRenderer(Server.class, new ServerTableCellRenderer());
        serverTable.setDefaultRenderer(Date.class, new ServerTableDateCellRenderer());
        final JScrollPane aScrollPane = new JScrollPane(serverTable);

        aScrollPane.setBackground(serverTable.getBackground());
        serverTable.getTableHeader().setBackground(serverTable.getBackground());
        aScrollPane.getViewport().setOpaque(false);
        MouseAdapter popupMouseAdapter = new MouseAdapter() {
            public void mousePressed(MouseEvent me) {
                super.mouseReleased(me);
                maybeShowPopup(me);
            }

            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                maybeShowPopup(e);
            }

            private void maybeShowPopup(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    int selectedRow = serverTable.rowAtPoint(e.getPoint());

                    if (selectedRow != -1) {
                        if (serverTable.getSelectedRowCount() == 0) {
                            serverTable.setRowSelectionInterval(selectedRow, selectedRow);
                        } else {
                            int[] currentSelectedRows = serverTable.getSelectedRows();

                            for (int i = 0; i < currentSelectedRows.length; i++) {
                                if (currentSelectedRows[i] == selectedRow) {
                                    selectedRow = -1;
                                    break;
                                }
                            }

                            if (selectedRow != -1) {
                                serverTable.setRowSelectionInterval(selectedRow, selectedRow);
                            }
                        }
                    }

                    if (serverTable.getSelectedRowCount() == 1) {
                        popup.show(serverTable, e.getX(), e.getY());
                    } else if (serverTable.getSelectedRowCount() > 1) {
                        popup3.show(serverTable, e.getX(), e.getY());
                    } else {
                        popup2.show(serverTable, e.getX(), e.getY());
                    }
                }
            }
        };

        aScrollPane.addMouseListener(popupMouseAdapter);
        serverTable.addMouseListener(popupMouseAdapter);
        add(aScrollPane, BorderLayout.CENTER);
        JPanel legend = new JPanel(new FlowLayout());
        ImageIcon icon1 = im.getIcon("serververbunden");
        ImageIcon icon2 = im.getIcon("serverversuche");
        ImageIcon icon3 = im.getIcon("aelter24h");
        ImageIcon icon4 = im.getIcon("juenger24h");
        JLabel label1 = new JLabel(icon1);
        JLabel label2 = new JLabel(icon2);
        JLabel label3 = new JLabel(icon3);
        JLabel label4 = new JLabel(icon4);

        legend.add(label1);
        legend.add(connectedText);
        legend.add(label2);
        legend.add(tryingToConnect);
        legend.add(label3);
        legend.add(older24h);
        legend.add(label4);
        legend.add(younger24h);
        add(legend, BorderLayout.SOUTH);
        AppleJuiceClient.getAjFacade().addDataUpdateListener(this, DATALISTENER_TYPE.SERVER_CHANGED);
    }

    public void registerSelected() {
        tabSelected = true;
        try {
            if (!initialized) {
                initialized = true;
                TableColumnModel headerModel = serverTable.getTableHeader().getColumnModel();
                int columnCount = headerModel.getColumnCount();
                PositionManager pm = PositionManagerImpl.getInstance();
                int[] sort = pm.getServerSort();

                if (null != sort) {
                    for (MouseListener curMl : serverTable.getTableHeader().getMouseListeners()) {
                        if (curMl instanceof HeaderListener) {
                            ((HeaderListener) curMl).sort(sort[0], sort[1] == 1);
                        }
                    }
                }

                serverTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                TableColumn[] columns = new TableColumn[columnCount];
                for (int index = 0; index < columnCount; index++) {
                    columns[index] = headerModel.getColumn(index);
                }
                TableColumnSettings.installCompact(serverTable, "server", columns);
            }
            TableColumnSettings.refreshCompact(serverTable);
            TableSelection.selectOnlyRow(serverTable);
        } catch (Exception e) {
            logger.error(ApplejuiceFacade.ERROR_MESSAGE, e);
        }
    }

    @SuppressWarnings("unchecked")
    public void fireContentChanged(DATALISTENER_TYPE type, final Object content) {
        if (type != DATALISTENER_TYPE.SERVER_CHANGED) {
            return;
        }

        if (!SwingUtilities.isEventDispatchThread()) {
            final HashMap<String, Server> snapshot = new HashMap<String, Server>((Map<String, Server>) content);

            SwingUtilities.invokeLater(() -> fireContentChanged(type, snapshot));
            return;
        }

        boolean changed = ((ServerTableModel) serverTable.getModel()).setTable((HashMap<String, Server>) content);

        if (changed && tabSelected) {
            TableColumnSettings.refreshCompact(serverTable);
            ((SortableTableModel) serverTable.getModel()).forceResort();
            serverTable.revalidate();
            serverTable.repaint();
        }
        if (tabSelected) {
            TableSelection.selectOnlyRow(serverTable);
        }
    }

    public void fireLanguageChanged() {
        try {
            LanguageSelector languageSelector = LanguageSelector.getInstance();

            searchServer.setText(languageSelector.getFirstAttributeByTagName("mainform.Label11.caption"));
            String[] columns = new String[5];

            columns[0] = languageSelector.getFirstAttributeByTagName("mainform.serverlist.col0caption");
            columns[1] = languageSelector.getFirstAttributeByTagName("mainform.serverlist.col1caption");
            columns[2] = languageSelector.getFirstAttributeByTagName("mainform.serverlist.col3caption");
            columns[3] = languageSelector.getFirstAttributeByTagName("javagui.serverform.col4caption");
            columns[4] = languageSelector.getFirstAttributeByTagName("mainform.serverlist.col5caption");
            connectingText.setText(languageSelector.getFirstAttributeByTagName("mainform.connserv.caption"));
            delete1.setText(languageSelector.getFirstAttributeByTagName("mainform.delserv.caption"));
            add2.setText(languageSelector.getFirstAttributeByTagName("mainform.addserv.caption"));
            add3.setText(languageSelector.getFirstAttributeByTagName("mainform.addserv.caption"));
            add1.setText(languageSelector.getFirstAttributeByTagName("mainform.addserv.caption"));
            delete2.setText(languageSelector.getFirstAttributeByTagName("mainform.delserv.caption"));
            connectedText.setText(languageSelector.getFirstAttributeByTagName("javagui.serverform.connected"));
            tryingToConnect.setText(languageSelector.getFirstAttributeByTagName("javagui.serverform.connecting"));
            older24h.setText(languageSelector.getFirstAttributeByTagName("javagui.serverform.older24h"));
            younger24h.setText(languageSelector.getFirstAttributeByTagName("javagui.serverform.younger24h"));
            warningTitle = languageSelector.getFirstAttributeByTagName("mainform.caption");
            warningMessage = languageSelector.getFirstAttributeByTagName("javagui.serverform.warningmessage");
            itemCopyToClipboard.setText(languageSelector.getFirstAttributeByTagName("mainform.getlink1.caption"));

            TableColumnModel tcm = serverTable.getColumnModel();

            for (int i = 0; i < tcm.getColumnCount(); i++) {
                tcm.getColumn(i).setHeaderValue(columns[i]);
            }
        } catch (Exception e) {
            logger.error(ApplejuiceFacade.ERROR_MESSAGE, e);
        }
    }

    public int[] getColumnWidths() {
        TableColumnModel tcm = serverTable.getColumnModel();
        int[] widths = new int[tcm.getColumnCount()];

        for (int i = 0; i < tcm.getColumnCount(); i++) {
            widths[i] = tcm.getColumn(i).getWidth();
        }

        return widths;
    }

    public void lostSelection() {
        tabSelected = false;
    }
}
