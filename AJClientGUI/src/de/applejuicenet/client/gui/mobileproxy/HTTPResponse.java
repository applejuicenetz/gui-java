/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.mobileproxy;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Hashtable;
import java.util.Locale;
import java.util.TimeZone;

public class HTTPResponse
{
   public final static int HTTP_OK                  = 0;
   public final static int HTTP_Redirect            = 1;
   public final static int HTTP_BadRequest          = 2;
   public final static int HTTP_Forbidden           = 3;
   public final static int HTTP_NotFound            = 4;
   public final static int HTTP_InternalServerError = 5;
   public final static int HTTP_NotImplemented      = 6;

   /** String-Konstante fÃ¼r HTTP-Repsonse-Code */
   public final static String[] ResponseCodes = 
                                                {
                                                   "200 OK", "301 Moved Permanently", "400 Bad Request", "403 Forbidden",
                                                   "404 Not Found", "500 Internal Server Error", "501 Not Implemented"
                                                };

   /**
   * Der Dateinamen fÃ¼r die Default-Datei, die verwendet wird, wenn nur ein
   * Verzeichnis abgefragt wird.
   */
   public static String DefaultFile = "index.html";

   /**
   * Name des Verzeichnisses, in dem die Dateien zum Verschicken liegen
   */
   public static String HTMLDir = "html";

   /**
   * Name der Datei, die bei einem FileNotFound-Ereignis verschickt wird
   */
   public static String FileNotFound = "not_found.html";

   /**
   * Zeitraum fÃ¼r das Cachen HTTP-Antworten<br>
   * unsere Daten kÃ¶nnen einen Tag gecacht werden:
   */
   public static String CacheControlSeconds = (3600 * 24) + "";

   /**
   * Der Mime-Typ fÃ¼r unbekannte Dateien
   */
   public static final String DefaultMimeType = "application/octet-stream";

   /**
   * Der Mime-Typ fÃ¼r einfache Text-Dateien, wird auch fÃ¼r das Senden von
   * Fehlermeldungen verwendet.
   */
   public static final String PlainMimeType = "text/plain";

   /**
   * Hashtable mit den Mime-Typen: Abbildung: Endung -> Mime-Typ
   */
   public static Hashtable<String, String> mimeTypes;

   /**
   * Datumsformat zur Ausgabe des Datums im HTTP-Header
   */
   public static SimpleDateFormat df;

   static
   {
      mimeTypes = new Hashtable<String, String>();
      mimeTypes.put("html", "text/html");
      mimeTypes.put("htm", "text/html");
      mimeTypes.put("txt", "text/plain");
      mimeTypes.put("java", "text/plain");
      mimeTypes.put("gif", "image/gif");
      mimeTypes.put("jpg", "image/jpeg");
      mimeTypes.put("jpeg", "image/jpeg");
      mimeTypes.put("png", "image/png");
      mimeTypes.put("pdf", "application/pdf");

      df = new SimpleDateFormat("E, d MMM yyyy HH:mm:ss 'GMT'", Locale.GERMANY);
      df.setTimeZone(TimeZone.getTimeZone("GMT"));
   }

   /* ************************************************************************* */
   protected HTTPRequest req;

   /**
   * Erzeugt ein neues HTTPResponse Objekt, welches den Socket des HTTPRequest
   * zum Schreiben der Antwort verwenden soll.
   *
   * @param req
   *            der HTTPRequest, in dessen Socket die Antwort geschrieben wird
   */
   public HTTPResponse(HTTPRequest req)
   {
      this.req = req;
   }

   /**
   * Schickt die Datei fileName an den Client. Im Fehlerfall wird ein Fehler
   * an den Client geschickt
   *
   * @param fileName
   *            der Name der Datei, die verschickt werden soll.
   */
   public void serviceFileRequest(String fileName)
   {
      serviceFileRequest(fileName, HTTP_OK);
   }

   /**
   * Schickt die Datei fileName an den Client. Im Fehlerfall wird ein Fehler
   * an den Client geschickt
   *
   * @param fileName
   *            der Name der Datei, die verschickt werden soll.
   * @param postData
   *            HTTP-POST Daten
   */
   public void serviceFileRequest(String fileName, String postData)
   {
      serviceFileRequest(fileName, HTTP_OK);
   }

   /**
   * Schickt die Datei fileName mit dem responseCode an den Client.
   *
   * @param fileName
   *            der Name der Datei, die verschickt werden soll.
   * @param responseCode
   *            die Konstante des zu verwendenden Response-Codes
   */
   protected void serviceFileRequest(String fileName, int responseCode)
   {
      BufferedInputStream in = null;

      try
      {
         if(fileName.equals("/"))
         {
            fileName += DefaultFile;
         }

         String mimeType = getMimeTypeOfFileName(fileName, DefaultMimeType);

         fileName = HTMLDir + fileName.replace('/', File.separatorChar);

         in = new BufferedInputStream(new FileInputStream(fileName));
         sendAnswer(in, mimeType, responseCode);
      }
      catch(FileNotFoundException e)
      {
         if(responseCode == HTTP_NotFound)
         {
            // Achtung: Rekursion:
            sendAnswer("Error 404, file not found.", PlainMimeType, HTTP_NotFound);
            throw new RuntimeException("Sending HTTP 404");
         }
         else
         {
            serviceFileRequest(FileNotFound, HTTP_NotFound);
         }
      }
      catch(Exception e)
      {
         throw new RuntimeException("HTTPResponse.serviceFileRequest: Failed to serve request.", e);
      }
      finally
      {
         try
         {
            if(in != null)
            {
               in.close();
            }

            in = null;
         }
         catch(IOException ioe)
         {
            throw new RuntimeException("HTTPResponse.serviceFileRequest: Failed to close inputstream.", ioe);
         }
      }
   }

   /**
   * Schickt einen Antwort-String mit dem Mime-Typen und dem Reponse-Code an
   * den Client.
   *
   * @param answer
   *            die Antwort
   * @param mimeType
   *            der Mime-Type der Antwort
   * @param responseCode
   *            die Konstante des zu verwendenden Response-Codes
   */
   public void sendAnswer(String answer, String mimeType, int responseCode)
   {
      sendAnswer(new ByteArrayInputStream(answer.getBytes()), mimeType, responseCode);
   }

   /**
   * Schickt eine Antwort in Form eines InputStreams mit dem Mime-Typen und
   * dem Reponse-Code an den Client.
   *
   * @param in
   *            der InputStream
   * @param httpCode
   *            der HTTP-Response-Code
   * @param mimeType
   *            der Mime-Type der Antwort
   */
   public void sendAnswer(InputStream in, int httpCode, String mimeType)
   {
      int responseCode = HTTPResponse2InternalCode(httpCode);

      sendAnswer(in, mimeType, responseCode);
   }

   /**
   * Schickt eine Antwort in Form eines InputStreams mit dem Mime-Typen und
   * dem Reponse-Code an den Client.
   *
   * @param in
   *            der InputStream
   * @param mimeType
   *            der Mime-Type der Antwort
   * @param responseCode
   *            die Konstante des zu verwendenden Response-Codes
   */
   public void sendAnswer(InputStream in, String mimeType, int responseCode)
   {
      BufferedOutputStream out = null;

      try
      {
         out = new BufferedOutputStream(req.getSocket().getOutputStream());

         // Erzeuge den Reponse-Header:
         StringBuffer header        = new StringBuffer();
         int          contentLength = in.available();

         createHeader(header, responseCode, mimeType, contentLength);
         out.write(header.toString().getBytes());

         if(!"HEAD".equals(req.getHTTPMethod()))
         {
            byte[] dataBody = new byte[1024];
            int    read;

            while((read = in.read(dataBody)) != -1)
            {
               out.write(dataBody, 0, read);
            }
         }

         // und Ausgabe in die Zugriffs-Log-Datei
         /*
          * req.log.info(req.socket.getInetAddress().getCanonicalHostName() + " : " +
          * req.getHTTPMethod() + " " + req.requestedFile + " " +
          * req.httpVersion + " " + ResponseCodes[responseCode] + " " +
          * contentLength);
          */
      }
      catch(Exception e)
      {
         throw new RuntimeException("HTTPResponse.sendAnswer: Failed to send answer: " + e);
      }
      finally
      {
         try
         {
            if(out != null)
            {
               out.close();
            }

            out = null;
         }
         catch(IOException ioe)
         {
            throw new RuntimeException("HTTPResponse.sendAnswer: Failed to close outputstream.", ioe);
         }
      }
   }

   /**
   * Schreibt einen HTTP-Header, bestehend aus
   * <ul>
   * <li>HTTP/1.0 ResponseCode</li>
   * <li>Server-Namen</li>
   * <li>Datum</li>
   * <li>Content-Type (=Mime-Typ)</li>
   * <li>Content-Length</li>
   * <li>Cache-Control</li>
   * </ul>
   * in den header-StringBuffer
   *
   * @param header
   *            in diesen StringBuffer wird der Header geschrieben
   * @param response
   *            die Konstante des zu verwendenden Response-Codes
   * @param mimeType
   *            der Mime-Typ
   * @param contentLength
   *            die Content-Length
   */
   protected void createHeader(StringBuffer header, int response, String mimeType, int contentLength)
   {
      header.append("HTTP/1.1 " + ResponseCodes[response] + "\r\n");
      header.append("Server: Java HTTP Server\r\n");
      header.append("Date: " + df.format(new Date()) + "\r\n");
      header.append("Content-type: " + mimeType + "\r\n");
      if(contentLength > 0)
      {
         header.append("Content-Length: " + contentLength + "\r\n");
      }

      header.append("Connection: Keep-Alive\r\n");
      // header.append("Expire: " + df.format(new Date()) + "\r\n");
      header.append("Cache-Control: " + CacheControlSeconds + "\r\n");
      header.append("\r\n");
   }

   public static String getMimeTypeOfFileName(String filename)
   {
      return getMimeTypeOfFileName(filename, DefaultMimeType);
   }

   public static String getMimeTypeOfFileName(String filename, String defaultMimeType)
   {
      int idx = filename.lastIndexOf('.');

      if(idx > 0)
      {
         String end = filename.substring(idx + 1);

         return mimeTypes.get(end);
      }

      return defaultMimeType;
   }

   public static int HTTPResponse2InternalCode(int httpCode)
   {
      switch(httpCode)
      {

         case 200:
            return HTTP_OK;

         case 302:
            return HTTP_Redirect;

         case 400:
            return HTTP_BadRequest;

         case 403:
            return HTTP_Forbidden;

         case 404:
            return HTTP_NotFound;

         case 500:
            return HTTP_InternalServerError;

         case 501:
            return HTTP_NotImplemented;
      }

      return -1;
   }
}
