package de.applejuicenet.client.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import de.applejuicenet.client.gui.controller.ApplejuiceFassade;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.controller.PropertiesManager;
import de.applejuicenet.client.gui.listener.LanguageListener;
import de.applejuicenet.client.gui.wizard.ConnectionKind;
import de.applejuicenet.client.gui.wizard.Schritt1Panel;
import de.applejuicenet.client.gui.wizard.Schritt2Panel;
import de.applejuicenet.client.gui.wizard.Schritt3Panel;
import de.applejuicenet.client.gui.wizard.Schritt4Panel;
import de.applejuicenet.client.gui.wizard.Schritt5Panel;
import de.applejuicenet.client.gui.wizard.WizardPanel;
import de.applejuicenet.client.shared.AJSettings;
import de.applejuicenet.client.shared.IconManager;
import de.applejuicenet.client.shared.ZeichenErsetzer;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/Attic/WizardDialog.java,v 1.8 2004/02/05 23:11:27 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: WizardDialog.java,v $
 * Revision 1.8  2004/02/05 23:11:27  maj0r
 * Formatierung angepasst.
 *
 * Revision 1.7  2004/02/04 15:38:18  maj0r
 * Wizarddialog korrigiert
 * Nickname wird nun auf Richtigkeit geprueft und gespeichert wird erst nach Durchlaufen des gesamten Wizards.
 *
 * Revision 1.6  2003/12/29 16:04:17  maj0r
 * Header korrigiert.
 *
 * Revision 1.5  2003/09/10 13:28:22  maj0r
 * Wizard um neue Option MaxNewConnectionsPerTurn erweitert.
 *
 * Revision 1.4  2003/09/09 12:28:15  maj0r
 * Wizard fertiggestellt.
 *
 * Revision 1.3  2003/09/09 06:37:36  maj0r
 * Wizard erweitert, aber noch nicht fertiggestellt.
 *
 * Revision 1.2  2003/09/08 14:55:15  maj0r
 * Wizarddialog weitergefuehrt.
 *
 * Revision 1.1  2003/09/08 06:27:11  maj0r
 * Um Wizard erweitert, aber noch nicht fertiggestellt.
 *
 *
 */

public class WizardDialog
    extends JDialog
    implements LanguageListener {
    private Logger logger;
    private WizardPanel aktuellesPanel;
    private WizardPanel schritt1 = new Schritt1Panel();
    private WizardPanel schritt2 = new Schritt2Panel();
    private WizardPanel schritt3 = new Schritt3Panel(this);
    private WizardPanel schritt4 = new Schritt4Panel();
    private WizardPanel schritt5 = new Schritt5Panel();
    private JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    private JButton zurueck = new JButton();
    private JButton weiter = new JButton();
    private JButton ende = new JButton();
    private AJSettings ajSettings;

    public WizardDialog(Frame parent, boolean modal) {
        super(parent, modal);
        logger = Logger.getLogger(getClass());
        try {
            init();
            LanguageSelector ls = LanguageSelector.getInstance();
            ls.addLanguageListener(this);
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", e);
            }
        }
    }

    private void init() {
        ajSettings = ApplejuiceFassade.getInstance().getAJSettings();
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

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                LanguageSelector.getInstance().removeLanguageListener(
                    WizardDialog.this);
                PropertiesManager.getOptionsManager().setErsterStart(false);
                dispose();
            }
        });
        ende.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                close();
            }
        });
        zurueck.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                if (aktuellesPanel.getVorherigesPanel() != null) {
                    setEndeEnabled(false);
                    getContentPane().remove(aktuellesPanel);
                    aktuellesPanel = aktuellesPanel.getVorherigesPanel();
                    getContentPane().add(aktuellesPanel, BorderLayout.CENTER);
                    weiter.setEnabled(true);
                    if (aktuellesPanel.getVorherigesPanel() == null) {
                        zurueck.setEnabled(false);
                    }
                    else {
                        zurueck.setEnabled(true);
                    }
                    if (aktuellesPanel == schritt3) {
                        if ( ( (Schritt3Panel) schritt3).isValidNickname()) {
                            setWeiterEnabled(true);
                        }
                        else {
                            setWeiterEnabled(false);
                        }
                    }
                    else {
                        setWeiterEnabled(true);
                    }
                    WizardDialog.this.validate();
                    WizardDialog.this.repaint();
                }
            }
        });
        weiter.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                if (aktuellesPanel.getNaechstesPanel() != null) {
                    getContentPane().remove(aktuellesPanel);
                    aktuellesPanel = aktuellesPanel.getNaechstesPanel();
                    getContentPane().add(aktuellesPanel, BorderLayout.CENTER);
                    zurueck.setEnabled(true);
                    if (aktuellesPanel.getNaechstesPanel() == null) {
                        weiter.setEnabled(false);
                        setEndeEnabled(true);
                    }
                    else {
                        setEndeEnabled(false);
                        if (aktuellesPanel == schritt3) {
                            if ( ( (Schritt3Panel) schritt3).isValidNickname()) {
                                setWeiterEnabled(true);
                            }
                            else {
                                setWeiterEnabled(false);
                            }
                        }
                        else {
                            setWeiterEnabled(true);
                        }
                    }
                    WizardDialog.this.validate();
                    WizardDialog.this.repaint();
                }
            }
        });
        ende.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                close();
            }
        });
        buttons.add(zurueck);
        buttons.add(weiter);
        buttons.add(ende);
        buttons.setBackground(Color.WHITE);
        zurueck.setEnabled(false);
        ende.setEnabled(false);

        getContentPane().add(label1, BorderLayout.NORTH);
        getContentPane().add(schritt1, BorderLayout.CENTER);
        aktuellesPanel = schritt1;
        getContentPane().add(buttons, BorderLayout.SOUTH);
        setSize(icon1.getIconWidth(),
                icon1.getIconHeight() + 180 + buttons.getPreferredSize().height);
        setResizable(false);
        fireLanguageChanged();
    }

    private void close() {
        LanguageSelector.getInstance().removeLanguageListener(this);
        if ( ( (Schritt3Panel) schritt3).isValidNickname()) {
            ConnectionKind connection = ( (Schritt4Panel) schritt4).
                getVerbindungsart();
            ajSettings.setNick( ( (Schritt3Panel) schritt3).getNickname());
            ajSettings.setMaxUpload(connection.getMaxUpload() * 1024);
            ajSettings.setMaxDownload(connection.getMaxDownload() * 1024);
            ajSettings.setMaxNewConnectionsPerTurn(connection.
                getMaxNewConnectionsPro10Sek());
            ApplejuiceFassade.getInstance().saveAJSettings(ajSettings);
        }
        PropertiesManager.getOptionsManager().setErsterStart(false);
        dispose();
    }

    public void setWeiterEnabled(boolean enabled) {
        weiter.setEnabled(enabled);
    }

    public void setEndeEnabled(boolean enabled) {
        ende.setEnabled(enabled);
    }

    public void fireLanguageChanged() {
        LanguageSelector languageSelector = LanguageSelector.getInstance();
        setTitle(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(new String[] {"javagui", "wizard",
                                      "titel"})));
        zurueck.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(new String[] {"javagui", "wizard",
                                      "zurueck"})));
        weiter.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(new String[] {"javagui", "wizard",
                                      "weiter"})));
        ende.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(new String[] {"javagui", "wizard", "ende"})));
        schritt1.fireLanguageChanged();
        schritt2.fireLanguageChanged();
        schritt3.fireLanguageChanged();
        schritt4.fireLanguageChanged();
        schritt5.fireLanguageChanged();
        repaint();
    }
}
