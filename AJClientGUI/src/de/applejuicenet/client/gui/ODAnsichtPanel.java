package de.applejuicenet.client.gui;

import de.applejuicenet.client.shared.Settings;
import de.applejuicenet.client.gui.tables.download.DownloadNode;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/Attic/ODAnsichtPanel.java,v 1.2 2003/08/16 17:49:56 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI f?r den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: ODAnsichtPanel.java,v $
 * Revision 1.2  2003/08/16 17:49:56  maj0r
 * Diverse Farben können nun manuell eingestellt bzw. deaktiviert werden.
 * DownloaduebersichtTabelle kann deaktiviert werden.
 *
 * Revision 1.1  2003/08/15 18:31:36  maj0r
 * Farbdialog in Optionen eingebaut.
 *
 *
 */

public class ODAnsichtPanel extends JPanel{
    private JLabel farbeFertigerDownload = new JLabel("      ");
    private JLabel farbeQuelle = new JLabel("      ");
    private Settings settings;
    JCheckBox cmbAktiv = new JCheckBox("aktiv");
    JCheckBox cmbDownloadUebersicht = new JCheckBox("Downloadübersicht anzeigen");

    public ODAnsichtPanel() {
        settings = Settings.getSettings();
        init();
    }

    private void init(){
        setLayout(new BorderLayout());
        farbeFertigerDownload.setOpaque(true);
        farbeFertigerDownload.setBackground(settings.getDownloadFertigHintergrundColor());
        farbeFertigerDownload.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent me){
                JColorChooser jColorChooserBackground = new JColorChooser();
                Color newColor = jColorChooserBackground.showDialog(null, "Hintergrundfarbe auswählen",
                farbeFertigerDownload.getBackground());
                if (newColor.getRGB()!=farbeFertigerDownload.getBackground().getRGB()){
                    farbeFertigerDownload.setBackground(newColor);
                    settings.setDownloadFertigHintergrundColor(newColor);
                }
            }
        });
        farbeQuelle.setOpaque(true);
        farbeQuelle.setBackground(settings.getQuelleHintergrundColor());
        farbeQuelle.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent me){
                JColorChooser jColorChooserBackground = new JColorChooser();
                Color newColor = jColorChooserBackground.showDialog(null, "Hintergrundfarbe auswählen",
                        farbeQuelle.getBackground());
                if (newColor.getRGB()!=farbeQuelle.getBackground().getRGB()){
                    farbeQuelle.setBackground(newColor);
                    settings.setQuelleHintergrundColor(newColor);
                }
            }
        });
        cmbAktiv.setSelected(settings.isFarbenAktiv());
        cmbDownloadUebersicht.setSelected(settings.isDownloadUebersicht());
        cmbDownloadUebersicht.addChangeListener(new ChangeListener(){
            public void stateChanged(ChangeEvent ce){
                settings.setDownloadUebersicht(cmbDownloadUebersicht.isSelected());
            }
        });
        cmbAktiv.addChangeListener(new ChangeListener(){
            public void stateChanged(ChangeEvent ce){
                settings.setFarbenAktiv(cmbAktiv.isSelected());
            }
        });
        JPanel panel1 = new JPanel();
        panel1.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets.bottom = 5;
        panel1.setBorder(BorderFactory.createTitledBorder("Hintergrundfarben"));
        panel1.add(cmbAktiv, constraints);
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
        panel2.add(cmbDownloadUebersicht, BorderLayout.SOUTH);
        add(panel2, BorderLayout.WEST);
    }

    public void save(){
        settings.save();
    }
}
