package de.applejuicenet.client.gui.components.tree;

import de.applejuicenet.client.fassade.controller.dac.DirectoryDO;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/components/tree/ApplejuiceNode.java,v 1.2 2005/01/18 17:35:29 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 */

public interface ApplejuiceNode {
    ApplejuiceNode addChild(DirectoryDO childDirectoryDO);

    DirectoryDO getDO();
}
