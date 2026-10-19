/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.server;

import de.applejuicenet.client.facade.ApplejuiceFacade;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.shared.NumberInputVerifier;
import de.applejuicenet.client.shared.SoundPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Copyright: General Public License
 */
public class NewServerDialog extends JDialog {
    private JButton ok = new JButton();
    private JTextField dynamicDns = new JTextField();
    private JTextField port = new JTextField();
    private boolean legal = false;
    private String link = "";
    private Logger logger;

    public NewServerDialog(Frame parent, boolean modal) {
        super(parent, modal);
        logger = LoggerFactory.getLogger(getClass());
        try {
            init();
        } catch (Exception e) {
            logger.error(ApplejuiceFacade.ERROR_MESSAGE, e);
        }
    }

    private void init() {
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                close();
            }
        });

        LanguageSelector languageSelector = LanguageSelector.getInstance();

        setTitle(languageSelector.getFirstAttributeByTagName("addserverform.caption"));
        ok.setText(languageSelector.getFirstAttributeByTagName("addserverform.okbtn.caption"));
        dynamicDns.setColumns(15);
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
        panel2.add(new JLabel(languageSelector.getFirstAttributeByTagName("addserverform.serverlbl.caption") + ": "), constraints);
        constraints.gridy = 1;
        panel2.add(new JLabel(languageSelector.getFirstAttributeByTagName("addserverform.portlbl.caption") + ": "), constraints);
        constraints.insets.left = 0;
        constraints.gridx = 1;
        constraints.gridy = 0;
        panel2.add(dynamicDns, constraints);
        constraints.gridy = 1;
        panel2.add(port, constraints);
        getContentPane().add(panel2, BorderLayout.CENTER);
        JPanel panel1 = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        panel1.add(ok);
        getContentPane().add(panel1, BorderLayout.SOUTH);
        pack();
        setResizable(false);
        port.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent ke) {
                if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
                    ok.doClick();
                }
            }
        });
        ok.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                if (dynamicDns.getText().length() > 0 && port.getText().length() > 0) {
                    legal = true;
                    link = "ajfsp://server|" + dynamicDns.getText() + "|" + port.getText();
                    close();
                }
            }
        });
        SoundPlayer.getInstance().playSound(SoundPlayer.SPECIFY);
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
