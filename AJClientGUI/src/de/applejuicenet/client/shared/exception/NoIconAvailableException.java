package de.applejuicenet.client.shared.exception;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/shared/exception/NoIconAvailableException.java,v 1.8 2004/10/15 13:34:47 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 */

public class NoIconAvailableException
    extends Exception {
    private static final long serialVersionUID = 8049635056308992266L;

	public NoIconAvailableException() {
        super("An der angebenen Stelle wurde kein Icon gefunden.");
    }
}