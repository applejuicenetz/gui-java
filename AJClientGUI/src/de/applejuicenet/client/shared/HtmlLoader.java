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

  public static String getHtmlContent(String urlPath) throws
      WebSiteNotFoundException {
    boolean proxy = false;
    String[] proxySettings = OptionsManager.getInstance().getProxySettings();
    if (proxySettings != null) {
      proxy = true;
    }
    if (proxy) {
      Properties systemSettings = System.getProperties();
      systemSettings.put("proxySet", "true");
      systemSettings.put("firewallSet", "true");
      systemSettings.put("firewallHost", proxySettings[0]);
      systemSettings.put("firewallPort", proxySettings[1]);
      systemSettings.put("http.proxyHost", proxySettings[0]);
      systemSettings.put("http.proxyPort", proxySettings[1]);
      System.setProperties(systemSettings);
    }

    URL url = null;
    try {
      url = new URL(urlPath);
    }
    catch (MalformedURLException ex) {
      ex.printStackTrace();
      throw new WebSiteNotFoundException(ex);
    }

    HttpURLConnection uc = null;
    try {
      uc = (HttpURLConnection) url.openConnection();
    }
    catch (IOException ex1) {
      ex1.printStackTrace();
      throw new WebSiteNotFoundException(ex1);
    }

    if (proxy) {
      String encoded = new String(Base64.encode( (proxySettings[2] + ":" +
                                                  Base64.decode(proxySettings[3])).
                                                getBytes()));
      uc.setRequestProperty("Proxy-Authorization", "Basic " + encoded);
    }
    try {
      uc.connect();
    }
    catch (IOException ex2) {
      ex2.printStackTrace();
      throw new WebSiteNotFoundException(ex2);
    }
    InputStream in = null;

    try {
      in = uc.getInputStream();
    }
    catch (IOException e) {
      throw new WebSiteNotFoundException(e);
    }
    String urlContent = null;
    try {
      byte b[] = new byte[1000];
      int numRead = in.read(b);
      urlContent = new String(b, 0, numRead);
      String newContent = null;
      while (numRead != -1) {
        numRead = in.read(b);
        if (numRead != -1) {
          newContent = new String(b, 0, numRead);
          urlContent += newContent;
        }
      }
    }
    catch (IOException e) {
      throw new WebSiteNotFoundException(e);
    }
    return urlContent;
  }
}