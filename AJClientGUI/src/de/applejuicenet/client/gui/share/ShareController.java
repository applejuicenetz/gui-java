package de.applejuicenet.client.gui.share;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.TableColumnModel;

import org.apache.log4j.Level;

import de.applejuicenet.client.gui.AppleJuiceDialog;
import de.applejuicenet.client.gui.components.GuiController;
import de.applejuicenet.client.gui.components.GuiControllerActionListener;
import de.applejuicenet.client.gui.components.util.Value;
import de.applejuicenet.client.gui.controller.ApplejuiceFassade;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.controller.OptionsManagerImpl;
import de.applejuicenet.client.gui.controller.PositionManager;
import de.applejuicenet.client.gui.controller.PositionManagerImpl;
import de.applejuicenet.client.gui.share.table.ShareNode;
import de.applejuicenet.client.gui.share.tree.DirectoryNode;
import de.applejuicenet.client.gui.share.tree.ShareSelectionTreeModel;
import de.applejuicenet.client.shared.AJSettings;
import de.applejuicenet.client.shared.Information;
import de.applejuicenet.client.shared.ShareEntry;
import de.applejuicenet.client.shared.SwingWorker;
import de.applejuicenet.client.shared.ZeichenErsetzer;
import de.applejuicenet.client.shared.dac.ServerDO;
import de.applejuicenet.client.shared.dac.ShareDO;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/share/ShareController.java,v 1.10 2004/12/03 17:31:36 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 */

public class ShareController extends GuiController {

	private static final int PRIORITAET_AUFHEBEN = 0;
	private static final int PRIORITAET_SETZEN = 1;
	private static final int REFRESH = 2;
	private static final int SHARE_ERNEUERN = 3;
	private static final int NOT_SHARED = 4;
	private static final int SHARE_WITHOUT_SUB = 5;
	private static final int SHARE_WITH_SUB = 6;
	private static final int COPY_TO_CLIPBOARD = 7;
	private static final int COPY_TO_CLIPBOARD_WITH_SOURCES = 8;
	private static final int COPY_TO_CLIPBOARD_AS_UBB_CODE = 9;
	private static final int NEUE_LISTE = 10;
	private static final int OPEN_WITH_PROGRAM = 11;
	
	private static ShareController instance = null;
	
	private SharePanel sharePanel;
    private String dateiGroesse;
    private String eintraege;
    private int anzahlDateien = 0;
    private boolean initialized = false;
    private boolean treeInitialisiert = false;
    private ShareTreeMouseAdapter shareTreeMouseAdapter;
	
	private ShareController(){
		super();
		sharePanel = new SharePanel(this);
		init();
		LanguageSelector.getInstance().addLanguageListener(this);
	}
	
	public static synchronized ShareController getInstance(){
		if (null == instance){
			instance = new ShareController();
		}
		return instance;
	}

	public JComponent getComponent() {
		return sharePanel;
	}
	
	public Value[] getCustomizedValues(){
		return null;
	}
	
	private void init(){
		sharePanel.getBtnPrioritaetAufheben().addActionListener(
				new GuiControllerActionListener(this, PRIORITAET_AUFHEBEN));
		sharePanel.getBtnPrioritaetSetzen().addActionListener(
				new GuiControllerActionListener(this, PRIORITAET_SETZEN));
		sharePanel.getBtnRefresh().addActionListener(
				new GuiControllerActionListener(this, REFRESH));
		sharePanel.getBtnNeuLaden().addActionListener(
				new GuiControllerActionListener(this, SHARE_ERNEUERN));
		sharePanel.getMnuNotShared().addActionListener(
				new GuiControllerActionListener(this, NOT_SHARED));
		sharePanel.getMnuSharedWithoutSub().addActionListener(
				new GuiControllerActionListener(this, SHARE_WITHOUT_SUB));
		sharePanel.getMnuSharedWithSub().addActionListener(
				new GuiControllerActionListener(this, SHARE_WITH_SUB));
		sharePanel.getMnuCopyToClipboard().addActionListener(
				new GuiControllerActionListener(this, COPY_TO_CLIPBOARD));
		sharePanel.getMnuCopyToClipboardWithSources().addActionListener(
				new GuiControllerActionListener(this, COPY_TO_CLIPBOARD_WITH_SOURCES));
		sharePanel.getMnuCopyToClipboardAsUBBCode().addActionListener(
				new GuiControllerActionListener(this, COPY_TO_CLIPBOARD_AS_UBB_CODE));		
		sharePanel.getBtnNeueListe().addActionListener(
				new GuiControllerActionListener(this, NEUE_LISTE));		
		
		shareTreeMouseAdapter = new ShareTreeMouseAdapter(sharePanel.getDirectoryTree(),
				sharePanel.getPopupMenu(), sharePanel.getMnuSharedWithSub(),
				sharePanel.getMnuSharedWithoutSub(), sharePanel.getMnuNotShared());
		sharePanel.getShareTable().addMouseListener(
				new ShareTableMouseAdapter(sharePanel.getShareTable(), sharePanel.getPopupMenu2()));

        if (ApplejuiceFassade.getInstance().isLocalhost()){
    		sharePanel.getMnuOpenWithProgram().addActionListener(
    				new GuiControllerActionListener(this, OPEN_WITH_PROGRAM));		
        }
        else{
        	sharePanel.getMnuOpenWithProgram().setEnabled(false);
        }
		
	}

	public void fireAction(int actionId, Object source) {
		switch(actionId){
			case PRIORITAET_AUFHEBEN:{
				prioritaetAufheben();
				break;
			}
			case PRIORITAET_SETZEN:{
				prioritaetSetzen();
				break;
			}
			case REFRESH:{
				refresh();
				break;
			}
			case SHARE_ERNEUERN:{
				shareNeuLaden(true);
				break;
			}
			case NOT_SHARED:{
				nichtSharen();
				break;
			}
			case SHARE_WITHOUT_SUB:{
				ohneUnterverzeichnisSharen();
				break;
			}
			case SHARE_WITH_SUB:{
				mitUnterverzeichnisSharen();
				break;
			}
			case COPY_TO_CLIPBOARD:{
				copyToClipboard();
				break;
			}
			case COPY_TO_CLIPBOARD_WITH_SOURCES:{
				copyToClipboardWithSources();
				break;
			}
			case COPY_TO_CLIPBOARD_AS_UBB_CODE:{
				copyToClipboardAsUBBCode();
				break;
			}
			case NEUE_LISTE:{
				neueListe();
				break;
			}
			case OPEN_WITH_PROGRAM:{
				mitProgrammOeffnen();
				break;
			}
			default:{
				logger.error("Unregistrierte EventId " + actionId);
			}
		}
	}
	
	private void mitProgrammOeffnen(){
        Object[] obj = sharePanel.getShareTable().getSelectedItems();
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
	
	private void neueListe(){
        DateiListeDialog dateiListeDialog = new DateiListeDialog(
                AppleJuiceDialog.getApp(), false);
        sharePanel.getShareTable().setDragEnabled(true);
        dateiListeDialog.setVisible(true);
	}
	
	private void copyToClipboardWithSources(){
        Object[] obj = sharePanel.getShareTable().getSelectedItems();
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
	
	private void copyToClipboardAsUBBCode(){
        Object[] obj = sharePanel.getShareTable().getSelectedItems();
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
	
	private void copyToClipboard(){
        Object[] obj = sharePanel.getShareTable().getSelectedItems();
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
	
	private void mitUnterverzeichnisSharen(){
        Set shares = ApplejuiceFassade.getInstance().getAJSettings().getShareDirs();
        DirectoryNode node = (DirectoryNode) sharePanel.getDirectoryTree().
            getLastSelectedPathComponent();
        if (node != null) {
            String path = node.getDO().getPath();
            ShareEntry entry = new ShareEntry(path,
                ShareEntry.SUBDIRECTORY);
            shares.add(entry);
            ApplejuiceFassade.getInstance().setShare(shares);
            DirectoryNode.setShareDirs(shares);
            sharePanel.getDirectoryTree().updateUI();
        }
	}
	
	private void ohneUnterverzeichnisSharen(){
        Set shares = ApplejuiceFassade.getInstance().getAJSettings().getShareDirs();
        DirectoryNode node = (DirectoryNode) sharePanel.getDirectoryTree().
            getLastSelectedPathComponent();
        if (node != null) {
            String path = node.getDO().getPath();
            ShareEntry entry = new ShareEntry(path,
                ShareEntry.SINGLEDIRECTORY);
            shares.add(entry);
            ApplejuiceFassade.getInstance().setShare(shares);
            DirectoryNode.setShareDirs(shares);
            sharePanel.getDirectoryTree().updateUI();
        }
	}
	
	private void nichtSharen(){
        Set shares = ApplejuiceFassade.getInstance().getAJSettings().getShareDirs();
        DirectoryNode node = (DirectoryNode) sharePanel.getDirectoryTree().
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
                sharePanel.getDirectoryTree().updateUI();
            }
        }
	}
	
	private void prioritaetAufheben(){
		sharePanel.getBtnPrioritaetAufheben().setEnabled(false);
		sharePanel.getBtnPrioritaetSetzen().setEnabled(false);
		sharePanel.getBtnNeuLaden().setEnabled(false);
        SwingWorker worker = new SwingWorker() {
            public Object construct() {
                try {
                    Object[] values = sharePanel.getShareTable().getSelectedItems();
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
                    	sharePanel.getShareTable().updateUI();
                        sharePanel.getBtnPrioritaetAufheben().setEnabled(true);
                        sharePanel.getBtnPrioritaetSetzen().setEnabled(true);
                        sharePanel.getBtnNeuLaden().setEnabled(true);
                    }
                });
            }
        };
        worker.start();
	}
	
	private void prioritaetSetzen(){
		sharePanel.getBtnPrioritaetAufheben().setEnabled(false);
		sharePanel.getBtnPrioritaetSetzen().setEnabled(false);
        sharePanel.getBtnNeuLaden().setEnabled(false);
        new Thread() {
            public void run() {
                try {
                    int prio = ( (Integer) sharePanel.getCmbPrioritaet().getSelectedItem()).
                        intValue();
                    Object[] values = sharePanel.getShareTable().getSelectedItems();
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
	
	private void refresh(){
		sharePanel.getBtnRefresh().setEnabled(false);
        final SwingWorker worker = new SwingWorker() {
            public Object construct() {
                try {
                    Set shares = ApplejuiceFassade.getInstance().
                    	getAJSettings().getShareDirs();
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
            	sharePanel.getBtnRefresh().setEnabled(true);
            }
        };
        worker.start();
	}
	
    private void shareNeuLaden(final boolean komplettNeu) {
    	sharePanel.getBtnPrioritaetAufheben().setEnabled(false);
    	sharePanel.getBtnPrioritaetSetzen().setEnabled(false);
    	sharePanel.getBtnNeuLaden().setEnabled(false);
        final SwingWorker worker = new SwingWorker() {
            public Object construct() {
                try {
                    ShareNode rootNode = sharePanel.getShareModel().getRootNode();
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
                    sharePanel.getLblDateien().setText(temp);
                }
                catch (Exception e) {
                    if (logger.isEnabledFor(Level.ERROR)) {
                        logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
                    }
                }
                return null;
            }

            public void finished() {
            	sharePanel.getShareTable().updateUI();
                sharePanel.getBtnPrioritaetAufheben().setEnabled(true);
                sharePanel.getBtnPrioritaetSetzen().setEnabled(true);
                sharePanel.getBtnNeuLaden().setEnabled(true);
            }
        };
        worker.start();
    }

    public void componentSelected() {
        try {
            if (!initialized) {
                initialized = true;
                sharePanel.getShareTable().setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                TableColumnModel headerModel = sharePanel.getShareTable().getTableHeader().
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
                        headerModel.getColumn(i).setPreferredWidth(sharePanel.getShareTable().
                            getWidth() / columnCount);
                    }
                }
            }
            if (!treeInitialisiert) {
                treeInitialisiert = true;
                new Thread() {
                    public void run() {
                        AJSettings ajSettings = ApplejuiceFassade.getInstance().
                            getAJSettings();
                        DirectoryNode.setShareDirs(ajSettings.getShareDirs());
                        sharePanel.getBtnPrioritaetAufheben().setEnabled(true);
                        sharePanel.getBtnPrioritaetSetzen().setEnabled(true);
                        sharePanel.getBtnNeuLaden().setEnabled(true);
                        sharePanel.getBtnRefresh().setEnabled(true);
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

    private void initShareSelectionTree() {
    	sharePanel.getDirectoryTree().removeMouseListener(shareTreeMouseAdapter);
        SwingWorker worker2 = new SwingWorker() {
            public Object construct() {
                ShareSelectionTreeModel treeModel = new ShareSelectionTreeModel();
                sharePanel.getDirectoryTree().setModel(treeModel);
                sharePanel.getDirectoryTree().setRootVisible(false);
                sharePanel.getDirectoryTree().addMouseListener(shareTreeMouseAdapter);
                return null;
            }
        };
        worker2.start();
    }
    
    public void componentLostSelection() {
    	// nix zu tun
	}
    
    protected void languageChanged() {
        LanguageSelector languageSelector = LanguageSelector.getInstance();
        sharePanel.getFolderTreeBolder().setTitle(ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.
                getFirstAttrbuteByTagName(".root.mainform.dirssheet.caption")));
        sharePanel.getMainPanelBolder().setTitle(ZeichenErsetzer.korrigiereUmlaute(
            languageSelector.
            getFirstAttrbuteByTagName(".root.mainform.filessheet.caption")));
        sharePanel.getMnuSharedWithSub().setText(ZeichenErsetzer.korrigiereUmlaute(
            languageSelector.getFirstAttrbuteByTagName(".root.mainform.addwsubdirsbtn.caption")));
        sharePanel.getMnuSharedWithoutSub().setText(ZeichenErsetzer.korrigiereUmlaute(
            languageSelector.getFirstAttrbuteByTagName(".root.mainform.addosubdirsbtn.caption")));
        sharePanel.getMnuNotShared().setText(ZeichenErsetzer.korrigiereUmlaute(
            languageSelector.getFirstAttrbuteByTagName(".root.mainform.deldirbtn.caption")));
        sharePanel.getMnuCopyToClipboard().setText(ZeichenErsetzer.korrigiereUmlaute(
            languageSelector.getFirstAttrbuteByTagName(".root.mainform.getlink1.caption")));
        sharePanel.getMnuCopyToClipboardAsUBBCode().setText(ZeichenErsetzer.korrigiereUmlaute(
            languageSelector.getFirstAttrbuteByTagName(".root.javagui.shareform.linkalsubbcode")));
        sharePanel.getMnuCopyToClipboardWithSources().setText(ZeichenErsetzer.korrigiereUmlaute(
            languageSelector.getFirstAttrbuteByTagName(".root.javagui.downloadform.getlinkwithsources")));
        sharePanel.getMnuOpenWithProgram().setText("VLC");
        sharePanel.getBtnRefresh().setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(".root.mainform.startsharecheck.caption")));
        sharePanel.getBtnRefresh().setToolTipText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(".root.mainform.startsharecheck.hint")));
        sharePanel.getBtnNeueListe().setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(".root.mainform.newfilelist.caption")));
        sharePanel.getBtnNeueListe().setToolTipText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(".root.mainform.newfilelist.hint")));
        sharePanel.getBtnNeuLaden().setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(".root.mainform.sharereload.caption")));
        sharePanel.getBtnNeuLaden().setToolTipText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(".root.mainform.sharereload.hint")));
        sharePanel.getBtnPrioritaetSetzen().setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(".root.mainform.setprio.caption")));
        sharePanel.getBtnPrioritaetSetzen().setToolTipText(ZeichenErsetzer.korrigiereUmlaute(
            languageSelector.getFirstAttrbuteByTagName(".root.mainform.setprio.hint")));
        sharePanel.getBtnPrioritaetAufheben().setText(ZeichenErsetzer.korrigiereUmlaute(
            languageSelector.getFirstAttrbuteByTagName(".root.mainform.clearprio.caption")));
        sharePanel.getBtnPrioritaetAufheben().setToolTipText(ZeichenErsetzer.korrigiereUmlaute(
            languageSelector.getFirstAttrbuteByTagName(".root.mainform.clearprio.hint")));

        String[] tableColumns = new String[6];
        tableColumns[0] = ZeichenErsetzer.korrigiereUmlaute(
            languageSelector.
            getFirstAttrbuteByTagName(".root.mainform.sfiles.col0caption"));
        tableColumns[1] = ZeichenErsetzer.korrigiereUmlaute(
            languageSelector.
            getFirstAttrbuteByTagName(".root.mainform.sfiles.col1caption"));
        tableColumns[2] = ZeichenErsetzer.korrigiereUmlaute(
            languageSelector.
            getFirstAttrbuteByTagName(".root.mainform.sfiles.col2caption"));
        tableColumns[3] = ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.getFirstAttrbuteByTagName(".root.javagui.shareform.letzteanfrage"));
        tableColumns[4] = ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.getFirstAttrbuteByTagName(".root.javagui.shareform.downloadanfragen"));
        tableColumns[5] = ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.getFirstAttrbuteByTagName(".root.javagui.shareform.suchanfragen"));

        TableColumnModel tcm = sharePanel.getShareTable().getColumnModel();
        for (int i = 0; i < tableColumns.length; i++) {
            tcm.getColumn(i).setHeaderValue(tableColumns[i]);
        }
        eintraege = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(".root.javagui.shareform.anzahlShare"));
        if (anzahlDateien > 0) {
            String temp = eintraege;
            temp = temp.replaceFirst("%i", Integer.toString(anzahlDateien));
            temp = temp.replaceFirst("%s", dateiGroesse);
            sharePanel.getLblDateien().setText(temp);
        }
        else {
        	sharePanel.getLblDateien().setText("");
        }
	}

	protected void contentChanged(int type, Object content) {
		// nix zu tun
	}
}
