package de.applejuicenet.client.gui.controller;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import java.net.InetAddress;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/controller/LinkListener.java,v 1.9 2004/06/15 09:43:49 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r [Maj0r@applejuicenet.de]
 *
 */

public class LinkListener
    implements Runnable {

    private final int PORT;

    private static Logger logger;
    private ServerSocket listen;
    private Thread connect;

    public LinkListener() throws IOException{
        PORT = OptionsManagerImpl.getInstance().getLinkListenerPort();
        logger = Logger.getLogger(getClass());
        try {
            listen = new ServerSocket(PORT);
            connect = new Thread(this);
            connect.start();
        }
        catch (IOException ioE){
            throw ioE;
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
            }
        }
    }

    public void run() {
        try {
            while (true) {
                Socket client = listen.accept();
                if (client.getInetAddress().getHostAddress().compareTo(
                    InetAddress.getByName("localhost").getHostAddress()) == 0){
                    try {
                        DataInputStream in = new DataInputStream(client.
                            getInputStream());
                        BufferedReader reader = new BufferedReader(new
                            InputStreamReader(in));
                        String line = reader.readLine();
                        if (line.indexOf("-link=") != -1) {
                            String link = getLinkFromReadLine(line);
                            if (link != null) {
                                ApplejuiceFassade.getInstance().processLink(
                                    link);
                            }
                        }
                        else if (line.indexOf("-command=") != -1) {
                            String command = line.substring(line.indexOf(
                                "-command=") + 9).toLowerCase();
                            if (command.startsWith("getajstats")) {
                                PrintStream out = new PrintStream(client.
                                    getOutputStream());
                                out.println(ApplejuiceFassade.getInstance().
                                            getStats());
                            }
                        }
                    }
                    catch (Exception e) {
                        if (logger.isEnabledFor(Level.ERROR)) {
                            logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
                        }
                        client.close();
                        return;
                    }
                }
                else{
                    DataInputStream in = new DataInputStream(client.
                        getInputStream());
                    BufferedReader reader = new BufferedReader(new
                        InputStreamReader(in));
                    reader.readLine();
                    PrintStream out = new PrintStream(client.
                        getOutputStream());
                    out.println("Fuck you, little bastard !!!");
                }
                client.close();
            }
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
            }
        }
    }

    private boolean isValidAjLink(String line) {
        try {
            if (line == null) {
                return false;
            }
            String password = OptionsManagerImpl.getInstance().
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
