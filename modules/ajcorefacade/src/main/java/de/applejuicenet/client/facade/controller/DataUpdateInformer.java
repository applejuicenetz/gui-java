/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.facade.controller;

import de.applejuicenet.client.facade.listener.DataUpdateListener;
import de.applejuicenet.client.facade.listener.DataUpdateListener.DATALISTENER_TYPE;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CopyOnWriteArraySet;
import java.util.Set;

public abstract class DataUpdateInformer
{
   private static final Logger logger = LoggerFactory.getLogger(DataUpdateInformer.class);
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
            logger.warn("Data listener {} failed ({})", curListener.getClass().getName(), listenerType, exception);
         }
      }
   }

   protected abstract Object getContentObject();
}
