package de.applejuicenet.client.shared;

import java.io.*;
import java.net.*;

import de.applejuicenet.client.shared.exception.*;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/shared/Attic/HtmlLoader.java,v 1.11 2003/08/15 14:46:30 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: HtmlLoader.java,v $
 * Revision 1.11  2003/08/15 14:46:30  maj0r
 * Refactoring.
 *
 * Revision 1.10  2003/08/09 16:47:42  maj0r
 * Diverse Änderungen.
 *
 * Revision 1.9  2003/07/04 10:35:42  maj0r
 * Lesen des Sockets geht nun wesentlich schneller.
 * Share wird daher wesentlich schneller angezeigt.
 *
 * Revision 1.8  2003/06/10 12:31:03  maj0r
 * Historie eingefügt.
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

        String inputLine = "";
        if (inputLine == null) {
          throw new WebSiteNotFoundException(WebSiteNotFoundException.
                                             UNKNOWN_HOST);
        }
        while((inputLine = in.readLine()).compareToIgnoreCase("Content-Type: text/html")!=0){
        }
          inputLine = in.readLine();
          inputLine = in.readLine();
        while((inputLine = in.readLine()) != null){
            urlContent.append(inputLine);
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

    public static String getHtmlXMLContent(String host, int method, String command) throws
        WebSiteNotFoundException {
        StringBuffer urlContent = new StringBuffer();
        try {
          InetAddress addr = InetAddress.getByName(host);
          Socket socket = new Socket(addr, 9851);
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

          BufferedReader in = new BufferedReader(
              new InputStreamReader(
              socket.getInputStream()));

          String inputLine = "";
          inputLine = in.readLine();
          if (method == HtmlLoader.GET) {
            if (inputLine == null) {
              throw new WebSiteNotFoundException(WebSiteNotFoundException.
                                                 UNKNOWN_HOST);
            }
            while (inputLine.indexOf("xml version") == -1) {
              inputLine = in.readLine();
              if (inputLine == null) {
                throw new WebSiteNotFoundException(WebSiteNotFoundException.
                                                   UNKNOWN_HOST);
              }
            }
            urlContent.append(inputLine);
            while((inputLine = in.readLine()) != null){
                urlContent.append(inputLine);
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
        return urlContent.toString();
    }
  }

