package de.applejuicenet.client.gui.tablerenderer;

import javax.swing.tree.*;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/tablerenderer/Attic/TreeTableModel.java,v 1.5 2003/06/10 12:31:03 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: TreeTableModel.java,v $
 * Revision 1.5  2003/06/10 12:31:03  maj0r
 * Historie eingefügt.
 *
 *
 */

public interface TreeTableModel
    extends TreeModel {
  public int getColumnCount();

  public String getColumnName(int column);

  public Class getColumnClass(int column);

  public Object getValueAt(Object node, int column);

  public boolean isCellEditable(Object node, int column);

  public void setValueAt(Object aValue, Object node, int column);

  public void fillTree();
}
