package de.applejuicenet.client.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import de.applejuicenet.client.gui.controller.*;
import de.applejuicenet.client.shared.*;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/Attic/ODStandardPanel.java,v 1.14 2003/10/27 13:00:17 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: ODStandardPanel.java,v $
 * Revision 1.14  2003/10/27 13:00:17  maj0r
 * Bug #1039 fixed (Danke an xcalibur):
 * Ist im Core noch nicht implementiert und deshalb erstmal wieder aus dem GUI geflogen.
 *
 * Revision 1.13  2003/10/14 15:43:52  maj0r
 * An pflegbaren Xml-Port angepasst.
 *
 * Revision 1.12  2003/10/13 12:37:48  maj0r
 * Bug #1003 behoben.
 *
 * Revision 1.11  2003/09/11 06:54:15  maj0r
 * Auf neues Sessions-Prinzip umgebaut.
 * Sprachenwechsel korrigert, geht nun wieder flott.
 *
 * Revision 1.10  2003/09/10 13:15:47  maj0r
 * Veraltete Option "Browsen erlauben" entfernt.
 *
 * Revision 1.9  2003/09/09 12:28:15  maj0r
 * Wizard fertiggestellt.
 *
 * Revision 1.8  2003/09/04 10:13:28  maj0r
 * Logger eingebaut.
 *
 * Revision 1.7  2003/08/24 14:59:59  maj0r
 * Version 0.14
 * Diverse Aenderungen.
 *
 * Revision 1.6  2003/06/24 12:06:49  maj0r
 * log4j eingefügt (inkl. Bedienung über Einstellungsdialog).
 *
 * Revision 1.5  2003/06/10 12:31:03  maj0r
 * Historie eingefügt.
 *
 *
 */

public class ODStandardPanel
        extends JPanel {
    private boolean dirty = false;
    private boolean xmlPortDirty = false;
    private JLabel label1 = new JLabel();
    private JLabel label2 = new JLabel();
    private JLabel label3 = new JLabel();
    private JLabel label4 = new JLabel();
    private JLabel label6 = new JLabel();
    private JLabel openTemp;
    private JLabel openIncoming;
    private JTextField temp = new JTextField();
    private JTextField incoming = new JTextField();
    private JTextField port = new JTextField();
    private JTextField xmlPort = new JTextField();
    private JTextField nick = new JTextField();
    private JLabel hint1;
    private JLabel hint2;
    private JLabel hint3;
    private JLabel hint4;
    private JLabel hint5;
    private JLabel hint6;
    private JDialog parent;
    private AJSettings ajSettings;
    private JComboBox cmbLog;
    private Logger logger;
    private ConnectionSettings remote;

    public ODStandardPanel(JDialog parent, AJSettings ajSettings, ConnectionSettings remote) {
        logger = Logger.getLogger(getClass());
        try
        {
            this.remote = remote;
            this.parent = parent;
            this.ajSettings = ajSettings;
            init();
        }
        catch (Exception e)
        {
            if (logger.isEnabledFor(Level.ERROR))
                logger.error("Unbehandelte Exception", e);
        }
    }

    public boolean isDirty() {
        return dirty;
    }

    public Level getLogLevel() {
        return ((LevelItem) cmbLog.getSelectedItem()).getLevel();
    }

    private void init() throws Exception {
        port.setDocument(new NumberInputVerifier());
        xmlPort.setDocument(new NumberInputVerifier());
        temp.setText(ajSettings.getTempDir());
        temp.setEditable(false);
        temp.setBackground(Color.WHITE);
        incoming.setText(ajSettings.getIncomingDir());
        incoming.setEditable(false);
        incoming.setBackground(Color.WHITE);
        port.setText(Long.toString(ajSettings.getPort()));
        xmlPort.setText(Long.toString(ajSettings.getXMLPort()));
        nick.setText(ajSettings.getNick());
        port.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                if (ajSettings.getPort() != Integer.parseInt(port.getText()))
                {
                    dirty = true;
                    ajSettings.setPort(Integer.parseInt(port.getText()));
                }
            }
        });
        xmlPort.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                if (ajSettings.getXMLPort() != Long.parseLong(xmlPort.getText()))
                {
                    dirty = true;
                    xmlPortDirty = true;
                    remote.setXmlPort(Integer.parseInt(xmlPort.getText()));
                    ajSettings.setXMLPort(Long.parseLong(xmlPort.getText()));
                }
            }
        });
        nick.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                if (ajSettings.getNick().compareTo(nick.getText()) != 0)
                {
                    dirty = true;
                    ajSettings.setNick(nick.getText());
                }
            }
        });

        JPanel panel8 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel8.add(new JLabel("Logging: "));
        LanguageSelector languageSelector = LanguageSelector.getInstance();
        Level logLevel = PropertiesManager.getOptionsManager().getLogLevel();

        LevelItem[] levelItems = new LevelItem[2];//{ "Info", "Debug"};
        levelItems[0] = new LevelItem(Level.INFO, ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.getFirstAttrbuteByTagName(new String[]{"javagui", "options", "logging", "info"})));
        levelItems[1] = new LevelItem(Level.DEBUG, ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.getFirstAttrbuteByTagName(new String[]{"javagui", "options", "logging", "debug"})));

        cmbLog = new JComboBox(levelItems);
        cmbLog.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                dirty = true;
            }
        });

        int index = 0;
        if (logLevel == Level.INFO)
            index = 0;
        else if (logLevel == Level.DEBUG)
            index = 1;
        cmbLog.setSelectedIndex(index);

        panel8.add(cmbLog);

        setLayout(new BorderLayout());
        port.setHorizontalAlignment(JLabel.RIGHT);
        xmlPort.setHorizontalAlignment(JLabel.RIGHT);
        JPanel panel6 = new JPanel(new GridBagLayout());
        label1.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                         getFirstAttrbuteByTagName(new String[]{"einstform", "Label2",
                                                                                                "caption"})));
        label2.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                         getFirstAttrbuteByTagName(new String[]{"einstform", "Label7",
                                                                                                "caption"})));
        label3.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                         getFirstAttrbuteByTagName(new String[]{"einstform", "Label3",
                                                                                                "caption"})));
        label4.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                         getFirstAttrbuteByTagName(new String[]{"einstform", "Label8",
                                                                                                "caption"})));
        label6.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                         getFirstAttrbuteByTagName(new String[]{"javagui", "options",
                                                                                                "standard", "xmlport"})));

        IconManager im = IconManager.getInstance();
        ImageIcon icon = im.getIcon("hint");
        hint1 = new JLabel(icon) {
            public JToolTip createToolTip() {
                MultiLineToolTip tip = new MultiLineToolTip();
                tip.setComponent(this);
                return tip;
            }
        };
        hint2 = new JLabel(icon) {
            public JToolTip createToolTip() {
                MultiLineToolTip tip = new MultiLineToolTip();
                tip.setComponent(this);
                return tip;
            }
        };
        hint3 = new JLabel(icon) {
            public JToolTip createToolTip() {
                MultiLineToolTip tip = new MultiLineToolTip();
                tip.setComponent(this);
                return tip;
            }
        };
        hint4 = new JLabel(icon) {
            public JToolTip createToolTip() {
                MultiLineToolTip tip = new MultiLineToolTip();
                tip.setComponent(this);
                return tip;
            }
        };
        hint5 = new JLabel(icon) {
            public JToolTip createToolTip() {
                MultiLineToolTip tip = new MultiLineToolTip();
                tip.setComponent(this);
                return tip;
            }
        };
        hint6 = new JLabel(icon) {
            public JToolTip createToolTip() {
                MultiLineToolTip tip = new MultiLineToolTip();
                tip.setComponent(this);
                return tip;
            }
        };
        hint1.setToolTipText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                               getFirstAttrbuteByTagName(new String[]{"javagui", "options",
                                                                                                      "standard", "ttipp_temp"})));
        hint2.setToolTipText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                               getFirstAttrbuteByTagName(new String[]{"javagui", "options",
                                                                                                      "standard", "ttipp_port"})));
        hint3.setToolTipText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                               getFirstAttrbuteByTagName(new String[]{"javagui", "options",
                                                                                                      "standard", "ttipp_nick"})));
        hint4.setToolTipText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                               getFirstAttrbuteByTagName(new String[]{"einstform", "Label1",
                                                                                                      "caption"})));
        hint5.setToolTipText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                               getFirstAttrbuteByTagName(new String[]{"javagui", "options",
                                                                                                      "logging", "ttip"})));
        hint6.setToolTipText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                               getFirstAttrbuteByTagName(new String[]{"javagui", "options",
                                                                                                      "standard", "ttipp_xmlport"})));

        Icon icon2 = im.getIcon("folderopen");
        DirectoryChooserMouseAdapter dcMouseAdapter = new DirectoryChooserMouseAdapter();
        openTemp = new JLabel(icon2);
        openTemp.addMouseListener(dcMouseAdapter);
        openIncoming = new JLabel(icon2);
        openIncoming.addMouseListener(dcMouseAdapter);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets.top = 5;

        JPanel panel1 = new JPanel(new GridBagLayout());
        JPanel panel2 = new JPanel(new GridBagLayout());
        JPanel panel3 = new JPanel(new GridBagLayout());
        JPanel panel4 = new JPanel(new GridBagLayout());
        JPanel panel7 = new JPanel(new GridBagLayout());

        constraints.insets.right = 5;
        constraints.insets.left = 4;
        panel1.add(label1, constraints);
        panel2.add(label2, constraints);
        panel3.add(label3, constraints);
        panel4.add(label4, constraints);
        panel7.add(label6, constraints);

        constraints.insets.left = 0;
        constraints.insets.right = 2;
        constraints.gridx = 1;
        constraints.weightx = 1;
        panel1.add(temp, constraints);
        panel2.add(incoming, constraints);
        panel3.add(port, constraints);
        panel7.add(xmlPort, constraints);
        panel4.add(nick, constraints);
        constraints.gridx = 2;
        constraints.weightx = 0;
        panel1.add(openTemp, constraints);
        panel2.add(openIncoming, constraints);

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 1;
        panel6.add(panel1, constraints);
        constraints.gridy = 1;
        panel6.add(panel2, constraints);
        constraints.gridy = 2;
        panel6.add(panel3, constraints);
        constraints.gridy = 3;
        panel6.add(panel7, constraints);
        constraints.gridy = 4;
        panel6.add(panel4, constraints);

        constraints.insets.top = 10;
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.weightx = 0;
        panel6.add(hint1, constraints);
        constraints.gridy = 2;
        panel6.add(hint2, constraints);
        constraints.gridy = 3;
        panel6.add(hint6, constraints);
        constraints.gridy = 4;
        panel6.add(hint3, constraints);

        constraints.gridy = 5;
        constraints.gridx = 0;
        constraints.gridwidth = 1;
        panel6.add(panel8, constraints);
        constraints.gridx = 1;
        panel6.add(hint5, constraints);

        add(panel6, BorderLayout.NORTH);
    }

    public boolean isXmlPortDirty() {
        return xmlPortDirty;
    }

    class DirectoryChooserMouseAdapter
            extends MouseAdapter {
        public void mouseEntered(MouseEvent e) {
            JLabel source = (JLabel) e.getSource();
            source.setBorder(BorderFactory.createLineBorder(Color.black));
        }

        public void mouseClicked(MouseEvent e) {
            JLabel source = (JLabel) e.getSource();
            String title = "";
            if (source == openTemp)
            {
                title = label1.getText();
            }
            else
            {
                title = label2.getText();
            }
            ODDirectoryChooser chooser = new ODDirectoryChooser(parent, title);
            chooser.setLocation(parent.getLocation());
            chooser.show();
            if (chooser.isNewPathSelected())
            {
                dirty = true;
                String path = chooser.getSelectedPath();
                if (source == openTemp)
                {
                    temp.setText(path);
                    ajSettings.setTempDir(path);
                }
                else
                {
                    incoming.setText(path);
                    ajSettings.setIncomingDir(path);
                }
            }
        }

        public void mouseExited(MouseEvent e) {
            JLabel source = (JLabel) e.getSource();
            source.setBorder(null);
        }
    }

    class LevelItem {
        private Level level;
        private String bezeichnung;

        public LevelItem(Level level, String bezeichnung) {
            this.level = level;
            this.bezeichnung = bezeichnung;
        }

        public Level getLevel() {
            return level;
        }

        public String toString() {
            return bezeichnung;
        }
    }
}