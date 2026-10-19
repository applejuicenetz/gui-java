package de.applejuicenet.client.gui.start;

import de.applejuicenet.client.shared.IconManager;

import javax.swing.*;
import java.awt.*;

/**
 * Copyright: General Public License
 */

public class NorthPanel extends JPanel {
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
