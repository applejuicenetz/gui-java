package de.applejuicenet.client.gui.options;

import java.util.Vector;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import de.applejuicenet.client.gui.AppleJuiceDialog;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.plugins.PluginConnector;
import de.applejuicenet.client.shared.IconManager;
import de.applejuicenet.client.shared.ZeichenErsetzer;
import de.applejuicenet.client.gui.controller.ApplejuiceFassade;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/options/ODPluginPanel.java,v 1.1 2004/10/28 14:57:57 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 */

public class ODPluginPanel
    extends JPanel
    implements OptionsRegister {
	
    private static final long serialVersionUID = -2694086541312417833L;
    
	private JList pluginList;
    private JTextArea beschreibung = new JTextArea();
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
                logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
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
        beschreibung.setLineWrap(true);
        beschreibung.setWrapStyleWord(true);
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
            getFirstAttrbuteByTagName(".root.einstform.Label11.caption")) + ":");
        name = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                 getFirstAttrbuteByTagName(".root.javagui.options.plugins.name"));
        version = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(".root.javagui.options.plugins.version"));
        erlaeuterung = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(".root.javagui.options.plugins.beschreibung"));
        autor = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                  getFirstAttrbuteByTagName(".root.javagui.options.plugins.autor"));
        einstellungen.setText(ZeichenErsetzer.korrigiereUmlaute(
            languageSelector.
            getFirstAttrbuteByTagName(".root.javagui.options.plugins.einstellungen")));
        menuText = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(".root.einstform.TabSheet1.caption"));

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
                if (selectedPluginConnector != null) {
                    ODPluginOptionsDialog pluginOptionsDialog =
                        new ODPluginOptionsDialog(parentDialog,
                                                  selectedPluginConnector);
                    pluginOptionsDialog.setResizable(true);
                    pluginOptionsDialog.setVisible(true);
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

        public PluginConnector getPlugin() {
            return plugin;
        }

        public String getBeschreibung() {
            String text;
            text = name + ":\r\n" + plugin.getTitle() + "\r\n\r\n" + autor +
                ":\r\n" +
                plugin.getAutor() + " [" + plugin.getContact() + "]"
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
        if (selected.getPluginOptionPanel() == null) {
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

    public void reloadSettings() {
        // nothing to do...
    }
}
