package de.applejuicenet.client.shared.exception;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/shared/exception/LanguageSelectorNotInstanciatedException.java,v 1.9 2004/11/22 16:25:25 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 */

public class LanguageSelectorNotInstanciatedException
    extends Exception {

	public LanguageSelectorNotInstanciatedException() {
        super("Der LanguageSelector muss erst initialisiert werden.");
    }
}