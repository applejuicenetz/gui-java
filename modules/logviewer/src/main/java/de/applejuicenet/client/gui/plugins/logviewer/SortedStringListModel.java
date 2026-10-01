/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.plugins.logviewer;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class SortedStringListModel implements ListModel
{
   private final List<File>                data      = new ArrayList<File>();
   private final HashSet<ListDataListener> listeners = new HashSet<ListDataListener>();

   public void setData(File[] htmlFiles)
   {
      int oldSize = data.size();

      data.clear();
      for(File curValue : htmlFiles)
      {
         data.add(curValue);
      }
      data.sort(new FileModificationDateComparator());
      fire(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, Math.max(oldSize, data.size()) - 1));
   }

   public void remove(File file)
   {
      int index = data.indexOf(file);

      if(index < 0)
      {
         return;
      }
      data.remove(index);
      fire(new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, index, index));
   }

   public File[] getFiles()
   {
      return data.toArray(new File[0]);
   }

   public int getSize()
   {
      return data.size();
   }

   public Object getElementAt(int index)
   {
      return data.get(index);
   }

   public void addListDataListener(ListDataListener l)
   {
      listeners.add(l);
   }

   public void removeListDataListener(ListDataListener l)
   {
      listeners.remove(l);
   }

   private void fire(ListDataEvent event)
   {
      if(event.getIndex1() < 0)
      {
         return;
      }
      for(ListDataListener listener : new HashSet<ListDataListener>(listeners))
      {
         if(event.getType() == ListDataEvent.INTERVAL_REMOVED)
         {
            listener.intervalRemoved(event);
         }
         else
         {
            listener.contentsChanged(event);
         }
      }
   }
}
