package de.applejuicenet.client.gui.tables.upload;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/tables/upload/Attic/UploadColumnComponent.java,v 1.1 2004/02/24 16:29:47 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: UploadColumnComponent.java,v $
 * Revision 1.1  2004/02/24 16:29:47  maj0r
 * Frisch dazu.
 *
 *
 */

import java.awt.Component;

public interface UploadColumnComponent {
    public Component getProgressbarComponent(Object value);

    public Component getVersionComponent(Object value);
}