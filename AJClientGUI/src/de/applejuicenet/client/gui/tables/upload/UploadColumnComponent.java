package de.applejuicenet.client.gui.tables.upload;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/tables/upload/Attic/UploadColumnComponent.java,v 1.3 2004/06/11 09:24:30 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: UploadColumnComponent.java,v $
 * Revision 1.3  2004/06/11 09:24:30  maj0r
 * [Maj0r] Wasserstände der einzelnen Uploader werden angezeigt.
 * [Maj0r] Beim Serverwechsel wird nun eine qualifizierte Warnung ausgegeben, wenn die aktuelle Verbindung noch keine 30 Minuten besteht.
 * [Maj0r] Bugfix
 *      Beim Neuerzeigen der properties.xml wurden die neuen Coredaten nicht für die aktuelle Sitzung übernommen. Folge war ein Verbindungsverlust.
 *
 * Revision 1.2  2004/02/24 18:21:51  maj0r
 * Schrift korrigiert.
 *
 * Revision 1.1  2004/02/24 16:29:47  maj0r
 * Frisch dazu.
 *
 *
 */

import java.awt.Component;
import javax.swing.JTable;

public interface UploadColumnComponent {
    public Component getProgressbarComponent(JTable table, Object value);

    public Component getWholeLoadedProgressbarComponent(JTable table, Object value);

    public Component getVersionComponent(JTable table, Object value);
}
