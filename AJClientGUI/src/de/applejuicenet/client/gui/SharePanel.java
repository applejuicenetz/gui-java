package de.applejuicenet.client.gui;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

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
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.tree.DefaultTreeModel;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import de.applejuicenet.client.gui.controller.ApplejuiceFassade;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.controller.OptionsManagerImpl;
import de.applejuicenet.client.gui.controller.PositionManager;
import de.applejuicenet.client.gui.controller.PositionManagerImpl;
import de.applejuicenet.client.gui.listener.LanguageListener;
import de.applejuicenet.client.gui.tables.NormalHeaderRenderer;
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
import de.applejuicenet.client.shared.Information;
import de.applejuicenet.client.shared.ShareEntry;
import de.applejuicenet.client.shared.SwingWorker;
import de.applejuicenet.client.shared.ZeichenErsetzer;
import de.applejuicenet.client.shared.dac.ServerDO;
import de.applejuicenet.client.shared.dac.ShareDO;
import de.applejuicenet.client.gui.components.AJButton;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/Attic/SharePanel.java,v 1.70 2004/07/09 11:34:00 loevenwong Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r [maj0r@applejuicenet.de]
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

    private AJButton neueListe = new AJButton();
    private AJButton neuLaden = new AJButton();
    private AJButton refresh = new AJButton();
    private AJButton prioritaetSetzen = new AJButton();
    private AJButton prioritaetAufheben = new AJButton();
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
    private JMenuItem itemOpenWithProgram = new JMenuItem();

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
                logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
            }
        }
    }

    private void init() throws Exception {
        IconManager im = IconManager.getInstance();
        itemCopyToClipboard.setIcon(im.getIcon("clipboard"));
        itemCopyToClipboardAsUBBCode.setIcon(im.getIcon("clipboard"));
        itemCopyToClipboardWithSources.setIcon(im.getIcon("clipboard"));
        prioritaetAufheben.setEnabled(false);
        prioritaetSetzen.setEnabled(false);
        neuLaden.setEnabled(false);
        refresh.setEnabled(false);

        popup2.add(itemCopyToClipboard);
        popup2.add(itemCopyToClipboardWithSources);
        popup2.add(itemCopyToClipboardAsUBBCode);
    	popup2.add(itemOpenWithProgram);
        itemOpenWithProgram.setIcon(im.getIcon("vlc"));
        if (ApplejuiceFassade.getInstance().isLocalhost()){
            itemOpenWithProgram.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    Object[] obj = shareTable.getSelectedItems();
                    if ( ( (ShareNode) obj[0]).isLeaf()) {
                        ShareDO shareDO = ( (ShareNode) obj[0]).getDO();
                        String filename = shareDO.getFilename();
                        String programToExecute = OptionsManagerImpl.getInstance().getOpenProgram();
                        if (programToExecute.length() != 0){
	            			try {
	            				Runtime.getRuntime().exec(new String[] { programToExecute, filename });
	            			} catch (Exception ex) {
	            				//nix zu tun
	            			}
                        }
                    }
                }
            });
        }
        else{
        	itemOpenWithProgram.setEnabled(false);
        }
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
                        ;
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
                                logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
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
                            if (values != null){
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
                        }
                        catch (Exception e) {
                            if (logger.isEnabledFor(Level.ERROR)) {
                                logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
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
        TableColumnModel model = shareTable.getColumnModel();
        int n = model.getColumnCount();
        TableCellRenderer renderer = new NormalHeaderRenderer();
        for (int i = 0; i < n; i++) {
            model.getColumn(i).setHeaderRenderer(renderer);
        }

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
                            Set shares = ajSettings.getShareDirs();
                            ApplejuiceFassade.getInstance().setShare(shares);
                        }
                        catch (Exception e) {
                            if (logger.isEnabledFor(Level.ERROR)) {
                                logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
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
        panel1.add(neuLaden);
        panel1.add(neueListe);
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
                Set shares = ajSettings.getShareDirs();
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
                Set shares = ajSettings.getShareDirs();
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
                Set shares = ajSettings.getShareDirs();
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
                    Map shares = ApplejuiceFassade.getInstance().getShare(true);
                    Iterator iterator = shares.values().iterator();
                    int anzahlDateien = 0;
                    double size = 0;
                    ShareDO shareDO;
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
                        logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
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
                PositionManager pm = PositionManagerImpl.getInstance();
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
                        prioritaetAufheben.setEnabled(true);
                        prioritaetSetzen.setEnabled(true);
                        neuLaden.setEnabled(true);
                        refresh.setEnabled(true);
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
                logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
            }
        }
    }

    public void fireLanguageChanged() {
        try {
            LanguageSelector languageSelector = LanguageSelector.getInstance();
            titledBorder1.setTitle(ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.
                getFirstAttrbuteByTagName(".root.mainform.dirssheet.caption")));
            titledBorder2.setTitle(ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.
                getFirstAttrbuteByTagName(".root.mainform.filessheet.caption")));
            item1.setText(ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.getFirstAttrbuteByTagName(".root.mainform.addwsubdirsbtn.caption")));
            item2.setText(ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.getFirstAttrbuteByTagName(".root.mainform.addosubdirsbtn.caption")));
            item3.setText(ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.getFirstAttrbuteByTagName(".root.mainform.deldirbtn.caption")));
            itemCopyToClipboard.setText(ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.getFirstAttrbuteByTagName(".root.mainform.getlink1.caption")));
            itemCopyToClipboardAsUBBCode.setText(ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.getFirstAttrbuteByTagName(".root.javagui.shareform.linkalsubbcode")));
            itemCopyToClipboardWithSources.setText(ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.getFirstAttrbuteByTagName(".root.javagui.downloadform.getlinkwithsources")));
            itemOpenWithProgram.setText("VLC");
            refresh.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(".root.mainform.startsharecheck.caption")));
            refresh.setToolTipText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(".root.mainform.startsharecheck.hint")));
            neueListe.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(".root.mainform.newfilelist.caption")));
            neueListe.setToolTipText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(".root.mainform.newfilelist.hint")));
            neuLaden.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(".root.mainform.sharereload.caption")));
            neuLaden.setToolTipText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(".root.mainform.sharereload.hint")));
            prioritaetSetzen.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(".root.mainform.setprio.caption")));
            prioritaetSetzen.setToolTipText(ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.getFirstAttrbuteByTagName(".root.mainform.setprio.hint")));
            prioritaetAufheben.setText(ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.getFirstAttrbuteByTagName(".root.mainform.clearprio.caption")));
            prioritaetAufheben.setToolTipText(ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.getFirstAttrbuteByTagName(".root.mainform.clearprio.hint")));

            String[] tableColumns = new String[3];
            tableColumns[0] = ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.
                getFirstAttrbuteByTagName(".root.mainform.sfiles.col0caption"));
            tableColumns[1] = ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.
                getFirstAttrbuteByTagName(".root.mainform.sfiles.col1caption"));
            tableColumns[2] = ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.
                getFirstAttrbuteByTagName(".root.mainform.sfiles.col2caption"));

            TableColumnModel tcm = shareTable.getColumnModel();
            for (int i = 0; i < 3; i++) {
                tcm.getColumn(i).setHeaderValue(tableColumns[i]);
            }

            eintraege = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(".root.javagui.shareform.anzahlShare"));
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
                logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
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
                    logger.error(ApplejuiceFassade.ERROR_MESSAGE, ex);
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
                    logger.error(ApplejuiceFassade.ERROR_MESSAGE, ex);
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
                    logger.error(ApplejuiceFassade.ERROR_MESSAGE, ex);
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
