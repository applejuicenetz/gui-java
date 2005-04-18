package de.applejuicenet.client.gui.download;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.TableColumn;

import org.apache.log4j.Level;

import de.applejuicenet.client.AppleJuiceClient;
import de.applejuicenet.client.fassade.ApplejuiceFassade;
import de.applejuicenet.client.fassade.entity.Download;
import de.applejuicenet.client.fassade.entity.DownloadSource;
import de.applejuicenet.client.fassade.entity.Information;
import de.applejuicenet.client.fassade.entity.Server;
import de.applejuicenet.client.fassade.entity.Share;
import de.applejuicenet.client.fassade.event.DataPropertyChangeEvent;
import de.applejuicenet.client.fassade.event.DownloadDataPropertyChangeEvent;
import de.applejuicenet.client.fassade.exception.IllegalArgumentException;
import de.applejuicenet.client.fassade.shared.ZeichenErsetzer;
import de.applejuicenet.client.gui.AppleJuiceDialog;
import de.applejuicenet.client.gui.components.GuiController;
import de.applejuicenet.client.gui.components.GuiControllerActionListener;
import de.applejuicenet.client.gui.components.treetable.TreeTableModelAdapter;
import de.applejuicenet.client.gui.components.util.Value;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.controller.OptionsManagerImpl;
import de.applejuicenet.client.gui.controller.PositionManager;
import de.applejuicenet.client.gui.controller.PositionManagerImpl;
import de.applejuicenet.client.gui.download.table.DownloadDirectoryNode;
import de.applejuicenet.client.gui.download.table.DownloadMainNode;
import de.applejuicenet.client.gui.download.table.DownloadRootNode;
import de.applejuicenet.client.gui.options.IncomingDirSelectionDialog;
import de.applejuicenet.client.shared.Settings;
import de.applejuicenet.client.shared.SoundPlayer;

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
	private String dialogTitel;
	private String downloadAbbrechen;
	private DownloadPartListWatcher downloadPartListWatcher;
	private boolean firstUpdate = true;
	private boolean isFirstDownloadPropertyChanged = true;
	
	private String alreadyLoaded;
	private String invalidLink;
	private String linkFailure;
	
	private DownloadController() {
		super();
		downloadPanel = new DownloadPanel(this);
		try{
			init();
		} catch (Exception e) {
			logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
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
        if (AppleJuiceClient.getAjFassade().isLocalhost()){
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
					changeTargetDir();
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
		AppleJuiceClient.getAjFassade().getDownloadPropertyChangeInformer().
			addDataPropertyChangeListener(
				new DownloadPropertyChangeListener(this, DOWNLOAD_PROPERTY_CHANGE_EVENT));

		JComboBox targetDirs = downloadPanel.getTargetDirField();
		String[] dirs = AppleJuiceClient.getAjFassade().getCurrentIncomingDirs();
		for (String curDir : dirs) {
			targetDirs.addItem(curDir);
            if (curDir.equals("")) {
            	targetDirs.setSelectedItem(curDir);
            }
        }
		targetDirs.setEditable(true);
	}
	
	public Value[] getCustomizedValues(){
		return new Value[0];
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
				changeTargetDir();
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
		if (isFirstDownloadPropertyChanged){
			isFirstDownloadPropertyChanged = false;
			Map<String, Download> downloads = AppleJuiceClient.getAjFassade().getDownloadsSnapshot();
			((DownloadRootNode) downloadPanel.getDownloadModel().
                    getRoot()).setDownloadMap(downloads);
			DownloadDirectoryNode.setDownloads(downloads);
		}
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
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
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
	
	private boolean handleDownloadDataPropertyChangeEvent(DownloadDataPropertyChangeEvent event){
		if (event.getName() == null){
			return false;
		}
		else if (event.getName().equals(DownloadDataPropertyChangeEvent.DOWNLOAD_ADDED)
				|| event.getName().equals(DownloadDataPropertyChangeEvent.DOWNLOAD_REMOVED)
				|| event.getName().equals(DownloadDataPropertyChangeEvent.DIRECTORY_CHANGED)
				|| event.getName().equals(DownloadDataPropertyChangeEvent.FILENAME_CHANGED)){
			if (event.getName().equals(DownloadDataPropertyChangeEvent.DIRECTORY_CHANGED) 
					|| event.getName().equals(DownloadDataPropertyChangeEvent.DOWNLOAD_ADDED)){
				String directory;
				if (event.getNewValue() instanceof Download){
					directory = ((Download)event.getNewValue()).getTargetDirectory();
				}
				else{
					directory = (String)event.getNewValue();
				}
				if (directory != null && directory.length()>0){
					JComboBox targetDirs = downloadPanel.getTargetDirField();
					boolean found = false;
					for (int i=0; i<targetDirs.getItemCount(); i++){
						if (!((String)targetDirs.getItemAt(i)).equalsIgnoreCase(directory)){
							continue;
						}
						else{
							found = true;
							break;
						}
					}
					if (!found){
						targetDirs.addItem(directory);
					}
				}
			}
			return true;
		}
		return false;
	}
	
	private void clearReadyDownloads(){
		AppleJuiceClient.getAjFassade().cleanDownloadList();
		downloadPanel.getDownloadOverviewPanel().enableHoleListButton(false);
		downloadPanel.getPowerDownloadPanel().btnPdl.setEnabled(false);
		downloadPanel.getPowerDownloadPanel().setPwdlValue(0);
		downloadPanel.getDownloadTable().getSelectionModel().clearSelection();
	}
	
	private void nodeItemClicked(Object node){
		if (node.getClass() == DownloadMainNode.class
				&& ((DownloadMainNode) node).getType() == DownloadMainNode.ROOT_NODE) {
			Download download = ((DownloadMainNode) node)
					.getDownload();
			if (Settings.getSettings().isDownloadUebersicht()) {
				downloadPanel.getDownloadOverviewPanel().enableHoleListButton(false);
				tryGetPartList(false);
			} else {
				if ((download.getStatus() == Download.SUCHEN_LADEN || download
						.getStatus() == Download.PAUSIERT)) {
					downloadPanel.getDownloadOverviewPanel().enableHoleListButton(true);
				} else {
					downloadPanel.getDownloadOverviewPanel().enableHoleListButton(false);
				}
			}
			if (!downloadPanel.getPowerDownloadPanel().isAutomaticPwdlActive()) {
				downloadPanel.getPowerDownloadPanel().btnPdl.setEnabled(true);
				if (download.getStatus() == Download.SUCHEN_LADEN
						|| download.getStatus() == Download.PAUSIERT) {
					downloadPanel.getPowerDownloadPanel().setPwdlValue(download
							.getPowerDownload());
				} else {
					downloadPanel.getDownloadOverviewPanel().enableHoleListButton(false);
					downloadPanel.getPowerDownloadPanel().btnPdl.setEnabled(false);
					downloadPanel.getPowerDownloadPanel().setPwdlValue(0);
				}
			}
		} else if (node instanceof DownloadSource) {
			if (Settings.getSettings().isDownloadUebersicht()) {
				downloadPanel.getDownloadOverviewPanel().enableHoleListButton(false);
				tryGetPartList(false);
			} else {
				downloadPanel.getDownloadOverviewPanel().enableHoleListButton(true);
			}
			if (!downloadPanel.getPowerDownloadPanel().isAutomaticPwdlActive()) {
				downloadPanel.getPowerDownloadPanel().btnPdl.setEnabled(true);
				downloadPanel.getPowerDownloadPanel()
						.setPwdlValue(((DownloadSource) node)
								.getPowerDownload());
			}
		} else {
			downloadPanel.getPowerDownloadPanel().btnPdl.setEnabled(false);
			downloadPanel.getPowerDownloadPanel().setPwdlValue(0);
			downloadPanel.getDownloadOverviewPanel().enableHoleListButton(false);
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
                List<Download> temp = new Vector<Download>();
                for (int i = 0; i < selectedItems.length; i++) {
                    if (selectedItems[i].getClass() == DownloadMainNode.class) {
                        temp.add(( (DownloadMainNode) selectedItems[i]).getDownload());
                    }
                }
                AppleJuiceClient.getAjFassade().setPowerDownload(temp,
                    new Integer(powerDownload));
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
						|| (selectedItems[0] instanceof DownloadSource)) {
					downloadPanel.getMnuPartlisteAnzeigen().setVisible(true);
					downloadPanel.getMnuUmbenennen().setVisible(true);
					downloadPanel.getMnuZielordner().setVisible(true);
				}
				if (selectedItems[0] instanceof DownloadSource) {
					downloadPanel.getMnuUmbenennen().setVisible(true);
					downloadPanel.getMnuZielordner().setVisible(true);
				}
				if (selectedItems[0].getClass() == DownloadMainNode.class) {
					Download downloadDO = ((DownloadMainNode) selectedItems[0])
							.getDownload();
					if (downloadDO.getStatus() == Download.SUCHEN_LADEN) {
						laufend = true;
					} else if (downloadDO.getStatus() == Download.PAUSIERT) {
						pausiert = true;
					}
				}
                if (selectedItems[0] instanceof DownloadDirectoryNode) {
                    downloadPanel.getMnuZielordner().setVisible(true);
                }
			} else {
				for (int i = 0; i < selectedItems.length; i++) {
					if ((selectedItems[i].getClass() == DownloadMainNode.class && ((DownloadMainNode) selectedItems[i])
							.getType() == DownloadMainNode.ROOT_NODE)) {
						downloadPanel.getMnuZielordner().setVisible(true);
					}
					Download downloadDO;
					if (selectedItems[i].getClass() == DownloadMainNode.class) {
						downloadDO = ((DownloadMainNode) selectedItems[i])
								.getDownload();
						if (downloadDO.getStatus() == Download.SUCHEN_LADEN) {
							laufend = true;
						} else if (downloadDO.getStatus() == Download.PAUSIERT) {
							pausiert = true;
						}
					}
				}
			}
		}
		downloadPanel.getMnuPause().setEnabled(laufend);
		downloadPanel.getMnuFortsetzen().setEnabled(pausiert);
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
			} else if (selectedItems[0] instanceof DownloadSource) {
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
			return new Object[0];
		}
	}
	
	private void renameDownload() {
		Object[] selectedItems = getSelectedDownloadItems();
		if (selectedItems != null && selectedItems.length == 1) {
			if (selectedItems[0].getClass() == DownloadMainNode.class
					&& ((DownloadMainNode) selectedItems[0]).getType() == DownloadMainNode.ROOT_NODE) {

				Download download = ((DownloadMainNode) selectedItems[0])
						.getDownload();
				RenameDownloadDialog renameDownloadDialog = new RenameDownloadDialog(
						AppleJuiceDialog.getApp(), download);
				renameDownloadDialog.setVisible(true);
				String neuerName = renameDownloadDialog.getNewName();

				if (neuerName == null) {
					return;
				} else {
					if (download.getFilename().compareTo(neuerName) != 0) {
						try {
							AppleJuiceClient.getAjFassade().renameDownload(
									download, neuerName);
						} catch (IllegalArgumentException e) {
							logger.error(e);
						}
					}
				}
			}
		}
	}

	private void pausieren() {
		Object[] selectedItems = getSelectedDownloadItems();
		if (selectedItems != null && selectedItems.length != 0
				&& !downloadPanel.getPowerDownloadPanel().isAutomaticPwdlActive()) {
			final List<Download> pausieren = new Vector<Download>();
			for (int i = 0; i < selectedItems.length; i++) {
				if (selectedItems[i].getClass() == DownloadMainNode.class) {
					Download download = ((DownloadMainNode) selectedItems[i])
							.getDownload();
					if (download.getStatus() == Download.SUCHEN_LADEN) {
						pausieren.add(download);
					}
				}
			}
			if (pausieren.size() > 0) {
				new Thread() {
					public void run() {
						try {
							AppleJuiceClient.getAjFassade().pauseDownload(pausieren);
						} catch (IllegalArgumentException e) {
							logger.error(e);
						}
					}
				}.start();
			}
		}
	}

	private void fortsetzen() {
		Object[] selectedItems = getSelectedDownloadItems();
		if (selectedItems != null && selectedItems.length != 0
				&& !downloadPanel.getPowerDownloadPanel().isAutomaticPwdlActive()) {
			final List<Download> fortsetzen = new Vector<Download>();
			for (int i = 0; i < selectedItems.length; i++) {
				if (selectedItems[i].getClass() == DownloadMainNode.class) {
					Download download = ((DownloadMainNode) selectedItems[i])
							.getDownload();
					if (download.getStatus() == Download.PAUSIERT) {
						fortsetzen.add(download);
					}
				}
			}
			if (fortsetzen.size() > 0) {
				new Thread() {
					public void run() {
						try {
							AppleJuiceClient.getAjFassade().resumeDownload(fortsetzen);
						} catch (IllegalArgumentException e) {
							logger.error(e);
						}
					}
				}.start();
			}
		}
	}

	private void startDownload() {
        if (downloadPanel.getDownloadLinkField().isInvalid()){
         return;   
        }
		final String link = downloadPanel.getDownloadLinkField().getText();
		Object sel = downloadPanel.getTargetDirField().getSelectedItem();
		String tmp;
		if (sel != null){
			tmp = (String)sel;
		}
		else{
			tmp = "";
		}
		final String targetDir = tmp;
		if (link.length() != 0) {
			downloadPanel.getDownloadLinkField().setText("");
		    Thread linkThread = new Thread(){
		        public void run(){
					try {
						final String result = AppleJuiceClient.getAjFassade().processLink(link, targetDir);
						SoundPlayer.getInstance().playSound(SoundPlayer.LADEN);
						if (result.indexOf("ok") != 0){
						    SwingUtilities.invokeLater(new Runnable(){
						        public void run(){
						            String message = null;
									if (result.indexOf("already downloaded") != -1){
									    message = alreadyLoaded.replaceAll("%s", link);
									}
									else if (result.indexOf("incorrect link") != -1){
									    message = invalidLink.replaceAll("%s", link);
									}
									else if (result.indexOf("failure") != -1){
									    message = linkFailure;
									}
									if (message != null){
										JOptionPane.showMessageDialog(AppleJuiceDialog
												.getApp(), message, dialogTitel,
												JOptionPane.OK_OPTION | JOptionPane.INFORMATION_MESSAGE);
									}
						        }
						    });
						}
					} catch (IllegalArgumentException e) {
						logger.error(e);
					}
		        }
		    };
		    linkThread.start();
		}
	}

	private void changeTargetDir() {
		Object[] selectedItems = getSelectedDownloadItems();
		if (selectedItems == null || selectedItems.length == 0) {
			return;
		}
		String[] dirs = AppleJuiceClient.getAjFassade()
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
		List<Download> toChange = new Vector<Download>();
		Download download;
		for (int i = 0; i < selectedItems.length; i++) {
			if (selectedItems[i].getClass() == DownloadMainNode.class
					&& ((DownloadMainNode) selectedItems[i]).getType() == DownloadMainNode.ROOT_NODE) {
				download = ((DownloadMainNode) selectedItems[i])
						.getDownload();
				if (download.getTargetDirectory().compareTo(neuerName) != 0) {
					toChange.add(download);
				}
			}
            else if (selectedItems[i] instanceof DownloadDirectoryNode) {
                Object[] children = ((DownloadDirectoryNode)selectedItems[i]).getChildren();
                for (int x=0; x<children.length; x++){
                    if (children[x].getClass() == DownloadMainNode.class
                            && ((DownloadMainNode) children[x]).getType() 
                                == DownloadMainNode.ROOT_NODE) {
                        download = ((DownloadMainNode) children[x]).getDownload();
                        if (download.getTargetDirectory().compareTo(neuerName) != 0) {
                            toChange.add(download);
                        }
                    }
                }
            }
		}
		if (toChange.size()>0) {
			try {
				AppleJuiceClient.getAjFassade().setTargetDir(toChange, neuerName);
			} catch (IllegalArgumentException e) {
				logger.error(e);
			}
		}
	}
	
	private void openWithProgram(){
		Object[] selectedItems = getSelectedDownloadItems();
		if (selectedItems != null && selectedItems.length == 1) {
			Download download = null;
			if (selectedItems[0].getClass() == DownloadMainNode.class
					&& ((DownloadMainNode) selectedItems[0]).getType() == DownloadMainNode.ROOT_NODE) {
				download = ((DownloadMainNode) selectedItems[0])
						.getDownload();
			} else if (selectedItems[0] instanceof DownloadSource) {
				DownloadSource downloadSource = (DownloadSource) selectedItems[0];
				Map downloads = AppleJuiceClient.getAjFassade()
						.getDownloadsSnapshot();
				String key = Integer.toString(downloadSource
						.getDownloadId());
				download = (Download) downloads.get(key);
			}
			if (download != null) {
                String programToExecute = OptionsManagerImpl.getInstance().getOpenProgram();
                if (programToExecute.length() != 0){
					Integer shareId = new Integer(download.getShareId());
					try {
						Share share = (Share)AppleJuiceClient.getAjFassade().getObjectById(shareId);
						if (share != null){
						    String filename = share.getFilename();
							try {
								Runtime.getRuntime().exec(new String[] { programToExecute, filename });
							} catch (Exception ex) {
								//nix zu tun
							}
						}
					} catch (IllegalArgumentException e) {
						logger.error(e);
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
				Download downloadDO = ((DownloadMainNode) selectedItems[0])
						.getDownload();
				toCopy.append(downloadDO.getFilename() + "|"
						+ downloadDO.getHash() + "|"
						+ downloadDO.getGroesse());
				copyToClipboard = true;
			} else if (selectedItems[0] instanceof DownloadSource) {
				DownloadSource downloadSource = (DownloadSource) selectedItems[0];
				Map<String, Download> downloads = AppleJuiceClient.getAjFassade()
						.getDownloadsSnapshot();
				String key = Integer.toString(downloadSource
						.getDownloadId());
				Download download = downloads.get(key);
				if (download != null) {
					toCopy.append(downloadSource.getFilename() + "|"
							+ download.getHash() + "|"
							+ download.getGroesse());
					copyToClipboard = true;
				}
			}
			if (copyToClipboard) {
				long port = AppleJuiceClient.getAjFassade()
						.getAJSettings().getPort();
				Information information = AppleJuiceClient.getAjFassade().getInformation();
				toCopy.append("|");
				toCopy.append(information.getExterneIP());
				toCopy.append(":");
				toCopy.append(port);
				if (information.getVerbindungsStatus() == Information.VERBUNDEN) {
					Server server = information.getServer();
					if (server != null) {
						toCopy.append(":");
						toCopy.append(server.getHost());
						toCopy.append(":");
						toCopy.append(server.getPort());
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
				Download download = ((DownloadMainNode) selectedItems[0])
						.getDownload();
				toCopy.append(download.getFilename() + "|"
						+ download.getHash() + "|"
						+ download.getGroesse() + "/");
				StringSelection contents = new StringSelection(toCopy
						.toString());
				cb.setContents(contents, null);
			} else if (selectedItems[0] instanceof DownloadSource) {
				DownloadSource downloadSource = (DownloadSource) selectedItems[0];
				Map<String, Download> downloads = AppleJuiceClient.getAjFassade().getDownloadsSnapshot();
				String key = Integer.toString(downloadSource
						.getDownloadId());
				Download download = downloads.get(key);
				if (download != null) {
					toCopy.append(downloadSource.getFilename() + "|"
							+ download.getHash() + "|"
							+ download.getGroesse() + "/");
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
				final List<Download> abbrechen = new Vector<Download>();
				for (int i = 0; i < selectedItems.length; i++) {
					if (selectedItems[i].getClass() == DownloadMainNode.class) {
						Download download = ((DownloadMainNode) selectedItems[i])
								.getDownload();
						abbrechen.add(download);
					}
				}
				if (abbrechen.size() > 0) {
					new Thread() {
						public void run() {
							try {
								AppleJuiceClient.getAjFassade()
										.cancelDownload(abbrechen);
								SoundPlayer.getInstance().playSound(
										SoundPlayer.ABGEBROCHEN);
							} catch (IllegalArgumentException e) {
								logger.error(e);
							}
						}
					}.start();
				}
			}
		}
	}

	public void componentSelected() {
		try {
			downloadPanel.getDownloadOverviewPanel().enableHoleListButton(false);
			if (!initialized) {
				initialized = true;
		        firstUpdate = false;
				int width = downloadPanel.getScrollPane().getWidth() - 18;
				PositionManager pm = PositionManagerImpl.getInstance();
				TableColumn[] columns = downloadPanel.getDownloadTableColumns();
				if (pm.isLegal()) {
					int[] widths = pm.getDownloadWidths();
					boolean[] visibilies = pm.getDownloadColumnVisibilities();
					int[] indizes = pm.getDownloadColumnIndizes();
					ArrayList<TableColumn> visibleColumns = new ArrayList<TableColumn>();
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
			logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
		}
	}

	public void componentLostSelection() {
		downloadPartListWatcher.setDownloadNode(null);
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
		downloadPanel.getLblTargetDir().setText(ZeichenErsetzer
				.korrigiereUmlaute(languageSelector
						.getFirstAttrbuteByTagName(".root.javagui.downloadform.zielverzeichnis")));
		alreadyLoaded = ZeichenErsetzer.korrigiereUmlaute(languageSelector
				.getFirstAttrbuteByTagName(".root.javagui.downloadform.bereitsgeladen"));
		invalidLink = ZeichenErsetzer.korrigiereUmlaute(languageSelector
				.getFirstAttrbuteByTagName(".root.javagui.downloadform.falscherlink"));
		linkFailure = ZeichenErsetzer.korrigiereUmlaute(languageSelector
				.getFirstAttrbuteByTagName(".root.javagui.downloadform.sonstigerlinkfehlerlang"));
	}

	protected void contentChanged(DATALISTENER_TYPE type, final Object content) {
		// wird jetzt ueber die neuen Events verarbeitet
	}

}
