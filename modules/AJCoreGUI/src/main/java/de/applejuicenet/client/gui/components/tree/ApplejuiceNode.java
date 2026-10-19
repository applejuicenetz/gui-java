package de.applejuicenet.client.gui.components.tree;

import de.applejuicenet.client.facade.entity.Directory;

/**
 * Copyright: General Public License
 */

public interface ApplejuiceNode {
    ApplejuiceNode addChild(Directory childDirectory);

    Directory getDirectory();
}
