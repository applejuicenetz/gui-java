package de.applejuicenet.client.gui.trees;

import de.applejuicenet.client.shared.dac.DirectoryDO;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/trees/Attic/ApplejuiceNode.java,v 1.1 2003/08/24 19:37:25 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: ApplejuiceNode.java,v $
 * Revision 1.1  2003/08/24 19:37:25  maj0r
 * no message
 *
 *
 */

public interface ApplejuiceNode {
    ApplejuiceNode addChild(DirectoryDO childDirectoryDO);

    DirectoryDO getDO();
}
