package de.applejuicenet.client.gui.controller;

import org.w3c.dom.NodeList;
import java.util.HashMap;
import org.w3c.dom.Element;
import de.applejuicenet.client.shared.dac.ServerDO;
import de.applejuicenet.client.shared.NetworkInfo;

/**
 * <p>Title: AppleJuice Client-GUI</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Maj0r
 * @version 1.0
 */

public class ModifiedXMLHolder extends WebXMPParser {
  private HashMap serverMap;
  private NetworkInfo netInfo;

  public ModifiedXMLHolder(){
    super("/xml/modified.xml", "");
  }

  public HashMap getServer(){
    return serverMap;
  }

  public NetworkInfo getNetworkInfo(){
    return netInfo;
  }

  public void update(){
    updateServer();
    updateNetworkInfo();
  }

  private void updateServer(){
    if (serverMap==null)
      serverMap = new HashMap();
    NodeList nodes = document.getElementsByTagName("server");
    HashMap changedServer = new HashMap();
    for (int i=0; i<nodes.getLength(); i++){
      Element e = (Element) nodes.item(i);
      String id_key = e.getAttribute("id");
      int id = Integer.parseInt(id_key);
      String name = e.getAttribute("name");
      String host = e.getAttribute("host");
      long lastseen = Long.parseLong(e.getAttribute("lastseen"));
      String port = e.getAttribute("port");
      ServerDO server = new ServerDO(id, name, host, port, lastseen);
      changedServer.put(id_key, server);
    }
    serverMap.putAll(changedServer);
  }

  private void updateNetworkInfo(){
    NodeList nodes = document.getElementsByTagName("networkinfo");
    if (nodes.getLength()==0)
      return;  //Keine Veränderung seit dem letzten Abrufen
    Element e = (Element) nodes.item(0);  //Es gibt nur ein Netzerkinfo-Element
    String users = e.getAttribute("users");
    String dateien = e.getAttribute("files");
    String dateigroesse = e.getAttribute("filesize");
    boolean firewalled = (e.getAttribute("firewalled").compareToIgnoreCase("true")==0) ? true : false;
    String externeIP = e.getAttribute("ip");
    netInfo = new NetworkInfo(users, dateien, dateigroesse, firewalled, externeIP);
  }
}