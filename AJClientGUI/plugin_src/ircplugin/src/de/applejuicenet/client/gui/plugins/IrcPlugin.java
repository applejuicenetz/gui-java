package de.applejuicenet.client.gui.plugins;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import de.applejuicenet.client.gui.controller.ApplejuiceFassade;
import de.applejuicenet.client.gui.plugins.ircplugin.XdccIrc;
import javax.swing.JCheckBox;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/plugin_src/ircplugin/src/de/applejuicenet/client/gui/plugins/Attic/IrcPlugin.java,v 1.12 2004/10/06 12:29:14 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r [Maj0r@applejuicenet.de]
 *
 */

public class IrcPlugin extends PluginConnector {
    private static final long serialVersionUID = -4880551541496115520L;
	private XdccIrc xdccIrc;
    private String savePath;
    private Properties properties;
    private XMLValueHolder pluginsPropertiesXMLHolder;

    public IrcPlugin(XMLValueHolder pluginsPropertiesXMLHolder, Map languageFiles, ImageIcon icon) {
        super(pluginsPropertiesXMLHolder, languageFiles, icon);
        this.pluginsPropertiesXMLHolder = pluginsPropertiesXMLHolder;
        init();
    }

    private void init(){
        savePath = ApplejuiceFassade.getPropertiesPath();
        int index = savePath.lastIndexOf(File.separator);
        savePath = savePath.substring(0, index) + File.separator +
            "ircplugin.properties";
        properties = new Properties();
        File tmpFile = new File(savePath);
        if (tmpFile.isFile()){
            try {
                properties.load(new FileInputStream(tmpFile));
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        setLayout(new BorderLayout());
        xdccIrc = new XdccIrc(this);
        add(xdccIrc, BorderLayout.CENTER);
    }

    public void saveProperties(){
        try {
            properties.store(new FileOutputStream(savePath),
                             "ajIrcPlugin-PropertyFile");
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public Properties getProperties(){
        return properties;
    }

    public void fireLanguageChanged() {
        xdccIrc.fireLanguageChanged();
    }

    /*Wird automatisch aufgerufen, wenn neue Informationen vom Server eingegangen sind.
      Über den DataManger können diese abgerufen werden.*/
    public void fireContentChanged(int type, Object content) {
    }

    public void registerSelected() {
    }

    public JPanel getOptionPanel(){
        JPanel panel1 = new JPanel(new GridBagLayout());
        final JTextField nick = new JTextField();
        nick.setMinimumSize(new Dimension(200, nick.getPreferredSize().height));
        nick.setPreferredSize(new Dimension(200, nick.getPreferredSize().height));
        final JTextField onJoinMessage = new JTextField();
        final JTextField channels = new JTextField();
        final JCheckBox rules = new JCheckBox();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets = new Insets(5, 0, 0, 0);
        constraints.insets.left = 5;
        panel1.add(new JLabel("Nickname: "), constraints);
        constraints.gridy = 1;
        panel1.add(new JLabel("OnJoinMessage: "), constraints);
        constraints.gridy = 2;
        panel1.add(new JLabel("Channels: "), constraints);
        constraints.insets.right = 5;
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.weightx = 1;
        panel1.add(nick, constraints);
        constraints.weightx = 0;
        constraints.gridy = 1;
        panel1.add(onJoinMessage, constraints);
        constraints.gridy = 2;
        panel1.add(channels, constraints);
        constraints.gridx = 0;
        constraints.gridwidth = 2;
        constraints.gridy = 3;
        rules.setText(getLanguageString(".root.language.disablerules.value"));
        panel1.add(rules, constraints);
        String propNick = properties.getProperty("nick");
        if (propNick != null){
            nick.setText(propNick);
        }
        String propOnJoin = properties.getProperty("onjoin");
        if (propOnJoin != null){
            onJoinMessage.setText(propOnJoin);
        }
        String propChannels = properties.getProperty("channels");
        if (propChannels != null){
            channels.setText(propChannels);
        }
        String propRules = properties.getProperty("norules");
        if (propRules != null && propRules.compareToIgnoreCase("true")==0){
            rules.setSelected(true);
        }
        nick.addFocusListener(new FocusAdapter(){
            public void focusLost(FocusEvent fe){
                properties.put("nick", nick.getText());
                saveProperties();
            }
        });
        onJoinMessage.addFocusListener(new FocusAdapter(){
            public void focusLost(FocusEvent fe){
                properties.put("onjoin", onJoinMessage.getText());
                saveProperties();
            }
        });
        channels.addFocusListener(new FocusAdapter(){
            public void focusLost(FocusEvent fe){
                properties.put("channels", channels.getText());
                saveProperties();
            }
        });
        rules.addChangeListener(new ChangeListener(){
            public void stateChanged(ChangeEvent e){
                properties.put("norules", new Boolean(rules.isSelected()).toString());
            }
        });
        return panel1;
    }
}
