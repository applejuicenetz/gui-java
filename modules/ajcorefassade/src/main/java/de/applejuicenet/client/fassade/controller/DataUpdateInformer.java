/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.fassade.controller;

import de.applejuicenet.client.fassade.listener.DataUpdateListener;
import de.applejuicenet.client.fassade.listener.DataUpdateListener.DATALISTENER_TYPE;

import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.Set;

public abstract class DataUpdateInformer
{
   private final DATALISTENER_TYPE listenerType;
   private final Set<DataUpdateListener> listener = new CopyOnWriteArraySet<>();

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
         try {
            curListener.fireContentChanged(listenerType, content);
         } catch (RuntimeException exception) {
            Logger.getLogger(DataUpdateInformer.class.getName()).log(Level.WARNING,
                  "Data listener failed: " + curListener.getClass().getName() + " (" + listenerType + ")", exception);
         }
      }
   }

   protected abstract Object getContentObject();
}
