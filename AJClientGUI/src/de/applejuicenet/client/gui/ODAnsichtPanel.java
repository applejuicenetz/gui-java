package de.applejuicenet.client.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToolTip;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import de.applejuicenet.client.gui.controller.ApplejuiceFassade;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.controller.OptionsManager;
import de.applejuicenet.client.gui.controller.OptionsManagerImpl;
import de.applejuicenet.client.shared.IconManager;
import de.applejuicenet.client.shared.Settings;
import de.applejuicenet.client.shared.ZeichenErsetzer;
import de.applejuicenet.client.shared.MultiLineToolTip;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/Attic/ODAnsichtPanel.java,v 1.23 2004/07/20 22:50:39 loevenwong Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 */

public class ODAnsichtPanel
    extends JPanel
    implements OptionsRegister {
    private JLabel farbeFertigerDownload = new JLabel("      ");
    private JLabel farbeQuelle = new JLabel("      ");
    private Settings settings;
    JCheckBox cmbAktiv = new JCheckBox();
    JCheckBox cmbDownloadUebersicht = new JCheckBox();
    private JCheckBox enableToolTip = new JCheckBox();
    private JCheckBox cmbStartscreenZeigen = new JCheckBox();
    private Logger logger;
    private Icon menuIcon;
    private String menuText;
    private boolean dirty = false;
    private JTextField openProgram = new JTextField();
    private JLabel program = new JLabel("VLC ");

    public ODAnsichtPanel() {
        logger = Logger.getLogger(getClass());
        try {
            settings = Settings.getSettings();
            init();
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
            }
        }
    }

    private void init() {
        LanguageSelector languageSelector = LanguageSelector.getInstance();
        IconManager im = IconManager.getInstance();
        menuIcon = im.getIcon("opt_ansicht");
        cmbDownloadUebersicht.setText(ZeichenErsetzer.korrigiereUmlaute(
            languageSelector.
            getFirstAttrbuteByTagName(".root.javagui.options.ansicht.downloadansicht")));
        cmbAktiv.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(".root.javagui.options.ansicht.aktiv")));
        cmbStartscreenZeigen.setText(ZeichenErsetzer.korrigiereUmlaute(
            languageSelector.
            getFirstAttrbuteByTagName(".root.javagui.options.ansicht.zeigestartscreen")));
        enableToolTip.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(".root.javagui.options.ansicht.zeigetooltipps")));

        setLayout(new BorderLayout());
        farbeFertigerDownload.setOpaque(true);
        farbeFertigerDownload.addMouseListener(new ColorChooserMouseAdapter());
        farbeQuelle.setOpaque(true);
        farbeQuelle.setBackground(settings.getQuelleHintergrundColor());
        farbeQuelle.addMouseListener(new ColorChooserMouseAdapter());
        OptionsManager om = OptionsManagerImpl.getInstance();
        cmbDownloadUebersicht.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent ce) {
                settings.setDownloadUebersicht(cmbDownloadUebersicht.isSelected());
                dirty = true;
            }
        });
        cmbStartscreenZeigen.setSelected(om.shouldShowConnectionDialogOnStartup());
        cmbStartscreenZeigen.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent ce) {
                dirty = true;
            }
        });
        cmbAktiv.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent ce) {
                dirty = true;
                settings.setFarbenAktiv(cmbAktiv.isSelected());
            }
        });
        enableToolTip.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                dirty = true;
                settings.enableToolTipEnabled(enableToolTip.isSelected());
            }
        });
        ImageIcon icon = im.getIcon("hint");
        JLabel hint1 = new JLabel(icon) {
            public JToolTip createToolTip() {
                MultiLineToolTip tip = new MultiLineToolTip();
                tip.setComponent(this);
                return tip;
            }
        };
        JLabel hint2 = new JLabel(icon) {
            public JToolTip createToolTip() {
                MultiLineToolTip tip = new MultiLineToolTip();
                tip.setComponent(this);
                return tip;
            }
        };
        String tooltipp = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(".root.javagui.options.ansicht.ttipp_farbewaehlen"));
        hint1.setToolTipText(tooltipp);
        hint2.setToolTipText(tooltipp);

        openProgram.setEditable(false);
        openProgram.setBackground(Color.WHITE);
        openProgram.setText(om.getOpenProgram());
        Icon icon2 = im.getIcon("folderopen");
        Icon icon3 = im.getIcon("vlc");
        program.setIcon(icon3);
        JLabel selectProgram = new JLabel(icon2);
        selectProgram.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                JLabel source = (JLabel) e.getSource();
                source.setBorder(BorderFactory.createLineBorder(Color.black));
            }

            public void mouseClicked(MouseEvent e) {
                JLabel source = (JLabel) e.getSource();
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogType(JFileChooser.FILES_ONLY);
                fileChooser.setDialogTitle(program.getText());
                if (openProgram.getText().length() != 0) {
                    File tmpFile = new File(openProgram.getText());
                    if (tmpFile.isFile()) {
                        fileChooser.setCurrentDirectory(tmpFile);
                    }
                }
                int returnVal = fileChooser.showOpenDialog(source);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File browserFile = fileChooser.getSelectedFile();
                    if (browserFile.isFile()) {
                    	openProgram.setText(browserFile.getPath());
                        dirty = true;
                    }
                }
            }

            public void mouseExited(MouseEvent e) {
                JLabel source = (JLabel) e.getSource();
                source.setBorder(null);
            }
        });

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets.bottom = 5;

        JPanel panel4 = new JPanel(new GridBagLayout());
        panel4.add(program, constraints);
        constraints.gridx = 1;
        constraints.weightx = 1;
        panel4.add(openProgram, constraints);
        constraints.weightx = 0;
        constraints.gridx = 2;
        panel4.add(selectProgram, constraints);

        JPanel panel1 = new JPanel();
        constraints.gridx = 0;
        constraints.gridy = 0;
        panel1.setLayout(new GridBagLayout());
        panel1.setBorder(BorderFactory.createTitledBorder(ZeichenErsetzer.
            korrigiereUmlaute(languageSelector.
                              getFirstAttrbuteByTagName(".root.javagui.options.ansicht.hintergrundfarben"))));
        panel1.add(cmbAktiv, constraints);
        constraints.gridy = 1;
        constraints.insets.left = 5;
        constraints.insets.right = 5;
        panel1.add(new JLabel(ZeichenErsetzer.korrigiereUmlaute(
            languageSelector.
            getFirstAttrbuteByTagName(".root.javagui.options.ansicht.fertigerdownload"))), constraints);
        constraints.gridy = 2;
        panel1.add(new JLabel(ZeichenErsetzer.korrigiereUmlaute(
            languageSelector.
            getFirstAttrbuteByTagName(".root.javagui.options.ansicht.quelle"))), constraints);
        menuText = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(".root.javagui.options.ansicht.caption"));
        constraints.gridx = 1;
        constraints.gridy = 1;
        panel1.add(farbeFertigerDownload, constraints);
        constraints.gridy = 2;
        panel1.add(farbeQuelle, constraints);
        constraints.gridx = 2;
        constraints.gridy = 1;
        panel1.add(hint1, constraints);
        constraints.gridy = 2;
        panel1.add(hint2, constraints);
        JPanel panel2 = new JPanel(new BorderLayout());
        panel2.add(panel1, BorderLayout.NORTH);
        JPanel panel3 = new JPanel(new GridBagLayout());

        constraints.insets.bottom = 0;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 1;
        constraints.weighty = 0;
        panel3.add(panel4, constraints);
        constraints.gridy = 1;
        panel3.add(cmbStartscreenZeigen, constraints);
        constraints.gridy = 2;
        panel3.add(cmbDownloadUebersicht, constraints);
        constraints.gridy = 3;
        panel3.add(enableToolTip, constraints);
        panel2.add(panel3, BorderLayout.SOUTH);

        add(panel2, BorderLayout.WEST);

        reloadSettings();
    }

    public boolean save() {
        try {
            boolean bRet = false;
            OptionsManager om = OptionsManagerImpl.getInstance();
            if (om.shouldShowConnectionDialogOnStartup() != shouldShowStartcreen()) {
                om.showConnectionDialogOnStartup(shouldShowStartcreen());
                bRet = true;
            }
            if (!om.getOpenProgram().equals(getProgramPfad())) {
                om.setOpenProgram(getProgramPfad());
                bRet = true;
            }
            if (settings.save()) {
                bRet = true;
            }
        return bRet;
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
            }
            return false;
        }
    }

    public boolean isDirty() {
        return dirty;
    }

    public boolean shouldShowStartcreen() {
        return cmbStartscreenZeigen.isSelected();
    }

    public Icon getIcon() {
        return menuIcon;
    }

    public String getMenuText() {
        return menuText;
    }

    public String getProgramPfad() {
        return openProgram.getText();
    }

    public void reloadSettings() {
        settings = Settings.getSettings();
        enableToolTip.setSelected(settings.isToolTipEnabled());
    }

    class ColorChooserMouseAdapter
        extends MouseAdapter {
        public void mouseEntered(MouseEvent e) {
            JLabel source = (JLabel) e.getSource();
            source.setBorder(BorderFactory.createLineBorder(Color.black));
        }

        public void mouseClicked(MouseEvent e) {
            LanguageSelector languageSelector = LanguageSelector.getInstance();
            JLabel source = (JLabel) e.getSource();
            Color newColor = JColorChooser.showDialog(null,
                ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(".root.javagui.options.ansicht.hintergrundfarbewaehlen")),
                source.getBackground());
            if (newColor != null &&
                newColor.getRGB() != source.getBackground().getRGB()) {
                source.setBackground(newColor);
                if (source == farbeQuelle) {
                    settings.setQuelleHintergrundColor(newColor);
                }
                else {
                    settings.setDownloadFertigHintergrundColor(newColor);
                }
            }
        }

        public void mouseExited(MouseEvent e) {
            JLabel source = (JLabel) e.getSource();
            source.setBorder(null);
        }
    }
}
