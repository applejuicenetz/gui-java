package de.applejuicenet.client.shared.exception;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/shared/exception/LanguageSelectorNotInstanciatedException.java,v 1.7 2004/10/11 18:18:52 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 */

public class LanguageSelectorNotInstanciatedException
    extends Exception {
    private static final long serialVersionUID = -960088084018334822L;

	public LanguageSelectorNotInstanciatedException() {
        super("Der LanguageSelector muss erst initialisiert werden.");
    }
}