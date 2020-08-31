package de.applejuicenet.client.gui.components.tree;

import de.applejuicenet.client.fassade.entity.Directory;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/components/tree/ApplejuiceNode.java,v 1.4 2005/01/19 16:22:19 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 */

public interface ApplejuiceNode {
    ApplejuiceNode addChild(Directory childDirectory);

    Directory getDirectory();
}
