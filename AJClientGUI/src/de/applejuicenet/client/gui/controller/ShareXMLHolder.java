package de.applejuicenet.client.gui.controller;

import java.util.*;
import java.text.SimpleDateFormat;

import org.w3c.dom.*;
import org.apache.log4j.Logger;
import de.applejuicenet.client.shared.dac.*;
import de.applejuicenet.client.shared.LoggerUtils;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/controller/Attic/ShareXMLHolder.java,v 1.8 2003/07/02 13:54:34 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI fï¿½r den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: ShareXMLHolder.java,v $
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
    super("/xml/share.xml", "");
    logger = Logger.getLogger(getClass());
  }

  public void update() {
    reload("");
    updateShare();
  }

  private void updateShare() {
    String methode = "updateShare() -";
    if (logger.isDebugEnabled()){
        logger.debug(LoggerUtils.createDebugMessage(methode, LoggerUtils.EINTRITT));
    }
    if (shareMap == null) {
      shareMap = new HashMap();
    }
    if (document == null) {
      reload("");
      if (logger.isDebugEnabled()){
          logger.debug(LoggerUtils.createDebugMessage(methode + " Geholt vom Server", LoggerUtils.DEFAULT));
      }
    }
    NodeList nodes = document.getElementsByTagName("share");
    int nodesSize = nodes.getLength();
    Element e = null;
    String id_key = null;
    String filename = null;
    String shortfilename = null;
    String size = null;
    String checksum = null;
    ShareDO share = null;
    for (int i = 0; i < nodesSize; i++) {
      e = (Element) nodes.item(i);
      id_key = e.getAttribute("id");
      filename = e.getAttribute("filename");
      shortfilename = e.getAttribute("shortfilename");
      size = e.getAttribute("size");
      checksum = e.getAttribute("checksum");
      share = new ShareDO(id_key, filename, shortfilename, size, checksum);
      shareMap.put(id_key, share);
    }
    if (logger.isDebugEnabled()){
        logger.debug(LoggerUtils.createDebugMessage(methode, LoggerUtils.AUSTRITT));
    }
  }

  public HashMap getShare() {
    updateShare();
    return shareMap;
  }
}