package de.applejuicenet.client.gui.wizard;

import java.io.File;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextArea;

import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.shared.ZeichenErsetzer;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/wizard/Schritt1Panel.java,v 1.10 2004/11/22 16:25:26 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 */

public class Schritt1Panel
    extends WizardPanel {
	private JTextArea label1 = new JTextArea();
    private JComboBox sprachen = new JComboBox();

    public Schritt1Panel() {
        super();
        init();
    }

    private void init() {
        label1.setWrapStyleWord(true);
        label1.setLineWrap(true);
        label1.setEditable(false);
        label1.setBackground(Color.WHITE);

        String path = System.getProperty("user.dir") + File.separator +
            "language" +
            File.separator;
        File languagePath = new File(path);
        if (languagePath.isDirectory()) {
            String[] tempListe = languagePath.list();
            for (int i = 0; i < tempListe.length; i++) {
                int pos = tempListe[i].indexOf(".xml");
                if (pos != -1) {
                    String temp = tempListe[i].substring(0, pos);
                    sprachen.addItem(temp);
                }
            }
        }

        sprachen.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                Object selected = sprachen.getSelectedItem();
                if (selected != null) {
                    String path = System.getProperty("user.dir") +
                        File.separator +
                        "language" + File.separator;
                    String dateiName = path + (String) selected + ".xml";
                    LanguageSelector.getInstance(dateiName);
                }
            }
        });

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
        constraints.gridwidth = 1;
        constraints.gridx = 0;
        constraints.gridy = 1;
        add(sprachen, constraints);
        constraints.gridx = 1;
        constraints.weightx = 1;
        add(new JLabel(), constraints);
        constraints.weightx = 0;
        constraints.gridy = 2;
        constraints.weighty = 1;
        add(new JLabel(), constraints);
    }

    public void fireLanguageChanged() {
        label1.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(".root.javagui.wizard.schritt1.label1")));
    }

}
