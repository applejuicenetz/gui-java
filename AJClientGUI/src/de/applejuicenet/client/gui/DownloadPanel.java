package de.applejuicenet.client.gui;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import de.applejuicenet.client.gui.controller.ApplejuiceFassade;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.controller.OptionsManagerImpl;
import de.applejuicenet.client.gui.controller.PositionManager;
import de.applejuicenet.client.gui.controller.PositionManagerImpl;
import de.applejuicenet.client.gui.listener.DataUpdateListener;
import de.applejuicenet.client.gui.listener.LanguageListener;
import de.applejuicenet.client.gui.shared.SortButtonRenderer;
import de.applejuicenet.client.gui.tables.JTreeTable;
import de.applejuicenet.client.gui.tables.TreeTableModelAdapter;
import de.applejuicenet.client.gui.tables.download.DownloadDirectoryNode;
import de.applejuicenet.client.gui.tables.download.DownloadMainNode;
import de.applejuicenet.client.gui.tables.download.DownloadModel;
import de.applejuicenet.client.gui.tables.download.DownloadRootNode;
import de.applejuicenet.client.gui.tables.download.DownloadTableCellRenderer;
import de.applejuicenet.client.gui.tables.download.DownloadTablePercentCellRenderer;
import de.applejuicenet.client.gui.tables.download.DownloadTableVersionCellRenderer;
import de.applejuicenet.client.shared.IconManager;
import de.applejuicenet.client.shared.Information;
import de.applejuicenet.client.shared.Settings;
import de.applejuicenet.client.shared.SoundPlayer;
import de.applejuicenet.client.shared.ZeichenErsetzer;
import de.applejuicenet.client.shared.dac.DownloadDO;
import de.applejuicenet.client.shared.dac.DownloadSourceDO;
import de.applejuicenet.client.shared.dac.ServerDO;
import de.applejuicenet.client.shared.dac.ShareDO;

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

public class DownloadPanel extends JPanel implements LanguageListener,
		RegisterI, DataUpdateListener {

	private static final long serialVersionUID = 3086254831787190128L;

	private static DownloadPanel instance = null;

	private DownloadDOOverviewPanel downloadDOOverviewPanel;
	private JTextField downloadLink = new JTextField();
	private JButton btnStartDownload = new JButton();
	private PowerDownloadPanel powerDownloadPanel;
	private JTreeTable downloadTable;
	private JLabel linkLabel = new JLabel();
	private DownloadModel downloadModel;
	private JPopupMenu popup = new JPopupMenu();
	private boolean initialized = false;
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
	private String downloadAbbrechen;
	private String dialogTitel;
	private JMenuItem partlisteAnzeigen;
	private Logger logger;
	private boolean panelSelected = false;
	private JPopupMenu columnPopup = new JPopupMenu();
	private TableColumn[] columns = new TableColumn[10];
	private JCheckBoxMenuItem[] columnPopupItems = new JCheckBoxMenuItem[columns.length];
	private JPopupMenu menu;
	private JMenuItem einfuegen;
	private DownloadPartListWatcher downloadPartListWatcher = new DownloadPartListWatcher();

	public static synchronized DownloadPanel getInstance() {
		if (instance == null) {
			instance = new DownloadPanel();
		}
		return instance;
	}

	private DownloadPanel() {
		logger = Logger.getLogger(getClass());
		try {
			downloadDOOverviewPanel = new DownloadDOOverviewPanel(this);
			powerDownloadPanel = new PowerDownloadPanel(this);
			init();
			LanguageSelector.getInstance().addLanguageListener(this);
		} catch (Exception e) {
			if (logger.isEnabledFor(Level.ERROR)) {
				logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
			}
		}
	}

	public void tryGetPartList() {
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

	private void init() throws Exception {
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

		popup.add(abbrechen);
		popup.add(pause);
		popup.add(fortsetzen);
		popup.add(umbenennen);
		popup.add(zielordner);
		popup.add(fertigEntfernen);
		popup.add(itemCopyToClipboard);
		popup.add(itemCopyToClipboardWithSources);
		popup.add(partlisteAnzeigen);
		partlisteAnzeigen.setVisible(false);
    	popup.add(itemOpenWithProgram);
        itemOpenWithProgram.setIcon(im.getIcon("vlc"));
        if (ApplejuiceFassade.getInstance().isLocalhost()){
            itemOpenWithProgram.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
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
            });
        }
        else{
        	itemOpenWithProgram.setEnabled(false);
        }

		einfuegen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				linkMenuActionPerformed(e);
			}
		});
		menu.add(einfuegen);

		itemCopyToClipboard.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
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
		});

		itemCopyToClipboardWithSources.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
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
		});

		abbrechen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
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
		});

		pause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				pausieren();
			}
		});

		fortsetzen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				fortsetzen();
			}
		});

		umbenennen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				renameDownload();
			}
		});

		zielordner.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				changeIncomingDir();
			}
		});

		fertigEntfernen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				ApplejuiceFassade.getInstance().cleanDownloadList();
				downloadDOOverviewPanel.enableHoleListButton(false);
				powerDownloadPanel.btnPdl.setEnabled(false);
				powerDownloadPanel.setPwdlValue(0);
				downloadTable.getSelectionModel().clearSelection();
			}
		});

		partlisteAnzeigen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				tryGetPartList();
			}
		});

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.NORTH;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 3;
		constraints.gridheight = 1;
		JPanel tempPanel = new JPanel();
		tempPanel.setLayout(new BorderLayout());
		tempPanel.add(linkLabel, BorderLayout.WEST);
		tempPanel.add(downloadLink, BorderLayout.CENTER);
		tempPanel.add(btnStartDownload, BorderLayout.EAST);
		topPanel.add(tempPanel, constraints);
		constraints.gridwidth = 3;
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.weighty = 1;
		constraints.weightx = 1;

		downloadLink.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent ke) {
				if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
					btnStartDownload.doClick();
				}
			}
		});
		downloadLink.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					showLinkMenu(e.getX(), e.getY());
				}
			}
		});

		downloadModel = new DownloadModel();
		downloadTable = new JTreeTable(downloadModel);
		downloadTable.addKeyListener(new KeyAdapter() {
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

		btnStartDownload.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				startDownload();
			}
		});
		aScrollPane = new JScrollPane(downloadTable);
		aScrollPane.setBackground(downloadTable.getBackground());
		downloadTable.getTableHeader().setBackground(
				downloadTable.getBackground());
		aScrollPane.getViewport().setOpaque(false);
		downloadTable.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				if (!DownloadRootNode.isInitialized()) {
					return;
				}
				Point p = e.getPoint();
				int selectedRow = downloadTable.rowAtPoint(p);
				Object node = ((TreeTableModelAdapter) downloadTable.getModel())
						.nodeForRow(selectedRow);
				if (downloadTable.columnAtPoint(p) != 0) {
					if (e.getClickCount() == 2) {
						((TreeTableModelAdapter) downloadTable.getModel())
								.expandOrCollapseRow(selectedRow);
					}
				}
				if (node.getClass() == DownloadMainNode.class
						&& ((DownloadMainNode) node).getType() == DownloadMainNode.ROOT_NODE) {
					DownloadDO downloadDO = ((DownloadMainNode) node)
							.getDownloadDO();
					if (Settings.getSettings().isDownloadUebersicht()) {
						downloadDOOverviewPanel.enableHoleListButton(false);
						tryGetPartList();
					} else {
						if ((downloadDO.getStatus() == DownloadDO.SUCHEN_LADEN || downloadDO
								.getStatus() == DownloadDO.PAUSIERT)) {
							downloadDOOverviewPanel.enableHoleListButton(true);
						} else {
							downloadDOOverviewPanel.enableHoleListButton(false);
						}
					}
					if (!powerDownloadPanel.isAutomaticPwdlActive()) {
						powerDownloadPanel.btnPdl.setEnabled(true);
						if (downloadDO.getStatus() == DownloadDO.SUCHEN_LADEN
								|| downloadDO.getStatus() == DownloadDO.PAUSIERT) {
							powerDownloadPanel.setPwdlValue(downloadDO
									.getPowerDownload());
						} else {
							downloadDOOverviewPanel.enableHoleListButton(false);
							powerDownloadPanel.btnPdl.setEnabled(false);
							powerDownloadPanel.setPwdlValue(0);
						}
					}
				} else if (node.getClass() == DownloadSourceDO.class) {
					if (Settings.getSettings().isDownloadUebersicht()) {
						downloadDOOverviewPanel.enableHoleListButton(false);
						tryGetPartList();
					} else {
						downloadDOOverviewPanel.enableHoleListButton(true);
					}
					if (!powerDownloadPanel.isAutomaticPwdlActive()) {
						powerDownloadPanel.btnPdl.setEnabled(true);
						powerDownloadPanel
								.setPwdlValue(((DownloadSourceDO) node)
										.getPowerDownload());
					}
				} else {
					powerDownloadPanel.btnPdl.setEnabled(false);
					powerDownloadPanel.setPwdlValue(0);
					downloadDOOverviewPanel.enableHoleListButton(false);
				}
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
				if (!DownloadRootNode.isInitialized()) {
					return;
				}
				if (e.isPopupTrigger()) {
					Point p = e.getPoint();
					int selectedRow = downloadTable.rowAtPoint(p);
					int[] currentSelectedRows = downloadTable.getSelectedRows();
					for (int i = 0; i < currentSelectedRows.length; i++) {
						if (currentSelectedRows[i] == selectedRow) {
							selectedRow = -1;
							break;
						}
					}
					if (selectedRow != -1) {
						downloadTable.setRowSelectionInterval(selectedRow,
								selectedRow);
					}
					Object[] selectedItems = getSelectedDownloadItems();
					umbenennen.setVisible(false);
					zielordner.setVisible(false);
					partlisteAnzeigen.setVisible(false);
					boolean pausiert = false;
					boolean laufend = false;
					if (selectedItems != null) {
						if (selectedItems.length == 1) {
							if ((selectedItems[0].getClass() == DownloadMainNode.class && ((DownloadMainNode) selectedItems[0])
									.getType() == DownloadMainNode.ROOT_NODE)
									|| (selectedItems[0].getClass() == DownloadSourceDO.class)) {
								partlisteAnzeigen.setVisible(true);
							}
							if (selectedItems[0].getClass() != DownloadSourceDO.class) {
								umbenennen.setVisible(true);
								zielordner.setVisible(true);
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
									zielordner.setVisible(true);
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
						pause.setEnabled(true);
					} else {
						pause.setEnabled(false);
					}
					if (pausiert) {
						fortsetzen.setEnabled(true);
					} else {
						fortsetzen.setEnabled(false);
					}
					popup.show(downloadTable, e.getX(), e.getY());
				}
			}
		});
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

		ApplejuiceFassade.getInstance().addDataUpdateListener(this,
				DataUpdateListener.DOWNLOAD_CHANGED);
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
				&& !powerDownloadPanel.isAutomaticPwdlActive()) {
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
				&& !powerDownloadPanel.isAutomaticPwdlActive()) {
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
		final String link = downloadLink.getText();
		if (link.length() != 0) {
			new Thread() {
				public void run() {
					ApplejuiceFassade.getInstance().processLink(link);
					SoundPlayer.getInstance().playSound(SoundPlayer.LADEN);
				}
			}.start();
			downloadLink.setText("");
		}
	}

	public Object[] getSelectedDownloadItems() {
		try {
			int count = downloadTable.getSelectedRowCount();
			Object[] result = null;
			if (count == 1) {
				result = new Object[count];
				result[0] = ((TreeTableModelAdapter) downloadTable.getModel())
						.nodeForRow(downloadTable.getSelectedRow());
			} else if (count > 1) {
				result = new Object[count];
				int[] indizes = downloadTable.getSelectedRows();
				for (int i = 0; i < indizes.length; i++) {
					result[i] = ((TreeTableModelAdapter) downloadTable
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

	public void registerSelected() {
		try {
			downloadDOOverviewPanel.enableHoleListButton(false);
			panelSelected = true;
			if (!initialized) {
				initialized = true;
				int width = aScrollPane.getWidth() - 18;
				PositionManager pm = PositionManagerImpl.getInstance();
				if (pm.isLegal()) {
					int[] widths = pm.getDownloadWidths();
					boolean[] visibilies = pm.getDownloadColumnVisibilities();
					int[] indizes = pm.getDownloadColumnIndizes();
					ArrayList visibleColumns = new ArrayList();
					for (int i = 0; i < columns.length; i++) {
						columns[i].setPreferredWidth(widths[i]);
						downloadTable.removeColumn(columns[i]);
						if (visibilies[i]) {
							visibleColumns.add(columns[i]);
						}
					}
					int pos = -1;
					for (int i = 0; i < visibleColumns.size(); i++) {
						for (int x = 0; x < columns.length; x++) {
							if (visibleColumns.contains(columns[x])
									&& indizes[x] == pos + 1) {
								downloadTable.addColumn(columns[x]);
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
				downloadTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
				int loc = (int) ((splitPane.getHeight()
						- splitPane.getDividerSize() - powerDownloadPanel
						.getPreferredSize().height));
				splitPane.setDividerLocation(loc);
			}
			downloadTable.updateUI();
		} catch (Exception e) {
			if (logger.isEnabledFor(Level.ERROR)) {
				logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
			}
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

	public void fireLanguageChanged() {
		try {
			LanguageSelector languageSelector = LanguageSelector.getInstance();
			String text = languageSelector
					.getFirstAttrbuteByTagName(".root.mainform.Label14.caption");
			dialogTitel = ZeichenErsetzer.korrigiereUmlaute(languageSelector
					.getFirstAttrbuteByTagName(".root.mainform.caption"));
			downloadAbbrechen = ZeichenErsetzer
					.korrigiereUmlaute(languageSelector
							.getFirstAttrbuteByTagName(".root.mainform.msgdlgtext5"));
			linkLabel.setText(ZeichenErsetzer.korrigiereUmlaute(text));
			btnStartDownload
					.setText(ZeichenErsetzer
							.korrigiereUmlaute(languageSelector
									.getFirstAttrbuteByTagName(".root.mainform.downlajfsp.caption")));
			btnStartDownload
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

			for (int i = 0; i < columns.length; i++) {
				columns[i].setHeaderValue(tableColumns[i]);
				columnPopupItems[i].setText(tableColumns[i]);
			}
			DownloadMainNode.setColumnTitles(tableColumns);

			abbrechen
					.setText(ZeichenErsetzer
							.korrigiereUmlaute(languageSelector
									.getFirstAttrbuteByTagName(".root.mainform.canceldown.caption")));
			pause
					.setText(ZeichenErsetzer
							.korrigiereUmlaute(languageSelector
									.getFirstAttrbuteByTagName(".root.mainform.pausedown.caption"))
							+ " [F5]");
			fortsetzen
					.setText(ZeichenErsetzer
							.korrigiereUmlaute(languageSelector
									.getFirstAttrbuteByTagName(".root.mainform.resumedown.caption"))
							+ " [F6]");
			umbenennen
					.setText(ZeichenErsetzer
							.korrigiereUmlaute(languageSelector
									.getFirstAttrbuteByTagName(".root.mainform.renamefile.caption"))
							+ " [F2]");
			zielordner
					.setText(ZeichenErsetzer
							.korrigiereUmlaute(languageSelector
									.getFirstAttrbuteByTagName(".root.mainform.changetarget.caption"))
							+ " [F3]");
			fertigEntfernen
					.setText(ZeichenErsetzer
							.korrigiereUmlaute(languageSelector
									.getFirstAttrbuteByTagName(".root.mainform.Clearfinishedentries1.caption")));
			partlisteAnzeigen
					.setText(ZeichenErsetzer
							.korrigiereUmlaute(languageSelector
									.getFirstAttrbuteByTagName(".root.javagui.downloadform.partlisteanzeigen")));
			itemCopyToClipboard
					.setText(ZeichenErsetzer
							.korrigiereUmlaute(languageSelector
									.getFirstAttrbuteByTagName(".root.mainform.getlink1.caption")));
			itemCopyToClipboardWithSources
					.setText(ZeichenErsetzer
							.korrigiereUmlaute(languageSelector
									.getFirstAttrbuteByTagName(".root.javagui.downloadform.getlinkwithsources")));
			einfuegen
					.setText(ZeichenErsetzer
							.korrigiereUmlaute(languageSelector
									.getFirstAttrbuteByTagName(".root.javagui.downloadform.einfuegen")));
            itemOpenWithProgram.setText("VLC");
		} catch (Exception e) {
			if (logger.isEnabledFor(Level.ERROR)) {
				logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
			}
		}
	}

	public int[] getColumnWidths() {
		int[] widths = new int[columns.length];
		for (int i = 0; i < columns.length; i++) {
			widths[i] = columns[i].getWidth();
		}
		return widths;
	}

	public void fireContentChanged(int type, final Object content) {
		if (type == DataUpdateListener.DOWNLOAD_CHANGED) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					try {
						HashMap downloads = (HashMap) content;
						((DownloadRootNode) downloadModel.getRoot())
								.setDownloadMap(downloads);
						DownloadDirectoryNode.setDownloads(downloads);
						if (panelSelected) {
							downloadTable.updateUI();
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

	public void lostSelection() {
		panelSelected = false;
		downloadPartListWatcher.setDownloadNode(null);
		downloadDOOverviewPanel.setDownloadDO(null);
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

	private class DownloadPartListWatcher {
		private Thread worker = null;
		private Object nodeObject = null;

		public void setDownloadNode(Object node) {
			if (node == null) {
				nodeObject = null;
				if (worker != null) {
					worker.interrupt();
					worker = null;
				}
				return;
			}
			if (worker != null) {
				worker.interrupt();
				worker = null;
			}
			nodeObject = node;
			worker = new Thread() {
				public void run() {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							if (nodeObject.getClass() == DownloadMainNode.class
									&& ((DownloadMainNode) nodeObject)
											.getType() == DownloadMainNode.ROOT_NODE) {
								downloadDOOverviewPanel
										.setDownloadDO(((DownloadMainNode) nodeObject)
												.getDownloadDO());
							} else if (nodeObject.getClass() == DownloadSourceDO.class) {
								if (((DownloadSourceDO) nodeObject).getStatus() == DownloadSourceDO.IN_WARTESCHLANGE
										&& ((DownloadSourceDO) nodeObject)
												.getQueuePosition() > 20) {
									return;
								}
								downloadDOOverviewPanel
										.setDownloadSourceDO((DownloadSourceDO) nodeObject);
							}
						}
					});
				}
			};
			worker.start();
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
