package de.applejuicenet.client.shared.exception;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/shared/exception/Attic/WebSiteNotFoundException.java,v 1.13 2004/11/22 16:25:25 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
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

    public WebSiteNotFoundException(int errorCode, Throwable t) {
        super("Die Webseite konnte nicht aufgerufen werden.", t);
        error = errorCode;
    }

    public int getErrorCode() {
        return error;
    }

}