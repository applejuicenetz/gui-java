package de.applejuicenet.client.gui.controller;

import org.w3c.dom.*;
import org.apache.log4j.Logger;
import de.applejuicenet.client.shared.dac.*;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/controller/Attic/UserPartListXMLHolder.java,v 1.2 2003/10/13 19:14:04 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: UserPartListXMLHolder.java,v $
 * Revision 1.2  2003/10/13 19:14:04  maj0r
 * Kleinen Bug beim Entfernen von Downloads gefixt.
 *
 * Revision 1.1  2003/10/04 15:29:12  maj0r
 * Userpartliste hinzugefuegt.
 *
 *
 */

public class UserPartListXMLHolder
        extends WebXMLParser implements PartListHolder{
    private Logger logger;
    private DownloadSourceDO downloadSourceDO;

    public UserPartListXMLHolder(DownloadSourceDO downloadSourceDO) {
        super("/xml/userpartlist.xml", "", false);
        this.downloadSourceDO = downloadSourceDO;
        logger = Logger.getLogger(getClass());
    }

    public void update() {}

    public PartListDO getPartList() {
        try {
            reload("id=" + downloadSourceDO.getId());
            PartListDO partListDO = new PartListDO(downloadSourceDO);
            Element e = null;
            NodeList nodes = document.getElementsByTagName("fileinformation");
            e = (Element) nodes.item(0);
            long fileSize = new Long(e.getAttribute("filesize")).longValue();
            partListDO.setGroesse(fileSize);
            nodes = document.getElementsByTagName("part");
            int nodesSize = nodes.getLength();
            long startPosition;
            int type;
            for (int i = 0; i < nodesSize; i++) {
                e = (Element) nodes.item(i);
                startPosition = Long.parseLong(e.getAttribute("fromposition"));
                type = new Integer(e.getAttribute("type")).intValue();
                partListDO.addPart(partListDO.new Part(startPosition, type));
            }
            return partListDO;
        }
        catch (Exception e) {
            return null;
        }
    }
}