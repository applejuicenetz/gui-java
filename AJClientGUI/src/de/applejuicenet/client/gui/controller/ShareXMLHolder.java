package de.applejuicenet.client.gui.controller;

import java.util.*;

import org.w3c.dom.*;
import de.applejuicenet.client.shared.dac.*;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/controller/Attic/ShareXMLHolder.java,v 1.6 2003/07/01 06:17:16 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI f�r den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: ShareXMLHolder.java,v $
 * Revision 1.6  2003/07/01 06:17:16  maj0r
 * Code optimiert.
 *
 * Revision 1.5  2003/06/22 19:00:27  maj0r
 * Basisklasse umbenannt.
 *
 * Revision 1.4  2003/06/10 12:31:03  maj0r
 * Historie eingef�gt.
 *
 *
 */

public class ShareXMLHolder
    extends WebXMLParser {
  private HashMap shareMap;

  public ShareXMLHolder() {
    super("/xml/share.xml", "");
  }

  public void update() {
    reload("");
    updateShare();
  }

  private void updateShare() {
    if (shareMap == null) {
      shareMap = new HashMap();
    }
    if (document == null) {
      reload("");
    }
    NodeList nodes = document.getElementsByTagName("share");
    HashMap changedShare = new HashMap();
    int nodesSize = nodes.getLength();
    Element e = null;
    String id_key = null;
    String filename = null;
    String size = null;
    String checksum = null;
    ShareDO share = null;
    for (int i = 0; i < nodesSize; i++) {
      e = (Element) nodes.item(i);
      id_key = e.getAttribute("id");
      filename = e.getAttribute("filename");
      size = e.getAttribute("size");
      checksum = e.getAttribute("checksum");
      share = new ShareDO(id_key, filename, size, checksum);
      changedShare.put(id_key, share);
    }
    shareMap.putAll(changedShare);
  }

  public HashMap getShare() {
    updateShare();
    return shareMap;
  }
}