package de.applejuicenet.client.gui.tables.download;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/tables/download/Attic/DownloadNode.java,v 1.15 2003/12/29 16:04:17 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: DownloadNode.java,v $
 * Revision 1.15  2003/12/29 16:04:17  maj0r
 * Header korrigiert.
 *
 * Revision 1.14  2003/09/02 16:06:26  maj0r
 * Downloadbaum komplett umgebaut.
 *
 *
 */

public interface DownloadNode {
    public Object[] getChildren();

    public int getChildCount();

    public boolean isLeaf();
}
