package de.applejuicenet.client.gui;

import javax.swing.Icon;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/Attic/OptionsRegister.java,v 1.2 2004/07/15 06:22:36 loevenwong Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: OptionsRegister.java,v $
 * Revision 1.2  2004/07/15 06:22:36  loevenwong
 * Anzeige nach erneutem Wizardstart aktualisiert (Danke UP^).
 *
 * Revision 1.1  2004/01/25 10:16:42  maj0r
 * Optionenmenue ueberarbeitet.
 *
 *
 */

public interface OptionsRegister {
    public Icon getIcon();

    public String getMenuText();

    public void reloadSettings();
}
