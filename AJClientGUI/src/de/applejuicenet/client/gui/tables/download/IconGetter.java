package de.applejuicenet.client.gui.tables.download;

import javax.swing.ImageIcon;

import de.applejuicenet.client.shared.IconManager;
import de.applejuicenet.client.shared.dac.DownloadDO;
import de.applejuicenet.client.shared.dac.DownloadSourceDO;
import de.applejuicenet.client.shared.dac.UploadDO;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/tables/download/Attic/IconGetter.java,v 1.7 2004/05/23 17:58:29 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: IconGetter.java,v $
 * Revision 1.7  2004/05/23 17:58:29  maj0r
 * Anpassungen an neue Schnittstelle.
 *
 * Revision 1.6  2004/02/21 17:09:57  maj0r
 * Neues Icon fuer unbekannte Verbindung eingebaut.
 *
 * Revision 1.5  2004/02/09 14:21:32  maj0r
 * Icons für Upload-DirectStates eingebaut.
 *
 * Revision 1.4  2004/02/05 23:11:28  maj0r
 * Formatierung angepasst.
 *
 * Revision 1.3  2003/12/29 16:04:17  maj0r
 * Header korrigiert.
 *
 * Revision 1.2  2003/10/16 12:06:37  maj0r
 * Diverse Schoenheitskorrekturen und Optimierungen.
 *
 * Revision 1.1  2003/09/02 16:06:26  maj0r
 * Downloadbaum komplett umgebaut.
 *
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
               && ((UploadDO)node).getDirectState()==UploadDO.STATE_UNBEKANNT){
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
