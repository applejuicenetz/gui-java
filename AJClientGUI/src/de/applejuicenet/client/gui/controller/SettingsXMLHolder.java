package de.applejuicenet.client.gui.controller;

import java.util.*;

import org.w3c.dom.*;
import de.applejuicenet.client.shared.*;

/**
 * <p>Title: AppleJuice Client-GUI</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Maj0r
 * @version 1.0
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