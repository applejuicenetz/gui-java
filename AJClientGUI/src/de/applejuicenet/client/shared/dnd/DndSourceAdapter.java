package de.applejuicenet.client.shared.dnd;

import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/shared/dnd/Attic/DndSourceAdapter.java,v 1.5 2004/10/15 13:34:47 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 */

abstract public class DndSourceAdapter
    implements DragSourceListener {

    abstract public void dragDropEnd(DragSourceDropEvent event);

    protected void setCursor(DragSourceEvent event, int dropAction) {
        event.getDragSourceContext()
            .setCursor(dropAction == DnDConstants.ACTION_COPY
                       ? DragSource.DefaultCopyDrop
                       : dropAction == DnDConstants.ACTION_MOVE
                       ? DragSource.DefaultMoveDrop
                       : dropAction == DnDConstants.ACTION_LINK
                       ? DragSource.DefaultLinkDrop
                       : DragSource.DefaultMoveNoDrop);
    }

    public void dragEnter(DragSourceDragEvent event) {
        setCursor(event, event.getDropAction());
    }

    public void dragExit(DragSourceEvent event) {
        setCursor(event, DnDConstants.ACTION_NONE);
    }

    public void dragOver(DragSourceDragEvent event) {
    }

    public void dropActionChanged(DragSourceDragEvent event) {
        if (event.getTargetActions() != 0) {
            setCursor(event, event.getUserAction());
        }
    }
}
