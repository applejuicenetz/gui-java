package de.applejuicenet.client.fassade.exception;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/ajcorefassade/src/de/applejuicenet/client/fassade/exception/Attic/LanguageSelectorNotInstanciatedException.java,v 1.1 2004/12/03 07:57:12 maj0r Exp $
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