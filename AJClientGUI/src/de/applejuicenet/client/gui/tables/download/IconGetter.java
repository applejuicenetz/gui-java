package de.applejuicenet.client.gui.tables.download;

import javax.swing.ImageIcon;

import de.applejuicenet.client.shared.IconManager;
import de.applejuicenet.client.shared.dac.DownloadDO;
import de.applejuicenet.client.shared.dac.DownloadSourceDO;
import de.applejuicenet.client.shared.dac.UploadDO;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/tables/download/Attic/IconGetter.java,v 1.8 2004/05/25 08:53:02 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r [Maj0r@applejuicenet.de]
 *
 */

public abstract class IconGetter {
    private static ImageIcon downloadIcon = IconManager.getInstance().getIcon(
        "treeRoot");
    private static ImageIcon direktVerbundenIcon = IconManager.getInstance().
        getIcon("treeUebertrage");
    private static ImageIcon verbindungUnbekanntIcon = IconManager.getInstance().
        getIcon("verbindungUnbekannt");
    private static ImageIcon indirektVerbundenIcon = IconManager.getInstance().
        getIcon("treeWarteschlange");
    private static ImageIcon versucheIndirektIcon = IconManager.getInstance().
        getIcon("treeIndirekt");

    public static ImageIcon getConvenientIcon(Object node) {
        if (node.getClass() == DownloadDO.class) {
            return downloadIcon;
        }
        else if (node.getClass() == DownloadSourceDO.class) {
            switch ( ( (DownloadSourceDO) node).getDirectstate()) {
                case DownloadSourceDO.UNBEKANNT:
                    return verbindungUnbekanntIcon;
                case DownloadSourceDO.DIREKTE_VERBINDUNG:
                    return direktVerbundenIcon;
                case DownloadSourceDO.INDIREKTE_VERBINDUNG:
                    return indirektVerbundenIcon;
                case DownloadSourceDO.INDIREKTE_VERBINDUNG_UNBESTAETIGT:
                    return versucheIndirektIcon;
                default:
                    return verbindungUnbekanntIcon;
            }
        }
        else if (node.getClass() == UploadDO.class) {
            if ((((UploadDO)node).getStatus()==UploadDO.AKTIVE_UEBERTRAGUNG
                || ((UploadDO)node).getStatus()== UploadDO.WARTESCHLANGE)
               && ((UploadDO)node).getDirectState()==UploadDO.STATE_DIREKT_VERBUNDEN){
                return direktVerbundenIcon;
            }
            else{
                switch ( ( (UploadDO) node).getDirectState()) {
                    case UploadDO.STATE_UNBEKANNT:
                        return verbindungUnbekanntIcon;
                    case UploadDO.STATE_DIREKT_VERBUNDEN:
                        return direktVerbundenIcon;
                    case UploadDO.STATE_INDIREKT_VERBUNDEN:
                        return indirektVerbundenIcon;
                    default:
                        return verbindungUnbekanntIcon;
                }
            }
        }
        else {
            return null;
        }
    }
}
