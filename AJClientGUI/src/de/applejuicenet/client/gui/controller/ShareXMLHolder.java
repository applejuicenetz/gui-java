package de.applejuicenet.client.gui.controller;

import java.util.*;

import org.w3c.dom.*;
import org.apache.log4j.Logger;
import org.apache.log4j.Level;
import de.applejuicenet.client.shared.dac.*;
import de.applejuicenet.client.shared.MapSetStringKey;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/controller/Attic/ShareXMLHolder.java,v 1.16 2003/12/29 16:04:17 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fï¿½r den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: ShareXMLHolder.java,v $
 * Revision 1.16  2003/12/29 16:04:17  maj0r
 * Header korrigiert.
 *
 * Revision 1.15  2003/10/21 14:08:45  maj0r
 * Mittels PMD Code verschoenert, optimiert.
 *
 * Revision 1.14  2003/10/12 15:57:55  maj0r
 * Kleinere Bugs behoben.
 * Sortiert wird nun nur noch bei Klick auf den Spaltenkopf um CPU-Zeit zu sparen.
 *
 * Revision 1.13  2003/09/04 22:12:45  maj0r
 * Logger verfeinert.
 * Threadbeendigung korrigiert.
 *
 * Revision 1.12  2003/09/01 15:50:51  maj0r
 * Wo es moeglich war, DOs auf primitive Datentypen umgebaut.
 *
 * Revision 1.11  2003/08/04 14:28:55  maj0r
 * An neue Schnittstelle angepasst.
 *
 * Revision 1.10  2003/08/03 19:54:05  maj0r
 * An neue Schnittstelle angepasst.
 *
 * Revision 1.9  2003/07/04 10:35:42  maj0r
 * Lesen des Sockets geht nun wesentlich schneller.
 * Share wird daher wesentlich schneller angezeigt.
 *
 * Revision 1.8  2003/07/02 13:54:34  maj0r
 * JTreeTable komplett überarbeitet.
 *
 * Revision 1.7  2003/07/01 14:58:07  maj0r
 * Loggerüberwachung eingefügt und unnützen Kram entfernt.
 *
 * Revision 1.6  2003/07/01 06:17:16  maj0r
 * Code optimiert.
 *
 * Revision 1.5  2003/06/22 19:00:27  maj0r
 * Basisklasse umbenannt.
 *
 * Revision 1.4  2003/06/10 12:31:03  maj0r
 * Historie eingefï¿½gt.
 *
 *
 */

public class ShareXMLHolder
        extends WebXMLParser {
    private HashMap shareMap;
    private Logger logger;

    public ShareXMLHolder() {
        super("/xml/share.xml", "", false);
        logger = Logger.getLogger(getClass());
    }

    public void update() {
        try{
            reload("");
        }
        catch(Exception e){
            if (logger.isEnabledFor(Level.ERROR))
                logger.error("Unbehandelte Exception", e);
        }
        updateShare();
    }

    private void updateShare() {
        try{
            if (shareMap == null) {
                shareMap = new HashMap();
            }
            reload("");
            NodeList nodes = document.getElementsByTagName("share");
            int nodesSize = nodes.getLength();
            Element e = null;
            int id_key;
            String filename = null;
            String shortfilename = null;
            long size;
            String checksum = null;
            ShareDO share = null;
            int prioritaet;
            for (int i = 0; i < nodesSize; i++) {
                e = (Element) nodes.item(i);
                id_key = Integer.parseInt(e.getAttribute("id"));
                filename = e.getAttribute("filename");
                shortfilename = e.getAttribute("shortfilename");
                size = Long.parseLong(e.getAttribute("size"));
                checksum = e.getAttribute("checksum");
                prioritaet = Integer.parseInt(e.getAttribute("priority"));
                share = new ShareDO(id_key, filename, shortfilename, size, checksum, prioritaet);
                shareMap.put(new MapSetStringKey(id_key), share);
            }
        }
        catch (Exception e){
            if (logger.isEnabledFor(Level.ERROR))
                logger.error("Unbehandelte Exception", e);
        }
    }

    public HashMap getShare() {
        updateShare();
        return shareMap;
    }
}