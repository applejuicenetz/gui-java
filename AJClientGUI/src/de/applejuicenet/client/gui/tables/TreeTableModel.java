package de.applejuicenet.client.gui.tables;

import javax.swing.tree.TreeModel;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/tables/Attic/TreeTableModel.java,v 1.6 2004/10/11 18:18:51 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
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
}
