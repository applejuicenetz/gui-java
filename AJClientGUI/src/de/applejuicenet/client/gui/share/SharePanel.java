package de.applejuicenet.client.gui.share;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.tree.DefaultTreeModel;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import de.applejuicenet.client.gui.components.GuiController;
import de.applejuicenet.client.gui.components.TklPanel;
import de.applejuicenet.client.gui.controller.ApplejuiceFassade;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.listener.LanguageListener;
import de.applejuicenet.client.gui.tables.NormalHeaderRenderer;
import de.applejuicenet.client.gui.tables.share.ShareModel;
import de.applejuicenet.client.gui.tables.share.ShareNode;
import de.applejuicenet.client.gui.tables.share.ShareTable;
import de.applejuicenet.client.gui.trees.WaitNode;
import de.applejuicenet.client.gui.trees.share.DirectoryTree;
import de.applejuicenet.client.gui.trees.share.ShareSelectionTreeCellRenderer;
import de.applejuicenet.client.shared.AJSettings;
import de.applejuicenet.client.shared.IconManager;
import de.applejuicenet.client.shared.ZeichenErsetzer;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/share/SharePanel.java,v 1.1 2004/10/15 10:14:04 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r [aj@tkl-soft.de]
 *
 */

public class SharePanel
    extends TklPanel
    implements LanguageListener {

    private static final long serialVersionUID = 2359016777104153892L;

    private JPanel panelCenter;
    private DirectoryTree folderTree = new DirectoryTree();
    private TitledBorder titledBorder1;
    private TitledBorder titledBorder2;
    private JLabel dateien = new JLabel();
    private JButton neueListe = new JButton();
    private JButton neuLaden = new JButton();
    private JButton refresh = new JButton();
    private JButton prioritaetSetzen = new JButton();
    private JButton prioritaetAufheben = new JButton();
    private JComboBox cmbPrio = new JComboBox();
    private AJSettings ajSettings;
    private ShareTable shareTable;
    private ShareModel shareModel;
    private JPopupMenu popup = new JPopupMenu();
    private JMenuItem sharedwsub;
    private JMenuItem sharedwosub;
    private JMenuItem notshared;
    private JPopupMenu popup2 = new JPopupMenu();
    private JMenuItem itemCopyToClipboard = new JMenuItem();
    private JMenuItem itemCopyToClipboardWithSources = new JMenuItem();
    private JMenuItem itemCopyToClipboardAsUBBCode = new JMenuItem();
    private JMenuItem itemOpenWithProgram = new JMenuItem();
    private Logger logger;
    
    public JButton getBtnPrioritaetAufheben(){
    	return prioritaetAufheben;
    }
    
    public JButton getBtnPrioritaetSetzen(){
    	return prioritaetSetzen;
    }

    public JButton getBtnNeuLaden(){
    	return neuLaden;
    }

    public JButton getBtnRefresh(){
    	return refresh;
    }

    public JButton getBtnNeueListe(){
    	return neueListe;
    }
    
    public JMenuItem getMnuNotShared(){
    	return notshared;
    }
    
    public JMenuItem getMnuSharedWithoutSub(){
    	return sharedwosub;
    }
    
    public JMenuItem getMnuSharedWithSub(){
    	return sharedwsub;
    }    
    
    public JMenuItem getMnuCopyToClipboard(){
    	return itemCopyToClipboard;
    }
    
    public JMenuItem getMnuCopyToClipboardWithSources(){
    	return itemCopyToClipboardWithSources;
    }

    public JMenuItem getMnuCopyToClipboardAsUBBCode(){
    	return itemCopyToClipboardAsUBBCode;
    }

    public JMenuItem getMnuOpenWithProgram(){
    	return itemOpenWithProgram;
    }
    
    public ShareTable getShareTable(){
    	return shareTable;
    }

    public ShareModel getShareModel(){
    	return shareModel;
    }
    
    public JLabel getLblDateien(){
    	return dateien;
    }
    
    public JComboBox getCmbPrioritaet(){
    	return cmbPrio;
    }
    
    public DirectoryTree getDirectoryTree(){
    	return folderTree;
    }
    
    public JPopupMenu getPopupMenu(){
    	return popup;
    }

    public JPopupMenu getPopupMenu2(){
    	return popup2;
    }
    
    public SharePanel(GuiController guiController) {
    	super(guiController);
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

        titledBorder1 = new TitledBorder("Test");
        titledBorder2 = new TitledBorder("Tester");
        setLayout(new BorderLayout());
        panelCenter = new JPanel(new BorderLayout());
        panelCenter.setBorder(titledBorder2);

        neueListe.setIcon(IconManager.getInstance().getIcon("treeRoot"));
        neuLaden.setIcon(IconManager.getInstance().getIcon("erneuern"));

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

        LanguageSelector.getInstance().addLanguageListener(this);
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
            sharedwsub.setText(ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.getFirstAttrbuteByTagName(".root.mainform.addwsubdirsbtn.caption")));
            sharedwosub.setText(ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.getFirstAttrbuteByTagName(".root.mainform.addosubdirsbtn.caption")));
            notshared.setText(ZeichenErsetzer.korrigiereUmlaute(
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
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
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
}
