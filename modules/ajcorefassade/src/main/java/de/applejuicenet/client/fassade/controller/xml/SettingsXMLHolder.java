/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.fassade.controller.xml;

import de.applejuicenet.client.fassade.controller.CoreConnectionSettingsHolder;
import de.applejuicenet.client.fassade.entity.ShareEntry;
import de.applejuicenet.client.fassade.shared.AJSettings;
import de.applejuicenet.client.fassade.shared.StringConstants;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.HashSet;
import java.util.Set;

/**
 * $Header:
 * /cvsroot/applejuicejava/ajcorefassade/src/de/applejuicenet/client/fassade/controller/xmlholder/SettingsXMLHolder.java,v
 * 1.1 2004/12/03 07:57:12 maj0r Exp $
 *
 * <p>
 * Titel: AppleJuice Client-GUI
 * </p>
 * <p>
 * Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten
 * appleJuice-Core
 * </p>
 * <p>
 * Copyright: General Public License
 * </p>
 *
 * @author Maj0r <aj@tkl-soft.de>
 *
 */
public class SettingsXMLHolder extends WebXMLParser
{
   private AJSettings settings;

   public SettingsXMLHolder(CoreConnectionSettingsHolder coreHolder)
   {
      super(coreHolder, "/xml/settings.xml", "");
   }

   public void update()
   {
      try
      {
         reload("", false);
      }
      catch(Exception e1)
      {
         throw new RuntimeException(e1);
      }

      NodeList nodes = document.getElementsByTagName(StringConstants.NICK);
      String   nick = nodes.item(0).getFirstChild().getNodeValue();

      nodes = document.getElementsByTagName(StringConstants.PORT);
      long port = Long.parseLong(nodes.item(0).getFirstChild().getNodeValue());

      nodes = document.getElementsByTagName(StringConstants.XMLPORT);
      long xmlPort = Long.parseLong(nodes.item(0).getFirstChild().getNodeValue());

      nodes = document.getElementsByTagName(StringConstants.MAXUPLOAD);
      long maxUpload = Long.parseLong(nodes.item(0).getFirstChild().getNodeValue());

      nodes = document.getElementsByTagName(StringConstants.MAXDOWNLOAD);
      long maxDownload = Long.parseLong(nodes.item(0).getFirstChild().getNodeValue());

      nodes = document.getElementsByTagName(StringConstants.MAXCONNECTIONS);
      long maxConnections = Long.parseLong(nodes.item(0).getFirstChild().getNodeValue());

      nodes = document.getElementsByTagName(StringConstants.MAXSOURCESPERFILE);
      long maxSourcesPerFile = Long.parseLong(nodes.item(0).getFirstChild().getNodeValue());

      nodes = document.getElementsByTagName(StringConstants.AUTOCONNECT);
      boolean autoConnect = new Boolean(nodes.item(0).getFirstChild().getNodeValue()).booleanValue();

      nodes = document.getElementsByTagName(StringConstants.SPEEDPERSLOT);
      int speedPerSlot = Integer.parseInt(nodes.item(0).getFirstChild().getNodeValue());

      nodes = document.getElementsByTagName(StringConstants.MAXNEWCONNECTIONSPERTURN);
      long maxNewConnectionsPerTurn = Long.parseLong(nodes.item(0).getFirstChild().getNodeValue());

      nodes = document.getElementsByTagName(StringConstants.INCOMINGDIRECTORY);
      String incomingDir = nodes.item(0).getFirstChild().getNodeValue();

      nodes = document.getElementsByTagName(StringConstants.TEMPORARYDIRECTORY);
      String          tempDir      = nodes.item(0).getFirstChild().getNodeValue();
      Set<ShareEntry> shareEntries = new HashSet<ShareEntry>();

      nodes = document.getElementsByTagName(StringConstants.DIRECTORY);
      Element      e         = null;
      String       dir       = null;
      String       shareMode = null;
      ShareEntryDO entry     = null;
      int          nodesSize = nodes.getLength();

      for(int i = 0; i < nodesSize; i++)
      {
         e         = (Element) nodes.item(i);
         dir       = e.getAttribute(StringConstants.NAME);
         shareMode = e.getAttribute(StringConstants.SHAREMODE);
         entry     = new ShareEntryDO(dir, shareMode);
         shareEntries.add(entry);
      }

      settings = new AJSettings(nick, port, xmlPort, maxUpload, maxDownload, speedPerSlot, incomingDir, tempDir, shareEntries,
                                maxConnections, autoConnect, maxNewConnectionsPerTurn, maxSourcesPerFile);
   }

   public AJSettings getAJSettings()
   {
      update();
      return settings;
   }

   public AJSettings getCurrentAJSettings()
   {
      if(settings == null)
      {
         update();
      }

      return settings;
   }
}
