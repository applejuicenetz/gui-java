package de.applejuicenet.client.gui.share.tree;

import javax.swing.*;
import java.awt.*;


/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/share/tree/DirectoryTree.java,v 1.2 2004/11/22 16:25:26 maj0r Exp $
 *
 * <p>Title: AppleJuice Client-GUI</p>
 * <p>Description: Official GUI for the appleJuice core developed by muhviehstarr</p>
 * <p>Copyright: General Public License</p>
 *
 * @author Maj0r [aj@tkl-soft.de]
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
        setRowHeight(0);
    }
}
