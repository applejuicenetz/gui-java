package de.applejuicenet.client.gui.share.tree;

import java.awt.Dimension;
import javax.swing.JTree;


/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/share/tree/DirectoryTree.java,v 1.1 2004/10/29 11:58:43 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r [aj@tkl-soft.de]
 *
 */

public class DirectoryTree
    extends JTree {

    private static final long serialVersionUID = 5052034838786568947L;

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
