package de.applejuicenet.client.gui.upload;

import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.apache.log4j.Level;

import de.applejuicenet.client.gui.components.GuiController;
import de.applejuicenet.client.gui.components.GuiControllerActionListener;
import de.applejuicenet.client.gui.components.treetable.TreeTableModelAdapter;
import de.applejuicenet.client.gui.controller.ApplejuiceFassade;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.controller.PositionManager;
import de.applejuicenet.client.gui.controller.PositionManagerImpl;
import de.applejuicenet.client.gui.listener.DataUpdateListener;
import de.applejuicenet.client.shared.ZeichenErsetzer;
import de.applejuicenet.client.shared.dac.ShareDO;
import de.applejuicenet.client.shared.dac.UploadDO;

public class UploadController extends GuiController {

	private static UploadController instance = null;

	private static final int HEADER_DRAGGED = 0;
	private static final int HEADER_POPUP = 1;
	private static final int TABLE_MOUSE_CLICKED = 2;
	private static final int TABLE_POPUP = 3;
	private static final int COPY_TO_CLIPBOARD = 4;
	
	private final UploadPanel uploadPanel;

	private boolean componentSelected = false;
	private boolean initialized = false;
	private int anzahlClients = 0;
	private String clientText;
	private String warteschlangeVoll = "";

	private UploadController() {
		super();
		uploadPanel = new UploadPanel(this);
		try{
			init();
			LanguageSelector.getInstance().addLanguageListener(this);
			ApplejuiceFassade.getInstance().addDataUpdateListener(this,
					DataUpdateListener.UPLOAD_CHANGED);
		} catch (Exception e) {
			if (logger.isEnabledFor(Level.ERROR)) {
				logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
			}
		}
	}

	public static synchronized UploadController getInstance() {
		if (null == instance) {
			instance = new UploadController();
		}
		return instance;
	}

	private void init() {
		uploadPanel.getTable().getTableHeader().addMouseListener(
				new HeaderPopupListener(this, HEADER_POPUP));
		uploadPanel.getTable().getTableHeader().addMouseMotionListener(
				new UploadMouseMotionListener(this, HEADER_DRAGGED));
		uploadPanel.getTable().addMouseListener(
				new UploadTableMouseListener(this, TABLE_MOUSE_CLICKED)); 
		uploadPanel.getTable().addMouseListener(
				new UploadTablePopupListener(this, TABLE_POPUP));
		uploadPanel.getMnuCopyToClipboar().addActionListener(
				new GuiControllerActionListener(this, COPY_TO_CLIPBOARD));
	}

	public void fireAction(int actionId, Object obj) {
		switch (actionId){
			case HEADER_DRAGGED:{
				headerDragged();
				break;
			}
			case HEADER_POPUP:{
				headerPopup((MouseEvent)obj);
				break;
			}
			case TABLE_MOUSE_CLICKED:{
				tableMouseClicked((MouseEvent)obj);
				break;
			}
			case TABLE_POPUP:{
				tablePopup((MouseEvent)obj);
				break;
			}
			case COPY_TO_CLIPBOARD:{
				copyLinkToClipboard();
				break;
			}
			default:{
				logger.error("Unregistrierte EventId " + actionId);
			}
		}
	}
	
	private void headerDragged(){
		PositionManager pm = PositionManagerImpl.getInstance();
		TableColumnModel columnModel = uploadPanel.getTable().getColumnModel();
		TableColumn[] columns = uploadPanel.getTableColumns();
		for (int i = 0; i < columns.length; i++) {
			try {
				pm.setUploadColumnIndex(i, columnModel
						.getColumnIndex(columns[i].getIdentifier()));
			} catch (IllegalArgumentException niaE) {
				;
				//nix zu tun
			}
		}
	}
	
	private void tablePopup(MouseEvent e){
		Point p = e.getPoint();
		int selectedRow = uploadPanel.getTable().rowAtPoint(p);
		if (selectedRow != -1) {
			uploadPanel.getTable().setRowSelectionInterval(selectedRow,
					selectedRow);
			Object selectedItem = ((TreeTableModelAdapter) uploadPanel.getTable()
					.getModel()).nodeForRow(selectedRow);
			if (selectedItem.getClass() == UploadDO.class) {
				uploadPanel.getPopup().show(uploadPanel.getTable(), e.getX(), e.getY());
			}
		}
	}
	
	private void tableMouseClicked(MouseEvent e){
		Point p = e.getPoint();
		if (uploadPanel.getTable().columnAtPoint(p) != 0) {
			int selectedRow = uploadPanel.getTable().rowAtPoint(p);
			if (e.getClickCount() == 2) {
				((TreeTableModelAdapter) uploadPanel.getTable().getModel())
						.expandOrCollapseRow(selectedRow);
			}
		}
	}

	private void copyLinkToClipboard() {
		Object selectedItem = ((TreeTableModelAdapter) uploadPanel.getTable()
				.getModel()).nodeForRow(uploadPanel.getTable().getSelectedRow());
		if (selectedItem.getClass() == UploadDO.class) {
			String shareFileId = ((UploadDO) selectedItem)
					.getShareFileIDAsString();
			Map share = ApplejuiceFassade.getInstance().getShare(false);
			if (share.containsKey(shareFileId)) {
				ShareDO shareDO = (ShareDO) share.get(shareFileId);
				if (shareDO != null) {
					Clipboard cb = Toolkit.getDefaultToolkit()
							.getSystemClipboard();
					StringBuffer toCopy = new StringBuffer();
					toCopy.append("ajfsp://file|");
					toCopy.append(shareDO.getShortfilename() + "|"
							+ shareDO.getCheckSum() + "|" + shareDO.getSize()
							+ "/");
					StringSelection contents = new StringSelection(toCopy
							.toString());
					cb.setContents(contents, null);
				}
			}
		}
	}
	
	private void headerPopup(MouseEvent e){
		TableColumn[] columns = uploadPanel.getTableColumns();
		JCheckBoxMenuItem[] columnPopupItems = uploadPanel.getColumnPopupItems();
		TableColumnModel tableColumnModel = uploadPanel.getTable().getColumnModel();
		for (int i = 1; i < columns.length; i++) {
			try {
				tableColumnModel.getColumnIndex(columns[i].getIdentifier());
				columnPopupItems[i].setSelected(true);
			} catch (IllegalArgumentException niaE) {
				columnPopupItems[i].setSelected(false);
			}
		}
		uploadPanel.getColumnPopup().show(uploadPanel.getTable().getTableHeader(), 
				e.getX(), e.getY());
	}
	
	
	public JComponent getComponent() {
		return uploadPanel;
	}

	public void componentSelected() {
		try {
			componentSelected = true;
			if (!initialized) {
				initialized = true;
				TableColumnModel headerModel = uploadPanel.getTable().getTableHeader()
						.getColumnModel();
				TableColumn[] columns = uploadPanel.getTableColumns();
				int columnCount = headerModel.getColumnCount();
				PositionManager pm = PositionManagerImpl.getInstance();
				if (pm.isLegal()) {
					int[] widths = pm.getUploadWidths();
					boolean[] visibilies = pm.getUploadColumnVisibilities();
					int[] indizes = pm.getUploadColumnIndizes();
					ArrayList visibleColumns = new ArrayList();
					for (int i = 0; i < columns.length; i++) {
						columns[i].setPreferredWidth(widths[i]);
						uploadPanel.getTable().removeColumn(columns[i]);
						if (visibilies[i]) {
							visibleColumns.add(columns[i]);
						}
					}
					int pos = -1;
					for (int i = 0; i < visibleColumns.size(); i++) {
						for (int x = 0; x < columns.length; x++) {
							if (visibleColumns.contains(columns[x])
									&& indizes[x] == pos + 1) {
								uploadPanel.getTable().addColumn(columns[x]);
								pos++;
								break;
							}
						}
					}
				} else {
					for (int i = 0; i < columnCount; i++) {
						headerModel.getColumn(i).setPreferredWidth(
								uploadPanel.getTable().getWidth() / columnCount);
					}
				}
				uploadPanel.getTable().setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			}
			uploadPanel.getTable().updateUI();
		} catch (Exception ex) {
			if (logger.isEnabledFor(Level.ERROR)) {
				logger.error(ApplejuiceFassade.ERROR_MESSAGE, ex);
			}
		}
	}

	public void componentLostSelection() {
		componentSelected = false;
	}

	protected void languageChanged() {
		LanguageSelector languageSelector = LanguageSelector.getInstance();
		clientText = ZeichenErsetzer.korrigiereUmlaute(languageSelector
				.getFirstAttrbuteByTagName(".root.mainform.uplcounttext"));
		uploadPanel.getUploadListeLabel().setText(clientText.replaceAll("%d", Integer
				.toString(anzahlClients)));
		String[] columnsText = new String[8];
		columnsText[0] = ZeichenErsetzer
				.korrigiereUmlaute(languageSelector
						.getFirstAttrbuteByTagName(".root.mainform.uploads.col0caption"));
		columnsText[1] = ZeichenErsetzer
				.korrigiereUmlaute(languageSelector
						.getFirstAttrbuteByTagName(".root.mainform.uploads.col3caption"));
		columnsText[2] = ZeichenErsetzer
				.korrigiereUmlaute(languageSelector
						.getFirstAttrbuteByTagName(".root.mainform.uploads.col1caption"));
		columnsText[3] = ZeichenErsetzer
				.korrigiereUmlaute(languageSelector
						.getFirstAttrbuteByTagName(".root.mainform.uploads.col2caption"));
		columnsText[4] = ZeichenErsetzer.korrigiereUmlaute(languageSelector
				.getFirstAttrbuteByTagName(".root.mainform.queue.col6caption"));
		columnsText[5] = ZeichenErsetzer
				.korrigiereUmlaute(languageSelector
						.getFirstAttrbuteByTagName(".root.javagui.uploadform.columnwasserstand"));
		columnsText[6] = ZeichenErsetzer
				.korrigiereUmlaute(languageSelector
						.getFirstAttrbuteByTagName(".root.mainform.uploads.col4caption"));
		columnsText[7] = ZeichenErsetzer
				.korrigiereUmlaute(languageSelector
						.getFirstAttrbuteByTagName(".root.mainform.uploads.col5caption"));
		TableColumn[] columns = uploadPanel.getTableColumns();
		for (int i = 0; i < columns.length; i++) {
			columns[i].setHeaderValue(columnsText[i]);
		}
		columns[0].setPreferredWidth(100);
		warteschlangeVoll = ZeichenErsetzer.korrigiereUmlaute(languageSelector
						.getFirstAttrbuteByTagName(".root.javagui.downloadform.warteschlangevoll"));
		uploadPanel.getMnuCopyToClipboar().setText(ZeichenErsetzer
						.korrigiereUmlaute(languageSelector
								.getFirstAttrbuteByTagName(".root.mainform.getlink1.caption")));
	}

	protected void contentChanged(int type, final Object content) {
		if (type == DataUpdateListener.UPLOAD_CHANGED) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					try {
						uploadPanel.getTableModel().setTable((HashMap) content);
						if (componentSelected) {
							uploadPanel.getTable().updateUI();
						}
						anzahlClients = uploadPanel.getTableModel().getRowCount();
						String tmp = clientText.replaceAll("%d", Integer
								.toString(anzahlClients));
						long maxUploadPos = ApplejuiceFassade.getInstance()
								.getInformation().getMaxUploadPositions();
						if (anzahlClients >= maxUploadPos) {
							tmp += " (" + warteschlangeVoll + ")";
						}
						uploadPanel.getUploadListeLabel().setText(tmp);
					} catch (Exception ex) {
						if (logger.isEnabledFor(Level.ERROR)) {
							logger.error(ApplejuiceFassade.ERROR_MESSAGE, ex);
						}
					}
				}
			});
		}
	}
}