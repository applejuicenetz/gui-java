package de.applejuicenet.client.gui.tables.download;

import de.applejuicenet.client.shared.IconManager;
import de.applejuicenet.client.shared.dac.DownloadDO;
import de.applejuicenet.client.shared.dac.DownloadSourceDO;

import javax.swing.*;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/tables/download/Attic/IconGetter.java,v 1.2 2003/10/16 12:06:37 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: IconGetter.java,v $
 * Revision 1.2  2003/10/16 12:06:37  maj0r
 * Diverse Schoenheitskorrekturen und Optimierungen.
 *
 * Revision 1.1  2003/09/02 16:06:26  maj0r
 * Downloadbaum komplett umgebaut.
 *
 *
 */

public abstract class IconGetter {
    private static ImageIcon downloadIcon = IconManager.getInstance().getIcon("treeRoot");
    private static ImageIcon direktVerbundenIcon = IconManager.getInstance().getIcon("treeUebertrage");
    private static ImageIcon verbindungUnbekanntIcon = IconManager.getInstance().getIcon("treeIndirekt");
    private static ImageIcon indirektVerbundenIcon = IconManager.getInstance().getIcon("treeWarteschlange");

    public static ImageIcon getConvenientIcon(Object node) {
        if (node.getClass()==DownloadDO.class){
            return downloadIcon;
        }
        else if (node.getClass()==DownloadSourceDO.class){
            switch (((DownloadSourceDO)node).getDirectstate())
            {
                case DownloadSourceDO.UNBEKANNT:
                    return verbindungUnbekanntIcon;
                case DownloadSourceDO.DIREKTE_VERBINDUNG:
                    return direktVerbundenIcon;
                case DownloadSourceDO.INDIREKTE_VERBINDUNG:
                    return indirektVerbundenIcon;
                default:
                    return verbindungUnbekanntIcon;
            }
        }
        else
            return null;
    }
}
