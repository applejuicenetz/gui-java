package de.applejuicenet.client.gui;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableColumnModel;
import javax.swing.tree.DefaultTreeModel;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import de.applejuicenet.client.gui.controller.ApplejuiceFassade;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.controller.PositionManager;
import de.applejuicenet.client.gui.controller.PropertiesManager;
import de.applejuicenet.client.gui.listener.LanguageListener;
import de.applejuicenet.client.gui.tables.share.ShareModel;
import de.applejuicenet.client.gui.tables.share.ShareNode;
import de.applejuicenet.client.gui.tables.share.ShareTable;
import de.applejuicenet.client.gui.trees.WaitNode;
import de.applejuicenet.client.gui.trees.share.DirectoryNode;
import de.applejuicenet.client.gui.trees.share.DirectoryTree;
import de.applejuicenet.client.gui.trees.share.ShareSelectionTreeCellRenderer;
import de.applejuicenet.client.gui.trees.share.ShareSelectionTreeModel;
import de.applejuicenet.client.shared.AJSettings;
import de.applejuicenet.client.shared.IconManager;
import de.applejuicenet.client.shared.ShareEntry;
import de.applejuicenet.client.shared.SwingWorker;
import de.applejuicenet.client.shared.ZeichenErsetzer;
import de.applejuicenet.client.shared.dac.ShareDO;
import javax.swing.JSplitPane;
import de.applejuicenet.client.shared.Information;
import de.applejuicenet.client.shared.dac.ServerDO;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/Attic/SharePanel.java,v 1.58 2004/02/10 14:58:23 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: SharePanel.java,v $
 * Revision 1.58  2004/02/10 14:58:23  maj0r
 * Link mit Quellen kann nun auch im Sharebereich erzeugt werden.
 *
 * Revision 1.57  2004/02/09 20:11:57  maj0r
 * SplitPane eingebaut.
 *
 * Revision 1.56  2004/02/05 23:11:27  maj0r
 * Formatierung angepasst.
 *
 * Revision 1.55  2004/02/04 14:26:05  maj0r
 * Bug #185 gefixt (Danke an muhviestarr)
 * Einstellungen des GUIs werden beim Schliessen des Core gesichert.
 *
 * Revision 1.54  2004/01/25 08:31:11  maj0r
 * Icons eingebaut.
 *
 * Revision 1.53  2004/01/05 15:30:22  maj0r
 * Bug #43 gefixt (Danke an flabeg)
 * Shareverzeichnis wird bei Prioritaetenaenderung nicht mehr komplett neu geladen, sondern nur aktualsiert.
 *
 * Revision 1.52  2003/12/29 16:04:17  maj0r
 * Header korrigiert.
 *
 * Revision 1.51  2003/12/29 10:54:57  maj0r
 * Bug #4 gefixt (Danke an muhviestarr).
 * Shareanzeige bei Prioritaetenaenderung gefixt.
 *
 * Revision 1.50  2003/12/22 13:56:14  maj0r
 * Kleine Bug behoben, der sich beim Einbau eines Thread eingeschlichen hat.
 *
 * Revision 1.49  2003/12/19 09:54:14  maj0r
 * Bug der Tableheader der Share- und der Uploadtabelle behoben (Danke an muhviestarr).
 *
 * Revision 1.48  2003/12/18 14:24:24  maj0r
 * Leerzeichen bekommen bei UBB-Code nun eine Sonderbehandlung.
 *
 * Revision 1.47  2003/12/18 13:26:51  maj0r
 * Es kann nun der Link einer gesharten Datei über das Popupmenue der Sharetabelle als UBB-Code in die Ablage kopiert werden.
 *
 * Revision 1.46  2003/12/17 11:39:45  maj0r
 * Initialen Aufruf des Sharetabs durch einen Initialisierungsthread beschleunigt.
 * Muell entfernt.
 *
 * Revision 1.45  2003/12/17 11:06:30  maj0r
 * RegisterI erweitert, um auf Verlassen eines Tabs reagieren zu koennen.
 *
 * Revision 1.44  2003/12/16 17:05:54  maj0r
 * Sharetabelle auf vielfachen Wunsch komplett überarbeitet.
 *
 * Revision 1.43  2003/12/05 11:18:02  maj0r
 * Workaround fürs Setzen der Hintergrundfarben der Scrollbereiche ausgebaut.
 *
 * Revision 1.42  2003/11/30 17:01:33  maj0r
 * Hintergrundfarbe aller Scrollbereiche an ihre Tabellen angepasst.
 *
 * Revision 1.41  2003/10/21 14:08:45  maj0r
 * Mittels PMD Code verschoenert, optimiert.
 *
 * Revision 1.40  2003/10/14 15:40:43  maj0r
 * Stacktraces ausgebaut.
 *
 * Revision 1.39  2003/09/09 12:28:15  maj0r
 * Wizard fertiggestellt.
 *
 * Revision 1.38  2003/09/07 09:29:55  maj0r
 * Position des Hauptfensters und Breite der Tabellenspalten werden gespeichert.
 *
 * Revision 1.37  2003/09/06 14:49:59  maj0r
 * Verwendung des Separators korrigiert.
 *
 * Revision 1.36  2003/09/05 09:47:35  maj0r
 * Einige Logger eingebaut.
 *
 * Revision 1.35  2003/09/04 10:13:28  maj0r
 * Logger eingebaut.
 *
 * Revision 1.34  2003/09/01 15:50:51  maj0r
 * Wo es moeglich war, DOs auf primitive Datentypen umgebaut.
 *
 * Revision 1.33  2003/08/29 11:32:49  maj0r
 * Link in Ablage kopieren eingefuegt.
 *
 * Revision 1.32  2003/08/28 06:55:06  maj0r
 * NullPointer behoben.
 *
 * Revision 1.31  2003/08/28 06:11:02  maj0r
 * DragNDrop vervollstaendigt.
 *
 * Revision 1.30  2003/08/27 16:44:42  maj0r
 * Unterstuetzung fuer DragNDrop teilweise eingebaut.
 *
 * Revision 1.29  2003/08/27 11:19:30  maj0r
 * Prioritaet setzen und aufheben vollstaendig implementiert.
 * Button fuer 'Share erneuern' eingefuehrt.
 *
 * Revision 1.28  2003/08/26 19:46:34  maj0r
 * Sharebereich weiter vervollstaendigt.
 *
 * Revision 1.27  2003/08/26 14:04:23  maj0r
 * ShareTree-Event-Behandlung fertiggestellt.
 *
 * Revision 1.26  2003/08/26 09:49:01  maj0r
 * ShareTree weitgehend fertiggestellt.
 *
 * Revision 1.25  2003/08/26 06:20:10  maj0r
 * Anpassungen an muhs neuen Tree.
 *
 * Revision 1.24  2003/08/25 19:28:52  maj0r
 * Anpassungen an muhs neuen Tree.
 *
 * Revision 1.23  2003/08/25 07:23:25  maj0r
 * Kleine Korrekturen.
 *
 * Revision 1.22  2003/08/24 14:59:59  maj0r
 * Version 0.14
 * Diverse Aenderungen.
 *
 * Revision 1.21  2003/08/22 11:34:43  maj0r
 * WarteNode eingefuegt.
 *
 * Revision 1.20  2003/08/20 07:49:50  maj0r
 * Programmstart beschleunigt.
 *
 * Revision 1.19  2003/08/15 14:46:30  maj0r
 * Refactoring.
 *
 * Revision 1.18  2003/08/14 20:08:42  maj0r
 * Tree fuer Shareauswahl eingefuegt, aber noch nicht fertiggestellt.
 *
 * Revision 1.17  2003/08/04 14:28:55  maj0r
 * An neue Schnittstelle angepasst.
 *
 * Revision 1.16  2003/08/03 19:54:05  maj0r
 * An neue Schnittstelle angepasst.
 *
 * Revision 1.15  2003/08/02 12:03:38  maj0r
 * An neue Schnittstelle angepasst.
 *
 * Revision 1.14  2003/07/04 11:32:18  maj0r
 * Anzeige der Anzahl der Dateien und Gesamtgroessee des Shares hinzugefuegt.
 *
 * Revision 1.13  2003/07/02 13:54:34  maj0r
 * JTreeTable komplett ueberarbeitet.
 *
 * Revision 1.12  2003/07/01 18:41:39  maj0r
 * Struktur veraendert.
 *
 * Revision 1.11  2003/07/01 18:33:53  maj0r
 * Sprachauswahl eingearbeitet.
 *
 * Revision 1.10  2003/06/22 19:01:55  maj0r
 * Laden des Shares nun erst nach Betaetigen des Buttons "Erneut laden".
 *
 * Revision 1.9  2003/06/10 12:31:03  maj0r
 * Historie eingefuegt.
 *
 *
 */

public class SharePanel
    extends JPanel
    implements LanguageListener, RegisterI {

    private static SharePanel instance;

    private JPanel panelCenter;
    private DirectoryTree folderTree = new DirectoryTree();
    private TitledBorder titledBorder1;
    private TitledBorder titledBorder2;
    private JLabel dateien = new JLabel();
    private MouseAdapter treeMouseAdapter;

    private JButton neueListe = new JButton();
    private JButton neuLaden = new JButton();
    private JButton refresh = new JButton();
    private JButton prioritaetSetzen = new JButton();
    private JButton prioritaetAufheben = new JButton();
    private JComboBox cmbPrio = new JComboBox();
    private AJSettings ajSettings;

    private ShareTable shareTable;
    private ShareModel shareModel;

    private String eintraege;

    private JPopupMenu popup = new JPopupMenu();

    private JMenuItem item1;
    private JMenuItem item2;
    private JMenuItem item3;

    private JPopupMenu popup2 = new JPopupMenu();
    private JMenuItem itemCopyToClipboard = new JMenuItem();
    private JMenuItem itemCopyToClipboardWithSources = new JMenuItem();
    private JMenuItem itemCopyToClipboardAsUBBCode = new JMenuItem();

    private int anzahlDateien = 0;
    private String dateiGroesse = "0 MB";
    private boolean treeInitialisiert = false;
    private boolean initialized = false;

    private Logger logger;

    public static synchronized SharePanel getInstance() {
        if (instance == null) {
            instance = new SharePanel();
        }
        return instance;
    }

    private SharePanel() {
        logger = Logger.getLogger(getClass());
        try {
            init();
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", e);
            }
        }
    }

    private void init() throws Exception {
        IconManager im = IconManager.getInstance();
        itemCopyToClipboard.setIcon(im.getIcon("clipboard"));
        itemCopyToClipboardAsUBBCode.setIcon(im.getIcon("clipboard"));
        itemCopyToClipboardWithSources.setIcon(im.getIcon("clipboard"));

        popup2.add(itemCopyToClipboard);
        popup2.add(itemCopyToClipboardWithSources);
        popup2.add(itemCopyToClipboardAsUBBCode);
        folderTree.setModel(new DefaultTreeModel(new WaitNode()));
        folderTree.setCellRenderer(new ShareSelectionTreeCellRenderer());
        itemCopyToClipboard.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                Object[] obj = shareTable.getSelectedItems();
                if ( ( (ShareNode) obj[0]).isLeaf()) {
                    ShareDO shareDO = ( (ShareNode) obj[0]).getDO();
                    Clipboard cb = Toolkit.getDefaultToolkit().
                        getSystemClipboard();
                    StringBuffer toCopy = new StringBuffer();
                    toCopy.append("ajfsp://file|");
                    toCopy.append(shareDO.getShortfilename() + "|" +
                                  shareDO.getCheckSum() + "|" + shareDO.getSize() +
                                  "/");
                    StringSelection contents = new StringSelection(toCopy.
                        toString());
                    cb.setContents(contents, null);
                }
            }
        });
        itemCopyToClipboardWithSources.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                Object[] obj = shareTable.getSelectedItems();
                if ( ( (ShareNode) obj[0]).isLeaf()) {
                    ShareDO shareDO = ( (ShareNode) obj[0]).getDO();
                    Clipboard cb = Toolkit.getDefaultToolkit().
                        getSystemClipboard();
                    StringBuffer toCopy = new StringBuffer();
                    toCopy.append("ajfsp://file|");
                    toCopy.append(shareDO.getShortfilename());
                    toCopy.append("|");
                    toCopy.append(shareDO.getCheckSum());
                    toCopy.append("|");
                    toCopy.append(shareDO.getSize());
                    long port = ApplejuiceFassade.getInstance().
                        getAJSettings().getPort();
                    Information information = ApplejuiceFassade.getInstance().
                        getInformation();
                    toCopy.append("|");
                    toCopy.append(information.getExterneIP());
                    toCopy.append(":");
                    toCopy.append(port);
                    if (information.getVerbindungsStatus() ==
                        Information.VERBUNDEN) {
                        ServerDO serverDO = information.getServerDO();
                        if (serverDO != null) {
                            toCopy.append(":");
                            toCopy.append(serverDO.getHost());
                            toCopy.append(":");
                            toCopy.append(serverDO.getPort());
                        }
                    }
                    toCopy.append("/");
                    StringSelection contents = new StringSelection(toCopy.
                        toString());
                    cb.setContents(contents, null);
                }
            }
        });
        itemCopyToClipboardAsUBBCode.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                Object[] obj = shareTable.getSelectedItems();
                if ( ( (ShareNode) obj[0]).isLeaf()) {
                    ShareDO shareDO = ( (ShareNode) obj[0]).getDO();
                    Clipboard cb = Toolkit.getDefaultToolkit().
                        getSystemClipboard();
                    StringBuffer toCopy = new StringBuffer();
                    StringBuffer tempFilename = new StringBuffer(shareDO.
                        getShortfilename());
                    for (int i = 0; i < tempFilename.length(); i++) {
                        if (tempFilename.charAt(i) == ' ') {
                            tempFilename.setCharAt(i, '.');
                        }
                    }
                    String encodedFilename = "";
                    try {
                        encodedFilename = URLEncoder.encode(tempFilename.
                            toString(), "ISO-8859-1");
                    }
                    catch (UnsupportedEncodingException ex) {
                        //gibbet, also nix zu behandeln...
                    }
                    toCopy.append("[URL=ajfsp://file|");
                    toCopy.append(encodedFilename + "|" + shareDO.getCheckSum() +
                                  "|" + shareDO.getSize());
                    toCopy.append("/]" + shareDO.getShortfilename() + "[/URL]");
                    StringSelection contents = new StringSelection(toCopy.
                        toString());
                    cb.setContents(contents, null);
                }
            }
        });

        neueListe.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                DateiListeDialog dateiListeDialog = new DateiListeDialog(
                    AppleJuiceDialog.getApp(), false);
                shareTable.setDragEnabled(true);
                dateiListeDialog.show();
            }
        });

        cmbPrio.setEditable(false);
        for (int i = 1; i < 251; i++) {
            cmbPrio.addItem(new Integer(i));
        }
        prioritaetAufheben.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                prioritaetAufheben.setEnabled(false);
                prioritaetSetzen.setEnabled(false);
                neuLaden.setEnabled(false);
                final SwingWorker worker = new SwingWorker() {
                    public Object construct() {
                        try {
                            Object[] values = shareTable.getSelectedItems();
                            if (values == null) {
                                return null;
                            }
                            ShareNode shareNode = null;
                            for (int i = 0; i < values.length; i++) {
                                shareNode = (ShareNode) values[i];
                                shareNode.setPriority(1);
                            }
                        }
                        catch (Exception e) {
                            if (logger.isEnabledFor(Level.ERROR)) {
                                logger.error("Unbehandelte Exception", e);
                            }
                        }
                        return null;
                    }

                    public void finished() {
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                shareTable.updateUI();
                                prioritaetAufheben.setEnabled(true);
                                prioritaetSetzen.setEnabled(true);
                                neuLaden.setEnabled(true);
                            }
                        });
                    }
                };
                worker.start();
            }
        });
        prioritaetSetzen.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                prioritaetAufheben.setEnabled(false);
                prioritaetSetzen.setEnabled(false);
                neuLaden.setEnabled(false);
                new Thread() {
                    public void run() {
                        try {
                            int prio = ( (Integer) cmbPrio.getSelectedItem()).
                                intValue();
                            Object[] values = shareTable.getSelectedItems();
                            synchronized (values) {
                                if (values == null) {
                                    return;
                                }
                                ShareNode shareNode = null;
                                for (int i = 0; i < values.length; i++) {
                                    shareNode = (ShareNode) values[i];
                                    shareNode.setPriority(prio);
                                }
                            }
                            shareNeuLaden(false);
                        }
                        catch (Exception e) {
                            if (logger.isEnabledFor(Level.ERROR)) {
                                logger.error("Unbehandelte Exception", e);
                            }
                        }
                    }
                }

                .start();
            }
        });

        item1 = new JMenuItem();
        item1.setIcon(im.getIcon("sharedwsub"));
        item2 = new JMenuItem();
        item2.setIcon(im.getIcon("sharedwosub"));
        item3 = new JMenuItem();
        item3.setIcon(im.getIcon("notshared"));
        popup.add(item1);
        popup.add(item2);
        popup.add(item3);

        shareModel = new ShareModel(new ShareNode(null, null));
        shareTable = new ShareTable(shareModel);
        shareTable.setSelectionMode(ListSelectionModel.
                                    MULTIPLE_INTERVAL_SELECTION);

        shareTable.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent me) {
                if (SwingUtilities.isRightMouseButton(me)) {
                    Point p = me.getPoint();
                    int iRow = shareTable.rowAtPoint(p);
                    int iCol = shareTable.columnAtPoint(p);
                    if (iRow == -1 || iCol == -1) {
                        return;
                    }
                    shareTable.setRowSelectionInterval(iRow, iRow);
                    shareTable.setColumnSelectionInterval(iCol, iCol);
                }
                maybeShowPopup(me);
            }

            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                maybeShowPopup(e);
            }

            private void maybeShowPopup(MouseEvent e) {
                if (e.isPopupTrigger() && shareTable.getSelectedRowCount() == 1) {
                    Object[] obj = shareTable.getSelectedItems();
                    if ( ( (ShareNode) obj[0]).isLeaf()) {
                        popup2.show(shareTable, e.getX(), e.getY());
                    }
                }
            }
        });

        titledBorder1 = new TitledBorder("Test");
        titledBorder2 = new TitledBorder("Tester");
        setLayout(new BorderLayout());
        panelCenter = new JPanel(new BorderLayout());
        panelCenter.setBorder(titledBorder2);

        neueListe.setIcon(IconManager.getInstance().getIcon("treeRoot"));
        neuLaden.setIcon(IconManager.getInstance().getIcon("erneuern"));
        refresh.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                refresh.setEnabled(false);
                final SwingWorker worker = new SwingWorker() {
                    public Object construct() {
                        try {
                            HashSet shares = ajSettings.getShareDirs();
                            ApplejuiceFassade.getInstance().setShare(shares);
                        }
                        catch (Exception e) {
                            if (logger.isEnabledFor(Level.ERROR)) {
                                logger.error("Unbehandelte Exception", e);
                            }
                        }
                        return null;
                    }

                    public void finished() {
                        refresh.setEnabled(true);
                    }
                };
                worker.start();
            }
        });

        neuLaden.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                shareNeuLaden(true);
            }
        });

        JPanel panel1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel1.add(neueListe);
        panel1.add(neuLaden);
        panel1.add(cmbPrio);
        panel1.add(prioritaetSetzen);
        panel1.add(prioritaetAufheben);

        panelCenter.add(panel1, BorderLayout.NORTH);
        JScrollPane scrollPane = new JScrollPane(shareTable);
        scrollPane.setBackground(shareTable.getBackground());
        scrollPane.getViewport().setOpaque(false);

        panelCenter.add(scrollPane, BorderLayout.CENTER);
        panelCenter.add(dateien, BorderLayout.SOUTH);

        JScrollPane aScrollPane = new JScrollPane(folderTree);
        aScrollPane.setBorder(titledBorder1);
        JPanel panelWest = new JPanel(new BorderLayout());
        panelWest.add(aScrollPane, BorderLayout.CENTER);
        panelWest.add(refresh, BorderLayout.SOUTH);
        JSplitPane splitPane = new JSplitPane();
        splitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setLeftComponent(panelWest);
        splitPane.setRightComponent(panelCenter);
        splitPane.setBorder(null);
        add(splitPane, BorderLayout.CENTER);

        treeMouseAdapter = new TreeMouseAdapter();

        LanguageSelector.getInstance().addLanguageListener(this);

        item1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                HashSet shares = ajSettings.getShareDirs();
                DirectoryNode node = (DirectoryNode) folderTree.
                    getLastSelectedPathComponent();
                if (node != null) {
                    String path = node.getDO().getPath();
                    ShareEntry entry = new ShareEntry(path,
                        ShareEntry.SUBDIRECTORY);
                    shares.add(entry);
                    ApplejuiceFassade.getInstance().setShare(shares);
                    DirectoryNode.setShareDirs(shares);
                    folderTree.updateUI();
                }
            }
        });

        item2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                HashSet shares = ajSettings.getShareDirs();
                DirectoryNode node = (DirectoryNode) folderTree.
                    getLastSelectedPathComponent();
                if (node != null) {
                    String path = node.getDO().getPath();
                    ShareEntry entry = new ShareEntry(path,
                        ShareEntry.SINGLEDIRECTORY);
                    shares.add(entry);
                    ApplejuiceFassade.getInstance().setShare(shares);
                    DirectoryNode.setShareDirs(shares);
                    folderTree.updateUI();
                }
            }
        });

        item3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                HashSet shares = ajSettings.getShareDirs();
                DirectoryNode node = (DirectoryNode) folderTree.
                    getLastSelectedPathComponent();
                if (node != null) {
                    String path = node.getDO().getPath();
                    Iterator it = shares.iterator();
                    ShareEntry toRemove = null;
                    while (it.hasNext()) {
                        toRemove = (ShareEntry) it.next();
                        if (toRemove.getDir().compareToIgnoreCase(path) == 0) {
                            break;
                        }
                        toRemove = null;
                    }
                    if (toRemove != null) {
                        shares.remove(toRemove);
                        ApplejuiceFassade.getInstance().setShare(shares);
                        DirectoryNode.setShareDirs(shares);
                        folderTree.updateUI();
                    }
                }
            }
        });
    }

    private void shareNeuLaden(final boolean komplettNeu) {
        prioritaetAufheben.setEnabled(false);
        prioritaetSetzen.setEnabled(false);
        neuLaden.setEnabled(false);
        final SwingWorker worker = new SwingWorker() {
            public Object construct() {
                try {
                    ShareNode rootNode = shareModel.getRootNode();
                    if (komplettNeu) {
                        rootNode.removeAllChildren();
                    }
                    HashMap shares = ApplejuiceFassade.getInstance().getShare(true);
                    Iterator iterator = shares.values().iterator();
                    int anzahlDateien = 0;
                    double size = 0;
                    int anzahlArray;
                    ShareDO shareDO;
                    String filename;
                    String path;
                    ShareNode superParentNode;
                    ShareNode parentNode;
                    while (iterator.hasNext()) {
                        shareDO = (ShareDO) iterator.next();
                        rootNode.addChild(shareDO);
                        size += shareDO.getSize();
                        anzahlDateien++;
                    }
                    size = size / 1048576;
                    dateiGroesse = Double.toString(size);
                    if (dateiGroesse.indexOf(".") + 3 < dateiGroesse.length()) {
                        dateiGroesse = dateiGroesse.substring(0,
                            dateiGroesse.indexOf(".") + 3) + " MB";
                    }
                    String temp = eintraege;
                    temp = temp.replaceFirst("%i",
                                             Integer.toString(anzahlDateien));
                    temp = temp.replaceFirst("%s", dateiGroesse);
                    dateien.setText(temp);
                }
                catch (Exception e) {
                    if (logger.isEnabledFor(Level.ERROR)) {
                        logger.error("Unbehandelte Exception", e);
                    }
                }
                return null;
            }

            public void finished() {
                shareTable.updateUI();
                prioritaetAufheben.setEnabled(true);
                prioritaetSetzen.setEnabled(true);
                neuLaden.setEnabled(true);
            }
        };
        worker.start();
    }

    private void initShareSelectionTree() {
        folderTree.removeMouseListener(treeMouseAdapter);
        final SwingWorker worker2 = new SwingWorker() {
            public Object construct() {
                ShareSelectionTreeModel treeModel = new ShareSelectionTreeModel();
                folderTree.setModel(treeModel);
                folderTree.setRootVisible(false);
                folderTree.addMouseListener(treeMouseAdapter);
                return null;
            }
        };
        worker2.start();
    }

    public void registerSelected() {
        try {
            if (!initialized) {
                initialized = true;
                shareTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                TableColumnModel headerModel = shareTable.getTableHeader().
                    getColumnModel();
                int columnCount = headerModel.getColumnCount();
                PositionManager pm = PropertiesManager.getPositionManager();
                if (pm.isLegal()) {
                    int[] widths = pm.getShareWidths();
                    for (int i = 0; i < columnCount; i++) {
                        headerModel.getColumn(i).setPreferredWidth(widths[i]);
                    }
                }
                else {
                    for (int i = 0; i < columnCount; i++) {
                        headerModel.getColumn(i).setPreferredWidth(shareTable.
                            getWidth() / columnCount);
                    }
                }
            }
            if (!treeInitialisiert) {
                treeInitialisiert = true;
                new Thread() {
                    public void run() {
                        ajSettings = ApplejuiceFassade.getInstance().
                            getAJSettings();
                        DirectoryNode.setShareDirs(ajSettings.getShareDirs());
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                initShareSelectionTree();
                            }
                        });
                    }
                }

                .start();
            }
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", e);
            }
        }
    }

    public void fireLanguageChanged() {
        try {
            LanguageSelector languageSelector = LanguageSelector.getInstance();
            titledBorder1.setTitle(ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.
                getFirstAttrbuteByTagName(new String[] {"mainform", "dirssheet",
                                          "caption"})));
            titledBorder2.setTitle(ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.
                getFirstAttrbuteByTagName(new String[] {"mainform",
                                          "filessheet",
                                          "caption"})));
            item1.setText(ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.getFirstAttrbuteByTagName(new String[] {
                "mainform",
                "addwsubdirsbtn", "caption"})));
            item2.setText(ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.getFirstAttrbuteByTagName(new String[] {
                "mainform",
                "addosubdirsbtn", "caption"})));
            item3.setText(ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.getFirstAttrbuteByTagName(new String[] {
                "mainform",
                "deldirbtn", "caption"})));
            itemCopyToClipboard.setText(ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.getFirstAttrbuteByTagName(new String[] {
                "mainform",
                "getlink1", "caption"})));
            itemCopyToClipboardAsUBBCode.setText(ZeichenErsetzer.
                                                 korrigiereUmlaute(
                languageSelector.getFirstAttrbuteByTagName(new String[] {
                "javagui",
                "shareform", "linkalsubbcode"})));
            itemCopyToClipboardWithSources.setText(ZeichenErsetzer.
                korrigiereUmlaute(
                languageSelector.getFirstAttrbuteByTagName(new String[] {
                "javagui",
                "downloadform", "getlinkwithsources"})));
            refresh.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(new String[] {"mainform",
                                          "startsharecheck",
                                          "caption"})));
            refresh.setToolTipText(ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.
                getFirstAttrbuteByTagName(new String[] {"mainform",
                                          "startsharecheck",
                                          "hint"})));
            neueListe.setText(ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.
                getFirstAttrbuteByTagName(new String[] {"mainform",
                                          "newfilelist",
                                          "caption"})));
            neueListe.setToolTipText(ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.
                getFirstAttrbuteByTagName(new String[] {"mainform",
                                          "newfilelist",
                                          "hint"})));
            neuLaden.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(new String[] {"mainform",
                                          "sharereload",
                                          "caption"})));
            neuLaden.setToolTipText(ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.
                getFirstAttrbuteByTagName(new String[] {"mainform",
                                          "sharereload",
                                          "hint"})));
            prioritaetSetzen.setText(ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.
                getFirstAttrbuteByTagName(new String[] {"mainform", "setprio",
                                          "caption"})));
            prioritaetSetzen.setToolTipText(ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.getFirstAttrbuteByTagName(new String[] {
                "mainform",
                "setprio", "hint"})));
            prioritaetAufheben.setText(ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.getFirstAttrbuteByTagName(new String[] {
                "mainform",
                "clearprio", "caption"})));
            prioritaetAufheben.setToolTipText(ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.getFirstAttrbuteByTagName(new String[] {
                "mainform",
                "clearprio", "hint"})));

            String[] tableColumns = new String[3];
            tableColumns[0] = ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.
                getFirstAttrbuteByTagName(new String[] {"mainform", "sfiles",
                                          "col0caption"}));
            tableColumns[1] = ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.
                getFirstAttrbuteByTagName(new String[] {"mainform", "sfiles",
                                          "col1caption"}));
            tableColumns[2] = ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.
                getFirstAttrbuteByTagName(new String[] {"mainform", "sfiles",
                                          "col2caption"}));

            TableColumnModel tcm = shareTable.getColumnModel();
            for (int i = 0; i < 3; i++) {
                tcm.getColumn(i).setHeaderValue(tableColumns[i]);
            }

            eintraege = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(new String[] {"javagui", "shareform",
                                          "anzahlShare"}));
            if (anzahlDateien > 0) {
                String temp = eintraege;
                temp = temp.replaceFirst("%i", Integer.toString(anzahlDateien));
                temp = temp.replaceFirst("%s", dateiGroesse);
                dateien.setText(temp);
            }
            else {
                dateien.setText("");
            }
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", e);
            }
        }
    }

    class TreeMouseAdapter
        extends MouseAdapter {
        public void mousePressed(MouseEvent me) {
            try {
                if (SwingUtilities.isRightMouseButton(me)) {
                    Point p = me.getPoint();
                    int iRow = folderTree.getRowForLocation(p.x, p.y);
                    folderTree.setSelectionRow(iRow);
                }
                maybeShowPopup(me);
            }
            catch (Exception ex) {
                if (logger.isEnabledFor(Level.ERROR)) {
                    logger.error("Unbehandelte Exception", ex);
                }
            }
        }

        public void mouseReleased(MouseEvent e) {
            try {
                super.mouseReleased(e);
                maybeShowPopup(e);
            }
            catch (Exception ex) {
                if (logger.isEnabledFor(Level.ERROR)) {
                    logger.error("Unbehandelte Exception", ex);
                }
            }
        }

        private void maybeShowPopup(MouseEvent e) {
            try {
                if (e.isPopupTrigger()) {
                    DirectoryNode node = (DirectoryNode) folderTree.
                        getLastSelectedPathComponent();
                    if (node == null) {
                        return;
                    }
                    popup.removeAll();
                    int nodeShareMode = node.getShareMode();
                    if (nodeShareMode == DirectoryNode.NOT_SHARED
                        || nodeShareMode == DirectoryNode.SHARED_SOMETHING
                        || nodeShareMode == DirectoryNode.SHARED_SUB) {
                        popup.add(item1);
                        popup.add(item2);
                        popup.show(folderTree, e.getX(), e.getY());
                    }
                    else if (nodeShareMode == DirectoryNode.SHARED_WITH_SUB
                             ||
                             nodeShareMode == DirectoryNode.SHARED_WITHOUT_SUB) {
                        popup.add(item3);
                        popup.show(folderTree, e.getX(), e.getY());
                    }
                }
            }
            catch (Exception ex) {
                if (logger.isEnabledFor(Level.ERROR)) {
                    logger.error("Unbehandelte Exception", ex);
                }
            }
        }
    }

    public int[] getColumnWidths() {
        TableColumnModel tcm = shareTable.getColumnModel();
        int[] widths = new int[tcm.getColumnCount()];
        for (int i = 0; i < tcm.getColumnCount(); i++) {
            widths[i] = tcm.getColumn(i).getWidth();
        }
        return widths;
    }

    public void lostSelection() {
    }
}
