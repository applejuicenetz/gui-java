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
    ProxyConfiguration proxy = OptionsManager.getInstance().getProxySettings();

    if (proxy.isProxyUsed()) {
      Properties systemSettings = System.getProperties();
      systemSettings.put("proxySet", "true");
      systemSettings.put("firewallSet", "true");
      systemSettings.put("firewallHost", proxy.getIP());
      systemSettings.put("firewallPort", Integer.toString(proxy.getPort()));
      systemSettings.put("http.proxyHost", proxy.getIP());
      systemSettings.put("http.proxyPort", Integer.toString(proxy.getPort()));
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

    if (proxy.isProxyUsed()) {
      String encoded = new String(Base64.encode( (proxy.getUsername() + ":" +
                                                  proxy.getPassword()).
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