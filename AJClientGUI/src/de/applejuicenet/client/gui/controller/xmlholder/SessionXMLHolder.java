package de.applejuicenet.client.gui.controller.xmlholder;

import de.applejuicenet.client.gui.controller.WebXMLParser;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/controller/xmlholder/Attic/SessionXMLHolder.java,v 1.3 2004/02/16 07:42:43 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: SessionXMLHolder.java,v $
 * Revision 1.3  2004/02/16 07:42:43  maj0r
 * alten Timestampfehler beseitig
 * Trotz Sessionumsetzung wurde immer noch der Timestamp mitgeschleppt.
 *
 * Revision 1.2  2004/02/05 23:11:28  maj0r
 * Formatierung angepasst.
 *
 * Revision 1.1  2003/12/31 16:13:31  maj0r
 * Refactoring.
 *
 * Revision 1.2  2003/12/29 16:04:17  maj0r
 * Header korrigiert.
 *
 * Revision 1.1  2003/09/10 15:30:48  maj0r
 * Begonnen auf neue Session-Struktur umzubauen.
 *
 * Revision 1.4  2003/06/22 19:00:27  maj0r
 * Basisklasse umbenannt.
 *
 * Revision 1.3  2003/06/10 12:31:03  maj0r
 * Historie eingefügt.
 *
 *
 */

public class SessionXMLHolder
    extends WebXMLParser {
    public SessionXMLHolder() {
        super("/xml/getsession.xml", "");
    }

    public void update() {

    }
}
