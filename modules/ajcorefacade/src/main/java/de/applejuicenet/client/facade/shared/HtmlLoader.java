/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.facade.shared;

import de.applejuicenet.client.facade.exception.WebSiteNotFoundException;
import de.applejuicenet.client.facade.exception.WrongPasswordException;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.zip.DataFormatException;

public abstract class HtmlLoader
{
   private static final Logger logger = LoggerFactory.getLogger(HtmlLoader.class);

   public static final int POST = 0;
   public static final int GET = 1;
   private static final int DEFAULT_CONNECT_TIMEOUT_MILLIS = 10000;
   private static final int MAX_BODY_BYTES = 256 * 1024 * 1024;
   private static final int DEFAULT_READ_TIMEOUT_MILLIS = 60000;
   private static final int DEFAULT_TOTAL_TIMEOUT_MILLIS = 70000;

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
      return getHtmlXMLContent(host, port, method, command, withResult, connectTimeoutMillis,
                               readTimeoutMillis, DEFAULT_TOTAL_TIMEOUT_MILLIS, InetAddress::getByName);
   }

   @FunctionalInterface
   interface Resolver
   {
      InetAddress resolve(String host) throws IOException;
   }

   static String getHtmlXMLContent(String host, Integer port, int method, String command, boolean withResult,
                                   int connectTimeoutMillis, int readTimeoutMillis, int totalTimeoutMillis,
                                   Resolver resolver)
   {
      if(totalTimeoutMillis <= 0 || connectTimeoutMillis <= 0 || readTimeoutMillis <= 0)
      {
         throw new IllegalArgumentException("Timeouts must be positive");
      }
      java.util.concurrent.FutureTask<String> task = new java.util.concurrent.FutureTask<>(() ->
              request(host, port, method, command, withResult, connectTimeoutMillis, readTimeoutMillis, resolver));
      Thread.ofVirtual().name("CoreRequest").start(task);
      try
      {
         return task.get(totalTimeoutMillis, java.util.concurrent.TimeUnit.MILLISECONDS);
      }
      catch(java.util.concurrent.TimeoutException e)
      {
         throw new WebSiteNotFoundException(WebSiteNotFoundException.INPUT_ERROR,
                 new java.net.SocketTimeoutException("Core request exceeded its total deadline"));
      }
      catch(InterruptedException e)
      {
         Thread.currentThread().interrupt();
         throw new WebSiteNotFoundException(WebSiteNotFoundException.INPUT_ERROR,
                 new java.io.InterruptedIOException("Core request interrupted"));
      }
      catch(java.util.concurrent.ExecutionException e)
      {
         if(e.getCause() instanceof RuntimeException runtime)
         {
            throw runtime;
         }
         if(e.getCause() instanceof Error error)
         {
            throw error;
         }
         throw new WebSiteNotFoundException(WebSiteNotFoundException.INPUT_ERROR, e.getCause());
      }
      finally
      {
         // Interrupting SocketChannel I/O closes its socket, including blocked writes.
         task.cancel(true);
      }
   }

   private static String request(String host, Integer port, int method, String command, boolean withResult,
                                 int connectTimeoutMillis, int readTimeoutMillis, Resolver resolver)
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
            InetAddress addr   = resolver.resolve(host);
            if(Thread.currentThread().isInterrupted())
            {
               throw new InterruptedIOException("Core request cancelled during DNS resolution");
            }

            socket.connect(new InetSocketAddress(addr, ajPort), connectTimeoutMillis);
            socket.setSoTimeout(readTimeoutMillis);
            PrintWriter out    = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));

            String      httpMethod = "";

            if(method == HtmlLoader.GET)
            {
               httpMethod = StringConstants.GET_EMPTY;
            }
            else if(method == HtmlLoader.POST)
            {
               httpMethod = StringConstants.POST_EMPTY;
            }
            else
            {
               return "";
            }

            command = command.replaceAll(StringConstants.SPACE, StringConstants._20);
            out.println(httpMethod + command + StringConstants.HTTP_1_1);
            out.println(StringConstants.HOST_COLON + host);
            out.println();
            out.flush();

            if(out.checkError())
            {
               throw new IOException("Core request could not be sent");
            }

            if(!withResult)
            {
               return readAcknowledgement(new DataInputStream(new BufferedInputStream(socket.getInputStream(), 8192)));
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

               long length;

               try
               {
                  length = Long.parseLong(inputLine.substring(inputLine.indexOf(StringConstants.SPACE) + 1).trim());
               }
               catch(NumberFormatException nfe)
               {
                  throw new WebSiteNotFoundException(WebSiteNotFoundException.INPUT_ERROR, nfe);
               }

               if(length < 0 || length > MAX_BODY_BYTES)
               {
                  throw new WebSiteNotFoundException(WebSiteNotFoundException.INPUT_ERROR,
                                                     new IOException("Invalid response length: " + length));
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
                  byte[] body = in.readNBytes((int) length);

                  if(body.length < length)
                  {
                     throw new WebSiteNotFoundException(WebSiteNotFoundException.INPUT_ERROR,
                                                        new EOFException("Response ended after " + body.length + " of " + length +
                                                                         " bytes"));
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
         catch(SocketException socketException)
         {
            logger.debug("Core request to {}:{} failed: {}", host, port, socketException.toString());
            throw new WebSiteNotFoundException(WebSiteNotFoundException.AUTHORIZATION_REQUIRED, socketException);
         }
         catch(IOException ioE)
         {
            logger.debug("Core request to {}:{} failed: {}", host, port, ioE.toString());
            throw new WebSiteNotFoundException(WebSiteNotFoundException.AUTHORIZATION_REQUIRED, ioE);
         }
      }
      catch(WebSiteNotFoundException wnfE)
      {
         throw wnfE;
      }

      return urlContent.toString();
   }

   private static String readAcknowledgement(DataInputStream in) throws IOException
   {
      String status = readLn(in);
      if(!status.matches("HTTP/1\\.[01] 200(?: .*)?"))
      {
         // Authentication redirects can follow Content-Length, so inspect every header.
         String header;
         while(!(header = readLn(in)).isEmpty())
         {
            if(header.contains(StringConstants.WRONGPASSWORD))
            {
               throw new WrongPasswordException();
            }
         }
         throw new IOException("Core command returned an unsuccessful HTTP status");
      }
      int length = -1;
      String header;
      while(!(header = readLn(in)).isEmpty())
      {
         if(header.toLowerCase(java.util.Locale.ROOT).startsWith("content-length:"))
         {
            try
            {
               length = Integer.parseInt(header.substring(header.indexOf(':') + 1).trim());
            }
            catch(NumberFormatException e)
            {
               throw new IOException("Invalid acknowledgement length", e);
            }
            if(length < 0 || length > MAX_BODY_BYTES)
            {
               throw new IOException("Invalid acknowledgement length");
            }
         }
      }
      if(length < 0)
      {
         throw new IOException("Missing acknowledgement length");
      }
      byte[] body = in.readNBytes(length);
      if(body.length != length)
      {
         throw new EOFException("Incomplete core acknowledgement");
      }
      String result = new String(body, StandardCharsets.UTF_8).toLowerCase(java.util.Locale.ROOT);
      if(result.contains("error:") || result.contains("failure") || result.contains("invalid id"))
      {
         // Never include the response: it may echo credentials or private request data.
         throw new IOException("Core rejected the command");
      }
      return StringConstants.OK;
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
