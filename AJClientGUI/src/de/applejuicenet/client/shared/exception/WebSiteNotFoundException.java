package de.applejuicenet.client.shared.exception;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/shared/exception/Attic/WebSiteNotFoundException.java,v 1.8 2004/02/05 23:11:28 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: WebSiteNotFoundException.java,v $
 * Revision 1.8  2004/02/05 23:11:28  maj0r
 * Formatierung angepasst.
 *
 * Revision 1.7  2003/12/29 16:04:17  maj0r
 * Header korrigiert.
 *
 * Revision 1.6  2003/06/10 12:31:03  maj0r
 * Historie eingefügt.
 *
 *
 */

public class WebSiteNotFoundException
    extends Exception {
    public static final int AUTHORIZATION_REQUIRED = 407;
    public static final int UNKNOWN_HOST = 1;
    public static final int INPUT_ERROR = 2;

    private int error;

    public WebSiteNotFoundException(int errorCode) {
        super("Die Webseite konnte nicht aufgerufen werden.");
        error = errorCode;
    }

    public int getErrorCode() {
        return error;
    }

}