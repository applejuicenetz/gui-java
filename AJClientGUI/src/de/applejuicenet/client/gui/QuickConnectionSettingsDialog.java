package de.applejuicenet.client.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.controller.PropertiesManager;
import de.applejuicenet.client.shared.ConnectionSettings;
import de.applejuicenet.client.shared.ZeichenErsetzer;
import de.applejuicenet.client.gui.controller.ApplejuiceFassade;
import de.applejuicenet.client.gui.controller.OptionsManagerImpl;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/Attic/QuickConnectionSettingsDialog.java,v 1.16 2004/03/09 16:25:17 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: QuickConnectionSettingsDialog.java,v $
 * Revision 1.16  2004/03/09 16:25:17  maj0r
 * PropertiesManager besser gekapselt.
 *
 * Revision 1.15  2004/03/05 15:49:39  maj0r
 * PMD-Optimierung
 *
 * Revision 1.14  2004/02/22 08:37:59  maj0r
 * Muell entfernt.
 *
 * Revision 1.13  2004/02/21 18:20:30  maj0r
 * LanguageSelector auf SAX umgebaut.
 *
 * Revision 1.12  2004/02/17 15:26:38  maj0r
 * Bug #219 gefixt (Danke an uselessplayer)
 * 100%-CPU bei Eingabe eines falschen Passwortes beim Anmeldedialog gefixt.
 *
 * Revision 1.11  2004/02/05 23:11:27  maj0r
 * Formatierung angepasst.
 *
 * Revision 1.10  2004/01/29 15:52:33  maj0r
 * Bug #153 umgesetzt (Danke an jr17)
 * Verbindungsdialog kann nun per Option beim naechsten GUI-Start erzwungen werden.
 *
 * Revision 1.9  2003/12/29 16:04:17  maj0r
 * Header korrigiert.
 *
 * Revision 1.8  2003/12/29 09:39:21  maj0r
 * Alte BugIDs entfernt, da auf neuen Bugtracker auf bugs.applejuicenet.de umgestiegen wurde.
 *
 * Revision 1.7  2003/12/27 19:06:33  maj0r
 * Im Verbindungsfenster geht nun ein einfaches <Enter> (Danke an muhviestarr).
 *
 * Revision 1.6  2003/10/14 19:21:23  maj0r
 * Korrekturen zur Xml-Port-Verwendung.
 *
 * Revision 1.5  2003/10/14 15:41:46  maj0r
 * An pflegbaren Xml-Port angepasst.
 *
 * Revision 1.4  2003/09/09 12:28:15  maj0r
 * Wizard fertiggestellt.
 *
 * Revision 1.3  2003/09/04 10:13:28  maj0r
 * Logger eingebaut.
 *
 * Revision 1.2  2003/08/24 14:59:59  maj0r
 * Version 0.14
 * Diverse Aenderungen.
 *
 * Revision 1.1  2003/08/22 10:55:06  maj0r
 * Klassen umbenannt.
 *
 * Revision 1.1  2003/07/01 14:52:36  maj0r
 * Wenn kein Core gefunden wird, können nun die entsprechenden Einstellungen beim Start der GUI angepasst werden.
 *
 *
 */

public class QuickConnectionSettingsDialog
    extends JDialog {
    private ODConnectionPanel remotePanel;
    public static final int ABGEBROCHEN = 1;
    private ConnectionSettings remote;
    private JButton ok = new JButton("OK");
    private JCheckBox cmbNieWiederZeigen = new JCheckBox();
    private Logger logger;
    private boolean dirty = false;

    private int result = 0;

    public QuickConnectionSettingsDialog(Frame parent) {
        super(parent, true);
        logger = Logger.getLogger(getClass());
        try {
            init();
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
            }
        }
    }

    private void init() {
        remote = OptionsManagerImpl.getInstance().getRemoteSettings();
        remotePanel = new ODConnectionPanel(remote, this, true);
        setTitle("appleJuice Client");

        getContentPane().setLayout(new BorderLayout());

        LanguageSelector languageSelector = LanguageSelector.getInstance();
        String nachricht = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(".root.javagui.startup.ueberpruefeEinst"));
        cmbNieWiederZeigen.setText(ZeichenErsetzer.korrigiereUmlaute(
            languageSelector.
            getFirstAttrbuteByTagName(".root.javagui.startup.showdialog")));
        cmbNieWiederZeigen.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent ce) {
                dirty = true;
            }
        });
        cmbNieWiederZeigen.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent ke) {
                if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
                    pressOK();
                }
                else {
                    super.keyPressed(ke);
                }
            }
        });
        JPanel panel2 = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets.top = 5;
        constraints.insets.left = 5;
        constraints.insets.bottom = 5;

        panel2.add(new JLabel(nachricht), constraints);
        constraints.gridx = 1;
        constraints.weightx = 1;
        constraints.insets.right = 5;
        panel2.add(new JLabel("           "), constraints);

        getContentPane().add(panel2, BorderLayout.NORTH);
        getContentPane().add(remotePanel, BorderLayout.CENTER);

        JPanel panel3 = new JPanel(new BorderLayout());
        JPanel panel1 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel1.add(ok);
        JButton abbrechen = new JButton("Abbrechen");
        panel1.add(abbrechen);

        JPanel panel4 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel4.add(cmbNieWiederZeigen);
        panel3.add(panel4, BorderLayout.NORTH);
        panel3.add(panel1, BorderLayout.SOUTH);
        getContentPane().add(panel3, BorderLayout.SOUTH);

        ok.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                speichereEinstellungen();
                result = 0;
                hide();
            }
        });

        abbrechen.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                result = ABGEBROCHEN;
                hide();
            }
        });
        addWindowListener(
            new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                result = ABGEBROCHEN;
                hide();
            }
        });
        pack();
        Dimension appDimension = getSize();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation( (screenSize.width - appDimension.width) / 2,
                    (screenSize.height - appDimension.height) / 2);

    }

    public void pressOK() {
        ok.doClick();
    }

    public int getResult() {
        return result;
    }

    private void speichereEinstellungen(){
        try {
            OptionsManagerImpl.getInstance().onlySaveRemote(remote);
            if (dirty) {
                OptionsManagerImpl.getInstance().
                    showConnectionDialogOnStartup(!cmbNieWiederZeigen.
                                                  isSelected());
            }
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
            }
        }
    }
}
