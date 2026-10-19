package de.applejuicenet.client.gui.start;

import de.applejuicenet.client.facade.ApplejuiceFacade;
import de.applejuicenet.client.gui.components.GuiController;
import de.applejuicenet.client.gui.components.TklPanel;
import de.applejuicenet.client.shared.IconManager;

import javax.swing.*;
import java.awt.*;

/**
 * $Header:
 * /cvsroot/applejuicejava/AJClientGUI/src/de/applejuicenet/client/gui/StartPanel.java,v
 * 1.57 2004/06/15 16:19:19 maj0r Exp $
 *
 * <p>
 * Title: AppleJuice Client-GUI
 * </p>
 * <p>
 * Description: Official GUI for the appleJuice core developed by muhviehstarr
 * </p>
 * <p>
 * Copyright: General Public License
 * </p>
 *
 * @author Maj0r [aj@tkl-soft.de]
 */

public class StartPanel extends TklPanel {

    private static final Color APPLE_RED = new Color(146, 36, 60);

    private JLabel warnings;
    private JLabel yourClient;
    private JLabel firewallWarning;
    private JTextPane messages;
    private JLabel news;
    private JLabel network;
    private JLabel status;
    private JLabel connectionMessage;
    private JLabel version;
    private JLabel warningIcon;
    private JTextPane serverMessage;

    public StartPanel(GuiController guiController) {
        super(guiController);
        try {
            init();
        } catch (Exception e) {
            logger.error(ApplejuiceFacade.ERROR_MESSAGE, e);
        }
    }

    public JTextPane getMessagesPane() {
        return messages;
    }

    public JTextPane getServerMessagePane() {
        return serverMessage;
    }

    public JLabel getLblVersion() {
        return version;
    }

    public JLabel getLblWarning() {
        return warnings;
    }

    public JLabel getLblWarningIcon() {
        return warningIcon;
    }

    public JLabel getLblStatus() {
        return status;
    }

    public JLabel getLblFirewallWarning() {
        return firewallWarning;
    }

    public JLabel getLblConnectionMessage() {
        return connectionMessage;
    }

    public JLabel getLblNetwork() {
        return network;
    }

    public JLabel getLblYourClient() {
        return yourClient;
    }

    public JLabel getLblNews() {
        return news;
    }

    private void init() {
        setLayout(new BorderLayout());
        serverMessage = new JTextPane();
        serverMessage.setContentType("text/html");
        serverMessage.setEditable(false);

        JPanel panel3 = new JPanel(new GridBagLayout());
        panel3.setBackground(UIManager.getColor("TextField.background"));

        JPanel panel4 = new JPanel(new BorderLayout());
        panel4.setBackground(UIManager.getColor("TextField.background"));

        IconManager im = IconManager.getInstance();
        JPanel panel1 = new NorthPanel(serverMessage);
        panel1.setBackground(UIManager.getColor("TextField.background"));

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
        yourClient = new JLabel();
        yourClient.setForeground(APPLE_RED);
        panel3.add(yourClient, constraints);
        constraints.weightx = 0;

        constraints.gridy++;
        constraints.insets.left = 15;
        version = new JLabel();
        panel3.add(version, constraints);

        constraints.gridy++;
        constraints.insets.left = 5;
        constraints.gridx = 0;
        ImageIcon icon3 = im.getIcon("warnung");
        warningIcon = new JLabel(icon3);
        panel3.add(warningIcon, constraints);

        constraints.gridx = 1;
        warnings = new JLabel();
        warnings.setForeground(APPLE_RED);
        panel3.add(warnings, constraints);

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
        news = new JLabel();
        news.setForeground(APPLE_RED);
        panel3.add(news, constraints);

        constraints.gridy++;
        constraints.insets.left = 15;
        messages = new JTextPane();
        panel3.add(messages, constraints);
        messages.setEditable(false);
        messages.setBackground(UIManager.getColor("TextField.background"));

        constraints.gridy++;
        constraints.insets.left = 5;
        constraints.gridx = 0;
        ImageIcon icon5 = im.getIcon("server");
        JLabel label5 = new JLabel(icon5);
        panel3.add(label5, constraints);

        constraints.gridx = 1;
        network = new JLabel();
        network.setForeground(APPLE_RED);
        panel3.add(network, constraints);

        constraints.gridy++;
        constraints.insets.left = 15;
        connectionMessage = new JLabel();
        panel3.add(connectionMessage, constraints);

        constraints.gridy++;
        constraints.insets.top = 5;
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
