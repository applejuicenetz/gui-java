package de.applejuicenet.client.gui.controller;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/controller/LinkListener.java,v 1.4 2004/02/05 23:11:27 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: LinkListener.java,v $
 * Revision 1.4  2004/02/05 23:11:27  maj0r
 * Formatierung angepasst.
 *
 * Revision 1.3  2003/12/29 16:04:17  maj0r
 * Header korrigiert.
 *
 * Revision 1.2  2003/11/19 12:57:19  maj0r
 * Deprecated behoben.
 *
 * Revision 1.1  2003/11/18 16:41:59  maj0r
 * Erste Version des LinkListener eingebaut.
 *
 *
 */

public class LinkListener
    implements Runnable {

    private final int PORT;

    private static Logger logger;
    private ServerSocket listen;
    private Thread connect;

    public LinkListener() {
        PORT = PropertiesManager.getOptionsManager().getLinkListenerPort();
        logger = Logger.getLogger(getClass());
        try {
            listen = new ServerSocket(PORT);
            connect = new Thread(this);
            connect.start();
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", e);
            }
        }
    }

    public void run() {
        try {
            while (true) {
                Socket client = listen.accept();
                try {
                    DataInputStream in = new DataInputStream(client.
                        getInputStream());
                    BufferedReader reader = new BufferedReader(new
                        InputStreamReader(in));
                    String line = reader.readLine();
                    String link = getLinkFromReadLine(line);
                    if (link != null) {
                        ApplejuiceFassade.getInstance().processLink(link);
                    }
                }
                catch (Exception e) {
                    if (logger.isEnabledFor(Level.ERROR)) {
                        logger.error("Unbehandelte Exception", e);
                    }
                    client.close();
                    return;
                }
                client.close();
            }
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", e);
            }
        }
    }

    private boolean isValidAjLink(String line) {
        try {
            if (line == null) {
                return false;
            }
            String password = PropertiesManager.getOptionsManager().
                getRemoteSettings().getOldPassword();
            if (line.substring(0, password.length()).compareTo(password) != 0) {
                return false;
            }
            if (line.indexOf("ajfsp://") == -1) {
                return false;
            }
        }
        catch (Exception e) {
            return false;
        }
        return true;
    }

    private String getLinkFromReadLine(String line) {
        if (!isValidAjLink(line)) {
            return null;
        }
        else {
            return line.substring(line.indexOf("ajfsp://"));
        }
    }
}