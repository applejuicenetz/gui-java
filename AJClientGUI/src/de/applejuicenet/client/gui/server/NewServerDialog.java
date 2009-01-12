package de.applejuicenet.client.gui.server;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import de.applejuicenet.client.fassade.ApplejuiceFassade;
import de.applejuicenet.client.fassade.shared.ZeichenErsetzer;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.shared.NumberInputVerifier;
import de.applejuicenet.client.shared.SoundPlayer;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/server/NewServerDialog.java,v 1.4 2009/01/12 09:02:56 maj0r Exp $
 *
 * <p>Titel: AppleJuice Core-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 */

public class NewServerDialog
    extends JDialog {
    
    private JButton ok = new JButton();
    private JTextField dyn = new JTextField();
    private JTextField port = new JTextField();
    private boolean legal = false;
    private String link = "";
    private Logger logger;

    public NewServerDialog(Frame parent, boolean modal) {
        super(parent, modal);
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
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                close();
            }
        });
        LanguageSelector languageSelector = LanguageSelector.getInstance();
        setTitle(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName("addserverform.caption")));
        ok.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName("addserverform.okbtn.caption")));
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
        panel2.add(new JLabel(ZeichenErsetzer.korrigiereUmlaute(
            languageSelector.
            getFirstAttrbuteByTagName("addserverform.serverlbl.caption")) + ": "), constraints);
        constraints.gridy = 1;
        panel2.add(new JLabel(ZeichenErsetzer.korrigiereUmlaute(
            languageSelector.
            getFirstAttrbuteByTagName("addserverform.portlbl.caption")) + ": "), constraints);
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
            public void keyPressed(KeyEvent ke) {
                if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
                    ok.doClick();
                }
            }
        });
        ok.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                if (dyn.getText().length() > 0 && port.getText().length() > 0) {
                    legal = true;
                    link = "ajfsp://server|" + dyn.getText() + "|" +
                        port.getText();
                    close();
                }
            }
        });
        SoundPlayer.getInstance().playSound(SoundPlayer.KONKRETISIEREN);
    }

    private void close() {
        dispose();
    }

    public String getLink() {
        return link;
    }

    public boolean isLegal() {
        return legal;
    }
}
