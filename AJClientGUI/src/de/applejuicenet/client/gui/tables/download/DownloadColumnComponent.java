package de.applejuicenet.client.gui.tables.download;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/tables/download/Attic/DownloadColumnComponent.java,v 1.2 2004/02/24 18:21:51 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI f�r den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: DownloadColumnComponent.java,v $
 * Revision 1.2  2004/02/24 18:21:51  maj0r
 * Schrift korrigiert.
 *
 * Revision 1.1  2004/02/24 16:29:54  maj0r
 * Frisch dazu.
 *
 *
 */

import java.awt.Component;
import javax.swing.JTable;

public interface DownloadColumnComponent {
    public Component getProgressbarComponent(JTable table, Object value);

    public Component getVersionComponent(JTable table, Object value);
}