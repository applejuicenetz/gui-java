package de.applejuicenet.client.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import de.applejuicenet.client.gui.controller.*;
import de.applejuicenet.client.shared.*;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/Attic/ODConnectionPanel.java,v 1.5 2003/10/14 19:21:23 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: ODConnectionPanel.java,v $
 * Revision 1.5  2003/10/14 19:21:23  maj0r
 * Korrekturen zur Xml-Port-Verwendung.
 *
 * Revision 1.4  2003/10/14 15:43:52  maj0r
 * An pflegbaren Xml-Port angepasst.
 *
 * Revision 1.3  2003/09/09 12:28:15  maj0r
 * Wizard fertiggestellt.
 *
 * Revision 1.2  2003/09/04 10:13:28  maj0r
 * Logger eingebaut.
 *
 * Revision 1.1  2003/08/22 10:55:06  maj0r
 * Klassen umbenannt.
 *
 * Revision 1.4  2003/08/02 12:03:38  maj0r
 * An neue Schnittstelle angepasst.
 *
 * Revision 1.3  2003/06/10 12:31:03  maj0r
 * Historie eingefügt.
 *
 *
 */

public class ODConnectionPanel
        extends JPanel {
    private boolean dirty = false;
    private JLabel label1;
    private JLabel label2;
    private JLabel label3;
    private JLabel label4;
    private JTextField host = new JTextField();
    private JTextField port = new JTextField();
    private JPasswordField passwortAlt = new JPasswordField();
    private JPasswordField passwortNeu = new JPasswordField();
    private ConnectionSettings remote;
    private Logger logger;
    private boolean showPort = false;

    public ODConnectionPanel(ConnectionSettings remote) {
        logger = Logger.getLogger(getClass());
        try
        {
            this.remote = remote;
            init();
        }
        catch (Exception e)
        {
            if (logger.isEnabledFor(Level.ERROR))
                logger.error("Unbehandelte Exception", e);
        }
    }

    public ODConnectionPanel(ConnectionSettings remote, boolean showPort) {
        logger = Logger.getLogger(getClass());
        try
        {
            this.showPort = showPort;
            this.remote = remote;
            init();
        }
        catch (Exception e)
        {
            if (logger.isEnabledFor(Level.ERROR))
                logger.error("Unbehandelte Exception", e);
        }
    }

    private void init() throws Exception {
        setLayout(new BorderLayout());
        JPanel panel1 = new JPanel(new GridBagLayout());
        FlowLayout flowL = new FlowLayout();
        flowL.setAlignment(FlowLayout.RIGHT);
        JPanel panel2 = new JPanel(flowL);

        LanguageSelector languageSelector = LanguageSelector.getInstance();
        label1 = new JLabel(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                              getFirstAttrbuteByTagName(new String[]{"javagui", "options", "remote",
                                                                                                     "host"})));
        label2 = new JLabel(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                              getFirstAttrbuteByTagName(new String[]{"javagui", "options", "remote",
                                                                                                     "passwortAlt"})));
        label3 = new JLabel(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                              getFirstAttrbuteByTagName(new String[]{"javagui", "options", "remote",
                                                                                                     "passwortNeu"})));
        label4 = new JLabel("Port");

        host.setText(remote.getHost());
        host.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                if (remote.getHost().compareTo(host.getText()) != 0)
                {
                    dirty = true;
                    remote.setHost(host.getText());
                }
            }
        });
        passwortAlt.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                dirty = true;
                remote.setOldPassword(new String(passwortAlt.getPassword()));
            }
        });
        passwortNeu.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                dirty = true;
                remote.setNewPassword(new String(passwortNeu.getPassword()));
            }
        });
        port.setDocument(new NumberInputVerifier());
        port.setText(Integer.toString(remote.getXmlPort()));
        port.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                dirty = true;
                remote.setXmlPort(Integer.parseInt(port.getText()));
            }
        });

        enableControls(true);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets.top = 5;
        constraints.insets.left = 5;

        panel1.add(label1, constraints);

        int gridy = 1;
        if (showPort){
            constraints.gridy = gridy;
            gridy++;
            panel1.add(label4, constraints);
        }
        constraints.gridy = gridy;
        gridy++;
        panel1.add(label2, constraints);

        constraints.gridy = gridy;
        gridy++;
        panel1.add(label3, constraints);

        constraints.insets.right = 5;
        constraints.gridy = 0;
        constraints.gridx = 1;
        constraints.weightx = 1;
        panel1.add(host, constraints);

        gridy = 1;
        if (showPort){
            constraints.gridy = gridy;
            gridy++;
            panel1.add(port, constraints);
        }
        constraints.gridy = gridy;
        gridy++;
        panel1.add(passwortAlt, constraints);

        constraints.gridy = gridy;
        gridy++;
        panel1.add(passwortNeu, constraints);

        constraints.gridy = gridy;
        gridy++;
        constraints.gridx = 0;
        constraints.gridwidth = 2;
        panel1.add(panel2, constraints);

        add(panel1, BorderLayout.NORTH);
    }

    public boolean isDirty() {
        return dirty;
    }

    public void enableControls(boolean enable) {
        host.setEnabled(enable);
        passwortAlt.setEnabled(enable);
        passwortNeu.setEnabled(enable);
        label1.setEnabled(enable);
        label2.setEnabled(enable);
        label3.setEnabled(enable);
    }
}