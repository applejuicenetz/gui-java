package de.applejuicenet.client.gui.plugins;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.DataInputStream;
import java.net.*;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/plugin_src/serverwatcher/src/de/applejuicenet/client/gui/plugins/Attic/ServerWatcherPlugin.java,v 1.1 2003/09/11 15:03:00 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: ServerWatcherPlugin.java,v $
 * Revision 1.1  2003/09/11 15:03:00  maj0r
 * ServerWatcherPlugin begonnen.
 *
 *
 */

public class ServerWatcherPlugin extends PluginConnector {
    private JPanel topPanel = new JPanel(new GridBagLayout());
    private JEditorPane editorPane = new JEditorPane();

    public ServerWatcherPlugin() {
        setLayout(new BorderLayout());
        String classname = getClass().toString();
        String path = System.getProperty("user.dir") + File.separator + "plugins" +
                        File.separator + classname.substring(classname.lastIndexOf('.')+1) + ".jar";
        initIcon(path);
        add(topPanel, BorderLayout.NORTH);
        editorPane.setEditable(false);
        editorPane.setContentType("text/html");

        try
        {
            URL url = new URL("http://213.47.61.216:8001/serverlist.htm");
            URLConnection conn = url.openConnection();
            conn.setRequestProperty("Authorization", URLEncoder.encode("user:pass"));
            DataInputStream in = new DataInputStream(conn.getInputStream());
            java.io.BufferedReader stream =new java.io.BufferedReader(new java.io.InputStreamReader(in));
            String line;
            while ((line = stream.readLine()) != null) {
               System.out.println(line);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        editorPane.setText("");

        add(editorPane, BorderLayout.CENTER);
    }

    public void fireLanguageChanged() {
    }

    /*Wird automatisch aufgerufen, wenn neue Informationen vom Server eingegangen sind.
      Über den DataManger können diese abgerufen werden.*/
    public void fireContentChanged(int type, Object content) {
    }

    public void registerSelected() {
    }

    public String getTitle() {
        return "ServerWatcher";
    }

    public String getAutor() {
        return "Maj0r";
    }

    public String getBeschreibung() {
        return "Mit dem Serverwatcher kann der Status von Server angezeigt werden.";
    }

    public String getVersion() {
        return "1.0";
    }

    public boolean istReiter() {
        return true;
    }
}