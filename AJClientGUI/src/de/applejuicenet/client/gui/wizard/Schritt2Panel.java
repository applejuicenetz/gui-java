package de.applejuicenet.client.gui.wizard;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;

import de.applejuicenet.client.fassade.shared.ZeichenErsetzer;
import de.tklsoft.gui.controls.TKLLabel;
import de.tklsoft.gui.controls.TKLTextArea;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/wizard/Schritt2Panel.java,v 1.12 2005/02/22 09:21:07 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 */

public class Schritt2Panel
    extends WizardPanel {
	private TKLLabel label1 = new TKLLabel();
    private TKLTextArea textArea1 = new TKLTextArea();
    private TKLTextArea textArea2 = new TKLTextArea();

    public Schritt2Panel() {
        super();
        init();
    }

    private void init() {
        textArea1.setWrapStyleWord(true);
        textArea2.setWrapStyleWord(true);
        textArea1.setLineWrap(true);
        textArea1.setEditable(false);
        textArea2.setLineWrap(true);
        textArea1.setBackground(Color.WHITE);
        textArea2.setBackground(Color.WHITE);
        textArea2.setEditable(false);
        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.insets.top = 5;
        constraints.insets.left = 5;
        constraints.gridx = 0;
        constraints.gridy = 0;
        add(label1, constraints);
        constraints.gridx = 1;
        constraints.weightx = 1;
        add(new JLabel(), constraints);
        constraints.weightx = 1;
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 2;
        add(textArea1, constraints);
        constraints.gridy = 2;
        add(textArea2, constraints);
        constraints.gridy = 3;
        constraints.weighty = 1;
        add(new JLabel(), constraints);
    }

    public void fireLanguageChanged() {
        label1.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(".root.javagui.wizard.schritt2.label1")));
        textArea1.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(".root.javagui.wizard.schritt2.label2")));
        textArea2.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(".root.javagui.wizard.schritt2.label3")));
    }

}
