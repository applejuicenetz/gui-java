package de.applejuicenet.client.shared.dnd;

import java.awt.Point;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/shared/dnd/Attic/DndTargetAdapter.java,v 1.4 2004/10/11 18:18:51 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 */

abstract public class DndTargetAdapter
    implements DropTargetListener {

    abstract protected Object getTarget(Point point);

    abstract public void drop(DropTargetDropEvent event);

    public void dragEnter(DropTargetDragEvent event) {
    }

    public void dragExit(DropTargetEvent event) {
    }

    public void dropActionChanged(DropTargetDragEvent event) {
    }

    public void dragOver(DropTargetDragEvent event) {
        if (getTarget(event.getLocation()) != null) {
            event.acceptDrag(event.getDropAction());
        }
        else {
            event.rejectDrag();
        }
    }
}
