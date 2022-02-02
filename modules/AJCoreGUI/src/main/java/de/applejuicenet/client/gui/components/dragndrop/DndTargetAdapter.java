package de.applejuicenet.client.gui.components.dragndrop;

import java.awt.Point;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/components/dragndrop/DndTargetAdapter.java,v 1.1 2004/10/29 11:58:43 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author Maj0r <aj@tkl-soft.de>
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
