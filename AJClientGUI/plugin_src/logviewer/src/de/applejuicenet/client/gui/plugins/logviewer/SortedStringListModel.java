package de.applejuicenet.client.gui.plugins.logviewer;

import java.util.HashSet;
import java.util.TreeSet;

import javax.swing.ListModel;
import javax.swing.event.ListDataListener;


public class SortedStringListModel implements ListModel{
    
    private TreeSet<String> data = new TreeSet<String>(new StringComparator());
    private HashSet<ListDataListener> listeners = new HashSet<ListDataListener>();

    public void setData(String[] newData){
        data.clear();
        for (String curValue:  newData){
            data.add(curValue);
        }
    }
    
    public int getSize() {
        return data.size();
    }

    public Object getElementAt(int index) {
        return data.toArray()[index];
    }

    public void addListDataListener(ListDataListener l) {
        listeners.add(l);
    }

    public void removeListDataListener(ListDataListener l) {
        listeners.remove(l);
    }
}
