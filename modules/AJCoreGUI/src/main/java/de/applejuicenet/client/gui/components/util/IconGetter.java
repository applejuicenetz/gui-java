package de.applejuicenet.client.gui.components.util;

import javax.swing.ImageIcon;

import de.applejuicenet.client.fassade.entity.Download;
import de.applejuicenet.client.fassade.entity.DownloadSource;
import de.applejuicenet.client.fassade.entity.Upload;
import de.applejuicenet.client.shared.IconManager;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/components/util/IconGetter.java,v 1.4 2005/01/19 11:03:56 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author Maj0r [aj@tkl-soft.de]
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
        if (node instanceof Download) {
            return downloadIcon;
        }
        else if (node instanceof DownloadSource) {
            switch ( ( (DownloadSource) node).getDirectstate()) {
                case DownloadSource.UNBEKANNT:
                    return verbindungUnbekanntIcon;
                case DownloadSource.DIREKTE_VERBINDUNG:
                    return direktVerbundenIcon;
                case DownloadSource.INDIREKTE_VERBINDUNG:
                    return indirektVerbundenIcon;
                case DownloadSource.INDIREKTE_VERBINDUNG_UNBESTAETIGT:
                    return versucheIndirektIcon;
                default:
                    return verbindungUnbekanntIcon;
            }
        }
        else if (node instanceof Upload) {
            if ((((Upload)node).getStatus()==Upload.AKTIVE_UEBERTRAGUNG
                || ((Upload)node).getStatus()== Upload.WARTESCHLANGE)
               && ((Upload)node).getDirectState()==Upload.STATE_DIREKT_VERBUNDEN){
                return direktVerbundenIcon;
            }
            else{
                switch ( ( (Upload) node).getDirectState()) {
                    case Upload.STATE_UNBEKANNT:
                        return verbindungUnbekanntIcon;
                    case Upload.STATE_DIREKT_VERBUNDEN:
                        return direktVerbundenIcon;
                    case Upload.STATE_INDIREKT_VERBUNDEN:
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
