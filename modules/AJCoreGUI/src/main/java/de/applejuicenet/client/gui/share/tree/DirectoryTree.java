package de.applejuicenet.client.gui.share.tree;

import javax.swing.*;
import java.awt.*;


/**
 * Copyright: General Public License
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
