package de.applejuicenet.client.gui.share;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.tree.DefaultTreeModel;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import de.applejuicenet.client.fassade.ApplejuiceFassade;
import de.applejuicenet.client.fassade.shared.AJSettings;
import de.applejuicenet.client.gui.components.GuiController;
import de.applejuicenet.client.gui.components.TklPanel;
import de.applejuicenet.client.gui.components.table.NormalHeaderRenderer;
import de.applejuicenet.client.gui.components.tree.WaitNode;
import de.applejuicenet.client.gui.share.table.ShareModel;
import de.applejuicenet.client.gui.share.table.ShareNode;
import de.applejuicenet.client.gui.share.table.ShareTable;
import de.applejuicenet.client.gui.share.tree.DirectoryTree;
import de.applejuicenet.client.gui.share.tree.ShareSelectionTreeCellRenderer;
import de.applejuicenet.client.shared.IconManager;
import de.tklsoft.gui.controls.TKLButton;
import de.tklsoft.gui.controls.TKLComboBox;
import de.tklsoft.gui.layout.Synchronizer;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/share/SharePanel.java,v 1.10 2005/04/18 13:54:16 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r [aj@tkl-soft.de]
 *
 */

public class SharePanel extends TklPanel{

    private JPanel panelCenter;
    private DirectoryTree folderTree = new DirectoryTree();
    private TitledBorder folderTreeBolder;
    private TitledBorder mailPanelBolder;
    private JLabel dateien = new JLabel();
    private TKLButton neueListe = new TKLButton();
    private TKLButton neuLaden = new TKLButton();
    private TKLButton refresh = new TKLButton();
    private TKLButton prioritaetSetzen = new TKLButton();
    private TKLButton prioritaetAufheben = new TKLButton();
    private TKLComboBox cmbPrio = new TKLComboBox();
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
    
    public TKLButton getBtnPrioritaetAufheben(){
    	return prioritaetAufheben;
    }
    
    public TKLButton getBtnPrioritaetSetzen(){
    	return prioritaetSetzen;
    }

    public TKLButton getBtnNeuLaden(){
    	return neuLaden;
    }

    public TKLButton getBtnRefresh(){
    	return refresh;
    }

    public TKLButton getBtnNeueListe(){
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
    
    public TKLComboBox getCmbPrioritaet(){
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
    
    public TitledBorder getFolderTreeBolder(){
    	return folderTreeBolder;
    }

    public TitledBorder getMainPanelBolder(){
    	return mailPanelBolder;
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
        popup2.add(new JSeparator());
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

        folderTreeBolder = new TitledBorder("Test");
        mailPanelBolder = new TitledBorder("Tester");
        setLayout(new BorderLayout());
        panelCenter = new JPanel(new BorderLayout());
        panelCenter.setBorder(mailPanelBolder);

        neueListe.setIcon(IconManager.getInstance().getIcon("treeRoot"));
        neuLaden.setIcon(IconManager.getInstance().getIcon("erneuern"));

        JPanel panel1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        Synchronizer synchronizer = new Synchronizer(Synchronizer.METHOD.HEIGHT);
        synchronizer.add(neuLaden);
        synchronizer.add(neueListe);
        synchronizer.add(cmbPrio);
        synchronizer.add(prioritaetSetzen);
        synchronizer.add(prioritaetAufheben);
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
        
        cmbPrio.disableDirtyComponent(true);
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
