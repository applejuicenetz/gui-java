package de.applejuicenet.client.gui.upload;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.apache.log4j.Level;

import de.applejuicenet.client.gui.RegisterI;
import de.applejuicenet.client.gui.components.GuiController;
import de.applejuicenet.client.gui.components.TklPanel;
import de.applejuicenet.client.gui.components.table.NormalHeaderRenderer;
import de.applejuicenet.client.gui.components.treetable.DefaultTreeTableCellRenderer;
import de.applejuicenet.client.gui.components.treetable.JTreeTable;
import de.applejuicenet.client.gui.controller.ApplejuiceFassade;
import de.applejuicenet.client.gui.controller.PositionManagerImpl;
import de.applejuicenet.client.gui.upload.table.UploadDataTableModel;
import de.applejuicenet.client.gui.upload.table.UploadTablePercentCellRenderer;
import de.applejuicenet.client.gui.upload.table.UploadTableVersionCellRenderer;
import de.applejuicenet.client.gui.upload.table.UploadTableWholeLoadedPercentCellRenderer;
import de.applejuicenet.client.gui.upload.table.UploadTreeTableCellRenderer;
import de.applejuicenet.client.shared.IconManager;

/**
 * $Header:
 * /cvsroot/applejuicejava/AJClientGUI/src/de/applejuicenet/client/gui/UploadPanel.java,v
 * 1.48 2004/06/23 13:31:24 maj0r Exp $
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
 * @author: Maj0r [aj@tkl-soft.de]
 *  
 */

public class UploadPanel extends TklPanel implements RegisterI {

	private static final long serialVersionUID = -636534546650240286L;

	private JTreeTable uploadDataTable;
	private JLabel uploadListeLabel = new JLabel("0 Clients in Deiner Uploadliste");
	private UploadDataTableModel uploadDataTableModel;
	private JPopupMenu popupMenu = new JPopupMenu();
	private JMenuItem itemCopyToClipboard;
	private JPopupMenu columnPopup = new JPopupMenu();
	private TableColumn[] columns = new TableColumn[8];
	private JCheckBoxMenuItem[] columnPopupItems = new JCheckBoxMenuItem[columns.length];

	public JTreeTable getTable(){
		return uploadDataTable;
	}

	public UploadDataTableModel getTableModel(){
		return uploadDataTableModel;
	}
	
	public TableColumn[] getTableColumns(){
		return columns;
	}
	
	public JCheckBoxMenuItem[] getColumnPopupItems(){
		return columnPopupItems;
	}
	
	public JPopupMenu getColumnPopup(){
		return columnPopup;
	}
	
	public JPopupMenu getPopup(){
		return popupMenu;
	}	
	public JLabel getUploadListeLabel(){
		return uploadListeLabel;
	}
	
	public JMenuItem getMnuCopyToClipboard(){
		return itemCopyToClipboard;
	}
	
	public UploadPanel(GuiController guiController) {
    	super(guiController);
		try {
			init();
		} catch (Exception ex) {
			if (logger.isEnabledFor(Level.ERROR)) {
				logger.error(ApplejuiceFassade.ERROR_MESSAGE, ex);
			}
		}
	}

	private void init() throws Exception {
		setLayout(new BorderLayout());
		uploadDataTableModel = new UploadDataTableModel();
		DefaultTreeTableCellRenderer uploadTreeTableCellRenderer =
			new UploadTreeTableCellRenderer(uploadDataTableModel);
		uploadDataTable = new JTreeTable(uploadDataTableModel, uploadTreeTableCellRenderer);
		uploadDataTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		uploadDataTable.getColumnModel().getColumn(4).setCellRenderer(
				new UploadTablePercentCellRenderer());
		uploadDataTable.getColumnModel().getColumn(5).setCellRenderer(
				new UploadTableWholeLoadedPercentCellRenderer());

		uploadDataTable.getColumnModel().getColumn(7).setCellRenderer(
				new UploadTableVersionCellRenderer());

		TableColumnModel model = uploadDataTable.getColumnModel();
		for (int i = 0; i < columns.length; i++) {
			columns[i] = model.getColumn(i);
			columnPopupItems[i] = new JCheckBoxMenuItem((String) columns[i]
					.getHeaderValue());
			final int x = i;
			columnPopupItems[i].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					if (columnPopupItems[x].isSelected()) {
						uploadDataTable.getColumnModel().addColumn(columns[x]);
						PositionManagerImpl.getInstance()
								.setUploadColumnVisible(x, true);
						PositionManagerImpl.getInstance().setUploadColumnIndex(
								x,
								uploadDataTable.getColumnModel()
										.getColumnIndex(
												columns[x].getIdentifier()));
					} else {
						uploadDataTable.getColumnModel().removeColumn(
								columns[x]);
						PositionManagerImpl.getInstance()
								.setUploadColumnVisible(x, false);
						for (int y = 0; y < columns.length; y++) {
							try {
								PositionManagerImpl.getInstance()
										.setUploadColumnIndex(y,
												uploadDataTable.getColumnModel()
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
		columnPopupItems[0].setSelected(true);

		JScrollPane aScrollPane = new JScrollPane(uploadDataTable);
		aScrollPane.setBackground(uploadDataTable.getBackground());
		aScrollPane.getViewport().setOpaque(false);
		add(aScrollPane, BorderLayout.CENTER);

		int n = model.getColumnCount();
		TableCellRenderer renderer = new NormalHeaderRenderer();
		for (int i = 0; i < n; i++) {
			model.getColumn(i).setHeaderRenderer(renderer);
		}

		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		panel.add(uploadListeLabel);
		JPanel panel2 = new JPanel();
		panel2.setLayout(new BorderLayout());
		panel2.add(panel, BorderLayout.WEST);
		add(panel2, BorderLayout.SOUTH);
		itemCopyToClipboard = new JMenuItem();
		itemCopyToClipboard.setIcon(IconManager.getInstance().getIcon(
				"clipboard"));
		popupMenu.add(itemCopyToClipboard);
	}

	public int[] getColumnWidths() {
		int[] widths = new int[columns.length];
		for (int i = 0; i < columns.length; i++) {
			widths[i] = columns[i].getWidth();
		}
		return widths;
	}
}