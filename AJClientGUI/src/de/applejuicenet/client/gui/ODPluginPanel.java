package de.applejuicenet.client.gui;

import java.util.Vector;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.plugins.PluginConnector;
import de.applejuicenet.client.shared.ZeichenErsetzer;
import javax.swing.JDialog;
import javax.swing.Icon;
import de.applejuicenet.client.shared.IconManager;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/Attic/ODPluginPanel.java,v 1.11 2004/01/25 10:16:42 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: ODPluginPanel.java,v $
 * Revision 1.11  2004/01/25 10:16:42  maj0r
 * Optionenmenue ueberarbeitet.
 *
 * Revision 1.10  2004/01/01 15:30:21  maj0r
 * Plugins koennen nun ein JPanel zB fuer Optionen implementieren.
 * Dieses wird dann im Optionendialog angezeigt.
 *
 * Revision 1.9  2003/12/29 16:04:17  maj0r
 * Header korrigiert.
 *
 * Revision 1.8  2003/09/04 10:13:28  maj0r
 * Logger eingebaut.
 *
 * Revision 1.7  2003/09/04 06:27:53  maj0r
 * Muell entfernt.
 *
 * Revision 1.6  2003/06/30 19:46:11  maj0r
 * Sourcestil verbessert.
 *
 * Revision 1.5  2003/06/10 12:31:03  maj0r
 * Historie eingefuegt.
 *
 *
 */

public class ODPluginPanel
    extends JPanel implements OptionsRegister{
    private JList pluginList;
    private JEditorPane beschreibung = new JEditorPane();
    private JLabel label1 = new JLabel();
    private AppleJuiceDialog theApp;
    private JButton einstellungen = new JButton();
    private String name;
    private String version;
    private String autor;
    private String erlaeuterung;
    private Logger logger;
    private PluginConnector selectedPluginConnector = null;
    private JDialog parentDialog;
    private Icon menuIcon;
    private String menuText;

    public ODPluginPanel(JDialog parent) {
        logger = Logger.getLogger(getClass());
        try {
            theApp = AppleJuiceDialog.getApp();
            parentDialog = parent;
            init();
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", e);
            }
        }
    }

    private void init() throws Exception {
        IconManager im = IconManager.getInstance();
        menuIcon = im.getIcon("opt_plugins");
        PluginConnector[] plugins = theApp.getPlugins();
        einstellungen.setVisible(false);
        Vector v = new Vector();
        if (plugins.length != 0) {
            for (int i = 0; i < plugins.length; i++) {
                v.add(new PluginContainer(plugins[i]));
            }
        }
        Dimension parentSize = theApp.getSize();
        beschreibung.setBackground(label1.getBackground());
        beschreibung.setPreferredSize(new Dimension(parentSize.width / 3,
            beschreibung.getPreferredSize().
            height));
        beschreibung.setEditable(false);
        pluginList = new JList(v);
        pluginList.setPreferredSize(new Dimension(190,
                                                  pluginList.getPreferredSize().
                                                  height));
        pluginList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                pluginList_valueChanged(e);
            }
        });
        setLayout(new BorderLayout());
        LanguageSelector languageSelector = LanguageSelector.getInstance();
        label1.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(new String[] {"einstform", "Label11",
                                      "caption"})) + ":");
        name = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                 getFirstAttrbuteByTagName(new
            String[] {"javagui", "options", "plugins", "name"}));
        version = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(new
                                      String[] {"javagui", "options", "plugins",
                                      "version"}));
        erlaeuterung = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(new String[] {"javagui", "options",
                                      "plugins",
                                      "beschreibung"}));
        autor = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                  getFirstAttrbuteByTagName(new
            String[] {"javagui", "options", "plugins", "autor"}));
        einstellungen.setText(ZeichenErsetzer.korrigiereUmlaute(
            languageSelector.
            getFirstAttrbuteByTagName(new
                                      String[] {"javagui", "options", "plugins",
                                      "einstellungen"})));
        menuText = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(new String[]{"einstform", "TabSheet1", "caption"}));

        add(label1, BorderLayout.NORTH);
        add(pluginList, BorderLayout.WEST);
        JPanel panel1 = new JPanel(new BorderLayout());
        JPanel panel2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel2.add(einstellungen);
        JScrollPane sp = new JScrollPane(beschreibung);
        panel1.add(sp, BorderLayout.CENTER);
        panel1.add(panel2, BorderLayout.SOUTH);
        add(panel1, BorderLayout.CENTER);
        einstellungen.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                if (selectedPluginConnector != null){
                    ODPluginOptionsDialog pluginOptionsDialog =
                        new ODPluginOptionsDialog(parentDialog, selectedPluginConnector);
                    pluginOptionsDialog.setResizable(true);
                    pluginOptionsDialog.show();
                }
            }
        });
    }

    class PluginContainer {
        private PluginConnector plugin;

        public PluginContainer(PluginConnector plugin) {
            this.plugin = plugin;
        }

        public String toString() {
            return plugin.getTitle();
        }

        public PluginConnector getPlugin(){
            return plugin;
        }

        public String getBeschreibung() {
            String text;
            text = name + ":\r\n" + plugin.getTitle() + "\r\n\r\n" + autor +
                ":\r\n" +
                plugin.getAutor()
                + "\r\n\r\n" + version + ":\r\n" + plugin.getVersion()
                + "\r\n\r\n" + erlaeuterung + ":\r\n" + plugin.getBeschreibung();
            return text;
        }

        public JPanel getPluginOptionPanel() {
            return plugin.getOptionPanel();
        }
    }

    void pluginList_valueChanged(ListSelectionEvent e) {
        PluginContainer selected = (PluginContainer) ( (JList) e.getSource()).
            getSelectedValue();
        beschreibung.setText(selected.getBeschreibung());
        if ( selected.getPluginOptionPanel() == null) {
            einstellungen.setVisible(false);
        }
        else {
            selectedPluginConnector = selected.getPlugin();
            einstellungen.setVisible(true);
        }
    }

    public Icon getIcon() {
        return menuIcon;
    }

    public String getMenuText() {
        return menuText;
    }
}