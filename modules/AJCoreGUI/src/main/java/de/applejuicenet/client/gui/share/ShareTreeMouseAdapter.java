package de.applejuicenet.client.gui.share;

import de.applejuicenet.client.fassade.ApplejuiceFassade;
import de.applejuicenet.client.gui.share.tree.DirectoryNode;
import de.applejuicenet.client.gui.share.tree.DirectoryTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/share/ShareTreeMouseAdapter.java,v 1.4 2005/01/18 17:35:29 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author Maj0r <aj@tkl-soft.de>
 */

public class ShareTreeMouseAdapter extends MouseAdapter {

    private static final Logger logger = LoggerFactory.getLogger(ShareTreeMouseAdapter.class);

    private final DirectoryTree folderTree;
    private final JPopupMenu popup;
    private final JMenuItem sharedWithSub;
    private final JMenuItem sharedWithoutSub;
    private final JMenuItem notShared;

    public ShareTreeMouseAdapter(DirectoryTree folderTree, JPopupMenu popup,
                                 JMenuItem sharedWithSub, JMenuItem sharedWithoutSub, JMenuItem notShared) {
        this.folderTree = folderTree;
        this.popup = popup;
        this.sharedWithSub = sharedWithSub;
        this.sharedWithoutSub = sharedWithoutSub;
        this.notShared = notShared;
    }

    public void mousePressed(MouseEvent me) {
        try {
            if (SwingUtilities.isRightMouseButton(me)) {
                Point p = me.getPoint();
                int iRow = folderTree.getRowForLocation(p.x, p.y);
                folderTree.setSelectionRow(iRow);
            }
            maybeShowPopup(me);
        } catch (Exception ex) {
            logger.error(ApplejuiceFassade.ERROR_MESSAGE, ex);
        }
    }

    public void mouseReleased(MouseEvent e) {
        try {
            super.mouseReleased(e);
            maybeShowPopup(e);
        } catch (Exception ex) {
            logger.error(ApplejuiceFassade.ERROR_MESSAGE, ex);
        }
    }

    private void maybeShowPopup(MouseEvent e) {
        try {
            if (e.isPopupTrigger()) {
                DirectoryNode node = (DirectoryNode) folderTree.
                        getLastSelectedPathComponent();
                if (node == null) {
                    return;
                }
                popup.removeAll();
                int nodeShareMode = node.getShareMode();
                if (nodeShareMode == DirectoryNode.NOT_SHARED
                        || nodeShareMode == DirectoryNode.SHARED_SOMETHING
                        || nodeShareMode == DirectoryNode.SHARED_SUB) {
                    popup.add(sharedWithSub);
                    popup.add(sharedWithoutSub);
                    popup.show(folderTree, e.getX(), e.getY());
                } else if (nodeShareMode == DirectoryNode.SHARED_WITH_SUB
                        ||
                        nodeShareMode == DirectoryNode.SHARED_WITHOUT_SUB) {
                    popup.add(notShared);
                    popup.show(folderTree, e.getX(), e.getY());
                }
            }
        } catch (Exception ex) {
            logger.error(ApplejuiceFassade.ERROR_MESSAGE, ex);
        }
    }
}
