package de.applejuicenet.client.gui;

import de.applejuicenet.client.gui.controller.PropertiesManager;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.shared.exception.InvalidPasswordException;
import de.applejuicenet.client.shared.ZeichenErsetzer;
import de.applejuicenet.client.shared.ConnectionSettings;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;

import org.apache.log4j.Logger;
import org.apache.log4j.Level;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/Attic/QuickConnectionSettingsDialog.java,v 1.9 2003/12/29 16:04:17 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI f?r den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: QuickConnectionSettingsDialog.java,v $
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

public class QuickConnectionSettingsDialog extends JDialog {
    private ODConnectionPanel remotePanel;
    public static final int ABGEBROCHEN = 1;
    private ConnectionSettings remote;
    private JButton ok = new JButton("OK");
    private Logger logger;

    private int result = 0;

    public QuickConnectionSettingsDialog(Frame parent){
        super(parent, true);
        logger = Logger.getLogger(getClass());
        try{
            init();
        }
        catch (Exception e)
        {
            if (logger.isEnabledFor(Level.ERROR))
                logger.error("Unbehandelte Exception", e);
        }
    }

    private void init(){
        remote = PropertiesManager.getOptionsManager().getRemoteSettings();
        remotePanel = new ODConnectionPanel(remote, this, true);
        setTitle("appleJuice Client");

        getContentPane().setLayout(new BorderLayout());

        LanguageSelector languageSelector = LanguageSelector.getInstance();
        String nachricht = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
              getFirstAttrbuteByTagName(new String[] {"javagui", "startup",
                                        "ueberpruefeEinst"}));

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

        JPanel panel1 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel1.add(ok);
        JButton abbrechen = new JButton("Abbrechen");
        panel1.add(abbrechen);

        getContentPane().add(panel1, BorderLayout.SOUTH);

        ok.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
                try {
                    speichereEinstellungen();
                    result = 0;
                    hide();
                } catch (InvalidPasswordException e) {
                    zeigeFehlerNachricht();
                }
            }
        });

        abbrechen.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
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

    public void pressOK(){
        ok.doClick();
    }

    public int getResult(){
        return result;
    }

    private void zeigeFehlerNachricht(){
        JOptionPane.showMessageDialog(this, "Falsches Passwort", "Fehler", JOptionPane.ERROR_MESSAGE);
    }

    private void speichereEinstellungen() throws InvalidPasswordException {
        try{
            PropertiesManager.getOptionsManager().saveRemote(remote);
        }
        catch (Exception e)
        {
            if (e.getClass()==InvalidPasswordException.class){
                throw (InvalidPasswordException)e;
            }
            else{
                if (logger.isEnabledFor(Level.ERROR))
                    logger.error("Unbehandelte Exception", e);
            }
        }
    }
}
