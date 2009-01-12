/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.mobileproxy;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.net.Socket;

import java.util.Hashtable;
import java.util.StringTokenizer;

public class HTTPRequest
{
   private Socket                    socket;
   private Hashtable<String, String> header;
   private Hashtable<String, String> params;
   private String                    requestedFile;
   private String                    httpMethod;

   /**
    * Erzeugt ein neues HTTPRequest-Objekt,
    * welches an den Socket so gebunden wird.
    *
    * @param so     der Socket, zum Lesen des Requests
    * @param log    ein Logger-Objekt, damit pro Client unterschiedliche
    *        Log-Levels definiert werden können
    */
   public HTTPRequest(Socket so)
   {
      this.socket = so;

      header = new Hashtable<String, String>();
      params = new Hashtable<String, String>();
   }

   /**
    * Liefert den Parameter-Wert zu n
    * @param n Name des Parameters
    * @return der zugehörige Wert oder null
    */
   public String getParameter(String n)
   {
      return params.get(n);
   }

   /**
    * Liefert das Header-Feld zu h
    * @param h Name des Headers
    * @return das zugehörige Headerfeld.
    */
   public String getHeader(String h)
   {
      return header.get(h);
   }

   /**
    * Liefer die HTTP Methode (GET, HEAD, POST)
    * dieses Requests.
    * @return die HTTP Methode dieses Requests
    */
   public String getHTTPMethod()
   {
      return httpMethod;
   }

   /**
    * Liefert die IP-Adresse dieses Clients.
    * @return die IP-Adresse dieses Clients.
    */
   public String getClientIP()
   {
      if(socket != null)
      {
         return socket.getInetAddress().getCanonicalHostName();
      }

      return null;
   }

   /**
    * Beantwortet den HTTP Request
    *
    */
   public boolean parse()
   {
      BufferedReader in = null;

      try
      {
         if(socket == null)
         {
            return false;
         }

         in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
         String line = in.readLine();

         if(line != null && line.indexOf("HTTP/") != -1)
         {
            StringTokenizer elems = new StringTokenizer(line, " \t");

            httpMethod    = elems.nextToken().toUpperCase();
            requestedFile = elems.nextToken();
         }
         else
         {
            in.close();
            return false;
         }

         int idx = requestedFile.indexOf('?');

         if(idx > 0)
         {

            // HTTP-GET Parameter
            StringTokenizer get   = new StringTokenizer(requestedFile.substring(idx + 1), "&");
            String          name  = null;
            String          value = null;

            while(get.hasMoreTokens())
            {
               String param = get.nextToken();
               int    idx2 = param.indexOf('=');

               if(idx2 > 0)
               {
                  name  = param.substring(0, idx2);
                  value = param.substring(idx2 + 1);
               }
               else
               {
                  name  = param.substring(0, idx2);
                  value = null;
               }

               params.put(name, value);
            }

            // requestedFile ohne HTTP GET Parameter:
            requestedFile = requestedFile.substring(0, idx);
         }

         while((line = in.readLine()) != null && !line.equals(""))
         {
            int sep = line.indexOf(':');

            if(sep == -1)
            {
               throw new RuntimeException("Failed to parse HTTP header: " + line);
            }

            String name  = line.substring(0, sep).trim();
            String value = line.substring(sep + 1).trim();

            header.put(name, value);
         }
      }
      catch(Exception e)
      {
         e.printStackTrace();
         return false;
      }

      return true;
   }

   public String toString()
   {
      return getClientIP();
   }

   public String getRequestedFile()
   {
      return requestedFile;
   }

   public Socket getSocket()
   {
      return socket;
   }
}
