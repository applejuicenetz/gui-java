package de.applejuicenet.client.gui.plugins.ircplugin;

import javax.swing.*;
import java.util.*;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/plugin_src/ircplugin/src/de/applejuicenet/client/gui/plugins/ircplugin/SortedListModel.java,v 1.1 2003/09/12 06:32:17 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: SortedListModel.java,v $
 * Revision 1.1  2003/09/12 06:32:17  maj0r
 * Nur verschoben.
 *
 * Revision 1.2  2003/08/28 15:53:02  maj0r
 * NullPointer behoben und Header eingefuegt.
 *
 *
 */

public class SortedListModel extends AbstractListModel {

    private SortedSet model = new TreeSet();

    public SortedListModel() {
    }

    public int getSize() {
        return model.size();
    }

    public synchronized Object getElementAt(int index) {
        if (index < model.size())
            return model.toArray()[index];
        else
            return null;
    }

    public synchronized void add(Object element) {
        if (model.add(element))
        {
            fireIntervalAdded(this, 0, getSize());
        }
    }

    public void clear() {
        model.clear();
        fireIntervalRemoved(this, 0, getSize());
    }

    public boolean contains(Object element) {
        return model.contains(element);
    }

    public Object firstElement() {
        return model.first();
    }

    public Iterator iterator() {
        return model.iterator();
    }

    public Object lastElement() {
        return model.last();
    }

    public synchronized boolean remove(Object element) {
        boolean removed = model.remove(element);
        if (removed)
        {
            fireIntervalRemoved(this, 0, getSize());
        }
        return removed;
    }
}
