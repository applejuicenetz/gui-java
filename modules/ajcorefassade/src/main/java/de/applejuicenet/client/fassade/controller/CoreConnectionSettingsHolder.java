/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.fassade.controller;

import de.applejuicenet.client.fassade.exception.IllegalArgumentException;
import de.applejuicenet.client.fassade.listener.CoreConnectionSettingsListener;
import de.applejuicenet.client.fassade.listener.CoreConnectionSettingsListener.ITEM;
import de.applejuicenet.client.fassade.tools.MD5Encoder;

import java.util.HashSet;

public final class CoreConnectionSettingsHolder
{
   private String                                  coreHost;
   private String                                  corePassword;
   private Integer                                 corePort;
   private HashSet<CoreConnectionSettingsListener> listener = new HashSet<CoreConnectionSettingsListener>();

   public CoreConnectionSettingsHolder(String coreHost, Integer corePort, String corePassword, boolean passwordIsPlaintext)
                                throws IllegalArgumentException
   {
      setCoreHost(coreHost);
      setCorePort(corePort);
      setCorePassword(corePassword, passwordIsPlaintext);
   }

   public String getCorePassword()
   {
      return corePassword;
   }

   public String getCoreHost()
   {
      return coreHost;
   }

   public Integer getCorePort()
   {
      return corePort;
   }

   public boolean isLocalhost()
   {
      return coreHost.equalsIgnoreCase("127.0.0.1") || coreHost.equalsIgnoreCase("localhost");
   }

   public void setCorePassword(String corePassword, boolean isPlaintext)
                        throws IllegalArgumentException
   {
      if(corePassword == null)
      {
         throw new IllegalArgumentException("illegal password");
      }

      String oldValue = this.corePassword;

      if(isPlaintext)
      {
         this.corePassword = MD5Encoder.getMD5(corePassword);
      }
      else
      {
         if(corePassword.length() == 0 || corePassword.trim().length() == 0)
         {
            throw new IllegalArgumentException("illegal password");
         }

         this.corePassword = corePassword;
      }

      informListener(ITEM.PASSWORD, oldValue, corePassword);
   }

   public void setCoreHost(String coreHost) throws IllegalArgumentException
   {
      if(coreHost == null || coreHost.length() == 0)
      {
         throw new IllegalArgumentException("illegal host");
      }

      String oldValue = this.coreHost;

      this.coreHost = coreHost;
      informListener(ITEM.HOST, oldValue, coreHost);
   }

   public void setCorePort(Integer corePort) throws IllegalArgumentException
   {
      if(corePort == null || corePort < 0)
      {
         throw new IllegalArgumentException("illegal port");
      }

      String oldValue = null;

      if(this.corePort != null)
      {
         this.corePort.toString();
      }

      this.corePort = corePort;
      informListener(ITEM.PORT, oldValue, corePort.toString());
   }

   public void addListener(CoreConnectionSettingsListener aListener)
   {
      listener.add(aListener);
   }

   public void removeListener(CoreConnectionSettingsListener aListener)
   {
      listener.remove(aListener);
   }

   private void informListener(ITEM item, String oldValue, String newValue)
   {
      for(CoreConnectionSettingsListener curListener : listener)
      {
         curListener.fireSettingsChanged(item, oldValue, newValue);
      }
   }
}
