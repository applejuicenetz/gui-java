package de.applejuicenet.client.shared;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/shared/Attic/XMLDecoder.java,v 1.22 2004/03/15 13:44:20 loevenwong Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI f�r den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: XMLDecoder.java,v $
 * Revision 1.22  2004/03/15 13:44:20  loevenwong
 * GUI neustart aufgrund fehlender property-datei verhindert.
 *
 * Revision 1.21  2004/03/03 15:33:31  maj0r
 * PMD-Optimierung
 *
 * Revision 1.20  2004/02/05 23:11:27  maj0r
 * Formatierung angepasst.
 *
 * Revision 1.19  2003/12/29 16:04:17  maj0r
 * Header korrigiert.
 *
 * Revision 1.18  2003/11/03 15:18:39  maj0r
 * Optimierungen.
 *
 * Revision 1.17  2003/10/21 14:08:45  maj0r
 * Mittels PMD Code verschoenert, optimiert.
 *
 * Revision 1.16  2003/10/14 15:39:28  maj0r
 * Stacktraces ausgebaut.
 *
 * Revision 1.15  2003/09/07 09:29:55  maj0r
 * Position des Hauptfensters und Breite der Tabellenspalten werden gespeichert.
 *
 * Revision 1.14  2003/09/06 14:51:39  maj0r
 * XMLDecoder bei Attributen korrigiert.
 *
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
 * Historie eingefuegt.
 *
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
            document = builder.parse(xmlFile);
            this.filePath = xmlFile.getPath();
        }
        catch (SAXException sxe) {
            Exception x = sxe;
            if (sxe.getException() != null) {
                x = sxe.getException();
            }
        }
        catch (Exception e) {
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
                        e.setAttribute(attributePath[attributePathSize - 1],
                                       ZeichenErsetzer.korrigiereUmlaute(
                            newValue, true));
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
