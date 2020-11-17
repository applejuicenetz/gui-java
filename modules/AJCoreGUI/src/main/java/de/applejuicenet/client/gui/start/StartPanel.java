package de.applejuicenet.client.gui.start;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import org.apache.log4j.Level;

import de.applejuicenet.client.fassade.ApplejuiceFassade;
import de.applejuicenet.client.fassade.entity.Information;
import de.applejuicenet.client.fassade.shared.NetworkInfo;
import de.applejuicenet.client.gui.components.GuiController;
import de.applejuicenet.client.gui.components.TklPanel;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.shared.IconManager;

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
 * @author: Maj0r [aj@tkl-soft.de]
 *
 */

public class StartPanel extends TklPanel {

	private static final Color APFEL_ROT = new Color(146, 36, 60);

	private JLabel warnungen;
	private JLabel deinClient;
	private JLabel firewallWarning;
	private JTextPane nachrichten;
	private JLabel neuigkeiten;
	private JLabel netzwerk;
	private JLabel status;
	private JLabel verbindungsNachricht;
	private JLabel verbindungen;
	private JLabel version;
	private JLabel warnungIcon;
	private JTextPane serverMessage;
	private NetworkInfo netInfo;
	private Information information;
	private LanguageSelector languageSelector;

	public StartPanel(GuiController guiController) {
    	super(guiController);
		try {
			init();
		} catch (Exception e) {
			if (logger.isEnabledFor(Level.ERROR)) {
				logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
			}
		}
	}

	public JTextPane getNachrichtenPane(){
		return nachrichten;
	}

	public JTextPane getServerMessagePane(){
		return serverMessage;
	}

	public JLabel getLblVersion(){
		return version;
	}

	public JLabel getLblWarnung(){
		return warnungen;
	}

	public JLabel getLblWarnungIcon(){
		return warnungIcon;
	}

	public JLabel getLblStatus(){
		return status;
	}

	public JLabel getLblFirewallWarning(){
		return firewallWarning;
	}

	public JLabel getLblVerbindungsnachricht(){
		return verbindungsNachricht;
	}

	public JLabel getLblVerbindungen(){
		return verbindungen;
	}

	public JLabel getLblNetzwerk(){
		return netzwerk;
	}

	public JLabel getLblDeinClient(){
		return deinClient;
	}

	public JLabel getLblNeuigkeiten(){
		return neuigkeiten;
	}

	private void init() throws Exception {
		setLayout(new BorderLayout());
		serverMessage = new JTextPane();
		serverMessage.setContentType("text/html");
		serverMessage.setEditable(false);

		JPanel panel3 = new JPanel(new GridBagLayout());
		panel3.setBackground(Color.WHITE);
		JPanel panel4 = new JPanel(new BorderLayout());
		panel4.setBackground(Color.WHITE);

		IconManager im = IconManager.getInstance();
		JPanel panel1 = new NorthPanel(serverMessage);
		panel1.setBackground(Color.WHITE);

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
		deinClient = new JLabel();
		deinClient.setForeground(APFEL_ROT);
		panel3.add(deinClient, constraints);
		constraints.weightx = 0;

		constraints.gridy++;
		constraints.insets.left = 15;
		version = new JLabel();
		panel3.add(version, constraints);

		constraints.gridy++;
		constraints.insets.left = 5;
		constraints.gridx = 0;
		ImageIcon icon3 = im.getIcon("warnung");
		warnungIcon = new JLabel(icon3);
		panel3.add(warnungIcon, constraints);

		constraints.gridx = 1;
		warnungen = new JLabel();
		warnungen.setForeground(APFEL_ROT);
		panel3.add(warnungen, constraints);

		constraints.gridy++;
		constraints.insets.left = 15;
		firewallWarning = new JLabel();
		firewallWarning.setForeground(Color.RED);

		panel3.add(firewallWarning, constraints);

		constraints.gridy++;
		constraints.insets.left = 5;
		constraints.gridx = 0;
		ImageIcon icon4 = im.getIcon("netzwerk");
		JLabel label4 = new JLabel(icon4);
		panel3.add(label4, constraints);

		constraints.gridx = 1;
		neuigkeiten = new JLabel();
		neuigkeiten.setForeground(APFEL_ROT);
		panel3.add(neuigkeiten, constraints);

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
		netzwerk = new JLabel();
		netzwerk.setForeground(APFEL_ROT);
		panel3.add(netzwerk, constraints);

		constraints.gridy++;
		constraints.insets.left = 15;
		verbindungsNachricht = new JLabel();
		panel3.add(verbindungsNachricht, constraints);

		verbindungen = new JLabel();
		constraints.gridy++;
		constraints.insets.top = 5;
		panel3.add(verbindungen, constraints);

		constraints.gridy++;
		status = new JLabel();
		panel3.add(status, constraints);

		constraints.insets.top = 0;

		add(panel1, BorderLayout.NORTH);
		panel4.add(panel3, BorderLayout.NORTH);
		JScrollPane scrollPane = new JScrollPane(panel4);
		scrollPane.setBorder(null);
		add(scrollPane, BorderLayout.CENTER);
	}
}
