package de.applejuicenet.client.fassade.shared;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/ajcorefassade/src/de/applejuicenet/client/fassade/shared/XMLDecoder.java,v 1.1 2004/12/03 07:57:12 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 */

public abstract class XMLDecoder {
    protected Document document;
    private String filePath;
    protected boolean webXML = false;

    protected XMLDecoder() {
    }

    protected XMLDecoder(String filePath) {
        File xmlFile = new File(filePath);
        reload(xmlFile);
    }

    protected XMLDecoder(File xmlFile) {
        reload(xmlFile);
    }

    protected void reload(String filePath) {
        File xmlFile = new File(filePath);
        this.reload(xmlFile);
    }

    protected void reload(File xmlFile) {
        DocumentBuilderFactory factory =
            DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.parse(new InputSource(new InputStreamReader(new FileInputStream(xmlFile), "UTF-8"))); 
            this.filePath = xmlFile.getPath();
        }
        catch (SAXException sxe) {
            Exception x = sxe;
            if (sxe.getException() != null) {
                x = sxe.getException();
            }
        }
        catch (Exception e) {
        	e.printStackTrace()
            ;
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
            int nodesSize;
            for (int i = 0; i < attributePathSize; i++) {
                nodesSize = nodes.getLength();
                for (int x = 0; x < nodesSize; x++) {
                    if (nodes.item(x).getNodeName().equalsIgnoreCase(
                        attributePath[i])) {
                        if (i == attributePathSize - 1 && lastIsElement) {
                            e = (Element) nodes.item(x);
                            nodes = e.getChildNodes();
                            result = nodes.item(0).getNodeValue();
                            return result;
                        }
                        else if (i == attributePathSize - 2 && !lastIsElement) {
                            e = (Element) nodes.item(x);
                            result = e.getAttribute(attributePath[
                                attributePathSize - 1]);
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
        int nodesSize;
        for (int i = 0; i < attributePathSize - 1; i++) {
            nodesSize = nodes.getLength();
            for (int x = 0; x < nodesSize; x++) {
                if (nodes.item(x).getNodeName().equalsIgnoreCase(attributePath[
                    i])) {
                    if (i == attributePathSize - 2) {
                        e = (Element) nodes.item(x);
                        return e.getAttribute(attributePath[attributePathSize -
                                              1]);
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

    protected void setAttributeByTagName(String[] attributePath, int newValue) {
        setAttributeByTagName(attributePath, Integer.toString(newValue));
    }

    protected void setAttributeByTagName(String[] attributePath,
                                         String newValue) {
        NodeList nodes = document.getChildNodes();
        Node rootNode = nodes.item(0); //Element "root"
        nodes = rootNode.getChildNodes();
        Element e = null;
        XMLSerializer xs = null;
        int attributePathSize = attributePath.length;
        int nodesSize;
        for (int i = 0; i < attributePathSize - 1; i++) {
            nodesSize = nodes.getLength();
            for (int x = 0; x < nodesSize; x++) {
                if (nodes.item(x).getNodeName().equalsIgnoreCase(attributePath[
                    i])) {
                    if (i == attributePathSize - 2) {
                        e = (Element) nodes.item(x);
                        e.setAttribute(attributePath[attributePathSize - 1], newValue);
                        try {
                            xs = new XMLSerializer(new FileWriter(filePath),
                                new OutputFormat(document,
                                                 "UTF-8", true));
                            xs.serialize(document);
                            return;
                        }
                        catch (IOException ioE) {
                            return;
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
