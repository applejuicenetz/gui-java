package de.applejuicenet.client.gui.trees;

import javax.swing.Icon;
import javax.swing.tree.DefaultMutableTreeNode;

import de.applejuicenet.client.gui.tables.Node;
import de.applejuicenet.client.shared.IconManager;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.shared.ZeichenErsetzer;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/trees/Attic/WaitNode.java,v 1.5 2004/02/26 15:10:55 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: WaitNode.java,v $
 * Revision 1.5  2004/02/26 15:10:55  maj0r
 * Auf LanguageSelector umgebaut.
 *
 * Revision 1.4  2004/02/05 23:11:28  maj0r
 * Formatierung angepasst.
 *
 * Revision 1.3  2003/12/29 16:04:17  maj0r
 * Header korrigiert.
 *
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

public class WaitNode
    extends DefaultMutableTreeNode
    implements Node {
    public Icon getConvenientIcon() {
        return IconManager.getInstance().getIcon("warten");
    }

    public String toString() {
        String anzeige = ZeichenErsetzer.korrigiereUmlaute(
            LanguageSelector.getInstance().getFirstAttrbuteByTagName(".root.javagui.downloadform.waitnodetext"));
        return anzeige;
    }
}
