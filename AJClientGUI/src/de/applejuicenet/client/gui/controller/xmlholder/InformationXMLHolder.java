package de.applejuicenet.client.gui.controller.xmlholder;

import de.applejuicenet.client.gui.controller.WebXMLParser;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/controller/xmlholder/Attic/InformationXMLHolder.java,v 1.1 2003/12/31 16:13:31 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: InformationXMLHolder.java,v $
 * Revision 1.1  2003/12/31 16:13:31  maj0r
 * Refactoring.
 *
 * Revision 1.5  2003/12/29 16:04:17  maj0r
 * Header korrigiert.
 *
 * Revision 1.4  2003/06/22 19:00:27  maj0r
 * Basisklasse umbenannt.
 *
 * Revision 1.3  2003/06/10 12:31:03  maj0r
 * Historie eingefügt.
 *
 *
 */

public class InformationXMLHolder
    extends WebXMLParser {
  public InformationXMLHolder() {
    super("/xml/information.xml", "");
  }

  public void update() {

  }
}