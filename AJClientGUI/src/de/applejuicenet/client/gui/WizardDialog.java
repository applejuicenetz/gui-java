package de.applejuicenet.client.gui;

import de.applejuicenet.client.shared.IconManager;
import de.applejuicenet.client.shared.ZeichenErsetzer;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.wizard.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import org.apache.log4j.Logger;
import org.apache.log4j.Level;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/Attic/WizardDialog.java,v 1.2 2003/09/08 14:55:15 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: WizardDialog.java,v $
 * Revision 1.2  2003/09/08 14:55:15  maj0r
 * Wizarddialog weitergefuehrt.
 *
 * Revision 1.1  2003/09/08 06:27:11  maj0r
 * Um Wizard erweitert, aber noch nicht fertiggestellt.
 *
 *
 */


public class WizardDialog extends JDialog {
    private Logger logger;
    private WizardPanel aktuellesPanel;
    private WizardPanel schritt1 = new Schritt1Panel();
    private WizardPanel schritt2 = new Schritt2Panel();
    private WizardPanel schritt3 = new Schritt3Panel();
    private WizardPanel schritt4 = new Schritt4Panel();
    private WizardPanel schritt5 = new Schritt5Panel();
    private JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    private JButton zurueck = new JButton("Zurück");
    private JButton weiter = new JButton("Weiter");
    private JButton ende = new JButton("Ende");

    public WizardDialog(Frame parent, boolean modal) {
        super(parent, modal);
        logger = Logger.getLogger(getClass());
        try{
            init();
        }
        catch (Exception e){
            if (logger.isEnabledFor(Level.ERROR))
                logger.error("Unbehandelte Exception", e);
        }
    }

    private void init() {
        setResizable(false);
        LanguageSelector languageSelector = LanguageSelector.getInstance();
        setTitle(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                   getFirstAttrbuteByTagName(new String[]{"javagui", "wizard", "titel"})));
        getContentPane().setLayout(new BorderLayout());
        ImageIcon icon1 = IconManager.getInstance().getIcon("wizardbanner");
        JLabel label1 = new JLabel(icon1);

        schritt1.setVorherigesPanel(null);
        schritt1.setNaechstesPanel(schritt2);
        schritt2.setVorherigesPanel(schritt1);
        schritt2.setNaechstesPanel(schritt3);
        schritt3.setVorherigesPanel(schritt2);
        schritt3.setNaechstesPanel(schritt4);
        schritt4.setVorherigesPanel(schritt3);
        schritt4.setNaechstesPanel(schritt5);
        schritt5.setVorherigesPanel(schritt4);
        schritt5.setNaechstesPanel(null);
        schritt2.setVisible(false);
        schritt3.setVisible(false);
        schritt4.setVisible(false);
        schritt5.setVisible(false);

        ende.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
                WizardDialog.this.dispose();
            }
        });
        zurueck.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
                if (aktuellesPanel.getVorherigesPanel()!=null){
                    aktuellesPanel.setVisible(false);
                    aktuellesPanel = aktuellesPanel.getVorherigesPanel();
                    aktuellesPanel.setVisible(true);
                    weiter.setEnabled(true);
                    if (aktuellesPanel.getVorherigesPanel()==null){
                        zurueck.setEnabled(false);
                    }
                    else{
                        zurueck.setEnabled(true);
                    }
                }
            }
        });
        weiter.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
                if (aktuellesPanel.getNaechstesPanel()!=null){
                    aktuellesPanel.setVisible(false);
                    aktuellesPanel = aktuellesPanel.getNaechstesPanel();
                    aktuellesPanel.setVisible(true);
                    zurueck.setEnabled(true);
                    if (aktuellesPanel.getNaechstesPanel()==null){
                        weiter.setEnabled(false);
                    }
                    else{
                        weiter.setEnabled(true);
                    }
                }
            }
        });

        buttons.add(zurueck);
        buttons.add(weiter);
        buttons.add(ende);
        zurueck.setEnabled(false);

        getContentPane().add(label1, BorderLayout.NORTH);
        getContentPane().add(schritt1, BorderLayout.CENTER);
        getContentPane().add(schritt2, BorderLayout.CENTER);
        getContentPane().add(schritt3, BorderLayout.CENTER);
        getContentPane().add(schritt4, BorderLayout.CENTER);
        getContentPane().add(schritt5, BorderLayout.CENTER);
        aktuellesPanel = schritt1;
        getContentPane().add(buttons, BorderLayout.SOUTH);
        pack();
    }
}
