package de.applejuicenet.client.gui.download;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.TableColumn;

import org.apache.log4j.Level;

import de.applejuicenet.client.gui.AppleJuiceDialog;
import de.applejuicenet.client.gui.components.GuiController;
import de.applejuicenet.client.gui.components.GuiControllerActionListener;
import de.applejuicenet.client.gui.components.treetable.TreeTableModelAdapter;
import de.applejuicenet.client.gui.components.util.Value;
import de.applejuicenet.client.gui.controller.ApplejuiceFassade;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.controller.OptionsManagerImpl;
import de.applejuicenet.client.gui.controller.PositionManager;
import de.applejuicenet.client.gui.controller.PositionManagerImpl;
import de.applejuicenet.client.gui.controller.event.DataPropertyChangeEvent;
import de.applejuicenet.client.gui.controller.event.DownloadDataPropertyChangeEvent;
import de.applejuicenet.client.gui.download.table.DownloadDirectoryNode;
import de.applejuicenet.client.gui.download.table.DownloadMainNode;
import de.applejuicenet.client.gui.download.table.DownloadRootNode;
import de.applejuicenet.client.gui.listener.DataUpdateListener;
import de.applejuicenet.client.gui.options.IncomingDirSelectionDialog;
import de.applejuicenet.client.shared.Information;
import de.applejuicenet.client.shared.Settings;
import de.applejuicenet.client.shared.SoundPlayer;
import de.applejuicenet.client.shared.ZeichenErsetzer;
import de.applejuicenet.client.shared.dac.DownloadDO;
import de.applejuicenet.client.shared.dac.DownloadSourceDO;
import de.applejuicenet.client.shared.dac.ServerDO;
import de.applejuicenet.client.shared.dac.ShareDO;

public class DownloadController extends GuiController {
	
	private static DownloadController instance = null;
	
	private static final int ABBRECHEN = 0;
	private static final int COPY_TO_CLIPBOARD = 1;
	private static final int COPY_TO_CLIPBOARD_WITH_SOURCES = 2;
	private static final int OPEN_WITH_PROGRAM = 3;
	private static final int PAUSE = 4;
	private static final int FORTSETZEN = 5;
	private static final int UMBENENNEN = 6;
	private static final int ZIELORDNER_AENDERN = 7;
	private static final int FERTIGE_ENTFERNEN = 8;
	private static final int PARTLISTE_ANZEIGEN = 9;
	private static final int START_DOWNLOAD = 10;
	private static final int PARTLISTE_ANZEIGEN_PER_BUTTON = 11;
	private static final int START_POWERDOWNLOAD = 12;
	private static final int NODE_ITEM_CLICKED = 13;
	private static final int MAYBE_SHOW_POPUP = 14;
	
	private static final int DOWNLOAD_PROPERTY_CHANGE_EVENT = 15;

	private DownloadPanel downloadPanel;

	private boolean initialized = false;
	private boolean panelSelected = false;
	private String dialogTitel;
	private String downloadAbbrechen;
	private DownloadPartListWatcher downloadPartListWatcher;
	
	private DownloadController() {
		super();
		downloadPanel = new DownloadPanel(this);
		try{
			init();
		} catch (Exception e) {
			if (logger.isEnabledFor(Level.ERROR)) {
				logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
			}
		}
	}

	public static synchronized DownloadController getInstance() {
		if (null == instance) {
			instance = new DownloadController();
		}
		return instance;
	}

	private void init() {
		downloadPanel.getMnuAbbrechen().addActionListener(
				new GuiControllerActionListener(this, ABBRECHEN));
		downloadPanel.getMnuCopyToClipboard().addActionListener(
				new GuiControllerActionListener(this, COPY_TO_CLIPBOARD));
		downloadPanel.getMnuCopyToClipboardWithSources().addActionListener(
				new GuiControllerActionListener(this, COPY_TO_CLIPBOARD_WITH_SOURCES));
		downloadPanel.getMnuPause().addActionListener(
				new GuiControllerActionListener(this, PAUSE));
		downloadPanel.getMnuFortsetzen().addActionListener(
				new GuiControllerActionListener(this, FORTSETZEN));
		downloadPanel.getMnuUmbenennen().addActionListener(
				new GuiControllerActionListener(this, UMBENENNEN));
		downloadPanel.getMnuZielordner().addActionListener(
				new GuiControllerActionListener(this, ZIELORDNER_AENDERN));
		downloadPanel.getMnuFertigeEntfernen().addActionListener(
				new GuiControllerActionListener(this, FERTIGE_ENTFERNEN));
		downloadPanel.getMnuPartlisteAnzeigen().addActionListener(
				new GuiControllerActionListener(this, PARTLISTE_ANZEIGEN));
		downloadPanel.getBtnStartDownload().addActionListener(
				new GuiControllerActionListener(this, START_DOWNLOAD));
		downloadPanel.getBtnHoleListe().addActionListener(
				new GuiControllerActionListener(this, PARTLISTE_ANZEIGEN_PER_BUTTON));
		downloadPanel.getBtnPowerDownload().addActionListener(
				new GuiControllerActionListener(this, START_POWERDOWNLOAD));
        if (ApplejuiceFassade.getInstance().isLocalhost()){
        	downloadPanel.getMnuOpenWithProgram().addActionListener(
        			new GuiControllerActionListener(this, OPEN_WITH_PROGRAM));
        }
        else{
        	downloadPanel.getMnuOpenWithProgram().setEnabled(false);
        }
		downloadPanel.getDownloadTable().addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent ke) {
				switch (ke.getKeyCode()) {
				case KeyEvent.VK_F2: {
					renameDownload();
					break;
				}
				case KeyEvent.VK_F3: {
					changeIncomingDir();
					break;
				}
				case KeyEvent.VK_F5: {
					pausieren();
					break;
				}
				case KeyEvent.VK_F6: {
					fortsetzen();
					break;
				}
				default: {
					break;
				}
				}
			}
		});
		downloadPartListWatcher = new DownloadPartListWatcher(this);
		downloadPanel.getDownloadTable().addMouseListener(
				new DownloadTableMouseListener(this, downloadPanel.getDownloadTable()
						, NODE_ITEM_CLICKED));
		downloadPanel.getDownloadTable().addMouseListener(
				new DownloadTablePopupListener(this, downloadPanel.getDownloadTable()
						, MAYBE_SHOW_POPUP));
		LanguageSelector.getInstance().addLanguageListener(this);
/*		ApplejuiceFassade.getInstance().addDataUpdateListener(this,
				DataUpdateListener.DOWNLOAD_CHANGED);*/
		Map downloads = ApplejuiceFassade.getInstance().getDownloadsSnapshot();
		((DownloadRootNode) downloadPanel.getDownloadModel().getRoot()).setDownloadMap(downloads);
		DownloadDirectoryNode.setDownloads(downloads);
		ApplejuiceFassade.getInstance().getDownloadPropertyChangeInformer().
			addDataPropertyChangeListener(
				new DownloadPropertyChangeListener(this, DOWNLOAD_PROPERTY_CHANGE_EVENT));
	}
	
	public Value[] getCustomizedValues(){
		return null;
	}
	
	public void fireAction(int actionId, Object source) {
		switch (actionId){
			case ABBRECHEN:{
				downloadAbbrechen();
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
			case OPEN_WITH_PROGRAM:{
				openWithProgram();
				break;
			}
			case PAUSE:{
				pausieren();
				break;
			}
			case FORTSETZEN:{
				fortsetzen();
				break;
			}
			case UMBENENNEN:{
				renameDownload();
				break;
			}
			case ZIELORDNER_AENDERN:{
				changeIncomingDir();
				break;
			}
			case FERTIGE_ENTFERNEN:{
				clearReadyDownloads();
				break;
			}
			case PARTLISTE_ANZEIGEN:{
				tryGetPartList(false);
				break;
			}
			case START_DOWNLOAD:{
				startDownload();
				break;
			}
			case PARTLISTE_ANZEIGEN_PER_BUTTON:{
				tryGetPartList(true);
				break;
			}
			case START_POWERDOWNLOAD:{
				startPowerDownload();
				break;
			}
			case NODE_ITEM_CLICKED:{
				nodeItemClicked(source);
				break;
			}
			case DOWNLOAD_PROPERTY_CHANGE_EVENT:{
				downloadPropertyChanged((DataPropertyChangeEvent)source);
				break;
			}
			case MAYBE_SHOW_POPUP:{
				maybeShowPopup((MouseEvent)source);
				break;
			}
			default:{
				logger.error("Unregistrierte EventId " + actionId);
			}
		}
	}

	public JComponent getComponent() {
		return downloadPanel;
	}
	
	private synchronized void downloadPropertyChanged(DataPropertyChangeEvent evt){
		boolean tmpSort = false;
		if (evt.isEventContainer()){
			DataPropertyChangeEvent[] events = evt.getNestedEvents();
			for (int i = 0; i<events.length; i++){
				if (handleDownloadDataPropertyChangeEvent(
						(DownloadDataPropertyChangeEvent)events[i])){
					tmpSort = true;
				}
			}
		}
		else{
			if (handleDownloadDataPropertyChangeEvent((DownloadDataPropertyChangeEvent)evt)){
				tmpSort = true;
			}
		}
		final boolean sort = tmpSort;
    	System.out.println("event");
		try{
	        SwingUtilities.invokeLater(new Runnable() {
	            public void run() {
	            	System.out.println("update");
	            	if (sort){
		            	downloadPanel.getDownloadModel().sortNextRefresh(sort);
		            	downloadPanel.getDownloadTable().updateUI();
		            	downloadPanel.getDownloadModel().sortNextRefresh(false);
	            	}
	            	else{
		            	downloadPanel.getDownloadTable().updateUI();
	            	}
	            }
	        });
		}
        catch(Exception e){
        	e.printStackTrace();
        }
	}
	
	private boolean handleDownloadDataPropertyChangeEvent(DownloadDataPropertyChangeEvent event){
		if (event.getName() == null){
			return false;
		}
		else if (event.getName().equals(DownloadDataPropertyChangeEvent.DOWNLOAD_ADDED)
				|| event.getName().equals(DownloadDataPropertyChangeEvent.DOWNLOAD_REMOVED)
				|| event.getName().equals(DownloadDataPropertyChangeEvent.DIRECTORY_CHANGED)
				|| event.getName().equals(DownloadDataPropertyChangeEvent.FILENAME_CHANGED)){
        	System.out.println("name: " + event.getName());
			return true;
		}
		return false;
	}
	
	private void clearReadyDownloads(){
		ApplejuiceFassade.getInstance().cleanDownloadList();
		downloadPanel.getDownloadDOOverviewPanel().enableHoleListButton(false);
		downloadPanel.getPowerDownloadPanel().btnPdl.setEnabled(false);
		downloadPanel.getPowerDownloadPanel().setPwdlValue(0);
		downloadPanel.getDownloadTable().getSelectionModel().clearSelection();
	}
	
	private void nodeItemClicked(Object node){
		if (node.getClass() == DownloadMainNode.class
				&& ((DownloadMainNode) node).getType() == DownloadMainNode.ROOT_NODE) {
			DownloadDO downloadDO = ((DownloadMainNode) node)
					.getDownloadDO();
			if (Settings.getSettings().isDownloadUebersicht()) {
				downloadPanel.getDownloadDOOverviewPanel().enableHoleListButton(false);
				tryGetPartList(false);
			} else {
				if ((downloadDO.getStatus() == DownloadDO.SUCHEN_LADEN || downloadDO
						.getStatus() == DownloadDO.PAUSIERT)) {
					downloadPanel.getDownloadDOOverviewPanel().enableHoleListButton(true);
				} else {
					downloadPanel.getDownloadDOOverviewPanel().enableHoleListButton(false);
				}
			}
			if (!downloadPanel.getPowerDownloadPanel().isAutomaticPwdlActive()) {
				downloadPanel.getPowerDownloadPanel().btnPdl.setEnabled(true);
				if (downloadDO.getStatus() == DownloadDO.SUCHEN_LADEN
						|| downloadDO.getStatus() == DownloadDO.PAUSIERT) {
					downloadPanel.getPowerDownloadPanel().setPwdlValue(downloadDO
							.getPowerDownload());
				} else {
					downloadPanel.getDownloadDOOverviewPanel().enableHoleListButton(false);
					downloadPanel.getPowerDownloadPanel().btnPdl.setEnabled(false);
					downloadPanel.getPowerDownloadPanel().setPwdlValue(0);
				}
			}
		} else if (node.getClass() == DownloadSourceDO.class) {
			if (Settings.getSettings().isDownloadUebersicht()) {
				downloadPanel.getDownloadDOOverviewPanel().enableHoleListButton(false);
				tryGetPartList(false);
			} else {
				downloadPanel.getDownloadDOOverviewPanel().enableHoleListButton(true);
			}
			if (!downloadPanel.getPowerDownloadPanel().isAutomaticPwdlActive()) {
				downloadPanel.getPowerDownloadPanel().btnPdl.setEnabled(true);
				downloadPanel.getPowerDownloadPanel()
						.setPwdlValue(((DownloadSourceDO) node)
								.getPowerDownload());
			}
		} else {
			downloadPanel.getPowerDownloadPanel().btnPdl.setEnabled(false);
			downloadPanel.getPowerDownloadPanel().setPwdlValue(0);
			downloadPanel.getDownloadDOOverviewPanel().enableHoleListButton(false);
		}
		
	}
	
	private void startPowerDownload(){
        try {
            Object[] selectedItems = getSelectedDownloadItems();
            if (selectedItems != null && selectedItems.length != 0) {
                int powerDownload = 0;
                if (!downloadPanel.getBtnPowerDownloadInaktiv().isSelected()) {
                    String temp = downloadPanel.getRatioField().getText();
                    double power = 2.2;
                    try {
                        power = Double.parseDouble(temp);
                    }
                    catch (NumberFormatException nfE) {
                        if (logger.isEnabledFor(Level.ERROR)) {
                            logger.error(ApplejuiceFassade.ERROR_MESSAGE, nfE);
                        }
                        downloadPanel.getRatioField().setText("2.2");
                    }
                    powerDownload = (int) (power * 10 - 10);
                }
                ArrayList temp = new ArrayList();
                for (int i = 0; i < selectedItems.length; i++) {
                    if (selectedItems[i].getClass() == DownloadMainNode.class) {
                        temp.add(new Integer( ( (DownloadMainNode)
                                               selectedItems[i]).getDownloadDO().
                                             getId()));
                    }
                }
                int[] ids = new int[temp.size()];
                for (int i = 0; i < temp.size(); i++) {
                    ids[i] = ( (Integer) temp.get(i)).intValue();
                }
                ApplejuiceFassade.getInstance().setPowerDownload(ids,
                    powerDownload);
                if (downloadPanel.getBtnPowerDownloadAktiv().isSelected()) {
                    SoundPlayer.getInstance().playSound(SoundPlayer.POWER);
                }
            }
        }
        catch (Exception ex) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error(ApplejuiceFassade.ERROR_MESSAGE, ex);
            }
        }
	}
	
	private void maybeShowPopup(MouseEvent e){
		Object[] selectedItems = getSelectedDownloadItems();
		downloadPanel.getMnuUmbenennen().setVisible(false);
		downloadPanel.getMnuZielordner().setVisible(false);
		downloadPanel.getMnuPartlisteAnzeigen().setVisible(false);
		boolean pausiert = false;
		boolean laufend = false;
		if (selectedItems != null) {
			if (selectedItems.length == 1) {
				if ((selectedItems[0].getClass() == DownloadMainNode.class && ((DownloadMainNode) selectedItems[0])
						.getType() == DownloadMainNode.ROOT_NODE)
						|| (selectedItems[0].getClass() == DownloadSourceDO.class)) {
					downloadPanel.getMnuPartlisteAnzeigen().setVisible(true);
				}
				if (selectedItems[0].getClass() != DownloadSourceDO.class) {
					downloadPanel.getMnuUmbenennen().setVisible(true);
					downloadPanel.getMnuZielordner().setVisible(true);
				}
				if (selectedItems[0].getClass() == DownloadMainNode.class) {
					DownloadDO downloadDO = ((DownloadMainNode) selectedItems[0])
							.getDownloadDO();
					if (downloadDO.getStatus() == DownloadDO.SUCHEN_LADEN) {
						laufend = true;
					} else if (downloadDO.getStatus() == DownloadDO.PAUSIERT) {
						pausiert = true;
					}
				}
			} else {
				for (int i = 0; i < selectedItems.length; i++) {
					if ((selectedItems[i].getClass() == DownloadMainNode.class && ((DownloadMainNode) selectedItems[i])
							.getType() == DownloadMainNode.ROOT_NODE)) {
						downloadPanel.getMnuZielordner().setVisible(true);
					}
					DownloadDO downloadDO;
					if (selectedItems[i].getClass() == DownloadMainNode.class) {
						downloadDO = ((DownloadMainNode) selectedItems[i])
								.getDownloadDO();
						if (downloadDO.getStatus() == DownloadDO.SUCHEN_LADEN) {
							laufend = true;
						} else if (downloadDO.getStatus() == DownloadDO.PAUSIERT) {
							pausiert = true;
						}
					}
				}
			}
		}
		if (laufend) {
			downloadPanel.getMnuPause().setEnabled(true);
		} else {
			downloadPanel.getMnuPause().setEnabled(false);
		}
		if (pausiert) {
			downloadPanel.getMnuFortsetzen().setEnabled(true);
		} else {
			downloadPanel.getMnuFortsetzen().setEnabled(false);
		}
		downloadPanel.getPopup().show(downloadPanel.getDownloadTable(), e.getX(), e.getY());
	}
	
	private void tryGetPartList(boolean disableButton) {
		if (disableButton){
            downloadPanel.getBtnHoleListe().setEnabled(false);
		}
		Object[] selectedItems = getSelectedDownloadItems();
		if (selectedItems != null && selectedItems.length == 1) {
			if (selectedItems[0].getClass() == DownloadMainNode.class
					&& ((DownloadMainNode) selectedItems[0]).getType() == DownloadMainNode.ROOT_NODE) {
				downloadPartListWatcher.setDownloadNode(selectedItems[0]);
			} else if (selectedItems[0].getClass() == DownloadSourceDO.class) {
				downloadPartListWatcher.setDownloadNode(selectedItems[0]);
			}
		}
	}
	
	private Object[] getSelectedDownloadItems() {
		try {
			int count = downloadPanel.getDownloadTable().getSelectedRowCount();
			Object[] result = null;
			if (count == 1) {
				result = new Object[count];
				result[0] = ((TreeTableModelAdapter) downloadPanel.getDownloadTable().getModel())
						.nodeForRow(downloadPanel.getDownloadTable().getSelectedRow());
			} else if (count > 1) {
				result = new Object[count];
				int[] indizes = downloadPanel.getDownloadTable().getSelectedRows();
				for (int i = 0; i < indizes.length; i++) {
					result[i] = ((TreeTableModelAdapter) downloadPanel.getDownloadTable()
							.getModel()).nodeForRow(indizes[i]);
				}
			}
			return result;
		} catch (Exception e) {
			if (logger.isEnabledFor(Level.ERROR)) {
				logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
			}
			return null;
		}
	}
	
	private void renameDownload() {
		Object[] selectedItems = getSelectedDownloadItems();
		if (selectedItems != null && selectedItems.length == 1) {
			if (selectedItems[0].getClass() == DownloadMainNode.class
					&& ((DownloadMainNode) selectedItems[0]).getType() == DownloadMainNode.ROOT_NODE) {

				DownloadDO downloadDO = ((DownloadMainNode) selectedItems[0])
						.getDownloadDO();
				RenameDownloadDialog renameDownloadDialog = new RenameDownloadDialog(
						AppleJuiceDialog.getApp(), downloadDO);
				renameDownloadDialog.setVisible(true);
				String neuerName = renameDownloadDialog.getNewName();

				if (neuerName == null) {
					return;
				} else {
					if (downloadDO.getFilename().compareTo(neuerName) != 0) {
						ApplejuiceFassade.getInstance().renameDownload(
								downloadDO.getId(), neuerName);
					}
				}
			}
		}
	}

	private void pausieren() {
		Object[] selectedItems = getSelectedDownloadItems();
		if (selectedItems != null && selectedItems.length != 0
				&& !downloadPanel.getPowerDownloadPanel().isAutomaticPwdlActive()) {
			ArrayList indizesPausieren = new ArrayList();
			for (int i = 0; i < selectedItems.length; i++) {
				if (selectedItems[i].getClass() == DownloadMainNode.class) {
					DownloadDO downloadDO = ((DownloadMainNode) selectedItems[i])
							.getDownloadDO();
					if (downloadDO.getStatus() == DownloadDO.SUCHEN_LADEN) {
						indizesPausieren.add(new Integer(downloadDO.getId()));
					}
				}
			}
			int size = indizesPausieren.size();
			if (size > 0) {
				final int[] pausieren = new int[size];
				for (int i = 0; i < size; i++) {
					pausieren[i] = ((Integer) indizesPausieren.get(i))
							.intValue();
				}
				new Thread() {
					public void run() {
						ApplejuiceFassade.getInstance()
								.pauseDownload(pausieren);
					}
				}.start();
			}
		}
	}

	private void fortsetzen() {
		Object[] selectedItems = getSelectedDownloadItems();
		if (selectedItems != null && selectedItems.length != 0
				&& !downloadPanel.getPowerDownloadPanel().isAutomaticPwdlActive()) {
			ArrayList indizesFortsetzen = new ArrayList();
			for (int i = 0; i < selectedItems.length; i++) {
				if (selectedItems[i].getClass() == DownloadMainNode.class) {
					DownloadDO downloadDO = ((DownloadMainNode) selectedItems[i])
							.getDownloadDO();
					if (downloadDO.getStatus() == DownloadDO.PAUSIERT) {
						indizesFortsetzen.add(new Integer(downloadDO.getId()));
					}
				}
			}
			int size = indizesFortsetzen.size();
			if (size > 0) {
				final int[] fortsetzen = new int[size];
				for (int i = 0; i < size; i++) {
					fortsetzen[i] = ((Integer) indizesFortsetzen.get(i))
							.intValue();
				}
				new Thread() {
					public void run() {
						ApplejuiceFassade.getInstance().resumeDownload(
								fortsetzen);
					}
				}.start();
			}
		}
	}

	private void startDownload() {
		final String link = downloadPanel.getDownloadLinkField().getText();
		if (link.length() != 0) {
			new Thread() {
				public void run() {
					ApplejuiceFassade.getInstance().processLink(link);
					SoundPlayer.getInstance().playSound(SoundPlayer.LADEN);
				}
			}.start();
			downloadPanel.getDownloadLinkField().setText("");
		}
	}

	private void changeIncomingDir() {
		Object[] selectedItems = getSelectedDownloadItems();
		if (selectedItems == null || selectedItems.length == 0) {
			return;
		}
		String[] dirs = ApplejuiceFassade.getInstance()
				.getCurrentIncomingDirs();
		IncomingDirSelectionDialog incomingDirSelectionDialog = new IncomingDirSelectionDialog(
				AppleJuiceDialog.getApp(), dirs);
		incomingDirSelectionDialog.setVisible(true);
		String neuerName = incomingDirSelectionDialog.getSelectedIncomingDir();

		if (neuerName == null) {
			return;
		} else {
			neuerName = neuerName.trim();
			if (neuerName.indexOf(File.separator) == 0
					|| neuerName.indexOf(ApplejuiceFassade.separator) == 0) {
				neuerName = neuerName.substring(1);
			}
		}
		DownloadDO downloadDO;
		for (int i = 0; i < selectedItems.length; i++) {
			if (selectedItems[i].getClass() == DownloadMainNode.class
					&& ((DownloadMainNode) selectedItems[i]).getType() == DownloadMainNode.ROOT_NODE) {
				downloadDO = ((DownloadMainNode) selectedItems[i])
						.getDownloadDO();
				if (downloadDO.getTargetDirectory().compareTo(neuerName) != 0) {
					ApplejuiceFassade.getInstance().setTargetDir(
							downloadDO.getId(), neuerName);
				}
			}
		}
	}
	
	private void openWithProgram(){
		Object[] selectedItems = getSelectedDownloadItems();
		if (selectedItems != null && selectedItems.length == 1) {
			DownloadDO downloadDO = null;
			if (selectedItems[0].getClass() == DownloadMainNode.class
					&& ((DownloadMainNode) selectedItems[0]).getType() == DownloadMainNode.ROOT_NODE) {
				downloadDO = ((DownloadMainNode) selectedItems[0])
						.getDownloadDO();
			} else if (selectedItems[0].getClass() == DownloadSourceDO.class) {
				DownloadSourceDO downloadSourceDO = (DownloadSourceDO) selectedItems[0];
				Map downloads = ApplejuiceFassade.getInstance()
						.getDownloadsSnapshot();
				String key = Integer.toString(downloadSourceDO
						.getDownloadId());
				downloadDO = (DownloadDO) downloads.get(key);
			}
			if (downloadDO != null) {
                String programToExecute = OptionsManagerImpl.getInstance().getOpenProgram();
                if (programToExecute.length() != 0){
					int shareId = downloadDO.getShareId();
					ShareDO shareDO = (ShareDO)ApplejuiceFassade.getInstance().getObjectById(shareId);
					if (shareDO != null){
                        String filename = shareDO.getFilename();
            			try {
            				Runtime.getRuntime().exec(new String[] { programToExecute, filename });
            			} catch (Exception ex) {
            				//nix zu tun
            			}
					}
				}
			}
        }
	}
	
	private void copyToClipboardWithSources(){
		Object[] selectedItems = getSelectedDownloadItems();
		if (selectedItems != null && selectedItems.length == 1) {
			Clipboard cb = Toolkit.getDefaultToolkit()
					.getSystemClipboard();
			StringBuffer toCopy = new StringBuffer();
			toCopy.append("ajfsp://file|");
			boolean copyToClipboard = false;
			if (selectedItems[0].getClass() == DownloadMainNode.class
					&& ((DownloadMainNode) selectedItems[0]).getType() == DownloadMainNode.ROOT_NODE) {
				DownloadDO downloadDO = ((DownloadMainNode) selectedItems[0])
						.getDownloadDO();
				toCopy.append(downloadDO.getFilename() + "|"
						+ downloadDO.getHash() + "|"
						+ downloadDO.getGroesse());
				copyToClipboard = true;
			} else if (selectedItems[0].getClass() == DownloadSourceDO.class) {
				DownloadSourceDO downloadSourceDO = (DownloadSourceDO) selectedItems[0];
				Map downloads = ApplejuiceFassade.getInstance()
						.getDownloadsSnapshot();
				String key = Integer.toString(downloadSourceDO
						.getDownloadId());
				DownloadDO downloadDO = (DownloadDO) downloads.get(key);
				if (downloadDO != null) {
					toCopy.append(downloadSourceDO.getFilename() + "|"
							+ downloadDO.getHash() + "|"
							+ downloadDO.getGroesse());
					copyToClipboard = true;
				}
			}
			if (copyToClipboard) {
				long port = ApplejuiceFassade.getInstance()
						.getAJSettings().getPort();
				Information information = ApplejuiceFassade
						.getInstance().getInformation();
				toCopy.append("|");
				toCopy.append(information.getExterneIP());
				toCopy.append(":");
				toCopy.append(port);
				if (information.getVerbindungsStatus() == Information.VERBUNDEN) {
					ServerDO serverDO = information.getServerDO();
					if (serverDO != null) {
						toCopy.append(":");
						toCopy.append(serverDO.getHost());
						toCopy.append(":");
						toCopy.append(serverDO.getPort());
					}
				}
				toCopy.append("/");
				StringSelection contents = new StringSelection(toCopy
						.toString());
				cb.setContents(contents, null);
			}
		}
	}
	
	private void copyToClipboard(){
		Object[] selectedItems = getSelectedDownloadItems();
		if (selectedItems != null && selectedItems.length == 1) {
			Clipboard cb = Toolkit.getDefaultToolkit()
					.getSystemClipboard();
			StringBuffer toCopy = new StringBuffer();
			toCopy.append("ajfsp://file|");
			if (selectedItems[0].getClass() == DownloadMainNode.class
					&& ((DownloadMainNode) selectedItems[0]).getType() == DownloadMainNode.ROOT_NODE) {
				DownloadDO downloadDO = ((DownloadMainNode) selectedItems[0])
						.getDownloadDO();
				toCopy.append(downloadDO.getFilename() + "|"
						+ downloadDO.getHash() + "|"
						+ downloadDO.getGroesse() + "/");
				StringSelection contents = new StringSelection(toCopy
						.toString());
				cb.setContents(contents, null);
			} else if (selectedItems[0].getClass() == DownloadSourceDO.class) {
				DownloadSourceDO downloadSourceDO = (DownloadSourceDO) selectedItems[0];
				Map downloads = ApplejuiceFassade.getInstance()
						.getDownloadsSnapshot();
				String key = Integer.toString(downloadSourceDO
						.getDownloadId());
				DownloadDO downloadDO = (DownloadDO) downloads.get(key);
				if (downloadDO != null) {
					toCopy.append(downloadSourceDO.getFilename() + "|"
							+ downloadDO.getHash() + "|"
							+ downloadDO.getGroesse() + "/");
					StringSelection contents = new StringSelection(
							toCopy.toString());
					cb.setContents(contents, null);
				}
			}
		}
	}

	private void downloadAbbrechen(){
		Object[] selectedItems = getSelectedDownloadItems();
		if (selectedItems != null && selectedItems.length != 0) {
			int result = JOptionPane.showConfirmDialog(AppleJuiceDialog
					.getApp(), downloadAbbrechen, dialogTitel,
					JOptionPane.YES_NO_OPTION);
			if (result == JOptionPane.YES_OPTION) {
				ArrayList indizesAbbrechen = new ArrayList();
				for (int i = 0; i < selectedItems.length; i++) {
					if (selectedItems[i].getClass() == DownloadMainNode.class) {
						DownloadDO downloadDO = ((DownloadMainNode) selectedItems[i])
								.getDownloadDO();
						indizesAbbrechen.add(new Integer(downloadDO
								.getId()));
					}
				}
				int size = indizesAbbrechen.size();
				if (size > 0) {
					final int[] abbrechen = new int[size];
					for (int i = 0; i < size; i++) {
						abbrechen[i] = ((Integer) indizesAbbrechen
								.get(i)).intValue();
					}
					new Thread() {
						public void run() {
							ApplejuiceFassade.getInstance()
									.cancelDownload(abbrechen);
							SoundPlayer.getInstance().playSound(
									SoundPlayer.ABGEBROCHEN);
						}
					}.start();
				}
			}
		}
	}

	public void componentSelected() {
		try {
			downloadPanel.getDownloadDOOverviewPanel().enableHoleListButton(false);
			panelSelected = true;
			if (!initialized) {
				initialized = true;
				int width = downloadPanel.getScrollPane().getWidth() - 18;
				PositionManager pm = PositionManagerImpl.getInstance();
				TableColumn[] columns = downloadPanel.getDownloadTableColumns();
				if (pm.isLegal()) {
					int[] widths = pm.getDownloadWidths();
					boolean[] visibilies = pm.getDownloadColumnVisibilities();
					int[] indizes = pm.getDownloadColumnIndizes();
					ArrayList visibleColumns = new ArrayList();
					for (int i = 0; i < columns.length; i++) {
						columns[i].setPreferredWidth(widths[i]);
						downloadPanel.getDownloadTable().
							removeColumn(columns[i]);
						if (visibilies[i]) {
							visibleColumns.add(columns[i]);
						}
					}
					int pos = -1;
					for (int i = 0; i < visibleColumns.size(); i++) {
						for (int x = 0; x < columns.length; x++) {
							if (visibleColumns.contains(columns[x])
									&& indizes[x] == pos + 1) {
								downloadPanel.getDownloadTable().addColumn(columns[x]);
								pos++;
								break;
							}
						}
					}
				} else {
					for (int i = 0; i < columns.length; i++) {
						columns[i].setPreferredWidth(width / columns.length);
					}
				}
				downloadPanel.getDownloadTable().setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
				int loc = (int) ((downloadPanel.getSplitPane().getHeight()
						- downloadPanel.getSplitPane().getDividerSize() - downloadPanel.getPowerDownloadPanel()
						.getPreferredSize().height));
				downloadPanel.getSplitPane().setDividerLocation(loc);
			}
			downloadPanel.getDownloadTable().updateUI();
		} catch (Exception e) {
			if (logger.isEnabledFor(Level.ERROR)) {
				logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
			}
		}
	}

	public void componentLostSelection() {
		panelSelected = false;
		downloadPartListWatcher.setDownloadNode(null);
		downloadPanel.getDownloadDOOverviewPanel().setDownloadDO(null);
	}

	protected void languageChanged() {
		LanguageSelector languageSelector = LanguageSelector.getInstance();
		String text = languageSelector
				.getFirstAttrbuteByTagName(".root.mainform.Label14.caption");
		dialogTitel = ZeichenErsetzer.korrigiereUmlaute(languageSelector
				.getFirstAttrbuteByTagName(".root.mainform.caption"));
		downloadAbbrechen = ZeichenErsetzer
				.korrigiereUmlaute(languageSelector
						.getFirstAttrbuteByTagName(".root.mainform.msgdlgtext5"));
		downloadPanel.getLblLink().setText(ZeichenErsetzer.korrigiereUmlaute(text));
		downloadPanel.getBtnStartDownload()
				.setText(ZeichenErsetzer
						.korrigiereUmlaute(languageSelector
								.getFirstAttrbuteByTagName(".root.mainform.downlajfsp.caption")));
		downloadPanel.getBtnStartDownload()
				.setToolTipText(ZeichenErsetzer
						.korrigiereUmlaute(languageSelector
								.getFirstAttrbuteByTagName(".root.mainform.downlajfsp.hint")));
		String[] tableColumns = new String[10];
		tableColumns[0] = ZeichenErsetzer
				.korrigiereUmlaute(languageSelector
						.getFirstAttrbuteByTagName(".root.mainform.queue.col0caption"));
		tableColumns[1] = ZeichenErsetzer
				.korrigiereUmlaute(languageSelector
						.getFirstAttrbuteByTagName(".root.mainform.queue.col1caption"));
		tableColumns[2] = ZeichenErsetzer
				.korrigiereUmlaute(languageSelector
						.getFirstAttrbuteByTagName(".root.mainform.queue.col2caption"));
		tableColumns[3] = ZeichenErsetzer
				.korrigiereUmlaute(languageSelector
						.getFirstAttrbuteByTagName(".root.mainform.queue.col3caption"));
		tableColumns[4] = ZeichenErsetzer
				.korrigiereUmlaute(languageSelector
						.getFirstAttrbuteByTagName(".root.mainform.queue.col4caption"));
		tableColumns[5] = ZeichenErsetzer
				.korrigiereUmlaute(languageSelector
						.getFirstAttrbuteByTagName(".root.mainform.queue.col5caption"));
		tableColumns[6] = ZeichenErsetzer
				.korrigiereUmlaute(languageSelector
						.getFirstAttrbuteByTagName(".root.mainform.queue.col6caption"));
		tableColumns[7] = ZeichenErsetzer
				.korrigiereUmlaute(languageSelector
						.getFirstAttrbuteByTagName(".root.mainform.queue.col7caption"));
		tableColumns[8] = ZeichenErsetzer
				.korrigiereUmlaute(languageSelector
						.getFirstAttrbuteByTagName(".root.mainform.queue.col8caption"));
		tableColumns[9] = ZeichenErsetzer
				.korrigiereUmlaute(languageSelector
						.getFirstAttrbuteByTagName(".root.mainform.queue.col9caption"));
		TableColumn[] columns = downloadPanel.getDownloadTableColumns();
		JCheckBoxMenuItem[] columnPopupItems = downloadPanel.getColumnPopupItems(); 
		for (int i = 0; i < columns.length; i++) {
			columns[i].setHeaderValue(tableColumns[i]);
			columnPopupItems[i].setText(tableColumns[i]);
		}
		DownloadMainNode.setColumnTitles(tableColumns);

		downloadPanel.getMnuAbbrechen()
				.setText(ZeichenErsetzer
						.korrigiereUmlaute(languageSelector
								.getFirstAttrbuteByTagName(".root.mainform.canceldown.caption")));
		downloadPanel.getMnuPause()
				.setText(ZeichenErsetzer
						.korrigiereUmlaute(languageSelector
								.getFirstAttrbuteByTagName(".root.mainform.pausedown.caption"))
						+ " [F5]");
		downloadPanel.getMnuFortsetzen()
				.setText(ZeichenErsetzer
						.korrigiereUmlaute(languageSelector
								.getFirstAttrbuteByTagName(".root.mainform.resumedown.caption"))
						+ " [F6]");
		downloadPanel.getMnuUmbenennen()
				.setText(ZeichenErsetzer
						.korrigiereUmlaute(languageSelector
								.getFirstAttrbuteByTagName(".root.mainform.renamefile.caption"))
						+ " [F2]");
		downloadPanel.getMnuZielordner()
				.setText(ZeichenErsetzer
						.korrigiereUmlaute(languageSelector
								.getFirstAttrbuteByTagName(".root.mainform.changetarget.caption"))
						+ " [F3]");
		downloadPanel.getMnuFertigeEntfernen()
				.setText(ZeichenErsetzer
						.korrigiereUmlaute(languageSelector
								.getFirstAttrbuteByTagName(".root.mainform.Clearfinishedentries1.caption")));
		downloadPanel.getMnuPartlisteAnzeigen()
				.setText(ZeichenErsetzer
						.korrigiereUmlaute(languageSelector
								.getFirstAttrbuteByTagName(".root.javagui.downloadform.partlisteanzeigen")));
		downloadPanel.getMnuCopyToClipboard()
				.setText(ZeichenErsetzer
						.korrigiereUmlaute(languageSelector
								.getFirstAttrbuteByTagName(".root.mainform.getlink1.caption")));
		downloadPanel.getMnuCopyToClipboardWithSources()
				.setText(ZeichenErsetzer
						.korrigiereUmlaute(languageSelector
								.getFirstAttrbuteByTagName(".root.javagui.downloadform.getlinkwithsources")));
		downloadPanel.getMnuEinfuegen()
				.setText(ZeichenErsetzer
						.korrigiereUmlaute(languageSelector
								.getFirstAttrbuteByTagName(".root.javagui.downloadform.einfuegen")));
		downloadPanel.getMnuOpenWithProgram().setText("VLC");
	}

	protected void contentChanged(int type, final Object content) {
		if (type == DataUpdateListener.DOWNLOAD_CHANGED) {
		    ApplejuiceFassade.getInstance().removeDataUpdateListener(this, DataUpdateListener.DOWNLOAD_CHANGED);
		    Map downloads = (Map) content;
			((DownloadRootNode) downloadPanel.getDownloadModel().getRoot()).setDownloadMap(downloads);
			DownloadDirectoryNode.setDownloads(downloads);
			if (panelSelected) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						try {
								downloadPanel.getDownloadTable().updateUI();
						} catch (Exception e) {
							if (logger.isEnabledFor(Level.ERROR)) {
								logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
							}
						}
					}
				});
			}
		}
	}

}
