package de.applejuicenet.client.gui.trees;

import de.applejuicenet.client.shared.dac.DirectoryDO;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/trees/Attic/ApplejuiceNode.java,v 1.2 2003/12/29 16:04:17 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: ApplejuiceNode.java,v $
 * Revision 1.2  2003/12/29 16:04:17  maj0r
 * Header korrigiert.
 *
 * Revision 1.1  2003/08/24 19:37:25  maj0r
 * no message
 *
 *
 */

public interface ApplejuiceNode {
    ApplejuiceNode addChild(DirectoryDO childDirectoryDO);

    DirectoryDO getDO();
}
