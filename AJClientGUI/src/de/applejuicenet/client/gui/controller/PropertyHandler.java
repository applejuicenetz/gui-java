/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.controller;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.HashSet;
import java.util.Properties;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import de.applejuicenet.client.fassade.ApplejuiceFassade;

public class PropertyHandler
{
   private Logger                          logger;
   private HashSet<PropertyChangeListener> listeners    = null;
   private String                          path;
   private Properties                      props;
   private String                          beschreibung;
   private boolean                         inform       = true;

   public PropertyHandler(String propertiesLocation, String beschreibung, boolean load)
                   throws IllegalArgumentException
   {
      logger = Logger.getLogger(getClass());
      try
      {
         path = propertiesLocation;
         if(beschreibung == null)
         {
            this.beschreibung = "";
         }
         else
         {
            this.beschreibung = beschreibung;
         }

         if(load)
         {
            reload();
         }
         else
         {
            props = new Properties();
         }
      }
      catch(Exception e)
      {
         if(logger.isEnabledFor(Level.ERROR))
         {
            logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
         }
      }
   }

   private void informListener(String identifier, String oldValue, String newValue)
   {
      if(listeners != null && inform)
      {
         PropertyChangeEvent propertyChangeEvent = new PropertyChangeEvent(this, identifier, oldValue, newValue);

         for(PropertyChangeListener curListener : listeners)
         {
            curListener.propertyChange(propertyChangeEvent);
         }
      }
   }

   public void allowInform(boolean shouldInform)
   {
      inform = shouldInform;
   }

   public void put(String identifier, String value)
   {
      try
      {
         String oldValue = get(identifier, null);

         props.put(identifier, value);
         informListener(identifier, oldValue, value);
      }
      catch(Exception e)
      {
         if(logger.isEnabledFor(Level.ERROR))
         {
            logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
         }
      }
   }

   public void put(String identifier, int value)
   {
      put(identifier, Integer.toString(value));
   }

   public void put(String identifier, boolean value)
   {
      put(identifier, Boolean.toString(value));
   }

   public String get(String identifier, String defaultValue)
   {
      Object obj = props.get(identifier);

      if(obj == null)
      {
         return defaultValue;
      }
      else
      {
         return obj.toString();
      }
   }

   public Boolean getAsBoolean(String identifier, Boolean defaultValue)
   {
      String obj = (String) props.get(identifier);

      if(null == obj)
      {
         return defaultValue;
      }

      if("true".equalsIgnoreCase(obj) || "false".equalsIgnoreCase(obj))
      {
         return Boolean.valueOf(obj);
      }
      else
      {
         return defaultValue;
      }
   }

   public Integer getAsInt(String identifier, int defaultValue)
   {
      String obj = (String) props.get(identifier);

      try
      {
         return new Integer(Integer.parseInt(obj));
      }
      catch(NumberFormatException nfE)
      {
         return defaultValue;
      }
   }

   public void reload() throws IllegalArgumentException
   {
      try
      {
         props = new Properties();
         FileInputStream inputStream = null;

         try
         {
            inputStream = new FileInputStream(path);
         }
         catch(FileNotFoundException e)
         {
            throw new IllegalArgumentException("PropertyDatei konnte nicht gefunden werden.");

         }

         props = new Properties();
         try
         {
            props.load(inputStream);
            inputStream.close();
         }
         catch(IOException e2)
         {
            throw new IllegalArgumentException("Ungueltige PropertyDatei.");
         }
      }
      catch(Exception e)
      {
         if(logger.isEnabledFor(Level.ERROR))
         {
            logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
         }
      }
   }

   public void save() throws IllegalArgumentException
   {
      try
      {
         File aFile = new File(path);

         try
         {
            aFile.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(aFile);

            props.store(outputStream, beschreibung);
            outputStream.close();
         }
         catch(IOException e)
         {
            throw new IllegalArgumentException("PropertyDatei konnte nicht gespeichert werden.");
         }
      }
      catch(Exception e)
      {
         if(logger.isEnabledFor(Level.ERROR))
         {
            logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
         }
      }
   }

   public boolean addPropertyChangeListener(PropertyChangeListener propertyChangeListener)
   {
      if(listeners == null)
      {
         listeners = new HashSet<PropertyChangeListener>();
      }

      return listeners.add(propertyChangeListener);
   }

   public boolean removePropertyChangeListener(PropertyChangeListener propertyChangeListener)
   {
      if(listeners == null)
      {
         return false;
      }

      return listeners.remove(propertyChangeListener);
   }
}
