package de.applejuicenet.client.gui.plugins.serverwatcher;

import java.io.*;
import java.util.ArrayList;

import de.applejuicenet.client.shared.*;
import org.apache.log4j.Logger;
import org.apache.log4j.Level;
import org.apache.xml.serialize.XMLSerializer;
import org.apache.xml.serialize.OutputFormat;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Attr;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/plugin_src/serverwatcher/src/de/applejuicenet/client/gui/plugins/serverwatcher/ServerXML.java,v 1.1 2003/09/12 11:15:49 maj0r Exp $
 *
 * <p>Titel: AppleJuice Core-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: ServerXML.java,v $
 * Revision 1.1  2003/09/12 11:15:49  maj0r
 * Server lassen sich nun speichern und entfernen.
 * Version 1.1
 *
 *
 */

public class ServerXML
        extends XMLDecoder {
    private static Logger logger;
    private static String path;

    private ServerXML(String path) {
        super(path);
        logger = Logger.getLogger(getClass());
    }

    public static ServerConfig[] getServer() {
        try
        {
            path = System.getProperty("user.dir") + File.separator + "plugins" + File.separator +
                    "serverwatcher.xml";
            ServerXML connectionXML = new ServerXML(path);
            Element e = null;
            ArrayList serverConfigs = new ArrayList();
            String bezeichnung;
            String dyn;
            String port;
            String userpass;
            NodeList nodes = connectionXML.document.getElementsByTagName("server");
            nodes = nodes.item(0).getChildNodes();
            int nodesSize = nodes.getLength();
            for (int y = 0; y < nodesSize; y++)
            {
                if (nodes.item(y).getNodeType() == Node.ELEMENT_NODE)
                {
                    e = (Element) nodes.item(y);
                    bezeichnung = e.getNodeName();
                    dyn = e.getAttribute("dyn");
                    port = e.getAttribute("port");
                    userpass = e.getAttribute("userpass");
                    serverConfigs.add(new ServerConfig(bezeichnung, dyn, port, userpass));
                }
            }
            return (ServerConfig[]) serverConfigs.toArray(new ServerConfig[serverConfigs.size()]);
        }
        catch (Exception e)
        {
            if (logger.isEnabledFor(Level.ERROR))
                logger.error("Unbehandelte Exception", e);
            return null;
        }
    }

    public static void addServer(ServerConfig serverConfig) {
        try
        {
            String path = System.getProperty("user.dir") + File.separator + "plugins" + File.separator +
                    "serverwatcher.xml";
            ServerXML connectionXML = new ServerXML(path);

            Element e = null;
            NodeList nodes = connectionXML.document.getElementsByTagName(serverConfig.getBezeichnung());
            if (nodes.getLength() > 0)
            {
                e = (Element) nodes.item(0);
                e.setAttribute("dyn", serverConfig.getDyn());
                e.setAttribute("port", serverConfig.getPort());
                e.setAttribute("userpass", serverConfig.getUserPass());
            }
            else
            {
                nodes = connectionXML.document.getElementsByTagName("server");
                e = (Element) nodes.item(0);
                Node node = connectionXML.document.createElement(serverConfig.getBezeichnung());
                Element newNode = (Element) e.appendChild(node);
                Attr attr1 = connectionXML.document.createAttribute("dyn");
                attr1.setValue(serverConfig.getDyn());
                newNode.setAttributeNode(attr1);
                Attr attr2 = connectionXML.document.createAttribute("port");
                attr2.setValue(serverConfig.getPort());
                newNode.setAttributeNode(attr2);
                Attr attr3 = connectionXML.document.createAttribute("userpass");
                attr3.setValue(serverConfig.getUserPass());
                newNode.setAttributeNode(attr3);
            }
            connectionXML.save();
        }
        catch (Exception e)
        {
            if (logger.isEnabledFor(Level.ERROR))
                logger.error("Unbehandelte Exception", e);
        }
    }

    private void save() {
        try
        {
            XMLSerializer xs = new XMLSerializer(new FileWriter(path),
                                                 new OutputFormat(document,
                                                                  "UTF-8", true));
            xs.serialize(document);
        }
        catch (Exception e)
        {
            if (logger.isEnabledFor(Level.ERROR))
                logger.error("Unbehandelte Exception", e);
        }
    }

    public static void removeServer(ServerConfig serverConfig) {
        try
        {
            String path = System.getProperty("user.dir") + File.separator + "plugins" + File.separator +
                    "serverwatcher.xml";
            ServerXML connectionXML = new ServerXML(path);

            Element e = null;
            NodeList nodes = connectionXML.document.getElementsByTagName(serverConfig.getBezeichnung());
            e = (Element) nodes.item(0);
            Node parent = e.getParentNode();
            parent.removeChild(e);
            connectionXML.save();
        }
        catch (Exception e)
        {
            if (logger.isEnabledFor(Level.ERROR))
                logger.error("Unbehandelte Exception", e);
        }
    }
}