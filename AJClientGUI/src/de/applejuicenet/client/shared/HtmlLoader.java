package de.applejuicenet.client.shared;

import java.io.*;
import java.net.*;
import java.util.*;

import org.apache.xerces.impl.dv.util.*;
import de.applejuicenet.client.gui.controller.*;
import de.applejuicenet.client.shared.exception.*;

/**
 * <p>Title: AppleJuice Client-GUI</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Maj0r
 * @version 1.0
 */

public abstract class HtmlLoader {
  public static final int POST = 0;
  public static final int GET = 1;

  public static String getHtmlContent(String host, int method, String command) throws
      WebSiteNotFoundException {
    String urlContent = "";
    try {
      InetAddress addr = InetAddress.getByName(host);
      Socket socket = new Socket(addr, 9851);
      PrintWriter out = new PrintWriter(
      new BufferedWriter(
      new OutputStreamWriter(
      socket.getOutputStream())));

      String methode ="";
      if (method==HtmlLoader.GET)
        methode="GET ";
      else if (method==HtmlLoader.POST)
        methode="POST ";
      else
        return "";
      out.println(methode + command + " HTTP/1.1");
      out.println();
      out.flush();

      BufferedReader in = new BufferedReader(
      new InputStreamReader(
      socket.getInputStream()));

      String inputLine="" ;
      inputLine = in.readLine();
      while (inputLine.indexOf("xml version")==-1) {
        inputLine = in.readLine();
      }
      urlContent += inputLine;
      while ( (inputLine = in.readLine()) != null) {
        urlContent += inputLine;
      }
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
   return urlContent;
  }
}