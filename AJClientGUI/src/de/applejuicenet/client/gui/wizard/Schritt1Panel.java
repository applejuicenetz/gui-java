package de.applejuicenet.client.gui.wizard;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;

import javax.swing.JLabel;

import de.applejuicenet.client.fassade.shared.ZeichenErsetzer;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.tklsoft.gui.controls.TKLComboBox;
import de.tklsoft.gui.controls.TKLTextArea;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/wizard/Schritt1Panel.java,v 1.12 2005/02/22 09:21:07 maj0r Exp $
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
	private TKLTextArea erlaeuterung = new TKLTextArea();
    private TKLComboBox sprachen = new TKLComboBox();

    public Schritt1Panel() {
        super();
        init();
    }

    private void init() {
        erlaeuterung.setWrapStyleWord(true);
        erlaeuterung.setLineWrap(true);
        erlaeuterung.setEditable(false);
        erlaeuterung.setBackground(Color.WHITE);

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
        add(erlaeuterung, constraints);
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
        erlaeuterung.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(".root.javagui.wizard.schritt1.label1")));
    }

}
