package de.applejuicenet.client.gui.server;

import java.util.HashMap;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import de.applejuicenet.client.gui.AppleJuiceDialog;
import de.applejuicenet.client.gui.RegisterI;
import de.applejuicenet.client.gui.components.table.HeaderListener;
import de.applejuicenet.client.gui.components.table.SortButtonRenderer;
import de.applejuicenet.client.gui.controller.ApplejuiceFassade;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.controller.PositionManager;
import de.applejuicenet.client.gui.controller.PositionManagerImpl;
import de.applejuicenet.client.gui.listener.DataUpdateListener;
import de.applejuicenet.client.gui.listener.LanguageListener;
import de.applejuicenet.client.gui.server.table.ServerTableCellRenderer;
import de.applejuicenet.client.gui.server.table.ServerTableModel;
import de.applejuicenet.client.shared.IconManager;
import de.applejuicenet.client.shared.Information;
import de.applejuicenet.client.shared.NetworkInfo;
import de.applejuicenet.client.shared.SoundPlayer;
import de.applejuicenet.client.shared.ZeichenErsetzer;
import de.applejuicenet.client.shared.dac.ServerDO;

/**
 * $Header:
 * /cvsroot/applejuicejava/AJClientGUI/src/de/applejuicenet/client/gui/ServerPanel.java,v
 * 1.57 2004/06/11 09:24:30 maj0r Exp $
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

public class ServerPanel extends JPanel implements LanguageListener,
		DataUpdateListener, RegisterI {

	private static final long serialVersionUID = -4783164086221861433L;

	private static ServerPanel instance;

	private JTable serverTable;
	private JButton sucheServer = new JButton();
	private JPopupMenu popup = new JPopupMenu();
	private JPopupMenu popup2 = new JPopupMenu();
	private JPopupMenu popup3 = new JPopupMenu();
	private JMenuItem verbinden;
	private JMenuItem loeschen1;
	private JMenuItem hinzufuegen2;
	private JMenuItem hinzufuegen3;
	private JMenuItem hinzufuegen1;
	private JMenuItem loeschen2;
	private JMenuItem itemCopyToClipboard;
	private JLabel verbunden = new JLabel();
	private JLabel versucheZuVerbinden = new JLabel();
	private JLabel aelter24h = new JLabel();
	private JLabel juenger24h = new JLabel();
	private Logger logger;
	private boolean initialized = false;
	private String warnungTitel;
	private String warnungNachricht;

	public static synchronized ServerPanel getInstance() {
		if (instance == null) {
			instance = new ServerPanel();
		}
		return instance;
	}

	private ServerPanel() {
		logger = Logger.getLogger(getClass());
		try {
			init();
		} catch (Exception e) {
			if (logger.isEnabledFor(Level.ERROR)) {
				logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
			}
		}
	}

	private void init() throws Exception {
		setLayout(new BorderLayout());
		LanguageSelector.getInstance().addLanguageListener(this);

		sucheServer.setForeground(Color.BLUE);
		IconManager im = IconManager.getInstance();
		verbinden = new JMenuItem();
		verbinden.setIcon(im.getIcon("serververbinden"));
		hinzufuegen1 = new JMenuItem();
		hinzufuegen1.setIcon(im.getIcon("serverneu"));
		loeschen1 = new JMenuItem();
		loeschen1.setIcon(im.getIcon("serverloeschen"));
		hinzufuegen2 = new JMenuItem();
		hinzufuegen2.setIcon(im.getIcon("serverneu"));
		hinzufuegen3 = new JMenuItem();
		hinzufuegen3.setIcon(im.getIcon("serverneu"));
		loeschen2 = new JMenuItem();
		loeschen2.setIcon(im.getIcon("serverloeschen"));
		itemCopyToClipboard = new JMenuItem();
		itemCopyToClipboard.setIcon(im.getIcon("clipboard"));

		popup.add(verbinden);
		popup.add(hinzufuegen3);
		popup.add(itemCopyToClipboard);
		popup.add(loeschen1);
		popup2.add(hinzufuegen2);
		popup3.add(hinzufuegen1);
		popup3.add(loeschen2);
		verbinden.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				int selected = serverTable.getSelectedRow();
				ServerDO server = (ServerDO) ((ServerTableModel) serverTable
						.getModel()).getRow(selected);
				ApplejuiceFassade af = ApplejuiceFassade.getInstance();
				if (af.getInformation().getVerbindungsStatus() == Information.VERBUNDEN) {
					NetworkInfo netInfo = af.getNetworkInfo();
					long timestamp = af.getLastCoreTimestamp();
					if (timestamp == 0) {
						/*
						 * Es wurden noch keine Referenzdaten geholt. Wir nehmen
						 * die eigene Zeit, in der Hoffnung, dass die
						 * uebereinstimmen.
						 */
						timestamp = System.currentTimeMillis();
					}
					long timeDiff = timestamp - netInfo.getConnectionTime();
					int minuten = (int) (timeDiff / 60000);
					if (minuten < 0) {
						minuten = 0;
					}
					if (minuten <= 30) {
						String tmp = warnungNachricht.replaceAll("%s", Integer
								.toString(minuten));
						int result = JOptionPane.showConfirmDialog(
								AppleJuiceDialog.getApp(), tmp, warnungTitel,
								JOptionPane.YES_NO_OPTION);
						if (result != JOptionPane.YES_OPTION) {
							return;
						}
					}
				}
				ApplejuiceFassade.getInstance().connectToServer(server.getID());
				SoundPlayer.getInstance().playSound(SoundPlayer.VERBINDEN);
			}
		});
		ActionListener loescheServerListener = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				int selected[] = serverTable.getSelectedRows();
				if (selected.length > 0) {
					ServerDO server = null;
					for (int i = 0; i < selected.length; i++) {
						server = (ServerDO) ((ServerTableModel) serverTable
								.getModel()).getRow(selected[i]);
						if (server != null) {
							ApplejuiceFassade.getInstance().entferneServer(
									server.getID());
						}
					}
				}
			}
		};
		ActionListener clipboardListener = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				int[] selectedItems = serverTable.getSelectedRows();
				if (selectedItems.length == 1) {
					Clipboard cb = Toolkit.getDefaultToolkit()
							.getSystemClipboard();
					StringBuffer toCopy = new StringBuffer();
					toCopy.append("ajfsp://server|");
					ServerDO serverDO = (ServerDO) ((ServerTableModel) serverTable
							.getModel()).getRow(selectedItems[0]);
					toCopy.append(serverDO.getHost());
					toCopy.append("|");
					toCopy.append(serverDO.getPort());
					toCopy.append("/");
					StringSelection contents = new StringSelection(toCopy
							.toString());
					cb.setContents(contents, null);
				}
			}
		};
		itemCopyToClipboard.addActionListener(clipboardListener);
		loeschen1.addActionListener(loescheServerListener);
		loeschen2.addActionListener(loescheServerListener);
		ActionListener newServerListener = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				NewServerDialog newServerDialog = new NewServerDialog(
						AppleJuiceDialog.getApp(), true);
				Dimension appDimension = newServerDialog.getSize();
				Dimension screenSize = Toolkit.getDefaultToolkit()
						.getScreenSize();
				newServerDialog.setLocation(
						(screenSize.width - appDimension.width) / 2,
						(screenSize.height - appDimension.height) / 2);
				newServerDialog.setVisible(true);
				if (newServerDialog.isLegal()) {
					final String link = newServerDialog.getLink();
					new Thread() {
						public void run() {
							ApplejuiceFassade.getInstance().processLink(link);
						}
					}.start();
				}
			}
		};
		hinzufuegen2.addActionListener(newServerListener);
		hinzufuegen3.addActionListener(newServerListener);
		hinzufuegen1.addActionListener(newServerListener);

		JPanel panel1 = new JPanel();
		panel1.setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.NORTH;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridx = 0;
		constraints.gridy = 0;

		sucheServer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				Thread worker = new Thread() {
					public void run() {
						ApplejuiceFassade af = ApplejuiceFassade.getInstance();
						String[] server = af.getNetworkKnownServers();
						if (server == null || server.length == 0) {
							return;
						}
						for (int i = 0; i < server.length; i++) {
							af.processLink(server[i]);
						}
					}
				};
				worker.start();
			}
		});
		panel1.add(sucheServer, constraints);
		constraints.gridx = 1;
		constraints.weightx = 1;
		panel1.add(new JLabel(), constraints);
		add(panel1, BorderLayout.NORTH);
		serverTable = new JTable();
		serverTable.setModel(new ServerTableModel());
		serverTable.setShowGrid(false);
		serverTable
				.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		SortButtonRenderer renderer = new SortButtonRenderer();
		TableColumnModel model = serverTable.getColumnModel();
		int n = model.getColumnCount();
		for (int i = 0; i < n; i++) {
			model.getColumn(i).setHeaderRenderer(renderer);
			model.getColumn(i).setPreferredWidth(model.getColumn(i).getWidth());
		}

		JTableHeader header = serverTable.getTableHeader();
		header.addMouseListener(new HeaderListener(header, renderer));

		TableColumn tc = serverTable.getColumnModel().getColumn(0);
		tc.setCellRenderer(new ServerTableCellRenderer());
		final JScrollPane aScrollPane = new JScrollPane(serverTable);
		aScrollPane.setBackground(serverTable.getBackground());
		serverTable.getTableHeader().setBackground(serverTable.getBackground());
		aScrollPane.getViewport().setOpaque(false);
		MouseAdapter popupMouseAdapter = new MouseAdapter() {
			public void mousePressed(MouseEvent me) {
				super.mouseReleased(me);
				maybeShowPopup(me);
			}

			public void mouseReleased(MouseEvent e) {
				super.mouseReleased(e);
				maybeShowPopup(e);
			}

			private void maybeShowPopup(MouseEvent e) {
				if (e.isPopupTrigger()) {
					int selectedRow = serverTable.rowAtPoint(e.getPoint());
					if (selectedRow != -1) {
						if (serverTable.getSelectedRowCount() == 0) {
							serverTable.setRowSelectionInterval(selectedRow,
									selectedRow);
						} else {
							int[] currentSelectedRows = serverTable
									.getSelectedRows();
							for (int i = 0; i < currentSelectedRows.length; i++) {
								if (currentSelectedRows[i] == selectedRow) {
									selectedRow = -1;
									break;
								}
							}
							if (selectedRow != -1) {
								serverTable.setRowSelectionInterval(
										selectedRow, selectedRow);
							}
						}
					}
					if (serverTable.getSelectedRowCount() == 1) {
						popup.show(aScrollPane, e.getX(), e.getY());
					} else if (serverTable.getSelectedRowCount() > 1) {
						popup3.show(aScrollPane, e.getX(), e.getY());
					} else {
						popup2.show(aScrollPane, e.getX(), e.getY());
					}
				}
			}
		};
		aScrollPane.addMouseListener(popupMouseAdapter);
		serverTable.addMouseListener(popupMouseAdapter);
		add(aScrollPane, BorderLayout.CENTER);
		JPanel legende = new JPanel(new FlowLayout());
		ImageIcon icon1 = im.getIcon("serververbunden");
		ImageIcon icon2 = im.getIcon("serverversuche");
		ImageIcon icon3 = im.getIcon("aelter24h");
		ImageIcon icon4 = im.getIcon("juenger24h");
		JLabel label1 = new JLabel(icon1);
		JLabel label2 = new JLabel(icon2);
		JLabel label3 = new JLabel(icon3);
		JLabel label4 = new JLabel(icon4);
		legende.add(label1);
		legende.add(verbunden);
		legende.add(label2);
		legende.add(versucheZuVerbinden);
		legende.add(label3);
		legende.add(aelter24h);
		legende.add(label4);
		legende.add(juenger24h);
		add(legende, BorderLayout.SOUTH);
		ApplejuiceFassade.getInstance().addDataUpdateListener(this,
				DataUpdateListener.SERVER_CHANGED);
	}

	public void registerSelected() {
		try {
			if (!initialized) {
				initialized = true;
				TableColumnModel headerModel = serverTable.getTableHeader()
						.getColumnModel();
				int columnCount = headerModel.getColumnCount();
				PositionManager pm = PositionManagerImpl.getInstance();
				if (pm.isLegal()) {
					int[] widths = pm.getServerWidths();
					for (int i = 0; i < columnCount; i++) {
						headerModel.getColumn(i).setPreferredWidth(widths[i]);
					}
				} else {
					for (int i = 0; i < columnCount; i++) {
						headerModel.getColumn(i).setPreferredWidth(
								serverTable.getWidth() / columnCount);
					}
				}
				serverTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			}
		} catch (Exception e) {
			if (logger.isEnabledFor(Level.ERROR)) {
				logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
			}
		}
	}

	public void fireContentChanged(int type, final Object content) {
		if (type == DataUpdateListener.SERVER_CHANGED) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					try {
						int[] selected = serverTable.getSelectedRows();
						((ServerTableModel) serverTable.getModel())
								.setTable((HashMap) content);
						if (selected.length != 0) {
							for (int i = 0; i < selected.length; i++) {
								serverTable.getSelectionModel()
										.addSelectionInterval(selected[i],
												selected[i]);
							}
						}
					} catch (Exception e) {
						if (logger.isEnabledFor(Level.ERROR)) {
							logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
						}
					}
				}
			});
		}
	}

	public void fireLanguageChanged() {
		try {
			LanguageSelector languageSelector = LanguageSelector.getInstance();
			sucheServer.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector
									.getFirstAttrbuteByTagName(".root.mainform.Label11.caption")));
			String[] columns = new String[5];
			columns[0] = ZeichenErsetzer.korrigiereUmlaute(languageSelector
							.getFirstAttrbuteByTagName(".root.mainform.serverlist.col0caption"));
			columns[1] = ZeichenErsetzer.korrigiereUmlaute(languageSelector
							.getFirstAttrbuteByTagName(".root.mainform.serverlist.col1caption"));
			columns[2] = ZeichenErsetzer.korrigiereUmlaute(languageSelector
							.getFirstAttrbuteByTagName(".root.mainform.serverlist.col3caption"));
			columns[3] = ZeichenErsetzer.korrigiereUmlaute(languageSelector
							.getFirstAttrbuteByTagName(".root.javagui.serverform.col4caption"));
			columns[4] = ZeichenErsetzer.korrigiereUmlaute(languageSelector
							.getFirstAttrbuteByTagName(".root.mainform.serverlist.col5caption"));
			verbinden.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector
									.getFirstAttrbuteByTagName(".root.mainform.connserv.caption")));
			loeschen1.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector
									.getFirstAttrbuteByTagName(".root.mainform.delserv.caption")));
			hinzufuegen2.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector
									.getFirstAttrbuteByTagName(".root.mainform.addserv.caption")));
			hinzufuegen3.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector
									.getFirstAttrbuteByTagName(".root.mainform.addserv.caption")));
			hinzufuegen1.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector
									.getFirstAttrbuteByTagName(".root.mainform.addserv.caption")));
			loeschen2.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector
									.getFirstAttrbuteByTagName(".root.mainform.delserv.caption")));
			verbunden.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector
									.getFirstAttrbuteByTagName(".root.javagui.serverform.verbunden")));
			versucheZuVerbinden.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector
									.getFirstAttrbuteByTagName(".root.javagui.serverform.verbinden")));
			aelter24h.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector
									.getFirstAttrbuteByTagName(".root.javagui.serverform.aelter24h")));
			juenger24h.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector
									.getFirstAttrbuteByTagName(".root.javagui.serverform.juenger24h")));
			warnungTitel = ZeichenErsetzer.korrigiereUmlaute(languageSelector
					.getFirstAttrbuteByTagName(".root.mainform.caption"));
			warnungNachricht = ZeichenErsetzer.korrigiereUmlaute(languageSelector
							.getFirstAttrbuteByTagName(".root.javagui.serverform.warnungnachricht"));
			itemCopyToClipboard.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector
									.getFirstAttrbuteByTagName(".root.mainform.getlink1.caption")));

			TableColumnModel tcm = serverTable.getColumnModel();
			for (int i = 0; i < tcm.getColumnCount(); i++) {
				tcm.getColumn(i).setHeaderValue(columns[i]);
			}
		} catch (Exception e) {
			if (logger.isEnabledFor(Level.ERROR)) {
				logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
			}
		}
	}

	public int[] getColumnWidths() {
		TableColumnModel tcm = serverTable.getColumnModel();
		int[] widths = new int[tcm.getColumnCount()];
		for (int i = 0; i < tcm.getColumnCount(); i++) {
			widths[i] = tcm.getColumn(i).getWidth();
		}
		return widths;
	}

	public void lostSelection() {}
}