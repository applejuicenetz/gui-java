package de.applejuicenet.client.gui;

import de.applejuicenet.client.shared.Settings;
import de.applejuicenet.client.shared.ZeichenErsetzer;
import de.applejuicenet.client.shared.IconManager;
import de.applejuicenet.client.shared.MultiLineToolTip;
import de.applejuicenet.client.gui.controller.LanguageSelector;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;

import org.apache.log4j.Logger;
import org.apache.log4j.Level;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/Attic/ODAnsichtPanel.java,v 1.5 2003/09/04 10:13:28 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI f?r den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: ODAnsichtPanel.java,v $
 * Revision 1.5  2003/09/04 10:13:28  maj0r
 * Logger eingebaut.
 *
 * Revision 1.4  2003/09/02 16:08:11  maj0r
 * Downloadbaum komplett umgebaut.
 *
 * Revision 1.3  2003/08/25 18:02:10  maj0r
 * Sprachberuecksichtigung und Tooltipps eingebaut.
 *
 * Revision 1.2  2003/08/16 17:49:56  maj0r
 * Diverse Farben können nun manuell eingestellt bzw. deaktiviert werden.
 * DownloaduebersichtTabelle kann deaktiviert werden.
 *
 * Revision 1.1  2003/08/15 18:31:36  maj0r
 * Farbdialog in Optionen eingebaut.
 *
 *
 */

public class ODAnsichtPanel extends JPanel {
    private JLabel farbeFertigerDownload = new JLabel("      ");
    private JLabel farbeQuelle = new JLabel("      ");
    private Settings settings;
    JCheckBox cmbAktiv = new JCheckBox();
    JCheckBox cmbDownloadUebersicht = new JCheckBox();
    private Logger logger;

    public ODAnsichtPanel() {
        logger = Logger.getLogger(getClass());
        try{
            settings = Settings.getSettings();
            init();
        }
        catch (Exception e){
            if (logger.isEnabledFor(Level.ERROR))
                logger.error("Unbehandelte Exception", e);
        }
    }

    private void init() {
        LanguageSelector languageSelector = LanguageSelector.getInstance();
        cmbDownloadUebersicht.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(new String[]{"javagui", "options", "ansicht",
                                                       "downloadansicht"})));
        cmbAktiv.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(new String[]{"javagui", "options", "ansicht",
                                                       "aktiv"})));
        setLayout(new BorderLayout());
        farbeFertigerDownload.setOpaque(true);
        farbeFertigerDownload.setBackground(settings.getDownloadFertigHintergrundColor());
        farbeFertigerDownload.addMouseListener(new ColorChooserMouseAdapter());
        farbeQuelle.setOpaque(true);
        farbeQuelle.setBackground(settings.getQuelleHintergrundColor());
        farbeQuelle.addMouseListener(new ColorChooserMouseAdapter());
        cmbAktiv.setSelected(settings.isFarbenAktiv());
        cmbDownloadUebersicht.setSelected(settings.isDownloadUebersicht());
        cmbDownloadUebersicht.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent ce) {
                settings.setDownloadUebersicht(cmbDownloadUebersicht.isSelected());
            }
        });
        cmbAktiv.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent ce) {
                settings.setFarbenAktiv(cmbAktiv.isSelected());
            }
        });
        IconManager im = IconManager.getInstance();
        ImageIcon icon = im.getIcon("hint");
        JLabel hint1 = new JLabel(icon) {
            public JToolTip createToolTip() {
                MultiLineToolTip tip = new MultiLineToolTip();
                tip.setComponent(this);
                return tip;
            }
        };
        JLabel hint2 = new JLabel(icon) {
            public JToolTip createToolTip() {
                MultiLineToolTip tip = new MultiLineToolTip();
                tip.setComponent(this);
                return tip;
            }
        };
        String tooltipp = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(new String[]{"javagui", "options",
                                                       "ansicht", "ttipp_farbewaehlen"}));
        hint1.setToolTipText(tooltipp);
        hint2.setToolTipText(tooltipp);

        JPanel panel1 = new JPanel();
        panel1.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets.bottom = 5;
        panel1.setBorder(BorderFactory.createTitledBorder(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(new String[]{"javagui", "options", "ansicht",
                                                       "hintergrundfarben"}))));
        panel1.add(cmbAktiv, constraints);
        constraints.gridy = 1;
        constraints.insets.left = 5;
        constraints.insets.right = 5;
        panel1.add(new JLabel(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(new String[]{"javagui", "options", "ansicht",
                                                       "fertigerdownload"}))), constraints);
        constraints.gridy = 2;
        panel1.add(new JLabel(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(new String[]{"javagui", "options", "ansicht",
                                                       "quelle"}))), constraints);
        constraints.gridx = 1;
        constraints.gridy = 1;
        panel1.add(farbeFertigerDownload, constraints);
        constraints.gridy = 2;
        panel1.add(farbeQuelle, constraints);
        constraints.gridx = 2;
        constraints.gridy = 1;
        panel1.add(hint1, constraints);
        constraints.gridy = 2;
        panel1.add(hint2, constraints);
        JPanel panel2 = new JPanel(new BorderLayout());
        panel2.add(panel1, BorderLayout.NORTH);
        panel2.add(cmbDownloadUebersicht, BorderLayout.SOUTH);
        add(panel2, BorderLayout.WEST);
    }

    public void save() {
        try{
            settings.save();
        }
        catch (Exception e){
            if (logger.isEnabledFor(Level.ERROR))
                logger.error("Unbehandelte Exception", e);
        }
    }

    class ColorChooserMouseAdapter
            extends MouseAdapter {
        public void mouseEntered(MouseEvent e) {
            JLabel source = (JLabel) e.getSource();
            source.setBorder(BorderFactory.createLineBorder(Color.black));
        }

        public void mouseClicked(MouseEvent e) {
            LanguageSelector languageSelector = LanguageSelector.getInstance();
            JLabel source = (JLabel) e.getSource();
            JColorChooser jColorChooserBackground = new JColorChooser();
            Color newColor = jColorChooserBackground.showDialog(null, ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                    getFirstAttrbuteByTagName(new String[]{"javagui", "options", "ansicht",
                                                           "hintergrundfarbewaehlen"})),
                    source.getBackground());
            if (newColor != null && newColor.getRGB() != source.getBackground().getRGB()) {
                source.setBackground(newColor);
                if (source==farbeQuelle)
                    settings.setQuelleHintergrundColor(newColor);
                else
                    settings.setDownloadFertigHintergrundColor(newColor);
            }
        }

        public void mouseExited(MouseEvent e) {
            JLabel source = (JLabel) e.getSource();
            source.setBorder(null);
        }
    }
}
