package de.applejuicenet.client.gui.plugins.ircplugin;

import javax.swing.*;
import java.util.*;

public class SortedListModel extends AbstractListModel {

  SortedSet model;

  public SortedListModel() {
    model = new TreeSet();
  }

  public int getSize() {
    return model.size();
  }

  public synchronized Object getElementAt(int index) {
    return model.toArray()[index];
  }

  public synchronized void add(Object element) {
    if (model.add(element)) {
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
    if (removed) {
      fireIntervalRemoved(this, 0, getSize());
    }
    return removed;
  }
}
