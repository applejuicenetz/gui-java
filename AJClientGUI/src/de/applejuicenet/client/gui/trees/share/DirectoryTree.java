package de.applejuicenet.client.gui.trees.share;

import java.awt.Dimension;
import javax.swing.JTree;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/trees/share/Attic/DirectoryTree.java,v 1.4 2004/04/14 09:36:35 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r [maj0r@applejuicenet.de]
 *
 */

public class DirectoryTree
    extends JTree {

    public Dimension getPreferredScrollableViewportSize() {
        Dimension result = super.getPreferredScrollableViewportSize();
        result = new Dimension(200, result.height);
        return result;
    }

    public void updateUI(){
        super.updateUI();
        setRowHeight(DirectoryNode.getMaxHeight() + 3);
    }
}
