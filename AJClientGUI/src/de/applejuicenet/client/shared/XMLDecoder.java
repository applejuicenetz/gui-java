package de.applejuicenet.client.shared;

import java.io.*;
import javax.xml.parsers.*;

import org.apache.xml.serialize.*;
import org.w3c.dom.*;
import org.xml.sax.*;

public abstract class XMLDecoder {
  protected Document document;
  private String filePath;
  protected boolean webXML = false;

  protected XMLDecoder(){}

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

  public Document getDocument(){
    return document;
  }

  public String getFirstAttrbuteByTagName(String[] attributePath, boolean lastIsElement) {
    if (!webXML)
      return getFirstAttrbuteByTagName(attributePath);
    else{
      NodeList nodes = document.getChildNodes();
      for (int i=0; i<attributePath.length; i++){
        for (int x = 0; x < nodes.getLength(); x++) {
          String test = nodes.item(x).getNodeName();
          if (nodes.item(x).getNodeName().equalsIgnoreCase(attributePath[i])) {
            if (i == attributePath.length - 1) {
              String result = "";
              if (lastIsElement){
                Element e = (Element) nodes.item(x);
                nodes = e.getChildNodes();
                result =  nodes.item(0).getNodeValue();
              }
              else{
                Element e = (Element) nodes.item(x);
                result = e.getAttribute(attributePath[attributePath.length-1]);
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
    Node rootNode = nodes.item(0);   //Element "root"
    nodes = rootNode.getChildNodes();
    for (int i=0; i<attributePath.length-1; i++){
      for (int x=0; x<nodes.getLength(); x++){
        String test = nodes.item(x).getNodeName();
        if (nodes.item(x).getNodeName().equalsIgnoreCase(attributePath[i])){
          if (i == attributePath.length-2){
            Element e = (Element) nodes.item(x);
            return e.getAttribute(attributePath[attributePath.length-1]);
          }
          else{
            nodes = nodes.item(x).getChildNodes();
            break;
          }
        }
      }
    }
    return "";  //Nicht gefunden
  }

  public void setAttributeByTagName(String[] attributePath, String newValue) {
    NodeList nodes = document.getChildNodes();
    Node rootNode = nodes.item(0);   //Element "root"
    nodes = rootNode.getChildNodes();
    for (int i=0; i<attributePath.length-1; i++){
      for (int x=0; x<nodes.getLength(); x++){
        String test = nodes.item(x).getNodeName();
        if (nodes.item(x).getNodeName().equalsIgnoreCase(attributePath[i])){
          if (i == attributePath.length-2){
            Element e = (Element) nodes.item(x);
            e.setAttribute(attributePath[attributePath.length - 1], ZeichenErsetzer.korrigiereUmlaute(newValue, true));
            try {
              XMLSerializer xs = new XMLSerializer(new FileWriter(filePath),
                                                   new OutputFormat(document,
                  "UTF-8", true));
              xs.serialize(document);
            }
            catch (IOException ioE) {
              ioE.printStackTrace();
            }
          }
          else{
            nodes = nodes.item(x).getChildNodes();
            break;
          }
        }
      }
    }
    return ;  //Nicht gefunden
  }
}
