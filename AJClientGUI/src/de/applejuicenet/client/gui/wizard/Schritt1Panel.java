package de.applejuicenet.client.gui.wizard;

import de.applejuicenet.client.shared.ZeichenErsetzer;
import de.applejuicenet.client.gui.controller.LanguageSelector;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.io.File;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/wizard/Schritt1Panel.java,v 1.3 2003/09/09 12:28:15 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: Schritt1Panel.java,v $
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

public class Schritt1Panel extends WizardPanel{
    private JTextArea label1 = new JTextArea();
    private JComboBox sprachen = new JComboBox();

    public Schritt1Panel(){
        super();
        init();
    }

    private void init(){
        label1.setWrapStyleWord(true);
        label1.setLineWrap(true);
        label1.setEditable(false);
        label1.setBackground(Color.WHITE);

        String path = System.getProperty("user.dir") + File.separator + "language" +
                File.separator;
        File languagePath = new File(path);
        if (languagePath.isDirectory())
        {
            String[] tempListe = languagePath.list();
            for (int i = 0; i < tempListe.length; i++)
            {
                int pos = tempListe[i].indexOf(".xml");
                if (pos != -1)
                {
                    String temp = tempListe[i].substring(0, pos);
                    sprachen.addItem(temp);
                }
            }
        }

        sprachen.addItemListener(new ItemListener(){
            public void itemStateChanged(ItemEvent e){
                Object selected = sprachen.getSelectedItem();
                if (selected!=null){
                    String path = System.getProperty("user.dir") + File.separator +
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
                                                   getFirstAttrbuteByTagName(new String[]{"javagui", "wizard", "schritt1", "label1"})));
    }

}
