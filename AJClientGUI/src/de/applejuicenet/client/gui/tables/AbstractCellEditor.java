package de.applejuicenet.client.gui.tables;

import java.util.*;

import javax.swing.*;
import javax.swing.event.*;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/tables/Attic/AbstractCellEditor.java,v 1.1 2003/07/01 18:41:39 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI f�r den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: AbstractCellEditor.java,v $
 * Revision 1.1  2003/07/01 18:41:39  maj0r
 * Struktur ver�ndert.
 *
 * Revision 1.4  2003/06/10 12:31:03  maj0r
 * Historie eingef�gt.
 *
 *
 */

public class AbstractCellEditor
    implements CellEditor {

  protected EventListenerList listenerList = new EventListenerList();

  public Object getCellEditorValue() {
    return null;
  }

  public boolean isCellEditable(EventObject e) {
    return true;
  }

  public boolean shouldSelectCell(EventObject anEvent) {
    return false;
  }

  public boolean stopCellEditing() {
    return true;
  }

  public void cancelCellEditing() {}

  public void addCellEditorListener(CellEditorListener l) {
    listenerList.add(CellEditorListener.class, l);
  }

  public void removeCellEditorListener(CellEditorListener l) {
    listenerList.remove(CellEditorListener.class, l);
  }

  protected void fireEditingStopped() {
    Object[] listeners = listenerList.getListenerList();
    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      if (listeners[i] == CellEditorListener.class) {
        ( (CellEditorListener) listeners[i + 1]).editingStopped(new ChangeEvent(this));
      }
    }
  }

  protected void fireEditingCanceled() {
    Object[] listeners = listenerList.getListenerList();
    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      if (listeners[i] == CellEditorListener.class) {
        ( (CellEditorListener) listeners[i +
         1]).editingCanceled(new ChangeEvent(this));
      }
    }
  }
}