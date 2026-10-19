package de.applejuicenet.client.gui.components.dragndrop;

import java.awt.dnd.*;

/**
 * Copyright: General Public License
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
