package de.applejuicenet.client.gui.controller;

import java.util.*;

import org.w3c.dom.*;
import de.applejuicenet.client.shared.*;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/controller/Attic/SettingsXMLHolder.java,v 1.3 2003/06/10 12:31:03 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: SettingsXMLHolder.java,v $
 * Revision 1.3  2003/06/10 12:31:03  maj0r
 * Historie eingefügt.
 *
 *
 */

public class SettingsXMLHolder
    extends WebXMPParser {
  private AJSettings settings;

  public SettingsXMLHolder() {
    super("/xml/settings.xml", "", false);
  }

  public void update() {
    reload("");
    NodeList nodes = document.getElementsByTagName("nick");
    String nick = nodes.item(0).getFirstChild().getNodeValue();
    nodes = document.getElementsByTagName("port");
    long port = Long.parseLong(nodes.item(0).getFirstChild().getNodeValue());
    nodes = document.getElementsByTagName("xmlport");
    long xmlPort = Long.parseLong(nodes.item(0).getFirstChild().getNodeValue());
    nodes = document.getElementsByTagName("allowbrowse");
    boolean allowBrowse = nodes.item(0).getFirstChild().getNodeValue().
        compareToIgnoreCase("true") == 0 ? true : false;
    nodes = document.getElementsByTagName("maxupload");
    long maxUpload = Long.parseLong(nodes.item(0).getFirstChild().getNodeValue());
    nodes = document.getElementsByTagName("maxdownload");
    long maxDownload = Long.parseLong(nodes.item(0).getFirstChild().
                                      getNodeValue());
    nodes = document.getElementsByTagName("speedperslot");
    int speedPerSlot = Integer.parseInt(nodes.item(0).getFirstChild().
                                        getNodeValue());
    nodes = document.getElementsByTagName("incomingdirectory");
    String incomingDir = nodes.item(0).getFirstChild().getNodeValue();
    nodes = document.getElementsByTagName("temporarydirectory");
    String tempDir = nodes.item(0).getFirstChild().getNodeValue();
    HashSet shareEntries = new HashSet();
    nodes = document.getElementsByTagName("directory");
    for (int i = 0; i < nodes.getLength(); i++) {
      Element e = (Element) nodes.item(i);
      String dir = e.getAttribute("name");
      String shareMode = e.getAttribute("sharemode");
      ShareEntry entry = new ShareEntry(dir, shareMode);
      shareEntries.add(entry);
    }
    settings = new AJSettings(nick, port, xmlPort, allowBrowse, maxUpload,
                              maxDownload, speedPerSlot, incomingDir, tempDir,
                              shareEntries);
  }

  public AJSettings getAJSettings() {
    update();
    return settings;
  }
}