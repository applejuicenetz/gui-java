package de.applejuicenet.client.gui.controller.xmlholder;

import de.applejuicenet.client.gui.controller.WebXMLParser;
import org.apache.log4j.Logger;
import org.apache.log4j.Level;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/controller/xmlholder/Attic/SecurerXMLHolder.java,v 1.1 2004/01/28 12:34:46 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI f�r den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: SecurerXMLHolder.java,v $
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

    public void secure(String sessionKontext){
        try{
            reload(sessionKontext + "&filter=informations", false);
        }
        catch (Exception ex) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", ex);
            }
        }
    }
}