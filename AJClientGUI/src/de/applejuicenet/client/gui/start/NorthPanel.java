package de.applejuicenet.client.gui.start;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import de.applejuicenet.client.shared.IconManager;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/start/NorthPanel.java,v 1.1 2004/10/15 13:34:47 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 */

public class NorthPanel extends JPanel {
	private static final long serialVersionUID = 8611614870757239667L;
	
	private ImageIcon icon;

	public NorthPanel(JTextPane serverMessage) {
		super(new BorderLayout());
		icon = IconManager.getInstance().getIcon("applejuicebanner");
		JLabel iconLabel = new JLabel(icon);
		add(iconLabel, BorderLayout.WEST);
		JScrollPane sp = new JScrollPane(serverMessage);
		sp.setBorder(null);
		sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		add(sp, BorderLayout.CENTER);
	}

	public Dimension getPreferredSize() {
		return new Dimension(super.getPreferredSize().width, icon.getIconHeight());
	}
}