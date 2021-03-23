/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.controller;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.HashSet;

import org.apache.log4j.Logger;

import de.applejuicenet.client.AppleJuiceClient;
import de.applejuicenet.client.fassade.ApplejuiceFassade;
import de.applejuicenet.client.fassade.exception.IllegalArgumentException;
import de.applejuicenet.client.fassade.listener.CoreStatusListener;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/controller/LinkListener.java,v 1.15 2009/01/12 14:53:08 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r [aj@tkl-soft.de]
 *
 */
public class LinkListener extends Thread implements CoreStatusListener
{
   private static Logger     logger;
   private final int         PORT;
   private ServerSocket      listen;
   private ApplejuiceFassade applejuiceFassade = null;
   private HashSet<Link>     linkCache         = null;

   public LinkListener() throws IOException
   {
      PORT   = OptionsManagerImpl.getInstance().getLinkListenerPort();
      logger = Logger.getLogger(getClass());
      try
      {
         listen = new ServerSocket(PORT);
         setName("LinkListenerThread");
         setDaemon(true);
         start();
         ApplejuiceFassade.addCoreStatusListener(this);
      }
      catch(IOException ioE)
      {
         throw ioE;
      }
      catch(Exception e)
      {
         logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
      }
   }

   public void run()
   {
      try
      {
         while(true)
         {
            Socket client = listen.accept();

            if(client.getInetAddress().getHostAddress().compareTo(InetAddress.getByName("localhost").getHostAddress()) == 0)
            {
               try
               {
                  DataInputStream in     = new DataInputStream(client.getInputStream());
                  BufferedReader  reader = new BufferedReader(new InputStreamReader(in));
                  String          line   = reader.readLine();

                  if(line.contains("-link="))
                  {
                     String link = getLinkFromReadLine(line);
                     link = link.replaceAll("%7C", "|");

                     if(link != null)
                     {
                        Link aLink = new Link(link, "");

                        if(applejuiceFassade != null)
                        {
                           processLink(aLink);
                        }
                        else
                        {
                           if(linkCache == null)
                           {
                              linkCache = new HashSet<Link>();
                           }

                           linkCache.add(aLink);
                        }
                     }
                  }

                  //todo
                  /*                        else if (line.indexOf("-command=") != -1) {
                                              String command = line.substring(line.indexOf(
                                                  "-command=") + 9).toLowerCase();
                                              if (command.startsWith("getajstats")) {
                                                  PrintStream out = new PrintStream(client.
                                                      getOutputStream());
                                                  out.println(AppleJuiceClient.getAjFassade().
                                                              getStats());
                                              }
                                              else if (command.startsWith("getajinfo")) {
                                                  PrintStream out = new PrintStream(client.
                                                      getOutputStream());
                                                  out.println(ApplejuiceFassade.getInstance().getVersionInformation());
                                              }
                                          }*/
               }
               catch(Exception e)
               {
                  logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
                  client.close();
                  return;
               }
            }
            else
            {
               DataInputStream in     = new DataInputStream(client.getInputStream());
               BufferedReader  reader = new BufferedReader(new InputStreamReader(in));

               reader.readLine();
               PrintStream out = new PrintStream(client.getOutputStream());

               out.println("Fuck you, little bastard !!!");
            }

            client.close();
         }
      }
      catch(Exception e)
      {
         logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
      }
   }

   private boolean isValidAjLink(String line)
   {
      try
      {
         if(line == null)
         {
            return false;
         }

         String password = OptionsManagerImpl.getInstance().getRemoteSettings().getOldPassword();

         if(line.substring(0, password.length()).compareTo(password) != 0)
         {
            return false;
         }

         if(line.indexOf("ajfsp://") == -1)
         {
            return false;
         }
      }
      catch(Exception e)
      {
         return false;
      }

      return true;
   }

   private String getLinkFromReadLine(String line)
   {
      if(!isValidAjLink(line))
      {
         return null;
      }
      else
      {
         return line.substring(line.indexOf("ajfsp://"));
      }
   }

   public void fireStatusChanged(STATUS newStatus)
   {
      if(newStatus == STATUS.STARTED)
      {
         ApplejuiceFassade.removeCoreStatusListener(this);
         applejuiceFassade = AppleJuiceClient.getAjFassade();
         processCache();
      }
   }

   private void processCache()
   {
      if(linkCache == null)
      {
         return;
      }

      for(Link curLink : linkCache)
      {
         processLink(curLink);
      }

      linkCache.clear();
      linkCache = null;
   }

   public void processLink(String link, String directory)
   {
      link = link.replaceAll("%7C", "|");
      link = link.replaceAll("%20", ".");

      Link aLink = new Link(link, "");

      if(applejuiceFassade == null)
      {
         if(linkCache == null)
         {
            linkCache = new HashSet<Link>();
         }

         linkCache.add(aLink);
      }
      else
      {
         processLink(aLink);
      }
   }

   private void processLink(Link aLink)
   {
      try
      {
         applejuiceFassade.processLink(aLink.getLink(), aLink.getDirectory());
      }
      catch(IllegalArgumentException e)
      {
         // an dieser Stelle unterbuttern
         logger.warn(e);
      }
   }

   private class Link
   {
      private final String link;
      private final String directory;

      public Link(String link, String directory)
      {
         this.link      = link;
         this.directory = directory;
      }

      public String getDirectory()
      {
         return directory;
      }

      public String getLink()
      {
         return link;
      }
   }
}
