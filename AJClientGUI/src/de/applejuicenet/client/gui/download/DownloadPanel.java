package de.applejuicenet.client.gui.download;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import de.applejuicenet.client.fassade.ApplejuiceFassade;
import de.applejuicenet.client.gui.components.GuiController;
import de.applejuicenet.client.gui.components.TklPanel;
import de.applejuicenet.client.gui.components.table.SortButtonRenderer;
import de.applejuicenet.client.gui.components.treetable.DefaultTreeTableCellRenderer;
import de.applejuicenet.client.gui.components.treetable.JTreeTable;
import de.applejuicenet.client.gui.controller.PositionManager;
import de.applejuicenet.client.gui.controller.PositionManagerImpl;
import de.applejuicenet.client.gui.download.table.DownloadModel;
import de.applejuicenet.client.gui.download.table.DownloadRootNode;
import de.applejuicenet.client.gui.download.table.DownloadTableCellRenderer;
import de.applejuicenet.client.gui.download.table.DownloadTablePercentCellRenderer;
import de.applejuicenet.client.gui.download.table.DownloadTableVersionCellRenderer;
import de.applejuicenet.client.gui.download.table.DownloadTreeTable;
import de.applejuicenet.client.gui.download.table.DownloadTreeTableCellRenderer;
import de.applejuicenet.client.shared.IconManager;
import de.tklsoft.gui.controls.InvalidRule;
import de.tklsoft.gui.controls.ModifyableComponent;
import de.tklsoft.gui.controls.TKLComboBox;
import de.tklsoft.gui.controls.TKLTextField;
import de.tklsoft.gui.controls.StatusHolder.STATUSFLAG;

/**
 * $Header:
 * /cvsroot/applejuicejava/AJClientGUI/src/de/applejuicenet/client/gui/DownloadPanel.java,v
 * 1.109 2004/06/23 12:39:15 maj0r Exp $
 *
 * <p>
 * Titel: AppleJuice Client-GUI
 * </p>
 * <p>
 * Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten
 * appleJuice-Core
 * </p>
 * <p>
 * Copyright: General Public License
 * </p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 */

public class DownloadPanel extends TklPanel {

	private DownloadOverviewPanel downloadDOOverviewPanel;
	private TKLTextField downloadLink = new TKLTextField();
	private JButton btnStartDownload = new JButton();
	private PowerDownloadPanel powerDownloadPanel;
	private JTreeTable downloadTable;
	private JLabel linkLabel = new JLabel();
	private DownloadModel downloadModel;
	private JPopupMenu popup = new JPopupMenu();
	private JScrollPane aScrollPane;
	private JMenuItem abbrechen;
	private JMenuItem pause;
	private JMenuItem fortsetzen;
	private JMenuItem umbenennen;
	private JMenuItem zielordner;
	private JMenuItem fertigEntfernen;
	private JMenuItem itemCopyToClipboard = new JMenuItem();
	private JMenuItem itemCopyToClipboardWithSources = new JMenuItem();
    private JMenuItem itemOpenWithProgram = new JMenuItem();
	private JSplitPane splitPane;
	private JMenuItem partlisteAnzeigen;
	private Logger logger;
	private JPopupMenu columnPopup = new JPopupMenu();
	private TableColumn[] columns = new TableColumn[10];
	private JCheckBoxMenuItem[] columnPopupItems = new JCheckBoxMenuItem[columns.length];
	private JPopupMenu menu;
	private JMenuItem einfuegen;
	private TKLComboBox targetDir = new TKLComboBox();
	private JLabel txtTargetDir = new JLabel();
	
	public JTreeTable getDownloadTable(){
		return downloadTable;
	}
	
	public TableColumn[] getDownloadTableColumns(){
		return columns;
	}
	
	public TKLTextField getDownloadLinkField(){
		return downloadLink;
	}

	public TKLComboBox getTargetDirField(){
		return targetDir;
	}

	public JLabel getLblTargetDir(){
		return txtTargetDir;
	}
	
	public JCheckBoxMenuItem[] getColumnPopupItems(){
		return columnPopupItems;
	}
	
	public DownloadOverviewPanel getDownloadDOOverviewPanel(){
		return downloadDOOverviewPanel;
	}
	
	public JScrollPane getScrollPane(){
		return aScrollPane;
	}

	public JSplitPane getSplitPane(){
		return splitPane;
	}
	
	public PowerDownloadPanel getPowerDownloadPanel(){
		return powerDownloadPanel;
	}
	
	public DownloadModel getDownloadModel(){
		return downloadModel;
	}
	
	public JLabel getLblLink(){
		return linkLabel;
	}
	
	public JButton getBtnStartDownload(){
		return btnStartDownload;
	}

	public JButton getBtnPowerDownload(){
		return powerDownloadPanel.btnPdl;
	}

	public JRadioButton getBtnPowerDownloadInaktiv(){
		return powerDownloadPanel.btnInaktiv;
	}

	public JRadioButton getBtnPowerDownloadAktiv(){
		return powerDownloadPanel.btnAktiv;
	}

	public TKLTextField getRatioField(){
		return powerDownloadPanel.ratio;
	}
	
	public JMenuItem getMnuAbbrechen(){
		return abbrechen;
	}

	public JMenuItem getMnuPause(){
		return pause;
	}

	public JMenuItem getMnuFortsetzen(){
		return fortsetzen;
	}

	public JMenuItem getMnuUmbenennen(){
		return umbenennen;
	}

	public JMenuItem getMnuZielordner(){
		return zielordner;
	}

	public JMenuItem getMnuFertigeEntfernen(){
		return fertigEntfernen;
	}

	public JMenuItem getMnuPartlisteAnzeigen(){
		return partlisteAnzeigen;
	}

	public JMenuItem getMnuOpenWithProgram(){
		return itemOpenWithProgram;
	}

	public JMenuItem getMnuCopyToClipboard(){
		return itemCopyToClipboard;
	}

	public JMenuItem getMnuCopyToClipboardWithSources(){
		return itemCopyToClipboardWithSources;
	}

	public JMenuItem getMnuEinfuegen(){
		return einfuegen;
	}
	
	public JPopupMenu getPopup(){
		return popup;
	}
	
    public JButton getBtnHoleListe(){
    	return downloadDOOverviewPanel.getBtnHoleListe();
    }
	
	public DownloadPanel(GuiController guiController) {
		super(guiController);
		try {
			downloadDOOverviewPanel = new DownloadOverviewPanel(this);
			powerDownloadPanel = new PowerDownloadPanel((DownloadController)guiController);
			init();
		} catch (Exception e) {
			if (logger.isEnabledFor(Level.ERROR)) {
				logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
			}
		}
	}

	private void init() throws Exception {
		targetDir.setMinimumSize(new Dimension(120, targetDir.getMinimumSize().height));
		targetDir.setPreferredSize(new Dimension(120, targetDir.getPreferredSize().height));
		setLayout(new BorderLayout());
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new GridBagLayout());
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new BorderLayout());

		abbrechen = new JMenuItem();
		pause = new JMenuItem();
		fortsetzen = new JMenuItem();
		umbenennen = new JMenuItem();
		zielordner = new JMenuItem();
		fertigEntfernen = new JMenuItem();
		partlisteAnzeigen = new JMenuItem();

		menu = new JPopupMenu();
		einfuegen = new JMenuItem();

		IconManager im = IconManager.getInstance();
		abbrechen.setIcon(im.getIcon("abbrechen"));
		pause.setIcon(im.getIcon("pause"));
		umbenennen.setIcon(im.getIcon("umbenennen"));
		zielordner.setIcon(im.getIcon("zielordner"));
		fertigEntfernen.setIcon(im.getIcon("bereinigen"));
		partlisteAnzeigen.setIcon(im.getIcon("partliste"));
		fortsetzen.setIcon(im.getIcon("pause"));
		itemCopyToClipboard.setIcon(im.getIcon("clipboard"));
		itemCopyToClipboardWithSources.setIcon(im.getIcon("clipboard"));
		einfuegen.setIcon(im.getIcon("clipboard"));

        popup.add(fortsetzen);
		popup.add(pause);
        popup.add(abbrechen);
        popup.add(new JSeparator());
		popup.add(umbenennen);
		popup.add(zielordner);
        popup.add(new JSeparator());
		popup.add(fertigEntfernen);
        popup.add(new JSeparator());
		popup.add(itemCopyToClipboard);
		popup.add(itemCopyToClipboardWithSources);
        popup.add(new JSeparator());
		popup.add(partlisteAnzeigen);
		partlisteAnzeigen.setVisible(false);
    	popup.add(itemOpenWithProgram);
        itemOpenWithProgram.setIcon(im.getIcon("vlc"));

		einfuegen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				linkMenuActionPerformed(e);
			}
		});
		menu.add(einfuegen);

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.NORTH;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 3;
		constraints.gridheight = 1;

		
		GridBagConstraints constraints2 = new GridBagConstraints();
		constraints2.anchor = GridBagConstraints.NORTH;
		constraints2.fill = GridBagConstraints.BOTH;
		constraints2.gridx = 0;
		constraints2.gridy = 0;
		
		JPanel tempPanel = new JPanel(new GridBagLayout());
		tempPanel.add(linkLabel, constraints2);
		constraints2.gridx = 1;
		constraints2.weightx = 1;
		tempPanel.add(downloadLink, constraints2);
		constraints2.gridx = 2;
		constraints2.weightx = 0;
		tempPanel.add(txtTargetDir, constraints2);
		constraints2.gridx = 3;
		tempPanel.add(targetDir, constraints2);
		constraints2.gridx = 4;
		tempPanel.add(btnStartDownload, constraints2);
		topPanel.add(tempPanel, constraints);
		constraints.gridwidth = 3;
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.weighty = 1;
		constraints.weightx = 1;

		KeyListener keyListener = new KeyAdapter() {
			public void keyPressed(KeyEvent ke) {
				if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
					btnStartDownload.doClick();
				}
			}
		};
		downloadLink.addKeyListener(keyListener);
        InvalidRule downloadloadlinkRule = new InvalidRule(){
            public boolean isInvalid(ModifyableComponent component){
                String text = ((TKLTextField) component).getText().toLowerCase();
                if (text.length() == 0){
                    return false;   
                }
                if (!text.startsWith("ajfsp://")){
                    return true;   
                }
                text = text.substring("ajfsp://".length());
                if (!text.startsWith("file") && !text.startsWith("server")){
                    return true;   
                }
                int count;
                if (text.startsWith("file")){
                    count = 3;
                }
                else{
                    count = 2;
                }
                for (int i=0; i<text.length(); i++){
                   if (text.charAt(i)== '|'){
                    count--;
                   }
                }
                if (count > 0){
                    return true;
                }
                return false;
            }
        };
        downloadLink.ignoreStatus(STATUSFLAG.MODIFIED, true);
        downloadLink.addInvalidRule(downloadloadlinkRule);
        downloadLink.ignoreInvalidRules(false);
        
        targetDir.ignoreStatus(STATUSFLAG.MODIFIED, true);
        InvalidRule targetDirRule = new InvalidRule(){
            public boolean isInvalid(ModifyableComponent component){
                Object obj = ((TKLComboBox) component).getSelectedItem();
                if (obj == null){
                    return false;   
                }
                String subdir = (String) obj;
                if (subdir.indexOf(File.separator) != -1
                        || subdir.indexOf(ApplejuiceFassade.separator) != -1
                        || subdir.indexOf("..") != -1
                        || subdir.indexOf(":") != -1) {
                    return true;
                }
                return false;
            }
        };
        targetDir.addInvalidRule(targetDirRule);
        targetDir.addKeyListener(keyListener);
        
		downloadLink.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					showLinkMenu(e.getX(), e.getY());
				}
			}
		});

		downloadModel = new DownloadModel();
		DefaultTreeTableCellRenderer defaultTreeTableCellRenderer =
			new DownloadTreeTableCellRenderer(downloadModel);
		downloadTable = new DownloadTreeTable(downloadModel, defaultTreeTableCellRenderer);

		TableColumnModel model = downloadTable.getColumnModel();
		for (int i = 0; i < columns.length; i++) {
			columns[i] = model.getColumn(i);
			columnPopupItems[i] = new JCheckBoxMenuItem((String) columns[i]
					.getHeaderValue());
			final int x = i;
			columnPopupItems[i].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					if (columnPopupItems[x].isSelected()) {
						downloadTable.getColumnModel().addColumn(columns[x]);
						PositionManagerImpl.getInstance()
								.setDownloadColumnVisible(x, true);
						PositionManagerImpl
								.getInstance()
								.setDownloadColumnIndex(
										x,
										downloadTable
												.getColumnModel()
												.getColumnIndex(
														columns[x]
																.getIdentifier()));
					} else {
						downloadTable.getColumnModel().removeColumn(columns[x]);
						PositionManagerImpl.getInstance()
								.setDownloadColumnVisible(x, false);
						for (int y = 0; y < columns.length; y++) {
							try {
								PositionManagerImpl
										.getInstance()
										.setDownloadColumnIndex(
												y,
												downloadTable
														.getColumnModel()
														.getColumnIndex(
																columns[y]
																		.getIdentifier()));
							} catch (IllegalArgumentException niaE) {
								;
								//nix zu tun
							}
						}
					}
				}
			});
			columnPopup.add(columnPopupItems[i]);
		}
		columnPopupItems[0].setEnabled(false);

		downloadTable.getColumnModel().getColumn(1).setCellRenderer(
				new DownloadTableCellRenderer());
		downloadTable.getColumnModel().getColumn(2).setCellRenderer(
				new DownloadTableCellRenderer());
		downloadTable.getColumnModel().getColumn(3).setCellRenderer(
				new DownloadTableCellRenderer());
		downloadTable.getColumnModel().getColumn(4).setCellRenderer(
				new DownloadTableCellRenderer());
		downloadTable.getColumnModel().getColumn(5).setCellRenderer(
				new DownloadTableCellRenderer());
		downloadTable.getColumnModel().getColumn(6).setCellRenderer(
				new DownloadTablePercentCellRenderer());
		downloadTable.getColumnModel().getColumn(7).setCellRenderer(
				new DownloadTableCellRenderer());
		downloadTable.getColumnModel().getColumn(8).setCellRenderer(
				new DownloadTableCellRenderer());
		downloadTable.getColumnModel().getColumn(9).setCellRenderer(
				new DownloadTableVersionCellRenderer());
		JTableHeader header = downloadTable.getTableHeader();

		aScrollPane = new JScrollPane(downloadTable);
		aScrollPane.setBackground(downloadTable.getBackground());
		downloadTable.getTableHeader().setBackground(
				downloadTable.getBackground());
		aScrollPane.getViewport().setOpaque(false);
		topPanel.add(aScrollPane, constraints);

		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.weighty = 1;

		bottomPanel.add(powerDownloadPanel, BorderLayout.WEST);
		bottomPanel.add(downloadDOOverviewPanel, BorderLayout.CENTER);

		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topPanel,
				bottomPanel);
		splitPane.setBorder(null);
		add(splitPane, BorderLayout.CENTER);

		SortButtonRenderer renderer2 = new SortButtonRenderer();
		int n = model.getColumnCount();
		for (int i = 0; i < n; i++) {
			model.getColumn(i).setHeaderRenderer(renderer2);
		}
		header.addMouseListener(new SortMouseAdapter(header, renderer2));
		header.addMouseListener(new HeaderPopupListener());
		header.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e) {
				PositionManager pm = PositionManagerImpl.getInstance();
				TableColumnModel columnModel = downloadTable.getColumnModel();
				for (int i = 0; i < columns.length; i++) {
					try {
						pm.setDownloadColumnIndex(i, columnModel
								.getColumnIndex(columns[i].getIdentifier()));
					} catch (IllegalArgumentException niaE) {
						;
						//nix zu tun
					}
				}
			}
		});
		downloadTable
				.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
	}

	public int[] getColumnWidths() {
		int[] widths = new int[columns.length];
		for (int i = 0; i < columns.length; i++) {
			widths[i] = columns[i].getWidth();
		}
		return widths;
	}

	private void showLinkMenu(int x, int y) {
		menu.show(downloadLink, x, y);
	}

	private void linkMenuActionPerformed(ActionEvent e) {
		Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable transferable = cb.getContents(this);
		if (transferable != null) {
			String data = null;
			try {
				data = (String) transferable
						.getTransferData(DataFlavor.stringFlavor);
				downloadLink.setText(data);
			} catch (Exception ex) {
				downloadLink.setText("Error");
			}
		}
	}

	class SortMouseAdapter extends MouseAdapter {
		private JTableHeader header;
		private SortButtonRenderer renderer;

		public SortMouseAdapter(JTableHeader header, SortButtonRenderer renderer) {
			this.header = header;
			this.renderer = renderer;
		}

		public void mouseClicked(MouseEvent e) {
			if (e.getButton() != MouseEvent.BUTTON1) {
				return;
			}
			int col = header.columnAtPoint(e.getPoint());
			if (col == -1) {
				return;
			}
			TableColumn pressedColumn = downloadTable.getColumnModel()
					.getColumn(col);
			if (pressedColumn == columns[9]) {
				return;
			}
			renderer.setPressedColumn(col);
			renderer.setSelectedColumn(col);
			header.repaint();

			if (header.getTable().isEditing()) {
				header.getTable().getCellEditor().stopCellEditing();
			}

			boolean isAscent;
			if (SortButtonRenderer.UP == renderer.getState(col)) {
				isAscent = true;
			} else {
				isAscent = false;
			}

			DownloadRootNode rootNode = ((DownloadRootNode) downloadModel
					.getRoot());

			if (pressedColumn == columns[0]) {
				rootNode.setSortCriteria(DownloadRootNode.SORT_DOWNLOADNAME,
						isAscent);
			} else if (pressedColumn == columns[1]) {
				rootNode
						.setSortCriteria(DownloadRootNode.SORT_STATUS, isAscent);
			} else if (pressedColumn == columns[2]) {
				rootNode.setSortCriteria(DownloadRootNode.SORT_GROESSE,
						isAscent);
			} else if (pressedColumn == columns[3]) {
				rootNode.setSortCriteria(DownloadRootNode.SORT_BEREITS_GELADEN,
						isAscent);
			} else if (pressedColumn == columns[4]) {
				rootNode.setSortCriteria(DownloadRootNode.SORT_GESCHWINDIGKEIT,
						isAscent);
			} else if (pressedColumn == columns[5]) {
				rootNode.setSortCriteria(DownloadRootNode.SORT_RESTZEIT,
						isAscent);
			} else if (pressedColumn == columns[6]) {
				rootNode.setSortCriteria(DownloadRootNode.SORT_PROZENT,
						isAscent);
			} else if (pressedColumn == columns[7]) {
				rootNode.setSortCriteria(DownloadRootNode.SORT_REST_ZU_LADEN,
						isAscent);
			} else if (pressedColumn == columns[8]) {
				rootNode.setSortCriteria(DownloadRootNode.SORT_PWDL, isAscent);
			}
			downloadTable.updateUI();
			renderer.setPressedColumn(-1);
			header.repaint();
		}
	}

	class HeaderPopupListener extends MouseAdapter {
		private TableColumnModel model;

		public HeaderPopupListener() {
			model = downloadTable.getColumnModel();
			columnPopupItems[0].setSelected(true);
		}

		public void mousePressed(MouseEvent me) {
			super.mousePressed(me);
			maybeShowPopup(me);
		}

		public void mouseReleased(MouseEvent e) {
			super.mouseReleased(e);
			maybeShowPopup(e);
		}

		private void maybeShowPopup(MouseEvent e) {
			if (e.isPopupTrigger()) {
				for (int i = 1; i < columns.length; i++) {
					try {
						model.getColumnIndex(columns[i].getIdentifier());
						columnPopupItems[i].setSelected(true);
					} catch (IllegalArgumentException niaE) {
						columnPopupItems[i].setSelected(false);
					}
				}
				columnPopup.show(downloadTable.getTableHeader(), e.getX(), e
						.getY());
			}
		}
	}
}
