package de.applejuicenet.client.gui.trees;

import de.applejuicenet.client.shared.dac.DirectoryDO;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/trees/Attic/ApplejuiceNode.java,v 1.3 2004/10/11 18:18:52 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 */

public interface ApplejuiceNode {
    ApplejuiceNode addChild(DirectoryDO childDirectoryDO);

    DirectoryDO getDO();
}
