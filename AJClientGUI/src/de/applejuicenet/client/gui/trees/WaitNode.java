package de.applejuicenet.client.gui.trees;

import de.applejuicenet.client.gui.tables.Node;
import de.applejuicenet.client.shared.IconManager;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.*;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/trees/Attic/WaitNode.java,v 1.2 2003/10/21 14:08:45 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: WaitNode.java,v $
 * Revision 1.2  2003/10/21 14:08:45  maj0r
 * Mittels PMD Code verschoenert, optimiert.
 *
 * Revision 1.1  2003/08/24 19:37:25  maj0r
 * no message
 *
 * Revision 1.1  2003/08/22 11:34:15  maj0r
 * WarteNode eingefuegt.
 *
 *
 */

public class WaitNode extends DefaultMutableTreeNode implements Node{
    public Icon getConvenientIcon() {
        return IconManager.getInstance().getIcon("warten");
    }

    public String toString() {
        return "bitte warten...";
    }
}
