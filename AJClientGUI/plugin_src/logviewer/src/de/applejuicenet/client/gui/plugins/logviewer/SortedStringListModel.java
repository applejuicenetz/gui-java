/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.plugins.logviewer;

import java.io.File;

import java.util.HashSet;
import java.util.TreeSet;

import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

public class SortedStringListModel implements ListModel
{
   private TreeSet<File>             data      = new TreeSet<File>(new FileModificationDateComparator());
   private HashSet<ListDataListener> listeners = new HashSet<ListDataListener>();

   public void setData(File[] htmlFiles)
   {
      data.clear();
      for(File curValue : htmlFiles)
      {
         data.add(curValue);
      }
   }

   public int getSize()
   {
      return data.size();
   }

   public Object getElementAt(int index)
   {
      return data.toArray()[index];
   }

   public void addListDataListener(ListDataListener l)
   {
      listeners.add(l);
   }

   public void removeListDataListener(ListDataListener l)
   {
      listeners.remove(l);
   }
}
