package de.applejuicenet.client.gui.wizard;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import javax.swing.JTextArea;

import de.applejuicenet.client.shared.ZeichenErsetzer;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/wizard/Schritt2Panel.java,v 1.7 2004/10/06 12:29:14 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 */

public class Schritt2Panel
    extends WizardPanel {
    private static final long serialVersionUID = -6518468998057003406L;
	private JLabel label1 = new JLabel();
    private JTextArea textArea1 = new JTextArea();
    private JTextArea textArea2 = new JTextArea();

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
