package de.applejuicenet.client.gui.controller;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/controller/Attic/DownloadPartListXMLHolder.java,v 1.1 2003/08/11 14:10:28 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI fï¿½r den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: DownloadPartListXMLHolder.java,v $
 * Revision 1.1  2003/08/11 14:10:28  maj0r
 * DownloadPartList eingefügt.
 * Diverse Änderungen.
 *
 *
 */

import org.w3c.dom.*;
import org.apache.log4j.Logger;
import de.applejuicenet.client.shared.dac.*;
import de.applejuicenet.client.shared.LoggerUtils;

public class DownloadPartListXMLHolder
    extends WebXMLParser {
  private Logger logger;
  private DownloadDO downloadDO;

  public DownloadPartListXMLHolder(DownloadDO downloadDO) {
    super("/xml/downloadpartlist.xml", "", false);
    this.downloadDO = downloadDO;
    logger = Logger.getLogger(getClass());
  }

  public void update() {

  }

  public PartListDO getPartList() {
    String methode = "updateDownloadPartList() -";
    if (logger.isDebugEnabled()){
        logger.debug(LoggerUtils.createDebugMessage(methode, LoggerUtils.EINTRITT));
    }
    reload("id=" + downloadDO.getId());
    if (logger.isDebugEnabled()){
      logger.debug(LoggerUtils.createDebugMessage(methode + " Geholt vom Server", LoggerUtils.DEFAULT));
    }
    PartListDO partListDO = new PartListDO(downloadDO);
    Element e = null;
    NodeList nodes = document.getElementsByTagName("fileinformation");
    e = (Element) nodes.item(0);
    Long fileSize = new Long(e.getAttribute("filesize"));
    partListDO.setGroesse(fileSize);
    nodes = document.getElementsByTagName("part");
    int nodesSize = nodes.getLength();
    Long startPosition = null;
    int type;
    for (int i = 0; i < nodesSize; i++) {
      e = (Element) nodes.item(i);
      startPosition = new Long(e.getAttribute("fromposition"));
      type = new Integer(e.getAttribute("type")).intValue();
      partListDO.addPart(partListDO.new Part(startPosition, type));
    }
    if (logger.isDebugEnabled()){
        logger.debug(LoggerUtils.createDebugMessage(methode, LoggerUtils.AUSTRITT));
    }
    return partListDO;
  }
}