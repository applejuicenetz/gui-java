package de.applejuicenet.client.gui;

import java.util.*;

import java.awt.*;
import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.border.*;
import javax.swing.table.*;

import de.applejuicenet.client.gui.controller.*;
import de.applejuicenet.client.gui.listener.*;
import de.applejuicenet.client.shared.*;
import de.applejuicenet.client.shared.exception.NodeAlreadyExistsException;
import de.applejuicenet.client.shared.dac.ShareDO;
import de.applejuicenet.client.gui.tables.share.ShareModel;
import de.applejuicenet.client.gui.tables.share.ShareNode;
import de.applejuicenet.client.gui.tables.share.ShareTable;
import de.applejuicenet.client.gui.trees.share.ShareSelectionTreeModel;
import de.applejuicenet.client.gui.trees.share.ShareSelectionTreeCellRenderer;
import de.applejuicenet.client.gui.trees.share.DirectoryNode;
import de.applejuicenet.client.gui.trees.share.DirectoryTree;
import de.applejuicenet.client.gui.trees.WaitNode;

import java.awt.event.*;
import java.io.File;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/Attic/SharePanel.java,v 1.31 2003/08/28 06:11:02 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: SharePanel.java,v $
 * Revision 1.31  2003/08/28 06:11:02  maj0r
 * DragNDrop vervollstaendigt.
 *
 * Revision 1.30  2003/08/27 16:44:42  maj0r
 * Unterstuetzung fuer DragNDrop teilweise eingebaut.
 *
 * Revision 1.29  2003/08/27 11:19:30  maj0r
 * Prioritaet setzen und aufheben vollstaendig implementiert.
 * Button für 'Share erneuern' eingefuehrt.
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
 * Anzeige der Anzahl der Dateien und Gesamtgöße des Shares hinzugefügt.
 *
 * Revision 1.13  2003/07/02 13:54:34  maj0r
 * JTreeTable komplett überarbeitet.
 *
 * Revision 1.12  2003/07/01 18:41:39  maj0r
 * Struktur verändert.
 *
 * Revision 1.11  2003/07/01 18:33:53  maj0r
 * Sprachauswahl eingearbeitet.
 *
 * Revision 1.10  2003/06/22 19:01:55  maj0r
 * Laden des Shares nun erst nach Betätigen des Buttons "Erneut laden".
 *
 * Revision 1.9  2003/06/10 12:31:03  maj0r
 * Historie eingefügt.
 *
 *
 */

public class SharePanel
        extends JPanel
        implements LanguageListener, RegisterI {

    private AppleJuiceDialog parent;

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

    private int anzahlDateien = 0;
    private String dateiGroesse = "0 MB";
    private boolean treeInitialisiert = false;

    public SharePanel(AppleJuiceDialog parent) {
        this.parent = parent;
        try
        {
            jbInit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void jbInit() throws Exception {
        //todo
//        neueListe.setEnabled(false);

        neueListe.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                DateiListeDialog dateiListeDialog = new DateiListeDialog(parent, false);
                shareTable.setDragEnabled(true);
                dateiListeDialog.show();
            }
        });

        cmbPrio.setEditable(false);
        for (int i = 1; i < 251; i++)
        {
            cmbPrio.addItem(new Integer(i));
        }
        prioritaetAufheben.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                prioritaetAufheben.setEnabled(false);
                prioritaetSetzen.setEnabled(false);
                neuLaden.setEnabled(false);
                final SwingWorker worker = new SwingWorker() {
                    public Object construct() {
                        Object[] values = shareTable.getSelectedItems();
                        if (values == null)
                            return null;
                        ShareNode shareNode = null;
                        for (int i = 0; i < values.length; i++)
                        {
                            shareNode = (ShareNode) values[i];
                            shareNode.setPriority(1);
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
                final SwingWorker worker = new SwingWorker() {
                    public Object construct() {
                        int prio = ((Integer) cmbPrio.getSelectedItem()).intValue();
                        Object[] values = shareTable.getSelectedItems();
                        if (values == null)
                            return null;
                        ShareNode shareNode = null;
                        for (int i = 0; i < values.length; i++)
                        {
                            shareNode = (ShareNode) values[i];
                            shareNode.setPriority(prio);
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

        item1 = new JMenuItem();
        item2 = new JMenuItem();
        item3 = new JMenuItem();
        popup.add(item1);
        popup.add(item2);
        popup.add(item3);

        shareModel = new ShareModel(new ShareNode(null, "/"));
        shareTable = new ShareTable(shareModel);
        shareTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
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
                        HashSet shares = ajSettings.getShareDirs();
                        ApplejuiceFassade.getInstance().setShare(shares);
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
                prioritaetAufheben.setEnabled(false);
                prioritaetSetzen.setEnabled(false);
                neuLaden.setEnabled(false);
                final SwingWorker worker = new SwingWorker() {
                    public Object construct() {
                        ShareNode rootNode = shareModel.getRootNode();
                        rootNode.removeAllChildren();
                        ArrayList sharesArray = new ArrayList();
                        HashSet shareDirs = ajSettings.getShareDirs();
                        Iterator it = shareDirs.iterator();
                        ShareNode directoryNode = null;
                        String pfad;
                        try
                        {
                            pfad = ajSettings.getTempDir();
                            pfad = pfad.substring(0, pfad.length() - 1);
                            directoryNode = new ShareNode(rootNode, pfad);
                        }
                        catch (NodeAlreadyExistsException e)
                        {
                            //Schon da, also brauchts den auch nicht.
                        }
                        rootNode.addDirectory(directoryNode);
                        while (it.hasNext())
                        {
                            pfad = ((ShareEntry) it.next()).getDir();
                            sharesArray.add(pfad);
                            try
                            {
                                directoryNode = new ShareNode(rootNode, pfad);
                                rootNode.addDirectory(directoryNode);
                            }
                            catch (NodeAlreadyExistsException e)
                            {
                                //Schon da, also brauchts den auch nicht.
                            }
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
                        while (iterator.hasNext())
                        {
                            shareDO = (ShareDO) iterator.next();
                            filename = shareDO.getFilename();
                            path = filename.substring(0, filename.lastIndexOf(File.separator));
                            parentNode = ShareNode.getNodeByPath(path);
                            if (parentNode != null)
                            {
                                parentNode.addChild(shareDO);
                            }
                            else
                            {
                                anzahlArray = sharesArray.size();
                                for (int i = 0; i < anzahlArray; i++)
                                {
                                    if (path.indexOf((String) sharesArray.get(i)) != -1)
                                    {
                                        path = path.substring(((String) sharesArray.get(i)).length());
                                        superParentNode = ShareNode.getNodeByPath((String) sharesArray.get(i));
                                        parentNode = ShareNode.getNodeByPath(path);
                                        if (parentNode != null)
                                        {
                                            parentNode.addChild(shareDO);
                                        }
                                        else
                                        {
                                            try
                                            {
                                                ShareNode neuesDirectory = new ShareNode(rootNode, path);
                                                superParentNode.addDirectory(neuesDirectory);
                                                neuesDirectory.addChild(shareDO);
                                            }
                                            catch (NodeAlreadyExistsException e)
                                            {
                                                e.printStackTrace();  //To change body of catch statement use Options | File Templates.
                                            }
                                        }
                                        break;
                                    }
                                }
                            }
                            size += Long.parseLong(shareDO.getSize());
                            anzahlDateien++;
                        }
                        size = size / 1048576;
                        dateiGroesse = Double.toString(size);
                        if (dateiGroesse.indexOf(".") + 3 < dateiGroesse.length())
                        {
                            dateiGroesse = dateiGroesse.substring(0, dateiGroesse.indexOf(".") + 3) + " MB";
                        }
                        String temp = eintraege;
                        temp = temp.replaceFirst("%i", Integer.toString(anzahlDateien));
                        temp = temp.replaceFirst("%s", dateiGroesse);
                        dateien.setText(temp);
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
        });

        JPanel panel1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel1.add(neueListe);
        panel1.add(neuLaden);
        panel1.add(cmbPrio);
        panel1.add(prioritaetSetzen);
        panel1.add(prioritaetAufheben);

        panelCenter.add(panel1, BorderLayout.NORTH);
        panelCenter.add(new JScrollPane(shareTable), BorderLayout.CENTER);
        panelCenter.add(dateien, BorderLayout.SOUTH);

        JScrollPane aScrollPane = new JScrollPane(folderTree);
        aScrollPane.setBorder(titledBorder1);
        JPanel panelWest = new JPanel(new BorderLayout());
        panelWest.add(aScrollPane, BorderLayout.CENTER);
        panelWest.add(refresh, BorderLayout.SOUTH);
        add(panelWest, BorderLayout.WEST);
        add(panelCenter, BorderLayout.CENTER);

        treeMouseAdapter = new TreeMouseAdapter();

        LanguageSelector.getInstance().addLanguageListener(this);

        item1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                HashSet shares = ajSettings.getShareDirs();
                DirectoryNode node = (DirectoryNode) folderTree.getLastSelectedPathComponent();
                if (node != null)
                {
                    String path = node.getDO().getPath();
                    ShareEntry entry = new ShareEntry(path, ShareEntry.SUBDIRECTORY);
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
                DirectoryNode node = (DirectoryNode) folderTree.getLastSelectedPathComponent();
                if (node != null)
                {
                    String path = node.getDO().getPath();
                    ShareEntry entry = new ShareEntry(path, ShareEntry.SINGLEDIRECTORY);
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
                DirectoryNode node = (DirectoryNode) folderTree.getLastSelectedPathComponent();
                if (node != null)
                {
                    String path = node.getDO().getPath();
                    Iterator it = shares.iterator();
                    ShareEntry toRemove = null;
                    while (it.hasNext())
                    {
                        toRemove = (ShareEntry) it.next();
                        if (toRemove.getDir().compareToIgnoreCase(path) == 0)
                        {
                            break;
                        }
                        toRemove = null;
                    }
                    if (toRemove != null)
                    {
                        shares.remove(toRemove);
                        ApplejuiceFassade.getInstance().setShare(shares);
                        DirectoryNode.setShareDirs(shares);
                        folderTree.updateUI();
                    }
                }
            }
        });
    }

    private void initShareSelectionTree() {
        folderTree.removeMouseListener(treeMouseAdapter);
        folderTree.setModel(new DefaultTreeModel(new WaitNode()));
        folderTree.setCellRenderer(new ShareSelectionTreeCellRenderer());
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
        if (!treeInitialisiert)
        {
            ajSettings = ApplejuiceFassade.getInstance().getAJSettings();
            DirectoryNode.setShareDirs(ajSettings.getShareDirs());
            treeInitialisiert = true;
            initShareSelectionTree();
        }
    }

    public void fireLanguageChanged() {
        LanguageSelector languageSelector = LanguageSelector.getInstance();
        titledBorder1.setTitle(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                                 getFirstAttrbuteByTagName(new String[]{"mainform", "dirssheet",
                                                                                                        "caption"})));
        titledBorder2.setTitle(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                                 getFirstAttrbuteByTagName(new String[]{"mainform", "filessheet",
                                                                                                        "caption"})));
        item1.setText(ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.getFirstAttrbuteByTagName(new String[]{"mainform",
                                                                        "addwsubdirsbtn", "caption"})));
        item2.setText(ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.getFirstAttrbuteByTagName(new String[]{"mainform",
                                                                        "addosubdirsbtn", "caption"})));
        item3.setText(ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.getFirstAttrbuteByTagName(new String[]{"mainform",
                                                                        "deldirbtn", "caption"})));
        refresh.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                          getFirstAttrbuteByTagName(new String[]{"mainform", "startsharecheck",
                                                                                                 "caption"})));
        refresh.setToolTipText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                                 getFirstAttrbuteByTagName(new String[]{"mainform", "startsharecheck",
                                                                                                        "hint"})));
        neueListe.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                            getFirstAttrbuteByTagName(new String[]{"mainform", "newfilelist",
                                                                                                   "caption"})));
        neueListe.setToolTipText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                                   getFirstAttrbuteByTagName(new String[]{"mainform", "newfilelist",
                                                                                                          "hint"})));
        neuLaden.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                           getFirstAttrbuteByTagName(new String[]{"mainform", "sharereload",
                                                                                                  "caption"})));
        neuLaden.setToolTipText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                                  getFirstAttrbuteByTagName(new String[]{"mainform", "sharereload",
                                                                                                         "hint"})));
        prioritaetSetzen.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                                   getFirstAttrbuteByTagName(new String[]{"mainform", "setprio",
                                                                                                          "caption"})));
        prioritaetSetzen.setToolTipText(ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.getFirstAttrbuteByTagName(new String[]{"mainform",
                                                                        "setprio", "hint"})));
        prioritaetAufheben.setText(ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.getFirstAttrbuteByTagName(new String[]{"mainform",
                                                                        "clearprio", "caption"})));
        prioritaetAufheben.setToolTipText(ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.getFirstAttrbuteByTagName(new String[]{"mainform",
                                                                        "clearprio", "hint"})));

        String[] tableColumns = new String[3];
        tableColumns[0] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                            getFirstAttrbuteByTagName(new String[]{"mainform", "sfiles",
                                                                                                   "col0caption"}));
        tableColumns[1] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                            getFirstAttrbuteByTagName(new String[]{"mainform", "sfiles",
                                                                                                   "col1caption"}));
        tableColumns[2] = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                            getFirstAttrbuteByTagName(new String[]{"mainform", "sfiles",
                                                                                                   "col2caption"}));

        TableColumnModel tcm = shareTable.getColumnModel();
        for (int i = 0; i < 3; i++)
        {
            tcm.getColumn(i).setHeaderValue(tableColumns[i]);
        }

        eintraege = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                      getFirstAttrbuteByTagName(new String[]{"javagui", "shareform", "anzahlShare"}));
        if (anzahlDateien > 0)
        {
            String temp = eintraege;
            temp = temp.replaceFirst("%i", Integer.toString(anzahlDateien));
            temp = temp.replaceFirst("%s", dateiGroesse);
            dateien.setText(temp);
        }
        else
        {
            dateien.setText("");
        }
    }

    class TreeMouseAdapter extends MouseAdapter {
        public void mousePressed(MouseEvent me) {
            if (SwingUtilities.isRightMouseButton(me))
            {
                Point p = me.getPoint();
                int iRow = folderTree.getRowForLocation(p.x, p.y);
                folderTree.setSelectionRow(iRow);
            }
            maybeShowPopup(me);
        }

        public void mouseReleased(MouseEvent e) {
            super.mouseReleased(e);
            maybeShowPopup(e);
        }

        private void maybeShowPopup(MouseEvent e) {
            if (e.isPopupTrigger())
            {
                DirectoryNode node = (DirectoryNode) folderTree.getLastSelectedPathComponent();
                popup.removeAll();
                int nodeShareMode = node.getShareMode();
                if (nodeShareMode == DirectoryNode.NOT_SHARED
                        || nodeShareMode == DirectoryNode.SHARED_SOMETHING
                        || nodeShareMode == DirectoryNode.SHARED_SUB)
                {
                    popup.add(item1);
                    popup.add(item2);
                    popup.show(folderTree, e.getX(), e.getY());
                }
                else if (nodeShareMode == DirectoryNode.SHARED_WITH_SUB
                        || nodeShareMode == DirectoryNode.SHARED_WITHOUT_SUB)
                {
                    popup.add(item3);
                    popup.show(folderTree, e.getX(), e.getY());
                }
            }
        }
    }
}