package de.applejuicenet.client.gui.upload.table;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/upload/table/Attic/UploadColumnComponent.java,v 1.1 2004/10/28 15:02:04 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 */

import java.awt.Component;
import javax.swing.JTable;

public interface UploadColumnComponent {
    public Component getProgressbarComponent(JTable table, Object value);

    public Component getWholeLoadedProgressbarComponent(JTable table, Object value);

    public Component getVersionComponent(JTable table, Object value);
}
