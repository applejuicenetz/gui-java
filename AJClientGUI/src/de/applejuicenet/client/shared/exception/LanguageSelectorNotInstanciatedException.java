package de.applejuicenet.client.shared.exception;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/shared/exception/LanguageSelectorNotInstanciatedException.java,v 1.8 2004/10/15 13:34:47 maj0r Exp $
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
    private static final long serialVersionUID = -960088084018334822L;

	public LanguageSelectorNotInstanciatedException() {
        super("Der LanguageSelector muss erst initialisiert werden.");
    }
}