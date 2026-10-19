package de.applejuicenet.client.gui.components.util;

import de.applejuicenet.client.facade.entity.Download;
import de.applejuicenet.client.facade.entity.DownloadSource;
import de.applejuicenet.client.facade.entity.Upload;
import de.applejuicenet.client.shared.IconManager;

import javax.swing.*;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/components/util/IconGetter.java,v 1.4 2005/01/19 11:03:56 maj0r Exp $
 *
 * <p>Title: AppleJuice Client-GUI</p>
 * <p>Description: Official GUI for the appleJuice core developed by muhviehstarr</p>
 * <p>Copyright: General Public License</p>
 *
 * @author Maj0r [aj@tkl-soft.de]
 *
 */

public abstract class IconGetter {
    private static ImageIcon downloadIcon = IconManager.getInstance().getIcon(
        "treeRoot");
    private static ImageIcon directConnectedIcon = IconManager.getInstance().
        getIcon("treeUebertrage");
    private static ImageIcon connectionUnknownIcon = IconManager.getInstance().
        getIcon("verbindungUnbekannt");
    private static ImageIcon indirectConnectedIcon = IconManager.getInstance().
        getIcon("treeWarteschlange");
    private static ImageIcon tryingIndirectIcon = IconManager.getInstance().
        getIcon("treeIndirekt");

    public static ImageIcon getConvenientIcon(Object node) {
        if (node instanceof Download) {
            return downloadIcon;
        }
        else if (node instanceof DownloadSource) {
            switch ( ( (DownloadSource) node).getDirectstate()) {
                case DownloadSource.UNKNOWN:
                    return connectionUnknownIcon;
                case DownloadSource.DIRECT_CONNECTION:
                    return directConnectedIcon;
                case DownloadSource.INDIRECT_CONNECTION:
                    return indirectConnectedIcon;
                case DownloadSource.INDIRECT_CONNECTION_UNCONFIRMED:
                    return tryingIndirectIcon;
                default:
                    return connectionUnknownIcon;
            }
        }
        else if (node instanceof Upload) {
            if ((((Upload)node).getStatus()==Upload.ACTIVE_TRANSFER
                || ((Upload)node).getStatus()== Upload.QUEUE)
               && ((Upload)node).getDirectState()==Upload.STATE_DIRECT_CONNECTED){
                return directConnectedIcon;
            }
            else{
                switch ( ( (Upload) node).getDirectState()) {
                    case Upload.STATE_UNKNOWN:
                        return connectionUnknownIcon;
                    case Upload.STATE_DIRECT_CONNECTED:
                        return directConnectedIcon;
                    case Upload.STATE_INDIRECT_CONNECTED:
                        return indirectConnectedIcon;
                    default:
                        return connectionUnknownIcon;
                }
            }
        }
        else {
            return null;
        }
    }
}
