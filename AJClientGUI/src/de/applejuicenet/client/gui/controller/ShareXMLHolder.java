package de.applejuicenet.client.gui.controller;

import java.util.HashMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import de.applejuicenet.client.shared.dac.ShareDO;

/**
 * <p>Title: AppleJuice Client-GUI</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Maj0r
 * @version 1.0
 */

public class ShareXMLHolder extends WebXMPParser {
  private HashMap shareMap;

  public ShareXMLHolder(){
    super("/xml/share.xml", "");
  }

  public void update(){
    reload("");
    updateShare();
  }

  private void updateShare(){
    if (shareMap == null) {
      shareMap = new HashMap();
    }
    if (document==null)
      reload("");
    NodeList nodes = document.getElementsByTagName("share");
    HashMap changedShare = new HashMap();
    for (int i = 0; i < nodes.getLength(); i++) {
      Element e = (Element) nodes.item(i);
      String id_key = e.getAttribute("id");
      String filename = e.getAttribute("filename");
      String size = e.getAttribute("size");
      String checksum = e.getAttribute("checksum");
      ShareDO share = new ShareDO(id_key, filename, size, checksum);
      changedShare.put(id_key, share);
    }
    shareMap.putAll(changedShare);
  }

  public HashMap getShare(){
    updateShare();
    return shareMap;
  }
}