package de.applejuicenet.client.gui;

import de.applejuicenet.client.shared.NumberInputVerifier;
import de.applejuicenet.client.shared.ZeichenErsetzer;
import de.applejuicenet.client.shared.SoundPlayer;
import de.applejuicenet.client.gui.controller.LanguageSelector;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/Attic/NewServerDialog.java,v 1.5 2003/12/29 16:04:17 maj0r Exp $
 *
 * <p>Titel: AppleJuice Core-GUI</p>
 * <p>Beschreibung: Offizielles GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: NewServerDialog.java,v $
 * Revision 1.5  2003/12/29 16:04:17  maj0r
 * Header korrigiert.
 *
 * Revision 1.4  2003/10/31 19:04:58  maj0r
 * Sounds eingebaut.
 *
 * Revision 1.3  2003/10/21 14:08:45  maj0r
 * Mittels PMD Code verschoenert, optimiert.
 *
 * Revision 1.2  2003/10/01 16:52:53  maj0r
 * Suche weiter gefuehrt.
 * Version 0.32
 *
 * Revision 1.1  2003/10/01 14:46:11  maj0r
 * Server koennen nun manuell hinzugefuegt werden.
 *
 *
 */

public class NewServerDialog extends JDialog {
    private JButton ok = new JButton();
    private JTextField dyn = new JTextField();
    private JTextField port = new JTextField();
    private boolean legal = false;
    private String link = "";
    private Logger logger;

    public NewServerDialog(Frame parent, boolean modal) {
        super(parent, modal);
        logger = Logger.getLogger(getClass());
        try{
            init();
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR))
                logger.error("Unbehandelte Exception", e);
        }
    }

    private void init() {
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                close();
            }
        });
        LanguageSelector languageSelector = LanguageSelector.getInstance();
        setTitle(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                        getFirstAttrbuteByTagName(new String[]{"addserverform", "caption"})));
        ok.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                        getFirstAttrbuteByTagName(new String[]{"addserverform", "okbtn",
                                                                                               "caption"})));
        dyn.setColumns(15);
        port.setColumns(5);
        port.setDocument(new NumberInputVerifier());
        getContentPane().setLayout(new BorderLayout());
        JPanel panel2 = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets.left = 5;
        panel2.add(new JLabel(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                        getFirstAttrbuteByTagName(new String[]{"addserverform", "serverlbl",
                                                                                               "caption"})) + ": "), constraints);
        constraints.gridy = 1;
        panel2.add(new JLabel(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                        getFirstAttrbuteByTagName(new String[]{"addserverform", "portlbl",
                                                                                               "caption"})) + ": "), constraints);
        constraints.insets.left = 0;
        constraints.gridx = 1;
        constraints.gridy = 0;
        panel2.add(dyn, constraints);
        constraints.gridy = 1;
        panel2.add(port, constraints);
        getContentPane().add(panel2, BorderLayout.CENTER);
        JPanel panel1 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel1.add(ok);
        getContentPane().add(panel1, BorderLayout.SOUTH);
        pack();
        port.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent ke){
                if (ke.getKeyCode() == KeyEvent.VK_ENTER){
                    ok.doClick();
                }
            }
        });
        ok.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                if (dyn.getText().length() > 0 && port.getText().length() > 0)
                {
                    legal = true;
                    link = "ajfsp://server|" + dyn.getText() + "|" + port.getText();
                    close();
                }
            }
        });
        SoundPlayer.getInstance().playSound(SoundPlayer.KONKRETISIEREN);
    }

    private void close() {
        dispose();
    }

    public String getLink(){
        return link;
    }

    public boolean isLegal() {
        return legal;
    }
}
