package de.applejuicenet.client.shared;

import java.io.*;
import java.net.*;

import de.applejuicenet.client.shared.exception.*;
import de.applejuicenet.client.gui.controller.PropertiesManager;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/shared/Attic/HtmlLoader.java,v 1.19 2004/01/28 07:59:47 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI f�r den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: HtmlLoader.java,v $
 * Revision 1.19  2004/01/28 07:59:47  maj0r
 * Holen von xmls beschleunigt.
 *
 * Revision 1.18  2004/01/01 14:26:53  maj0r
 * Es koennen nun auch Objekte nach Id vom Core abgefragt werden.
 *
 * Revision 1.17  2003/12/29 16:04:17  maj0r
 * Header korrigiert.
 *
 * Revision 1.16  2003/10/14 19:21:23  maj0r
 * Korrekturen zur Xml-Port-Verwendung.
 *
 * Revision 1.15  2003/10/14 15:43:52  maj0r
 * An pflegbaren Xml-Port angepasst.
 *
 * Revision 1.14  2003/09/07 09:29:55  maj0r
 * Position des Hauptfensters und Breite der Tabellenspalten werden gespeichert.
 *
 * Revision 1.13  2003/09/06 16:25:39  maj0r
 * Newsanfrage an neue Domain angepasst.
 * HtmlLoader korrigiert.
 *
 * Revision 1.12  2003/08/28 06:55:59  maj0r
 * Erweiter, so dass nun auch GET/POST ohne Returnwert ausgefuehrt werden koennen.
 *
 * Revision 1.11  2003/08/15 14:46:30  maj0r
 * Refactoring.
 *
 * Revision 1.10  2003/08/09 16:47:42  maj0r
 * Diverse �nderungen.
 *
 * Revision 1.9  2003/07/04 10:35:42  maj0r
 * Lesen des Sockets geht nun wesentlich schneller.
 * Share wird daher wesentlich schneller angezeigt.
 *
 * Revision 1.8  2003/06/10 12:31:03  maj0r
 * Historie eingef�gt.
 *
 *
 */

public abstract class HtmlLoader {
  public static final int POST = 0;
  public static final int GET = 1;

  public static String getHtmlContent(String host, int port, int method, String command) throws
      WebSiteNotFoundException {
      StringBuffer urlContent = new StringBuffer();
      try {
        InetAddress addr = InetAddress.getByName(host);
        Socket socket = new Socket(addr, port);
        PrintWriter out = new PrintWriter(
            new BufferedWriter(
            new OutputStreamWriter(
            socket.getOutputStream())));

        String methode = "";
        if (method == HtmlLoader.GET) {
          methode = "GET ";
        }
        else if (method == HtmlLoader.POST) {
          methode = "POST ";
        }
        else {
          return "";
        }
        out.println(methode + command + " HTTP/1.1");
        out.println("host: " + host);
        out.println();
        out.flush();

        BufferedReader in = new BufferedReader(
            new InputStreamReader(
            socket.getInputStream()));

        String inputLine = in.readLine();
        if (inputLine == null) {
          throw new WebSiteNotFoundException(WebSiteNotFoundException.
                                             UNKNOWN_HOST);
        }
        while(inputLine.indexOf("Content-Type: text")!=0){
            inputLine = in.readLine();
        }
        int gelesen;
        while ((gelesen = in.read()) != -1){
            urlContent.append((char)gelesen);
        }
      }
      catch (SocketException sex) {
        throw new WebSiteNotFoundException(WebSiteNotFoundException.
                                           AUTHORIZATION_REQUIRED);
      }
      catch (IOException sex) {
        throw new WebSiteNotFoundException(WebSiteNotFoundException.
                                           AUTHORIZATION_REQUIRED);
      }
      return urlContent.toString();
    }

    public static String getHtmlXMLContent(String host, int method, String command, boolean withResult) throws
        WebSiteNotFoundException {
        int ajPort = PropertiesManager.getOptionsManager().getRemoteSettings().getXmlPort();
        StringBuffer urlContent = new StringBuffer();
        try {
          InetAddress addr = InetAddress.getByName(host);
          Socket socket = new Socket(addr, ajPort);
          PrintWriter out = new PrintWriter(
              new BufferedWriter(
              new OutputStreamWriter(
              socket.getOutputStream())));

          String methode = "";
          if (method == HtmlLoader.GET) {
            methode = "GET ";
          }
          else if (method == HtmlLoader.POST) {
            methode = "POST ";
          }
          else {
            return "";
          }
          command = command.replaceAll(" ", "%20");
          out.println(methode + command + " HTTP/1.1");
          out.println("host: " + host);
          out.println();
          out.flush();

          if (!withResult){
              return "ok";
          }
          BufferedReader in = new BufferedReader(
              new InputStreamReader(
              socket.getInputStream()));
          String inputLine = in.readLine();
          if (method == HtmlLoader.GET) {
            if (inputLine == null) {
              throw new WebSiteNotFoundException(WebSiteNotFoundException.
                                                 UNKNOWN_HOST);
            }
            while (inputLine.indexOf("Content-Length:") == -1) {
              inputLine = in.readLine();
              if (inputLine == null) {
                throw new WebSiteNotFoundException(WebSiteNotFoundException.
                                                   UNKNOWN_HOST);
              }
              if (inputLine.indexOf("invalid id") != -1) {
                  return "";
              }
            }
            long laenge = Long.parseLong(inputLine.substring(inputLine.indexOf(" ")+1));
            in.skip(1);
            char[] toRead = null;
            int gelesen = 0;
            while (laenge>0){
                if (laenge>Integer.MAX_VALUE){
                    toRead = new char[Integer.MAX_VALUE];
                }
                else{
                    toRead = new char[(int)laenge];
                }
                gelesen = in.read(toRead);
                if (gelesen < toRead.length){
                    urlContent.append(toRead, 0, gelesen);
                    laenge -= gelesen;
                }
                else{
                    urlContent.append(toRead);
                    laenge -= toRead.length;
                }
            }
          }
          else {
            if (inputLine.compareToIgnoreCase("HTTP/1.1 200 OK") == 0) {
              urlContent = new StringBuffer(inputLine);
            }
            else {
              throw new WebSiteNotFoundException(WebSiteNotFoundException.
                                                 INPUT_ERROR);
            }
          }
        }
        catch (SocketException sex) {
          throw new WebSiteNotFoundException(WebSiteNotFoundException.
                                             AUTHORIZATION_REQUIRED);
        }
        catch (IOException sex) {
          throw new WebSiteNotFoundException(WebSiteNotFoundException.
                                             AUTHORIZATION_REQUIRED);
        }
        String test = urlContent.toString();
        return test;
    }

    public static String getHtmlXMLContent(String host, int method, String command) throws
        WebSiteNotFoundException {
        return getHtmlXMLContent(host, method, command, true);
    }
  }

