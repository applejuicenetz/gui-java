package de.applejuicenet.client.gui.trees.share;

import java.awt.Dimension;
import javax.swing.JTree;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/trees/share/Attic/DirectoryTree.java,v 1.3 2004/02/05 23:11:28 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: DirectoryTree.java,v $
 * Revision 1.3  2004/02/05 23:11:28  maj0r
 * Formatierung angepasst.
 *
 * Revision 1.2  2003/12/29 16:04:17  maj0r
 * Header korrigiert.
 *
 * Revision 1.1  2003/08/26 19:46:35  maj0r
 * Sharebereich weiter vervollstaendigt.
 *
 *
 */

public class DirectoryTree
    extends JTree {
    public Dimension getPreferredScrollableViewportSize() {
        Dimension result = super.getPreferredScrollableViewportSize();
        result = new Dimension(200, result.height);
        return result;
    }
}
