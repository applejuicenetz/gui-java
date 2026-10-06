/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.fassade.shared;

import de.applejuicenet.client.fassade.exception.WebSiteNotFoundException;
import de.applejuicenet.client.fassade.exception.WrongPasswordException;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.zip.DataFormatException;

public abstract class HtmlLoader
{
   public static final int POST = 0;
   public static final int GET = 1;
   private static final int DEFAULT_CONNECT_TIMEOUT_MILLIS = 10000;
   private static final int MAX_BODY_BYTES = 256 * 1024 * 1024;
   private static final int DEFAULT_READ_TIMEOUT_MILLIS = 60000;

   public static String getHtmlXMLContent(String host, Integer port, int method, String command, boolean withResult)
                                   throws WebSiteNotFoundException, WrongPasswordException
   {
      return getHtmlXMLContent(host, port, method, command, withResult, DEFAULT_CONNECT_TIMEOUT_MILLIS,
                               DEFAULT_READ_TIMEOUT_MILLIS);
   }

   static String getHtmlXMLContent(String host, Integer port, int method, String command, boolean withResult,
                                          int connectTimeoutMillis, int readTimeoutMillis)
                                   throws WebSiteNotFoundException, WrongPasswordException
   {
      int           ajPort     = port.intValue();
      StringBuilder urlContent = new StringBuilder();
      if(connectTimeoutMillis <= 0 || readTimeoutMillis <= 0)
      {
         throw new IllegalArgumentException("Timeouts must be positive");
      }

      try
      {
         try(SocketChannel channel = SocketChannel.open())
         {
            Socket socket = channel.socket();
            InetAddress addr   = InetAddress.getByName(host);

            socket.connect(new InetSocketAddress(addr, ajPort), connectTimeoutMillis);
            socket.setSoTimeout(readTimeoutMillis);
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

            DataInputStream in        = new DataInputStream(new BufferedInputStream(socket.getInputStream(), 8192));
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

               long laenge;

               try
               {
                  laenge = Long.parseLong(inputLine.substring(inputLine.indexOf(StringConstants.SPACE) + 1).trim());
               }
               catch(NumberFormatException nfe)
               {
                  throw new WebSiteNotFoundException(WebSiteNotFoundException.INPUT_ERROR, nfe);
               }

               if(laenge < 0 || laenge > MAX_BODY_BYTES)
               {
                  throw new WebSiteNotFoundException(WebSiteNotFoundException.INPUT_ERROR,
                                                     new IOException("Ungueltige Antwortlaenge: " + laenge));
               }

               in.readNBytes(1);
               if(command.indexOf(StringConstants.MODE_ZIP) != -1)
               {
                  try
                  {
                     byte[] data = ZLibUtils.inflate(in, MAX_BODY_BYTES);

                     urlContent.append(new String(data, StandardCharsets.ISO_8859_1));
                  }
                  catch(DataFormatException dfe)
                  {
                     throw new WebSiteNotFoundException(WebSiteNotFoundException.INPUT_ERROR, dfe);
                  }
               }
               else
               {
                  byte[] body = in.readNBytes((int) laenge);

                  if(body.length < laenge)
                  {
                     throw new WebSiteNotFoundException(WebSiteNotFoundException.INPUT_ERROR,
                                                        new EOFException("Antwort nach " + body.length + " von " + laenge +
                                                                         " Bytes beendet"));
                  }

                  urlContent.append(new String(body, Charset.defaultCharset()));
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

   private static String readLn(DataInputStream in) throws IOException
   {
      StringBuilder line = new StringBuilder();
      int           next;

      while((next = in.read()) != -1 && next != '\n')
      {
         line.append((char) next);
      }

      return line.toString().trim();
   }

   public static String getHtmlXMLContent(String host, Integer port, int method, String command)
                                   throws WebSiteNotFoundException
   {
      return getHtmlXMLContent(host, port, method, command, true);
   }
}
