/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.fassade.controller;

import de.applejuicenet.client.fassade.listener.DataUpdateListener;
import de.applejuicenet.client.fassade.listener.DataUpdateListener.DATALISTENER_TYPE;

import java.util.HashSet;
import java.util.Set;

public abstract class DataUpdateInformer
{
   private final DATALISTENER_TYPE listenerType;
   private Set<DataUpdateListener> listener = new HashSet<DataUpdateListener>();

   protected DataUpdateInformer(DATALISTENER_TYPE dataUpdateListenerType)
   {
      listenerType = dataUpdateListenerType;
   }

   public DATALISTENER_TYPE getDataUpdateListenerType()
   {
      return listenerType;
   }

   public void addDataUpdateListener(DataUpdateListener dataUpdateListener)
   {
      listener.add(dataUpdateListener);
   }

   public void removeDataUpdateListener(DataUpdateListener dataUpdateListener)
   {
      listener.remove(dataUpdateListener);
   }

   public void informDataUpdateListener()
   {
      Object content = getContentObject();

      for(DataUpdateListener curListener : listener)
      {
         curListener.fireContentChanged(listenerType, content);
      }
   }

   protected abstract Object getContentObject();
}
