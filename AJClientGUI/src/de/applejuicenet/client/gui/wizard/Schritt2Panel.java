package de.applejuicenet.client.gui.wizard;

import de.applejuicenet.client.shared.ZeichenErsetzer;

import javax.swing.*;
import java.awt.*;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/wizard/Schritt2Panel.java,v 1.3 2003/09/09 12:28:15 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: Schritt2Panel.java,v $
 * Revision 1.3  2003/09/09 12:28:15  maj0r
 * Wizard fertiggestellt.
 *
 * Revision 1.2  2003/09/09 06:37:36  maj0r
 * Wizard erweitert, aber noch nicht fertiggestellt.
 *
 * Revision 1.1  2003/09/08 14:55:09  maj0r
 * Wizarddialog weitergefuehrt.
 *
 *
 */

public class Schritt2Panel extends WizardPanel{
    private JLabel label1 = new JLabel();
    private JTextArea textArea1 = new JTextArea();
    private JTextArea textArea2 = new JTextArea();

    public Schritt2Panel(){
        super();
        init();
    }

    private void init(){
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
                                                   getFirstAttrbuteByTagName(new String[]{"javagui", "wizard", "schritt2", "label1"})));
        textArea1.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                   getFirstAttrbuteByTagName(new String[]{"javagui", "wizard", "schritt2", "label2"})));
        textArea2.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                   getFirstAttrbuteByTagName(new String[]{"javagui", "wizard", "schritt2", "label3"})));
    }

}
