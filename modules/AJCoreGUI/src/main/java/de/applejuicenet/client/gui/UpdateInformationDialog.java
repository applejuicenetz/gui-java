/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui;

import de.applejuicenet.client.fassade.ApplejuiceFassade;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.shared.DesktopTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;

/**
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author Maj0r <aj@tkl-soft.de>
 */
public class UpdateInformationDialog extends JDialog {
    private final JButton schliessen = new JButton();
    private final String titel;
    private final String nachricht;
    private final String linkBeschriftung;
    private final String releaseLink;
    private final Logger logger;

    public UpdateInformationDialog(JFrame parentFrame, String aktuellsteVersion, String releaseLink) {
        this(parentFrame, LanguageSelector.getInstance().getFirstAttrbuteByTagName("javagui.startup.newversiontitel"),
                LanguageSelector.getInstance().getFirstAttrbuteByTagName("javagui.startup.newversionnachricht")
                        .replaceFirst("%s", aktuellsteVersion),
                LanguageSelector.getInstance().getFirstAttrbuteByTagName("javagui.startup.newversion"), releaseLink);
    }

    private UpdateInformationDialog(JFrame parentFrame, String titel, String nachricht, String linkBeschriftung,
                                    String link) {
        super(parentFrame, true);
        logger = LoggerFactory.getLogger(getClass());
        this.titel = titel;
        this.nachricht = nachricht;
        this.linkBeschriftung = linkBeschriftung;
        this.releaseLink = link;
        try {
            init();
        } catch (Exception e) {
            logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
        }
    }

    /** Hinweis "keine neuere Version" mit Link auf das Repository. */
    public static UpdateInformationDialog noNewVersion(JFrame parentFrame) {
        LanguageSelector ls = LanguageSelector.getInstance();

        return new UpdateInformationDialog(parentFrame, ls.getFirstAttrbuteByTagName("mainform.checkupdate.caption"),
                ls.getFirstAttrbuteByTagName("javagui.checkupdate.keineNeueVersion"), "GitHub",
                UpdateChecker.REPOSITORY_URL);
    }

    /** Hinweis "Pruefung fehlgeschlagen" (kein Netz, ungueltige Antwort, unbekannte eigene Version) mit Repository-Link. */
    public static UpdateInformationDialog checkFailed(JFrame parentFrame) {
        LanguageSelector ls = LanguageSelector.getInstance();

        return new UpdateInformationDialog(parentFrame, ls.getFirstAttrbuteByTagName("mainform.checkupdate.caption"),
                ls.getFirstAttrbuteByTagName("javagui.checkupdate.fehler"), "GitHub", UpdateChecker.REPOSITORY_URL);
    }

    private void init() {
        LanguageSelector ls = LanguageSelector.getInstance();

        schliessen.addActionListener(ae -> UpdateInformationDialog.this.dispose());
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

        label1.setText(linkBeschriftung + ": ");
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
        DialogLocation.center(this);
    }

    private void executeLink(String link) {
        try {
            DesktopTools.browse(new URI(link));
        } catch (Exception e) {
            logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
        }
    }
}
