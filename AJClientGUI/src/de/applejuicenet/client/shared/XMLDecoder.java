package de.applejuicenet.client.shared;

import java.io.*;
import java.util.*;
import javax.xml.parsers.*;

import org.w3c.dom.*;
import org.xml.sax.*;

public abstract class XMLDecoder {
  private Document document;

  protected XMLDecoder(String filePath) {
    reload(filePath);
  }

  protected void reload(String filePath){
    DocumentBuilderFactory factory =
        DocumentBuilderFactory.newInstance();
    try {
      DocumentBuilder builder = factory.newDocumentBuilder();
      document = builder.parse(filePath);
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

  public String getFirstAttrbuteByTagName(String elementName,
                                          String attrbuteName) {
    Element ele = document.getDocumentElement();
    NodeList nl = ele.getElementsByTagName(elementName);
    if (nl.getLength() != 0) {
      Element language = (Element) nl.item(0);
      String attribute = language.getAttribute(attrbuteName);
      return attribute;
    }
    else {
      return null;
    }
  }

  public String getFirstAttrbuteByTagName(String elementName, String subElement,
                                          String attrbuteName) {
    Element ele = document.getDocumentElement();
    NodeList nl = ele.getElementsByTagName(elementName);
    if (nl.getLength() != 0) {
      NodeList nl2 = ele.getElementsByTagName(subElement);
      if (nl2.getLength() != 0) {
        Element language = (Element) nl2.item(0);
        String attribute = language.getAttribute(attrbuteName);
        return attribute;
      }
    }
    return null;
  }

  public String[] getAllAttrbutesByTagName(String elementName,
                                           String attrbuteName) {
    HashSet attributes = new HashSet();
    Element ele = document.getDocumentElement();
    NodeList nl = ele.getElementsByTagName(elementName);
    if (nl.getLength() != 0) {
      for (int i = 0; i < nl.getLength(); i++) {
        Element language = (Element) nl.item(0);
        attributes.add(language.getAttribute(attrbuteName));
      }
      return (String[]) attributes.toArray(new String[attributes.size()]);
    }
    else {
      return null;
    }
  }
}
