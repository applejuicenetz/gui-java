package de.applejuicenet.client.gui.controller;

import java.io.*;
import javax.xml.parsers.*;

import org.xml.sax.*;
import de.applejuicenet.client.gui.*;
import de.applejuicenet.client.shared.*;
import de.applejuicenet.client.shared.exception.*;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/controller/Attic/WebXMPParser.java,v 1.9 2003/06/10 12:31:03 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: WebXMPParser.java,v $
 * Revision 1.9  2003/06/10 12:31:03  maj0r
 * Historie eingefügt.
 *
 *
 */

public abstract class WebXMPParser
    extends XMLDecoder {
  private String host;
  private String xmlCommand;
  private long timestamp = 0;
  private boolean firstRun = true;
  private boolean useTimestamp = true;

  public WebXMPParser(String xmlCommand, String parameters) {
    super();
    String savedHost = OptionsManager.getInstance().getRemoteSettings().getHost();
    if (savedHost.length() == 0) {
      host = "localhost";
    }
    else {
      host = savedHost;
    }
    this.xmlCommand = xmlCommand;
    webXML = true;
  }

  public WebXMPParser(String xmlCommand, String parameters,
                      boolean useTimestamp) {
    super();
    this.useTimestamp = useTimestamp;
    String savedHost = OptionsManager.getInstance().getRemoteSettings().getHost();
    if (savedHost.length() == 0) {
      host = "localhost";
    }
    else {
      host = savedHost;
    }
    this.xmlCommand = xmlCommand;
    webXML = true;
  }

  public void reload(String parameters) {
    String xmlData = null;
    try {
      if (useTimestamp) {
        xmlData = HtmlLoader.getHtmlContent(host, HtmlLoader.GET,
                                            xmlCommand + "?timestamp=" +
                                            timestamp + parameters);
      }
      else {
        xmlData = HtmlLoader.getHtmlContent(host, HtmlLoader.GET,
                                            xmlCommand + "?" + parameters);
      }
    }
    catch (WebSiteNotFoundException ex) {
      AppleJuiceDialog.closeWithErrormessage(
          "Die Verbindung zum Core ist abgebrochen.\r\nDas GUI wird beendet.");
    }
    DocumentBuilderFactory factory =
        DocumentBuilderFactory.newInstance();
    try {
      DocumentBuilder builder = factory.newDocumentBuilder();
      xmlData = xmlData.replaceAll(
          "<!DOCTYPE applejuice SYSTEM \"applejuice_1.dtd\">", "");
      document = builder.parse(new InputSource(new StringReader(xmlData)));
      if (!firstRun) {
        if (useTimestamp) {
          timestamp = Long.parseLong(getFirstAttrbuteByTagName(new String[] {
              "applejuice", "time"}
              , true));
        }
      }
      else {
        firstRun = !firstRun;
      }
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

  public abstract void update();
}