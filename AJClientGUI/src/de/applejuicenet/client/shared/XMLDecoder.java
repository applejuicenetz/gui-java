package de.applejuicenet.client.shared;

import java.io.*;
import javax.xml.parsers.*;

import org.apache.xml.serialize.*;
import org.w3c.dom.*;
import org.xml.sax.*;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/shared/Attic/XMLDecoder.java,v 1.13 2003/08/22 10:54:25 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI f�r den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: XMLDecoder.java,v $
 * Revision 1.13  2003/08/22 10:54:25  maj0r
 * Klassen umbenannt.
 * ConnectionSettings ueberarbeitet.
 *
 * Revision 1.12  2003/07/01 06:17:16  maj0r
 * Code optimiert.
 *
 * Revision 1.11  2003/06/30 20:35:50  maj0r
 * Code optimiert.
 *
 * Revision 1.10  2003/06/30 19:46:11  maj0r
 * Sourcestil verbessert.
 *
 * Revision 1.9  2003/06/10 12:31:03  maj0r
 * Historie eingef�gt.
 *
 *
 */

public abstract class XMLDecoder {
  protected Document document;
  private String filePath;
  protected boolean webXML = false;

  protected XMLDecoder() {}

  protected XMLDecoder(String filePath) {
    File xmlFile = new File(filePath);
    reload(xmlFile);
  }

  protected XMLDecoder(File xmlFile) {
    reload(xmlFile);
  }

  protected void reload(File xmlFile) {
    DocumentBuilderFactory factory =
        DocumentBuilderFactory.newInstance();
    try {
      DocumentBuilder builder = factory.newDocumentBuilder();
      document = builder.parse(xmlFile);
      this.filePath = xmlFile.getPath();
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

  public Document getDocument() {
    return document;
  }

  public String getFirstAttrbuteByTagName(String[] attributePath,
                                          boolean lastIsElement) {
    if (!webXML) {
      return getFirstAttrbuteByTagName(attributePath);
    }
    else {
      NodeList nodes = document.getChildNodes();
      int attributePathSize = attributePath.length;
      String result = null;
      Element e = null;
      for (int i = 0; i < attributePathSize; i++) {
        int nodesSize = nodes.getLength();
        for (int x = 0; x < nodesSize; x++) {
          if (nodes.item(x).getNodeName().equalsIgnoreCase(attributePath[i])) {
            if (i == attributePathSize - 1) {
              if (lastIsElement) {
                e = (Element) nodes.item(x);
                nodes = e.getChildNodes();
                result = nodes.item(0).getNodeValue();
              }
              else {
                e = (Element) nodes.item(x);
                result = e.getAttribute(attributePath[attributePathSize - 1]);
              }
              return result;
            }
            else {
              nodes = nodes.item(x).getChildNodes();
              break;
            }
          }
        }
      }
      return "";
    }

  }

  public String getFirstAttrbuteByTagName(String[] attributePath) {
    NodeList nodes = document.getChildNodes();
    Node rootNode = nodes.item(0); //Element "root"
    nodes = rootNode.getChildNodes();
    Element e = null;
    int attributePathSize = attributePath.length;
    for (int i = 0; i < attributePathSize - 1; i++) {
      int nodesSize = nodes.getLength();
      for (int x = 0; x < nodesSize; x++) {
        if (nodes.item(x).getNodeName().equalsIgnoreCase(attributePath[i])) {
          if (i == attributePathSize - 2) {
            e = (Element) nodes.item(x);
            return e.getAttribute(attributePath[attributePathSize - 1]);
          }
          else {
            nodes = nodes.item(x).getChildNodes();
            break;
          }
        }
      }
    }
    return ""; //Nicht gefunden
  }

  protected void setAttributeByTagName(String[] attributePath, String newValue) {
    NodeList nodes = document.getChildNodes();
    Node rootNode = nodes.item(0); //Element "root"
    nodes = rootNode.getChildNodes();
    Element e = null;
    XMLSerializer xs = null;
    int attributePathSize = attributePath.length;
    for (int i = 0; i < attributePathSize - 1; i++) {
      int nodesSize = nodes.getLength();
      for (int x = 0; x < nodesSize; x++) {
        if (nodes.item(x).getNodeName().equalsIgnoreCase(attributePath[i])) {
          if (i == attributePathSize - 2) {
            e = (Element) nodes.item(x);
            e.setAttribute(attributePath[attributePathSize - 1],
                           ZeichenErsetzer.korrigiereUmlaute(newValue, true));
            try {
              xs = new XMLSerializer(new FileWriter(filePath),
                  new OutputFormat(document,
                                   "UTF-8", true));
              xs.serialize(document);
              return;
            }
            catch (IOException ioE) {
              ioE.printStackTrace();
            }
          }
          else {
            nodes = nodes.item(x).getChildNodes();
            break;
          }
        }
      }
    }
    return; //Nicht gefunden
  }
}
