package de.applejuicenet.client.gui.controller.xmlholder;

import de.applejuicenet.client.gui.controller.WebXMLParser;
import org.apache.log4j.Logger;
import org.apache.log4j.Level;
import de.applejuicenet.client.shared.Information;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import de.applejuicenet.client.gui.controller.ApplejuiceFassade;
import de.applejuicenet.client.gui.listener.DataUpdateListener;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/controller/xmlholder/Attic/SecurerXMLHolder.java,v 1.2 2004/01/29 13:47:57 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fï¿½r den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: SecurerXMLHolder.java,v $
 * Revision 1.2  2004/01/29 13:47:57  maj0r
 * Während des ersten Holens der Quellen wird nun alle 5 Seks die Statuszeile aktualisiert.
 *
 * Revision 1.1  2004/01/28 12:34:46  maj0r
 * Session wird nun besser aufrecht erhalten.
 *
 *
 */

public class SecurerXMLHolder extends WebXMLParser {
    private Logger logger;

    public SecurerXMLHolder(){
        super("/xml/modified.xml", "");
        logger = Logger.getLogger(getClass());

    }

    public void update() {
        throw new RuntimeException();
    }

    public void secure(String sessionKontext, Information information){
        try{
            reload(sessionKontext + "&filter=informations", false);
            updateInformation(information);
        }
        catch (Exception ex) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", ex);
            }
        }
    }

    private void updateInformation(Information information){
        if (information != null){
            NodeList nodes = document.getElementsByTagName("information");
            long sessionUpload;
            long sessionDownload;
            long credits;
            long uploadSpeed;
            long downloadSpeed;
            long openConnections;
            if (nodes.getLength() != 0) {
                Element e = (Element) nodes.item(0);
                credits = Long.parseLong(e.getAttribute("credits"));
                uploadSpeed = Long.parseLong(e.getAttribute("uploadspeed"));
                downloadSpeed = Long.parseLong(e.getAttribute(
                    "downloadspeed"));
                openConnections = Long.parseLong(e.getAttribute(
                    "openconnections"));
                sessionUpload = Long.parseLong(e.getAttribute(
                    "sessionupload"));
                sessionDownload = Long.parseLong(e.getAttribute(
                    "sessiondownload"));
                information.setCredits(credits);
                information.setUploadSpeed(uploadSpeed);
                information.setDownloadSpeed(downloadSpeed);
                information.setOpenConnections(openConnections);
                information.setSessionUpload(sessionUpload);
                information.setSessionDownload(sessionDownload);
            }
            ApplejuiceFassade.getInstance().informDataUpdateListener(DataUpdateListener.INFORMATION_CHANGED);
        }
    }
}