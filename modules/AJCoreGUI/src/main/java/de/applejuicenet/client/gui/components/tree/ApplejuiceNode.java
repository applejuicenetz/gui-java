package de.applejuicenet.client.gui.components.tree;

import de.applejuicenet.client.facade.entity.Directory;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/components/tree/ApplejuiceNode.java,v 1.4 2005/01/19 16:22:19 maj0r Exp $
 *
 * <p>Title: AppleJuice Client-GUI</p>
 * <p>Description: Official GUI for the appleJuice core developed by muhviehstarr</p>
 * <p>Copyright: General Public License</p>
 *
 * @author Maj0r <aj@tkl-soft.de>
 *
 */

public interface ApplejuiceNode {
    ApplejuiceNode addChild(Directory childDirectory);

    Directory getDirectory();
}
