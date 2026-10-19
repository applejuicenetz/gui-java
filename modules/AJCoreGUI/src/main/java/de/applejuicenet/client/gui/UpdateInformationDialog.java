/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui;

import de.applejuicenet.client.facade.ApplejuiceFacade;
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
 * Copyright: General Public License
 */
public class UpdateInformationDialog extends JDialog {
    private final JButton close = new JButton();
    private final String title;
    private final String message;
    private final String linkCaption;
    private final String releaseLink;
    private final Logger logger;

    public UpdateInformationDialog(JFrame parentFrame, String latestVersion, String releaseLink) {
        this(parentFrame, LanguageSelector.getInstance().getFirstAttributeByTagName("javagui.startup.newversiontitle"),
                LanguageSelector.getInstance().getFirstAttributeByTagName("javagui.startup.newversionmessage")
                        .replaceFirst("%s", latestVersion),
                LanguageSelector.getInstance().getFirstAttributeByTagName("javagui.startup.newversion"), releaseLink);
    }

    private UpdateInformationDialog(JFrame parentFrame, String title, String message, String linkCaption,
                                    String link) {
        super(parentFrame, true);
        logger = LoggerFactory.getLogger(getClass());
        this.title = title;
        this.message = message;
        this.linkCaption = linkCaption;
        this.releaseLink = link;
        try {
            init();
        } catch (Exception e) {
            logger.error(ApplejuiceFacade.ERROR_MESSAGE, e);
        }
    }

    /** Notice "no newer version" with a link to the repository. */
    public static UpdateInformationDialog noNewVersion(JFrame parentFrame) {
        LanguageSelector ls = LanguageSelector.getInstance();

        return new UpdateInformationDialog(parentFrame, ls.getFirstAttributeByTagName("mainform.checkupdate.caption"),
                ls.getFirstAttributeByTagName("javagui.checkupdate.noNewVersion"), "GitHub",
                UpdateChecker.REPOSITORY_URL);
    }

    /**
     * Notice "check failed" (no network, invalid answer, unknown own version) with a repository
     * link.
     */
    public static UpdateInformationDialog checkFailed(JFrame parentFrame) {
        LanguageSelector ls = LanguageSelector.getInstance();

        return new UpdateInformationDialog(parentFrame, ls.getFirstAttributeByTagName("mainform.checkupdate.caption"),
                ls.getFirstAttributeByTagName("javagui.checkupdate.error"), "GitHub", UpdateChecker.REPOSITORY_URL);
    }

    private void init() {
        LanguageSelector ls = LanguageSelector.getInstance();

        close.addActionListener(ae -> UpdateInformationDialog.this.dispose());
        close.setText(ls.getFirstAttributeByTagName("javagui.options.plugins.close"));
        setTitle(title);
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
        panel1.add(new JLabel(message), constraints);
        constraints.insets.right = 0;
        constraints.gridwidth = 1;
        constraints.gridy = 1;
        JLabel label1 = new JLabel();

        label1.setText(linkCaption + ": ");
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

        southPanel.add(close);
        getContentPane().add(southPanel, BorderLayout.SOUTH);
        pack();
        DialogLocation.center(this);
    }

    private void executeLink(String link) {
        try {
            DesktopTools.browse(new URI(link));
        } catch (Exception e) {
            logger.error(ApplejuiceFacade.ERROR_MESSAGE, e);
        }
    }
}
