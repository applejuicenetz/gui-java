package de.applejuicenet.win.linkadapter;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/LinkAdapter/src/de/applejuicenet/win/linkadapter/Attic/WinLinkAdapter.java,v 1.1 2003/11/18 16:57:46 maj0r Exp $
 *
 * <p>Titel: AppleJuice WinLinkAdapter</p>
 * <p>Beschreibung: Zum direkten Anklicken von aj-Links (nur fuer Windows)</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: WinLinkAdapter.java,v $
 * Revision 1.1  2003/11/18 16:57:46  maj0r
 * Erste Version des WinLinkAdapters eingebaut.
 *
 *
 */

public class WinLinkAdapter {

    public static final int PORT = 8768;

    public WinLinkAdapter(String link) {
        try {
            Socket socket = new Socket("localhost", PORT);
            PrintStream out = new PrintStream(socket.getOutputStream());
            out.println(link);
            socket.close();
        }
        catch (IOException e) {
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        if (args != null && args.length == 1) {
            new WinLinkAdapter(args[0]);
        }
    }
}