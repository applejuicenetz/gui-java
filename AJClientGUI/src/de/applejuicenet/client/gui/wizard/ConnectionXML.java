package de.applejuicenet.client.gui.wizard;

import java.io.*;
import java.util.ArrayList;

import de.applejuicenet.client.shared.*;
import org.apache.log4j.Logger;
import org.apache.log4j.Level;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/wizard/ConnectionXML.java,v 1.2 2003/09/10 13:28:22 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: ConnectionXML.java,v $
 * Revision 1.2  2003/09/10 13:28:22  maj0r
 * Wizard um neue Option MaxNewConnectionsPerTurn erweitert.
 *
 * Revision 1.1  2003/09/09 12:28:15  maj0r
 * Wizard fertiggestellt.
 *
 *
 */

public class ConnectionXML
        extends XMLDecoder {
    private static Logger logger;

    private ConnectionXML(String path) {
        super(path);
        logger = Logger.getLogger(getClass());
    }

    public static ConnectionKind[] getConnections(){
        try{
            String path = System.getProperty("user.dir") + File.separator +
                    "wizard.xml";
            ConnectionXML connectionXML = new ConnectionXML(path);

            Element e = null;
            ArrayList connectionKinds = new ArrayList();
            String bezeichnung;
            int maxUpload;
            int maxDownload;
            int maxNewConnectionsPro10Sek;
            NodeList nodes = connectionXML.document.getElementsByTagName("wizard");
            nodes = nodes.item(0).getChildNodes();
            int nodesSize = nodes.getLength();
            for (int y = 0; y < nodesSize; y++) {
                if (nodes.item(y).getNodeType()==Node.ELEMENT_NODE){
                    e = (Element) nodes.item(y);
                    bezeichnung = e.getAttribute("bezeichnung");
                    maxUpload = Integer.parseInt(e.getAttribute("maxupload"));
                    maxDownload = Integer.parseInt(e.getAttribute("maxdownload"));
                    maxNewConnectionsPro10Sek = Integer.parseInt(e.getAttribute("maxnewconnections10"));
                    connectionKinds.add(new ConnectionKind(bezeichnung, maxUpload, maxDownload, maxNewConnectionsPro10Sek));
                }
            }
            return (ConnectionKind[]) connectionKinds.toArray(new ConnectionKind[connectionKinds.size()]);
        }
        catch (Exception e)
        {
            if (logger.isEnabledFor(Level.ERROR))
                logger.error("Unbehandelte Exception", e);
            return null;
        }
    }
}