package de.applejuicenet.client.gui;

import java.util.Map;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.LayoutManager;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import de.applejuicenet.client.gui.controller.ApplejuiceFassade;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.controller.OptionsManagerImpl;
import de.applejuicenet.client.gui.listener.DataUpdateListener;
import de.applejuicenet.client.gui.listener.LanguageListener;
import de.applejuicenet.client.shared.IconManager;
import de.applejuicenet.client.shared.Information;
import de.applejuicenet.client.shared.NetworkInfo;
import de.applejuicenet.client.shared.WebsiteContentLoader;
import de.applejuicenet.client.shared.ZeichenErsetzer;
import de.applejuicenet.client.shared.dac.ServerDO;
import javax.swing.SwingUtilities;

/**
 * $Header:
 * /cvsroot/applejuicejava/AJClientGUI/src/de/applejuicenet/client/gui/StartPanel.java,v
 * 1.57 2004/06/15 16:19:19 maj0r Exp $
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
 * @author: Maj0r [Maj0r@applejuicenet.de]
 *  
 */

public class StartPanel extends JPanel implements LanguageListener, RegisterI,
		DataUpdateListener {
	private static final Color APFEL_ROT = new Color(146, 36, 60);

	private static StartPanel instance = null;

	private JLabel warnungen;

	private JLabel deinClient;

	private JLabel label7;

	private JTextPane nachrichten;

	private JLabel label8;

	private JLabel netzwerk;

	private JLabel label6;

	private JLabel label9;

	private JLabel label10;

	private JLabel version;

	private JTextPane faq;

	private JLabel warnungIcon;

	private JTextPane serverMessage;

	private String label9Text;

	private String label10Text;

	private String label6Text;

	private String firewallWarning;

	private boolean firstChange = true;

	private ImageIcon icon1;

	private String keinServer = "";

	private Logger logger;

	private NetworkInfo netInfo;

	private Information information;

	private LanguageSelector languageSelector;

	private boolean firewalled = false;

	public static synchronized StartPanel getInstance() {
		if (instance == null) {
			instance = new StartPanel();
		}
		return instance;
	}

	private StartPanel() {
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
		serverMessage = new JTextPane();
		serverMessage.setContentType("text/html");
		serverMessage.setEditable(false);
		serverMessage.addHyperlinkListener(new HyperlinkListener() {
			public void hyperlinkUpdate(HyperlinkEvent e) {
				if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
					if (e.getURL() != null) {
						String url = e.getURL().toString();
						if (url.length() != 0) {
							executeLink(url);
						}
					}
				}
			}
		});

		JPanel panel3 = new JPanel(new GridBagLayout());
		panel3.setBackground(Color.WHITE);
		JPanel panel4 = new JPanel(new BorderLayout());
		panel4.setBackground(Color.WHITE);

		IconManager im = IconManager.getInstance();
		icon1 = im.getIcon("applejuicebanner");
		JPanel panel1 = new NorthPanel(new BorderLayout());
		panel1.setBackground(Color.WHITE);

		JLabel label1 = new JLabel(icon1);
		JScrollPane sp = new JScrollPane(serverMessage);
		sp.setBorder(null);
		sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		panel1.add(label1, BorderLayout.WEST);
		panel1.add(sp, BorderLayout.CENTER);

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.NORTH;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.insets.left = 5;

		ImageIcon icon2 = im.getIcon("start");
		JLabel label2 = new JLabel(icon2);
		panel3.add(label2, constraints);

		constraints.gridx = 1;
		constraints.weightx = 1;
		deinClient = new JLabel("<html><font><h2>Dein Core</h2></font></html>");
		deinClient.setForeground(APFEL_ROT);
		panel3.add(deinClient, constraints);
		constraints.weightx = 0;

		constraints.gridy++;
		constraints.insets.left = 15;
		version = new JLabel();
		panel3.add(version, constraints);
		constraints.gridy++;
		constraints.insets.left = 15;
		faq = new JTextPane();
		faq.setContentType("text/html");
		faq.setEditable(false);
		faq
				.setText("<html><a href=\"http://www.applejuicenet.de/13.0.html\">FAQ</a></html>");
		faq.addHyperlinkListener(new HyperlinkListener() {
			public void hyperlinkUpdate(HyperlinkEvent e) {
				if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
					if (e.getURL() != null) {
						String url = e.getURL().toString();
						if (url.length() != 0) {
							executeLink(url);
						}
					}
				}
			}
		});
		panel3.add(faq, constraints);

		constraints.gridy++;
		constraints.insets.left = 5;
		constraints.gridx = 0;
		ImageIcon icon3 = im.getIcon("warnung");
		warnungIcon = new JLabel(icon3);
		panel3.add(warnungIcon, constraints);

		constraints.gridx = 1;
		warnungen = new JLabel("<html><font><h2>Warnungen</h2></font></html>");
		warnungen.setForeground(APFEL_ROT);
		panel3.add(warnungen, constraints);

		constraints.gridy++;
		constraints.insets.left = 15;
		label7 = new JLabel();
		label7.setForeground(Color.RED);

		panel3.add(label7, constraints);

		constraints.gridy++;
		constraints.insets.left = 5;
		constraints.gridx = 0;
		ImageIcon icon4 = im.getIcon("netzwerk");
		JLabel label4 = new JLabel(icon4);
		panel3.add(label4, constraints);

		constraints.gridx = 1;
		label8 = new JLabel(
				"<html><font><h2>Netzwerk, Neuigkeiten und Nachrichten</h2></font></html>");
		label8.setForeground(APFEL_ROT);
		panel3.add(label8, constraints);

		constraints.gridy++;
		constraints.insets.left = 15;
		nachrichten = new JTextPane();
		panel3.add(nachrichten, constraints);
		nachrichten.setEditable(false);

		constraints.gridy++;
		constraints.insets.left = 5;
		constraints.gridx = 0;
		ImageIcon icon5 = im.getIcon("server");
		JLabel label5 = new JLabel(icon5);
		panel3.add(label5, constraints);

		constraints.gridx = 1;
		netzwerk = new JLabel(
				"<html><font><h2>appleJuice Netzwerk</h2></font></html>");
		netzwerk.setForeground(APFEL_ROT);
		panel3.add(netzwerk, constraints);

		constraints.gridy++;
		constraints.insets.left = 15;
		label9 = new JLabel("Du bist mit xxxx vielleicht verbunden.");
		panel3.add(label9, constraints);

		label10 = new JLabel("10 Verbindungen.");
		constraints.gridy++;
		constraints.insets.top = 5;
		panel3.add(label10, constraints);

		constraints.gridy++;
		label6 = new JLabel();
		panel3.add(label6, constraints);

		constraints.insets.top = 0;

		add(panel1, BorderLayout.NORTH);
		panel4.add(panel3, BorderLayout.NORTH);
		JScrollPane scrollPane = new JScrollPane(panel4);
		scrollPane.setBorder(null);
		add(scrollPane, BorderLayout.CENTER);
		languageSelector = LanguageSelector.getInstance();
		languageSelector.addLanguageListener(this);
		ApplejuiceFassade.getInstance().addDataUpdateListener(this,
				DataUpdateListener.NETINFO_CHANGED);
		ApplejuiceFassade.getInstance().addDataUpdateListener(this,
				DataUpdateListener.INFORMATION_CHANGED);
	}

	private void executeLink(String link) {
		try {
			String browser = OptionsManagerImpl.getInstance()
					.getStandardBrowser();
			try {
				Runtime.getRuntime().exec(new String[] { browser, link });
			} catch (Exception ex) {
				LanguageSelector ls = LanguageSelector.getInstance();
				String nachricht = ZeichenErsetzer
						.korrigiereUmlaute(ls
								.getFirstAttrbuteByTagName(".root.javagui.startup.updatefehlernachricht"));
				String titel = ZeichenErsetzer.korrigiereUmlaute(ls
						.getFirstAttrbuteByTagName(".root.mainform.caption"));
				JOptionPane.showMessageDialog(this, nachricht, titel,
						JOptionPane.INFORMATION_MESSAGE);
			}
		} catch (Exception e) {
			if (logger.isEnabledFor(Level.ERROR)) {
				logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
			}
		}
	}

	public void registerSelected() {
	}

	public void fireLanguageChanged() {
		try {
			keinServer = languageSelector
					.getFirstAttrbuteByTagName(".root.javagui.mainform.keinserver");
			netzwerk.setText("<html><font><h2>"
					+ ZeichenErsetzer.korrigiereUmlaute(languageSelector
							.getFirstAttrbuteByTagName(".root.mainform.html7"))
					+ "</h2></font></html>");
			label8
					.setText("<html><font><h2>"
							+ ZeichenErsetzer
									.korrigiereUmlaute(languageSelector
											.getFirstAttrbuteByTagName(".root.mainform.html13"))
							+ "</h2></font></html>");
			deinClient.setText("<html><font><h2>"
					+ ZeichenErsetzer.korrigiereUmlaute(languageSelector
							.getFirstAttrbuteByTagName(".root.mainform.html1"))
					+ "</h2></font></html>");
			firewallWarning = ZeichenErsetzer
					.korrigiereUmlaute(languageSelector
							.getFirstAttrbuteByTagName(".root.mainform.firewallwarning.caption"));
			if (firewalled) {
				warnungen.setVisible(true);
				warnungIcon.setVisible(true);
				label7.setText(firewallWarning);
			} else {
				warnungen.setVisible(false);
				warnungIcon.setVisible(false);
				label7.setText("");
			}
			label9Text = ZeichenErsetzer.korrigiereUmlaute(languageSelector
					.getFirstAttrbuteByTagName(".root.mainform.html10"));
			StringBuffer temp = new StringBuffer(label9Text);
			int pos = temp.indexOf("%s");
			if (pos != -1) {
				temp.replace(pos, pos + 2, keinServer);
			}
			pos = temp.indexOf("%d");
			if (pos != -1) {
				Map servers = ApplejuiceFassade.getInstance().getAllServer();
				int count;
				if (servers != null) {
					count = servers.size();
				} else {
					count = 0;
				}
				temp.replace(pos, pos + 2, Integer.toString(count));
			}
			label9.setText(temp.toString());
			label10Text = ZeichenErsetzer
					.korrigiereUmlaute(languageSelector
							.getFirstAttrbuteByTagName(".root.mainform.status.status0"));
			temp = new StringBuffer(label10Text);
			pos = temp.indexOf("%d");
			if (pos != -1) {
				if (information != null) {
					temp.replace(pos, pos + 2, Long.toString(information
							.getOpenConnections()));
				} else {
					temp.replace(pos, pos + 2, "0");
				}
			}
			label10.setText(temp.toString());

			label6Text = ZeichenErsetzer
					.korrigiereUmlaute(languageSelector
							.getFirstAttrbuteByTagName(".root.mainform.status.status2"));
			temp = new StringBuffer(label6Text);
			if (netInfo != null) {
				pos = temp.indexOf("%d");
				if (pos != -1) {
					temp.replace(pos, pos + 2, netInfo
							.getAJUserGesamtAsStringWithPoints());
				}
				pos = temp.indexOf("%d");
				if (pos != -1) {
					temp.replace(pos, pos + 2, netInfo
							.getAJAnzahlDateienAsStringWithPoints());
				}
				pos = temp.indexOf("%s");
				if (pos != -1) {
					temp.replace(pos, pos + 2, netInfo
							.getAJGesamtShareWithPoints(0));
				}
			} else {
				pos = temp.indexOf("%d");
				if (pos != -1) {
					temp.replace(pos, pos + 2, "0");
				}
				pos = temp.indexOf("%d");
				if (pos != -1) {
					temp.replace(pos, pos + 2, "0");
				}
				pos = temp.indexOf("%s");
				if (pos != -1) {
					temp.replace(pos, pos + 2, "0 MB");
				}

			}
			label6.setText(temp.toString());
			warnungen
					.setText("<html><font><h2>"
							+ ZeichenErsetzer
									.korrigiereUmlaute(languageSelector
											.getFirstAttrbuteByTagName(".root.mainform.html15"))
							+ "</h2></font></html>");
		} catch (Exception e) {
			if (logger.isEnabledFor(Level.ERROR)) {
				logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
			}
		}
	}

	public void fireContentChanged(int type, final Object content) {
		if (type == DataUpdateListener.NETINFO_CHANGED) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					try {
						if (firstChange) {
							firstChange = false;
							new NachrichtenWorker().start();
						}
						netInfo = (NetworkInfo) content;
						StringBuffer temp = new StringBuffer(label6Text);
						int pos = temp.indexOf("%d");
						if (pos != -1) {
							temp.replace(pos, pos + 2, netInfo
									.getAJUserGesamtAsStringWithPoints());
						}
						pos = temp.indexOf("%d");
						if (pos != -1) {
							temp.replace(pos, pos + 2, netInfo
									.getAJAnzahlDateienAsStringWithPoints());
						}
						pos = temp.indexOf("%s");
						if (pos != -1) {
							temp.replace(pos, pos + 2, netInfo
									.getAJGesamtShareWithPoints(0));
						}
						label6.setText(temp.toString());
						if (netInfo.isFirewalled() != firewalled) {
							firewalled = !firewalled;
							warnungen.setVisible(firewalled);
							warnungIcon.setVisible(firewalled);
							if (firewalled) {
								label7.setText(firewallWarning);
							} else {
								label7.setText("");
							}
							repaint();
						}
						String tmp = netInfo.getWelcomeMessage();
						if (tmp.compareTo(serverMessage.getText()) != 0) {
							serverMessage.setText(tmp);
							serverMessage.setCaretPosition(0);
						}
					} catch (Exception e) {
						if (logger.isEnabledFor(Level.ERROR)) {
							logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
						}
					}
				}
			});
		} else if (type == DataUpdateListener.INFORMATION_CHANGED) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					try {
						information = (Information) content;
						StringBuffer temp = new StringBuffer(label9Text);
						int pos = temp.indexOf("%s");
						if (pos != -1) {
							if (information.getVerbindungsStatus() == Information.VERBUNDEN) {
								if (information.getServerName() == null
										|| information.getServerName().length() == 0) {
									ServerDO serverDO = information
											.getServerDO();
									if (serverDO != null) {
										String tmp = serverDO.getHost() + ":"
												+ serverDO.getPort();
										temp.replace(pos, pos + 2, tmp);
									} else {
										temp.replace(pos, pos + 2, "?");
									}
								} else {
									temp.replace(pos, pos + 2, information
											.getServerName());
								}
							} else {
								temp.replace(pos, pos + 2, keinServer);
							}
							pos = temp.indexOf("%d");
							if (pos != -1) {
								temp.replace(pos, pos + 2, Integer
										.toString(ApplejuiceFassade
												.getInstance().getAllServer()
												.size()));
							}
						}
						label9.setText(temp.toString());
						temp = new StringBuffer(label10Text);
						pos = temp.indexOf("%d");
						if (pos != -1) {
							temp
									.replace(pos, pos + 2, Long
											.toString(information
													.getOpenConnections()));
						}
						label10.setText(temp.toString());
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
	}

	private class NachrichtenWorker extends Thread {
		public void run() {
			if (logger.isEnabledFor(Level.DEBUG)) {
				logger.debug("NachrichtenWorkerThread gestartet. " + this);
			}
			try {
				String coreVersion = ApplejuiceFassade.getInstance()
						.getCoreVersion().getVersion();
				version.setText("<html>GUI: " + ApplejuiceFassade.GUI_VERSION
						+ "<br>Core: " + coreVersion + "</html>");
				String nachricht = "verwendeter Core: " + coreVersion;
				if (logger.isEnabledFor(Level.INFO)) {
					logger.info(nachricht);
				}
				String htmlText = WebsiteContentLoader.getWebsiteContent(
						"http://www.applejuicenet.org", 80,
						"/inprog/news.php?version="
								+ ApplejuiceFassade.getInstance()
										.getCoreVersion().getVersion());

				int pos = htmlText.toLowerCase().indexOf("<html>");
				if (pos != -1) {
					htmlText = htmlText.substring(pos);
				} else {
					htmlText = "<html>" + htmlText + "</html>";
				}
				StringBuffer buffer = new StringBuffer(htmlText);
				int index;
				while ((index = buffer.indexOf(". ")) != -1) {
					buffer.replace(index, index + 1, ".<br>");
				}
				htmlText = buffer.toString();
				final String htmlContent = htmlText;
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						nachrichten.setContentType("text/html");
						nachrichten.setText(htmlContent);
						nachrichten.setFont(label9.getFont());
						nachrichten
								.addHyperlinkListener(new HyperlinkListener() {
									public void hyperlinkUpdate(HyperlinkEvent e) {
										if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
											if (e.getURL() != null) {
												String url = e.getURL()
														.toString();
												if (url.length() != 0) {
													executeLink(url);
												}
											}
										}
									}
								});
					}
				});
			} catch (Exception e) {
				if (logger.isEnabledFor(Level.INFO)) {
					logger
							.info("Versionsabhaengige Nachrichten konnten nicht geladen werden. Server down?");
				}
			}
			if (logger.isEnabledFor(Level.DEBUG)) {
				logger.debug("NachrichtenWorkerThread beendet. " + this);
			}
		}
	}

	private class NorthPanel extends JPanel {
		public NorthPanel(LayoutManager layoutManager) {
			super(layoutManager);
		}

		public Dimension getPreferredSize() {
			return new Dimension(super.getPreferredSize().width, icon1
					.getIconHeight());
		}
	}
}