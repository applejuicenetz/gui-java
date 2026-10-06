/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.share;

import de.applejuicenet.client.fassade.ApplejuiceFassade;
import de.applejuicenet.client.fassade.shared.AJSettings;
import de.applejuicenet.client.gui.components.GuiController;
import de.applejuicenet.client.gui.components.TklPanel;
import de.applejuicenet.client.gui.components.table.NormalHeaderRenderer;
import de.applejuicenet.client.gui.components.tree.WaitNode;
import de.applejuicenet.client.gui.share.table.ShareNode;
import de.applejuicenet.client.gui.share.table.ShareTable;
import de.applejuicenet.client.gui.share.table.ShareTableModel;
import de.applejuicenet.client.gui.share.tree.DirectoryTree;
import de.applejuicenet.client.gui.share.tree.ShareSelectionTreeCellRenderer;
import de.applejuicenet.client.shared.IconManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/share/SharePanel.java,v 1.13 2009/02/12 09:11:24 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author Maj0r [aj@tkl-soft.de]
 */
public class SharePanel extends TklPanel {
    private JPanel panelCenter;
    private DirectoryTree folderTree = new DirectoryTree();
    private TitledBorder folderTreeBolder;
    private TitledBorder mailPanelBolder;
    private JLabel dateien = new JLabel();
    private JButton neueListe = new JButton();
    private JButton neuLaden = new JButton();
    private JButton refresh = new JButton();
    private JButton prioritaetSetzen = new JButton();
    private JButton prioritaetAufheben = new JButton();
    private JComboBox cmbPrio = new JComboBox();
    private AJSettings ajSettings;
    private ShareTable shareTable;
    private ShareTableModel shareModel;
    private JPopupMenu popup = new JPopupMenu();
    private JMenuItem sharedwsub;
    private JMenuItem sharedwosub;
    private JMenuItem notshared;
    private JPopupMenu popup2 = new JPopupMenu();
    private JPopupMenu folderPopup = new JPopupMenu();
    private JMenuItem itemCreateFileList = new JMenuItem();
    private JMenuItem itemReleaseInfo = new JMenuItem();
    private JMenuItem itemCopyToClipboard = new JMenuItem();
    private JMenuItem itemCopyToClipboardWithSources = new JMenuItem();
    private JMenuItem itemCopyToClipboardAsUBBCode = new JMenuItem();
    private JMenuItem itemOpenWithProgram = new JMenuItem();
    private JMenuItem itemOpenWithStandardProgramm = new JMenuItem();
    private Logger logger;

    public SharePanel(GuiController guiController) {
        super(guiController);
        logger = LoggerFactory.getLogger(getClass());
        try {
            init();
        } catch (Exception e) {
            logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
        }
    }

    public JMenuItem getMnuOpenWithStandardProgram() {
        return itemOpenWithStandardProgramm;
    }

    public JButton getBtnPrioritaetAufheben() {
        return prioritaetAufheben;
    }

    public JButton getBtnPrioritaetSetzen() {
        return prioritaetSetzen;
    }

    public JButton getBtnNeuLaden() {
        return neuLaden;
    }

    public JButton getBtnRefresh() {
        return refresh;
    }

    public JButton getBtnNeueListe() {
        return neueListe;
    }

    public JMenuItem getMnuNotShared() {
        return notshared;
    }

    public JMenuItem getMnuSharedWithoutSub() {
        return sharedwosub;
    }

    public JMenuItem getMnuSharedWithSub() {
        return sharedwsub;
    }

    public JMenuItem getMnuCopyToClipboard() {
        return itemCopyToClipboard;
    }

    public JMenuItem getMnuReleaseInfo() {
        return itemReleaseInfo;
    }

    public JMenuItem getMnuCopyToClipboardWithSources() {
        return itemCopyToClipboardWithSources;
    }

    public JMenuItem getMnuCopyToClipboardAsUBBCode() {
        return itemCopyToClipboardAsUBBCode;
    }

    public JMenuItem getMnuOpenWithProgram() {
        return itemOpenWithProgram;
    }

    public ShareTable getShareTable() {
        return shareTable;
    }

    public ShareTableModel getShareModel() {
        return shareModel;
    }

    public JLabel getLblDateien() {
        return dateien;
    }

    public JComboBox getCmbPrioritaet() {
        return cmbPrio;
    }

    public DirectoryTree getDirectoryTree() {
        return folderTree;
    }

    public JPopupMenu getPopupMenu() {
        return popup;
    }

    public JPopupMenu getPopupMenu2() {
        return popup2;
    }

    public JPopupMenu getFolderPopupMenu() {
        return folderPopup;
    }

    public JMenuItem getMnuCreateFileList() {
        return itemCreateFileList;
    }

    public TitledBorder getFolderTreeBolder() {
        return folderTreeBolder;
    }

    public TitledBorder getMainPanelBolder() {
        return mailPanelBolder;
    }

    private void init() throws Exception {
        IconManager im = IconManager.getInstance();

        itemReleaseInfo.setIcon(im.getIcon("hint"));
        itemCopyToClipboard.setIcon(im.getIcon("clipboard"));
        itemCopyToClipboardAsUBBCode.setIcon(im.getIcon("clipboard"));
        itemCopyToClipboardWithSources.setIcon(im.getIcon("clipboard"));
        itemCreateFileList.setIcon(im.getIcon("treeRoot"));
        folderPopup.add(itemCreateFileList);
        prioritaetAufheben.setEnabled(false);
        prioritaetSetzen.setEnabled(false);
        neuLaden.setEnabled(false);
        refresh.setEnabled(false);

        popup2.add(itemCopyToClipboard);
        popup2.add(itemCopyToClipboardWithSources);
        popup2.add(itemCopyToClipboardAsUBBCode);
        popup2.add(new JSeparator());
        popup2.add(itemReleaseInfo);
        popup2.add(itemOpenWithProgram);
        popup2.add(itemOpenWithStandardProgramm);
        itemOpenWithProgram.setIcon(im.getIcon("vlc"));
        folderTree.setModel(new DefaultTreeModel(new WaitNode()));
        folderTree.setCellRenderer(new ShareSelectionTreeCellRenderer());

        cmbPrio.setEditable(false);
        for (int i = 1; i < 251; i++) {
            cmbPrio.addItem(new Integer(i));
        }

        sharedwsub = new JMenuItem();
        sharedwsub.setIcon(im.getIcon("sharedwsub"));
        sharedwosub = new JMenuItem();
        sharedwosub.setIcon(im.getIcon("sharedwosub"));
        notshared = new JMenuItem();
        notshared.setIcon(im.getIcon("notshared"));
        popup.add(sharedwsub);
        popup.add(sharedwosub);
        popup.add(notshared);

        shareModel = new ShareTableModel(new ShareNode(null, null));
        shareTable = new ShareTable(shareModel);
        shareTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        TableColumnModel model = shareTable.getColumnModel();
        int n = model.getColumnCount();
        TableCellRenderer renderer = new NormalHeaderRenderer();

        for (int i = 0; i < n; i++) {
            model.getColumn(i).setHeaderRenderer(renderer);
        }

        folderTreeBolder = new TitledBorder("Test");
        mailPanelBolder = new TitledBorder("Tester");
        setLayout(new BorderLayout());
        panelCenter = new JPanel(new BorderLayout());
        panelCenter.setBorder(mailPanelBolder);

        neueListe.setIcon(IconManager.getInstance().getIcon("treeRoot"));
        neuLaden.setIcon(IconManager.getInstance().getIcon("erneuern"));

        JPanel panel1 = new JPanel(new GridBagLayout());
        GridBagConstraints toolbar = new GridBagConstraints();

        toolbar.fill = GridBagConstraints.VERTICAL;
        toolbar.insets = new Insets(2, 2, 2, 3);
        for (JComponent component : new JComponent[] {neuLaden, neueListe, cmbPrio, prioritaetSetzen,
                prioritaetAufheben}) {
            panel1.add(component, toolbar);
        }
        toolbar.weightx = 1;
        panel1.add(Box.createHorizontalGlue(), toolbar);

        panelCenter.add(panel1, BorderLayout.NORTH);
        JScrollPane scrollPane = new JScrollPane(shareTable);

        scrollPane.setBackground(shareTable.getBackground());
        scrollPane.getViewport().setOpaque(false);

        panelCenter.add(scrollPane, BorderLayout.CENTER);
        panelCenter.add(dateien, BorderLayout.SOUTH);

        JScrollPane aScrollPane = new JScrollPane(folderTree);

        aScrollPane.setBorder(folderTreeBolder);
        JPanel panelWest = new JPanel(new BorderLayout());

        panelWest.add(aScrollPane, BorderLayout.CENTER);
        panelWest.add(refresh, BorderLayout.SOUTH);
        JSplitPane splitPane = new JSplitPane();

        splitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setLeftComponent(panelWest);
        splitPane.setRightComponent(panelCenter);
        splitPane.setBorder(null);
        add(splitPane, BorderLayout.CENTER);
    }

    public int[] getColumnWidths() {
        TableColumnModel tcm = shareTable.getColumnModel();
        int[] widths = new int[tcm.getColumnCount()];

        for (int i = 0; i < tcm.getColumnCount(); i++) {
            widths[i] = tcm.getColumn(i).getWidth();
        }

        return widths;
    }
}
