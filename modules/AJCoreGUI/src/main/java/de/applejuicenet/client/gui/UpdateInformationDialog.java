/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import de.applejuicenet.client.gui.tray.DesktopTool;
import de.applejuicenet.client.shared.DesktopTools;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import de.applejuicenet.client.fassade.ApplejuiceFassade;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.controller.OptionsManagerImpl;

/**
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author Maj0r <aj@tkl-soft.de>
 */
public class UpdateInformationDialog extends JDialog {
    private String aktuellsteVersion;
    private final JButton schliessen = new JButton();
    private String releaseLink = "";
    private final Logger logger;

    public UpdateInformationDialog(JFrame parentFrame, String aktuellsteVersion, String releaseLink) {
        super(parentFrame, true);
        logger = Logger.getLogger(getClass());
        try {
            this.aktuellsteVersion = aktuellsteVersion;
            this.releaseLink = releaseLink;
            init();
        } catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
            }
        }
    }

    private void init() {
        LanguageSelector ls = LanguageSelector.getInstance();

        schliessen.addActionListener(ae -> UpdateInformationDialog.this.dispose());

        String titel = ls.getFirstAttrbuteByTagName("javagui.startup.newversiontitel");
        String nachricht = ls.getFirstAttrbuteByTagName("javagui.startup.newversionnachricht");

        nachricht = nachricht.replaceFirst("%s", aktuellsteVersion);
        schliessen.setText(ls.getFirstAttrbuteByTagName("javagui.options.plugins.schliessen"));
        setTitle(titel);
        JPanel panel1 = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        constraints.anchor = GridBagConstraints.NORTH;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 3;
        constraints.insets.left = 5;
        constraints.insets.top = 5;
        constraints.insets.right = 5;
        panel1.add(new JLabel(nachricht), constraints);
        constraints.insets.right = 0;
        constraints.gridwidth = 1;
        constraints.gridy = 1;
        JLabel label1 = new JLabel();

        label1.setText(ls.getFirstAttrbuteByTagName("javagui.startup.newversion") + ": ");
        JLabel linkWin = new JLabel("<html><font><u>" + releaseLink + "</u></font></html>");

        panel1.add(label1, constraints);
        constraints.gridx = 1;
        panel1.add(linkWin, constraints);
        constraints.gridx = 2;
        constraints.weightx = 1;
        constraints.insets.right = 5;
        panel1.add(new JLabel(), constraints);
        constraints.insets.right = 0;
        constraints.weightx = 0;
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.insets.bottom = 5;

        linkWin.setForeground(Color.blue);
        linkWin.addMouseListener(new MouseAdapter() {
            public void mouseExited(MouseEvent e) {
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }

            public void mouseEntered(MouseEvent e) {
                setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            public void mouseClicked(MouseEvent e) {
                executeLink(releaseLink);
            }
        });

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(panel1, BorderLayout.CENTER);
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        southPanel.add(schliessen);
        getContentPane().add(southPanel, BorderLayout.SOUTH);
        pack();
        Dimension appDimension = getSize();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        setLocation((screenSize.width - appDimension.width) / 2, (screenSize.height - appDimension.height) / 2);
    }

    private void executeLink(String link) {
        try {
            DesktopTools.browse(new URI(link));
        } catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
            }
        }
    }
}
