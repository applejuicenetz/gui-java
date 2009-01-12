package de.applejuicenet.client.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import de.applejuicenet.client.fassade.ApplejuiceFassade;
import de.applejuicenet.client.fassade.shared.ZeichenErsetzer;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.controller.OptionsManagerImpl;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/UpdateInformationDialog.java,v 1.10 2009/01/12 09:02:56 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 */

public class UpdateInformationDialog
    extends JDialog {
	private String aktuellsteVersion;
    private JButton schliessen = new JButton();
    private String windowsLink = "";
    private String sonstigeLink = "";
    private Logger logger;

    public UpdateInformationDialog(JFrame parentFrame, String aktuellsteVersion,
                                   String winLink, String sonstigeLink) {
        super(parentFrame, true);
        logger = Logger.getLogger(getClass());
        try {
            this.aktuellsteVersion = aktuellsteVersion;
            this.windowsLink = winLink;
            this.sonstigeLink = sonstigeLink;
            init();
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
            }
        }
    }

    private void init() {
        LanguageSelector ls = LanguageSelector.
            getInstance();
        schliessen.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                UpdateInformationDialog.this.dispose();
            }
        });
        String titel = ZeichenErsetzer.korrigiereUmlaute(ls.
            getFirstAttrbuteByTagName("javagui.startup.newversiontitel"));
        String nachricht = ZeichenErsetzer.korrigiereUmlaute(ls.
            getFirstAttrbuteByTagName("javagui.startup.newversionnachricht"));
        nachricht = nachricht.replaceFirst("%s", aktuellsteVersion);
        schliessen.setText(ZeichenErsetzer.korrigiereUmlaute(
            ls.getFirstAttrbuteByTagName("javagui.options.plugins.schliessen")));
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
        label1.setText(ZeichenErsetzer.korrigiereUmlaute(ls.
            getFirstAttrbuteByTagName("javagui.startup.windowsversion")) + ": ");
        JLabel linkWin = new JLabel("<html><font><u>" + windowsLink +
                                    "</u></font></html>");
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
        JLabel label2 = new JLabel();
        label2.setText(ZeichenErsetzer.korrigiereUmlaute(ls.
            getFirstAttrbuteByTagName("javagui.startup.sonstigeversionen")) + ": ");
        JLabel linkSonstige = new JLabel("<html><font><u>" + sonstigeLink +
                                         "</u></font></html>");
        panel1.add(label2, constraints);
        constraints.gridx = 1;
        panel1.add(linkSonstige, constraints);
        constraints.gridx = 2;
        constraints.weightx = 1;
        constraints.insets.right = 5;
        panel1.add(new JLabel(), constraints);
        constraints.insets.right = 0;
        constraints.weightx = 0;

        linkWin.setForeground(Color.blue);
        linkWin.addMouseListener(new MouseAdapter() {
            public void mouseExited(MouseEvent e) {
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }

            public void mouseEntered(MouseEvent e) {
                setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            public void mouseClicked(MouseEvent e) {
                executeLink(windowsLink);
            }
        });

        linkSonstige.setForeground(Color.blue);
        linkSonstige.addMouseListener(new MouseAdapter() {
            public void mouseExited(MouseEvent e) {
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }

            public void mouseEntered(MouseEvent e) {
                setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            public void mouseClicked(MouseEvent e) {
                executeLink(sonstigeLink);
            }
        });

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(panel1, BorderLayout.CENTER);
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        southPanel.add(schliessen);
        getContentPane().add(southPanel, BorderLayout.SOUTH);
        pack();
        Dimension appDimension = getSize();
        Dimension screenSize = Toolkit.getDefaultToolkit().
            getScreenSize();
        setLocation( (screenSize.width -
                      appDimension.width) / 2,
                    (screenSize.height -
                     appDimension.height) / 2);
    }

    private void executeLink(String link) {
        try {
            String browser = OptionsManagerImpl.getInstance().
                getStandardBrowser();
            try {
                Runtime.getRuntime().exec(new String[] {browser, link});
            }
            catch (Exception ex) {
                LanguageSelector ls = LanguageSelector.
                    getInstance();
                String nachricht = ZeichenErsetzer.korrigiereUmlaute(ls.
                    getFirstAttrbuteByTagName("javagui.startup.updatefehlernachricht"));
                String titel = ZeichenErsetzer.korrigiereUmlaute(ls.
                    getFirstAttrbuteByTagName("mainform.caption"));
                setVisible(false);
                JOptionPane.showMessageDialog(this, nachricht,
                                              titel, JOptionPane.INFORMATION_MESSAGE);
                setVisible(true);
            }
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
            }
        }
    }
}
