package de.applejuicenet.client.gui.components.tree;

import javax.swing.Icon;
import javax.swing.tree.DefaultMutableTreeNode;

import de.applejuicenet.client.shared.IconManager;
import de.applejuicenet.client.gui.components.treetable.Node;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.shared.ZeichenErsetzer;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/components/tree/WaitNode.java,v 1.2 2004/11/22 16:25:26 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
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