package de.applejuicenet.client.gui.plugins;

import java.awt.BorderLayout;
import javax.swing.ImageIcon;

import de.applejuicenet.client.gui.plugins.ircplugin.XdccIrc;
import java.util.Map;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JLabel;
import java.awt.Dimension;
import de.applejuicenet.client.gui.controller.ApplejuiceFassade;
import java.io.File;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusAdapter;
import java.io.FileOutputStream;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/plugin_src/ircplugin/src/de/applejuicenet/client/gui/plugins/Attic/IrcPlugin.java,v 1.10 2004/05/12 12:31:39 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: IrcPlugin.java,v $
 * Revision 1.10  2004/05/12 12:31:39  maj0r
 * Weitere Arbeiten zum Standardplugin.
 *
 * Revision 1.9  2004/03/03 15:35:45  maj0r
 * PMD-Optimierung
 *
 * Revision 1.8  2004/03/03 14:22:23  maj0r
 * Es wird nun Deutsch und Englisch unterstuetzt.
 *
 * Revision 1.7  2004/03/03 12:50:25  maj0r
 * Sprachunterstuetzung eungebaut.
 *
 * Revision 1.6  2004/03/02 21:17:35  maj0r
 * An neue Schnittstelle angepasst.
 *
 * Revision 1.5  2003/10/27 18:26:58  maj0r
 * Bugs behoben...
 *
 * Revision 1.4  2003/10/27 14:10:07  maj0r
 * Header eingefuegt.
 *
 *
 */

public class IrcPlugin extends PluginConnector {
    private XdccIrc xdccIrc;
    private String savePath;
    private Properties properties;

    public IrcPlugin(XMLValueHolder pluginsPropertiesXMLHolder, Map languageFiles, ImageIcon icon) {
        super(pluginsPropertiesXMLHolder, languageFiles, icon);
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
        return panel1;
    }
}
