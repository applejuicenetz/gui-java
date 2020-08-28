/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.fassade.shared;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

import de.applejuicenet.client.fassade.exception.WebSiteNotFoundException;
import de.applejuicenet.client.fassade.exception.WrongPasswordException;

public abstract class HtmlLoader
{
   public static final int POST = 0;
   public static final int GET = 1;

   public static String getHtmlXMLContent(String host, Integer port, int method, String command, boolean withResult)
                                   throws WebSiteNotFoundException, WrongPasswordException
   {
      int           ajPort     = port.intValue();
      StringBuilder urlContent = new StringBuilder();

      try
      {
         try
         {
            InetAddress addr   = InetAddress.getByName(host);
            Socket      socket = new Socket(addr, ajPort);
            PrintWriter out    = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));

            String      methode = "";

            if(method == HtmlLoader.GET)
            {
               methode = StringConstants.GET_EMTPY;
            }
            else if(method == HtmlLoader.POST)
            {
               methode = StringConstants.POST_EMPTY;
            }
            else
            {
               return "";
            }

            command = command.replaceAll(StringConstants.SPACE, StringConstants._20);
            out.println(methode + command + StringConstants.HTTP_1_1);
            out.println(StringConstants.HOST_DOUBLEPOINT + host);
            out.println();
            out.flush();

            if(!withResult)
            {
               return StringConstants.OK;
            }

            DataInputStream in        = new DataInputStream(socket.getInputStream());
            String          inputLine = readLn(in);

            if(method == HtmlLoader.GET)
            {
               if(inputLine == null || inputLine.length() == 0)
               {
                  throw new WebSiteNotFoundException(WebSiteNotFoundException.UNKNOWN_HOST);
               }
               while(inputLine.indexOf(StringConstants.CONTENT_LENGTH) == -1)
               {
                  inputLine = readLn(in);
                  if(inputLine == null || inputLine.length() == 0)
                  {
                     throw new WebSiteNotFoundException(WebSiteNotFoundException.UNKNOWN_HOST);
                  }

                  if(inputLine.indexOf(StringConstants.WRONGPASSWORD) != -1)
                  {
                     throw new WrongPasswordException();
                  }

                  if(inputLine.indexOf(StringConstants.INVALID_ID) != -1)
                  {
                     return StringConstants.EMPTY;
                  }
               }

               long laenge = Long.parseLong(inputLine.substring(inputLine.indexOf(StringConstants.SPACE) + 1));

               if(command.indexOf(StringConstants.MODE_ZIP) != -1)
               {
                  DataInputStream in_data = new DataInputStream(socket.getInputStream());

                  in_data.skip(1);
                  ByteArrayOutputStream baoS   = new ByteArrayOutputStream();
                  byte[]                toRead = new byte[2048];
                  int                   read;

                  while((read = in_data.read(toRead)) > 0)
                  {
                     baoS.write(toRead, 0, read);
                  }

                  urlContent.append(ZLibUtils.uncompress(baoS.toByteArray()));
                  baoS.close();
               }
               else
               {
                  in.skip(1);
                  byte[] toRead  = null;
                  int    gelesen = 0;

                  while(laenge > 0)
                  {
                     if(laenge > Integer.MAX_VALUE)
                     {
                        toRead = new byte[Integer.MAX_VALUE];
                     }
                     else
                     {
                        toRead = new byte[(int) laenge];
                     }

                     gelesen = in.read(toRead);
                     if(gelesen < toRead.length)
                     {
                        urlContent.append(new String(toRead, 0, gelesen));
                        laenge -= gelesen;
                     }
                     else
                     {
                        urlContent.append(new String(toRead));
                        laenge -= toRead.length;
                     }
                  }
               }
            }
            else
            {
               if(inputLine.compareToIgnoreCase(StringConstants.HTTP_1_1_200_OK) == 0)
               {
                  urlContent = new StringBuilder(inputLine);
               }
               else
               {
                  throw new WebSiteNotFoundException(WebSiteNotFoundException.INPUT_ERROR);
               }
            }
         }
         catch(SocketException sex)
         {
            throw new WebSiteNotFoundException(WebSiteNotFoundException.AUTHORIZATION_REQUIRED, sex);
         }
         catch(IOException ioE)
         {
            throw new WebSiteNotFoundException(WebSiteNotFoundException.AUTHORIZATION_REQUIRED, ioE);
         }
      }
      catch(WebSiteNotFoundException wnfE)
      {
         if(withResult)
         {
            throw wnfE;
         }
      }

      return urlContent.toString();
   }

   private static String readLn(DataInputStream in)
   {
      try
      {
         StringBuilder line   = new StringBuilder();
         byte[]        toRead = new byte[1];

         while(in.read(toRead) != -1 && (char) toRead[0] != '\n')
         {
            line.append((char) toRead[0]);
         }

         return line.toString().trim();
      }
      catch(Exception e)
      {
         return StringConstants.EMPTY;
      }
   }

   public static String getHtmlXMLContent(String host, Integer port, int method, String command)
                                   throws WebSiteNotFoundException
   {
      return getHtmlXMLContent(host, port, method, command, true);
   }
}
