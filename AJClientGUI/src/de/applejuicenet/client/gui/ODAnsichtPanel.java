package de.applejuicenet.client.gui;

import de.applejuicenet.client.shared.AJSettings;
import de.applejuicenet.client.gui.tables.download.DownloadNode;

import javax.swing.*;
import java.awt.*;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/Attic/ODAnsichtPanel.java,v 1.1 2003/08/15 18:31:36 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI f?r den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: ODAnsichtPanel.java,v $
 * Revision 1.1  2003/08/15 18:31:36  maj0r
 * Farbdialog in Optionen eingebaut.
 *
 *
 */

public class ODAnsichtPanel extends JPanel{
    private AJSettings ajSettings;
    private JLabel farbeFertigerDownload = new JLabel("      ");
    private JLabel farbeQuelle = new JLabel("      ");

    public ODAnsichtPanel(AJSettings ajSettings) {
        this.ajSettings = ajSettings;
        init();
    }

    private void init(){
        setLayout(new BorderLayout());
        farbeFertigerDownload.setOpaque(true);
        farbeFertigerDownload.setBackground(DownloadNode.DOWNLOAD_FERTIG_COLOR);
        farbeQuelle.setOpaque(true);
        farbeQuelle.setBackground(DownloadNode.SOURCE_NODE_COLOR);
        JPanel panel1 = new JPanel();
        panel1.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets.bottom = 5;
        panel1.setBorder(BorderFactory.createTitledBorder("Farben"));
        panel1.add(new JCheckBox("aktiv"), constraints);
        constraints.gridy = 1;
        constraints.insets.left = 5;
        constraints.insets.right = 5;
        panel1.add(new JLabel("fertiger Download"), constraints);
        constraints.gridy = 2;
        panel1.add(new JLabel("Quelle"), constraints);
        constraints.gridx = 1;
        constraints.gridy = 1;
        panel1.add(farbeFertigerDownload, constraints);
        constraints.gridy = 2;
        panel1.add(farbeQuelle, constraints);
        JPanel panel2 = new JPanel(new BorderLayout());
        panel2.add(panel1, BorderLayout.NORTH);
        add(panel2, BorderLayout.WEST);
    }
}
