package de.applejuicenet.client.gui.tables.download;

import de.applejuicenet.client.shared.IconManager;
import de.applejuicenet.client.shared.dac.DownloadDO;
import de.applejuicenet.client.shared.dac.DownloadSourceDO;

import javax.swing.*;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/tables/download/Attic/IconGetter.java,v 1.1 2003/09/02 16:06:26 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: IconGetter.java,v $
 * Revision 1.1  2003/09/02 16:06:26  maj0r
 * Downloadbaum komplett umgebaut.
 *
 *
 */

public abstract class IconGetter {
    private static ImageIcon downloadIcon;

    private static ImageIcon direktVerbundenIcon;
    private static ImageIcon verbindungUnbekanntIcon;
    private static ImageIcon indirektVerbundenIcon;

    private static void initIcons() {
        if (downloadIcon == null)
        {
            IconManager im = IconManager.getInstance();
            downloadIcon = im.getIcon("treeRoot");
            direktVerbundenIcon = im.getIcon("treeUebertrage");
            verbindungUnbekanntIcon = im.getIcon("treeIndirekt");
            indirektVerbundenIcon = im.getIcon("treeWarteschlange");
        }
    }

    public static ImageIcon getConvenientIcon(Object node) {
        initIcons();
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
