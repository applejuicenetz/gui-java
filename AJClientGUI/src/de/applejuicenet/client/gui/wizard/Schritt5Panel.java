package de.applejuicenet.client.gui.wizard;

import de.applejuicenet.client.shared.ZeichenErsetzer;

import javax.swing.*;
import java.awt.*;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/wizard/Schritt5Panel.java,v 1.2 2003/09/09 12:28:15 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: Schritt5Panel.java,v $
 * Revision 1.2  2003/09/09 12:28:15  maj0r
 * Wizard fertiggestellt.
 *
 * Revision 1.1  2003/09/08 14:55:09  maj0r
 * Wizarddialog weitergefuehrt.
 *
 *
 */

public class Schritt5Panel extends WizardPanel{
    private JTextArea label1 = new JTextArea();
    private JTextArea label2 = new JTextArea();

    public Schritt5Panel(){
        super();
        init();
    }

    private void init(){
        label1.setWrapStyleWord(true);
        label1.setLineWrap(true);
        label1.setBackground(Color.WHITE);
        label1.setEditable(false);
        label2.setWrapStyleWord(true);
        label2.setLineWrap(true);
        label2.setBackground(Color.WHITE);
        label2.setEditable(false);

        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.insets.top = 5;
        constraints.insets.left = 5;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        add(label1, constraints);
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.insets.top = 10;
        add(label2, constraints);
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.weightx = 1;
        constraints.weighty = 1;
        add(new JLabel(), constraints);
    }

    public void fireLanguageChanged() {
        label1.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                   getFirstAttrbuteByTagName(new String[]{"javagui", "wizard", "schritt5", "label1"})));
        label2.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                   getFirstAttrbuteByTagName(new String[]{"javagui", "wizard", "schritt5", "label2"})));
    }

}
