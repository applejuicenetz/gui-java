/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.fassade.controller.xml;


/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/ajcorefassade/src/de/applejuicenet/client/fassade/controller/xml/GetObjectXMLHolder.java,v 1.6 2009/01/26 13:31:53 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author Maj0r <aj@tkl-soft.de>
 *
 */

import de.applejuicenet.client.fassade.controller.CoreConnectionSettingsHolder;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class GetObjectXMLHolder extends WebXMLParser
{
   public GetObjectXMLHolder(CoreConnectionSettingsHolder coreHolder)
   {
      super(coreHolder, "/xml/getobject.xml", "");
   }

   public void update()
   {
   }

   private Object getShareObject(NodeList nodes)
   {
      Element e             = (Element) nodes.item(0);
      int     id_key        = Integer.parseInt(e.getAttribute("id"));
      String  filename      = e.getAttribute("filename");
      String  shortfilename = e.getAttribute("shortfilename");
      long    size          = Long.parseLong(e.getAttribute("size"));
      String  checksum      = e.getAttribute("checksum");
      int     prioritaet    = Integer.parseInt(e.getAttribute("priority"));
      long    lastAsked     = Long.parseLong(e.getAttribute("lastasked"));
      long    askCount      = Long.parseLong(e.getAttribute("askcount"));
      long    searchCount   = Long.parseLong(e.getAttribute("searchcount"));

      ShareDO shareDO = new ShareDO(id_key, filename, shortfilename, size, checksum, prioritaet, lastAsked, askCount, searchCount);

      return shareDO;
   }

   private Object getDownloadObject(NodeList nodes)
   {
      DownloadDO downloadDO      = null;
      Element    e               = (Element) nodes.item(0);
      int        id              = Integer.parseInt(e.getAttribute("id"));
      int        shareid         = Integer.parseInt(e.getAttribute("shareid"));
      String     hash            = e.getAttribute("hash");
      int        fileSize        = Integer.parseInt(e.getAttribute("size"));
      int        sizeReady       = Integer.parseInt(e.getAttribute("ready"));
      String     temp            = e.getAttribute("status");
      int        status          = Integer.parseInt(temp);
      String     filename        = e.getAttribute("filename");
      String     targetDirectory = e.getAttribute("targetdirectory");

      temp = e.getAttribute("powerdownload");
      int powerDownload = Integer.parseInt(temp);

      temp = e.getAttribute("temporaryfilenumber");
      int temporaryFileNumber = Integer.parseInt(temp);

      downloadDO = new DownloadDO(id, shareid, hash, fileSize, sizeReady, status, filename, targetDirectory, powerDownload,
                                  temporaryFileNumber);
      return downloadDO;
   }

   private Object getInformationObject(NodeList nodes)
   {
      Element       e  = (Element) nodes.item(0);
      int           id = Integer.parseInt(e.getAttribute("id"));

      long          credits            = Long.parseLong(e.getAttribute("credits"));
      long          uploadSpeed        = Long.parseLong(e.getAttribute("uploadspeed"));
      long          downloadSpeed      = Long.parseLong(e.getAttribute("downloadspeed"));
      long          openConnections    = Long.parseLong(e.getAttribute("openconnections"));
      long          sessionUpload      = Long.parseLong(e.getAttribute("sessionupload"));
      long          sessionDownload    = Long.parseLong(e.getAttribute("sessiondownload"));
      long          maxuploadpositions = Long.parseLong(e.getAttribute("maxuploadpositions"));
      InformationDO information        = new InformationDO(id, sessionUpload, sessionDownload, credits, uploadSpeed, downloadSpeed,
                                                           openConnections, maxuploadpositions);

      return information;
   }

   private Object getUploadObject(NodeList nodes)
   {
      int       os;
      VersionDO version = null;

      Element   e          = (Element) nodes.item(0);
      int       id         = Integer.parseInt(e.getAttribute("id"));
      int       shareId    = Integer.parseInt(e.getAttribute("shareid"));
      String    versionsNr = e.getAttribute("version");

      if(versionsNr.compareToIgnoreCase("0.0.0.0") == 0)
      {
         version = null;
      }
      else
      {
         os      = Integer.parseInt(e.getAttribute("operatingsystem"));
         version = new VersionDO(versionsNr, os);
      }

      int      prioritaet      = Integer.parseInt(e.getAttribute("priority"));
      String   nick            = e.getAttribute("nick");
      String   status          = e.getAttribute("status");
      int      uploadFrom      = Integer.parseInt(e.getAttribute("uploadfrom"));
      int      uploadTo        = Integer.parseInt(e.getAttribute("uploadto"));
      long     actualUploadPos = Long.parseLong(e.getAttribute("actualuploadposition"));
      int      speed           = Integer.parseInt(e.getAttribute("speed"));
      int      directstate     = Integer.parseInt(e.getAttribute("directstate"));
      long     lastConnection  = Long.parseLong(e.getAttribute("lastconnection"));
      double   loaded          = Double.parseDouble(e.getAttribute("loaded"));
      UploadDO upload          = new UploadDO(id, shareId, version, status, nick, uploadFrom, uploadTo, actualUploadPos, speed,
                                              prioritaet, directstate, lastConnection, loaded);

      return upload;
   }

   private Object getDownloadSourceObject(NodeList nodes)
   {
      int       downloadFrom;
      int       downloadTo;
      int       actualDownloadPosition;
      int       speed;
      int       downloadId;
      VersionDO version       = null;
      String    versionNr     = null;
      String    nickname      = null;
      int       queuePosition;
      int       os;
      Element   e             = (Element) nodes.item(0);
      int       id            = Integer.parseInt(e.getAttribute("id"));
      String    temp          = e.getAttribute("status");
      int       status        = Integer.parseInt(temp);

      temp = e.getAttribute("directstate");
      int directstate = Integer.parseInt(temp);

      if(status == DownloadSourceDO.UEBERTRAGUNG)
      {
         temp                   = e.getAttribute("downloadfrom");
         downloadFrom           = Integer.parseInt(temp);
         temp                   = e.getAttribute("downloadto");
         downloadTo             = Integer.parseInt(temp);
         temp                   = e.getAttribute("actualdownloadposition");
         actualDownloadPosition = Integer.parseInt(temp);
         temp                   = e.getAttribute("speed");
         speed                  = Integer.parseInt(temp);
      }
      else
      {
         downloadFrom           = -1;
         downloadTo             = -1;
         actualDownloadPosition = -1;
         speed                  = 0;
      }

      versionNr = e.getAttribute("version");
      if(versionNr.compareToIgnoreCase("0.0.0.0") == 0)
      {
         version = null;
      }
      else
      {
         temp    = e.getAttribute("operatingsystem");
         os      = Integer.parseInt(temp);
         version = new VersionDO(versionNr, os);
      }

      temp          = e.getAttribute("queueposition");
      queuePosition = Integer.parseInt(temp);
      temp          = e.getAttribute("powerdownload");
      int    powerDownload = Integer.parseInt(temp);
      String filename = e.getAttribute("filename");

      nickname   = e.getAttribute("nickname");
      temp       = e.getAttribute("downloadid");
      downloadId = Integer.parseInt(temp);
      temp       = e.getAttribute("source");
      int              herkunft = Integer.parseInt(temp);
      DownloadSourceDO downloadSourceDO = new DownloadSourceDO(id, status, directstate, downloadFrom, downloadTo,
                                                               actualDownloadPosition, speed, version, queuePosition,
                                                               powerDownload, filename, nickname, downloadId, herkunft);

      return downloadSourceDO;
   }

   private Object getServerObject(NodeList nodes)
   {
      Element  e        = (Element) nodes.item(0);
      String   id_key   = e.getAttribute("id");
      int      id       = Integer.parseInt(id_key);
      String   name     = e.getAttribute("name");
      String   host     = e.getAttribute("host");
      long     lastseen = Long.parseLong(e.getAttribute("lastseen"));
      String   port     = e.getAttribute("port");
      int      versuche = Integer.parseInt(e.getAttribute("connectiontry"));
      ServerDO server   = new ServerDO(id, name, host, port, lastseen, versuche);

      return server;
   }

   public Object getObjectByID(int id)
   {
      try
      {
         reload("id=" + id, false);
         NodeList nodes = document.getElementsByTagName("share");

         if(nodes.getLength() == 1)
         {
            return getShareObject(nodes);
         }

         nodes = document.getElementsByTagName("download");
         if(nodes.getLength() == 1)
         {
            return getDownloadObject(nodes);
         }

         nodes = document.getElementsByTagName("upload");
         if(nodes.getLength() == 1)
         {
            return getUploadObject(nodes);
         }

         nodes = document.getElementsByTagName("user");
         if(nodes.getLength() == 1)
         {
            return getDownloadSourceObject(nodes);
         }

         nodes = document.getElementsByTagName("server");
         if(nodes.getLength() == 1)
         {
            return getServerObject(nodes);
         }

         nodes = document.getElementsByTagName("information");
         if(nodes.getLength() == 1)
         {
            getInformationObject(nodes);
         }

         return null;
      }
      catch(Exception e)
      {
         return null;
      }
   }
}
