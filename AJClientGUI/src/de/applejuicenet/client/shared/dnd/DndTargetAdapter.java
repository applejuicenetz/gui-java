package de.applejuicenet.client.shared.dnd;

import java.awt.*;
import java.awt.dnd.*;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/shared/dnd/Attic/DndTargetAdapter.java,v 1.2 2003/12/29 16:04:17 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: DndTargetAdapter.java,v $
 * Revision 1.2  2003/12/29 16:04:17  maj0r
 * Header korrigiert.
 *
 * Revision 1.1  2003/08/27 16:44:42  maj0r
 * Unterstuetzung fuer DragNDrop teilweise eingebaut.
 *
 *
 */

abstract public class DndTargetAdapter implements DropTargetListener {

    abstract protected Object getTarget(Point point);

    abstract public void drop(DropTargetDropEvent event);

    public void dragEnter(DropTargetDragEvent event) {
    }

    public void dragExit(DropTargetEvent event) {
    }

    public void dropActionChanged(DropTargetDragEvent event) {
    }

    public void dragOver(DropTargetDragEvent event) {
        if (getTarget(event.getLocation()) != null)
        {
            event.acceptDrag(event.getDropAction());
        }
        else
        {
            event.rejectDrag();
        }
    }
}
