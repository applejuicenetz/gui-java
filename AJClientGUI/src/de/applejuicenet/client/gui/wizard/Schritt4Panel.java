package de.applejuicenet.client.gui.wizard;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextArea;

import de.applejuicenet.client.fassade.shared.ZeichenErsetzer;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/wizard/Schritt4Panel.java,v 1.10 2005/01/18 17:35:28 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 */

public class Schritt4Panel
    extends WizardPanel {
	private JTextArea label1 = new JTextArea();
    private JTextArea label2 = new JTextArea();
    private JComboBox verbindungsart = new JComboBox();

    public Schritt4Panel() {
        super();
        init();
    }

    private void init() {
        label1.setWrapStyleWord(true);
        label1.setLineWrap(true);
        label1.setBackground(Color.WHITE);
        label1.setEditable(false);
        label2.setWrapStyleWord(true);
        label2.setLineWrap(true);
        label2.setBackground(Color.WHITE);
        label2.setEditable(false);

        ConnectionKind[] connections = ConnectionXML.getConnections();
        if (connections != null) {
            for (int i = 0; i < connections.length; i++) {
                verbindungsart.addItem(connections[i]);
            }
            verbindungsart.setSelectedIndex(0);
        }

        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.insets.top = 5;
        constraints.insets.left = 5;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        add(label1, constraints);
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.insets.top = 10;
        add(label2, constraints);
        constraints.gridwidth = 1;
        constraints.gridy = 2;
        add(verbindungsart, constraints);
        constraints.gridx = 1;
        constraints.weightx = 1;
        add(new JLabel(), constraints);
        constraints.weightx = 0;
        constraints.weighty = 1;
        constraints.gridy = 3;
        add(new JLabel(), constraints);
    }

    public ConnectionKind getVerbindungsart() {
        return (ConnectionKind) verbindungsart.getSelectedItem();
    }

    public void fireLanguageChanged() {
        label1.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(".root.javagui.wizard.schritt4.label1")));
        label2.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(".root.javagui.wizard.schritt4.label2")));
    }
}
