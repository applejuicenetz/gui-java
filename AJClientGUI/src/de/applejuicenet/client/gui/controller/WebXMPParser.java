package de.applejuicenet.client.gui.controller;

import de.applejuicenet.client.shared.XMLDecoder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import de.applejuicenet.client.shared.HtmlLoader;
import org.xml.sax.InputSource;
import java.io.StringReader;
import de.applejuicenet.client.shared.exception.*;
import java.sql.Time;


/**
 * <p>Title: AppleJuice Client-GUI</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Maj0r
 * @version 1.0
 */

public class WebXMPParser extends XMLDecoder {
  private String host;
  private String xmlCommand;
  private long timestamp=0;
  private boolean firstRun = true;

  public WebXMPParser(String xmlCommand, String parameters){
    super();
    String savedHost = OptionsManager.getInstance().getRemoteSettings().getHost();
    if (savedHost.length()==0)
      host = "localhost";
    else
      host = savedHost;
    this.xmlCommand = xmlCommand;
    webXML = true;
    reload(parameters);
  }

  public void reload(String parameters){
    String xmlData = null;
    try {
      xmlData = HtmlLoader.getHtmlContent(host, HtmlLoader.GET,
                                          xmlCommand + "?timestamp=" + timestamp + parameters);
    }
    catch (WebSiteNotFoundException ex) {}
    DocumentBuilderFactory factory =
        DocumentBuilderFactory.newInstance();
    try {
      DocumentBuilder builder = factory.newDocumentBuilder();
      xmlData = xmlData.replaceAll("<!DOCTYPE applejuice SYSTEM \"applejuice_1.dtd\">", "");
      document = builder.parse(new InputSource(new StringReader(xmlData)));
      if (!firstRun)
        timestamp = Long.parseLong(getFirstAttrbuteByTagName(new String[]{"applejuice", "time"}, true));
      else
        firstRun = !firstRun;
    }
    catch (SAXException sxe) {
      Exception x = sxe;
      if (sxe.getException() != null) {
        x = sxe.getException();
      }
      x.printStackTrace();

    }
    catch (ParserConfigurationException pce) {
      pce.printStackTrace();

    }
    catch (IOException ioe) {
      ioe.printStackTrace();
    }
  }
}