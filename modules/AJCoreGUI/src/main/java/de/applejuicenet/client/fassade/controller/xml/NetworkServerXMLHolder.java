/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.fassade.controller.xml;


/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/ajcorefassade/src/de/applejuicenet/client/fassade/controller/xml/NetworkServerXMLHolder.java,v 1.3 2009/01/05 12:07:01 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 */
import java.io.StringReader;

import java.util.ArrayList;
import java.util.List;

import de.applejuicenet.client.fassade.shared.ProxySettings;
import de.applejuicenet.client.gui.controller.OptionsManagerImpl;
import de.applejuicenet.client.gui.controller.ProxyManagerImpl;
import org.apache.xerces.parsers.SAXParser;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import de.applejuicenet.client.fassade.shared.WebsiteContentLoader;

public class NetworkServerXMLHolder extends DefaultHandler
{
   private static NetworkServerXMLHolder instance = null;
   private XMLReader                     xr = null;
   private List<String> links = new ArrayList<String>();

   @SuppressWarnings("unchecked")
   private NetworkServerXMLHolder()
   {
      try
      {
         Class parser = SAXParser.class;

         xr = XMLReaderFactory.createXMLReader(parser.getName());
         xr.setContentHandler(this);
      }
      catch(Exception ex)
      {
         throw new RuntimeException(ex);
      }
   }

   public static NetworkServerXMLHolder getInstance()
   {
      if(instance == null)
      {
         instance = new NetworkServerXMLHolder();
      }

      return instance;
   }

   public void startElement(String namespaceURI, String localName, String qName, Attributes attr)
                     throws SAXException
   {
      if(localName.equals("server"))
      {
         checkServerAttributes(attr);
      }
   }

   private void checkServerAttributes(Attributes attr)
   {
      for(int i = 0; i < attr.getLength(); i++)
      {
         if(attr.getLocalName(i).equals("link"))
         {
            links.add(attr.getValue(i));
         }
      }
   }

   public String[] getNetworkKnownServers()
   {
      String xmlData = null;

      ProxySettings proxy = ProxyManagerImpl.getInstance().getProxySettings();
      String ServerListURL = OptionsManagerImpl.getInstance().getServerListURL();

      try
      {
         xmlData = WebsiteContentLoader.getWebsiteContent(ServerListURL, proxy);
         if(xmlData.length() == 0)
         {
            return null;
         }

         links.clear();
         xr.parse(new InputSource(new StringReader(xmlData)));
         return (String[]) links.toArray(new String[links.size()]);
      }
      catch(Exception e)
      {
         throw new RuntimeException(e);
      }
   }
}
