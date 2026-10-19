/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.facade.controller.xml;


/**
 * Copyright: General Public License
 */

import de.applejuicenet.client.facade.shared.WebsiteContentLoader;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

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
         xr = SaxReaders.create();
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

   public String[] getNetworkKnownServers(String ServerListURL)
   {
      String xmlData = null;
      try
      {
         xmlData = WebsiteContentLoader.getWebsiteContent(ServerListURL);
         if(xmlData.length() == 0)
         {
            return null;
         }

         links.clear();
         xr.parse(new InputSource(new StringReader(xmlData)));
         return links.toArray(new String[links.size()]);
      }
      catch(Exception e)
      {
         throw new RuntimeException(e);
      }
   }
}
